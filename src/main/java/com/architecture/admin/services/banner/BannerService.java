package com.architecture.admin.services.banner;

import com.architecture.admin.libraries.S3Library;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.banner.BannerDao;
import com.architecture.admin.models.daosub.banner.BannerDaoSub;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.banner.BannerDto;
import com.architecture.admin.models.dto.banner.BannerImageDto;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.log.AdminActionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RequiredArgsConstructor
@Service
@Transactional
public class BannerService extends BaseService {

    private final BannerDao bannerDao;

    private final BannerDaoSub bannerDaoSub;

    private final S3Library s3Library;

    private final AdminActionLogService adminActionLogService;


    /*
        banner_type
        1 - 메인페이지
        2 - 메인 바디
        3 - 뷰어
        4 - 커뮤니티리스트
        5 - 커뮤니티 내용
        6 - 충전소
        7 - 마이페이지 비로그인
        8 - 마이페이지 로그인

        img_type : 1.메인/카테고리(720*364), 2.가로A버전(720*260), 3.가로B버전(720*160)
        img_type 1 : banner_type - 1, 6
        img_type 2 : banner_type - 2, 3, 5, 7, 8
        img_type 3 : banner_type - 4
     */


    // 1. 720*364 - bannerType : 1, 6
    public static final Integer IMAGE_TYPE_1 = 1;

    // 2. 720*260 - bannerType : 2, 3, 5, 7, 8
    public static final Integer IMAGE_TYPE_2 = 2;

    // 3. 720*160 - bannerType : 4
    public static final Integer IMAGE_TYPE_3 = 3;

    /**
     * 배너 목록
     * @param searchDto
     * @return
     */
    public List<BannerDto> getList(SearchDto searchDto) {
        // 컨텐츠 목록
        List<BannerDto> list = bannerDaoSub.getList(searchDto);

        // 이미지 fullUrl
        for (BannerDto bannerDto : list) {
            if (bannerDto.getStrImage364() != null && bannerDto.getStrImage364() != "") {
                bannerDto.setStrImage364(s3Library.getUploadedFullUrl(bannerDto.getStrImage364()));
            }
            if (bannerDto.getStrImage260() != null && bannerDto.getStrImage260() != "") {
                bannerDto.setStrImage260(s3Library.getUploadedFullUrl(bannerDto.getStrImage260()));
            }
            if (bannerDto.getStrImage160() != null && bannerDto.getStrImage160() != "") {
                bannerDto.setStrImage160(s3Library.getUploadedFullUrl(bannerDto.getStrImage160()));
            }
        }

        // 문자변환
        if (!list.isEmpty()) {
            convertTextList(list);
        }
        System.out.println("list = " + list);
        return list;
    }

    /**
     * 배너 등록
     * @param bannerDto
     * @return
     */
    @Transactional
    public Integer register(BannerDto bannerDto) {
        // validate
        validate(bannerDto);

        // 등록일
        bannerDto.setRegdate(dateLibrary.getDatetime());
        // 배너 시작일
        bannerDto.setStartDate(dateLibrary.localTimeToUtc(bannerDto.getStartDate()));
        // 배너 종료일
        bannerDto.setEndDate(dateLibrary.localTimeToUtc(bannerDto.getEndDate()));

        // 배너 등록
        bannerDao.register(bannerDto);

        // 배너 노출 영역
        if (bannerDto.getType() != null && bannerDto.getType().size() > 0) {
            bannerDao.registerTypeMapping(bannerMappingMap(bannerDto, bannerDto.getType(), null));
        }

        // 배너 노출 영역 - 웹툰 장르 (genre : 0 랭킹)
        if (bannerDto.getWebtoonGenre() != null && bannerDto.getWebtoonGenre().size() > 0) {
            bannerDao.registerGenreMapping(bannerMappingMap(bannerDto, bannerDto.getWebtoonGenre(), 1));
        }

        // 배너 노출 영역 - 만화 장르 (genre : 0 랭킹)
        if (bannerDto.getComicGenre() != null && bannerDto.getComicGenre().size() > 0) {
            bannerDao.registerGenreMapping(bannerMappingMap(bannerDto, bannerDto.getComicGenre(), 2));
        }

        // 배너 노출 영역 - 소설 장르 (genre : 0 랭킹)
        if (bannerDto.getNovelGenre() != null && bannerDto.getNovelGenre().size() > 0) {
            bannerDao.registerGenreMapping(bannerMappingMap(bannerDto, bannerDto.getNovelGenre(), 3));
        }

        // s3에 저장될 path
        String s3Path = "kr/banner/" + bannerDto.getInsertedId();

        /** image 720*160 - 4 - 커뮤니티리스트 */
        // webp 이미지 삭제
        bannerDto.getImageFile_160().removeIf(multipartFiles -> multipartFiles.getContentType().contains("webp"));
        bannerDto.getImageFile_160().removeIf(multipartFiles -> multipartFiles.isEmpty());
        List<MultipartFile> imageFile160 = bannerDto.getImageFile_160();

        // 파일 있는지 체크
        Boolean chkImageFile160  = chkIsEmptyImage(imageFile160);
        if (Boolean.TRUE.equals(chkImageFile160)) {
            // s3 upload(원본)
            List<HashMap<String, Object>> uploadResponse = s3Library.uploadFileNew(imageFile160, s3Path);

            // db insert
            registerImage(uploadResponse, bannerDto.getInsertedId(), s3Path, IMAGE_TYPE_3);
        }

        /** image 720*260 - 2 - 메인 바디, 3 - 뷰어, 5 - 커뮤니티 내용, 7 - 마이페이지 비로그인, 8 - 마이페이지 로그인 */
        // webp 이미지 삭제
        bannerDto.getImageFile_260().removeIf(multipartFiles -> multipartFiles.getContentType().contains("webp"));
        bannerDto.getImageFile_260().removeIf(multipartFiles -> multipartFiles.isEmpty());
        List<MultipartFile> imageFile260 = bannerDto.getImageFile_260();

        // 파일 있는지 체크
        Boolean chkImageFile260  = chkIsEmptyImage(imageFile260);
        if (Boolean.TRUE.equals(chkImageFile260)) {
            // s3 upload(원본)
            List<HashMap<String, Object>> uploadResponse = s3Library.uploadFileNew(imageFile260, s3Path);

            // db insert
            registerImage(uploadResponse, bannerDto.getInsertedId(), s3Path, IMAGE_TYPE_2);
        }

        /** image 720*364 - 1 - 메인, 6 - 충전소 */
        // webp 이미지 삭제
        bannerDto.getImageFile_364().removeIf(multipartFiles -> multipartFiles.getContentType().contains("webp"));
        bannerDto.getImageFile_364().removeIf(multipartFiles -> multipartFiles.isEmpty());
        List<MultipartFile> imageFile364 = bannerDto.getImageFile_364();

        // 파일 있는지 체크
        Boolean chkImageFile364  = chkIsEmptyImage(imageFile364);
        if (Boolean.TRUE.equals(chkImageFile364)) {
            // s3 upload(원본)
            List<HashMap<String, Object>> uploadResponse = s3Library.uploadFileNew(imageFile364, s3Path);

            // db insert
            registerImage(uploadResponse, bannerDto.getInsertedId(), s3Path, IMAGE_TYPE_1);
        }

        return bannerDto.getInsertedId();
    }

    /**
     * 배너 상세
     * @param idx
     * @return
     */
    public BannerDto getView(Integer idx) {
        if (idx == null || idx < 1) {
            throw new CustomException(CustomError.BANNER_IDX_ERROR);
        }

        // 배너 상세
        BannerDto view = bannerDaoSub.getView(idx);

        if (view == null) {
            throw new CustomException(CustomError.BANNER_NO_DATA_ERROR);
        }

        // 이미지 fullUrl
        for (BannerImageDto imageDto : view.getBannerImageList()) {
            if (imageDto.getUrl() != null) {
                imageDto.setUrl(s3Library.getUploadedFullUrl(imageDto.getUrl()));
            }
        }

        return view;
    }

    /**
     * 배너 수정
     * @param bannerDto
     * @return
     */
    public Integer modify(BannerDto bannerDto) {

        // 등록일
        bannerDto.setRegdate(dateLibrary.getDatetime());
        // 배너 시작일
        bannerDto.setStartDate(dateLibrary.localTimeToUtc(bannerDto.getStartDate()));
        // 배너 종료일
        bannerDto.setEndDate(dateLibrary.localTimeToUtc(bannerDto.getEndDate()));
        // banner idx
        bannerDto.setInsertedId(bannerDto.getIdx());
        // 배너 수정
        Integer result = bannerDao.modify(bannerDto);


        // 배너 상세
        BannerDto view = bannerDaoSub.getView(bannerDto.getIdx());
        // 이전 노출 위치 list
        String[] strType = {};
        if (view.getStrType() != null && view.getStrType() != ""){
            strType = view.getStrType().split(",");
        }
        List<Integer> prevType = new ArrayList<>();
        for (String type : strType) {
            prevType.add(Integer.parseInt(type.trim()));
        }

        // 이전 노출 위치 - 장르(웹툰)
        String[] strWebtoonGenre = {};
        if (view.getStrWebtoonGenre() != null && view.getStrWebtoonGenre() != "") {
            strWebtoonGenre = view.getStrWebtoonGenre().split(",");
        }
        List<Integer> prevWebtoonGenre = new ArrayList<>();
        for (String genreIdx : strWebtoonGenre) {
            prevWebtoonGenre.add(Integer.parseInt(genreIdx.trim()));
        }

        // 이전 노출 위치 - 장르(만화)
        String[] strComicGenre = {};
        if (view.getStrComicGenre() != null && view.getStrComicGenre() != "") {
            strComicGenre = view.getStrComicGenre().split(",");
        }
        List<Integer> prevComicGenre = new ArrayList<>();
        for (String genreIdx : strComicGenre) {
            prevComicGenre.add(Integer.parseInt(genreIdx.trim()));
        }

        // 이전 노출 위치 - 장르(소설)
        String[] strNovelGenre = {};
        if (view.getStrNovelGenre() != null && view.getStrNovelGenre() != "") {
            strNovelGenre = view.getStrNovelGenre().split(",");
        }
        List<Integer> prevNovelGenre = new ArrayList<>();
        for (String genreIdx : strNovelGenre) {
            prevNovelGenre.add(Integer.parseInt(genreIdx.trim()));
        }


        // 현재 노출 위치 list
        List<Integer> nowType = new ArrayList<>();
        if (bannerDto.getType() != null) {
            nowType.addAll(bannerDto.getType());
        }
        // 배너 노출 영역 - 추가
        nowType.removeAll(prevType);
        if (!nowType.isEmpty()) {
            bannerDao.registerTypeMapping(bannerMappingMap(bannerDto, nowType, null));
        }
        // 배너 노출 영역 - 삭제
        if (bannerDto.getType() != null) {
            prevType.removeAll(bannerDto.getType());
        }
        if (!prevType.isEmpty()) {
            List<Map<String, Object>> mapList = new ArrayList<>();
            for (Integer type : prevType) {
                Map<String, Object> map = new HashMap<>();
                map.put("bannerIdx", bannerDto.getIdx());
                map.put("type", type);
                map.put("state", 0);
                map.put("regdate", bannerDto.getRegdate());
                mapList.add(map);
            }
            bannerDao.deleteTypeMapping(mapList);
        }

        // 현재 노출 위치 list - 웹툰
        List<Integer> nowWebtoonGenre = new ArrayList<>();
        if (bannerDto.getWebtoonGenre() != null) {
            nowWebtoonGenre.addAll(bannerDto.getWebtoonGenre());
        }
        // 배너 노출 영역 등록 - 웹툰 장르 (genre : 0 랭킹)
        nowWebtoonGenre.removeAll(prevWebtoonGenre);
        if (!nowWebtoonGenre.isEmpty()) {
            bannerDao.registerGenreMapping(bannerMappingMap(bannerDto, nowWebtoonGenre, 1));
        }
        // 배너 노출 영역 삭제 - 웹툰 장르 (genre : 0 랭킹)
        if (bannerDto.getWebtoonGenre() != null) {
            prevWebtoonGenre.removeAll(bannerDto.getWebtoonGenre());
        }
        if (!prevWebtoonGenre.isEmpty()) {
            List<Map<String, Object>> mapList = new ArrayList<>();
            for (Integer genre : prevWebtoonGenre) {
                Map<String, Object> map = new HashMap<>();
                map.put("bannerIdx", bannerDto.getIdx());
                map.put("categoryIdx", 1);
                map.put("genreIdx", genre);
                map.put("state", 0);
                map.put("regdate", bannerDto.getRegdate());
                mapList.add(map);
            }
            bannerDao.deleteGenreMapping(mapList);
        }

        // 현재 노출 위치 list - 만화
        List<Integer> nowComicGenre = new ArrayList<>();
        if (bannerDto.getComicGenre() != null) {
            nowComicGenre.addAll(bannerDto.getComicGenre());
        }
        // 배너 노출 영역 등록 - 만화 장르 (genre : 0 랭킹)
        nowComicGenre.removeAll(prevComicGenre);
        if (!nowComicGenre.isEmpty()) {
            bannerDao.registerGenreMapping(bannerMappingMap(bannerDto, nowComicGenre, 2));
        }
        // 배너 노출 영역 삭제 - 만화 장르 (genre : 0 랭킹)
        if (bannerDto.getComicGenre() != null) {
            prevComicGenre.removeAll(bannerDto.getComicGenre());
        }
        if (!prevComicGenre.isEmpty()) {
            List<Map<String, Object>> mapList = new ArrayList<>();
            for (Integer genre : prevComicGenre) {
                Map<String, Object> map = new HashMap<>();
                map.put("bannerIdx", bannerDto.getIdx());
                map.put("categoryIdx", 2);
                map.put("genreIdx", genre);
                map.put("state", 0);
                map.put("regdate", bannerDto.getRegdate());
                mapList.add(map);
            }
            bannerDao.deleteGenreMapping(mapList);
        }

        // 현재 노출 위치 list - 소설
        List<Integer> nowNovelGenre = new ArrayList<>();
        if (bannerDto.getNovelGenre() != null) {
            nowNovelGenre.addAll(bannerDto.getNovelGenre());
        }
        // 배너 노출 영역 삭제 - 소설 장르 (genre : 0 랭킹)
        nowNovelGenre.removeAll(prevNovelGenre);
        if (!nowNovelGenre.isEmpty()) {
            bannerDao.registerGenreMapping(bannerMappingMap(bannerDto, nowNovelGenre, 3));
        }
        // 배너 노출 영역 삭제 - 소설 장르 (genre : 0 랭킹)
        if (bannerDto.getNovelGenre() != null) {
            prevNovelGenre.removeAll(bannerDto.getNovelGenre());
        }
        if (!prevNovelGenre.isEmpty()) {
            List<Map<String, Object>> mapList = new ArrayList<>();
            for (Integer genre : prevNovelGenre) {
                Map<String, Object> map = new HashMap<>();
                map.put("bannerIdx", bannerDto.getIdx());
                map.put("categoryIdx", 3);
                map.put("genreIdx", genre);
                map.put("state", 0);
                map.put("regdate", bannerDto.getRegdate());
                mapList.add(map);
            }
            bannerDao.deleteGenreMapping(mapList);
        }


        // s3에 저장될 path
        String s3Path = "kr/banner/" + bannerDto.getInsertedId();

        /** image 720*160 - 4 - 커뮤니티리스트 */
        // webp 이미지 삭제
        bannerDto.getImageFile_160().removeIf(multipartFiles -> multipartFiles.getContentType().contains("webp"));
        bannerDto.getImageFile_160().removeIf(multipartFiles -> multipartFiles.isEmpty());
        List<MultipartFile> imageFile160 = bannerDto.getImageFile_160();

        // 파일 있는지 체크
        Boolean chkImageFile160  = chkIsEmptyImage(imageFile160);
        // 파일 있는지 체크
        if (Boolean.TRUE.equals(chkImageFile160)) {
            // s3 upload(원본)
            List<HashMap<String, Object>> uploadResponse = s3Library.uploadFileNew(imageFile160, s3Path);

            // db delete
            Map<String, Object> bannerMap = new HashMap<>();
            bannerMap.put("bannerIdx", bannerDto.getIdx());
            bannerMap.put("imgType", IMAGE_TYPE_3);
            bannerDao.deleteImage(bannerMap);

            // db insert
            registerImage(uploadResponse, bannerDto.getIdx(), s3Path, IMAGE_TYPE_3);
        }


        /** image 720*260 - 2 - 메인 바디, 3 - 뷰어, 5 - 커뮤니티 내용, 7 - 마이페이지 비로그인, 8 - 마이페이지 로그인 */
        // webp 이미지 삭제
        bannerDto.getImageFile_260().removeIf(multipartFiles -> multipartFiles.getContentType().contains("webp"));
        bannerDto.getImageFile_260().removeIf(multipartFiles -> multipartFiles.isEmpty());
        List<MultipartFile> imageFile260 = bannerDto.getImageFile_260();

        // 파일 있는지 체크
        Boolean chkImageFile260  = chkIsEmptyImage(imageFile260);
        if (Boolean.TRUE.equals(chkImageFile260)) {
            // s3 upload(원본)
            List<HashMap<String, Object>> uploadResponse = s3Library.uploadFileNew(imageFile260, s3Path);

            // db delete
            Map<String, Object> bannerMap = new HashMap<>();
            bannerMap.put("bannerIdx", bannerDto.getIdx());
            bannerMap.put("imgType", IMAGE_TYPE_2);
            bannerDao.deleteImage(bannerMap);

            // db insert
            registerImage(uploadResponse, bannerDto.getIdx(), s3Path, IMAGE_TYPE_2);
        }


        /** image 720*364 - 1 - 메인, 6 - 충전소 */
        // webp 이미지 삭제
        bannerDto.getImageFile_364().removeIf(multipartFiles -> multipartFiles.getContentType().contains("webp"));
        bannerDto.getImageFile_364().removeIf(multipartFiles -> multipartFiles.isEmpty());
        List<MultipartFile> imageFile364 = bannerDto.getImageFile_364();

        // 파일 있는지 체크
        Boolean chkImageFile364  = chkIsEmptyImage(imageFile364);
        if (Boolean.TRUE.equals(chkImageFile364)) {
            // s3 upload(원본)
            List<HashMap<String, Object>> uploadResponse = s3Library.uploadFileNew(imageFile364, s3Path);

            // db delete
            Map<String, Object> bannerMap = new HashMap<>();
            bannerMap.put("bannerIdx", bannerDto.getIdx());
            bannerMap.put("imgType", IMAGE_TYPE_1);
            bannerDao.deleteImage(bannerMap);

            // db insert
            registerImage(uploadResponse, bannerDto.getIdx(), s3Path, IMAGE_TYPE_1);
        }

        // 관리자 action log
        adminActionLogService.regist(bannerDto.getIdx(), Thread.currentThread().getStackTrace());

        return result;
    }

    /**
     * 배너 이미지 삭제
     * @param idx
     * @return
     */
    public Integer deleteBannerImage(Integer idx) {
        // idx validation
        if (idx == null || idx < 1) {
            throw new CustomException(CustomError.BANNER_IDX_ERROR);
        }

        // 관리자 action log
        adminActionLogService.regist(idx, Thread.currentThread().getStackTrace());

        Map<String, Object> bannerMap = new HashMap<>();
        bannerMap.put("idx", idx);
        return bannerDao.deleteImage(bannerMap);
    }



    /**
     * 배너 마지막 순번
     * @return
     */
    public Integer getSortNumber() {
        return bannerDaoSub.getSortNumber();
    }

    /**
     * 배너 등록 validate
     * @param bannerDto
     * @return
     */
    private Boolean validate(BannerDto bannerDto) {
        // 배너 제목
        if (bannerDto.getTitle() == null || bannerDto.getTitle().equals("")) {
            throw new CustomException(CustomError.BANNER_TITLE_ERROR);
        }

        // 배너 예약 - 시작일
        if (bannerDto.getStartDate() == null || bannerDto.getStartDate().equals("")) {
            throw new CustomException(CustomError.BANNER_START_DATE_ERROR);
        }

        // 배너 예약 - 종료일
        if (bannerDto.getEndDate() == null || bannerDto.getEndDate().equals("")) {
            throw new CustomException(CustomError.BANNER_END_DATE_ERROR);
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false); // 입력한 값이 잘못된 형식일 경우 오류 발생

            // 날짜 형식 변환
            Date startDate = sdf.parse(bannerDto.getStartDate());
            Date endDate = sdf.parse(bannerDto.getEndDate());

            // 시작일이 종료일을 넘길 경우
            if (startDate.compareTo(endDate) > 0) {
                throw new CustomException(CustomError.BANNER_START_DATE_ERROR);
            }
        } catch (ParseException e) {
            throw new CustomException(CustomError.BANNER_END_DATE_ERROR);
        }

        // 이미지 파일
        if (!chkIsEmptyImage(bannerDto.getImageFile_364()) && !chkIsEmptyImage(bannerDto.getImageFile_260()) && !chkIsEmptyImage(bannerDto.getImageFile_160())) {
            throw new CustomException(CustomError.BANNER_IMAGE_ERROR);
        }

        return true;
    }

    /**
     * 이미지 등록
     * @param uploadResponse
     * @param idx
     * @param s3Path
     * @param type
     */
    @Transactional
    public void registerImage(List<HashMap<String,Object>> uploadResponse, Integer idx, String s3Path, Integer type) {
        // type - 배너 이미지 타입(1: 720*364, 2: 720*260, 3: 720*160)

        int i = 1;
        for (HashMap<String, Object> map : uploadResponse) {
            map.put("idx", idx);        // banner_idx
            map.put("path", s3Path);    // path
            map.put("type", type);      // 이미지 타입
            map.put("regdate", dateLibrary.getDatetime());

            // insertedId
            map.put("insertedId", 0);

            i++;
        }
        bannerDao.registerImage(uploadResponse);
    }

    /*****************************************************
     *  SubFunction - Etc
     ****************************************************/

    /**
     * List<MultipartFile> image empty 체크
     * List 형식임으로 각 image 별로 isEmpty() 체크 처리
     * return Boolean
     */
    public Boolean chkIsEmptyImage(List<MultipartFile> uploadFileImgList) {
        boolean isEmptyValue = false;
        int isNotEmptyCnt = 0;
        if (!uploadFileImgList.isEmpty()) {
            for(MultipartFile image : uploadFileImgList){
                if(!image.isEmpty()) {
                    isNotEmptyCnt += 1;
                }
            }

            if (isNotEmptyCnt > 0) {
                isEmptyValue = true;
            }
        }

        return isEmptyValue;
    }


    public Map<String, Object> bannerMappingMap(BannerDto bannerDto, List<Integer> list, Integer categoryIdx) {
        Map<String, Object> map = new HashMap<>();

        map.put("bannerIdx", bannerDto.getInsertedId());
        map.put("regdate", bannerDto.getRegdate());
        if (categoryIdx != null) {
            map.put("categoryIdx", categoryIdx);
            map.put("genre", list);
        } else {
            map.put("type", list);
        }

        return map;
    }


    /**
     * 문자변환 list
     * @param list
     */
    public void convertTextList(List<BannerDto> list) {

        for (BannerDto dto : list) {
            convertText(dto);
        }
    }

    /**
     * 문자변환 dto
     * @param dto
     */
    public void convertText(BannerDto dto) {

        // 상태값
        if (dto.getState() == 1) {
            // 정상
            dto.setStateText(super.langMessage("lang.banner.state.normal"));
            dto.setStateBg("badge-success");

        } else if (dto.getState() == 0) {
            // 중지
            dto.setStateText(super.langMessage("lang.banner.state.cancel"));
            dto.setStateBg("badge-danger");
        }

        // 노출 영역
        if (dto.getStrType() != null) {
            String[] type = dto.getStrType().split(",");
            String typeText = "";

            for (String str : type) {
                switch (str) {
                    case "1" :
                        typeText += super.langMessage("lang.banner.type.main.top") + ", ";
                        break;
                    case "2" :
                        typeText += super.langMessage("lang.banner.type.main.bottom") + ", ";
                        break;
                    case "3" :
                        typeText += super.langMessage("lang.banner.type.viewer") + ", ";
                        break;
                    case "4" :
                        typeText += super.langMessage("lang.banner.type.community.list") + ", ";
                        break;
                    case "5" :
                        typeText += super.langMessage("lang.banner.type.community.content") + ", ";
                        break;
                    case "6" :
                        typeText += super.langMessage("lang.banner.type.charging") + ", ";
                        break;
                    case "7" :
                        typeText += super.langMessage("lang.banner.type.mypage.guest") + ", ";
                        break;
                    case "8" :
                        typeText += super.langMessage("lang.banner.type.mypage.member") + ", ";
                        break;
                    default :
                        break;
                }
            }
            // 마지막 노출 영역 세팅 시 쉼표 제거
            dto.setTypeText(typeText.substring(0, typeText.length()-2));
        }
    }
}

package com.architecture.admin.services.contents;

import com.architecture.admin.libraries.S3Library;
import com.architecture.admin.libraries.ThumborLibrary;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.contents.EpisodeDao;
import com.architecture.admin.models.daosub.contents.EpisodeDaoSub;
import com.architecture.admin.models.dto.contents.EpisodeDto;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.log.AdminActionLogService;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

@RequiredArgsConstructor
@Service
@Transactional
public class EpisodeService extends BaseService {

    private final EpisodeDao episodeDao;
    private final EpisodeDaoSub episodeDaoSub;
    private final S3Library s3Library;
    private final ThumborLibrary thumborLibrary;
    private final AdminActionLogService adminActionLogService;// 관리자 action log 처리용

    /**
     * 회차 목록
     * @param contentsIdx
     * @return
     */
    @Transactional(readOnly = true)
    public List<EpisodeDto> getList(Integer contentsIdx) {

        // 회차 목록
        List<EpisodeDto> list = episodeDaoSub.getList(contentsIdx);

        // 이미지 fullUrl
        for(EpisodeDto episodeDto : list) {
            episodeDto.setImageList(thumborLibrary.getImageCFUrl(episodeDto.getImageList()));
        }

        // 문자변환
        if (!list.isEmpty()) {
            convertTextList(list);
        }
        return list;
    }

    /**
     * 회차 등록
     * @param episodeDto
     * @return
     */
    @Transactional
    public Long register(EpisodeDto episodeDto) {

        // 등록일
        episodeDto.setRegdate(dateLibrary.getDatetime());

        // 오픈날짜(UTC)
        String pubdate = episodeDto.getPubdate() + " " + episodeDto.getPubTime().toString().concat(":00:00");
        episodeDto.setPubdate(dateLibrary.localTimeToUtc(pubdate));

        // TODO validate
        validate(episodeDto);

        /** 회차 등록 */
        episodeDao.register(episodeDto);
        Long result = episodeDto.getInsertedId();

        /** 회차 이벤트(할인) 등록 */
        if (!episodeDto.getEventStartDate().isEmpty() && !episodeDto.getEventEndDate().isEmpty()) {
            // 이벤트 기간(UTC)
            String eventStartDate = episodeDto.getEventStartDate() + " " + episodeDto.getEventStartTime().toString().concat(":00:00");
            String eventEndDate = episodeDto.getEventEndDate() + " " + episodeDto.getEventEndTime().toString().concat(":00:00");
            episodeDto.setEventStartDate(dateLibrary.localTimeToUtc(eventStartDate));
            episodeDto.setEventEndDate(dateLibrary.localTimeToUtc(eventEndDate));
        }
        episodeDao.registerEventCoin(episodeDto);


        /** 회차 추가 정보 등록 */
        episodeDao.registerInfo(episodeDto);

        /** 회차 이미지 등록 */
        // s3에 저장될 path
        String s3Path = "kr/contents/"+ episodeDto.getContentsIdx() + "/episode/" + episodeDto.getInsertedId();

        /** 가로 이미지 원본 */
        String device = "origin";
        String type = "width";

        // webp 이미지 삭제
        episodeDto.getFileDataWidth().removeIf(multipartFiles -> multipartFiles.getContentType().contains("webp"));
        episodeDto.getFileDataWidth().removeIf(multipartFiles -> multipartFiles.isEmpty());
        List<MultipartFile> widthImage = episodeDto.getFileDataWidth();

        // 파일 있는지 체크
        Boolean chkWidthImage = chkIsEmptyImage(widthImage);
        if (Boolean.TRUE.equals(chkWidthImage)) {
            // s3 upload
            List<HashMap<String,Object>> uploadResponse = s3Library.uploadFileNew(widthImage, s3Path);

            uploadResponse = imageSize(uploadResponse, 104);

            // db insert
            registerImage(uploadResponse, episodeDto.getInsertedId(), s3Path, device, type, new ArrayList<>());
        }
        /** //컨텐츠 이미지 등록 */

        return result;
    }

    /**
     * 이미지 등록
     * @param uploadResponse
     * @param idx
     * @param s3Path
     * @param device
     * @param type
     * @param parents
     * @return
     */
    @Transactional
    public List<Integer> registerImage(List<HashMap<String,Object>> uploadResponse, Long idx, String s3Path, String device, String type, List<Integer> parents) {

        int i = 1;
        for (HashMap<String, Object> map : uploadResponse) {
            map.put("idx", idx);
            map.put("path", s3Path);
            map.put("sort", i);
            map.put("device", device);
            map.put("type", type);
            map.put("regdate", dateLibrary.getDatetime());

            if (!parents.isEmpty()) {
                map.put("parent", parents.get(i-1) );
            } else {
                map.put("parent", 0);
            }

            // insertedId
            map.put("insertedId", 0);

            // 대량 insertedId
            map.put("keyId",0);

            i++;
        }

        episodeDao.registerImage(uploadResponse);

        // 원본 insert idx list
        List<Integer> parentList = new ArrayList<>();
        for(HashMap<String, Object> map : uploadResponse) {
            map.forEach((key, value) -> {
                if (key.equals("keyId")) {
                    parentList.add(Integer.parseInt(value.toString()));
                }
            });
        }

        return parentList;
    }

    /**
     * 회차 상세
     * @param idx
     * @return
     */
    @Transactional(readOnly = true)
    public EpisodeDto getView(Integer idx) {
        if (idx == null || idx < 1) {
            throw new CustomException(CustomError.IDX_EPISODE_ERROR);
        }

        // 회차 상세
        EpisodeDto view = episodeDaoSub.getView(idx);

        if (view == null) {
            throw new CustomException(CustomError.NO_DATA_EPISODE_ERROR);
        }

        if (view.getPubdate() != null) {
            String[] pubdate = view.getPubdate().split(" ");
            view.setPubdate(pubdate[0]);
            view.setPubTime(pubdate[1].substring(0,2));
        }

        if (view.getEventStartDate() != null) {
            String[] eventStartDate = view.getEventStartDate().split(" ");
            view.setEventStartDate(eventStartDate[0]);
            view.setEventStartTime(eventStartDate[1].substring(0,2));
        }

        if (view.getEventEndDate() != null) {
            String[] eventEndDate = view.getEventEndDate().split(" ");
            view.setEventEndDate(eventEndDate[0]);
            view.setEventEndTime(eventEndDate[1].substring(0,2));
        }

        // 이미지 fullUrl
        view.setImageList(thumborLibrary.getImageCFUrl(view.getImageList()));

        return view;
    }

    /**
     * 회차 수정
     * @param episodeDto
     * @return
     */
    @Transactional
    public Integer modifyEpisode(EpisodeDto episodeDto) {

        // 결과
        Integer result = null;

        if (episodeDto.getIdx() == null || episodeDto.getIdx() < 1) {
            throw new CustomException(CustomError.IDX_EPISODE_ERROR);
        }

        // 오픈날짜(UTC)
        if (!episodeDto.getPubdate().isEmpty()) {
            String pubdate = episodeDto.getPubdate() + " " + episodeDto.getPubTime() + ":00:00";
            episodeDto.setPubdate(dateLibrary.localTimeToUtc(pubdate));
        }

        /** 회차 수정 */
        result = episodeDao.modify(episodeDto);

        /** 회차 이벤트(할인) 수정 */
        if (!episodeDto.getEventStartDate().isEmpty() && !episodeDto.getEventEndDate().isEmpty()) {
            // 이벤트 기간(UTC)
            String eventStartDate = episodeDto.getEventStartDate() + " " + episodeDto.getEventStartTime() + ":00:00";
            String eventEndDate = episodeDto.getEventEndDate() + " " + episodeDto.getEventEndTime() + ":00:00";
            episodeDto.setEventStartDate(dateLibrary.localTimeToUtc(eventStartDate));
            episodeDto.setEventEndDate(dateLibrary.localTimeToUtc(eventEndDate));
        } else {
            episodeDto.setEventStartDate("");
            episodeDto.setEventEndDate("");
        }
        episodeDao.modifyEventCoin(episodeDto);

        /** 회차 이미지 수정 */
        // s3에 저장될 path
        String s3Path = "kr/contents/"+ episodeDto.getContentsIdx() + "/episode/" + episodeDto.getIdx();

        String device = "origin";
        String type = "width";

        /** 가로 이미지 원본 */
        // webp 이미지 삭제
        episodeDto.getFileDataWidth().removeIf(multipartFiles -> multipartFiles.getContentType().contains("webp"));
        episodeDto.getFileDataWidth().removeIf(multipartFiles -> multipartFiles.isEmpty());
        List<MultipartFile> widthImage = episodeDto.getFileDataWidth();

        // s3 upload(원본)
        List<HashMap<String,Object>> uploadResponse = s3Library.uploadFileNew(widthImage, s3Path);

        uploadResponse = imageSize(uploadResponse, 104);

        // db 등록한 insertedId
        List<Integer> parentList = new ArrayList<>();

        // upload 가 있는 경우
        if (!uploadResponse.isEmpty()) {
            // db insert
            parentList = registerImage(uploadResponse, episodeDto.getIdx(), s3Path, device, type, new ArrayList<>());
        }

        // sort 재정렬
        List<Map<String, Object>> mapList = new ArrayList<>();

        Integer uploadIdx = 0;
        Integer sort = 1;

        JSONArray jsonArray = new JSONArray(uploadResponse);
        for (Object widthObj : jsonArray) {
            Map<String, Object> map = new HashMap<>();
            JSONObject jsonObject = (JSONObject) widthObj;
            String idx = jsonObject.get("idx").toString();

            if (idx.equals("null")) {
                map.put("idx", parentList.get(uploadIdx));
                uploadIdx++;
            } else {
                map.put("idx", Integer.parseInt(idx));
            }
            map.put("sort", sort);
            map.put("episodeIdx", episodeDto.getIdx());
            sort++;

            mapList.add(map);
        }

        if (!mapList.isEmpty()) {
            // sort 수정 - origin
            episodeDao.imageSort(mapList);
        }
        /** //컨텐츠 이미지 등록 */

        // 관리자 action log
        adminActionLogService.regist(episodeDto, Thread.currentThread().getStackTrace());

        return result;
    }

    /**
     * 이미지 삭제
     * @param idx
     * @return
     */
    @Transactional
    public Integer deleteEpisodeImage(Integer idx) {

        // idx validation
        if (idx == null || idx < 1) {
            throw new CustomException(CustomError.IDX_EPISODE_ERROR);
        }

        // 관리자 action log
        adminActionLogService.regist(idx, Thread.currentThread().getStackTrace());

        // origin 삭제
        return episodeDao.deleteImage(idx);
    }

    /**
     * 회차 순서 정보
     * @param contentsIdx
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getLastEpisodeOrder(Integer contentsIdx) {
        return episodeDaoSub.getLastEpisodeOrder(contentsIdx);
    }

    /*****************************************************
     *  SubFunction - Etc
     ****************************************************/

    /*
     * List<MultipartFile> image empty 체크
     * List 형식임으로 각 image 별로 isEmpty() 체크 처리
     * return Boolean
     */
    public Boolean chkIsEmptyImage(List<MultipartFile> uploadFileImgList) {
        boolean isEmptyValue = false;
        int isNotEmptyCnt = 0;
        for(MultipartFile image : uploadFileImgList){
            if(!image.isEmpty()) {
                isNotEmptyCnt += 1;
            }
        }

        if (isNotEmptyCnt > 0) {
            isEmptyValue = true;
        }
        return isEmptyValue;
    }


    /**
     * validate
     * @param episodeDto
     * @return
     */
    private Boolean validate(EpisodeDto episodeDto) {

        return true;
    }


    /**
     * 문자변환 list
     * @param list
     */
    public void convertTextList(List<EpisodeDto> list) {

        for (EpisodeDto dto : list) {
            convertText(dto);
        }
    }

    /**
     * 문자변환 dto
     * @param dto
     */
    public void convertText(EpisodeDto dto) {
        // 상태값
        if (dto.getState() == 1) {
            // 정상
            dto.setStateText(super.langMessage("lang.contents.state.normal"));
            dto.setStateBg("badge-success");
        } else if (dto.getState() == 0) {
            // 중지
            dto.setStateText(super.langMessage("lang.contents.state.unsold"));
            dto.setStateBg("badge-danger");
        }
    }

    /**
     * 리사이징 이미지 사이즈 구하기
     * @param uploadResponse
     * @param width
     * @return
     */
    public List<HashMap<String, Object>> imageSize(List<HashMap<String, Object>> uploadResponse, int width) {
        for(HashMap<String, Object> data : uploadResponse) {
            String url  = data.get("fileUrl").toString();
            String fullUrl = s3Library.getUploadedFullUrl(url);
            String suffix = url.substring(url.lastIndexOf('.')+1).toLowerCase();

            Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);

            if (iter.hasNext()) {
                ImageReader reader = iter.next();
                try {
                    InputStream is = new URL(fullUrl).openStream();
                    ImageInputStream stream = ImageIO.createImageInputStream(is);

                    reader.setInput(stream);
                    int widthOrg = reader.getWidth(reader.getMinIndex());
                    int heightOrg = reader.getHeight(reader.getMinIndex());

                    int height = (width * heightOrg / widthOrg);

                    data.put("width", width);
                    data.put("height", height);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    reader.dispose();
                }
            }
        }

        return uploadResponse;
    }

}

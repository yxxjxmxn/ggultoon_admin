package com.architecture.admin.services.admin;

import com.architecture.admin.libraries.PaginationLibray;
import com.architecture.admin.libraries.S3Library;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.admin.CpDao;
import com.architecture.admin.models.daosub.admin.CpDaoSub;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.admin.CpDto;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.log.AdminActionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Transactional
public class CpService extends BaseService {

    private final CpDao cpDao;
    private final CpDaoSub cpDaoSub;
    private final S3Library s3Library;
    private final AdminActionLogService adminActionLogService;// 관리자 action log 처리용

    // 설정된 S3 tmpUpload 폴더
    @Value("${cloud.aws.s3.tmpFolder}")
    private String s3TmpUploadFolder;

    /**
     * CP관리자 전체 카운트
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public int getCpTotalCnt(SearchDto searchDto) {

        // 목록 전체 count
        int totalCnt = cpDaoSub.getCpTotalCnt(searchDto);

        // 보여줄 데이터 개수 set
        Integer searchCount = searchDto.getSearchCount();
        if(searchCount != null) {
            searchDto.setRecordSize(searchCount);
        } else {
            searchDto.setRecordSize(searchDto.getLimit());
        }

        // paging
        PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
        searchDto.setPagination(pagination);

        return totalCnt;
    }

    /**
     * 관리자 목록 전체 조회
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public List<CpDto> getCpList(SearchDto searchDto) {

        // 목록 전체 count
        int totalCount = cpDaoSub.getCpTotalCnt(searchDto);

        // 보여줄 데이터 개수 set
        Integer searchCount = searchDto.getSearchCount();
        if(searchCount != null) {
            searchDto.setLimit(searchCount);
        } else {
            searchDto.setLimit(searchDto.getLimit());
        }

        // paging
        PaginationLibray pagination = new PaginationLibray(totalCount, searchDto);
        searchDto.setPagination(pagination);

        // list
        List<CpDto> listCp = cpDaoSub.getCpList(searchDto);

        if (!listCp.isEmpty()) {
            // 문자 변환
            stateText(listCp);
        }
        return listCp;
    }

    /**
     * 관리자 상세
     * @param idx
     * @return
     */
    @Transactional(readOnly = true)
    public CpDto getViewCp(Long idx) {

        // idx
        if (idx == null || idx < 1L) {
            throw new CustomException(CustomError.IDX_ADMIN_ERROR);
        }

        CpDto viewCp = cpDaoSub.getViewCp(idx);

        if (viewCp != null) {
            // 문자 변환
            stateText(viewCp);
        }
        // 이미지 URL 세팅
        setFileUrl(viewCp);
        return viewCp;
    }

    /**
     * CP관리자 등록
     *
     * @param cpDto
     * @return
     */
    @Transactional
    public Long registCpAdmin(CpDto cpDto) throws Exception {

        // 아이디/패스워드 검증
        String id = cpDto.getId();
        String password = cpDto.getPassword();

        if (id == null || id.equals("")){
            throw new CustomException(CustomError.JOIN_ID_ERROR);
        }
        if (password == null || password.equals("")){
            throw new CustomException(CustomError.JOIN_PW_ERROR);
        }

        if (cpDto.getName() == null || cpDto.getName().equals("")){
            throw new CustomException(CustomError.JOIN_NAME_ERROR);
        }
        // 패스워드 암호화
        cpDto.setPassword(super.encrypt(password));
        insert(cpDto);

        //사업자등록증, 통장사본 업로드
        fileUpload(cpDto, cpDto.getInsertedId());

        // 관리자 action log
        adminActionLogService.regist(cpDto, Thread.currentThread().getStackTrace());

        return cpDto.getInsertedId();
    }

    /**
     * CP관리자 등록
     * 
     * @param cpDto
     * @return
     */
    @Transactional
    public Long insert(CpDto cpDto) {

        // 등록일
        cpDto.setRegdate(dateLibrary.getDatetime());

        // insert cp_member
        cpDao.insert(cpDto);

        // insert cp_member_info
        cpDao.insertInfo(cpDto);

        return cpDto.getInsertedId();
    }

    /**
     * 관리자 수정
     * @param cpDto
     * @return
     * @throws Exception
     */
    @Transactional
    public int modifyCpAdmin(CpDto cpDto) throws Exception {

        // idx
        if (cpDto.getIdx() == null || cpDto.getIdx() < 1L) {
            throw new CustomException(CustomError.IDX_ADMIN_ERROR);
        }
        
        //패스워드 입력 확인
        String password = cpDto.getPassword();
        if (password != null && !password.equals("")){
            // 패스워드 암호화
            cpDto.setPassword(super.encrypt(password));
        }
        
        int cnt = cpDao.modify(cpDto);
        cpDao.modifyInfo(cpDto);

        // 관리자 action log
        adminActionLogService.regist(cpDto, Thread.currentThread().getStackTrace());

        //사업자등록증, 통장사본 업로드
        fileUpload(cpDto, cpDto.getIdx());

        return cnt;
    }

    /**
     * 사업자등록증, 통장사본 업로드
     * @param cpDto
     * @param idx cp_member.idx
     * @throws Exception
     */
    public void fileUpload(CpDto cpDto, long idx) {
        // s3에 저장될 path
        String s3Path = "cp/"+ idx;

        // List<MultipartFile> image empty 체크
        if (cpDto.getBusinessFile() != null && Boolean.FALSE.equals(cpDto.getBusinessFile().isEmpty())) {
            List<HashMap<String,Object>> response = imageUpload(cpDto.getBusinessFile(),s3Path);
            response.get(0).put("type","1");
            response.get(0).put("idx",idx);
            response.get(0).put("regdate",dateLibrary.getDatetime());
            // 이미지 등록
            cpDao.fileUpload(response.get(0));
        }

        // List<MultipartFile> image empty 체크
        if (cpDto.getBankFile() != null && Boolean.FALSE.equals(cpDto.getBankFile().isEmpty())) {
            List<HashMap<String,Object>> response = imageUpload(cpDto.getBankFile(),s3Path);
            response.get(0).put("type","0");
            response.get(0).put("idx",idx);
            response.get(0).put("regdate",dateLibrary.getDatetime());
            // 이미지 등록
            cpDao.fileUpload(response.get(0));
        }
    }

    /**
     * 상품 사진 등록
     * @param multipartFile
     * @return
     */
    private List<HashMap<String,Object>>imageUpload(MultipartFile multipartFile,String s3Path) {
        List<MultipartFile> imageData = new ArrayList<>();
        List<HashMap<String, Object>> response;
        String contentType = Objects.requireNonNull(multipartFile.getContentType());
        // content-type이 image/*가 아닐 경우 해당 루프 진행하지 않음
        if (contentType.contains("image")) {
            try {
                // 원본
                imageData.add(multipartFile);

                // s3업로드할 파일 정보
                response = s3Library.uploadFileNew(imageData, s3Path);

            } catch (Exception e) {
                throw new CustomException(CustomError.IMAGE_RESIZE_ERROR);
            }
        } else {
            // 이미지 파일이 아닙니다.
            throw new CustomException(CustomError.NO_IMAGE_PRODUCT_ERROR);
        }
        return response;
    }


    /**
     * CP사 이름 목록
     * @return
     */
    @Transactional(readOnly = true)
    public List<CpDto> getCompanyList() {
        return cpDaoSub.getCompanyList();
    }

    /*****************************************************
     *  SUB
     ****************************************************/

    /**
     * 문자변환 list
     * @param list
     */
    public void stateText(List<CpDto> list)  {
        for (CpDto l : list) {
            stateText(l);
        }
    }

    /**
     * 문자변환 dto
     * @param dto
     */
    public void stateText(CpDto dto) {
        if (dto.getState() != null) {
            if (dto.getState() == 0) {
                // 탈퇴
                dto.setStateText(super.langMessage("lang.cp.state.out"));
                dto.setStateBg("badge-danger");
            } else if (dto.getState() == 1) {
                // 정상
                dto.setStateText(super.langMessage("lang.cp.state.normal"));
                dto.setStateBg("badge-success");
            } else if (dto.getState() == 2) {
                // 확인중
                dto.setStateText(super.langMessage("lang.cp.state.confirm"));
                dto.setStateBg("badge-warning");
            } else if (dto.getState() == 3) {
                // 반려
                dto.setStateText(super.langMessage("lang.cp.state.deny"));
                dto.setStateBg("badge-warning");
            } else if (dto.getState() == 4) {
                // 대기
                dto.setStateText(super.langMessage("lang.cp.state.wait"));
                dto.setStateBg("badge-warning");
            }
        }
    }

    /**
     * 문자변환 dto
     * @param dto
     */
    public void setFileUrl(CpDto dto) {
        if (dto.getBankFileUrl() != null) {
            dto.setBankFileUrl(s3Library.getUploadedFullUrl(dto.getBankFileUrl()));
        }
        if (dto.getBankFileUrl() != null) {
            dto.setBusinessFileUrl(s3Library.getUploadedFullUrl(dto.getBusinessFileUrl()));
        }
    }
}

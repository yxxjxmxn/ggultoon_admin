package com.architecture.admin.services.admin;

import com.architecture.admin.config.SessionConfig;
import com.architecture.admin.libraries.PaginationLibray;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.admin.AdminDao;
import com.architecture.admin.models.daosub.admin.AdminDaoSub;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.admin.AdminDto;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.log.AdminActionLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class AdminService extends BaseService {

    private final AdminDao adminDao;
    private final AdminDaoSub adminDaoSub;
    private final AdminActionLogService adminActionLogService;// 관리자 action log 처리용
    //private final JwtInterceptor jwtInterceptor;

    /**
     * 관리자 회원가입
     * @param adminDto
     * @return
     */
    @Transactional
    public Long regist(AdminDto adminDto) throws Exception {

        // 아이디/패스워드 검증
        String id = adminDto.getId();
        String password = adminDto.getPassword();

        if (id == null || id.equals("")){
            throw new CustomException(CustomError.JOIN_ID_ERROR);
        }
        if (password == null || password.equals("")){
            throw new CustomException(CustomError.JOIN_PW_ERROR);
        }

        // 패스워드 확인
        String passwordConfirm = adminDto.getPasswordConfirm();
        if (!password.equals(passwordConfirm)) {
            throw new CustomException(CustomError.PASSWORD_CONFIRM);
        }

        if (adminDto.getName() == null || adminDto.getName().equals("")){
            throw new CustomException(CustomError.JOIN_NAME_ERROR);
        }

        // 패스워드 암호화
        adminDto.setPassword(super.encrypt(password));

        // 관리자 등록
        return insertAdmin(adminDto);
    }

    /**
     * 관리자 로그인
     * @param adminDto
     * @return
     */
    @Transactional
    public Boolean login(AdminDto adminDto, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {

        // 아이디/패스워드 검증
        String id = adminDto.getId();
        String password = adminDto.getPassword();

        if (id == null || id.equals("")){
            throw new CustomException(CustomError.LOGIN_ID_ERROR);
        }
        if (password == null || password.equals("")){
            throw new CustomException(CustomError.LOGIN_PW_ERROR);
        }

        // 패스워드 암호화
        adminDto.setPassword(super.encrypt(password));

        // login
        AdminDto userInfo = adminDaoSub.getInfoForLogin(adminDto);

        if (userInfo != null && userInfo.getIdx() > 0 ) {

            if(userInfo.getState() != 1){ // 계정 상태 확인 (정상 : 1)
                throw new CustomException(CustomError.ADMIN_STATE_ERROR);
            }

            // 마지막 로그인 날짜 업데이트
            userInfo.setLastLoginDate(dateLibrary.getDatetime());
            adminDao.updateLastDate(userInfo);

            // 세션 생성
            session.setAttribute(SessionConfig.LOGIN_ID, userInfo.getId());
            // 관리자 정보 입력 Object -> json
            ObjectMapper objectMapper = new ObjectMapper();
            session.setAttribute(SessionConfig.ADMIN_INFO, objectMapper.writeValueAsString(userInfo));
            // 세션 만료 시간 설정
            session.setMaxInactiveInterval(SessionConfig.EXPIRED_TIME);// 30분
            // 레디스 key 입력
            String sKey = "session_" + userInfo.getId();
            session.setAttribute(sKey, userInfo.toString());
            // 세션 정보 레디스에 적용
            super.setRedis(sKey, adminDto.toString(), SessionConfig.EXPIRED_TIME);
            // jwt access/refresh 토큰 생성 및 쿠키에 저장
            //jwtInterceptor.setJwtToken(httpRequest, httpResponse);

            return true;
        } else {
            return false;
        }
    }

    /**
     * 관리자 로그아웃
     */
    @Transactional
    public void logout() {
        // 세션 아이디 가져오기
        String sId = (String) session.getAttribute(SessionConfig.LOGIN_ID);

        // 레디스 세션 정보 삭제
        String sKey = "session_" + sId;
        super.removeRedis(sKey);

        // 세션 비활성화
        session.invalidate();
    }

    /**
     * 관리자 전체 카운트
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public int getAdminTotalCnt(SearchDto searchDto) {

        // 목록 전체 count
        int totalCnt = adminDaoSub.getAdminTotalCnt(searchDto);

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
    public List<AdminDto> getAdminList(SearchDto searchDto) {

        // 목록 전체 count
        int totalCount = adminDaoSub.getAdminTotalCnt(searchDto);

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
        List<AdminDto> listAdmin = adminDaoSub.getAdminList(searchDto);

        if (!listAdmin.isEmpty()) {
            // 문자 변환
            stateText(listAdmin);
        }
        return listAdmin;
    }

    /**
     * 관리자 상세
     * @param idx (admin.idx)
     * @return
     */
    @Transactional(readOnly = true)
    public AdminDto getViewAdmin(Long idx) {

        // idx
        if (idx == null || idx < 1L) {
            throw new CustomException(CustomError.IDX_ADMIN_ERROR);
        }

        // view
        AdminDto viewAdmin = adminDaoSub.getViewAdmin(idx);

        if (viewAdmin != null) {
            // 문자 변환
            stateText(viewAdmin);
        }
        return viewAdmin;
    }

    /**
     * 관리자 수정
     * @param adminDto
     * @return
     */
    @Transactional
    public int modifyAdmin(AdminDto adminDto) {

        // idx
        if (adminDto.getIdx() == null || adminDto.getIdx() < 1L) {
            throw new CustomException(CustomError.IDX_ADMIN_ERROR);
        }

        // 관리자 action log
        adminActionLogService.regist(adminDto, Thread.currentThread().getStackTrace());

        return adminDao.modifyAdmin(adminDto);
    }

    /**
     * 내 정보 수정
     * @param adminDto
     * @return
     */
    @Transactional
    public int modifyAdminMyPage(AdminDto adminDto) {

        // idx
        if (adminDto.getIdx() == null || adminDto.getIdx() < 1L) {
            throw new CustomException(CustomError.IDX_ADMIN_ERROR);
        }
        
        // 수정 전 내 정보 조회
        AdminDto beforeInfo = adminDaoSub.getViewAdmin(adminDto.getIdx());

        if (beforeInfo != null) {
            // level를 변경하는 경우 -> 수정 가능한 레벨인지 체크
            if (adminDto.getLevel() != null && beforeInfo.getLevel() != adminDto.getLevel()) {

                // 관리자 권한 체크
                super.checkAdminAccessLevel();

                // state를 변경하는 경우 -> 수정 가능한 레벨인지 체크
            } else if (adminDto.getState() != null && beforeInfo.getState() != adminDto.getState()) {

                // 관리자 권한 체크
                super.checkAdminAccessLevel();
            }
        }

        // 내 정보 수정
        return adminDao.modifyMyPage(adminDto);
    }

    /**
     * 비밀번호 수정
     * @param adminDto
     * @return
     */
    @SneakyThrows
    @Transactional
    public int modifyAdminPassword(AdminDto adminDto){

        // 유효성 체크
        adminPasswordValidation(adminDto);

        // 변경할 비밀번호 암호화
        adminDto.setPassword(super.encrypt(adminDto.getNewPassword()));

        // 비밀번호 변경
        return adminDao.modifyPassword(adminDto);
    }

    /*****************************************************
     *  SUB
     ****************************************************/

    /**
     * 관리자 등록
     * @param adminDto
     * @return
     */
    @Transactional
    public Long insertAdmin(AdminDto adminDto) {
        // 등록일
        adminDto.setRegdate(dateLibrary.getDatetime());
        // 관리자 등록
        adminDao.insertAdmin(adminDto);
        return adminDto.getInsertedId();
    }

    /**
     * 관리자 중복 아이디 검색
     * @param adminDto
     * @return
     */
    @Transactional(readOnly = true)
    public Boolean checkDupleId(AdminDto adminDto) {
        Integer iCount = adminDaoSub.getCountById(adminDto);

        // 0 이상이면 true , 아니면 false
        return iCount > 0;
    }

    /*****************************************************
     *  문자변환
     ****************************************************/

    /**
     * 문자변환 list
     * @param list
     */
    public void stateText(List<AdminDto> list)  {
        for (AdminDto l : list) {
            stateText(l);
        }
    }

    /**
     * 문자변환 dto
     * @param dto
     */
    public void stateText(AdminDto dto) {
        if (dto.getState() != null) {
            if (dto.getState() == 0) {
                // 탈퇴
                dto.setStateText(super.langMessage("lang.admin.state.out"));
                dto.setStateBg("badge-danger");
            } else if (dto.getState() == 1) {
                // 정상
                dto.setStateText(super.langMessage("lang.admin.state.normal"));
                dto.setStateBg("badge-success");
            } else if (dto.getState() == 2) {
                // 대기
                dto.setStateText(super.langMessage("lang.admin.state.wait"));
                dto.setStateBg("badge-warning");
            }
        }
    }

    /*****************************************************
     *  Validation
     ****************************************************/
    /**
     * 비밀번호 정보 유효성 검사
     * @param adminDto
     */
    private void adminPasswordValidation(AdminDto adminDto) throws Exception {

        /** 기본 유효성 검사 **/
        // 관리자 idx
        if (adminDto.getIdx() == null || adminDto.getIdx() < 1L) {
            throw new CustomException(CustomError.IDX_ADMIN_ERROR); // // 관리자 idx가 없습니다.
        }

        // 이전 비밀번호
        if (adminDto.getOldPassword() == null || adminDto.getOldPassword().isEmpty()) {
            throw new CustomException(CustomError.OLD_PASSWORD_EMPTY); // 이전 비밀번호를 입력해주세요.
        }

        // 변경할 비밀번호
        if (adminDto.getNewPassword() == null || adminDto.getNewPassword().isEmpty()) {
            throw new CustomException(CustomError.NEW_PASSWORD_EMPTY); // 변경할 비밀번호를 입력해주세요.
        }

        // 변경할 비밀번호 확인란
        if (adminDto.getPasswordConfirm() == null || adminDto.getPasswordConfirm().isEmpty()) {
            throw new CustomException(CustomError.PASSWORD_CONFIRM_EMPTY); // 변경할 비밀번호 확인란을 입력해주세요.
        }

        // 이전 비밀번호와 변경할 비밀번호가 같은 경우
        if (adminDto.getOldPassword().equals(adminDto.getNewPassword())) {
            throw new CustomException(CustomError.PASSWORD_CORRESPOND); // 변경할 비밀번호가 이전 비밀번호와 같습니다.
        }

        // 이전 비밀번호와 변경할 비밀번호 확인란이 같은 경우
        if (adminDto.getOldPassword().equals(adminDto.getPasswordConfirm())) {
            throw new CustomException(CustomError.PASSWORD_CORRESPOND); // 변경할 비밀번호가 이전 비밀번호와 같습니다.
        }

        // 변경할 비밀번호와 비밀번호 확인란이 다른 경우
        if (!adminDto.getNewPassword().equals(adminDto.getPasswordConfirm())) {
            throw new CustomException(CustomError.PASSWORD_NOT_CORRESPOND); // 변경할 비밀번호와 비밀번호 확인란이 일치하지 않습니다.
        }

        /** DB 조회 후 검사 **/
        // 변경하기 전 관리자 정보 조회
        AdminDto admin = adminDaoSub.getViewAdmin(adminDto.getIdx());

        // 관리자의 현재 비밀번호(암호화 상태)
        String nowPassword = admin.getPassword();

        // 입력받은 이전 비밀번호 값 암호화 처리
        String oldPasswordInput = super.encrypt(adminDto.getOldPassword());

        // 입력받은 변경할 비밀번호 값 암호화 처리
        String newPasswordInput = super.encrypt(adminDto.getNewPassword());

        // 입력받은 변경할 비밀번호 확인란 값 암호화 처리
        String passwordConfirmInput = super.encrypt(adminDto.getPasswordConfirm());

        // 입력받은 현재 비밀번호와 실제 현재 비밀번호가 다를 경우
        if (!nowPassword.equals(oldPasswordInput)) {
            throw new CustomException(CustomError.OLD_PASSWORD_ERROR); // 이전 비밀번호가 올바르지 않습니다.
        }

        // 변경할 비밀번호가 현재 비밀번호와 같을 경우
        if(nowPassword.equals(newPasswordInput)) {
            throw new CustomException(CustomError.PASSWORD_CORRESPOND); // 변경할 비밀번호가 현재 비밀번호와 같습니다.
        }

        // 변경할 비밀번호 확인란이 현재 비밀번호와 같을 경우
        if(nowPassword.equals(passwordConfirmInput)) {
            throw new CustomException(CustomError.PASSWORD_CORRESPOND); // 변경할 비밀번호가 현재 비밀번호와 같습니다.
        }
    }
}

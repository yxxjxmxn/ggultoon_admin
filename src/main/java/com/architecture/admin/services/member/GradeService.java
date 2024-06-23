package com.architecture.admin.services.member;

import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.member.GradeDao;
import com.architecture.admin.models.dto.member.MemberGradeDto;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.log.AdminActionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GradeService extends BaseService {

    private final GradeDao gradeDao;
    private final AdminActionLogService adminActionLogService;// 관리자 action log 처리용

    /**
     * 회원 등급 목록
     * @return
     */
    public static final List<Map<String, Object>> gradeList =  new ArrayList<>(){
        {
            add(Map.of("grade", 0, "condition", 0, "addMileage", 0, "payback", 0));
            add(Map.of("grade", 1, "condition", 10000, "addMileage", 0, "payback", 1));
            add(Map.of("grade", 2, "condition", 50000, "addMileage", 1, "payback", 2));
            add(Map.of("grade", 3, "condition", 100000, "addMileage", 2, "payback", 3));
            add(Map.of("grade", 4, "condition", 150000, "addMileage", 3, "payback", 4));
            add(Map.of("grade", 5, "condition", 200000, "addMileage", 5, "payback", 5));
        }
    };

    /**
     * 회원 등급 수정
     * @return
     */
    @Transactional
    public void modifyGrade(Long memberIdx) {
        // SimpleDateFormat 1일 0시 세팅
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-01 00:00:00");
        sdf.setLenient(false);
        Calendar day = Calendar.getInstance();

        // 현재시간(utc)
        String endDate = dateLibrary.getDatetime();

        // 현재월 포함 3개월전
        day.add(Calendar.MONTH, -2);
        // utc 변환
        String startDate = dateLibrary.localTimeToUtc(sdf.format(day.getTime()));

        if (memberIdx > 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("startDate", startDate);
            map.put("endDate", endDate);
            map.put("memberIdx", memberIdx);

            // 결제 금액
            Integer amount = gradeDao.getMemberPayment(map);

            MemberGradeDto memberGradeDto = myGrade(amount);
            if (memberGradeDto.getGrade() != null) {
                memberGradeDto.setIdx(memberIdx);
                // 현재 등급 수정
                gradeDao.modifyGrade(memberGradeDto);

                // 관리자 action log
                adminActionLogService.regist(memberGradeDto, Thread.currentThread().getStackTrace());
            }
        } else {
            throw new CustomException(CustomError.IDX_MEMBER_ERROR); // 로그인 후 이용해주세요.
        }
    }

    /**
     * 회원 등급 정보
     * @param memberPayment
     * @return
     */
    private MemberGradeDto myGrade(Integer memberPayment) {

        MemberGradeDto dto = new MemberGradeDto();
        for (Map<String, Object> grade : gradeList) {
            if (memberPayment >= Integer.parseInt(grade.get("condition").toString())) {
                dto.setAmount(memberPayment);
                dto.setGrade(Integer.parseInt(grade.get("grade").toString()));
                dto.setCondition(Integer.parseInt(grade.get("condition").toString()));
                dto.setAddMileage(Integer.parseInt(grade.get("addMileage").toString()));
                dto.setPayback(Integer.parseInt(grade.get("payback").toString()));
            }
        }
        return dto;
    }
}

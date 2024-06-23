package com.architecture.admin.services.code;

import com.architecture.admin.models.dao.code.CodeDao;
import com.architecture.admin.models.daosub.code.CodeDaoSub;
import com.architecture.admin.models.dto.contents.CodeDto;
import com.architecture.admin.models.dto.contents.TagDto;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.log.AdminActionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class CodeService extends BaseService {

    private final CodeDao codeDao;
    private final CodeDaoSub codeDaoSub;
    private final AdminActionLogService adminActionLogService;// 관리자 action log 처리용


    /**
     * 코드 목록
     * @return
     */
    @Transactional(readOnly = true)
    public List<TagDto> getList() {
        return codeDaoSub.getList();
    }


    /**
     * 코드 추가
     * @param codeDto
     * @return
     */
    @Transactional
    public Integer registerCode(CodeDto codeDto) {

        Integer result = null;
        if (codeDto.getCode() != null) {
            codeDto.setRegdate(dateLibrary.getDatetime());
            result = codeDao.register(codeDto);
        }

        // 관리자 action log
        adminActionLogService.regist(codeDto, Thread.currentThread().getStackTrace());

        return result;
    }
}

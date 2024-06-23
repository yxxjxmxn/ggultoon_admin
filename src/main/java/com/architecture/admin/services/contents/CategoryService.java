package com.architecture.admin.services.contents;

import com.architecture.admin.models.daosub.contents.CategoryDaoSub;
import com.architecture.admin.models.dto.contents.CategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class CategoryService {

    private final CategoryDaoSub categoryDaoSub;

    /**
     * 카테고리 목록
     * @return
     */
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategory() {

        return categoryDaoSub.getCategory();
    }

}

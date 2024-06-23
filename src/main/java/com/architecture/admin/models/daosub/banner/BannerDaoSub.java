package com.architecture.admin.models.daosub.banner;

import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.banner.BannerDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface BannerDaoSub {


    /**
     * 배너 목록
     * @param searchDto
     * @return
     */
    List<BannerDto> getList(SearchDto searchDto);

    /**
     * 배너 마지막 순번
     * @return
     */
    Integer getSortNumber();


    /**
     * 배너 상세
     * @param idx
     * @return
     */
    BannerDto getView(Integer idx);
}

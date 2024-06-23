package com.architecture.admin.models.dao.contents;

import com.architecture.admin.models.dto.contents.AuthorDto;
import com.architecture.admin.models.dto.contents.CodeDto;
import com.architecture.admin.models.dto.contents.ContentsDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface ContentsDao {

    /**
     * 컨텐츠 등록
     * @param contentsDto
     * @return
     */
    Integer register(ContentsDto contentsDto);

    /**
     * 컨텐츠 추가 정보 등록
     * @param contentsDto
     * @return
     */
    Integer registerInfo(ContentsDto contentsDto);

    /**
     * 컨텐츠 CP 등록
     * @param contentsDto
     * @return
     */
    Integer registerCp(ContentsDto contentsDto);

    /**
     * 컨텐츠 이미지 등록
     * @param mapList
     * @return
     */
    Integer registerImage(List<HashMap<String,Object>> mapList);

    /**
     * 작가 등록
     * @param authorDto
     * @return
     */
    Integer registerAuthor(AuthorDto authorDto);

    /**
     * 컨텐츠 글, 그림작가 등록
     * @param authorDto
     * @return
     */
    Integer registerContentsAuthor(AuthorDto authorDto);

    /**
     * 작가 조회
     * @param name
     */
    Integer getAuthor(String name);

    /**
     * 컨텐츠 이벤트 무료
     * @param contentsDto
     * @return
     */
    Integer registerEventFree(ContentsDto contentsDto);

    /**
     * 코드 등록
     * @param codeDto
     * @return
     */
    Integer registerCode(CodeDto codeDto);

    /**
     * 연재 요일 등록
     * @param mapList
     * @return
     */
    Integer registerWeekly(List<Map<String, Object>> mapList);

    /**
     * 태그 등록
     * @param mapList
     * @return
     */
    Integer registerTag(List<Map<String, Object>> mapList);

    /**
     * 컨텐츠 수정
     * @param contentsDto
     * @return
     */
    Integer modify(ContentsDto contentsDto);

    /**
     * 컨텐츠 추가 정보 수정
     * @param contentsDto
     */
    Integer modifyInfo(ContentsDto contentsDto);

    /**
     * 연재 요일 매핑 삭제
     * @param idx
     */
    Integer deleteWeekly(Integer idx);

    /**
     * 태그 삭제
     * @param idx
     * @return
     */
    Integer deleteTag(Integer idx);

    /**
     * 컨텐츠 이벤트 무료 수정
     * @param contentsDto
     * @return
     */
    Integer modifyEventFree(ContentsDto contentsDto);


    /**
     * 작가 삭제
     * @param authorMap
     * @return
     */
    Integer deleteContentsAuthor(Map<String, Integer> authorMap);

    /**
     * 컨텐츠 CP 수정
     * @param contentsDto
     * @return
     */
    Integer modifyCp(ContentsDto contentsDto);

    /**
     * 컨텐츠 이미지 삭제(origin)
     * @param idx
     * @return
     */
    Integer deleteImage(Integer idx);

    /**
     * 컨텐츠 이미지 sort 수정
     * @param mapList
     * @return
     */
    Integer imageSort(List<Map<String, Object>> mapList);


    /**
     * 코드 매핑 삭제
     * @param idx
     * @return
     */
    Integer deleteCode(Integer idx);
}

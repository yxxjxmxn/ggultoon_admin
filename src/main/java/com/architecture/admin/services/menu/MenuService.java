package com.architecture.admin.services.menu;

import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.menu.MenuDao;
import com.architecture.admin.models.daosub.menu.MenuDaoSub;
import com.architecture.admin.models.dto.menu.MenuDto;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.log.AdminActionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.codehaus.groovy.runtime.typehandling.NumberMath.isInteger;

@Service
@RequiredArgsConstructor
public class MenuService extends BaseService {
    // mainDB
    private final MenuDao menuDao;
    // subDB
    private final MenuDaoSub menuDaoSub;
    private final AdminActionLogService adminActionLogService;// 관리자 action log 처리용

    /*****************************************************
     *  Modules
     ****************************************************/
    // 카테고리 등록
    @Transactional
    public Integer cateRegist(MenuDto menuDto) {
        Integer iInsertedIdx = cateInsert(menuDto);
        insertName(iInsertedIdx, menuDto);

        // 관리자 action log
        adminActionLogService.regist(menuDto, Thread.currentThread().getStackTrace());

        return iInsertedIdx;
    }

    // 하위 메뉴 등록
    @Transactional
    public Integer regist(MenuDto menuDto) {
        Integer iInsertedIdx = insert(menuDto);
        insertName(iInsertedIdx, menuDto);

        // 관리자 action log
        adminActionLogService.regist(menuDto, Thread.currentThread().getStackTrace());

        return iInsertedIdx;
    }

    // 카테고리 수정
    @Transactional
    public Integer cateModify(MenuDto menuDto) {
        Integer iAffectedRow = updateCate(menuDto);
        updateName(menuDto);

        // 관리자 action log
        adminActionLogService.regist(menuDto, Thread.currentThread().getStackTrace());
        return iAffectedRow;
    }

    /*****************************************************
     *  SubFunction - Select
     ****************************************************/
    // 리스트 가져오기
    @Transactional(readOnly = true)
    public List<MenuDto> getList(MenuDto params) { // choose database source

        List<MenuDto> list = menuDaoSub.getList(params);
        // 메뉴 통합
        List<MenuDto> menu = new ArrayList<>();

        for (MenuDto m : list) {
            if (m.getParent() == 0) {
                // 메인메뉴
                menu.add(m);
                for (MenuDto s : list) {
                    if (Objects.equals(m.getIdx(), s.getParent())) {
                        // 서브메뉴
                        menu.add(s);
                    }
                }
            }
        }
        return menu;
    }

    // 좌측 메뉴 리스트 가져오기
    @Transactional(readOnly = true)
    public List<MenuDto> getLeftList(Integer level) { // choose database source

        // validation
        if (level == null) {
            throw new CustomException(CustomError.MENU_ACCESS_FAIL);
        }

        List<MenuDto> list = menuDaoSub.getLeftList(level);
        // 메뉴 통합
        List<MenuDto> menu = new ArrayList<>();

        for (MenuDto m : list) {
            if (m.getParent() == 0) {
                // 메인메뉴
                menu.add(m);
                for (MenuDto s : list) {
                    if (Objects.equals(m.getIdx(), s.getParent())) {
                        // 서브메뉴
                        menu.add(s);
                    }
                }
            }
        }
        return menu;
    }

    // 정보 가져오기
    @Transactional(readOnly = true)
    public MenuDto getCateInfo(Integer idx) {

        // validation
        if (idx == null || !isInteger(idx)) {
            throw new CustomException(CustomError.MENU_ACCESS_FAIL);
        }

        return menuDaoSub.getCateInfo(idx);
    }

    // 현 메뉴의 다음 순서 메뉴 정보 조회
    @Transactional(readOnly = true)
    public MenuDto getNextMenu(Integer parent, Integer sort) {

        // validation
        if (parent == null || !isInteger(parent) || !isInteger(sort) || sort == null) {
            throw new CustomException(CustomError.MENU_ACCESS_FAIL);
        }

        // dto set
        MenuDto dto = MenuDto.builder()
                .parent(parent)
                .sort(sort)
                .build();

        // 다음 순서 메뉴 리턴
        return menuDaoSub.getNextMenu(dto);
    }

    // 현 메뉴의 이전 순서 메뉴 정보 조회
    @Transactional(readOnly = true)
    public MenuDto getPrevMenu(Integer parent, Integer sort) {

        // validation
        if (parent == null || !isInteger(parent) || !isInteger(sort) || sort == null) {
            throw new CustomException(CustomError.MENU_ACCESS_FAIL);
        }

        // dto set
        MenuDto dto = MenuDto.builder()
                .parent(parent)
                .sort(sort)
                .build();

        // 이전 순서 메뉴 리턴
        return menuDaoSub.getPrevMenu(dto);
    }

    // Menu의 접근 level 가져오기
    @Transactional(readOnly = true)
    public Integer getMenuLevel(Integer idx) {

        // validation
        if (idx == null || !isInteger(idx)) {
            throw new CustomException(CustomError.MENU_ACCESS_FAIL);
        }
        return menuDaoSub.getMenuLevel(idx);
    }

    // 현재 생성된 메뉴의 정렬 최대값 조회
    @Transactional(readOnly = true)
    public Integer getMaxSort() {
        return menuDaoSub.getMaxSort();
    }

    /*****************************************************
     *  SubFunction - Insert
     ****************************************************/

    // 카테고리 데이터 등록
    @Transactional
    public Integer cateInsert(MenuDto menuDto) {

        // validation
        if (menuDto.getName() == null || Objects.equals(menuDto.getName(), "")) {
            throw new CustomException(CustomError.MENU_NAME_FAIL);
        }

        // parent값 0으로 셋팅
        menuDto.setParent(0);

        // link 값 빈값 셋팅
        menuDto.setLink("");

        // regdate 셋팅
        menuDto.setRegdate(dateLibrary.getDatetime());

        // state가 빈값이면 0 으로 셋팅
        if (menuDto.getState() == null) {
            menuDto.setState(0);
        }

        //대카테고리 sort MAX값 가져오기
        Integer iCateSort = menuDaoSub.getLastSort(menuDto);

        //대카테고리 ort값 셋팅
        menuDto.setSort(iCateSort + 1);

        menuDao.insert(menuDto);

        return menuDto.getInsertedIdx();
    }

    // 서브 카테고리 데이터 등록
    @Transactional
    public Integer insert(MenuDto menuDto) {

        // 이름 빈값 체크
        if (menuDto.getName() == null || Objects.equals(menuDto.getName(), "")) {
            throw new CustomException(CustomError.MENU_NAME_FAIL);
        }

        // 링크 빈값 체크
        if (menuDto.getLink() == null || Objects.equals(menuDto.getLink(), "")) {
            throw new CustomException(CustomError.MENU_LINK_FAIL);
        }

        // state가 빈값이면 0 으로 셋팅
        if (menuDto.getState() == null) {
            menuDto.setState(0);
        }

        // regdate 셋팅
        menuDto.setRegdate(dateLibrary.getDatetime());

        // 대카테고리 sort MAX값 가져오기
        Integer iCateSort = menuDaoSub.getLastSort(menuDto);

        // 대카테고리 ort값 셋팅
        if (iCateSort == null) {
            menuDto.setSort(1);
        } else {
            menuDto.setSort(iCateSort + 1);
        }
        menuDao.insert(menuDto);

        return menuDto.getInsertedIdx();
    }

    // 내용 등록
    @Transactional
    public Integer insertName(Integer iInsertId, MenuDto menuDto) {

        // validation
        if (iInsertId == null || !isInteger(iInsertId)) {
            throw new CustomException(CustomError.MENU_ACCESS_FAIL);
        }

        menuDto.setMenuIdx(iInsertId);
        menuDao.insertName(menuDto);

        return menuDto.getMenuInsertedName();
    }

    /*****************************************************
     *  SubFunction - Update
     ****************************************************/

    // 데이터 수정 -- 카테고리
    @Transactional
    public Integer updateCate(MenuDto menuDto) {

        // 이름 빈값 체크
        if (menuDto.getName() == null || Objects.equals(menuDto.getName(), "")) {
            throw new CustomException(CustomError.MENU_NAME_FAIL);
        }

        // state가 빈값이면 0 으로 셋팅
        if (menuDto.getState() == null) {
            menuDto.setState(0);
        }

        // link가 빈값이면 빈값 셋팅
        if (menuDto.getLink() == null) {
            menuDto.setLink("");
        }

        menuDao.updateCate(menuDto);

        return menuDto.getAffectedRow();
    }

    // 데이터 수정 - 이름
    @Transactional
    public Integer updateName(MenuDto menuDto) {

        menuDao.updateName(menuDto);
        return menuDto.getMenuAffectedRow();
    }

    // 데이터 수정 - 정렬
    @Transactional
    public Integer updateSort(Integer idx, Integer sort) {

        // validation
        if (idx == null || !isInteger(idx)) {
            throw new CustomException(CustomError.MENU_ACCESS_FAIL);
        }

        // dto set
        MenuDto menu = MenuDto.builder()
                .idx(idx)
                .sort(sort)
                .build();
        
        // 정렬 수정
        return menuDao.updateSort(menu);
    }
}
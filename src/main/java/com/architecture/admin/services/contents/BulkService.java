package com.architecture.admin.services.contents;

import com.architecture.admin.libraries.S3Library;
import com.architecture.admin.libraries.SortLibrary;
import com.architecture.admin.libraries.ThumborLibrary;
import com.architecture.admin.models.dao.contents.BulkDao;
import com.architecture.admin.models.daosub.contents.BulkDaoSub;
import com.architecture.admin.models.dto.contents.*;
import com.architecture.admin.services.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;


@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BulkService extends BaseService {

    private final S3Library s3Library;
    private final ThumborLibrary thumborLibrary;
    private final SortLibrary sortLibrary;
    private final BulkDao bulkDao;
    private final BulkDaoSub bulkDaoSub;

    @Value("${spring.servlet.multipart.location}")
    private String ftpPath;

    @Value("/cont")
    private String ftpCont;


    /**
     * 작품 대량 등록
     * @param multipartFile
     * @return
     */
    @Transactional
    public Integer registerBulkContents(MultipartFile multipartFile) {

        Integer result = null;

        try {
            FileInputStream fileInputStream = (FileInputStream) multipartFile.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);

            int rowIndex = 0;
            //시트 수 (첫번째에만 존재하므로 0을 준다)
            //만약 각 시트를 읽기위해서는 FOR문을 한번더 돌려준다
            XSSFSheet sheet = workbook.getSheetAt(0);

            //행의 수
            int rows = sheet.getPhysicalNumberOfRows();

            List<ContentsDto> contentsList = new ArrayList<>();

            // 3번째 줄부터 data 시작
            for (rowIndex = 1; rowIndex < rows; rowIndex++) {

                //행을읽는다
                XSSFRow row = sheet.getRow(rowIndex);

                if (row != null && !cellToString(row.getCell(0)).equals("")) {
                    ContentsDto contentsDto = new ContentsDto();

                    // idx
                    String idx = cellToString(row.getCell(0));
                    if (idx != null && !idx.equals("")) {
                        contentsDto.setIdx(Integer.parseInt(idx));

                        // 중복 체크
                        Integer checkContents = bulkDaoSub.getContent(contentsDto.getIdx());
                        if (checkContents > 0) {
                            continue;
                        }

                    } else {
                        throw new Error( (rowIndex + 1) + "번째 줄의 idx 가 없습니다.");
                    }

                    // cp_member_idx
                    String cpMemberIdx = cellToString(row.getCell(1));
                    if (cpMemberIdx != null && !cpMemberIdx.equals("")) {
                        contentsDto.setCpMemberIdx(Integer.parseInt(cpMemberIdx));
                    } else {
                        throw new Error( (rowIndex + 1) + "번째 줄의 cp idx 가 없습니다.");
                    }

                    // 작품 제목
                    String title = cellToString(row.getCell(3));
                    if (title != null && !title.equals("")) {
                        contentsDto.setTitle(title);
                    } else {
                        throw new Error( (rowIndex + 1) + "번째 줄의 작품제목이 없습니다.");
                    }

                    // 글작가
                    String writer = cellToString(row.getCell(4));
                    if (writer != null && !writer.equals("")) { contentsDto.setWriter(writer); }

                    // 그림작가
                    String illustrator = cellToString(row.getCell(5));
                    if (illustrator != null && !illustrator.equals("")) { contentsDto.setIllustrator(illustrator); }

                    // view 카운팅
                    String viewDummy = cellToString(row.getCell(6));
                    if (viewDummy != null && !viewDummy.equals("")) {
                        contentsDto.setViewDummy(Integer.parseInt(viewDummy));
                    } else {
                        contentsDto.setViewDummy(0);
                    }

                    // 카테고리
                    String categoryIdx = cellToString(row.getCell(7));
                    if (categoryIdx != null && !categoryIdx.equals("")) {
                        contentsDto.setCategoryIdx(Integer.parseInt(categoryIdx));
                    } else {
                        throw new Error( (rowIndex + 1) + "번째 줄의 카테고리가 없습니다.");
                    }

                    // 장르
                    String genreIdx = cellToString(row.getCell(8));
                    if (genreIdx != null && !genreIdx.equals("")) { contentsDto.setGenreIdx(Integer.parseInt(genreIdx)); }

                    // 성인
                    String adult = cellToString(row.getCell(9));
                    if (adult != null && !adult.equals("")) {
                        contentsDto.setAdult(Integer.parseInt(adult));
                    } else {
                        contentsDto.setAdult(0);
                    }

                    // 성인관
                    String adultPavilion = cellToString(row.getCell(10));
                    if (adultPavilion != null && !adultPavilion.equals("")) {
                        contentsDto.setAdultPavilion(Integer.parseInt(adultPavilion));
                    } else {
                        contentsDto.setAdultPavilion(0);
                    }

                    // 연재상태
                    String progress = cellToString(row.getCell(11));
                    if (progress != null && !progress.equals("")) {
                        contentsDto.setProgress(Integer.parseInt(progress));
                    } else {
                        throw new Error( (rowIndex + 1) + "번째 줄의 연재상태 가 없습니다.");
                    }

                    // 연재요일
                    String weekly = cellToString(row.getCell(12));
                    if (weekly != null && !weekly.equals("")) { contentsDto.setWeekly(weekly); }

                    // 소개글
                    String description = cellToString(row.getCell(13));
                    if (description != null && !description.equals("")) { contentsDto.setDescription(description); }

                    // 레이블
                    String label = cellToString(row.getCell(14));
                    if (label != null && !label.equals("")) { contentsDto.setLabel(label); }

                    // 코드
                    String code = cellToString(row.getCell(15));
                    if (code != null && !code.equals("")) { contentsDto.setCode(Integer.parseInt(code)); }

                    // 정산타입
                    String contract = cellToString(row.getCell(16));
                    if (contract != null && !contract.equals("")) { contentsDto.setContract(Integer.parseInt(contract)); }

                    // 비율
                    String calculate = cellToString(row.getCell(17));
                    if (calculate != null && !calculate.equals("")) {
                        contentsDto.setCalculate(Integer.parseInt(calculate));
                        // 대량 등록시 정산비율 웹, 어플 같은 값 등록
                        contentsDto.setCalculateApp(Integer.parseInt(calculate));
                    }

                    // MG 금액
                    String guarantee = cellToString(row.getCell(18));
                    if (guarantee != null && !guarantee.equals("")) { contentsDto.setGuarantee(Integer.parseInt(guarantee)); }

                    // 독점 여부
                    String exclusive = cellToString(row.getCell(19));
                    if (exclusive != null && !exclusive.equals("")) {
                        contentsDto.setExclusive(Integer.parseInt(exclusive));
                    } else {
                        throw new Error( (rowIndex + 1) + "번째 줄의 독점여부 가 없습니다.");
                    }

                    // 단행본 여부
                    String publication = cellToString(row.getCell(20));
                    if (publication != null && !publication.equals("")) {
                        contentsDto.setPublication(Integer.parseInt(publication));
                    } else {
                        throw new Error( (rowIndex + 1) + "번째 줄의 단행본여부 가 없습니다.");
                    }

                    // 개정판 여부
                    String revision = cellToString(row.getCell(21));
                    if (revision != null && !revision.equals("")) {
                        contentsDto.setRevision(Integer.parseInt(revision));
                    } else {
                        throw new Error( (rowIndex + 1) + "번째 줄의 개정판여부 가 없습니다.");
                    }

                    // 면제 여부
                    String tax = cellToString(row.getCell(22));
                    if (tax != null && !tax.equals("")) {
                        contentsDto.setTax(Integer.parseInt(tax));
                    }

                    // 면세 유형
                    String taxType = cellToString(row.getCell(23));
                    if (taxType != null && !taxType.equals("")) {
                        contentsDto.setTaxType(Integer.parseInt(taxType));
                    }

                    // 면세 코드
                    String taxCode = cellToString(row.getCell(24));
                    if (taxCode != null && !taxCode.equals("")) {
                        contentsDto.setTaxCode(taxCode);
                    }

                    // 태그
                    String tagArr = cellToString(row.getCell(25));
                    if (tagArr != null && !tagArr.equals("")) {
                        contentsDto.setTagArr(tagArr);
                    }

                    // 오픈일
                    String pubdate = cellToString(row.getCell(26));
                    if (pubdate != null && !pubdate.equals("")) {
                        contentsDto.setPubdate(dateLibrary.localTimeToUtc(pubdate));
                    } else {
                        contentsDto.setPubdate(dateLibrary.getDatetime());
                    }

                    // 무료회차수
                    String freeEpisodeCnt = cellToString(row.getCell(27));
                    if (freeEpisodeCnt != null && !freeEpisodeCnt.equals("")) {
                        contentsDto.setFreeEpisodeCnt(Integer.parseInt(freeEpisodeCnt));
                    } else {
                        contentsDto.setFreeEpisodeCnt(1);
                    }

                    // 구매종류
                    String sellType = cellToString(row.getCell(28));
                    if (sellType != null && !sellType.equals("")) {
                        contentsDto.setSellType(Integer.parseInt(sellType));
                    }

                    // 등록일
                    contentsDto.setRegdate(dateLibrary.getDatetime());

                    contentsList.add(contentsDto);
                }
            }


            if (!contentsList.isEmpty()) {
                /** 작품 대량 등록 */
                result = bulkDao.registerBulkContents(contentsList);

                /** 작품 추가 정보 대량 등록 */
                bulkDao.registerBulkInfo(contentsList);

                /** 무료 회차 등록 */
                bulkDao.registerBulkEventFree(contentsList);

                /** 컨텐츠 CP 등록 */
                bulkDao.registerBulkContentsCp(contentsList);

                List<CodeDto> codeList = new ArrayList<>();
                List<Map<String, Object>> weeklyList = new ArrayList<>();
                List<Map<String, Object>> tagList = new ArrayList<>();
                List<AuthorDto> authorList = new ArrayList<>();

                for (ContentsDto dto : contentsList) {

                    /** 코드 등록 */
                    if (dto.getCode() != null && !dto.getCode().equals("")) {
                        CodeDto codeDto = new CodeDto();
                        codeDto.setCodeIdx(dto.getCode());
                        codeDto.setContentsIdx(dto.getIdx());
                        codeDto.setRegdate(dateLibrary.getDatetime());

                        codeList.add(codeDto);
                    }

                    /** 연재 요일 등록 */
                    if (dto.getWeekly() != null && !dto.getWeekly().isEmpty()) {
                        String[] weekly = dto.getWeekly().split(",");
                        for (String day : weekly) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("contentsIdx", dto.getIdx());
                            map.put("weeklyIdx", day.trim());
                            map.put("regdate", dateLibrary.getDatetime());

                            weeklyList.add(map);
                        }
                    }

                    /** 태그 등록 */
                    if (dto.getTagArr() != null && !dto.getTagArr().isEmpty()) {
                        String[] tags = dto.getTagArr().split(",");
                        for (String tag : tags) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("contentsIdx", dto.getIdx());
                            map.put("tagIdx", tag.trim());
                            map.put("regdate", dateLibrary.getDatetime());

                            tagList.add(map);
                        }
                    }

                    /**  작가 등록 */
                    // 글작가
                    if (dto.getWriter() != null && !dto.getWriter().isEmpty()) {
                        String[] writers = dto.getWriter().split(",");
                        for (String name : writers) {
                            AuthorDto authorDto = new AuthorDto();
                            authorDto.setRegdate(dateLibrary.getDatetime());
                            authorDto.setContentsIdx(dto.getIdx());
                            authorDto.setType(1); // type 1: 글작가, 2: 그림작가

                            // 작가 조회(작가 등록후 바로 조회 하는 경우가 있어서 main 에서 조회)
                            authorDto.setIdx(bulkDao.getAuthor(name.trim()));

                            // 작가 없으면 등록
                            if (authorDto.getIdx() == null) {
                                authorDto.setName(name.trim());
                                // 작가 등록
                                bulkDao.registerAuthor(authorDto);
                            }

                            authorList.add(authorDto);
                        }
                    }

                    // 그림 작가
                    if (dto.getIllustrator() != null && !dto.getIllustrator().isEmpty()) {
                        String[] illustrators = dto.getIllustrator().split(",");
                        for (String name : illustrators) {
                            AuthorDto authorDto = new AuthorDto();
                            authorDto.setRegdate(dateLibrary.getDatetime());
                            authorDto.setContentsIdx(dto.getIdx());
                            authorDto.setType(2); // type 1:글작가, 2: 그림작가

                            // 작가 조회(작가 등록후 바로 조회 하는 경우가 있어서 main 에서 조회)
                            authorDto.setIdx(bulkDao.getAuthor(name.trim()));

                            // 작가 없으면 등록
                            if (authorDto.getIdx() == null) {
                                authorDto.setName(name.trim());
                                // 작가 등록
                                bulkDao.registerAuthor(authorDto);
                            }

                            authorList.add(authorDto);
                        }
                    }
                }
                if (!codeList.isEmpty()) {
                    bulkDao.registerBulkCode(codeList);
                }
                if (!weeklyList.isEmpty()) {
                    bulkDao.registerBulkWeekly(weeklyList);
                }
                if (!tagList.isEmpty()) {
                    bulkDao.registerBulkTag(tagList);
                }
                if (!authorList.isEmpty()) {
                    bulkDao.registerBulkAuthor(authorList);
                }
            }
            fileInputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 작품 썸네일 대량 등록
     * @param multipartFile
     * @return
     */
    @Transactional
    public Integer registerContentsImage(MultipartFile multipartFile) {

        try {
            FileInputStream fileInputStream = (FileInputStream) multipartFile.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);

            int rowIndex = 0;
            //시트 수 (첫번째에만 존재하므로 0을 준다)
            //만약 각 시트를 읽기위해서는 FOR문을 한번더 돌려준다
            XSSFSheet sheet = workbook.getSheetAt(0);

            //행의 수
            int rows = sheet.getPhysicalNumberOfRows();

            List<ContentsDto> contentsList = new ArrayList<>();

            // 3번째 줄부터 data 시작
            for (rowIndex = 1; rowIndex < rows; rowIndex++) {

                //행을읽는다
                XSSFRow row = sheet.getRow(rowIndex);

                if (row != null && !cellToString(row.getCell(0)).equals("")) {
                    ContentsDto contentsDto = new ContentsDto();

                    String idx = cellToString(row.getCell(0));
                    if (idx != null && !idx.equals("")) {
                        contentsDto.setIdx(Integer.parseInt(idx));

                        // 중복 체크
                        Integer checkContentsImg = bulkDaoSub.getContentImg(contentsDto.getIdx());
                        if (checkContentsImg > 0) {
                            continue;
                        }

                    } else {
                        throw new Error("idx 가 없습니다.");
                    }

                    String categoryIdx = cellToString(row.getCell(7));
                    if (categoryIdx != null && !categoryIdx.equals("")) {
                        contentsDto.setCategoryIdx(Integer.parseInt(categoryIdx));
                    } else {
                        throw new Error("카테고리 idx 가 없습니다.");
                    }

                    // 이미지 파일
                    String path = contentsDto.getCategoryIdx().toString().concat("/") + contentsDto.getIdx().toString().concat("/thum");
                    File dir = new File(ftpPath + ftpCont.concat("/") + path);

                    if (dir.listFiles() != null) {

                        List<String> heightList = Stream.of(dir.listFiles()).filter(file -> file.getName().substring(0, file.getName().lastIndexOf(".")).equals("m")).map(File:: getName).toList();

                        if (!heightList.isEmpty()) {
                            String heightImage = dir + "/" + heightList.get(0);
                            contentsDto.setHeightImage(heightImage);
                        }

                        List<String> widthList = Stream.of(dir.listFiles()).filter(file -> file.getName().substring(0, file.getName().lastIndexOf(".")).equals("w")).map(File:: getName).toList();
                        if (!widthList.isEmpty()) {
                            String widthImage = dir + "/" + widthList.get(0);
                            contentsDto.setWidthImage(widthImage);
                        }

                        contentsList.add(contentsDto);
                    }
                }
            }


            /** 이미지 등록 */
            for (ContentsDto dto : contentsList) {

                // 등록일
                dto.setRegdate(dateLibrary.getDatetime());

                // s3에 저장될 path
                String s3Path = "kr/contents/" + dto.getIdx();

                /** 대표 이미지 원본 */
                String device = "origin";
                String type = "height";

                if (dto.getHeightImage() != null && !dto.getHeightImage().equals("")) {

                    // file to multipartFile
                    File file = new File(dto.getHeightImage());
                    FileItem fileItem = new DiskFileItem("file", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());

                    // webp 이미지 등록 안함(contentType - image/webp)
                    if (!fileItem.getContentType().contains("webp")) {
                        InputStream input = new FileInputStream(file);
                        OutputStream os = fileItem.getOutputStream();
                        IOUtils.copy(input, os);

                        List<MultipartFile> heightImage = new ArrayList<>();
                        heightImage.add(new CommonsMultipartFile(fileItem));

                        // s3 upload (원본)
                        List<HashMap<String,Object>> uploadResponse = s3Library.uploadFileNew(heightImage, s3Path);

                        uploadResponse = imageSize(uploadResponse, 180);

                        // db insert
                        registerImage(uploadResponse, dto.getIdx(), 0L, s3Path, device, type, new ArrayList<>(), "contents");

                        input.close();
                    } else {
                        pushAlarm(dto.getIdx() + " 작품 메인 썸네일(webp) 변경", "IMG");
                    }
                }


                /** 가로 이미지 원본 */
                device = "origin";
                type = "width";

                if (dto.getWidthImage() != null && !dto.getWidthImage().equals("")) {

                    // file to multipartFile
                    File file = new File(dto.getWidthImage());
                    FileItem fileItem = new DiskFileItem("file", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());

                    // webp 이미지 등록 안함(contentType - image/webp)
                    if (!fileItem.getContentType().contains("webp")) {
                        InputStream input = new FileInputStream(file);
                        OutputStream os = fileItem.getOutputStream();
                        IOUtils.copy(input, os);

                        List<MultipartFile> widthImage = new ArrayList<>();
                        widthImage.add(new CommonsMultipartFile(fileItem));

                        // s3 upload (원본)
                        List<HashMap<String,Object>> uploadResponse = s3Library.uploadFileNew(widthImage, s3Path);

                        uploadResponse = imageSize(uploadResponse, 720);

                        // db insert
                        registerImage(uploadResponse, dto.getIdx(), 0L, s3Path, device, type, new ArrayList<>(), "contents");

                        input.close();
                    } else {
                        pushAlarm(dto.getIdx() + " 작품 가로 썸네일(webp) 변경", "IMG");
                    }
                }
            }
            fileInputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }


    /**
     * 회차 대량 등록
     * @param multipartFile
     * @return
     */
    @Transactional
    public Integer registerBulkEpisode(MultipartFile multipartFile) {
        Integer result = null;

        // 작품 타이틀 목록 - 회차 타이틀 없을때(ex. 작품명_1화)
        List<ContentsDto> contentsList = bulkDaoSub.getListContentsTitle();
        Map<Integer, String> contentsMap = new HashMap<>();
        for (ContentsDto contentsDto : contentsList) {
            contentsMap.put(contentsDto.getIdx(), contentsDto.getTitle());
        }

        try {
            FileInputStream fileInputStream = (FileInputStream) multipartFile.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);

            int rowIndex = 0;
            //시트 수 (첫번째에만 존재하므로 0을 준다)
            //만약 각 시트를 읽기위해서는 FOR문을 한번더 돌려준다
            XSSFSheet sheet = workbook.getSheetAt(0);

            //행의 수
            int rows = sheet.getPhysicalNumberOfRows();

            List<EpisodeDto> episodeList = new ArrayList<>();

            // 3번째 줄부터 data 시작
            for (rowIndex = 1; rowIndex < rows; rowIndex++) {

                //행을읽는다
                XSSFRow row = sheet.getRow(rowIndex);

                if (row != null && !cellToString(row.getCell(0)).equals("")) {
                    EpisodeDto episodeDto = new EpisodeDto();

                    String idx = cellToString(row.getCell(0));
                    if (idx != null && !idx.equals("")) {
                        episodeDto.setIdx(Long.parseLong(idx));

                        // 중복 체크
                        Integer checkEpisode = bulkDaoSub.getEpisode(episodeDto.getIdx());
                        if (checkEpisode > 0) {
                            continue;
                        }

                    } else {
                        throw new Error( (rowIndex + 1) + "번째 줄의 idx 가 없습니다.");
                    }

                    String contentsIdx = cellToString(row.getCell(1));
                    if (contentsIdx != null && !contentsIdx.equals("")) {
                        episodeDto.setContentsIdx(Integer.parseInt(contentsIdx));
                    } else {
                        throw new Error( (rowIndex + 1) + "번째 줄의 작품 idx 가 없습니다.");
                    }

                    String episodeNumber = cellToString(row.getCell(4));
                    if (episodeNumber != null && !episodeNumber.equals("")) {
                        episodeDto.setEpisodeNumber(Integer.parseInt(episodeNumber));
                        episodeDto.setSort(Integer.parseInt(episodeNumber));
                    } else {
                        throw new Error( (rowIndex + 1) + "번째 줄의 회차 차수가 없습니다.");
                    }

                    String title = cellToString(row.getCell(3));
                    String publication = cellToString(row.getCell(5));
                    if (title != null && !title.equals("")) {
                        episodeDto.setTitle(title);
                        if (Integer.parseInt(publication) != 0) {
                            episodeDto.setLastEpisodeTitle(episodeNumber + "권");
                        } else {
                            episodeDto.setLastEpisodeTitle(episodeNumber + "화");
                        }
                    } else {
                        String contentsTitle = contentsMap.get(Integer.parseInt(contentsIdx)) + " " + episodeNumber;
                        if (Integer.parseInt(publication) != 0) {
                            episodeDto.setTitle(contentsTitle + "권");
                            episodeDto.setLastEpisodeTitle(episodeNumber + "권");
                        } else {
                            episodeDto.setTitle(contentsTitle + "화");
                            episodeDto.setLastEpisodeTitle(episodeNumber + "화");
                        }
                    }

                    String episodeTypeIdx = cellToString(row.getCell(6));
                    if (episodeTypeIdx != null && !episodeTypeIdx.equals("")) {
                        episodeDto.setEpisodeTypeIdx(Integer.parseInt(episodeTypeIdx));
                    } else {
                        episodeDto.setEpisodeTypeIdx(1);
                    }

                    String pubdate = cellToString(row.getCell(7));
                    if (pubdate != null && !pubdate.equals("")) {
                        episodeDto.setPubdate(dateLibrary.localTimeToUtc(pubdate));
                    } else {
                        episodeDto.setPubdate(dateLibrary.getDatetime());
                    }

                    String coinRent = cellToString(row.getCell(8));
                    if (coinRent!= null && !coinRent.equals("")) {
                        episodeDto.setCoinRent(Integer.parseInt(coinRent));
                    } else {
                        throw new Error( (rowIndex + 1) + "번째 줄의 대여가격이 없습니다.");
                    }

                    String coin = cellToString(row.getCell(9));
                    if (coin != null && !coin.equals("")) {
                        episodeDto.setCoin(Integer.parseInt(coin));
                    } else {
                        throw new Error( (rowIndex + 1) + "번째 줄의 소장가격이 없습니다.");
                    }

                    // 등록일
                    episodeDto.setRegdate(dateLibrary.getDatetime());

                    episodeList.add(episodeDto);
                }
            }

            if (!episodeList.isEmpty()) {
                List<EpisodeDto> lastEpisodeList = new ArrayList<>();
                int dtoIndex = 0;
                for(EpisodeDto dto : episodeList) {
                    // 작품 마지막 회차 목록
                    if (dtoIndex < episodeList.size() - 1) { // size는 갯수라 index 번호 맞추기 위해 -1 해줌
                        if (Integer.parseInt(dto.getContentsIdx().toString().trim()) !=  Integer.parseInt(episodeList.get(dtoIndex + 1).getContentsIdx().toString().trim()) ) {
                            lastEpisodeList.add(dto);
                        }
                    } else {
                        lastEpisodeList.add(dto);
                    }
                    dtoIndex++;
                }

                /** 회차 대량 등록 */
                result = bulkDao.registerBulkEpisode(episodeList);

                /** 회차 이벤트(할인) 등록 */
                bulkDao.registerBulkEpisodeEventCoin(episodeList);

                /** 회차 추가 정보 대량 등록 */
                bulkDao.registerBulkEpisodeInfo(episodeList);

                /** 작품 마지막 회차 정보 등록 */
                bulkDao.modifyLastEpisode(lastEpisodeList);
            }
            fileInputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 회차 썸네일 대량 등록
     * @param multipartFile
     * @return
     */
    @Transactional
    public Integer registerBulkEpisodeImage(MultipartFile multipartFile) {

        try {
            FileInputStream fileInputStream = (FileInputStream) multipartFile.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);

            int rowIndex = 0;
            //시트 수 (첫번째에만 존재하므로 0을 준다)
            //만약 각 시트를 읽기위해서는 FOR문을 한번더 돌려준다
            XSSFSheet sheet = workbook.getSheetAt(0);

            //행의 수
            int rows = sheet.getPhysicalNumberOfRows();

            List<EpisodeDto> episodeList = new ArrayList<>();

            // 3번째 줄부터 data 시작
            for (rowIndex = 1; rowIndex < rows; rowIndex++) {

                //행을읽는다
                XSSFRow row = sheet.getRow(rowIndex);

                if (row != null && !cellToString(row.getCell(0)).equals("")) {
                    EpisodeDto episodeDto = new EpisodeDto();

                    String idx = cellToString(row.getCell(0));
                    if (idx != null && !idx.equals("")) {
                        episodeDto.setIdx(Long.parseLong(idx));

                        // 중복 체크
                        Integer checkEpisodeImg = bulkDaoSub.getEpisodeImg(episodeDto.getIdx());
                        if (checkEpisodeImg > 0) {
                            continue;
                        }

                    } else {
                        throw new Error( (rowIndex + 1) + "번째 줄의 idx가 없습니다.");
                    }

                    String contentsIdx = cellToString(row.getCell(1));
                    if (contentsIdx != null && !contentsIdx.equals("")) {
                        episodeDto.setContentsIdx(Integer.parseInt(contentsIdx));
                    } else {
                        throw new Error( (rowIndex + 1) + "번째 줄의 작품idx가 없습니다.");
                    }

                    String categoryIdx = cellToString(row.getCell(2));
                    if (categoryIdx != null && !categoryIdx.equals("")) {
                        episodeDto.setCategoryIdx(Integer.parseInt(categoryIdx));
                    } else {
                        throw new Error( (rowIndex + 1) + "번째 줄의 카테고리가 없습니다.");
                    }

                    String episodeNumber = cellToString(row.getCell(4));
                    if (episodeNumber != null && !episodeNumber.equals("")) {
                        episodeDto.setEpisodeNumber(Integer.parseInt(episodeNumber));
                    } else {
                        throw new Error( (rowIndex + 1) + "번째 줄의 회차차수가 없습니다.");
                    }

                    // 이미지 파일
                    String path = episodeDto.getCategoryIdx().toString().concat("/") + episodeDto.getContentsIdx().toString().concat("/thum");
                    File dir = new File(ftpPath + ftpCont.concat("/") + path);

                    if (dir.listFiles() != null) {
                        List<String> widthList = Stream.of(dir.listFiles()).filter(file -> file.getName().substring(0, file.getName().lastIndexOf(".")).equals(episodeNumber)).map(File:: getName).toList();
                        if (!widthList.isEmpty()) {
                            String widthImage = dir + "/" + widthList.get(0);
                            episodeDto.setWidthImage(widthImage);

                            episodeList.add(episodeDto);
                        } else {
                            // 회차 썸네일 없을때

                            // contents width 썸네일 대체
                            List<String> contentsWidthList = Stream.of(dir.listFiles()).filter(file -> file.getName().substring(0, file.getName().lastIndexOf(".")).equals("w")).map(File:: getName).toList();
                            if (!contentsWidthList.isEmpty()) {
                                String widthImage = dir + "/" + contentsWidthList.get(0);
                                episodeDto.setWidthImage(widthImage);

                                episodeList.add(episodeDto);
                            } else {
                                // contents main(height) 썸네일 대체
                                List<String> contentsHeightList = Stream.of(dir.listFiles()).filter(file -> file.getName().substring(0, file.getName().lastIndexOf(".")).equals("m")).map(File:: getName).toList();

                                if (!contentsHeightList.isEmpty()) {
                                    String widthImage = dir + "/" + contentsHeightList.get(0);
                                    episodeDto.setWidthImage(widthImage);

                                    episodeList.add(episodeDto);
                                }
                            }
                        }
                    }
                }
            }

            /** 이미지 등록 */
            for (EpisodeDto dto : episodeList) {

                // 등록일
                dto.setRegdate(dateLibrary.getDatetime());

                // s3에 저장될 path
                String s3Path = "kr/contents/"+ dto.getContentsIdx() + "/episode/" + dto.getIdx();

                /** 가로 이미지 원본 */
                String device = "origin";
                String type = "width";

                if (dto.getWidthImage() != null && !dto.getWidthImage().equals("")) {

                    // file to multipartFile
                    File file = new File(dto.getWidthImage());
                    FileItem fileItem = new DiskFileItem("file", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());

                    // webp 이미지 등록 안함(contentType - image/webp)
                    if (!fileItem.getContentType().contains("webp")) {
                        InputStream input = new FileInputStream(file);
                        OutputStream os = fileItem.getOutputStream();
                        IOUtils.copy(input, os);

                        List<MultipartFile> widthImage = new ArrayList<>();
                        widthImage.add(new CommonsMultipartFile(fileItem));

                        // s3 upload (원본)
                        List<HashMap<String,Object>> uploadResponse = s3Library.uploadFileNew(widthImage, s3Path);

                        uploadResponse = imageSize(uploadResponse, 104);

                        // db insert
                        registerImage(uploadResponse, 0, dto.getIdx(), s3Path, device, type, new ArrayList<>(), "episode");

                        input.close();
                    } else {
                        pushAlarm(dto.getIdx() + " 회차 가로 썸네일(webp) 변경", "IMG");
                    }
                }
            }
            fileInputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 뷰어(내용 이미지) 대량 등록
     * @param multipartFile
     * @return
     */
    @Transactional
    public Integer registerBulkViewer(MultipartFile multipartFile) {

        pushAlarm(multipartFile.getOriginalFilename() + " - Start", "IMG");

        List<EpisodeDto> episodeList = new ArrayList<>();

        try {
            FileInputStream fileInputStream = (FileInputStream) multipartFile.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);

            int rowIndex = 0;
            //시트 수 (첫번째에만 존재하므로 0을 준다)
            //만약 각 시트를 읽기위해서는 FOR문을 한번더 돌려준다
            XSSFSheet sheet = workbook.getSheetAt(0);

            //행의 수
            int rows = sheet.getPhysicalNumberOfRows();

            // 3번째 줄부터 data 시작
            for (rowIndex = 1; rowIndex < rows; rowIndex++) {

                //행을읽는다
                XSSFRow row = sheet.getRow(rowIndex);

                if (row != null && !cellToString(row.getCell(0)).equals("")) {

                    EpisodeDto episodeDto = new EpisodeDto();

                    String idx = cellToString(row.getCell(0));
                    if (idx != null && !idx.equals("")) { episodeDto.setIdx(Long.parseLong(idx)); }

                    String contentsIdx = cellToString(row.getCell(1));
                    if (contentsIdx != null && !contentsIdx.equals("")) { episodeDto.setContentsIdx(Integer.parseInt(contentsIdx)); }

                    String categoryIdx = cellToString(row.getCell(2));
                    if (categoryIdx != null && !categoryIdx.equals("")) { episodeDto.setCategoryIdx(Integer.parseInt(categoryIdx)); }

                    String episodeNumber = cellToString(row.getCell(4));
                    if (episodeNumber != null && !episodeNumber.equals("")) { episodeDto.setEpisodeNumber(Integer.parseInt(episodeNumber)); }

                    /* 파일명 체크 ex) 1.zip, 01.zip, 001.zip */
                    List<String> fileNameList = new ArrayList<>();
                    if (episodeDto.getCategoryIdx() == 1 || episodeDto.getCategoryIdx() == 2) {
                        String fileName = episodeNumber.concat(".zip");
                        String twoDigits = String.format("%02d", Integer.parseInt(episodeNumber)).concat(".zip");
                        String threeDigits = String.format("%03d", Integer.parseInt(episodeNumber)).concat(".zip");

                        fileNameList.add(episodeNumber.concat(".zip"));
                        if (!fileName.equals(twoDigits)) {
                            fileNameList.add(twoDigits);
                        }
                        if (!fileName.equals(threeDigits)) {
                            fileNameList.add(threeDigits);
                        }
                    } else if (episodeDto.getCategoryIdx() == 3) {
                        String fileName = episodeNumber.concat(".epub");
                        String twoDigits = String.format("%02d", Integer.parseInt(episodeNumber)).concat(".epub");
                        String threeDigits = String.format("%03d", Integer.parseInt(episodeNumber)).concat(".epub");

                        fileNameList.add(episodeNumber.concat(".epub"));
                        if (!fileName.equals(twoDigits)) {
                            fileNameList.add(twoDigits);
                        }
                        if (!fileName.equals(threeDigits)) {
                            fileNameList.add(threeDigits);
                        }
                    }

                    // 파일이 1개 인지 확인
                    List<File> checkFile = new ArrayList<>();
                    for (String name : fileNameList) {
                        File file = new File(ftpPath + ftpCont.concat("/") + categoryIdx.concat("/") + contentsIdx.concat("/cont"), name);
                        // 등록 가능한 file size 10kb 이상 파일만 저장(잘못 등록된 파일 확인용)
                        if (file != null && file.isFile() && file.length() > 10000) {
                            checkFile.add(file);
                        }
                    }

                    // file 이 1개 일때
                    if (checkFile.size() == 1) {
                        // viewer 조회(중복 체크) - 느릴 경우 제거
                        Integer cnt = bulkDaoSub.getCheckViewer(episodeDto);
                        if (cnt < 1) {
                            episodeDto.setFile(checkFile.get(0));
                            episodeList.add(episodeDto);
                        }
                    } else if (checkFile.size() > 1) {
                        // file 이 2개 이상(중복)
                        pushAlarm("파일 중복 : " + multipartFile.getOriginalFilename() + " - " + checkFile.get(0).getPath(), "IMG");
                    } else {
                        // file 이 없음
                        String path = categoryIdx + "/" + contentsIdx + "/" +  idx;
                        pushAlarm("파일 없음 : " + multipartFile.getOriginalFilename() + " - " + path, "IMG");
                    }
                }
            }

            pushAlarm("excel end - " + multipartFile.getOriginalFilename() + " - " + episodeList.size() + " 등록할 회차 수", "IMG");

            int imageCount = 1;

            List<Map<String, Object>> listNovel = new ArrayList<>();
            for (EpisodeDto episodeDto : episodeList) {
                ZipFile zipFile = new ZipFile(episodeDto.getFile(), "UTF-8");

                // outputStream 보다 빠름
                BufferedOutputStream fileOutputStream = null;

                // 이미지
                List<MultipartFile> viewerImage = new ArrayList<>();

                // zip 파일 압축 해제 리스트
                Enumeration<? extends ZipArchiveEntry> entries = zipFile.getEntries();

                Map<String, Object> coverMap = new HashMap<>();
                // xhtml(소설) 내용
                List<Map<String, Object>> viewerData = new ArrayList<>();
                // xhtml(소설) 목차
                Map<String, Object> tocMap = new HashMap<>();
                // xhtml(소설) 카피라이트
                Map<String, Object> copyrightMap = new HashMap<>();

                // 이미지 fileName, s3 fullUrl
                Map<String, Object> imageList = new HashMap<>();


                // 순서 재정렬
                List<ZipArchiveEntry> zipEntryList = sortLibrary.entrySort(entries);

                for (ZipArchiveEntry entry : zipEntryList) {
                    String unzipPath = ftpPath + "/unzip/" + episodeDto.getCategoryIdx().toString().concat("/") + episodeDto.getContentsIdx().toString().concat("/") + episodeDto.getIdx();

                    if (entry.isDirectory()) {
                        // 파일이 폴더 안에 있을때
                        File unzipFile = new File(unzipPath, entry.getName());
                        unzipFile.mkdirs();

                    } else {
                        String fileName = entry.getName();

                        // 소설 아닐때
                        if (episodeDto.getCategoryIdx() != 3) {
                            // 확장자
                            String extension = entry.getName().substring(entry.getName().lastIndexOf("."));

                            // file name
                            String sReplace = entry.getName().replaceAll("[^\\d]", ",");
                            String[] sSplit = sReplace.split(",");
                            StringBuilder stringBuilder = new StringBuilder();
                            for (String s : sSplit) {
                                if (!s.isEmpty()) {
                                    stringBuilder.append(String.format("%04d", Integer.parseInt(s)));
                                }
                            }
                            fileName = stringBuilder.toString().concat(extension);
                        }

                        // 파일만 있을때
                        File unzipFile = new File(unzipPath, fileName);
                        fileExists(unzipFile);

                        InputStream stream = zipFile.getInputStream(entry);
                        BufferedInputStream bfStream = new BufferedInputStream(stream);

                        fileOutputStream = new BufferedOutputStream(new FileOutputStream(unzipFile));

                        // 압축 해제된 파일 읽기
                        int length = 0;
                        while ((length = bfStream.read()) != -1) {
                            fileOutputStream.write(length);
                        }
                        fileOutputStream.close();

                        InputStream input = new FileInputStream(unzipFile);

                        // contentType
                        String entryContentType = new MimetypesFileTypeMap().getContentType(fileName);

                        // 이미지 일때
                        if (entryContentType.substring(0, entryContentType.lastIndexOf("/")).equals("image")) {

                            FileItem fileItem = new DiskFileItem("file", Files.probeContentType(unzipFile.toPath()), false, unzipFile.getName(), (int) unzipFile.length(), unzipFile.getParentFile());
                            OutputStream os = fileItem.getOutputStream();
                            IOUtils.copy(input, os);

                            viewerImage.add(new CommonsMultipartFile(fileItem));

                            os.close();

                            imageCount++;
                        }
                        // 확장자가 xhtml, html 일때(소설)
                        else if (fileName.substring(fileName.lastIndexOf(".") + 1).equals("xhtml") || fileName.substring(fileName.lastIndexOf(".") + 1).equals("html")) {

                            Map<String, Object> map = new HashMap<>();

                            InputStreamReader inputStreamReader = new InputStreamReader(input);

                            BufferedReader br = new BufferedReader(inputStreamReader);
                            String html = "";
                            Boolean flag = Boolean.FALSE;
                            String s = "";
                            while((s = br.readLine()) != null) {

                                // body 태그 부터 저장
                                if (s.toLowerCase().indexOf("<body") >= 0) {
                                    flag = Boolean.TRUE;
                                    continue;
                                }

                                // body 닫는 곳 까지만 저장
                                if (s.toLowerCase().indexOf("</body>") >= 0) {
                                    break;
                                }

                                // a 태그(블로그, 메일 등)
                                if (s.indexOf("<a ") >= 0) {
                                    s = "<div>" + Jsoup.parse(s).text() + "</div>";
                                }

                                // css 제외
                                if (s.toLowerCase().indexOf(".css") >= 0) {
                                    continue;
                                }

                                // flag true 이면 html 추가
                                if (Boolean.TRUE.equals(flag)) {
                                    html += s;
                                }
                            }

                            br.close();
                            inputStreamReader.close();

                            if (!html.equals("")) {
                                map.put("detail", html);
                                map.put("regdate", dateLibrary.getDatetime());
                                map.put("episodeIdx", episodeDto.getIdx());

                                if (fileName.toLowerCase().equals("cover.xhtml") || fileName.toLowerCase().equals("cover.html")) {
                                    // cover image
                                    coverMap.putAll(map);
                                } else if (fileName.toLowerCase().equals("cr.xhtml") || fileName.toLowerCase().equals("cr.html") || fileName.toLowerCase().equals("copyright.xhtml") || fileName.toLowerCase().equals("copyright.html")) {
                                    // copyright 순서 마지막(map 에 따로 담아둠)
                                    copyrightMap.putAll(map);
                                } else if (fileName.toLowerCase().equals("toc.xhtml") || fileName.toLowerCase().equals("toc.html")) {
                                    // 목차 순서 처음(map 에 따로 담아둠)
                                    tocMap.putAll(map);
                                } else {
                                    viewerData.add(map);
                                }
                            }
                        }

                        input.close();
                    }
                }

                zipFile.close();

                /** s3 upload (이미지일 때만) */
                // s3에 저장될 path
                String s3Path = "kr/contents/" + episodeDto.getContentsIdx() + "/episode/" + episodeDto.getIdx() + "/upload";

                String device = "origin";
                String type = null;

                String position = "viewer"+episodeDto.getCategoryIdx().toString();

                /** 이미지 원본 */
                // 파일 있는지 체크
                Boolean chkHeightImage = chkFile(viewerImage);

                if (chkHeightImage) {
                    // s3 upload (원본)
                    List<HashMap<String, Object>> uploadResponse = s3Library.uploadFileNew(viewerImage, s3Path);

                    uploadResponse = imageSize(uploadResponse, 720);

                    // db insert
                    registerImage(uploadResponse, 0, episodeDto.getIdx(), s3Path, device, type, new ArrayList<>(), position);

                    if (episodeDto.getCategoryIdx() == 3) {
                        // 이미지 정보 배열에 저장(소설 이미지 변경에 사용)
                        for (HashMap<String, Object> uploadMap : uploadResponse) {
                            // 소설 이미지는 원본
                            String fullUrl = s3Library.getUploadedFullUrl(uploadMap.get("fileUrl").toString());
                            imageList.put(uploadMap.get("orgFileName").toString(), fullUrl);
                        }
                    }
                }

                // 목차 첫번째
                if (!tocMap.isEmpty()) {
                    viewerData.add(0, tocMap);
                }

                // cover image(있을경우 첫번째, 목차 두번째)
                if (!coverMap.isEmpty()) {
                    viewerData.add(0, coverMap);
                }

                // copyright 마지막 순서
                if (!copyrightMap.isEmpty()) {
                    viewerData.add(copyrightMap);
                }

                if (!viewerData.isEmpty()) {
                    // 이미지 replace
                    for (Map<String, Object> data : viewerData) {
                        String detail = data.get("detail").toString();
                        String imgReg = "(<img[^>]*src=[\\\"']?([^>\\\"']+)[\\\"']?[^>]*>)";
                        Pattern pattern = Pattern.compile(imgReg);
                        Matcher matcher = pattern.matcher(data.get("detail").toString());

                        while(matcher.find()) {
                            String fullUrl = "";

                            String src = matcher.group(2).trim();
                            Object objUrl = src.substring(src.lastIndexOf("/")+1);
                            if (objUrl != null) {
                                // 파일명이 같은 이미지가 있는지 확인
                                if (imageList.get(objUrl.toString()) != null) {
                                    fullUrl = imageList.get(objUrl.toString()).toString();
                                } else {
                                    // 이미지가 없으면 파일명 decode 해서 확인
                                    if (imageList.get(URLDecoder.decode(objUrl.toString())) != null) {
                                        fullUrl = imageList.get(URLDecoder.decode(objUrl.toString())).toString();
                                    }
                                }
                            }

                            data.put("detail", detail.replace(src, fullUrl));
                        }

                        String xhtmlImgReg = "(<image[^>]*href=[\\\"']?([^>\\\"']+)[\\\"']?[^>]*>)";
                        Pattern xhtmlPattern = Pattern.compile(xhtmlImgReg);
                        Matcher xhtmlMatcher = xhtmlPattern.matcher(data.get("detail").toString());
                        while(xhtmlMatcher.find()) {
                            String fullUrl = "";

                            String src = matcher.group(2).trim();
                            Object objUrl = src.substring(src.lastIndexOf("/")+1);
                            if (objUrl != null) {
                                // 파일명이 같은 이미지가 있는지 확인
                                if (imageList.get(objUrl.toString()) != null) {
                                    fullUrl = imageList.get(objUrl.toString()).toString();
                                } else {
                                    // 이미지가 없으면 파일명 decode 해서 확인
                                    if (imageList.get(URLDecoder.decode(objUrl.toString())) != null) {
                                        fullUrl = imageList.get(URLDecoder.decode(objUrl.toString())).toString();
                                    }
                                }
                            }

                            data.put("detail", detail.replace(src, fullUrl));
                        }
                    }

                    int sort = 1;
                    for (Map<String, Object> map : viewerData) {
                        map.put("sort", sort);
                        sort++;

                        listNovel.add(map);
                    }
                }

                // 100번째 회차 마다 알람
                if (episodeDto.getIdx() % 50 == 0) {
                    pushAlarm(multipartFile.getOriginalFilename() + " - " + episodeDto.getIdx() + "번 회차 등록중", "IMG");
                }
            }
            if (!listNovel.isEmpty()) {
                bulkDao.registerBulkViewerNovel(listNovel);
            }
            fileInputStream.close();

            pushAlarm(multipartFile.getOriginalFilename() + " - " + imageCount + "건 Image Register End", "IMG");

        } catch (IOException ioe) {
            pushAlarm(multipartFile.getOriginalFilename() + " (IOException)- " + ioe, "IMG");
            log.error("error", ioe);
        } catch (Exception e) {
            pushAlarm(multipartFile.getOriginalFilename() + " (Exception)- " + e.toString(), "IMG");
            log.error("error", e);
        } finally {
            // TODO unzip 삭제는 따로 추가 예정 (여러 파일 진행시 등록중 삭제 진행 확인됨)
//            deleteFile(ftpPath + "/unzip");
        }
        return 1;
    }

    /**
     * 이미지 등록
     * @param uploadResponse
     * @param contentsIdx
     * @param episodeIdx
     * @param s3Path
     * @param device
     * @param type
     * @param parents
     * @return
     */
    @Transactional
    public List<Integer> registerImage(List<HashMap<String,Object>> uploadResponse, Integer contentsIdx, Long episodeIdx, String s3Path, String device, String type, List<Integer> parents, String position) {

        Object idx = null;
        if (contentsIdx != 0) {
            idx = contentsIdx;
        }

        if (episodeIdx != 0) {
            idx = episodeIdx;
        }

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

        if (position.equals("contents")) {
            // 작품 썸네일
            bulkDao.registerBulkContentsImage(uploadResponse);
        } else if (position.equals("episode")) {
            // 회차 썸네일
            bulkDao.registerBulkEpisodeImage(uploadResponse);
        } else if (position.equals("viewer1")) {
            // 뷰어 webtoon
            bulkDao.registerBulkWebtoonImage(uploadResponse);
        } else if (position.equals("viewer2")) {
            // 뷰어 comic
            bulkDao.registerBulkComicImage(uploadResponse);
        } else if (position.equals("viewer3")) {
            // 뷰어 novel
            bulkDao.registerBulkNovelImage(uploadResponse);
        }

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

    /*****************************************************
     *  SubFunction - Etc
     ****************************************************/

    /**
     * excel 셀 값 string 변환
     * @param cell
     * @return
     */
    private String cellToString(XSSFCell cell) {
        String value = "";
        if (cell != null) {
            // 셀 값 String 변환
            cell.setCellType(CellType.STRING);
            value = cell.getStringCellValue().replaceAll("\u00A0", " ").trim();
            if (value.equals("Y")) {
                value = "1";
            } else if (value.equals("N")) {
                value = "0";
            }
        }

        return value;
    }

    /**
     * upload 폴더가 없으면 생성
     * @param file
     */
    public void fileExists(File file) {
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
    }

    /**
     * 파일 check(비어 있는지 확인)
     * @param fileList
     * @return
     */
    public Boolean chkFile(List<MultipartFile> fileList) {
        Boolean flag = true;
        for(MultipartFile image : fileList){
            if(image.isEmpty()) {
                flag = false;
                break;
            }
        }

        return flag;
    }

    /**
     * 압축 해제 임시 폴더 삭제
     * @param path
     */
    public void deleteFile(String path) {
        File deleteFolder = new File(path);
        if (deleteFolder.exists()) {
            File[] deleteFolderList = deleteFolder.listFiles();

            for (int i = 0; i < deleteFolderList.length; i++) {
                if (deleteFolderList[i].isFile()) {
                    deleteFolderList[i].delete();
                } else {
                    deleteFile(deleteFolderList[i].getPath());
                }
                deleteFolderList[i].delete();
            }
            deleteFolder.delete();
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

    /**************************************************************/

    /**
     * 회차 목록
     * @return
     */
    @Transactional(readOnly = true)
    public List<EpisodeDto> getListEpisodeAll() {
        // 회차 목록
        List<EpisodeDto> list = bulkDaoSub.getListEpisodeAll();

        // 이미지 fullUrl
        for(EpisodeDto episodeDto : list) {
            for(ImageDto imageDto : episodeDto.getImageList()) {

                imageDto.setUrl(thumborLibrary.getUploadedFullUrl(imageDto.getUrl()));
            }
        }

        return list;
    }

    /**
     * 컨텐츠 목록
     * @return
     */
    @Transactional(readOnly = true)
    public List<ContentsDto> getListContentsAll() {
        // 컨텐츠 목록
        List<ContentsDto> list = bulkDaoSub.getListContentsAll();

        // 이미지 fullUrl
        for(ContentsDto contentsDto : list) {
            for(ImageDto imageDto : contentsDto.getImageList()) {
                imageDto.setUrl(thumborLibrary.getUploadedFullUrl(imageDto.getUrl()));
                imageDto.setUrl(imageDto.getUrl());
            }
        }
        return list;
    }

}

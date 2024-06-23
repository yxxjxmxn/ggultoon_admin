package com.architecture.admin.services.contents;

import com.architecture.admin.libraries.S3Library;
import com.architecture.admin.libraries.SortLibrary;
import com.architecture.admin.libraries.ThumborLibrary;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.contents.ViewerDao;
import com.architecture.admin.models.daosub.contents.ViewerDaoSub;
import com.architecture.admin.models.dto.contents.EpisodeDto;
import com.architecture.admin.models.dto.contents.ImageDto;
import com.architecture.admin.models.dto.contents.ViewerDto;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.log.AdminActionLogService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
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
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
@Transactional
public class ViewerService extends BaseService {

    private final ViewerDao viewerDao;
    private final ViewerDaoSub viewerDaoSub;
    private final S3Library s3Library;
    private final ThumborLibrary thumborLibrary;
    private final SortLibrary sortLibrary;
    private final AdminActionLogService adminActionLogService;// 관리자 action log 처리용


    // TODO 파일 경로 디렉토리 없으면 생성 추가해야함
    @Value("${spring.servlet.multipart.location}")
    private String uploadPath;

    // mac에서 압축 해제시 __MACOSX폴더 생성됨
    // 파일의 확장 정보를 기록하는 리소스 포크가 저장되는 폴더
    private final String MAC_OS_X = "__MACOSX";


    /**
     * 뷰어 웹툰
     * @param idx
     * @return
     */
    @Transactional(readOnly = true)
    public List<ImageDto> getViewerWebtoon(Integer idx) {
        if (idx == null || idx < 1) {
            throw new CustomException(CustomError.IDX_EPISODE_ERROR);
        }

        // 뷰어 웹툰
        List<ImageDto> images = viewerDaoSub.getViewerWebtoon(idx);

        // 이미지 fullUrl
        images = thumborLibrary.getImageCFUrl(images);

        return images;
    }

    /**
     * 뷰어 만화
     * @param idx
     * @return
     */
    @Transactional(readOnly = true)
    public List<ImageDto> getViewerComic(Integer idx) {

        // idx validation
        if (idx == null || idx < 1) {
            throw new CustomException(CustomError.IDX_EPISODE_ERROR);
        }

        // 뷰어 만화
        List<ImageDto> images = viewerDaoSub.getViewerComic(idx);

        // 이미지 fullUrl
        images = thumborLibrary.getImageCFUrl(images);

        return images;
    }


    /**
     * 뷰어 소설
     * @param idx
     * @return
     */
    @Transactional(readOnly = true)
    public List<ViewerDto> getViewerNovel(Integer idx) {

        // idx validation
        if (idx == null || idx < 1) {
            throw new CustomException(CustomError.IDX_EPISODE_ERROR);
        }
        return viewerDaoSub.getViewerNovel(idx);
    }

    /**
     * 뷰어 소설 이미지
     * @param idx
     * @return
     */
    @Transactional(readOnly = true)
    public List<ImageDto> getViewerNovelImg(Integer idx) {

        // idx validation
        if (idx == null || idx < 1) {
            throw new CustomException(CustomError.IDX_EPISODE_ERROR);
        }

        // 뷰어 소설 이미지
        List<ImageDto> images = viewerDaoSub.getViewerNovelImg(idx);

        // 이미지 fullUrl
        images = thumborLibrary.getImageCFUrl(images);

        return images;
    }

    /**
     * viewer webtoon 등록
     * @param episodeDto
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    @Transactional
    public Integer webtoon(EpisodeDto episodeDto) throws IllegalStateException {

        Integer result = null;
        /** zip 압축 해제후 디렉토리 저장 */
        // 압축 파일
        MultipartFile episodeZipFile = episodeDto.getZipFile();

        // outputStream 보다 빠름
        BufferedOutputStream fileOutputStream = null;

        List<MultipartFile> viewerImage = new ArrayList<>();

        String contentType = new MimetypesFileTypeMap().getContentType(episodeZipFile.getOriginalFilename());

        // 이미지 등록
        if (contentType.substring(0, contentType.lastIndexOf("/")).equals("image")) {
            viewerImage.add(episodeZipFile);

            // 뷰어 마지막 sort 번호
            Integer sort = viewerDaoSub.getViewerWebtoonLastSort(episodeDto.getIdx());

            /** s3 upload (이미지일 때만) */
            // s3에 저장될 path
            String s3Path = "kr/contents/" + episodeDto.getContentsIdx() + "/episode/" + episodeDto.getIdx() + "/upload";

            /** 이미지 원본 */
            String device = "origin";
            String type = "webtoon";

            // 파일 있는지 체크
            // s3 upload (원본)
            List<HashMap<String, Object>> uploadResponse = s3Library.uploadFileNew(viewerImage, s3Path);

            uploadResponse = imageSize(uploadResponse, 720);

            // db insert(registerImage 에서 type 으로 구분 webtoon, novel, comic)
            registerImageOne(uploadResponse, episodeDto.getIdx(), s3Path, device, type, new ArrayList<>(), sort+1);

            // 결과
            result = 1;

        }
        // zip  등록
        else if (episodeZipFile.getOriginalFilename() != null && !episodeZipFile.getOriginalFilename().equals("")) {
            try (
                    FileInputStream fileInputStream = new FileInputStream(multipartToFile(episodeZipFile));
                    ZipFile zipFile = new ZipFile(uploadPath + "/zip/" + episodeZipFile.getOriginalFilename());
            ) {
                // zip 파일 압축 해제 리스트
                Enumeration<? extends ZipArchiveEntry> entries = zipFile.getEntries();

                // 순서 재정렬
                List<ZipArchiveEntry> zipEntryList = sortLibrary.entrySort(entries);

                for (ZipArchiveEntry entry : zipEntryList) {
                    String unzipPath = uploadPath + "/unzip/" + episodeDto.getIdx();

                    if (entry.isDirectory()) {
                        // 파일이 폴더 안에 있을때
                        File unzipFile = new File(unzipPath, entry.getName());
                        unzipFile.mkdirs();

                    } else {
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
                        String fileName = stringBuilder.toString().concat(extension);

                        // 파일만 있을때
                        File unzipFile = new File(unzipPath, fileName);
                        fileExists(unzipFile);

                        // contentType
                        String entryContentType = new MimetypesFileTypeMap().getContentType(fileName);

                        // 이미지 일때
                        if (entryContentType.substring(0, entryContentType.lastIndexOf("/")).equals("image")) {
                            InputStream stream = zipFile.getInputStream(entry);
                            BufferedInputStream bfStream = new BufferedInputStream(stream);

                            fileOutputStream = new BufferedOutputStream(new FileOutputStream(unzipFile));

                            // 압축 해제된 파일 읽기
                            int length = 0;
                            while ((length = bfStream.read()) != -1) {
                                fileOutputStream.write(length);
                            }
                            fileOutputStream.close();
                            fileOutputStream.flush();

                            FileItem fileItem = new DiskFileItem("file", Files.probeContentType(unzipFile.toPath()), false, unzipFile.getName(), (int) unzipFile.length(), unzipFile.getParentFile());

                            InputStream input = new FileInputStream(unzipFile);
                            OutputStream os = fileItem.getOutputStream();
                            IOUtils.copy(input, os);

                            viewerImage.add(new CommonsMultipartFile(fileItem));
                        }
                    }
                }
                // close
                fileInputStream.close();
                zipFile.close();
                /** //zip 압축 해제후 디렉토리 저장 */


                /** s3 upload (이미지일 때만) */
                // s3에 저장될 path
                String s3Path = "kr/contents/" + episodeDto.getContentsIdx() + "/episode/" + episodeDto.getIdx() + "/upload";

                /** 이미지 원본 */
                String device = "origin";
                String type = "webtoon";

                // 파일 있는지 체크
                Boolean chkViewerImage = chkIsEmptyImage(viewerImage);
                if (Boolean.TRUE.equals(chkViewerImage)) {
                    // s3 upload (원본)
                    List<HashMap<String, Object>> uploadResponse = s3Library.uploadFileNew(viewerImage, s3Path);

                    uploadResponse = imageSize(uploadResponse, 720);

                    // db insert(registerImage 에서 type 으로 구분 webtoon, novel, comic)
                    registerImage(uploadResponse, episodeDto.getIdx(), s3Path, device, type, new ArrayList<>());

                }

                // 결과
                result = 1;

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 압축 해제 파일 삭제
                deleteFile(uploadPath + "/unzip");
                // 압축 파일 삭제
                deleteFile(uploadPath + "/zip");
            }
        } else {
            // 결과
            result = 0;
        }
        return result;
    }

    /**
     * viewer 소설 등록
     * @param episodeDto
     * @return
     * @throws IllegalStateException
     */
    @Transactional
    public Integer novel(EpisodeDto episodeDto) throws IllegalStateException {

        Integer result = null;

        /** zip(epub) 압축 해제후 디렉토리 저장 */
        // 압축 파일
        MultipartFile episodeZipFile = episodeDto.getZipFile();

        // outputStream 보다 빠름
        BufferedOutputStream fileOutputStream = null;

        List<Map<String, Object>> viewerData = new ArrayList<>();

        String contentType = new MimetypesFileTypeMap().getContentType(episodeZipFile.getOriginalFilename());

        // 이미지 등록
        if (contentType.substring(0, contentType.lastIndexOf("/")).equals("image")) {

            List<MultipartFile> viewerImage = new ArrayList<>();

            viewerImage.add(episodeZipFile);

            /** s3 upload (이미지일 때만) */
            // s3에 저장될 path
            String s3Path = "kr/contents/"+ episodeDto.getContentsIdx() + "/episode/" + episodeDto.getIdx()+"/upload";

            String device = "origin";
            String type = "novel";

            /** 이미지 원본 */
            // 파일 있는지 체크
            Boolean chkViewerImage = chkIsEmptyImage(viewerImage);
            if (Boolean.TRUE.equals(chkViewerImage)) {
                // s3 upload (원본)
                List<HashMap<String,Object>> uploadResponse = s3Library.uploadFileNew(viewerImage, s3Path);

                uploadResponse = imageSize(uploadResponse, 720);

                // db insert(registerImage 에서 type 으로 구분 webtoon, novel, comic)
                registerImageOne(uploadResponse, episodeDto.getIdx(), s3Path, device, type, new ArrayList<>(), 1);
            }

            // 결과
            result = 1;

        } else if (episodeZipFile.getOriginalFilename() != null && !episodeZipFile.getOriginalFilename().equals("")) {
            try (
                    FileInputStream fileInputStream = new FileInputStream(multipartToFile(episodeZipFile));
                    ZipFile zipFile = new ZipFile(uploadPath + "/zip/" + episodeZipFile.getOriginalFilename());
            ) {
                // zip 파일 압축 해제 리스트
                Enumeration<? extends ZipArchiveEntry> entries = zipFile.getEntries();

                Map<String, Object> coverMap = new HashMap<>();
                Map<String, Object> tocMap = new HashMap<>();
                Map<String, Object> copyrightMap = new HashMap<>();


                // 이미지 fileName, s3 fullUrl
                Map<String, Object> imageList = new HashMap<>();

                // epub 파일 리스트 목록 순환
                while (entries.hasMoreElements()) {
                    ZipArchiveEntry epubEntry = entries.nextElement();

                    if (epubEntry.getName().startsWith(MAC_OS_X)) {
                        continue;
                    }

                    // epub contentType
                    String epubEntryContentType = new MimetypesFileTypeMap().getContentType(epubEntry.getName());

                    // 이미지 일때(epub 이미지)
                    if (epubEntryContentType.substring(0, epubEntryContentType.lastIndexOf("/")).equals("image")) {
                        File unzipFile = new File(uploadPath + "/unzip/" + episodeDto.getIdx(), epubEntry.getName());

                        if (epubEntry.isDirectory()) {
                            unzipFile.mkdirs();
                        } else {
                            // 파일만 있을때
                            fileExists(unzipFile);

                            InputStream stream = zipFile.getInputStream(epubEntry);
                            BufferedInputStream bfStream = new BufferedInputStream(stream);

                            fileOutputStream = new BufferedOutputStream(new FileOutputStream(unzipFile));

                            // 압축 해제된 파일 읽기
                            int length = 0;
                            while ((length = bfStream.read()) != -1) {
                                fileOutputStream.write(length);
                            }
                            fileOutputStream.close();
                            fileOutputStream.flush();

                            FileItem fileItem = new DiskFileItem("file", Files.probeContentType(unzipFile.toPath()), false, unzipFile.getName(), (int) unzipFile.length() , unzipFile.getParentFile());

                            InputStream input = new FileInputStream(unzipFile);
                            OutputStream os = fileItem.getOutputStream();
                            IOUtils.copy(input, os);

                            List<MultipartFile> viewerImage = new ArrayList<>();

                            viewerImage.add(new CommonsMultipartFile(fileItem));

                            /** s3 upload (이미지일 때만) */
                            // s3에 저장될 path
                            String s3Path = "kr/contents/"+ episodeDto.getContentsIdx() + "/episode/" + episodeDto.getIdx()+"/upload";

                            String device = "origin";
                            String type = "novel";

                            /** 이미지 원본 */
                            // 파일 있는지 체크
                            Boolean chkViewerImage = chkIsEmptyImage(viewerImage);
                            if (Boolean.TRUE.equals(chkViewerImage)) {
                                // s3 upload (원본)
                                List<HashMap<String,Object>> uploadResponse = s3Library.uploadFileNew(viewerImage, s3Path);

                                uploadResponse = imageSize(uploadResponse, 720);

                                // db insert(registerImage 에서 type 으로 구분 webtoon, novel, comic)
                                registerImage(uploadResponse, episodeDto.getIdx(), s3Path, device, type, new ArrayList<>());

                                for (HashMap<String, Object> uploadMap : uploadResponse) {
                                    // 소설 이미지는 원본
                                    String fullUrl = s3Library.getUploadedFullUrl(uploadMap.get("fileUrl").toString());
                                    imageList.put(uploadMap.get("orgFileName").toString(), fullUrl);
                                }
                            }
                        }
                    }
                    // CSS file 이 필요할수도 있음

                    // xhtml 일때
                    if (epubEntry.getName().substring(epubEntry.getName().lastIndexOf(".") + 1).equalsIgnoreCase("xhtml")) {

                        String filePath = uploadPath + "/unzip/" + episodeDto.getIdx() + "/" + episodeZipFile.getOriginalFilename().substring(0, episodeZipFile.getOriginalFilename().lastIndexOf("."));
                        String fileName = epubEntry.getName().substring(epubEntry.getName().lastIndexOf('/') + 1);
                        File unzipFile = new File(filePath, fileName);

                        fileExists(unzipFile);

                        InputStream epubStream = zipFile.getInputStream(epubEntry);
                        BufferedInputStream epubBfStream = new BufferedInputStream(epubStream);

                        fileOutputStream = new BufferedOutputStream(new FileOutputStream(unzipFile));

                        // 압축 해제된 파일 읽기
                        int length = 0;
                        while ((length = epubBfStream.read()) != -1) {
                            fileOutputStream.write(length);
                        }
                        fileOutputStream.close();
                        fileOutputStream.flush();

                        InputStream inputStreamReader = new FileInputStream(unzipFile);

                        BufferedReader br = new BufferedReader(new InputStreamReader(inputStreamReader));
                        String html = "";
                        Boolean flag = Boolean.FALSE;
                        String s = "";
                        while((s = br.readLine()) != null) {

                            // xhtml에서 body 태그 부터 저장
                            if (s.equalsIgnoreCase("<body>")) {
                                flag = Boolean.TRUE;
                                continue;
                            }

                            // xhtml에서 body 닫기 전 까지만 저장
                            if (s.equalsIgnoreCase("</body>")) {
                                break;
                            }

                            // 블로그, 메일 등
                            if (s.indexOf("href") >= 0) {
                                s = "<div>" + Jsoup.parse(s).text() + "</div>";
                            }

                            // flag true 이면 html 추가
                            if (Boolean.TRUE.equals(flag)) {
                                html += s;
                            }
                        }

                        br.close();
                        inputStreamReader.close();

                        Map<String, Object> map = new HashMap<>();

                        if (!html.equals("")) {
                            map.put("detail", html);
                            map.put("regdate", dateLibrary.getDatetime());
                            map.put("episodeIdx", episodeDto.getIdx());
                        }

                        if (epubEntry.getName().substring(epubEntry.getName().lastIndexOf("/")+1).equalsIgnoreCase("cover.xhtml")) {
                            // cover image
                            coverMap.putAll(map);
                        } else if (
                            epubEntry.getName().substring(epubEntry.getName().lastIndexOf("/")+1).equalsIgnoreCase("cr.xhtml")
                            || epubEntry.getName().substring(epubEntry.getName().lastIndexOf("/")+1).equalsIgnoreCase("copyright.xhtml")
                        ) {
                            // copyright 순서 마지막(map 에 따로 담아둠)
                            copyrightMap.putAll(map);
                        } else if (epubEntry.getName().substring(epubEntry.getName().lastIndexOf("/")+1).equalsIgnoreCase("toc.xhtml")) {
                            // 목차 순서 처음(map 에 따로 담아둠)
                            tocMap.putAll(map);
                        } else {
                            viewerData.add(map);
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
                            String src = matcher.group(2).trim();
                            String fullUrl = imageList.get(src.substring(src.lastIndexOf("/")+1)).toString();

                            data.put("detail", detail.replace(src, fullUrl));
                        }
                    }

                    // sort 순서
                    int i = 1;
                    for (Map<String, Object> map : viewerData) {
                        map.put("sort", i);
                        i++;
                    }

                    // dao 추가
                    result = viewerDao.registerNovel(viewerData);
                }

                // close
                fileInputStream.close();
                zipFile.close();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 압축 해제 파일 삭제
                deleteFile(uploadPath + "/unzip");
                // 압축 파일 삭제
                deleteFile(uploadPath + "/zip");
            }
        } else {
            result = 0;
        }

        return result;
    }

    /**
     * 만화 등록
     * @param episodeDto
     * @return
     */
    @Transactional
    public Integer comic(EpisodeDto episodeDto) throws IllegalStateException {
        Integer result = null;
        /** zip 압축 해제후 디렉토리 저장 */
        // 압축 파일
        MultipartFile episodeZipFile = episodeDto.getZipFile();

        // outputStream 보다 빠름
        BufferedOutputStream fileOutputStream = null;

        List<MultipartFile> viewerImage = new ArrayList<>();

        String contentType = new MimetypesFileTypeMap().getContentType(episodeZipFile.getOriginalFilename());

        // 이미지 등록
        if (contentType.substring(0, contentType.lastIndexOf("/")).equals("image")) {
            viewerImage.add(episodeZipFile);

            // 뷰어 마지막 sort 번호
            Integer sort = viewerDaoSub.getViewerComicLastSort(episodeDto.getIdx());

            /** s3 upload (이미지일 때만) */
            // s3에 저장될 path
            String s3Path = "kr/contents/" + episodeDto.getContentsIdx() + "/episode/" + episodeDto.getIdx() + "/upload";

            /** 이미지 원본 */
            String device = "origin";
            String type = "comic";

            // 파일 있는지 체크
            // s3 upload (원본)
            List<HashMap<String, Object>> uploadResponse = s3Library.uploadFileNew(viewerImage, s3Path);

            uploadResponse = imageSize(uploadResponse, 720);

            // db insert(registerImage 에서 type 으로 구분 webtoon, novel, comic)
            registerImageOne(uploadResponse, episodeDto.getIdx(), s3Path, device, type, new ArrayList<>(), sort+1);

            // 결과
            result = 1;

        } else if (episodeZipFile.getOriginalFilename() != null && !episodeZipFile.getOriginalFilename().equals("")) {
            try (
                    FileInputStream fileInputStream = new FileInputStream(multipartToFile(episodeZipFile));
                    ZipFile zipFile = new ZipFile(uploadPath + "/zip/" + episodeZipFile.getOriginalFilename());
            ) {
                // zip 파일 압축 해제 리스트
                Enumeration<? extends ZipArchiveEntry> entries = zipFile.getEntries();

                // 순서 재정렬
                List<ZipArchiveEntry> zipEntryList = sortLibrary.entrySort(entries);

                for (ZipArchiveEntry entry : zipEntryList) {
                    String unzipPath = uploadPath + "/unzip/" + episodeDto.getIdx();


                    if (entry.isDirectory()) {
                        // 파일이 폴더 안에 있을때
                        File unzipFile = new File(unzipPath, entry.getName());
                        unzipFile.mkdirs();
                    } else {
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
                        String fileName = stringBuilder.toString().concat(extension);

                        // 파일만 있을때
                        File unzipFile = new File(unzipPath, fileName);
                        fileExists(unzipFile);

                        // contentType
                        String entryContentType = new MimetypesFileTypeMap().getContentType(fileName);

                        // 이미지 일때
                        if (entryContentType.substring(0, entryContentType.lastIndexOf("/")).equals("image")) {
                            InputStream stream = zipFile.getInputStream(entry);
                            BufferedInputStream bfStream = new BufferedInputStream(stream);

                            fileOutputStream = new BufferedOutputStream(new FileOutputStream(unzipFile));

                            // 압축 해제된 파일 읽기
                            int length = 0;
                            while ((length = bfStream.read()) != -1) {
                                fileOutputStream.write(length);
                            }
                            fileOutputStream.close();
                            fileOutputStream.flush();

                            FileItem fileItem = new DiskFileItem("file", Files.probeContentType(unzipFile.toPath()), false, unzipFile.getName(), (int) unzipFile.length() , unzipFile.getParentFile());

                            InputStream input = new FileInputStream(unzipFile);
                            OutputStream os = fileItem.getOutputStream();
                            IOUtils.copy(input, os);

                            viewerImage.add(new CommonsMultipartFile(fileItem));
                        }
                    }
                }
                // close
                fileInputStream.close();
                zipFile.close();
                /** //zip 압축 해제후 디렉토리 저장 */


                /** s3 upload (이미지일 때만) */
                // s3에 저장될 path
                String s3Path = "kr/contents/"+ episodeDto.getContentsIdx() + "/episode/" + episodeDto.getIdx() + "/upload";

                /** 이미지 원본 */
                String device = "origin";
                String type = "comic";

                // 파일 있는지 체크
                Boolean chkViewerImage = chkIsEmptyImage(viewerImage);
                if (Boolean.TRUE.equals(chkViewerImage)) {
                    // s3 upload (원본)
                    List<HashMap<String,Object>> uploadResponse = s3Library.uploadFileNew(viewerImage, s3Path);

                    uploadResponse = imageSize(uploadResponse, 720);

                    // db insert
                    registerImage(uploadResponse, episodeDto.getIdx(), s3Path, device, type, new ArrayList<>());
                }

                result = 1;

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 압축 해제 파일 삭제
                deleteFile(uploadPath + "/unzip");
                // 압축 파일 삭제
                deleteFile(uploadPath + "/zip");
            }
        } else {
            result = 0;
        }
        return result;
    }

    /**
     * 뷰어 이미지 삭제
     * @param idx
     * @return
     */
    @Transactional
    public Integer deleteViewerImage(Integer idx, Integer categoryIdx) {

        // idx validation
        if (idx == null || idx < 1) {
            throw new CustomException(CustomError.IDX_VIEWER_ERROR);
        }

        Integer result = null;

        if (categoryIdx == 1) {
            // origin 삭제
            result = viewerDao.deleteWebtoon(idx);
        } else if (categoryIdx == 2) {
            // origin 삭제
            result = viewerDao.deleteComic(idx);
        } else if (categoryIdx == 3) {
            // 소설 삭제
            result = viewerDao.deleteNovel(idx);
        }

        // 관리자 action log
        adminActionLogService.regist(idx, Thread.currentThread().getStackTrace());

        return result;
    }

    /**
     * viewer 이미지 등록
     * @param uploadResponse
     * @param s3Path
     * @param idx
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
            map.put("regdate", dateLibrary.getDatetime());

            if (parents.size() != 0) {
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

        // type (webtoon, novel, comic)
        if ("webtoon".equals(type)) {
            viewerDao.registerImage(uploadResponse);
        } else if ("novel".equals(type)) {
            viewerDao.registerNovelImage(uploadResponse);
        } else if ("comic".equals(type)) {
            viewerDao.registerComicImage(uploadResponse);
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

    /**
     * viewer 이미지 추가 등록
     * @param uploadResponse
     * @param s3Path
     * @param idx
     * @param type
     * @param parents
     * @return
     */
    @Transactional
    public List<Integer> registerImageOne(List<HashMap<String,Object>> uploadResponse, Long idx, String s3Path, String device, String type, List<Integer> parents, Integer sort) {
        for (HashMap<String, Object> map : uploadResponse) {
            map.put("idx", idx);
            map.put("path", s3Path);
            map.put("sort", sort);
            map.put("device", device);
            map.put("regdate", dateLibrary.getDatetime());

            if (parents.size() != 0) {
                map.put("parent", parents.get(0) );
            } else {
                map.put("parent", 0);
            }

            // insertedId
            map.put("insertedId", 0);
            // 대량 insertedId
            map.put("keyId",0);

        }

        // type (webtoon, novel, comic)
        if ("webtoon".equals(type)) {
            viewerDao.registerImage(uploadResponse);
        } else if ("novel".equals(type)) {
            viewerDao.registerNovelImage(uploadResponse);
        } else if ("comic".equals(type)) {
            viewerDao.registerComicImage(uploadResponse);
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

    /**
     * 뷰어 이미지 순서 변경, 소설 내용 수정
     * @param viewerList
     * @return
     */
    @Transactional
    public Integer modifyNovelViewer(List<ViewerDto> viewerList) {
        // 결과
        Integer result =null;

        Integer categoryIdx = viewerList.get(0).getCategoryIdx();

        // 웹툰 이미지 순서 변경
        if (categoryIdx == 1) {
            result = viewerDao.modifyWebtoon(viewerList);
        }
        // 만화 이미지 순서 변경
        else if (categoryIdx == 2) {
            result = viewerDao.modifyComic(viewerList);
        }
        // 소설 내용 수정
        else if (categoryIdx == 3) {
            result = viewerDao.modifyNovel(viewerList);
        }

        // 관리자 action log
        adminActionLogService.regist(viewerList, Thread.currentThread().getStackTrace());

        return result;
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
     * mutipartfile을 file로 변환하는 메서드
     * @param multipart
     * @return
     */
    public File multipartToFile(MultipartFile multipart) {
        File convFile = null;
        try {
            convFile = new File(uploadPath + "/zip", multipart.getOriginalFilename());

            // 디렉토리 확인
            fileExists(convFile);

            multipart.transferTo(convFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convFile;
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
     * 압축 해제 임시 폴더 삭제
     * @param path
     */
    public static void deleteFile(String path) {
        /*File deleteFolder = new File(path);
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
        }*/
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

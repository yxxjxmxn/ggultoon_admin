package com.architecture.admin.libraries.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


public class ExcelWriter {

    private final Workbook workbook;
    private final Map<String, Object> data;
    private final HttpServletResponse response;

    // 생성자
    public ExcelWriter(Workbook workbook, Map<String, Object> data, HttpServletResponse response) {
        this.workbook = workbook;
        this.data = data;
        this.response = response;
    }

    // 엑셀 파일 생성
    public void create() throws IOException {
        setFileName(response, mapToFileName());

        Sheet sheet = workbook.createSheet();

        createHead(sheet, mapToHeadList());

        createBody(sheet, mapToBodyList());
    }

    // 모델 객체에서 파일 이름 꺼내기
    private String mapToFileName() {
        return (String) data.get("filename");
    }

    // 모델 객체에서 헤더 이름 리스트 꺼내기
    @SuppressWarnings("unchecked")
    private List<String> mapToHeadList() {
        return (List<String>) data.get("head");
    }

    // 모델 객체에서 바디 데이터 리스트 꺼내기
    @SuppressWarnings("unchecked")
    private List<List<String>> mapToBodyList() {
        return (List<List<String>>) data.get("body");
    }

    // 파일 이름 지정
    private void setFileName(HttpServletResponse response, String fileName) throws IOException {
        String outputFileName = new String(getFileExtension(fileName).getBytes("KSC5601"), StandardCharsets.ISO_8859_1);
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + outputFileName + "\"");
    }

    // 넘어온 뷰에 따라서 확장자 결정
    private String getFileExtension(String fileName) {
        if (workbook instanceof XSSFWorkbook) {
            fileName += ".xlsx";
        }
        if (workbook instanceof SXSSFWorkbook) {
            fileName += ".xlsx";
        }
        if (workbook instanceof HSSFWorkbook) {
            fileName += ".xls";
        }

        return fileName;
    }

    // 엑셀 헤더 생성
    private void createHead(Sheet sheet, List<String> headList) {
        createRow(sheet, headList, 0);
    }

    // 엑셀 바디 생성
    private void createBody(Sheet sheet, List<List<String>> bodyList) {
        int rowSize = bodyList.size();
        for (int i = 0; i < rowSize; i++) {
            createRow(sheet, bodyList.get(i), i + 1);
        }
    }

    // 행 생성
    private void createRow(Sheet sheet, List<String> cellList, int rowNum) {
        int size = cellList.size();
        Row row = sheet.createRow(rowNum);

        for (int i = 0; i < size; i++) {
            // 엑셀 width 설정
            Integer columnWidth = 3000;
            if (cellList.get(i) != null && cellList.get(i).getBytes().length > 6) {
                columnWidth = cellList.get(i).getBytes().length * 400;
            }
            sheet.setColumnWidth(i, columnWidth);
            row.createCell(i).setCellValue(cellList.get(i));

            // 자료형 설정
/*            if (cellList.get(i) == null) {
                row.createCell(i).setCellValue("-");
            } else if (isNumber(cellList.get(i).toString())) {
                row.createCell(i).setCellValue(Integer.parseInt(cellList.get(i).toString()));
            } else {
                row.createCell(i).setCellValue(cellList.get(i).toString());
            }*/
        }
    }

    // 자료형 구분
    private static boolean isNumber(String strValue) {
        return strValue != null && strValue.matches("[-+]?\\d*\\.?\\d+");
    }


}
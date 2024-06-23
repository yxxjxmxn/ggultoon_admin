package com.architecture.admin.libraries.excel;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class ExcelLibrary {

    /**
     * Workbook 생성 메서드
     * @param file
     * @return
     * @throws IOException
     */
    public Workbook createWorkBook(MultipartFile file) throws IOException {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        Workbook workbook;
        switch (extension) {
            case "xlsx" -> workbook = new XSSFWorkbook(file.getInputStream());
            case "xls" -> workbook = new HSSFWorkbook(file.getInputStream());
            default -> throw new IOException(); // 확장자가 excel이 아닌 경우
        }
        return workbook;
    }

    /**
     * workBook close 메서드.
     * close 하지 않으면 outOFMemory 발생할 가능성 존재
     *
     * @param workbook
     */
    public void closeWorkbook(Workbook workbook) {
        if (workbook != null) {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

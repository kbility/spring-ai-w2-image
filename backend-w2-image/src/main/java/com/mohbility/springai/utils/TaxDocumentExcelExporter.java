package com.mohbility.springai.utils;

import com.mohbility.springai.model.TaxDocumentResult;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaxDocumentExcelExporter {
    public static byte[] toExcel(List<TaxDocumentResult> list) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Tax Documents");
            int rowNum = 0;

            String[] headers = {
                    "Document Type", "Payer Name", "Payer EIN", "Payer Address",
                    "Recipient Name", "Recipient Address", "Recipient SSN Last4",
                    "Wages (W-2 Box 1)", "Federal Tax Withheld (W-2 Box 2)",
                    "Social Security Wages (W-2 Box 3)", "Medicare Wages (W-2 Box 5)",
                    "Nonemployee Compensation (1099-NEC Box 1)", "Federal Tax Withheld (1099-NEC Box 4)",
                    "State", "State Wages/Income", "State Income Tax", "Tax Year"
            };

            Row headerRow = sheet.createRow(rowNum++);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            for (TaxDocumentResult doc : list) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(doc.getDocument_type());
                row.createCell(1).setCellValue(doc.getPayer_name());
                row.createCell(2).setCellValue(doc.getPayer_ein());
                row.createCell(3).setCellValue(doc.getPayer_address());
                row.createCell(4).setCellValue(doc.getRecipient_name());
                row.createCell(5).setCellValue(doc.getRecipient_address());
                row.createCell(6).setCellValue(doc.getRecipient_ssn_last4());
                row.createCell(7).setCellValue(doc.getWages_box1() != null ? doc.getWages_box1() : 0.0);
                row.createCell(8).setCellValue(doc.getFederal_income_tax_withheld_box2() != null ? doc.getFederal_income_tax_withheld_box2() : 0.0);
                row.createCell(9).setCellValue(doc.getSocial_security_wages_box3() != null ? doc.getSocial_security_wages_box3() : 0.0);
                row.createCell(10).setCellValue(doc.getMedicare_wages_box5() != null ? doc.getMedicare_wages_box5() : 0.0);
                row.createCell(11).setCellValue(doc.getNonemployee_compensation_box1() != null ? doc.getNonemployee_compensation_box1() : 0.0);
                row.createCell(12).setCellValue(doc.getFederal_income_tax_withheld_box4() != null ? doc.getFederal_income_tax_withheld_box4() : 0.0);
                row.createCell(13).setCellValue(doc.getState() != null ? doc.getState() : "");
                row.createCell(14).setCellValue(doc.getState_wages() != null ? doc.getState_wages() : 0.0);
                row.createCell(15).setCellValue(doc.getState_income_tax() != null ? doc.getState_income_tax() : 0.0);
                row.createCell(16).setCellValue(doc.getTax_year() != null ? doc.getTax_year() : 0);
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    public static List<Map<String, String>> toTable(List<TaxDocumentResult> list) {
        return list.stream().map(doc -> {
            Map<String, String> row = new LinkedHashMap<>();
            row.put("Document Type", doc.getDocument_type());
            row.put("Payer Name", doc.getPayer_name());
            row.put("Payer EIN", doc.getPayer_ein());
            row.put("Recipient Name", doc.getRecipient_name());
            row.put("Recipient SSN Last4", doc.getRecipient_ssn_last4());
            
            if ("W2".equals(doc.getDocument_type())) {
                row.put("Wages", String.valueOf(doc.getWages_box1() != null ? doc.getWages_box1() : 0.0));
                row.put("Federal Tax Withheld", String.valueOf(doc.getFederal_income_tax_withheld_box2() != null ? doc.getFederal_income_tax_withheld_box2() : 0.0));
                row.put("Social Security Wages", String.valueOf(doc.getSocial_security_wages_box3() != null ? doc.getSocial_security_wages_box3() : 0.0));
                row.put("Medicare Wages", String.valueOf(doc.getMedicare_wages_box5() != null ? doc.getMedicare_wages_box5() : 0.0));
            } else if ("1099-NEC".equals(doc.getDocument_type())) {
                row.put("Nonemployee Compensation", String.valueOf(doc.getNonemployee_compensation_box1() != null ? doc.getNonemployee_compensation_box1() : 0.0));
                row.put("Federal Tax Withheld", String.valueOf(doc.getFederal_income_tax_withheld_box4() != null ? doc.getFederal_income_tax_withheld_box4() : 0.0));
            }
            
            row.put("State", doc.getState() != null ? doc.getState() : "");
            row.put("State Wages/Income", String.valueOf(doc.getState_wages() != null ? doc.getState_wages() : 0.0));
            row.put("State Income Tax", String.valueOf(doc.getState_income_tax() != null ? doc.getState_income_tax() : 0.0));
            row.put("Tax Year", String.valueOf(doc.getTax_year() != null ? doc.getTax_year() : 0));
            return row;
        }).toList();
    }
}

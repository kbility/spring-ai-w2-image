package com.mohbility.springai.utils;

import com.mohbility.springai.model.W2Result;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExcelExporter {
    public static byte[] toExcel(List<W2Result> list) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("W2 Data");
            int rowNum = 0;

            String[] headers = {
                    "Employer Name","Employer EIN", "Employer Address",
                    "Employee Name", "Employee Address","Employee SSN Last4", "Wages Box1",
                    "Federal Tax Box2", "Social Security Wages", "Medicare Wages",
                    "State", "State Wages", "State Income Tax",
                    "Box 12",
                    "Box 13 - Statutory Employee", "Box 13 - Retirement Plan", "Box 13 - Third-Party Sick Pay",
                    "Locality Wages", "Locality Income Tax", "Locality Name"
            };

            Row headerRow = sheet.createRow(rowNum++);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            for (W2Result w2 : list) {
                Row row = sheet.createRow(rowNum++);
                String[] values = {
                        w2.getEmployer_name(), w2.getEmployer_ein(), w2.getEmployer_address(),
                        w2.getEmployee_name(), w2.getEmployee_full_address(), w2.getEmployee_ssn_last4(),
                        String.valueOf(w2.getWages_box1()),
                        String.valueOf(w2.getFederal_income_tax_withheld_box2()),
                        String.valueOf(w2.getSocial_security_wages_box3()),
                        String.valueOf(w2.getMedicare_wages_box5()),
                        w2.getState(),
                        String.valueOf(w2.getState_wages()),
                        String.valueOf(w2.getState_income_tax()),
                        formatBox12(w2.getBox_12()),
                        yesNo(w2.isBox13_statutory_employee()),
                        yesNo(w2.isBox13_retirement_plan()),
                        yesNo(w2.isBox13_third_party_sick_pay()),
                        String.valueOf(w2.getLocality_wages()),
                        String.valueOf(w2.getLocality_income_tax()),
                        w2.getLocality_name()
                };
                for (int i = 0; i < values.length; i++) {
                    row.createCell(i).setCellValue(values[i]);
                }
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private static String formatBox12(Map<String, Double> box12) {
        if (box12 == null || box12.isEmpty()) return "";
        return box12.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining("; "));
    }

    private static String yesNo(boolean b) {
        return b ? "Yes" : "No";
    }

    public static List<Map<String, String>> toTable(List<W2Result> list) {
        return list.stream().map(w2 -> {
            Map<String, String> row = new LinkedHashMap<>();
            row.put("Employer Name", w2.getEmployer_name());
            row.put("Employer EIN", w2.getEmployer_ein());
            row.put("Employer Address", w2.getEmployer_address());
            row.put("Employee Name", w2.getEmployee_name());
            row.put("Employee Address", w2.getEmployee_full_address());
            row.put("Employee SSN Last4", w2.getEmployee_ssn_last4());
            row.put("Wages Box1", String.valueOf(w2.getWages_box1()));
            row.put("Federal Tax Box2", String.valueOf(w2.getFederal_income_tax_withheld_box2()));
            row.put("Social Security Wages", String.valueOf(w2.getSocial_security_wages_box3()));
            row.put("Medicare Wages", String.valueOf(w2.getMedicare_wages_box5()));
            row.put("State", w2.getState());
            row.put("State Wages", String.valueOf(w2.getState_wages()));
            row.put("State Income Tax", String.valueOf(w2.getState_income_tax()));
            row.put("Box 12", formatBox12(w2.getBox_12()));
            row.put("Box 13 - Statutory Employee", yesNo(w2.isBox13_statutory_employee()));
            row.put("Box 13 - Retirement Plan", yesNo(w2.isBox13_retirement_plan()));
            row.put("Box 13 - Third-Party Sick Pay", yesNo(w2.isBox13_third_party_sick_pay()));
            row.put("Locality Wages", String.valueOf(w2.getLocality_wages()));
            row.put("Locality Income Tax", String.valueOf(w2.getLocality_income_tax()));
            row.put("Locality Name", w2.getLocality_name());
            return row;
        }).toList();
    }
}

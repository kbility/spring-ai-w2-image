package com.mohbility.springai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaxDocumentResult {
    // Document type identifier
    private String document_type; // "W2" or "1099-NEC"
    
    // Common fields (payer/employer)
    private String payer_name;
    private String payer_ein;
    private String payer_address;
    
    // Common fields (recipient/employee)
    private String recipient_name;
    private String recipient_address;
    private String recipient_ssn_last4;
    
    // W-2 specific fields
    private Double wages_box1;
    private Double federal_income_tax_withheld_box2;
    private Double social_security_wages_box3;
    private Double medicare_wages_box5;
    private Map<String, Double> box_12;
    private Boolean box13_statutory_employee;
    private Boolean box13_retirement_plan;
    private Boolean box13_third_party_sick_pay;
    
    // 1099-NEC specific fields
    private Double nonemployee_compensation_box1;
    private Boolean payer_made_direct_sales_box2;
    private Double federal_income_tax_withheld_box4;
    
    // State tax info (common)
    private String state;
    private Double state_wages;
    private Double state_income_tax;
    private String state_payer_number;
    
    // Locality info (W-2 specific)
    private Double locality_wages;
    private Double locality_income_tax;
    private String locality_name;
    
    private Integer tax_year;
}

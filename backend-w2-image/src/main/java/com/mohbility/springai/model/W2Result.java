package com.mohbility.springai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class W2Result {
    private String employer_name;
    private String employer_ein;
    private String employer_address;
    private String employee_name;
    private String employee_full_address;
    private String employee_email;
    private String employee_phone;
    private String employee_ssn_last4;
    private double wages_box1;
    private double federal_income_tax_withheld_box2;
    private double social_security_wages_box3;
    private double medicare_wages_box5;
    private Map<String, Double> box_12;
    // Box 13 checkboxes
    private boolean box13_statutory_employee;
    private boolean box13_retirement_plan;
    private boolean box13_third_party_sick_pay;
    private String state;
    private double state_wages;
    private double state_income_tax;
    private double locality_wages;
    private double locality_income_tax;
    private String locality_name;
    private int tax_year;
}


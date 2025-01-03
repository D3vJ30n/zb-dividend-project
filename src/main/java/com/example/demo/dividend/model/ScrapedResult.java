package com.example.demo.dividend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScrapedResult {
    private Company company;
    private List<Dividend> dividends;

    public ScrapedResult(Company company) {
        this.company = company;
        this.dividends = new ArrayList<>();
    }
}
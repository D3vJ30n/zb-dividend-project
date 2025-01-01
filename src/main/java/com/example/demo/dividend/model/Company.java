package com.example.demo.dividend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    private Long id;
    private String ticker;
    private String name;

    public Company(String ticker, String name) {
        this.ticker = ticker;
        this.name = name;
    }
}
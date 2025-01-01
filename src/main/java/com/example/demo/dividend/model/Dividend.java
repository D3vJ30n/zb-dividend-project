package com.example.demo.dividend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dividend {
    private LocalDateTime date;
    private String dividend;

    public Dividend(LocalDateTime now, double v) {

    }
}
package com.example.demo.dividend.persist.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "COMPANY")
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String ticker;

    private String name;
}
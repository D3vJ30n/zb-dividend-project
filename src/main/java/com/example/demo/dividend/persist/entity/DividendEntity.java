package com.example.demo.dividend.persist.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    name = "DIVIDEND",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"companyId", "date"}
        )
    }
)
public class DividendEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyId;

    private LocalDateTime date;

    private String dividend;
}
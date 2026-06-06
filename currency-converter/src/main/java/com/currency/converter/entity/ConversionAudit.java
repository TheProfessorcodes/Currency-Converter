package com.currency.converter.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversion_audits")
@EntityListeners(AuditingEntityListener.class)
@Data
public class ConversionAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String toCurrency;
    private String fromCurrency;
    private double units;
    private double result;


    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime conversrionTime;

}

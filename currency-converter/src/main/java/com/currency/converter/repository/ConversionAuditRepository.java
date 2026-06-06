package com.currency.converter.repository;

import com.currency.converter.entity.ConversionAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversionAuditRepository extends JpaRepository<ConversionAudit, Long> {

}

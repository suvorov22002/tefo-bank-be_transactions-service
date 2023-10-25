package com.tefo.bank.transactionsservice.model;

import com.tefo.library.commonutils.validation.MandatoryField;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {
    @MandatoryField
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String updatedBy;
}

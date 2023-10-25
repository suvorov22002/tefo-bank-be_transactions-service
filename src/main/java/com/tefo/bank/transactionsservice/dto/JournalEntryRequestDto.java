package com.tefo.bank.transactionsservice.dto;

import com.tefo.library.commonutils.validation.CustomValidation;
import com.tefo.library.commonutils.validation.MandatoryField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntryRequestDto {
    private Long id;
    @MandatoryField
    private Integer accountBalanceTypeDictionaryValueId;
    @MandatoryField
    private Long accountId;
    @MandatoryField
    @CustomValidation(min = 0)
    private BigDecimal originalCurrencyAmount;
    @MandatoryField
    private LocalDateTime entryDate;
    private String notes;
}

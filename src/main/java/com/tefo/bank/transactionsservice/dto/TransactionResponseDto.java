package com.tefo.bank.transactionsservice.dto;

import com.tefo.library.commonutils.basestructure.dto.BaseResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TransactionResponseDto extends BaseResponseDto {
    private Long id;
    private String number;
    private String notes;
    private LocalDateTime entryDate;
    private LocalDateTime processingDate;
    private Long currencyId;
    private String currencyAlphabeticCode;
    private String currencySymbol;
    private BigDecimal originalCurrencyAmount;
    private BigDecimal referenceCurrencyAmount;
    private Long transactionStatusDictionaryValueId;
    private String transactionStatusDictionaryValueName;
    private Long transactionSubStatusDictionaryValueId;
    private List<JournalEntryResponseDto> journalEntries = new ArrayList<>();
}

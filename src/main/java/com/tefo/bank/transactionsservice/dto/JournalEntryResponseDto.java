package com.tefo.bank.transactionsservice.dto;

import com.tefo.library.commonutils.basestructure.dto.BaseResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class JournalEntryResponseDto extends BaseResponseDto {
    private Long id;
    private Integer accountBalanceTypeDictionaryValueId;
    private String accountBalanceTypeDictionaryValueName;
    private Long accountId;
    private String accountNumber;
    private String accountName;
    private Long currencyId;
    private String currencyAlphabeticCode;
    private String currencySymbol;
    private Integer transactionStatusDictionaryValueId;
    private String transactionStatusName;
    private BigDecimal debit;
    private BigDecimal credit;
    private LocalDateTime entryDate;
    private String transactionNumber;
    private LocalDateTime processedAt;
}

package com.tefo.bank.transactionsservice.feignclient.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyBasicInfoDto {
    private Long id;
    private String currencySymbol;
    private String currencyAlphabeticCode;
    private Boolean isReferenceCurrency;
    private Integer numberOfDecimals;
}

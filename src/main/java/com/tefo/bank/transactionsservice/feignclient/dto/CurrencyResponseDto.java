package com.tefo.bank.transactionsservice.feignclient.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyResponseDto {
    private Long id;
    private String name;

    private String alphabeticCode;

    private String numericCode;

    private String symbol;

    private Integer numberOfDecimals;
}

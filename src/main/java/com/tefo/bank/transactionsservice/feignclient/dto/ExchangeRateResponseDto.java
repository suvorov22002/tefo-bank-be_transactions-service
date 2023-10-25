package com.tefo.bank.transactionsservice.feignclient.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ExchangeRateResponseDto {

    private Long id;
    private String exchangeRateType;
    private String baseCurrency;
    private String quoteCurrency;
    private double bidRate;
    private double midRate;
    private double askRate;
    private Integer multiplier;
    private LocalDate busisnessDate;
    private LocalDateTime effectiveFromDate;
    private String unitId;

}

package com.tefo.bank.transactionsservice.feignclient.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DictionaryValueResponseDto {
    private Integer id;
    private String name;
    private String code;
    private String status;
    private Integer index;
}

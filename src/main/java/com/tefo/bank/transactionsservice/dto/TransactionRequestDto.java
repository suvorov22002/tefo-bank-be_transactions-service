package com.tefo.bank.transactionsservice.dto;

import com.tefo.library.commonutils.validation.MandatoryField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestDto {
    private Long id;
    @MandatoryField
    private Long currencyId;
    private String notes;
    private List<JournalEntryRequestDto> journalEntries = new ArrayList<>();
}

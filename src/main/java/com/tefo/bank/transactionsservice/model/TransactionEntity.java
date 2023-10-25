package com.tefo.bank.transactionsservice.model;

import com.tefo.library.commonutils.validation.CustomValidation;
import com.tefo.library.commonutils.validation.MandatoryField;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction")
@FieldNameConstants
public class TransactionEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @MandatoryField
    private String number;
    @MandatoryField
    private Boolean isManual;
    //TODO
    private String transactionType;
    //TODO
    private String transactionChannel;
    //TODO Will be dictionary value id
    private String transactionSource;
    @MandatoryField
    private LocalDateTime entryDate;
    private LocalDateTime valueDate;
    private LocalDateTime executionDate;
    private LocalDateTime processingDate;
    @MandatoryField
    private Long currencyId;
    private String currencySymbol;
    private String currencyAlphabeticCode;
    @MandatoryField
    @CustomValidation(min = 0)
    private BigDecimal originalCurrencyAmount;
    @MandatoryField
    @CustomValidation(min = 0)
    private BigDecimal referenceCurrencyAmount;
    private Long adjustmentTransactionId;
    private String externalTransactionNumber;
    private String notes;
    @MandatoryField
    private Long transactionStatusDictionaryValueId;
    private Long transactionSubStatusDictionaryValueId;
    @OneToMany(mappedBy = "transaction")
    private List<JournalEntry> journalEntries = new ArrayList<>();
    @MandatoryField
    private String createdBy;
}

package com.tefo.bank.transactionsservice.model;

import com.tefo.library.commonutils.validation.MandatoryField;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntry extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    @MandatoryField
    private TransactionEntity transaction;
    @MandatoryField
    private Long accountId;
    @MandatoryField
    private String accountNumber;
    @MandatoryField
    private BigDecimal originalCurrencyAmount;
    @MandatoryField
    private BigDecimal referenceCurrencyAmount;
    @MandatoryField
    private Integer accountBalanceTypeDictionaryValueId;
}

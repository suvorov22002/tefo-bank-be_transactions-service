package com.tefo.bank.transactionsservice.service;

import com.tefo.bank.transactionsservice.model.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionService {

    TransactionEntity createManualTransaction(TransactionEntity entity);

    TransactionEntity update(Long id, TransactionEntity entity);

    TransactionEntity getOne(Long id);

    List<TransactionEntity> getAll();

    Page<TransactionEntity> getAllPaginated(Pageable pageable);

    void approveTransaction(Long transactionId);

    void rejectTransaction(Long transactionId);

    void cancelTransaction(Long transactionId);

}

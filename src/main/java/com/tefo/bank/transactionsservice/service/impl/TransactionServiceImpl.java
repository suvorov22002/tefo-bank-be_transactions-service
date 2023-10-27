package com.tefo.bank.transactionsservice.service.impl;

import com.tefo.bank.transactionsservice.feignclient.dto.CurrencyBasicInfoDto;
import com.tefo.bank.transactionsservice.mapper.TransactionMapper;
import com.tefo.bank.transactionsservice.model.JournalEntry;
import com.tefo.bank.transactionsservice.model.TransactionEntity;
import com.tefo.bank.transactionsservice.repository.JournalEntryRepository;
import com.tefo.bank.transactionsservice.repository.TransactionRepository;
import com.tefo.bank.transactionsservice.service.JournalEntryService;
import com.tefo.bank.transactionsservice.service.TransactionService;
import com.tefo.bank.transactionsservice.utils.TransactionUtils;
import com.tefo.library.commonutils.auth.RequestScope;
import com.tefo.library.commonutils.constants.ExceptionMessages;
import com.tefo.library.commonutils.constants.SystemDictionaryConstants;
import com.tefo.library.commonutils.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final TransactionMapper mapper;
    private final TransactionUtils transactionUtils;
    private final JournalEntryService journalEntryService;
    private final JournalEntryRepository journalEntryRepository;
    private final RequestScope requestScope;

    @Override
    @Transactional
    public TransactionEntity createManualTransaction(TransactionEntity transaction) {
        transaction.setCreatedAt(LocalDateTime.now());
        transactionUtils.calculateTransaction(transaction);
        transaction.setNumber(transactionUtils.generateTransactionNumber(transaction.getCreatedAt()));
        transaction.setCreatedBy(requestScope.getCurrentUserId());
        transaction.setEntryDate(transactionUtils.getEntryDateForTransactionFromCurrentBusinessDay());
        transaction.setIsManual(true);
        CurrencyBasicInfoDto currencyDto = transactionUtils.getCurrencyBasicInfoDtoById(transaction.getCurrencyId());
        transaction.setCurrencySymbol(currencyDto.getCurrencySymbol());
        transaction.setCurrencyAlphabeticCode(currencyDto.getCurrencyAlphabeticCode());
        transaction.setTransactionStatusDictionaryValueId(SystemDictionaryConstants.INITIAL_STATUS_TRANSACTION_DICTIONARY_VALUE_ID.longValue());
        TransactionEntity savedTransaction = repository.save(transaction);
        List<JournalEntry> savedJournalEntries = journalEntryService.addJournalEntriesToTransaction(savedTransaction, transaction.getJournalEntries());
        savedTransaction.setJournalEntries(savedJournalEntries);
        return savedTransaction;
    }

    @Override
    @Transactional
    public TransactionEntity update(Long id, TransactionEntity source) {
        TransactionEntity target = getOne(id);
        if (!transactionUtils.isUserAbleToEditTransaction(target)) {
            throw new IllegalArgumentException(ExceptionMessages.GENERAL_ACCESS_DENIED);
        }
        return repository.save(mapper.update(target, source));
    }

    @Override
    public TransactionEntity getOne(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.TRANSACTION_NOT_FOUND));
    }

    @Override
    public List<TransactionEntity> getAll() {
        return repository.findAll();
    }

    @Override
    public Page<TransactionEntity> getAllPaginated(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    @Transactional
    public void approveTransaction(Long transactionId) {
        TransactionEntity transactionToApprove = getOne(transactionId);
        if (!transactionUtils.isUserAbleToApproveTransaction(transactionToApprove)) {
            throw new IllegalArgumentException(ExceptionMessages.GENERAL_ACCESS_DENIED);
        }
        transactionToApprove.setTransactionStatusDictionaryValueId(SystemDictionaryConstants.PENDING_STATUS_TRANSACTION_DICTIONARY_VALUE_ID.longValue());
        repository.save(transactionToApprove);
    }

    @Override
    @Transactional
    public void rejectTransaction(Long transactionId) {
        TransactionEntity transactionToReject = getOne(transactionId);
        if (!transactionUtils.isUserAbleToRejectTransaction(transactionToReject)) {
            throw new IllegalArgumentException(ExceptionMessages.GENERAL_ACCESS_DENIED);
        }
        transactionToReject.setTransactionStatusDictionaryValueId(
                SystemDictionaryConstants.REJECTED_STATUS_TRANSACTION_DICTIONARY_VALUE_ID.longValue());
        repository.save(transactionToReject);
    }

    @Override
    @Transactional
    public void cancelTransaction(Long transactionId) {
        TransactionEntity transactionToCancel = getOne(transactionId);
        if (!transactionUtils.isUserAbleToCancelTransaction(transactionToCancel)) {
            throw new IllegalArgumentException(ExceptionMessages.GENERAL_ACCESS_DENIED);
        }
        transactionToCancel.setTransactionStatusDictionaryValueId(
                SystemDictionaryConstants.CANCELED_STATUS_TRANSACTION_DICTIONARY_VALUE_ID.longValue());
        repository.save(transactionToCancel);
    }

    @Override
    public List<TransactionEntity> getTransactionsByAccountNumber(String accountNumber) {
        List<JournalEntry> journalEntriesByAccountNumber = journalEntryRepository.findAllByAccountNumber(accountNumber);
        return journalEntriesByAccountNumber.stream()
                .map(JournalEntry::getTransaction)
                .distinct()
                .toList();
    }
}

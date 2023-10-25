package com.tefo.bank.transactionsservice.service.impl;

import com.tefo.bank.transactionsservice.mapper.JournalEntryMapper;
import com.tefo.bank.transactionsservice.model.JournalEntry;
import com.tefo.bank.transactionsservice.model.TransactionEntity;
import com.tefo.bank.transactionsservice.repository.JournalEntryRepository;
import com.tefo.bank.transactionsservice.service.JournalEntryService;
import com.tefo.bank.transactionsservice.utils.TransactionUtils;
import com.tefo.library.commonutils.constants.ExceptionMessages;
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
public class JournalEntryServiceImpl implements JournalEntryService {

    private final JournalEntryRepository repository;
    private final JournalEntryMapper mapper;
    private final TransactionUtils transactionUtils;

    @Override
    @Transactional
    public JournalEntry addOneJournalEntryToTransaction(JournalEntry journalEntry, TransactionEntity transaction) {
        journalEntry.setCreatedAt(LocalDateTime.now());
        journalEntry.setTransaction(transaction);
        transactionUtils.calculateTransaction(transaction);
        return repository.save(journalEntry);
    }

    @Override
    public JournalEntry getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.JOURNAL_ENTRY_IS_NOT_FOUND));
    }

    @Override
    public List<JournalEntry> getAllByTransactionId(Long transactionId) {
        return repository.findAllByTransactionIdOrderByCreatedAtDesc(transactionId);
    }

    @Override
    public Page<JournalEntry> getAllByTransactionIdPaginated(Long transactionId, Pageable pageable) {
        return repository.findAllByTransactionIdOrderByCreatedAtDesc(transactionId, pageable);
    }

    @Override
    @Transactional
    public List<JournalEntry> addJournalEntriesToTransaction(TransactionEntity transaction, List<JournalEntry> journalEntries) {
        journalEntries.forEach(journalEntry -> {
            journalEntry.setCreatedAt(LocalDateTime.now());
            journalEntry.setTransaction(transaction);
        });
        transactionUtils.calculateTransaction(transaction);
        return repository.saveAll(journalEntries);
    }

    @Override
    @Transactional
    public JournalEntry updateJournalEntry(Long journalEntryId, JournalEntry source) {
        JournalEntry target = getById(journalEntryId);
        JournalEntry updatedJE = repository.save(mapper.update(target, source));
        TransactionEntity transaction = updatedJE.getTransaction();
        transactionUtils.calculateTransaction(transaction);
        return updatedJE;
    }

    @Override
    @Transactional
    public void deleteJournalEntry(Long journalEntryId) {
        JournalEntry toDelete = getById(journalEntryId);
        TransactionEntity transaction = toDelete.getTransaction();
        repository.delete(toDelete);

        transactionUtils.calculateTransaction(transaction);
    }

    @Override
    public Page<JournalEntry> getAllPaginated(Pageable pageable) {
        return repository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Override
    public Page<JournalEntry> getAllPaginatedFilterByAccountNumber(String accountNumber, Pageable pageable) {
        return repository.findAllByAccountNumberIsLikeIgnoreCaseOrderByCreatedAtDesc(accountNumber, pageable);
    }

    @Override
    public List<JournalEntry> getAll() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional
    public void deleteJournalEntries(List<Long> journalEntryIds) {
        List<JournalEntry> journalEntriesToDelete = repository.findAllById(journalEntryIds);
        if(journalEntriesToDelete.size() == 0) {
            throw new EntityNotFoundException(ExceptionMessages.JOURNAL_ENTRY_IS_NOT_FOUND);
        }
        TransactionEntity transaction = journalEntriesToDelete.get(0).getTransaction();

        repository.deleteAll(journalEntriesToDelete);
        transactionUtils.calculateTransaction(transaction);
    }
}

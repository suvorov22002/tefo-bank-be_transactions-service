package com.tefo.bank.transactionsservice.service;

import com.tefo.bank.transactionsservice.model.JournalEntry;
import com.tefo.bank.transactionsservice.model.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JournalEntryService {
    JournalEntry addOneJournalEntryToTransaction(JournalEntry journalEntry, TransactionEntity transaction);

    JournalEntry getById(Long id);

    List<JournalEntry> getAllByTransactionId(Long transactionId);

    Page<JournalEntry> getAllByTransactionIdPaginated(Long transactionId, Pageable pageable);

    List<JournalEntry> addJournalEntriesToTransaction(TransactionEntity transaction, List<JournalEntry> journalEntries);

    JournalEntry updateJournalEntry(Long journalEntryId, JournalEntry source);

    void deleteJournalEntry(Long journalEntryId);

    void deleteJournalEntries(List<Long> journalEntryIds);

    Page<JournalEntry> getAllPaginated(Pageable pageable);

    Page<JournalEntry> getAllPaginatedFilterByAccountNumber(String accountNumber, Pageable pageable);

    List<JournalEntry> getAll();

}

package com.tefo.bank.transactionsservice.repository;

import com.tefo.bank.transactionsservice.model.JournalEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {

    Page<JournalEntry> findAllByTransactionIdOrderByCreatedAtDesc(Long transactionId, Pageable pageable);

    Page<JournalEntry> findAllByAccountNumberIsLikeIgnoreCaseOrderByCreatedAtDesc(String accountNumber, Pageable pageable);

    List<JournalEntry> findAllByTransactionIdOrderByCreatedAtDesc(Long transactionId);

    List<JournalEntry> findAllByOrderByCreatedAtDesc();

    Page<JournalEntry> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<JournalEntry> findAllByAccountNumber(String accountNumber);
}

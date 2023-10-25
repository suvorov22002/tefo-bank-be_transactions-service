package com.tefo.bank.transactionsservice.service.impl;

import com.tefo.bank.transactionsservice.mapper.JournalEntryMapper;
import com.tefo.bank.transactionsservice.model.JournalEntry;
import com.tefo.bank.transactionsservice.model.TransactionEntity;
import com.tefo.bank.transactionsservice.repository.JournalEntryRepository;
import com.tefo.bank.transactionsservice.utils.TransactionUtils;
import com.tefo.library.commonutils.constants.ExceptionMessages;
import com.tefo.library.commonutils.constants.SystemDictionaryConstants;
import com.tefo.library.commonutils.exception.EntityNotFoundException;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JournalEntryServiceImplTest {

    @InjectMocks
    private JournalEntryServiceImpl journalEntryService;

    @Mock
    private JournalEntryRepository repository;

    @Mock
    private TransactionUtils transactionUtils;

    @Mock
    private JournalEntryMapper mapper;

    private JournalEntry journalEntry;
    private TransactionEntity transactionEntity;


    @BeforeEach
    void setUp() {
        journalEntry = Instancio.create(JournalEntry.class);
        transactionEntity = Instancio.create(TransactionEntity.class);
        transactionEntity.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        transactionEntity.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        transactionEntity.setEntryDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        transactionEntity.setProcessingDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        transactionEntity.setExecutionDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        transactionEntity.setValueDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        transactionEntity.setTransactionStatusDictionaryValueId(SystemDictionaryConstants.INITIAL_STATUS_TRANSACTION_DICTIONARY_VALUE_ID.longValue());
    }

    @Test
    void shouldAddOneJournalEntryToTransaction() {
        when(repository.save(any(JournalEntry.class))).thenReturn(journalEntry);

        JournalEntry result = journalEntryService.addOneJournalEntryToTransaction(journalEntry, transactionEntity);

        assertNotNull(result);
        verify(repository).save(any(JournalEntry.class));
    }

    @Test
    void shouldGetJournalEntryById() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(journalEntry));

        JournalEntry result = journalEntryService.getById(1L);

        assertNotNull(result);
        verify(repository).findById(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenJournalEntryNotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> journalEntryService.getById(1L));
        assertEquals(ExceptionMessages.JOURNAL_ENTRY_IS_NOT_FOUND, exception.getMessage());
    }

    @Test
    void shouldGetAllJournalEntries() {
        when(repository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(journalEntry));

        List<JournalEntry> result = journalEntryService.getAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(repository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void shouldGetAllPaginatedByTransactionIdJournalEntries() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<JournalEntry> journalEntryPage = mock(Page.class);
        when(repository.findAllByTransactionIdOrderByCreatedAtDesc(anyLong(), any(Pageable.class))).thenReturn(journalEntryPage);

        Page<JournalEntry> result = journalEntryService.getAllByTransactionIdPaginated(1L, pageable);

        assertNotNull(result);
        verify(repository).findAllByTransactionIdOrderByCreatedAtDesc(anyLong(), any(Pageable.class));
    }

    @Test
    void shouldAddJournalEntriesToTransaction() {
        when(repository.saveAll(anyList())).thenReturn(List.of(journalEntry));

        List<JournalEntry> result = journalEntryService.addJournalEntriesToTransaction(transactionEntity, List.of(journalEntry));

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(repository).saveAll(anyList());
    }

    @Test
    void shouldGetAllByTransactionId(){
        when(repository.findAllByTransactionIdOrderByCreatedAtDesc(anyLong())).thenReturn(List.of(journalEntry));
        List<JournalEntry> result = journalEntryService.getAllByTransactionId(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(repository).findAllByTransactionIdOrderByCreatedAtDesc(anyLong());
    }

    @Test
    void shouldGetAllPaginatedFilterByAccountNumber(){
        Pageable pageable = PageRequest.of(0, 10);
        Page<JournalEntry> journalEntryPage = mock(Page.class);
        when(repository.findAllByAccountNumberIsLikeIgnoreCaseOrderByCreatedAtDesc(anyString(), any(Pageable.class))).thenReturn(journalEntryPage);

        Page<JournalEntry> result = journalEntryService.getAllPaginatedFilterByAccountNumber("NUMBER", pageable);

        assertNotNull(result);
        verify(repository).findAllByAccountNumberIsLikeIgnoreCaseOrderByCreatedAtDesc(anyString(), any(Pageable.class));
    }

    @Test
    void shouldGetAllPaginated(){
        Pageable pageable = PageRequest.of(0, 10);
        Page<JournalEntry> journalEntryPage = mock(Page.class);
        when(repository.findAllByOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(journalEntryPage);

        Page<JournalEntry> result = journalEntryService.getAllPaginated(pageable);

        assertNotNull(result);
        verify(repository).findAllByOrderByCreatedAtDesc(any(Pageable.class));
    }

    @Test
    void shouldDeleteJournalEntry(){
        when(repository.findById(anyLong())).thenReturn(Optional.of(journalEntry));
        journalEntryService.deleteJournalEntry(1L);
        transactionUtils.calculateTransaction(transactionEntity);
        verify(repository).delete(any());
        verify(repository).findById(anyLong());
    }

    @Test
    void shouldUpdateJournalEntry(){
        when(repository.findById(anyLong())).thenReturn(Optional.of(journalEntry));
        when(mapper.update(any(JournalEntry.class), any(JournalEntry.class))).thenReturn(journalEntry);
        when(repository.save(any(JournalEntry.class))).thenReturn(journalEntry);

        JournalEntry result = journalEntryService.updateJournalEntry(1L, journalEntry);

        verify(repository).findById(anyLong());
        verify(repository).save(any(JournalEntry.class));
        assertNotNull(result);
    }

    @Test
    void shouldDeleteJournalEntries(){
        List<Long> id = List.of(1L, 2L, 3L);
        when(repository.findAllById(any())).thenReturn(List.of(journalEntry));
        journalEntryService.deleteJournalEntries(id);
        verify(repository).deleteAll(anyList());
        verify(repository).findAllById(anyList());
    }

    @Test
    void shouldThrowExceptionWhenJournalListIsEmpty(){
        when(repository.findAllById(anyList())).thenReturn(Collections.emptyList());
        Exception exception = assertThrows(EntityNotFoundException.class, () -> journalEntryService.deleteJournalEntries(List.of(1L)));
        assertEquals(ExceptionMessages.JOURNAL_ENTRY_IS_NOT_FOUND, exception.getMessage());
    }
}
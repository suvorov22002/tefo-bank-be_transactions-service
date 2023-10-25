package com.tefo.bank.transactionsservice.service.impl;

import com.tefo.bank.transactionsservice.feignclient.dto.CurrencyBasicInfoDto;
import com.tefo.bank.transactionsservice.mapper.TransactionMapper;
import com.tefo.bank.transactionsservice.model.JournalEntry;
import com.tefo.bank.transactionsservice.model.TransactionEntity;
import com.tefo.bank.transactionsservice.repository.TransactionRepository;
import com.tefo.bank.transactionsservice.service.JournalEntryService;
import com.tefo.bank.transactionsservice.utils.TransactionUtils;
import com.tefo.library.commonutils.auth.RequestScope;
import com.tefo.library.commonutils.constants.ExceptionMessages;
import com.tefo.library.commonutils.constants.SystemDictionaryConstants;
import com.tefo.library.commonutils.exception.EntityNotFoundException;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository repository;
    @Mock
    private TransactionMapper mapper;
    @Mock
    private TransactionUtils transactionUtils;
    @Mock
    private JournalEntryService journalEntryService;
    @Mock
    private RequestScope requestScope;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    TransactionEntity entity;

    @BeforeEach
    void setUp() {
        entity = Instancio.create(TransactionEntity.class);
        entity.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        entity.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        entity.setEntryDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        entity.setProcessingDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        entity.setExecutionDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        entity.setValueDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        entity.setTransactionStatusDictionaryValueId(SystemDictionaryConstants.INITIAL_STATUS_TRANSACTION_DICTIONARY_VALUE_ID.longValue());
    }

    @Test
    void shouldCreateManualTransaction() {
        when(transactionUtils.getCurrencyBasicInfoDtoById(any())).thenReturn(new CurrencyBasicInfoDto());
        when(repository.save(any(TransactionEntity.class))).thenReturn(entity);
        when(journalEntryService.addJournalEntriesToTransaction(any(TransactionEntity.class), anyList())).thenReturn(List.of(new JournalEntry()));

        TransactionEntity result = transactionService.createManualTransaction(entity);

        assertNotNull(result);
        verify(transactionUtils).calculateTransaction(eq(entity));
        verify(repository).save(any(TransactionEntity.class));
    }

    @Test
    void shouldUpdateTransaction() {
        TransactionEntity sourceTransaction = new TransactionEntity();
        when(repository.findById(any())).thenReturn(Optional.of(entity));
        when(repository.save(any())).thenReturn(entity);
        when(mapper.update(any(), any())).thenReturn(entity);
        given(transactionUtils.isUserAbleToEditTransaction(any())).willReturn(true);

        TransactionEntity result = transactionService.update(1L, sourceTransaction);

        assertNotNull(result);
        verify(repository).findById(eq(1L));
        verify(mapper).update(eq(entity), eq(sourceTransaction));
        verify(repository).save(any(TransactionEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonInitialStatusTransaction() {
        entity.setTransactionStatusDictionaryValueId(1L);
        when(repository.findById(anyLong())).thenReturn(Optional.of(entity));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> transactionService.update(1L, entity));
        assertEquals(ExceptionMessages.GENERAL_ACCESS_DENIED, exception.getMessage());
    }

    @Test
    void shouldGetOneTransaction() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(entity));

        TransactionEntity result = transactionService.getOne(1L);

        assertNotNull(result);
        verify(repository).findById(eq(1L));
    }

    @Test
    void shouldThrowExceptionWhenTransactionNotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> transactionService.getOne(1L));
        assertEquals(ExceptionMessages.TRANSACTION_NOT_FOUND, exception.getMessage());
    }

    @Test
    void shouldGetAllTransactions() {
        when(repository.findAll()).thenReturn(List.of(entity));

        List<TransactionEntity> result = transactionService.getAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(repository).findAll();
    }

    @Test
    void shouldGetAllPaginatedTransactions() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TransactionEntity> transactionEntityPage = mock(Page.class);
        when(repository.findAll(any(Pageable.class))).thenReturn(transactionEntityPage);

        Page<TransactionEntity> result = transactionService.getAllPaginated(pageable);

        assertNotNull(result);
        verify(repository).findAll(eq(pageable));
    }

    @Test
    void shouldApproveTransaction() {
        given(repository.findById(1L)).willReturn(Optional.of(entity));
        given(transactionUtils.isUserAbleToApproveTransaction(any())).willReturn(true);
        entity.setCreatedBy("user2");

        transactionService.approveTransaction(1L);

        verify(repository).save(any());
        verify(repository).findById(any());
    }

    @Test
    void shouldRejectTransaction() {
        given(repository.findById(1L)).willReturn(Optional.of(entity));
        given(transactionUtils.isUserAbleToRejectTransaction(any())).willReturn(true);
        entity.setCreatedBy("user2");

        transactionService.rejectTransaction(1L);

        verify(repository).save(any());
        verify(repository).findById(any());
    }

    @Test
    void shouldCancelTransaction() {
        given(repository.findById(1L)).willReturn(Optional.of(entity));
        given(transactionUtils.isUserAbleToCancelTransaction(any())).willReturn(true);
        entity.setCreatedBy("user2");

        transactionService.cancelTransaction(1L);

        verify(repository).save(any());
        verify(repository).findById(any());
    }

    @Test
    void shouldThrowException_onChangeTransactionStatusBySameUserForApprove() {
        given(repository.findById(1L)).willReturn(Optional.of(entity));
        given(transactionUtils.isUserAbleToApproveTransaction(any())).willReturn(false);
        entity.setCreatedBy("user1");

        assertThrows(IllegalArgumentException.class, () -> transactionService.approveTransaction(1L));
    }

    @Test
    void shouldThrowException_onChangeTransactionStatusBySameUserForReject() {
        given(repository.findById(1L)).willReturn(Optional.of(entity));
        given(transactionUtils.isUserAbleToRejectTransaction(any())).willReturn(false);
        entity.setCreatedBy("user1");

        assertThrows(IllegalArgumentException.class, () -> transactionService.rejectTransaction(1L));
    }

    @Test
    void shouldThrowException_onChangeTransactionStatusBySameUserForCancel() {
        given(repository.findById(1L)).willReturn(Optional.of(entity));
        given(transactionUtils.isUserAbleToCancelTransaction(any())).willReturn(false);
        entity.setCreatedBy("user1");

        assertThrows(IllegalArgumentException.class, () -> transactionService.cancelTransaction(1L));
    }
}
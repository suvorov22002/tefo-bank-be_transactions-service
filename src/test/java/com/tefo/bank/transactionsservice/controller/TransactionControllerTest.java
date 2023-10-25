package com.tefo.bank.transactionsservice.controller;

import com.tefo.bank.transactionsservice.RequestScopeImpl;
import com.tefo.bank.transactionsservice.dto.TransactionRequestDto;
import com.tefo.bank.transactionsservice.dto.TransactionResponseDto;
import com.tefo.bank.transactionsservice.mapper.JournalEntryMapper;
import com.tefo.bank.transactionsservice.mapper.TransactionMapper;
import com.tefo.bank.transactionsservice.model.JournalEntry;
import com.tefo.bank.transactionsservice.model.TransactionEntity;
import com.tefo.bank.transactionsservice.service.JournalEntryService;
import com.tefo.bank.transactionsservice.service.TransactionService;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static com.tefo.library.commonutils.constants.RestEndpoints.MANUAL_TRANSACTIONS_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@AutoConfigureJsonTesters
@ExtendWith(InstancioExtension.class)
@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private JournalEntryService journalEntryService;

    @MockBean
    private TransactionMapper transactionMapper;

    @MockBean
    private JournalEntryMapper journalEntryMapper;

    @MockBean
    private RequestScopeImpl requestScope;

    @Autowired
    private JacksonTester<TransactionRequestDto> transactionRequestDtoJacksonTester;

    @Autowired
    private JacksonTester<TransactionResponseDto> transactionResponseDtoJacksonTester;

    @Autowired
    private JacksonTester<List<TransactionResponseDto>> transactionResponseDtoListJacksonTester;

    TransactionRequestDto requestDto;
    TransactionResponseDto responseDto;
    TransactionEntity entity;

    @BeforeEach
    public void beforeEach() {
        requestDto = Instancio.create(TransactionRequestDto.class);
        entity = Instancio.create(TransactionEntity.class);
        entity.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        entity.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        entity.setEntryDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        entity.setProcessingDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        entity.setJournalEntries(Collections.emptyList());
        responseDto = Instancio.create(TransactionResponseDto.class);
        responseDto.setJournalEntries(Collections.emptyList());
        responseDto.setEntryDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        responseDto.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        responseDto.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        responseDto.setProcessingDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    void shouldAddNewTransaction() throws Exception {
        given(transactionMapper.toEntity(any())).willReturn(entity);
        given(transactionService.createManualTransaction(entity)).willReturn(entity);
        given(transactionMapper.toDto(entity)).willReturn(responseDto);

        MockHttpServletResponse response = mvc.perform(
                        post(MANUAL_TRANSACTIONS_URL)
                                .content(transactionRequestDtoJacksonTester.write(requestDto).getJson())
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEqualTo(transactionResponseDtoJacksonTester.write(responseDto).getJson());
    }

    @Test
    void shouldUpdateTransaction() throws Exception {
        Long id = 1L;

        given(transactionMapper.toEntity(any())).willReturn(entity);
        given(transactionService.update(id, entity)).willReturn(entity);
        given(transactionMapper.toDto(entity)).willReturn(responseDto);

        MockHttpServletResponse response = mvc.perform(
                        put(MANUAL_TRANSACTIONS_URL + "/" + id)
                                .content(transactionRequestDtoJacksonTester.write(requestDto).getJson())
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(transactionResponseDtoJacksonTester.write(responseDto).getJson());
    }

    @Test
    void shouldGetOneTransaction() throws Exception {
        Long id = 1L;

        given(transactionService.getOne(id)).willReturn(entity);
        given(transactionMapper.toDto(entity)).willReturn(responseDto);

        MockHttpServletResponse response = mvc.perform(
                        get(MANUAL_TRANSACTIONS_URL + "/" + id)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(transactionResponseDtoJacksonTester.write(responseDto).getJson());
    }

    @Test
    void shouldGetAllTransactions() throws Exception {
        List<TransactionEntity> entities = List.of(entity);

        given(transactionService.getAll()).willReturn(entities);
        given(transactionMapper.toDtoList(any())).willReturn(
                List.of(responseDto)
        );

        MockHttpServletResponse response = mvc.perform(
                        get(MANUAL_TRANSACTIONS_URL + "/all")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(transactionResponseDtoListJacksonTester.write(List.of(responseDto)).getJson());
    }

    @Test
    void shouldGetAllPaginatedTransactions() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TransactionEntity> entityPage = new PageImpl<>(List.of(entity));

        given(transactionService.getAllPaginated(pageable)).willReturn(entityPage);

        MockHttpServletResponse response = mvc.perform(
                        get(MANUAL_TRANSACTIONS_URL)
                                .param("page", "0")
                                .param("size", "10")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void shouldApproveTransaction() throws Exception {
        Long transactionId = 1L;
        doNothing().when(transactionService).approveTransaction(transactionId);

        MockHttpServletResponse response = mvc.perform(
                        put(MANUAL_TRANSACTIONS_URL + "/manage/approve/" + transactionId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(transactionService, times(1)).approveTransaction(transactionId);
    }

    @Test
    void shouldRejectTransaction() throws Exception {
        Long transactionId = 1L;
        doNothing().when(transactionService).rejectTransaction(transactionId);

        MockHttpServletResponse response = mvc.perform(
                        put(MANUAL_TRANSACTIONS_URL + "/manage/reject/" + transactionId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(transactionService, times(1)).rejectTransaction(transactionId);
    }

    @Test
    void shouldCancelTransaction() throws Exception {
        Long transactionId = 1L;
        doNothing().when(transactionService).cancelTransaction(transactionId);

        MockHttpServletResponse response = mvc.perform(
                        delete(MANUAL_TRANSACTIONS_URL + "/manage/cancel/" + transactionId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        verify(transactionService, times(1)).cancelTransaction(transactionId);
    }
}
package com.tefo.bank.transactionsservice.controller;

import com.tefo.bank.transactionsservice.RequestScopeImpl;
import com.tefo.bank.transactionsservice.dto.JournalEntryRequestDto;
import com.tefo.bank.transactionsservice.dto.JournalEntryResponseDto;
import com.tefo.bank.transactionsservice.mapper.JournalEntryMapper;
import com.tefo.bank.transactionsservice.model.JournalEntry;
import com.tefo.bank.transactionsservice.model.TransactionEntity;
import com.tefo.bank.transactionsservice.service.JournalEntryService;
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
import java.util.List;

import static com.tefo.library.commonutils.constants.RestEndpoints.JOURNAL_ENTRY_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@AutoConfigureJsonTesters
@ExtendWith(InstancioExtension.class)
@WebMvcTest(JournalEntryController.class)
public class JournalEntryControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private JournalEntryService journalEntryService;
    @MockBean
    private JournalEntryMapper journalEntryMapper;
    @MockBean
    private RequestScopeImpl requestScope;
    @Autowired
    private JacksonTester<JournalEntryRequestDto> journalEntryRequestDtoJacksonTester;

    @Autowired
    private JacksonTester<JournalEntryResponseDto> journalEntryResponseDtoJacksonTester;

    @Autowired
    private JacksonTester<List<JournalEntryResponseDto>> journalEntryResponseDtoListJacksonTester;

    JournalEntryRequestDto requestDto;
    JournalEntryResponseDto responseDto;
    JournalEntry entity;

    @BeforeEach
    public void beforeEach() {
        requestDto = Instancio.create(JournalEntryRequestDto.class);
        entity = Instancio.create(JournalEntry.class);
        entity.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        entity.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        entity.setTransaction(Instancio.create(TransactionEntity.class));

        responseDto = Instancio.create(JournalEntryResponseDto.class);
        responseDto.setEntryDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        responseDto.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        responseDto.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        responseDto.setProcessedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    void shouldGetAllJournalEntries() throws Exception {
        List<JournalEntry> entities = List.of(entity);

        given(journalEntryService.getAll()).willReturn(entities);
        given(journalEntryMapper.toDtoList(any())).willReturn(
                List.of(responseDto)
        );

        MockHttpServletResponse response = mvc.perform(
                        get(JOURNAL_ENTRY_URL + "/all")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(journalEntryResponseDtoListJacksonTester.write(List.of(responseDto)).getJson());
    }

    @Test
    void shouldGetAllJournalEntriesPaginated() throws Exception {
        Long transactionId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<JournalEntry> entityPage = new PageImpl<>(List.of(Instancio.create(JournalEntry.class)));

        given(journalEntryService.getAllByTransactionIdPaginated(transactionId, pageable)).willReturn(entityPage);

        MockHttpServletResponse response = mvc.perform(
                        get(JOURNAL_ENTRY_URL + "/" + transactionId)
                                .param("page", "0")
                                .param("size", "10")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void shouldGetAllJournalEntriesByTransactionId() throws Exception {
        List<JournalEntry> entities = List.of(entity);
        Long transactionId = 1L;
        given(journalEntryService.getAllByTransactionId(transactionId)).willReturn(entities);
        given(journalEntryMapper.toDtoList(any())).willReturn(
                List.of(responseDto)
        );

        MockHttpServletResponse response = mvc.perform(
                        get(JOURNAL_ENTRY_URL + "/all/" + transactionId)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(journalEntryResponseDtoListJacksonTester.write(List.of(responseDto)).getJson());
    }

    @Test
    void shouldDeleteJournalEntry() throws Exception {
        Long journalEntryId = 1L;
        doNothing().when(journalEntryService).deleteJournalEntry(journalEntryId);

        MockHttpServletResponse response = mvc.perform(
                        delete(JOURNAL_ENTRY_URL + "/" + journalEntryId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        verify(journalEntryService, times(1)).deleteJournalEntry(journalEntryId);
    }

    @Test
    void shouldUpdateTransaction() throws Exception {
        Long id = 1L;

        given(journalEntryMapper.toEntity(any())).willReturn(entity);
        given(journalEntryService.updateJournalEntry(id, entity)).willReturn(entity);
        given(journalEntryMapper.toDto(entity)).willReturn(responseDto);

        MockHttpServletResponse response = mvc.perform(
                        put(JOURNAL_ENTRY_URL + "/" + id)
                                .content(journalEntryRequestDtoJacksonTester.write(requestDto).getJson())
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(journalEntryResponseDtoJacksonTester.write(responseDto).getJson());
    }

    @Test
    void shouldGetAllPaginatedJournalEntries() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        Page<JournalEntry> entityPage = new PageImpl<>(List.of(entity));

        given(journalEntryService.getAllPaginated(pageable)).willReturn(entityPage);

        MockHttpServletResponse response = mvc.perform(
                        get(JOURNAL_ENTRY_URL)
                                .param("page", "0")
                                .param("size", "5")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void shouldDeleteJournalEntries() throws Exception {
        List<Long> journalEntryIds = List.of(1L, 2L);
        doNothing().when(journalEntryService).deleteJournalEntries(journalEntryIds);

        MockHttpServletResponse response = mvc.perform(
                        delete(JOURNAL_ENTRY_URL)
                                .param("journalEntryIds", "1", "2")
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        verify(journalEntryService, times(1)).deleteJournalEntries(journalEntryIds);
    }

    @Test
    void shouldGetPaginatedFilterByAccountNumber() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<JournalEntry> entityPage = new PageImpl<>(List.of(Instancio.create(JournalEntry.class)));

        String accountNumber = entity.getAccountNumber();
        given(journalEntryService.getAllPaginatedFilterByAccountNumber(accountNumber, pageable)).willReturn(entityPage);
        given(journalEntryMapper.toDtoList(any())).willReturn(List.of(responseDto));

        MockHttpServletResponse response = mvc.perform(
                        get(JOURNAL_ENTRY_URL)
                                .param("accountNumber", accountNumber)
                                .param("page", "0")
                                .param("size", "10")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }
}
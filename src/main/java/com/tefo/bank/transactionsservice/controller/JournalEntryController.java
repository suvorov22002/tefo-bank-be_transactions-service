package com.tefo.bank.transactionsservice.controller;

import com.tefo.bank.transactionsservice.dto.JournalEntryRequestDto;
import com.tefo.bank.transactionsservice.dto.JournalEntryResponseDto;
import com.tefo.bank.transactionsservice.mapper.JournalEntryMapper;
import com.tefo.bank.transactionsservice.service.JournalEntryService;
import com.tefo.library.commonutils.constants.RestEndpoints;
import com.tefo.library.commonutils.pagination.PageDto;
import com.tefo.library.commonutils.pagination.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(RestEndpoints.JOURNAL_ENTRY_URL)
public class JournalEntryController {

    private final JournalEntryService journalEntryService;
    private final JournalEntryMapper journalEntryMapper;

    @GetMapping
    public ResponseEntity<PageDto<JournalEntryResponseDto>> getAllPaginated(Pageable pageable) {
        return ResponseEntity.ok(
                PaginationUtils.convertEntityPageToDtoPage(journalEntryService.getAllPaginated(pageable), journalEntryMapper::toDtoList)
        );
    }

    @GetMapping("/all")
    public ResponseEntity<List<JournalEntryResponseDto>> getAll() {
        return ResponseEntity.ok(journalEntryMapper.toDtoList(journalEntryService.getAll()));
    }

    @GetMapping(params = "accountNumber")
    public ResponseEntity<PageDto<JournalEntryResponseDto>> getAllPaginatedFilterByAccountNumber(@RequestParam String accountNumber,
                                                                                                 Pageable pageable) {
        return ResponseEntity.ok(PaginationUtils.convertEntityPageToDtoPage(
                journalEntryService.getAllPaginatedFilterByAccountNumber(accountNumber, pageable), journalEntryMapper::toDtoList)
        );
    }

    @GetMapping("/{transactionId}")
    ResponseEntity<PageDto<JournalEntryResponseDto>> getAllJournalEntriesPaginatedByTransactionId(@PathVariable Long transactionId,
                                                                                                  Pageable pageable) {
        return ResponseEntity.ok(
                PaginationUtils.convertEntityPageToDtoPage(
                        journalEntryService.getAllByTransactionIdPaginated(transactionId, pageable), journalEntryMapper::toDtoList));
    }

    @GetMapping("/all/{transactionId}")
    ResponseEntity<List<JournalEntryResponseDto>> getAllJournalEntriesByTransactionId(@PathVariable Long transactionId) {
        return ResponseEntity.ok(journalEntryMapper.toDtoList(journalEntryService.getAllByTransactionId(transactionId)));
    }

    @PutMapping("/{journalEntryId}")
    ResponseEntity<JournalEntryResponseDto> updateJournalEntry(@PathVariable Long journalEntryId,
                                                               @RequestBody JournalEntryRequestDto dto) {
        return ResponseEntity.ok(journalEntryMapper.toDto(
                journalEntryService.updateJournalEntry(journalEntryId, journalEntryMapper.toEntity(dto)))
        );
    }

    @DeleteMapping("/{journalEntryId}")
    ResponseEntity<Void> deleteJournalEntry(@PathVariable Long journalEntryId) {
        journalEntryService.deleteJournalEntry(journalEntryId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    ResponseEntity<Void> deleteJournalEntries(@RequestParam List<Long> journalEntryIds) {
        journalEntryService.deleteJournalEntries(journalEntryIds);
        return ResponseEntity.noContent().build();
    }
}

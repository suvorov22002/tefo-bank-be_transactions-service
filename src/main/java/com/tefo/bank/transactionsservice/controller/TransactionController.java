package com.tefo.bank.transactionsservice.controller;

import com.tefo.bank.transactionsservice.dto.TransactionRequestDto;
import com.tefo.bank.transactionsservice.dto.TransactionResponseDto;
import com.tefo.bank.transactionsservice.mapper.TransactionMapper;
import com.tefo.bank.transactionsservice.service.TransactionService;
import com.tefo.library.commonutils.constants.RestEndpoints;
import com.tefo.library.commonutils.pagination.PageDto;
import com.tefo.library.commonutils.pagination.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(RestEndpoints.MANUAL_TRANSACTIONS_URL)
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @PostMapping
    ResponseEntity<TransactionResponseDto> addNewTransaction(@RequestBody TransactionRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionMapper.toDto(transactionService.createManualTransaction(transactionMapper.toEntity(dto))));
    }

    @PutMapping("/{id}")
    ResponseEntity<TransactionResponseDto> updateTransaction(@PathVariable Long id,
                                                             @RequestBody TransactionRequestDto dto) {
        return ResponseEntity.ok(transactionMapper.toDto(transactionService.update(id, transactionMapper.toEntity(dto))));
    }

    @GetMapping("/{id}")
    ResponseEntity<TransactionResponseDto> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(transactionMapper.toDto(transactionService.getOne(id)));
    }

    @GetMapping("/all")
    ResponseEntity<List<TransactionResponseDto>> getAll() {
        return ResponseEntity.ok(transactionMapper.toDtoList(transactionService.getAll()));
    }

    @GetMapping
    ResponseEntity<PageDto<TransactionResponseDto>> getAllPaginated(Pageable pageable) {
        return ResponseEntity.ok(
                PaginationUtils.convertEntityPageToDtoPage(
                        transactionService.getAllPaginated(pageable), transactionMapper::toDtoList));
    }

    @PutMapping("/manage/approve/{transactionId}")
    ResponseEntity<Void> approveTransaction(@PathVariable Long transactionId) {
        transactionService.approveTransaction(transactionId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/manage/reject/{transactionId}")
    ResponseEntity<Void> rejectTransaction(@PathVariable Long transactionId) {
        transactionService.rejectTransaction(transactionId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/manage/cancel/{transactionId}")
    ResponseEntity<Void> cancelTransaction(@PathVariable Long transactionId) {
        transactionService.cancelTransaction(transactionId);
        return ResponseEntity.noContent().build();
    }
}

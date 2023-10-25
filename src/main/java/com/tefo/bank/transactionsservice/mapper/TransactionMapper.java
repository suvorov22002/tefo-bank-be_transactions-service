package com.tefo.bank.transactionsservice.mapper;

import com.tefo.bank.transactionsservice.dto.TransactionRequestDto;
import com.tefo.bank.transactionsservice.dto.TransactionResponseDto;
import com.tefo.bank.transactionsservice.feignclient.DictionaryServiceClient;
import com.tefo.bank.transactionsservice.feignclient.dto.CurrencyBasicInfoDto;
import com.tefo.bank.transactionsservice.feignclient.dto.DictionaryValueResponseDto;
import com.tefo.bank.transactionsservice.model.TransactionEntity;
import com.tefo.library.commonutils.auth.RequestScope;
import com.tefo.library.commonutils.constants.SystemDictionaryConstants;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = JournalEntryMapper.class)
public abstract class TransactionMapper {

    @Autowired
    protected DictionaryServiceClient dictionaryServiceClient;

    @Autowired
    protected RequestScope requestScope;

    public abstract TransactionEntity toEntity(TransactionRequestDto dto);

    public abstract TransactionResponseDto toDto(TransactionEntity entity);

    public TransactionEntity update(@MappingTarget TransactionEntity target, TransactionEntity source) {
        if (source == null) {
            return target;
        }
        target.setUpdatedBy(requestScope.getCurrentUserId());
        target.setUpdatedAt(LocalDateTime.now());
        target.setCurrencyId(source.getCurrencyId());
        CurrencyBasicInfoDto currencyBasicInfoDto = dictionaryServiceClient.getCurrencyBasicInfoDtoById(target.getCurrencyId());
        target.setCurrencySymbol(currencyBasicInfoDto.getCurrencySymbol());
        target.setCurrencyAlphabeticCode(currencyBasicInfoDto.getCurrencyAlphabeticCode());
        target.setNotes(source.getNotes());
        target.setEntryDate(source.getEntryDate());
        return target;
    }

    public List<TransactionResponseDto> toDtoList(List<TransactionEntity> entityList) {
        if (entityList.isEmpty()) {
            return Collections.emptyList();
        }

        List<DictionaryValueResponseDto> allDictionaryValuesByCode = dictionaryServiceClient.getAllDictionaryValuesByCode(SystemDictionaryConstants.STATUS_OF_TRANSACTION_DICTIONARY_CODE);
        Map<Integer, String> collect = allDictionaryValuesByCode.stream().collect(Collectors.toMap(DictionaryValueResponseDto::getId, DictionaryValueResponseDto::getName));

        List<TransactionResponseDto> list = new ArrayList<>(entityList.size());
        for (TransactionEntity transactionEntity : entityList) {
            TransactionResponseDto dto = toDto(transactionEntity);
            dto.setTransactionStatusDictionaryValueName(collect.get(dto.getTransactionStatusDictionaryValueId().intValue()));
            list.add(dto);
        }

        return list;
    }
}

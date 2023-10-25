package com.tefo.bank.transactionsservice.mapper;

import com.tefo.bank.transactionsservice.dto.JournalEntryRequestDto;
import com.tefo.bank.transactionsservice.dto.JournalEntryResponseDto;
import com.tefo.bank.transactionsservice.feignclient.AccountServiceClient;
import com.tefo.bank.transactionsservice.feignclient.DictionaryServiceClient;
import com.tefo.bank.transactionsservice.feignclient.dto.AccountBasicInfo;
import com.tefo.bank.transactionsservice.feignclient.dto.CurrencyBasicInfoDto;
import com.tefo.bank.transactionsservice.feignclient.dto.DictionaryValueResponseDto;
import com.tefo.bank.transactionsservice.model.JournalEntry;
import com.tefo.library.commonutils.auth.RequestScope;
import com.tefo.library.commonutils.constants.SystemDictionaryConstants;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class JournalEntryMapper {

    @Autowired
    protected DictionaryServiceClient dictionaryServiceClient;

    @Autowired
    protected AccountServiceClient accountServiceClient;

    @Autowired
    protected RequestScope requestScope;

    public abstract JournalEntry toEntity(JournalEntryRequestDto dto);

    public JournalEntryResponseDto toDto(JournalEntry entity) {
        if (entity == null) {
            return null;
        }

        AccountBasicInfo accountBasicInfoDto
                = accountServiceClient.getAccountBasicInfoDto(entity.getAccountId());

        DictionaryValueResponseDto accountBalanceTypeDictionaryValue
                = dictionaryServiceClient.getDictionaryValueById(entity.getAccountBalanceTypeDictionaryValueId());

        DictionaryValueResponseDto transactionStatusDictionaryValue
                = dictionaryServiceClient.getDictionaryValueById(entity.getTransaction().getTransactionStatusDictionaryValueId().intValue());

        CurrencyBasicInfoDto currencyBasicInfoDtoById
                = dictionaryServiceClient.getCurrencyBasicInfoDtoById(entity.getTransaction().getCurrencyId());

        JournalEntryResponseDto responseDto = new JournalEntryResponseDto();
        responseDto.setId(entity.getId());
        responseDto.setAccountId(entity.getAccountId());
        responseDto.setAccountNumber(entity.getAccountNumber());
        responseDto.setAccountName(accountBasicInfoDto.getName());
        responseDto.setAccountBalanceTypeDictionaryValueId(entity.getAccountBalanceTypeDictionaryValueId());
        responseDto.setAccountBalanceTypeDictionaryValueName(accountBalanceTypeDictionaryValue.getName());
        responseDto.setCurrencyId(entity.getTransaction().getCurrencyId());
        responseDto.setCurrencyAlphabeticCode(currencyBasicInfoDtoById.getCurrencyAlphabeticCode());
        responseDto.setCurrencySymbol(currencyBasicInfoDtoById.getCurrencySymbol());
        responseDto.setTransactionNumber(entity.getTransaction().getNumber());
        responseDto.setTransactionStatusDictionaryValueId(entity.getTransaction().getTransactionStatusDictionaryValueId().intValue());
        responseDto.setTransactionStatusName(transactionStatusDictionaryValue.getName());
        responseDto.setEntryDate(entity.getTransaction().getEntryDate());
        responseDto.setProcessedAt(entity.getTransaction().getProcessingDate());
        responseDto.setCreatedAt(entity.getCreatedAt());
        responseDto.setUpdatedBy(entity.getUpdatedBy());
        responseDto.setUpdatedAt(entity.getUpdatedAt());
        if(responseDto.getAccountBalanceTypeDictionaryValueId().equals(SystemDictionaryConstants.DEBIT_ACCOUNT_BALANCE_TYPE_DICTIONARY_VALUE_ID)) {
            responseDto.setDebit(entity.getOriginalCurrencyAmount());
            responseDto.setCredit(BigDecimal.ZERO);
        } else {
            responseDto.setCredit(entity.getOriginalCurrencyAmount());
            responseDto.setDebit(BigDecimal.ZERO);
        }
        return responseDto;
    }

    public JournalEntry update(@MappingTarget JournalEntry target, JournalEntry source) {
        if (source == null) {
            return target;
        }
        target.setUpdatedBy(requestScope.getCurrentUserId());
        target.setUpdatedAt(LocalDateTime.now());
        target.setAccountBalanceTypeDictionaryValueId(source.getAccountBalanceTypeDictionaryValueId());
        target.setOriginalCurrencyAmount(target.getOriginalCurrencyAmount());
        target.setAccountId(source.getAccountId());
        target.setAccountNumber(source.getAccountNumber());
        return target;
    }

    public abstract List<JournalEntry> toEntityList(List<JournalEntryRequestDto> dtoList);

    public List<JournalEntryResponseDto> toDtoList(List<JournalEntry> entityList) {
        if (entityList.isEmpty()) {
            return Collections.emptyList();
        }

        List<JournalEntryResponseDto> list = new ArrayList<>(entityList.size());

        List<DictionaryValueResponseDto> allDictionaryValuesAccountBalanceType = dictionaryServiceClient.getAllDictionaryValuesByCode(SystemDictionaryConstants.ACCOUNT_BALANCE_TYPE_DICTIONARY_CODE);
        List<DictionaryValueResponseDto> allDictionaryValuesTransactionStatus = dictionaryServiceClient.getAllDictionaryValuesByCode(SystemDictionaryConstants.STATUS_OF_TRANSACTION_DICTIONARY_CODE);
        List<CurrencyBasicInfoDto> allCurrencyBasicInfoDtos = dictionaryServiceClient.getAllCurrencyBasicInfoDtos();
        List<AccountBasicInfo> allAccountBasicInfoDtos = accountServiceClient.getAllAccountBasicInfoDtos();

        Map<Integer, String> dictionaryValuesAccountBalanceTypeCollect = allDictionaryValuesAccountBalanceType.stream().collect(Collectors.toMap(DictionaryValueResponseDto::getId, DictionaryValueResponseDto::getName));
        Map<Integer, String> dictionaryValuesTransactionStatusCollect = allDictionaryValuesTransactionStatus.stream().collect(Collectors.toMap(DictionaryValueResponseDto::getId, DictionaryValueResponseDto::getName));
        Map<Long, CurrencyBasicInfoDto> currenciesBasicInfoById = allCurrencyBasicInfoDtos.stream().collect(Collectors.toMap(CurrencyBasicInfoDto::getId, Function.identity()));
        Map<Long, AccountBasicInfo> accountsBasicInfoCollect = allAccountBasicInfoDtos.stream().collect(Collectors.toMap(AccountBasicInfo::getId, Function.identity()));

        for (JournalEntry entity : entityList) {
            JournalEntryResponseDto dto = new JournalEntryResponseDto();

            AccountBasicInfo accountBasicInfo = accountsBasicInfoCollect.get(entity.getAccountId());
            CurrencyBasicInfoDto currencyBasicInfoDto = currenciesBasicInfoById.get(entity.getTransaction().getCurrencyId());

            dto.setId(entity.getId());
            dto.setAccountId(entity.getAccountId());
            dto.setAccountBalanceTypeDictionaryValueId(entity.getAccountBalanceTypeDictionaryValueId());
            dto.setAccountBalanceTypeDictionaryValueName(dictionaryValuesAccountBalanceTypeCollect.get(dto.getAccountBalanceTypeDictionaryValueId()));
            dto.setAccountName(accountBasicInfo.getName());
            dto.setAccountNumber(accountBasicInfo.getNumber());
            dto.setCurrencyId(entity.getTransaction().getCurrencyId());
            dto.setCurrencyAlphabeticCode(currencyBasicInfoDto.getCurrencyAlphabeticCode());
            dto.setCurrencySymbol(currencyBasicInfoDto.getCurrencySymbol());
            dto.setEntryDate(entity.getTransaction().getEntryDate());
            dto.setTransactionNumber(entity.getTransaction().getNumber());
            dto.setTransactionStatusDictionaryValueId(entity.getTransaction().getTransactionStatusDictionaryValueId().intValue());
            dto.setTransactionStatusName(dictionaryValuesTransactionStatusCollect.get(dto.getTransactionStatusDictionaryValueId()));
            dto.setProcessedAt(entity.getTransaction().getProcessingDate());
            if(dto.getAccountBalanceTypeDictionaryValueId().equals(SystemDictionaryConstants.DEBIT_ACCOUNT_BALANCE_TYPE_DICTIONARY_VALUE_ID)) {
                dto.setDebit(entity.getOriginalCurrencyAmount());
                dto.setCredit(BigDecimal.ZERO);
            } else {
                dto.setCredit(entity.getOriginalCurrencyAmount());
                dto.setDebit(BigDecimal.ZERO);
            }
            dto.setCreatedAt(entity.getCreatedAt());
            dto.setUpdatedAt(entity.getUpdatedAt());
            dto.setUpdatedBy(entity.getUpdatedBy());
            list.add(dto);
        }

        return list;
    }
}

package com.tefo.bank.transactionsservice.utils;

import com.tefo.bank.transactionsservice.feignclient.AccountServiceClient;
import com.tefo.bank.transactionsservice.feignclient.CoreSettingsServiceClient;
import com.tefo.bank.transactionsservice.feignclient.DictionaryServiceClient;
import com.tefo.bank.transactionsservice.feignclient.IdentityServiceClient;
import com.tefo.bank.transactionsservice.feignclient.dto.*;
import com.tefo.bank.transactionsservice.model.JournalEntry;
import com.tefo.bank.transactionsservice.model.TransactionEntity;
import com.tefo.library.commonutils.auth.RequestScope;
import com.tefo.library.commonutils.constants.ExceptionMessages;
import com.tefo.library.commonutils.constants.PermissionCodes;
import com.tefo.library.commonutils.constants.SystemDictionaryConstants;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.tefo.library.commonutils.constants.SystemDictionaryConstants.CREDIT_ACCOUNT_BALANCE_TYPE_DICTIONARY_VALUE_ID;
import static com.tefo.library.commonutils.constants.SystemDictionaryConstants.DEBIT_ACCOUNT_BALANCE_TYPE_DICTIONARY_VALUE_ID;

@Service
@RequiredArgsConstructor
public class TransactionUtils {

    private final CoreSettingsServiceClient coreSettingsServiceClient;
    private final DictionaryServiceClient dictionaryServiceClient;
    private final RequestScope requestScope;
    private final IdentityServiceClient identityServiceClient;
    private final AccountServiceClient accountServiceClient;

    private static final String OPEN_BUSINESS_DAY_DATE_KEY = "date";
    private static final Integer NUMBER_OF_DEFAULT_CREDIT_JE = 1;
    private static final Integer NUMBER_OF_DEFAULT_DEBIT_JE = 1;

    public String generateTransactionNumber(LocalDateTime transactionCreatedAt) {
        TransactionSettingsDTO transactionSettings = coreSettingsServiceClient.getTransactionSettings();

        int length = transactionSettings.getTransactionNumberLength();
        boolean useLetters = true;
        boolean useNumbers = true;

        int day = transactionCreatedAt.getDayOfMonth();
        int month = transactionCreatedAt.getMonthValue();
        int year = transactionCreatedAt.getYear() % 100;
        int hour = transactionCreatedAt.getHour();
        int minute = transactionCreatedAt.getMinute();
        int second = transactionCreatedAt.getSecond();

        String dateTimeString = String.format("%02d%02d%02d%02d%02d%02d", day, month, year, hour, minute, second);

        long num = Long.parseLong(dateTimeString);

        String base36 = Long.toString(num, 36);

        String randomSymbols = RandomStringUtils.random(length, useLetters, useNumbers);

        return base36 + randomSymbols;
    }


    public CurrencyBasicInfoDto getCurrencyBasicInfoDtoById(Long id) {
        return dictionaryServiceClient.getCurrencyBasicInfoDtoById(id);
    }

    public LocalDateTime getEntryDateForTransactionFromCurrentBusinessDay() {
        Map<String, LocalDateTime> openBusinessDayDate = coreSettingsServiceClient.getOpenBusinessDayDate();
        return Optional.ofNullable(openBusinessDayDate.get(OPEN_BUSINESS_DAY_DATE_KEY))
                .orElseThrow(() -> new UnsupportedOperationException(ExceptionMessages.TRANSACTION_CREATION_FAILED_BUSINESS_DAY));
    }

    public void calculateTransaction(TransactionEntity transaction) {
        List<JournalEntry> journalEntries = transaction.getJournalEntries();

        List<JournalEntry> debitJournalEntries = new ArrayList<>();
        List<JournalEntry> creditJournalEntries = new ArrayList<>();

        journalEntries.forEach(journalEntry -> {
            Integer accountBalanceTypeDictionaryValueId = journalEntry.getAccountBalanceTypeDictionaryValueId();
            if (accountBalanceTypeDictionaryValueId.equals(CREDIT_ACCOUNT_BALANCE_TYPE_DICTIONARY_VALUE_ID)) {
                creditJournalEntries.add(journalEntry);
            }
            if (accountBalanceTypeDictionaryValueId.equals(DEBIT_ACCOUNT_BALANCE_TYPE_DICTIONARY_VALUE_ID)) {
                debitJournalEntries.add(journalEntry);
            }
        });

        checkIfDefaultJournalEntriesPresent(debitJournalEntries, creditJournalEntries);

        BigDecimal debit = debitJournalEntries.stream()
                .map(JournalEntry::getOriginalCurrencyAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal credit = creditJournalEntries.stream()
                .map(JournalEntry::getOriginalCurrencyAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (!credit.equals(debit)) {
            throw new IllegalArgumentException(ExceptionMessages.JOURNAL_ENTRY_IS_NOT_BALANCED);
        }

        transaction.setOriginalCurrencyAmount(debit);
        convertToReferenceCurrency(transaction);
    }

    private void checkIfDefaultJournalEntriesPresent(List<JournalEntry> debitJournalEntries,
                                                     List<JournalEntry> creditJournalEntries) {

        if (debitJournalEntries.size() < NUMBER_OF_DEFAULT_DEBIT_JE) {
            throw new IllegalArgumentException(ExceptionMessages.TRANSACTION_DEBIT_JOURNAL_ENTRY_MISSING);
        }

        if (creditJournalEntries.size() < NUMBER_OF_DEFAULT_CREDIT_JE) {
            throw new IllegalArgumentException(ExceptionMessages.TRANSACTION_CREDIT_JOURNAL_ENTRY_MISSING);
        }
    }

    public void convertToReferenceCurrency(TransactionEntity transaction) {
        List<JournalEntry> journalEntries = transaction.getJournalEntries();
        for (JournalEntry je : journalEntries) {

            Long accountId = je.getAccountId();
            AccountBasicInfo accountBasicInfo = accountServiceClient.getAccountBasicInfoDto(accountId);

            String currencyId = accountBasicInfo.getCurrencyId();
            CurrencyBasicInfoDto currencyDtoById = dictionaryServiceClient.getCurrencyBasicInfoDtoById(Long.parseLong(currencyId));
            Boolean isReferenceCurrency = currencyDtoById.getIsReferenceCurrency();

            BigDecimal originalCurrencyAmount = je.getOriginalCurrencyAmount();

            if (isReferenceCurrency) {
                je.setReferenceCurrencyAmount(originalCurrencyAmount);
                return;
            }

            CurrencyResponseDto referenceCurrency = dictionaryServiceClient.getReferenceCurrency();
            ExchangeRateResponseDto exchangeRate
                    = dictionaryServiceClient.getExchangeRateForCurrencyConversion(transaction.getCurrencyId(), referenceCurrency.getId());
            double midRate = exchangeRate.getMidRate();
            Integer multiplier = exchangeRate.getMultiplier();

            BigDecimal convertedAmount = originalCurrencyAmount.multiply(BigDecimal.valueOf(midRate / multiplier));

            je.setReferenceCurrencyAmount(convertedAmount);
        }

        transaction.setReferenceCurrencyAmount(transaction.getJournalEntries().stream()
                .map(JournalEntry::getReferenceCurrencyAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
    }

    public boolean isTransactionCreatedByLoggedUser(TransactionEntity transaction) {
        String currentUserId = requestScope.getCurrentUserId();
        return transaction.getCreatedBy().equals(currentUserId);
    }

    public boolean isTransactionInStatus(TransactionEntity transaction, Long transactionStatusDictionaryValueId) {
        return transaction.getTransactionStatusDictionaryValueId().equals(transactionStatusDictionaryValueId);
    }

    public boolean checkIfLoggedUserHasPermission(String permissionCode) {
        String currentUserId = requestScope.getCurrentUserId();
        UserBasicInfoDto userBasicInfoById = identityServiceClient.getUserBasicInfoById(currentUserId);
        Set<String> permissionCodes = userBasicInfoById.getPermissions().stream()
                .map(UserPermissionBasicDto::getCode)
                .collect(Collectors.toSet());
        return permissionCodes.contains(permissionCode);
    }

    public boolean isUserAbleToCancelTransaction(TransactionEntity transactionToCancel) {
        if (isTransactionCreatedByLoggedUser(transactionToCancel) && checkIfLoggedUserHasPermission(PermissionCodes.EDIT_TRANSACTION)
                && isTransactionInStatus(transactionToCancel, SystemDictionaryConstants.INITIAL_STATUS_TRANSACTION_DICTIONARY_VALUE_ID.longValue())) {
            return true;
        } else
            return !isTransactionCreatedByLoggedUser(transactionToCancel) && checkIfLoggedUserHasPermission(PermissionCodes.APPROVE_TRANSACTION)
                    && isTransactionInStatus(transactionToCancel, SystemDictionaryConstants.PENDING_STATUS_TRANSACTION_DICTIONARY_VALUE_ID.longValue());
    }

    public boolean isUserAbleToRejectTransaction(TransactionEntity transactionToReject) {
        return !isTransactionCreatedByLoggedUser(transactionToReject)
                && isTransactionInStatus(transactionToReject, SystemDictionaryConstants.INITIAL_STATUS_TRANSACTION_DICTIONARY_VALUE_ID.longValue());
    }

    public boolean isUserAbleToEditTransaction(TransactionEntity transactionToEdit) {
        return isTransactionCreatedByLoggedUser(transactionToEdit)
                && isTransactionInStatus(transactionToEdit, SystemDictionaryConstants.INITIAL_STATUS_TRANSACTION_DICTIONARY_VALUE_ID.longValue());
    }

    public boolean isUserAbleToApproveTransaction(TransactionEntity transactionToApprove) {
        return !isTransactionCreatedByLoggedUser(transactionToApprove)
                && isTransactionInStatus(transactionToApprove, SystemDictionaryConstants.INITIAL_STATUS_TRANSACTION_DICTIONARY_VALUE_ID.longValue());
    }

    public boolean isUserAbleToAdjustTransaction(TransactionEntity transactionToAdjust) {
        return isTransactionInStatus(transactionToAdjust, SystemDictionaryConstants.COMPLETED_STATUS_TRANSACTION_DICTIONARY_VALUE_ID.longValue());
    }

    public boolean isUserAbleToDeleteTransaction(TransactionEntity transactionToDelete) {
        return isTransactionInStatus(transactionToDelete, SystemDictionaryConstants.DELETED_STATUS_TRANSACTION_DICTIONARY_VALUE_ID.longValue());
    }
}

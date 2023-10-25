package com.tefo.bank.transactionsservice.feignclient;

import com.tefo.bank.transactionsservice.feignclient.dto.TransactionSettingsDTO;
import com.tefo.library.commonutils.constants.RestEndpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDateTime;
import java.util.Map;

@FeignClient(name = "coreSettingsServiceClient", url = "http://" + "${core.settings.service.host}" + RestEndpoints.CORE_SETTINGS_BASE)
public interface CoreSettingsServiceClient {

    @RequestMapping(method = RequestMethod.GET, value = "bank-settings/transactions")
    TransactionSettingsDTO getTransactionSettings();

    @RequestMapping(method = RequestMethod.GET, value = "business-day" + RestEndpoints.OPEN_BUSINESS_DAY)
    Map<String, LocalDateTime> getOpenBusinessDayDate();
}

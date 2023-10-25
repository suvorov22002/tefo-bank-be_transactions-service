package com.tefo.bank.transactionsservice.feignclient;

import com.tefo.bank.transactionsservice.feignclient.dto.CurrencyBasicInfoDto;
import com.tefo.bank.transactionsservice.feignclient.dto.CurrencyResponseDto;
import com.tefo.bank.transactionsservice.feignclient.dto.DictionaryValueResponseDto;
import com.tefo.bank.transactionsservice.feignclient.dto.ExchangeRateResponseDto;
import com.tefo.library.commonutils.constants.RestEndpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "dictionaryService", url = "http://" + "${dictionary.service.host}" + RestEndpoints.DICTIONARY_BASE_URL)
public interface DictionaryServiceClient {

    @RequestMapping(method = RequestMethod.GET, value = "dictionaries/values", params = "id")
    DictionaryValueResponseDto getDictionaryValueById(@RequestParam Integer id);

    @RequestMapping(method = RequestMethod.GET, value = "currencies/basic-info", params = "id")
    CurrencyBasicInfoDto getCurrencyBasicInfoDtoById(@RequestParam Long id);

    @RequestMapping(method = RequestMethod.GET, value = "dictionaries/values/all", params = "dictionaryCode")
    List<DictionaryValueResponseDto> getAllDictionaryValuesByCode(@RequestParam String dictionaryCode);

    @RequestMapping(method = RequestMethod.GET, value = "currencies/reference-currency")
    CurrencyResponseDto getReferenceCurrency();

    @RequestMapping(method = RequestMethod.GET, value = "exchange-rates/currency-conversion", params = {"baseCurrencyId", "quoteCurrencyId"})
    ExchangeRateResponseDto getExchangeRateForCurrencyConversion(@RequestParam Long baseCurrencyId,
                                                                 @RequestParam Long quoteCurrencyId);

    @RequestMapping(method = RequestMethod.GET, value = "currencies/all/basic-info")
    List<CurrencyBasicInfoDto> getAllCurrencyBasicInfoDtos();

}

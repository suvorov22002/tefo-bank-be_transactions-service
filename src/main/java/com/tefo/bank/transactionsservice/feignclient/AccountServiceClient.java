package com.tefo.bank.transactionsservice.feignclient;

import com.tefo.bank.transactionsservice.feignclient.dto.AccountBasicInfo;
import com.tefo.library.commonutils.constants.RestEndpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "accountServiceClient", url = "http://" + "${account.service.host}" + RestEndpoints.ACCOUNT_BASE_URL)
public interface AccountServiceClient {

    @RequestMapping(method = RequestMethod.GET, value = "accounts/for-transactions", params = "currencyId")
    List<AccountBasicInfo> getAccountsForTransactions(@RequestParam String currencyId);

    @RequestMapping(method = RequestMethod.GET, value = "accounts/basic-info/{id}", params = "id")
    AccountBasicInfo getAccountBasicInfoDto(@PathVariable Long id);

    @RequestMapping(method = RequestMethod.GET, value = "accounts/all/basic-info")
    List<AccountBasicInfo> getAllAccountBasicInfoDtos();
}

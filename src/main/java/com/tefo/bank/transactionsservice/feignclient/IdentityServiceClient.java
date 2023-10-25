package com.tefo.bank.transactionsservice.feignclient;

import com.tefo.bank.transactionsservice.feignclient.dto.UserBasicInfoDto;
import com.tefo.library.commonutils.constants.RestEndpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "identityService", url = "http://" + "${identity.service.host}" + RestEndpoints.IDENTITY_SERVICE_BASE)
public interface IdentityServiceClient {

    @RequestMapping(method = RequestMethod.GET, value = "users/{id}/basic-info", params = "id")
    UserBasicInfoDto getUserBasicInfoById(@PathVariable String id);
}

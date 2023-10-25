package com.tefo.bank.transactionsservice.feignclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tefo.library.commonutils.exception.EntityNotFoundException;
import com.tefo.library.commonutils.exception.utils.ExceptionDetails;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.io.InputStream;

public class FeignClientErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        ExceptionDetails exceptionDetails;
        try (InputStream body = response.body().asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            exceptionDetails = mapper.readValue(body, ExceptionDetails.class);
        } catch (IOException e) {
            return new Exception(e.getMessage());
        }

        if (response.status() == 404) {
            return new EntityNotFoundException(
                    exceptionDetails.getMessage() != null ? exceptionDetails.getMessage() : "Not found");
        }
        return new Exception();
    }
}

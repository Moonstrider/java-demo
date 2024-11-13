package com.java.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Robert
 */
@Slf4j
@RestController
@RequestMapping("/volvo-id/sms")
public class SmsController {

    private final VolvoIdSmsService volvoIdSmsService;

    public SmsController(VolvoIdSmsService volvoIdSmsService) {
        this.volvoIdSmsService = volvoIdSmsService;
    }

    @PostMapping("/messages")
    public <T> ResponseEntity<T> sendSms(
        @RequestHeader(TRACE_ID) String traceId,
        @RequestHeader(V_SIGNATURE) String signature,
        @RequestHeader(URI) String uri,
        @Validated @RequestBody SmsMessage<T> smsMessage) {
        log.info("sendSms params:{}", smsMessage);// use aop logging
        return volvoIdSmsService.sendSms(traceId, signature, uri, smsMessage);
    }
}

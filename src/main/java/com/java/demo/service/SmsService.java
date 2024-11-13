package com.java.demo.service;

import com.java.demo.aop.wrapper.ResponseWrapper;
import java.util.LinkedHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @author Robert
 */
@Slf4j
@Service
public class SmsService {

    private final MessageCenterFeignClient messageCenterFeignClient;

    public SmsService(MessageCenterFeignClient messageCenterFeignClient) {
        this.messageCenterFeignClient = messageCenterFeignClient;
    }

    public<T> ResponseEntity<T> sendSms(String traceId, String signature, String uri, SmsMessage<T> smsMessage) {
        // Verify template name existed or not
        ValidationUtil.mapSmsTemplateName(smsMessage.getTemplateName());
        TemplateIdentifierEnum identifier = ValidationUtil.validateTemplateParameter(
                smsMessage.getTemplateName(), (LinkedHashMap<String, Object>) smsMessage.getTemplateParameters());
        if(LINK == identifier) {
            verifySmsMessageLink(smsMessage);
        }

		CommonResponse<MessageResult> commonResponse = messageCenterFeignClient.sendSms(VOLVO_ID.getValue(), traceId,
																						signature, uri, smsMessage);
        log.info("message-center send sms response: {}", commonResponse);// use aop logging

        return ResponseWrapper.smsMessageResponseWrapper(commonResponse);
    }

    private<T, K, V> void verifySmsMessageLink(SmsMessage<T> smsMessage) {
        LinkedHashMap<K, V> templateParameter = (LinkedHashMap) smsMessage.getTemplateParameters();
        var link = (String) templateParameter.getOrDefault("link", null);
        var query = link.substring(link.indexOf("?") + 1);
        if (StringUtils.isBlank(query)) {
            throwInvalidTemplateException("Query missing", "No query data");
        }
    }
}

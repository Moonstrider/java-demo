package com.java.demo.service;

import static com.mytechathon.vadaptor.enums.HeaderEnum.VOLVO_ID;

import com.mytechathon.vadaptor.aop.context.request.RequestContextUtils;
import com.mytechathon.vadaptor.aop.mapper.EmailMessageMapper;
import com.mytechathon.vadaptor.aop.wrapper.ResponseMapper;
import com.mytechathon.vadaptor.domain.vo.email.EmailMessage;
import com.mytechathon.vadaptor.service.feign.MessageCenterFeignClient;
import com.mytechathon.vadaptor.service.feign.dto.EmailMessageDTO;
import com.mytechathon.vadaptor.service.feign.dto.EmailResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @author Robert
 */
@Slf4j
@Service
public class EmailService {

    private final MessageCenterFeignClient messageCenterFeignClient;
    private final EmailMessageMapper emailMessageMapper;

    public EmailService(MessageCenterFeignClient messageCenterFeignClient,
        EmailMessageMapper emailMessageMapper
    ) {
        this.messageCenterFeignClient = messageCenterFeignClient;
        this.emailMessageMapper = emailMessageMapper;
    }

    public <T> ResponseEntity<T> sendEmail(EmailMessage<?> emailMessage) {

        EmailMessageDTO dto = emailMessageMapper.voToDto(emailMessage);
        log.info("sendEmail dto: " + dto);
        ResponseEntity<EmailResponseDTO> response = messageCenterFeignClient.sendEmail(
                VOLVO_ID.getValue(),
                RequestContextUtils.correlationId(),
                RequestContextUtils.signature(),
                RequestContextUtils.uri(),
                dto);
        log.info("sendEmail response: " + response);
        return ResponseMapper.emailResponseWrapper(response);
    }
}

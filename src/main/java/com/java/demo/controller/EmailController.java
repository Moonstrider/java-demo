package com.java.demo.controller;

import com.java.demo.service.EmailService;
import com.mytechathon.vadaptor.common.AdapterResponse;
import com.mytechathon.vadaptor.domain.vo.email.EmailMessage;
import com.mytechathon.vadaptor.service.VolvoIdEmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Robert
 */
@Slf4j
@RestController
@RequestMapping("/volvo-id/email")
public class EmailController {

    private final EmailService volvoIdEmailService;

    public EmailController(EmailService volvoIdEmailService) {
        this.volvoIdEmailService = volvoIdEmailService;
    }

    @PostMapping("/messages")
    @Operation(summary = "send email", operationId = "sendEmail",
            responses = {@ApiResponse(
                    responseCode = "200", description = "Successfully send email",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AdapterResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Error occurred while sending email")})
    public <T> ResponseEntity<T> sendEmail(@Validated @RequestBody EmailMessage<T> emailMessage) {
        log.info("sendEmail emailMessage:{}", emailMessage);
        return volvoIdEmailService.sendEmail(emailMessage);
    }
}

package com.java.demo.aop.mapper;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailMessageMapper {

    @Value("${email.from}")
    private String from;
    @Value("${email.fromName}")
    private String fromName;
    @Value("${email.subject}")
    private String subject;

    public <T> EmailMessageDTO voToDto(EmailMessage<T> source) {
        if (source == null) {
            return null;
        }
        EmailMessageDTO dto = new EmailMessageDTO();
        dto.setTo(source.getTo());

        LinkedHashMap<String, String> templateParameters =
            (LinkedHashMap<String, String>) source.getTemplateParameters();

        String mappedTemplateName = mapEmailTemplateName(source.getTemplateName());
        TemplateIdentifierEnum templateIdentifier = validateTemplateParameterForEmail(
            source.getTemplateName(), templateParameters);

        setHtml(mappedTemplateName, dto, templateIdentifier, templateParameters);

        dto.setFrom(from);
        dto.setFromName(fromName);
        dto.setSubject(subject);
        return dto;
    }

    private <K, V> void setHtml(String templateName, EmailMessageDTO dto,
        TemplateIdentifierEnum templateIdentifier, Map<K, V> templateParameters) {

        // get html by template name
        // if there is a placeholder, replace it
        String html = templateMap.get(templateName.toLowerCase(Locale.ROOT));

        if (OTP_CODE == templateIdentifier) {
            String otpCode = (String) templateParameters.getOrDefault("otpCode", null);
            String replaced = StringUtils.replace(html, "{{otpCode}}", otpCode);
            dto.setHtml(replaced);
        } else if (LINK == templateIdentifier) {
            String link = (String) templateParameters.getOrDefault("link", null);
            String replaced = StringUtils.replace(html, "{{link}}", link);
            dto.setHtml(replaced);
        } else {
            // if it's OTHER, it's a template with no placeholder
            dto.setHtml(html);
        }
    }
}

package com.java.demo.aop.wrapper;


import com.adaptor.aop.context.request.RequestContextUtils;
import com.adaptor.common.AdapterResponse;
import com.adaptor.common.AdapterResponseDetail;
import com.adaptor.enums.AdapterResponseStatusEnum;
import com.adaptor.enums.MessageStatusEnum;
import com.adaptor.enums.SmsMessageFieldEnum;
import com.adaptor.service.feign.dto.CommonResponse;
import com.adaptor.service.feign.dto.EmailResponseDTO;
import com.adaptor.service.feign.dto.MessageResult;
import com.adaptor.utils.DateUtil;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author zwu73
 */
@Slf4j
@UtilityClass
public class ResponseWrapper {

    public static<T> ResponseEntity<T> smsMessageResponseWrapper(CommonResponse<MessageResult> res) {
        String statusCode = res.getCode();
        String timeStamp = "";
        String messageId = "";
        if (null != res.getData()) {
            var data = res.getData();
            timeStamp = data.getTimeStamp();
            messageId = data.getMessageId();
        }
        return generateResponseEntity(statusCode, messageId, timeStamp);
    }

    private static<T> ResponseEntity<T> generateResponseEntity(String statusCode, String messageId, String timeStamp) {
        return switch (statusCode) {
            case "400" -> buildAdapterResponse(AdapterResponseStatusEnum.REQUEST_INVALID, null);
            case "401" -> buildAdapterResponse(AdapterResponseStatusEnum.INVALID_SIG, null);
            case "415" -> buildAdapterResponse(AdapterResponseStatusEnum.UNSUPPORTED_MEDIA_TYPE, null);
            case "429" -> buildAdapterResponse(AdapterResponseStatusEnum.TOO_MANY_REQUESTS, null);
            case "500" -> buildAdapterResponse(AdapterResponseStatusEnum.INTERNAL_SERVER_ERROR, null);
            case "502" -> buildAdapterResponse(BAD_GATEWAY_ERROR, new AdapterResponseDetail(messageId));
            case "504" -> buildAdapterResponse(AdapterResponseStatusEnum.REQUEST_TIMEOUT_ERROR, null);
            case "200" -> buildAdapterResponse(MessageStatusEnum.DONE.getName(), messageId, timeStamp);
            default -> buildAdapterResponse(MessageStatusEnum.FAILED.getName(),
                    "No related messageId", DateUtil.createCurrentUTCDate());
        };
    }

    private static<T> ResponseEntity<T> buildAdapterResponse(String status, String messageId, String timeStamp) {
        AdapterResponse smsAdapterResponse = new AdapterResponse();
        smsAdapterResponse.setStatus(status);
        smsAdapterResponse.setMessageId(messageId);
        smsAdapterResponse.setCreatedAt(timeStamp);
        smsAdapterResponse.setUpdatedAt(timeStamp);
        HttpHeaders header = new HttpHeaders();
        header.add(VCC_API_OPERATION_ID.getValue(), RequestContextUtils.correlationId());
        return new ResponseEntity<>((T)smsAdapterResponse, header, HttpStatus.OK);
    }

    public static<T> ResponseEntity<T> buildAdapterResponse(
            AdapterResponseStatusEnum adapterResponseStatusEnum, AdapterResponseDetail detail) {
        AdapterResponse adapterResponse = new AdapterResponse();
        adapterResponse.setCode(adapterResponseStatusEnum);
        adapterResponse.setMessage(adapterResponseStatusEnum.getMessage());
        if (detail != null && detail.getMessage() != null) {
            adapterResponse.setDetails(List.of(detail));
        }
        HttpHeaders header = new HttpHeaders();
        header.add(VCC_API_OPERATION_ID.getValue(), RequestContextUtils.correlationId());
        return new ResponseEntity<>((T) adapterResponse, header, HttpStatus.valueOf(adapterResponseStatusEnum.getStatusCode()));
    }

    public static<T> ResponseEntity<T> emailResponseWrapper(ResponseEntity<EmailResponseDTO> res) {
        String statusCode = String.valueOf(res.getStatusCode().value());
        String timeStamp = "";
        String messageId = "";
        if (null != res.getBody()) {
            EmailResponseDTO body = res.getBody();
            timeStamp = DateUtil.createCurrentUTCDate();
            messageId = body.getEmailId();
        } else {
            log.error("send email error:{}", res);
            statusCode = String.valueOf(BAD_GATEWAY_ERROR.getStatusCode());
        }
        if (!SUCCESS_CODE.equals(statusCode)) {
            log.error("send email failed:{}", res);
        }
        return generateResponseEntity(statusCode, messageId, timeStamp);
    }

}

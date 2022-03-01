package com.demo.digitalproduct.exception;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.http.HttpStatus;

@Data
public class ApiException extends RuntimeException {
    private ResponseBody body;

    public ApiException(String code, HttpStatus status, String message) {
        this.body = ResponseBody.builder().code(code).status(status).message(message).build();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    @Data
    public static class ResponseBody {
        private String code;
        private HttpStatus status;
        private String message;

        @Builder
        public ResponseBody(String code, HttpStatus status, String message) {
            this.code = code;
            this.status = status;
            this.message = message;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
        }
    }
}

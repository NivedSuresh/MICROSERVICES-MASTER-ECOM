package com.service.product.payloads;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ErrorResponse {
    private String code;
    private String message;
}

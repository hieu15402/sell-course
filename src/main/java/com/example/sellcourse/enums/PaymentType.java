package com.example.sellcourse.enums;

import com.example.sellcourse.exception.AppException;
import com.example.sellcourse.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum PaymentType {
    DEPOSIT("DEPOSIT"),
    ADVERTISEMENT("ADS");

    private final String value;

    PaymentType(String value) {
        this.value = value;
    }

    public static PaymentType fromValue(String value) {
        for (PaymentType type : PaymentType.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new AppException(ErrorCode.INVALID_PAYMENT_TYPE);
    }
}


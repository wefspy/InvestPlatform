package com.example.investplatform.application.dto.payment.yookassa;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record YookassaConfirmationDto(
        String type,
        @JsonProperty("return_url") String returnUrl,
        @JsonProperty("confirmation_url") String confirmationUrl,
        @JsonProperty("confirmation_token") String confirmationToken,
        String locale
) {
    public static YookassaConfirmationDto embedded() {
        return new YookassaConfirmationDto("embedded", null, null, null, "ru_RU");
    }
}

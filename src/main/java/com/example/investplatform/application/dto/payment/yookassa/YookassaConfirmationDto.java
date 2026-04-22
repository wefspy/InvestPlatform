package com.example.investplatform.application.dto.payment.yookassa;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record YookassaConfirmationDto(
        String type,
        @JsonProperty("return_url") String returnUrl,
        @JsonProperty("confirmation_url") String confirmationUrl,
        String locale
) {
    public static YookassaConfirmationDto redirect(String returnUrl) {
        return new YookassaConfirmationDto("redirect", returnUrl, null, "ru_RU");
    }
}

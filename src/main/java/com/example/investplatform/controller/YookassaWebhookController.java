package com.example.investplatform.controller;

import com.example.investplatform.application.dto.payment.yookassa.YookassaWebhookNotification;
import com.example.investplatform.application.service.YookassaWebhookService;
import com.example.investplatform.infrastructure.config.property.YookassaProperties;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/webhooks/yookassa")
@RequiredArgsConstructor
public class YookassaWebhookController {

    private final YookassaWebhookService webhookService;
    private final YookassaProperties yookassaProperties;

    @Operation(summary = "Приём webhook-уведомлений от ЮKassa",
            description = "Публичный endpoint. ЮKassa шлёт уведомления о смене статуса платежа.")
    @PostMapping
    public ResponseEntity<Void> receive(@RequestBody YookassaWebhookNotification notification,
                                        HttpServletRequest request) {
        if (!isAllowedIp(request)) {
            log.warn("Webhook ЮKassa отклонён: неразрешённый IP {}", request.getRemoteAddr());
            return ResponseEntity.status(403).build();
        }

        log.info("Webhook ЮKassa: event={}, type={}",
                notification.event(), notification.type());

        webhookService.handle(notification);
        return ResponseEntity.ok().build();
    }

    private boolean isAllowedIp(HttpServletRequest request) {
        List<String> allowed = yookassaProperties.getAllowedWebhookIps();
        if (allowed == null || allowed.isEmpty()) {
            return true;
        }
        String remote = request.getRemoteAddr();
        return allowed.contains(remote);
    }
}

package com.example.investplatform.infrastructure.config;

import com.example.investplatform.infrastructure.config.property.YookassaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Base64;

@Configuration
public class YookassaConfig {

    @Bean
    public RestClient yookassaRestClient(YookassaProperties properties) {
        String credentials = properties.getShopId() + ":" + properties.getSecretKey();
        String basicAuth = "Basic " + Base64.getEncoder()
                .encodeToString(credentials.getBytes());

        Duration connectTimeout = properties.getConnectTimeout() != null
                ? properties.getConnectTimeout() : Duration.ofSeconds(10);
        Duration readTimeout = properties.getReadTimeout() != null
                ? properties.getReadTimeout() : Duration.ofSeconds(30);

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(connectTimeout)
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(readTimeout);

        return RestClient.builder()
                .baseUrl(properties.getApiUrl())
                .requestFactory(requestFactory)
                .defaultHeader(HttpHeaders.AUTHORIZATION, basicAuth)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}

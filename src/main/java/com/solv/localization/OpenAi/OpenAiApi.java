package com.solv.localization.OpenAi;

import com.solv.localization.Response.ChatCompletion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiApi {

    @Value("${openai.api.key}")
    private String openAIKey;

    @Value("${openai.api.model}")
    private String model;

    private final RestTemplate restTemplate;

    @Value("${openai.api.baseurl}")
    private String apiUrl;

    public OpenAiApi(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ChatCompletion generateTranslation(List prompt, int maxTokens, double temperature) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(openAIKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("messages", prompt);
        requestParams.put("max_tokens", maxTokens);
        requestParams.put("temperature", temperature);
        requestParams.put("model", model);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestParams, headers);
        ParameterizedTypeReference<ResponseEntity<ChatCompletion>> responseType =
                new ParameterizedTypeReference<ResponseEntity<ChatCompletion>>() {};
        ResponseEntity<ChatCompletion> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, ChatCompletion.class);
        ChatCompletion chatCompletion = response.getBody();
        return chatCompletion;
    }
}

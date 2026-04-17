package com.zetenkastel.core.config;

import com.zetenkastel.core.adapter.OllamaLlmAdapter;
import com.zetenkastel.core.port.LlmPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LlmConfig {

    @Value("${llm.ollama.base-url:http://localhost:11434}")
    private String baseUrl;

    @Value("${llm.ollama.model:llama3.2}")
    private String model;

    @Bean
    public LlmPort llmPort() {
        return new OllamaLlmAdapter(baseUrl, model);
    }
}
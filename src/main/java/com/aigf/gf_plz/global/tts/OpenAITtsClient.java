package com.aigf.gf_plz.global.tts;

import com.aigf.gf_plz.global.tts.exception.TtsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.Map;

/**
 * OpenAI TTS API 클라이언트 구현체
 * Groq는 OpenAI 호환 API를 제공하므로 TTS도 사용 가능합니다.
 */
@Service
public class OpenAITtsClient implements TtsClient {

    private static final String TTS_ENDPOINT = "/audio/speech";

    private final WebClient webClient;

    @Value("${groq.api-key}")
    private String apiKey;

    public OpenAITtsClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("https://api.groq.com/openai/v1")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public byte[] synthesize(String text, String voiceType) {
        if (text == null || text.isBlank()) {
            throw new TtsException("변환할 텍스트가 비어있습니다.");
        }

        // OpenAI TTS 모델 및 음성 선택
        String model = "tts-1";
        String voice = mapVoiceType(voiceType);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("input", text);
        requestBody.put("voice", voice);
        requestBody.put("response_format", "mp3");

        try {
            byte[] audioBytes = webClient.post()
                    .uri(TTS_ENDPOINT)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

            if (audioBytes == null || audioBytes.length == 0) {
                throw new TtsException("TTS API 응답이 비어있습니다.");
            }

            return audioBytes;
        } catch (WebClientResponseException e) {
            throw new TtsException(
                    String.format("TTS API 호출 실패: %s - %s", e.getStatusCode(), e.getResponseBodyAsString()),
                    e
            );
        } catch (Exception e) {
            if (e instanceof TtsException) {
                throw e;
            }
            throw new TtsException("TTS API 호출 중 예상치 못한 오류가 발생했습니다.", e);
        }
    }

    /**
     * 캐릭터의 VoiceType을 OpenAI TTS voice로 매핑합니다.
     * OpenAI TTS voice: alloy, echo, fable, onyx, nova, shimmer
     */
    private String mapVoiceType(String voiceType) {
        // 기본값은 "nova" (여성 목소리)
        return switch (voiceType != null ? voiceType.toUpperCase() : "") {
            case "TYPE1" -> "nova";
            case "TYPE2" -> "shimmer";
            case "TYPE3" -> "echo";
            default -> "nova";
        };
    }
}


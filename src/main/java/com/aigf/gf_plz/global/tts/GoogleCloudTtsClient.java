package com.aigf.gf_plz.global.tts;

import com.aigf.gf_plz.global.tts.exception.TtsException;
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechRequest;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.gax.core.FixedCredentialsProvider;

/**
 * Google Cloud TTS 클라이언트 구현체
 * 한국어를 지원하는 TTS 서비스를 제공합니다.
 */
@Service
@Primary
public class GoogleCloudTtsClient implements TtsClient {

    @Value("${google.cloud.tts.credentials-path:}")
    private String credentialsPath;

    @Value("${google.cloud.tts.project-id:}")
    private String projectId;

    private TextToSpeechClient textToSpeechClient;

    /**
     * TextToSpeechClient를 초기화합니다.
     */
    private void initializeClient() {
        if (textToSpeechClient == null) {
            try {
                TextToSpeechSettings.Builder settingsBuilder = TextToSpeechSettings.newBuilder();
                
                // 서비스 계정 키 파일이 지정된 경우 직접 credentials 로드
                if (credentialsPath != null && !credentialsPath.isBlank()) {
                    try (FileInputStream credentialsStream = new FileInputStream(credentialsPath)) {
                        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
                        settingsBuilder.setCredentialsProvider(FixedCredentialsProvider.create(credentials));
                    }
                }
                // credentialsPath가 없으면 환경 변수 GOOGLE_APPLICATION_CREDENTIALS 또는 기본 인증 사용
                
                textToSpeechClient = TextToSpeechClient.create(settingsBuilder.build());
            } catch (IOException e) {
                String errorMsg = e.getMessage();
                if (errorMsg != null && errorMsg.contains("credentials were not found")) {
                    throw new TtsException(
                        "Google Cloud TTS 인증 정보를 찾을 수 없습니다. " +
                        "application.yml에 google.cloud.tts.credentials-path를 설정하거나 " +
                        "환경 변수 GOOGLE_APPLICATION_CREDENTIALS를 설정해주세요. " +
                        "자세한 내용: https://cloud.google.com/docs/authentication/external/set-up-adc",
                        e
                    );
                }
                throw new TtsException("Google Cloud TTS 클라이언트 초기화 실패: " + errorMsg, e);
            }
        }
    }

    @Override
    public byte[] synthesize(String text, String voiceType) {
        if (text == null || text.isBlank()) {
            throw new TtsException("변환할 텍스트가 비어있습니다.");
        }

        try {
            initializeClient();

            // 음성 설정
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("ko-KR")  // 한국어
                    .setName(mapVoiceType(voiceType))  // 목소리 선택
                    .setSsmlGender(SsmlVoiceGender.FEMALE)  // 여성 목소리
                    .build();

            // 오디오 설정
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)  // MP3 형식
                    .build();

            // TTS 요청 생성
            SynthesizeSpeechRequest request = SynthesizeSpeechRequest.newBuilder()
                    .setInput(SynthesisInput.newBuilder().setText(text).build())
                    .setVoice(voice)
                    .setAudioConfig(audioConfig)
                    .build();

            // TTS API 호출
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(request);

            // 오디오 데이터 추출
            ByteString audioContents = response.getAudioContent();
            return audioContents.toByteArray();

        } catch (Exception e) {
            if (e instanceof TtsException) {
                throw e;
            }
            throw new TtsException("Google Cloud TTS API 호출 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 캐릭터의 VoiceType을 Google Cloud TTS voice로 매핑합니다.
     * Google Cloud TTS 한국어 여성 목소리:
     * - ko-KR-Standard-A (여성, 표준)
     * - ko-KR-Standard-B (남성, 표준)
     * - ko-KR-Standard-C (여성, 표준)
     * - ko-KR-Standard-D (남성, 표준)
     * - ko-KR-Wavenet-A (여성, 고품질)
     * - ko-KR-Wavenet-B (남성, 고품질)
     * - ko-KR-Wavenet-C (여성, 고품질)
     * - ko-KR-Wavenet-D (남성, 고품질)
     */
    private String mapVoiceType(String voiceType) {
        // 기본값은 "ko-KR-Wavenet-A" (고품질 여성 목소리)
        return switch (voiceType != null ? voiceType.toUpperCase() : "") {
            case "TYPE1" -> "ko-KR-Wavenet-A";  // 부드러운 여성 목소리 (고품질)
            case "TYPE2" -> "ko-KR-Wavenet-C";  // 밝은 여성 목소리 (고품질)
            case "TYPE3" -> "ko-KR-Standard-A"; // 표준 여성 목소리
            default -> "ko-KR-Wavenet-A";
        };
    }
}


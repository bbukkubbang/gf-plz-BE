package com.aigf.gf_plz.domain.call.service;

import com.aigf.gf_plz.domain.call.dto.CallAudioRequestDto;
import com.aigf.gf_plz.domain.call.dto.CallAudioResponseDto;
import com.aigf.gf_plz.domain.call.dto.CallTextRequestDto;
import com.aigf.gf_plz.domain.call.dto.CallTextResponseDto;
import org.springframework.web.multipart.MultipartFile;

/**
 * 통화 서비스 인터페이스
 */
public interface CallService {
    /**
     * Whisper로 변환된 발화 텍스트에 대해 AI 여자친구의 답변을 생성합니다.
     * 
     * @param request Whisper로 변환된 발화 텍스트
     * @return AI 여자친구의 답변 텍스트 (TTS로 변환되어 재생됨)
     */
    CallTextResponseDto replyToTranscript(CallTextRequestDto request);

    /**
     * 음성 파일을 받아서 STT → AI 답변 생성 → TTS 변환을 수행합니다.
     *
     * @param audioFile 사용자의 음성 파일
     * @param request 캐릭터 및 세션 정보
     * @return AI 여자친구의 음성 응답 (MP3 형식)
     */
    CallAudioResponseDto replyToAudio(MultipartFile audioFile, CallAudioRequestDto request);
}

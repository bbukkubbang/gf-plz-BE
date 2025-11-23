package com.aigf.gf_plz.domain.call.service;

import com.aigf.gf_plz.domain.call.dto.CallTextRequestDto;
import com.aigf.gf_plz.domain.call.dto.CallTextResponseDto;
import com.aigf.gf_plz.global.groq.GroqClient;
import org.springframework.stereotype.Service;

/**
 * 통화 서비스 구현체
 */
@Service
public class CallServiceImpl implements CallService {

    private final GroqClient groqClient;

    public CallServiceImpl(GroqClient groqClient) {
        this.groqClient = groqClient;
    }

    @Override
    public CallTextResponseDto replyToTranscript(CallTextRequestDto request) {
        String reply = groqClient.generateReply("call", request.transcript());
        return new CallTextResponseDto(reply);
    }
}


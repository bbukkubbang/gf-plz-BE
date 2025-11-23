package com.aigf.gf_plz.domain.chat.service;

import com.aigf.gf_plz.domain.chat.dto.ChatRequestDto;
import com.aigf.gf_plz.domain.chat.dto.ChatResponseDto;
import com.aigf.gf_plz.global.groq.GroqClient;
import org.springframework.stereotype.Service;

/**
 * 채팅 서비스 구현체
 */
@Service
public class ChatServiceImpl implements ChatService {

    private final GroqClient groqClient;

    public ChatServiceImpl(GroqClient groqClient) {
        this.groqClient = groqClient;
    }

    @Override
    public ChatResponseDto chat(ChatRequestDto request) {
        String reply = groqClient.generateReply("chat", request.content());
        return new ChatResponseDto(reply);
    }
}


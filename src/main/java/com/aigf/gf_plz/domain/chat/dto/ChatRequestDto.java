package com.aigf.gf_plz.domain.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

/**
 * 채팅 요청 DTO
 * 사용자가 입력한 텍스트 메시지를 담습니다.
 */
public record ChatRequestDto(
        @NotNull(message = "캐릭터 ID는 필수입니다.")
        Long characterId,
        Optional<Long> sessionId,
        @NotBlank(message = "메시지 내용은 필수입니다.")
        String content
) {}

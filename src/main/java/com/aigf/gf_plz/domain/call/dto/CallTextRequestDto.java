package com.aigf.gf_plz.domain.call.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

/**
 * 통화 텍스트 요청 DTO
 * Whisper로 변환된 사용자의 발화 텍스트를 담습니다.
 */
public record CallTextRequestDto(
        @NotNull(message = "캐릭터 ID는 필수입니다.")
        Long characterId,
        Optional<Long> sessionId,
        @NotBlank(message = "발화 텍스트는 필수입니다.")
        String transcript
) {}


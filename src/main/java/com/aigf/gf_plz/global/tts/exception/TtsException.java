package com.aigf.gf_plz.global.tts.exception;

/**
 * TTS API 예외
 */
public class TtsException extends RuntimeException {

    public TtsException(String message) {
        super(message);
    }

    public TtsException(String message, Throwable cause) {
        super(message, cause);
    }
}


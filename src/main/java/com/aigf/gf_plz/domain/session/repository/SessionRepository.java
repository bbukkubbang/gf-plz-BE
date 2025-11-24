package com.aigf.gf_plz.domain.session.repository;

import com.aigf.gf_plz.domain.session.entity.Session;
import com.aigf.gf_plz.domain.session.entity.SessionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 세션 리포지토리
 */
public interface SessionRepository extends JpaRepository<Session, Long> {

    /**
     * 세션 ID와 활성화 상태로 세션을 조회합니다.
     */
    Optional<Session> findBySessionIdAndIsActiveTrue(Long sessionId);

    /**
     * 캐릭터 ID, 세션 타입, 활성화 상태로 세션을 조회합니다.
     */
    Optional<Session> findByCharacterIdAndSessionTypeAndIsActiveTrue(
            Long characterId,
            SessionType sessionType
    );

    /**
     * 캐릭터 ID와 세션 타입으로 최근 세션을 조회합니다 (비활성 포함).
     */
    List<Session> findByCharacterIdAndSessionTypeOrderByLastMessageAtDesc(
            Long characterId,
            SessionType sessionType
    );
}


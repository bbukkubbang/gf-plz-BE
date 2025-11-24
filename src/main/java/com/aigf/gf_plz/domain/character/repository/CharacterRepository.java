package com.aigf.gf_plz.domain.character.repository;

import com.aigf.gf_plz.domain.character.entity.Character;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 캐릭터 리포지토리
 */
public interface CharacterRepository extends JpaRepository<Character, Long> {
}

package org.bbsk.mysender.fmkorea.jpa.repository;


import org.bbsk.mysender.fmkorea.jpa.entity.FmKoreaSearchKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FmKoreaJpaRepository extends JpaRepository<FmKoreaSearchKeyword, Long> {
    List<FmKoreaSearchKeyword> getFmKoreaSearchKeywordByUseYn(String useYn);
    FmKoreaSearchKeyword findByKeyword(String keyword);
}

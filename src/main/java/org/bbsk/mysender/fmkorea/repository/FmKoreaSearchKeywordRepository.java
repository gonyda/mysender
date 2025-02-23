package org.bbsk.mysender.fmkorea.repository;

import org.bbsk.mysender.fmkorea.entity.FmKoreaSearchKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FmKoreaSearchKeywordRepository extends JpaRepository<FmKoreaSearchKeyword, Long> {
    List<FmKoreaSearchKeyword> getFmKoreaSearchKeywordByUseYn(String useYn);
}

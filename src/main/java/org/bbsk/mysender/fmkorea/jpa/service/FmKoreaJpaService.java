package org.bbsk.mysender.fmkorea.jpa.service;

import lombok.RequiredArgsConstructor;
import org.bbsk.mysender.fmkorea.jpa.entity.FmKoreaSearchKeyword;
import org.bbsk.mysender.fmkorea.jpa.repository.FmKoreaJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FmKoreaJpaService {

    private final FmKoreaJpaRepository fmKoreaJpaRepository;

    public List<FmKoreaSearchKeyword> getFmKoreaSearchKeywordByUseYn(String useYn) {
        return fmKoreaJpaRepository.getFmKoreaSearchKeywordByUseYn(useYn);
    }

    @Transactional
    public void addFmKoreaSearchKeyword(String keyword) {
        if (fmKoreaJpaRepository.findByKeyword(keyword) == null) {
            fmKoreaJpaRepository.save(FmKoreaSearchKeyword.builder()
                    .keyword(keyword)
                    .useYn("Y")
                    .build());
        }
    }

    @Transactional
    public void removeFmKoreaSearchKeyword(String keyword) {
        fmKoreaJpaRepository.delete(fmKoreaJpaRepository.findByKeyword(keyword));
    }
}

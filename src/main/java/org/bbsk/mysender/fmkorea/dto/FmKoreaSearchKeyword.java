package org.bbsk.mysender.fmkorea.dto;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
public class FmKoreaSearchKeyword {

    public List<String> keywordList = new ArrayList<>();

    public void addKeyword(String keyword) {
        keywordList.add(keyword);
    }

    public void removeKeyword(String keyword) {
        keywordList.remove(keyword);
    }

    @PostConstruct
    public void init() {
        this.addKeyword("팔란티어");
    }
}

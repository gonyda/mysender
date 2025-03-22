package org.bbsk.mysender.fmkorea.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FmKoreaKeywordService {

    private static final List<String> KEYWORDLIST = new ArrayList<>();

    public List<String> getKeywordList() {
        return KEYWORDLIST;
    }

    public List<String> addKeyword(String keyword) {
        if(!KEYWORDLIST.contains(keyword)) {
            KEYWORDLIST.add(keyword);
        }

        return KEYWORDLIST;
    }

    public List<String> removeKeyword(String keyword) {
        KEYWORDLIST.remove(keyword);

        return KEYWORDLIST;
    }

    public List<String> removeAll() {
        KEYWORDLIST.clear();
        return KEYWORDLIST;
    }
}

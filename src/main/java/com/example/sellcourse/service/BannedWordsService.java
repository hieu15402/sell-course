package com.example.sellcourse.service;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BannedWordsService {
    AhoCorasickDoubleArrayTrie<String> ahoCorasickTrie = new AhoCorasickDoubleArrayTrie<>();

    @PostConstruct
    public void loadBannedWords() {
        try {
            Resource resource = new ClassPathResource("banned_words.txt");
            InputStream inputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            List<String> bannedWordsList = reader.lines().toList();
            Map<String, String> map = new HashMap<>();
            for (String word : bannedWordsList) {
                map.put(word.toLowerCase(), word);
            }

            ahoCorasickTrie.build(map);

        } catch (IOException e) {
            log.error("Lỗi khi đọc file banned_words.txt: ", e);
        }
    }
    public boolean containsBannedWords(String content) {
        content = content.toLowerCase();

        List<AhoCorasickDoubleArrayTrie.Hit<String>> hits = ahoCorasickTrie.parseText(content.toLowerCase());

        for (AhoCorasickDoubleArrayTrie.Hit<String> hit : hits) {
            String word = hit.value;
            String regex = "\\b" + word + "\\b";

            if (content.matches(".*" + regex + ".*")) {
                return true;
            }
        }

        return false;
    }
}

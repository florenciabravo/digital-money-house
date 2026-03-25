package com.digitalmoneyhouse.accountservice.util;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
public class AliasGenerator {

    private final List<String> words = new ArrayList<>();
    private final Random random = new Random();

    @PostConstruct
    public void loadWords() throws Exception {

        ClassPathResource resource = new ClassPathResource("alias-words.txt");

        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(resource.getInputStream()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim());
            }
        }
    }

    public String generate() {

        Collections.shuffle(words);

        return words.get(0) + "." + words.get(1) + "." + words.get(2);
    }
}

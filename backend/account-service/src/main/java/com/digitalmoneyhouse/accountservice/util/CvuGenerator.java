package com.digitalmoneyhouse.accountservice.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CvuGenerator {

    public String generate() {
        StringBuilder cvu = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 22; i++) {
            cvu.append(random.nextInt(10));
        }
        return cvu.toString();
    }
}

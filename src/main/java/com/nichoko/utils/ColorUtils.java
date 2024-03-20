package com.nichoko.utils;

import java.util.Random;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@ApplicationScoped
public class ColorUtils {
    private Random random = new Random();

    public String generateRandomHexColor() {
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        return String.format("#%02X%02X%02X", red, green, blue);
    }
}

package com.nichoko.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class ColorUtilsTest {

    @Inject
    ColorUtils colorUtils;

    @Test
    void testGenerateRandomHexColor() {
        String hexColor = colorUtils.generateRandomHexColor();
        assertTrue(hexColor.matches("^#[0-9A-Fa-f]{6}$"), "Generated color is not a valid hexadecimal color");
    }
}
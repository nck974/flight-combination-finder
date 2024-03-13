package com.nichoko.utils;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

@QuarkusTest
class CartesianProductTest {

    @Test
    void testCartesianProductTwoLists() {
        List<List<String>> input = Arrays.asList(Arrays.asList("A", "B"), Arrays.asList("C", "D"));
        List<List<String>> expected = Arrays.asList(
                Arrays.asList("A", "C"),
                Arrays.asList("A", "D"),
                Arrays.asList("B", "C"),
                Arrays.asList("B", "D"));
        List<List<String>> result = CartesianProduct.generateListsCartesianProduct(input);
        assertEquals(expected, result);
    }

    @Test
    void testCartesianProductThreeLists() {
        List<List<String>> input = Arrays.asList(
                Arrays.asList("A", "B"),
                Arrays.asList("C", "D"),
                Arrays.asList("E", "F"));
        List<List<String>> expected = Arrays.asList(
                Arrays.asList("A", "C", "E"),
                Arrays.asList("A", "C", "F"),
                Arrays.asList("A", "D", "E"),
                Arrays.asList("A", "D", "F"),
                Arrays.asList("B", "C", "E"),
                Arrays.asList("B", "C", "F"),
                Arrays.asList("B", "D", "E"),
                Arrays.asList("B", "D", "F"));
        List<List<String>> result = CartesianProduct.generateListsCartesianProduct(input);
        assertEquals(expected, result);
    }

    @Test
    void testConstructorThrowsException() {
        assertThrows(IllegalStateException.class, () -> {
            new CartesianProduct();
        });
    }

}
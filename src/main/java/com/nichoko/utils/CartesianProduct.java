package com.nichoko.utils;

import java.util.ArrayList;
import java.util.List;

public class CartesianProduct {

    CartesianProduct() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Generate all possible combinations of some lists.
     * Example:
     * Input: List.of("A", "B") and List.of("C", "D")
     * Output: (A,C), (A,D), (B,C), (B,D)
     * 
     * @param <T>
     * @param lists
     * @return
     */
    public static <T> List<List<T>> generateListsCartesianProduct(List<List<T>> lists) {
        List<List<T>> result = new ArrayList<>();
        generateCombinationsHelper(lists, result, new ArrayList<>(), 0);
        return result;
    }

    private static <T> void generateCombinationsHelper(List<List<T>> lists, List<List<T>> result,
            List<T> current, int index) {
        if (index == lists.size()) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (T value : lists.get(index)) {
            current.add(value);
            generateCombinationsHelper(lists, result, current, index + 1);
            current.remove(current.size() - 1);
        }
    }
}

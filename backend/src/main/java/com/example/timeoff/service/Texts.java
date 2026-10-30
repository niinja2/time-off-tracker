package com.example.timeoff.service;

final class Texts {

    private Texts() {
    }

    // An empty or whitespace-only text is stored as no text (L21 point 3, L23 point 7).
    static String blankToNull(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        return text;
    }
}

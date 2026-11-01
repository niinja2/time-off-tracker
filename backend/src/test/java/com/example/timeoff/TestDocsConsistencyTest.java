package com.example.timeoff;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Provjera dokumentacije testova: zaglavlje klase i imena testova moraju se slagati
 * međusobno i s oznakama u docs/zajednicko. Ne provjerava je li test stvarno
 * provjerio pravilo iz svoje oznake; to rade review i mutacijsko testiranje.
 *
 * Pravila:
 *   1. klasa ima zaglavlje s ključevima Izvori, Mreža, Pokriva;
 *   2. ime svakog testa završava oznakom, npr. _R6 ili _Q30_1 (= Q30.1);
 *   3. svaka oznaka iz imena testa navedena je u Pokriva;
 *   4. svaka oznaka iz Pokriva nosi barem jedan test u imenu ili u komentaru odjeljka;
 *   5. svaka oznaka iz zaglavlja i imena postoji: R, Q, D u specifikaciji,
 *      T kao naslov u sučelju, L kao naslov u coding logu.
 */
class TestDocsConsistencyTest {

    // Klase koje već imaju zaglavlje; cilj je da popis postane svih test klasa
    private static final List<String> DOCUMENTED = List.of(
        "CancelApiTest",
        "CancelDayBoundaryApiTest");

    private static final Path TESTS = Path.of("src/test/java/com/example/timeoff");
    private static final Path DOCS = Path.of("../docs/zajednicko");

    private static final List<String> KEYS = List.of("Izvori:", "Mreža:", "Pokriva:");

    private static final Pattern CODE = Pattern.compile("\\b[RQDTL]\\d+(?:\\.\\d+)?\\b");
    private static final Pattern TEST_METHOD = Pattern.compile(
        "@(?:Test|ParameterizedTest)\\b[\\s\\S]*?\\bvoid\\s+(\\w+)\\s*\\(");
    private static final Pattern NAME_CODE = Pattern.compile("_([RQDTL]\\d+(?:_\\d+)?)$");
    private static final Pattern SECTION_COMMENT = Pattern.compile("(?m)^\\s*//.*$");

    @ParameterizedTest(name = "{0}")
    @MethodSource("documented")
    void classHasHeaderWithAllKeys(String className) throws IOException {
        Map<String, String> header = header(className);

        assertThat(header.keySet()).containsExactlyElementsOf(KEYS);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("documented")
    void everyTestNameEndsWithCode(String className) throws IOException {
        List<String> withoutCode = testNames(className).stream()
            .filter(name -> !NAME_CODE.matcher(name).find())
            .toList();

        assertThat(withoutCode).isEmpty();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("documented")
    void everyCodeInTestNamesIsListedInHeader(String className) throws IOException {
        Set<String> listed = codesIn(header(className).get("Pokriva:"));

        Set<String> missing = new LinkedHashSet<>(nameCodes(className));
        missing.removeAll(listed);

        assertThat(missing).isEmpty();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("documented")
    void everyListedCodeHasTest(String className) throws IOException {
        Set<String> used = new LinkedHashSet<>(nameCodes(className));
        used.addAll(sectionCommentCodes(className));

        Set<String> unused = codesIn(header(className).get("Pokriva:"));
        unused.removeAll(used);

        assertThat(unused).isEmpty();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("documented")
    void everyCodeExistsInDocs(String className) throws IOException {
        Set<String> codes = new LinkedHashSet<>(nameCodes(className));
        header(className).values()
            .forEach(value -> codes.addAll(codesIn(value)));

        List<String> unknown = new ArrayList<>();
        for (String code : codes) {
            if (!existsInDocs(code)) {
                unknown.add(code);
            }
        }

        assertThat(unknown).isEmpty();
    }

    // Pomoćne metode

    static Stream<String> documented() {
        return DOCUMENTED.stream();
    }

    private static String source(String className) throws IOException {
        return Files.readString(TESTS.resolve(className + ".java"));
    }

    // Javadoc neposredno iznad "class X", ključ → vrijednost; nastavak retka pripada zadnjem ključu
    private static Map<String, String> header(String className) throws IOException {
        String source = source(className);
        int classAt = source.indexOf("\nclass " + className);
        int start = source.lastIndexOf("/**", classAt);
        int end = source.lastIndexOf("*/", classAt);
        Map<String, String> entries = new LinkedHashMap<>();
        if (start < 0 || end < start) {
            return entries;
        }
        String current = null;
        for (String raw : source.substring(start + 3, end).split("\n")) {
            String line = raw.strip().replaceFirst("^\\*\\s?", "").strip();
            String key = keyOf(line);
            if (key != null) {
                current = key;
                entries.put(key, line.substring(key.length()).strip());
            } else if (current != null && !line.isEmpty()) {
                entries.put(current, entries.get(current) + " " + line);
            }
        }
        return entries;
    }

    private static String keyOf(String line) {
        return KEYS.stream()
            .filter(line::startsWith)
            .findFirst()
            .orElse(null);
    }

    private static List<String> testNames(String className) throws IOException {
        Matcher matcher = TEST_METHOD.matcher(source(className));
        List<String> names = new ArrayList<>();
        while (matcher.find()) {
            names.add(matcher.group(1));
        }
        return names;
    }

    private static Set<String> nameCodes(String className) throws IOException {
        Set<String> codes = new LinkedHashSet<>();
        for (String name : testNames(className)) {
            Matcher matcher = NAME_CODE.matcher(name);
            if (matcher.find()) {
                codes.add(matcher.group(1).replace('_', '.'));
            }
        }
        return codes;
    }

    private static Set<String> sectionCommentCodes(String className) throws IOException {
        Matcher matcher = SECTION_COMMENT.matcher(source(className));
        Set<String> codes = new LinkedHashSet<>();
        while (matcher.find()) {
            codes.addAll(codesIn(matcher.group()));
        }
        return codes;
    }

    private static Set<String> codesIn(String text) {
        Set<String> codes = new LinkedHashSet<>();
        if (text == null) {
            return codes;
        }
        Matcher matcher = CODE.matcher(text);
        while (matcher.find()) {
            codes.add(matcher.group());
        }
        return codes;
    }

    private static boolean existsInDocs(String code) throws IOException {
        return switch (code.charAt(0)) {
            case 'T' -> doc("sucelje.md").contains("\n## " + code + " ");
            case 'L' -> doc("coding-log.md").contains("\n## " + code + " ");
            default -> doc("specifikacija.md").contains("`" + code + "`");
        };
    }

    private static String doc(String name) throws IOException {
        return Files.readString(DOCS.resolve(name));
    }
}

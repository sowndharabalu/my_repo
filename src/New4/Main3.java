package New4;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class InvalidTextException extends Exception {
    public InvalidTextException(String message) { super(message); }
}

class TextProcessor {
    private static final Pattern email=Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    static Map<String, Long> countWordFrequency(String text){
        String s=text.replaceAll("\\p{Punct}","").toLowerCase();
        return Arrays.stream(s.split(" ")).collect(Collectors.groupingBy(s1->s1, Collectors.counting()));
    }
    static Optional<String> findLongestWord(String text){
        return Arrays.stream(text.replaceAll("\\p{Punct}","").split("\\s+")).filter(s->!s.isEmpty()).max(Comparator.comparingInt(String::length));
    }
    private static boolean palidrome(String text){
        String cleaned = text.toLowerCase();
        return cleaned.contentEquals(new StringBuilder(cleaned).reverse());
    }
    static List<String> findPalindromes(String text){
        return Arrays.stream(text.split(" ")).map(String::toLowerCase).filter(s->s.length()>2 && palidrome(s)).toList();
    }
    static Map<Integer, List<String>> groupByLength(String text){
        return countWordFrequency(text).keySet().stream().collect(Collectors.groupingBy(String::length));
    }
    static List<String> findWordsStartingWith(String text, String prefix){
        return countWordFrequency(text).keySet().stream().filter(s -> s.startsWith(prefix)).sorted().collect(Collectors.toList());
    }
    static String maskNumbers(String text){
        return text.replaceAll("\\d+", "#NUM#");
    }
    static List<String> extractEmails(String text){
        return email.matcher(text).results().map(MatchResult::group).distinct().toList();
    }
    static double getAverageWordLength(String text){
        return Arrays.stream(text.replaceAll("\\p{Punct}","").split("\\s")).filter(s->!s.isEmpty()).mapToInt(String::length).average().orElse(0.0);
    }
    static Map<String, List<String>> findAnagrams(String text){
        if (text == null || text.isBlank()) {
            return Map.of();
        }
        return Arrays.stream(text.replaceAll("[^a-zA-Z\\s]", "").toLowerCase().split("\\s+"))
                .filter(word -> !word.isBlank())
                .distinct()
                .collect(Collectors.groupingBy(TextProcessor::getSortKey))
                .entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    private static String getSortKey(String word) {
        char[] chars = word.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }
}

public class Main3 {
    public static void main(String[] args) {
        String text = """
    Hello world! Java is amazing. 
    The quick brown fox jumps over the lazy dog.
    Anna went to civic center. She saw a racecar.
    Contact us at test@example.com or support@company.org
    Call 123-456-7890 or 9876543210 for details.
    Listen to the silent night.
    """;

        System.out.println(TextProcessor.countWordFrequency(text));
        TextProcessor.findLongestWord(text).ifPresent(System.out::println);
        System.out.println(TextProcessor.findPalindromes(text));
        System.out.println(TextProcessor.groupByLength(text));
        System.out.println(TextProcessor.extractEmails(text));
        System.out.println(TextProcessor.maskNumbers(text));
        System.out.println(TextProcessor.findAnagrams(text));
        System.out.println(TextProcessor.findWordsStartingWith(text, "th"));
        System.out.println(TextProcessor.getAverageWordLength(text));
    }
}

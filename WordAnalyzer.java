package learn;
import java.util.HashMap;
public class WordAnalyzer {
    public static void main(String[] args) {
        String sentence = "the dog chased the cat and the cat ran";
        String[] words = sentence.split(" ");
        HashMap<String, Integer> wordCounts = new HashMap<String, Integer>();
        for (String word : words) {
            if (wordCounts.containsKey(word)) {
                int currentCount = wordCounts.get(word);
                wordCounts.put(word, currentCount + 1);
            } else {
                wordCounts.put(word, 1);
            }
        }
        for (String key : wordCounts.keySet()) {
            System.out.println(key + ": " + wordCounts.get(key));
        }
    }
}
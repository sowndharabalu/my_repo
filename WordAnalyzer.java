package learn;
import java.util.HashMap;
public class WordAnalyzer {
    public static void main(String[] args) {
        String sentence = "the dog chased the cat and the cat ran";
        String[] words = sentence.split(" ");
        HashMap<String, Integer> wordCounts = new HashMap<String, Integer>();
        for(String i:words){
            int n=0;
            if(!wordCounts.containsKey(i))  {
                for(int j=0;j<words.length;j++){
                    if(words[j].equals(i)){
                        n+=1;
                    }
                }wordCounts.put(i,n);
            }
        }
        for(String s:wordCounts.keySet()){
            System.out.println(s+":"+wordCounts.get(s)+"\n");
        }
    }
}

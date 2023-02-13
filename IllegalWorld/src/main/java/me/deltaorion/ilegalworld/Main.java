package me.deltaorion.ilegalworld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        System.out.println(findSubstring("aaaaaaaaaaaaaa",new String[]{"aa","aa"}));
    }

    private final int NUM_OF_BASKETS = 2;

    public static List<Integer> findSubstring(String s, String[] words) {
        List<Integer> res = new ArrayList<>();

        int wordLength = words[0].length();
        int k = wordLength * words.length;

        Map<String,Integer> wordsCount = new HashMap<>();

        for(String word : words) {
            wordsCount.merge(word, 1, Integer::sum);
        }

        for(int m=0;m<wordLength;m++) {
            int j = m;
            int i = m;
            Map<String,Integer> windowCount = new HashMap<>();

            while (j < s.length() - wordLength + 1) {
                String word = getWord(s, j, wordLength);

                windowCount.merge(word, 1, Integer::sum);
                j += wordLength;

                if (j - i + 1 > k) {
                    if(windowCount.equals(wordsCount))
                        res.add(i);

                    String iWord = getWord(s, i, wordLength);
                    Integer count = windowCount.get(iWord);
                    if (count == 1) {
                        windowCount.remove(iWord);
                    } else {
                        windowCount.put(iWord, count - 1);
                    }
                    i += wordLength;
                }
            }
        }

        return res;
    }

    private static String getWord(String s,int j, int wordLength) {
        StringBuilder word = new StringBuilder();
        for(int m=0;m<wordLength;m++) {
            word.append(s.charAt(j+m));
        }
        return word.toString();
    }
}

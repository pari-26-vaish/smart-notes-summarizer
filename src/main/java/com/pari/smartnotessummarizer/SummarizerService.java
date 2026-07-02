package com.pari.smartnotessummarizer;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SummarizerService {

    private static final Set<String> STOP_WORDS = Set.of(
            "the", "is", "at", "which", "on", "a", "an", "and", "or", "but",
            "in", "with", "to", "for", "of", "as", "by", "that", "this",
            "it", "are", "was", "were", "be", "been", "has", "have", "had",
            "not", "from", "will", "would", "could", "should", "can", "i",
            "you", "he", "she", "they", "we", "his", "her", "their", "our"
    );

    public String summarize(String text, String length) {
        if (text == null || text.trim().isEmpty()) {
            return "Please enter some text to summarize.";
        }

        text = text.trim();
        String[] sentences = text.split("(?<=[.!?])\\s+");

        if (sentences.length <= 2) {
            return text;
        }

        Map<String, Integer> wordFreq = new HashMap<>();
        String[] words = text.toLowerCase().split("\\W+");
        for (String word : words) {
            if (!word.isBlank() && !STOP_WORDS.contains(word)) {
                wordFreq.merge(word, 1, Integer::sum);
            }
        }

        Map<String, Double> sentenceScores = new LinkedHashMap<>();
        for (String sentence : sentences) {
            String[] sentenceWords = sentence.toLowerCase().split("\\W+");
            double score = 0;
            int count = 0;
            for (String word : sentenceWords) {
                if (wordFreq.containsKey(word)) {
                    score += wordFreq.get(word);
                    count++;
                }
            }
            if (count > 0) sentenceScores.put(sentence, score / count);
        }

        // Determine how many sentences based on requested length
        double ratio = switch (length == null ? "medium" : length) {
            case "short" -> 0.2;
            case "long" -> 0.5;
            default -> 0.33;
        };
        int numSentences = Math.max(1, (int) Math.round(sentences.length * ratio));
        numSentences = Math.min(numSentences, sentences.length);

        List<Map.Entry<String, Double>> sorted = new ArrayList<>(sentenceScores.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        Set<String> topSentences = new LinkedHashSet<>();
        for (int i = 0; i < Math.min(numSentences, sorted.size()); i++) {
            topSentences.add(sorted.get(i).getKey());
        }

        StringBuilder summary = new StringBuilder();
        for (String sentence : sentences) {
            if (topSentences.contains(sentence)) {
                summary.append(sentence.trim()).append(" ");
            }
        }

        return summary.toString().trim();
    }
}
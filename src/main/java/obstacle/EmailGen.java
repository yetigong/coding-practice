package obstacle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmailGen {
    static class EmailGenSol {
        static Map<String, String> templateMap = Map.of(
                "spam", "we found the spam content",
                "phishing", "we found the phishy content",
                "abusive", "the following contents are abusive",
                "violence", "the following contents are violent"
        );

        public EmailGenSol() {

        }

        String generate(List<String> keywords, List<String> keywordLabels) {
            // initialize the keywords label into a map, so we have [keyword -> list<label>] map
            Map<String, List<String>> keywordToLabels = new HashMap<>();
            for (String keywordToLabel : keywordLabels) {
                String[] parts = keywordToLabel.split(":");
                String keyword = parts[0].trim();
                String label = parts[1].trim();
                keywordToLabels.putIfAbsent(keyword, new ArrayList<>());
                keywordToLabels.get(keyword).add(label);
            }

            // first goal is to generate a [label -> list[keywords]] map, based on the keywords provided
            Map<String, List<String>> labelToKeywords = new HashMap<>();
            for (String keyword: keywords) {
                List<String> labels = keywordToLabels.getOrDefault(keyword, new ArrayList<>());
                for (String label: labels) {
                    labelToKeywords.putIfAbsent(label, new ArrayList<>());
                    labelToKeywords.get(label).add(keyword);
                }
            }

            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, List<String>> entry: labelToKeywords.entrySet()) {
                sb.append(templateMap.get(entry.getKey()))
                        .append(": ")
                        .append(entry.getValue())
                        .append("\n");
            }
            return sb.toString();
        }
    }
    public static void main(String[] args) {
        EmailGenSol sol = new EmailGenSol();
        List<String> keywords = List.of(
                "black",
                "password",
                "click",
                "assg",
                "blast",
                "kill",
                "good words"
        );

        List<String> keywordToLabels = List.of(
                "black: abusive",
                "hatred: abusive",
                "password: phishing",
                "click: phishing",
                "assg: spam",
                "blast: spam",
                "kill: violence"
        );

        System.out.println(sol.generate(keywords, keywordToLabels));
    }

}

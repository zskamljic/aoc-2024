import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day05 {
    public static void main(String[] args) throws IOException {
        var input = Files.readString(Path.of("input05.txt"));

        var parts = input.split("\n\n");
        var rules = parseRules(parts[0]);
        var prints = parsePrints(parts[1]);

        var incorrect = part1(prints, rules);
        part2(incorrect, rules);
    }

    private static List<List<String>> part1(List<List<String>> prints, Map<String, List<String>> rules) {
        var incorrect = new ArrayList<List<String>>();
        var sum = 0;
        print:
        for (var print : prints) {
            for (int i = 0; i < print.size(); i++) {
                var number = print.get(i);
                var requirements = rules.get(number);
                if (requirements == null) continue;

                for (var requirement : requirements) {
                    var index = print.indexOf(requirement);
                    if (index != -1 && index > i) {
                        incorrect.add(print);
                        continue print;
                    }
                }
            }
            var center = Integer.parseInt(print.get(print.size() / 2));
            sum += center;
        }
        System.out.println(sum);
        return incorrect;
    }

    private static void part2(List<List<String>> incorrect, Map<String, List<String>> rules) {
        var reverseRules = new HashMap<String, List<String>>();
        for (var rule : rules.entrySet()) {
            for (var item : rule.getValue()) {
                reverseRules.computeIfAbsent(item, ignored -> new ArrayList<>()).add(rule.getKey());
            }
        }

        var sum = 0;
        for (var print : incorrect) {
            var sorted = new ArrayList<String>();
            for (var item : print) {
                var set = reverseRules.get(item);
                if (set == null) {
                    sorted.add(item);
                    continue;
                }

                set.stream()
                    .mapToInt(sorted::indexOf)
                    .filter(i -> i != -1)
                    .min()
                    .ifPresentOrElse(i -> sorted.add(i, item), () -> sorted.add(item));
            }
            var center = Integer.parseInt(sorted.get(sorted.size() / 2));
            sum += center;
        }
        System.out.println(sum);
    }

    private static Map<String, List<String>> parseRules(String part) {
        var result = new HashMap<String, List<String>>();

        part.lines()
            .map(s -> s.split("\\|"))
            .forEach(as -> result.computeIfAbsent(as[1], ignored -> new ArrayList<>()).add(as[0]));

        return result;
    }

    private static List<List<String>> parsePrints(String part) {
        return part.lines()
            .map(s -> s.split(","))
            .map(Arrays::asList)
            .toList();
    }
}

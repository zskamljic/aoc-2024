import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day19 {
    public static void main(String[] args) throws IOException {
        var input = Files.readString(Path.of("input19.txt"));

        var parts = input.split("\n\n");
        var patterns = Arrays.asList(parts[0].split(", "));

        var desired = parts[1].split("\n");

        var seen = new HashMap<String, Long>();
        seen.put("", 1L);
        var counts = Arrays.stream(desired)
            .filter(design -> designs(design, patterns, seen) > 0)
            .count();
        System.out.println(counts);
        var part2 = Arrays.stream(desired)
            .mapToLong(p -> seen.getOrDefault(p, 0L))
            .sum();
        System.out.println(part2);
    }

    private static long designs(String design, List<String> parts, Map<String, Long> seen) {
        var stack = new ArrayDeque<String>();
        stack.add(design);

        while (!stack.isEmpty()) {
            var current = stack.pop();

            if (seen.containsKey(current)) continue;

            var haveAllChildren = true;
            var count = 0L;
            for (var start : parts) {
                if (current.startsWith(start)) {
                    var replacement = current.substring(start.length());
                    if (seen.containsKey(replacement)) {
                        count += seen.get(replacement);
                        continue;
                    } else if (haveAllChildren) {
                        haveAllChildren = false;
                        stack.push(current);
                    }
                    stack.push(replacement);
                }
            }
            if (haveAllChildren) {
                seen.put(current, count);
            }
        }
        return seen.get(design);
    }
}

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;

public class Day11 {
    public static void main(String[] args) throws IOException {
        var input = Files.readString(Path.of("input11.txt"));

        var numbers = Arrays.stream(input.trim().split("\\s"))
            .mapToLong(Long::parseLong)
            .toArray();

        var values = new HashMap<Long, Long>();
        for (var number : numbers) {
            values.put(number, 1L);
        }
        for (int i = 0; i < 25; i++) {
            values = blink(values);
        }
        System.out.println(values.values().stream().mapToLong(l -> l).sum());
        for (int i = 0; i < 50; i++) {
            values = blink(values);
        }
        System.out.println(values.values().stream().mapToLong(l -> l).sum());
    }

    private static HashMap<Long, Long> blink(HashMap<Long, Long> values) {
        var nextMap = new HashMap<Long, Long>();
        for (var entry : values.entrySet()) {
            var value = entry.getKey();
            if (value == 0) {
                nextMap.merge(1L, entry.getValue(), Long::sum);
                continue;
            }
            var digits = (int) Math.floor(Math.log10(value)) + 1;
            if (digits % 2 == 0) {
                var factor = (int) Math.pow(10, digits / 2f);
                var left = value / factor;
                var right = value % factor;
                nextMap.merge(left, entry.getValue(), Long::sum);
                nextMap.merge(right, entry.getValue(), Long::sum);
            } else {
                nextMap.merge(value * 2024, entry.getValue(), Long::sum);
            }
        }
        return nextMap;
    }
}

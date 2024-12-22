import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Gatherers;

public class Day22 {
    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Path.of("input22.txt"));

        var numbers = input.stream()
            .mapToLong(Long::parseLong)
            .toArray();

        part1(numbers);
        part2(numbers);
    }

    private static void part1(long[] numbers) {
        var sum = 0L;
        for (var number : numbers) {
            for (int i = 0; i < 2000; i++) {
                number = evolve(number);
            }
            sum += number;
        }
        System.out.println(sum);
    }

    private static void part2(long[] numbers) {
        var patterns = new HashMap<Delta, Long>();

        for (var number : numbers) {
            var deltas = generateDeltas(number);

            var added = new HashSet<Delta>();
            Arrays.stream(deltas)
                .gather(Gatherers.windowSliding(4))
                .forEach(window -> {
                    var pattern = new Delta(window.stream()
                        .mapToLong(l -> l[0])
                        .toArray());
                    if (!added.contains(pattern)) {
                        patterns.merge(pattern, window.get(3)[1], Long::sum);
                        added.add(pattern);
                    }
                });
        }

        var maxValue = patterns.values()
            .stream()
            .mapToLong(l -> l)
            .max()
            .orElseThrow();
        System.out.println(maxValue);
    }

    private static long[][] generateDeltas(long number) {
        var deltas = new long[2000][2];
        for (int i = 0; i < 2000; i++) {
            var next = evolve(number);

            var prevBananas = number % 10;
            var newBananas = next % 10;

            deltas[i][0] = newBananas - prevBananas;
            deltas[i][1] = newBananas;

            number = next;
        }
        return deltas;
    }

    private static long evolve(long secret) {
        var product = secret * 64;
        secret ^= product;
        secret %= 16777216;

        var div = secret / 32;
        secret ^= div;
        secret %= 16777216;

        var mul = secret * 2048;
        secret ^= mul;
        secret %= 16777216;
        return secret;
    }

    record Delta(long[] values) {
        @Override
        public boolean equals(Object obj) {
            return Arrays.equals(values, ((Delta) obj).values);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(values);
        }

        @Override
        public String toString() {
            return "Delta[values=" + Arrays.toString(values) + "]";
        }
    }
}

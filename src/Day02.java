import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class Day02 {
    public static void main(String[] args) throws IOException {
        try (var input = Files.lines(Path.of("input02.txt"))) {
            var reports = input.map(l -> Arrays.stream(l.split(" "))
                    .mapToInt(Integer::parseInt)
                    .toArray())
                .toList();

            part1(reports);
            part2(reports);
        }
    }

    private static void part1(List<int[]> input) {
        var result = input.parallelStream()
            .filter(Day02::isSafe)
            .count();
        System.out.println(result);
    }

    private static void part2(List<int[]> input) {
        var result = input.parallelStream()
            .filter(report -> isSafe(report, 1))
            .count();
        System.out.println(result);
    }

    private static boolean isSafe(int[] input) {
        return isSafe(input, 0);
    }

    private static boolean isSafe(int[] ints, int tolerance) {
        var previous = ints[0];
        var order = Integer.compare(ints[0], ints[ints.length - 1]) +
            Integer.compare(ints[0], ints[1]) +
            Integer.compare(ints[1], ints[ints.length - 1]);
        for (int i = 1; i < ints.length; i++) {
            if (order < 0 && previous >= ints[i]) tolerance--;
            else if (order > 0 && previous <= ints[i]) tolerance--;
            else if (Math.abs(previous - ints[i]) > 3) tolerance--;
            else previous = ints[i];

            if (tolerance < 0) {
                return false;
            }
        }
        return true;
    }
}

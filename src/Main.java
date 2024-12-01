import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        var testInput = """
            3   4
            4   3
            2   5
            1   3
            3   9
            3   3""".split("\n");
        var input = Files.readAllLines(Path.of("input01.txt"));

        var array = parseInput(input);

        part1(array.left(), array.right());
        part2(array.left(), array.right());
    }

    private static Sides parseInput(List<String> input) {
        var left = new int[input.size()];
        var right = new int[input.size()];

        for (int i = 0; i < input.size(); i++) {
            var parts = input.get(i).split("\\s+");
            left[i] = Integer.parseInt(parts[0]);
            right[i] = Integer.parseInt(parts[1]);
        }
        Arrays.sort(left);
        Arrays.sort(right);
        return new Sides(left, right);
    }

    private static void part2(int[] left, int[] right) {
        var currentRight = 0;
        var score = 0;
        var seen = new HashMap<Integer, Integer>();
        for (int i : left) {
            while (currentRight < right.length && i > right[currentRight]) {
                currentRight++;
            }
            if (seen.containsKey(i)) {
                score += seen.get(i);
                continue;
            }
            int counter = 0;
            while (currentRight < right.length && i == right[currentRight]) {
                currentRight++;
                counter++;
            }
            if (!seen.containsKey(i)) seen.put(i, i * counter);
            score += counter * i;
        }
        System.out.println(score);
    }

    private static void part1(int[] left, int[] right) {
        var distance = 0;
        for (int i = 0; i < left.length; i++) {
            distance += Math.abs(left[i] - right[i]);
        }
        System.out.println(distance);
    }

    record Sides(int[] left, int[] right) {
    }
}
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class Day03 {
    public static void main(String[] args) throws IOException {
        var input = Files.readString(Path.of("input03.txt"));

        part1(input);
        part2(input);
    }

    private static void part1(String input) {
        var matcher = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)").matcher(input);

        var sum = 0;
        while (matcher.find()) {
            var a = Integer.parseInt(matcher.group(1));
            var b = Integer.parseInt(matcher.group(2));
            sum += a * b;
        }
        System.out.println(sum);
    }

    private static void part2(String input) {
        var pattern = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)|do\\(\\)|don't\\(\\)");
        var matcher = pattern.matcher(input);

        var enabled = true;
        var score = 0;
        while (matcher.find()) {
            var keyword = matcher.group();
            if ("don't()".equals(keyword)) {
                enabled = false;
            } else if ("do()".equals(keyword)) {
                enabled = true;
            } else if (enabled) {
                score += Integer.parseInt(matcher.group(1)) * Integer.parseInt(matcher.group(2));
            }
        }
        System.out.println(score);
    }
}

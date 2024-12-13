import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Day13 {
    private static final Pattern BUTTON = Pattern.compile("Button [AB]: X\\+(\\d+), Y\\+(\\d+)");
    private static final Pattern PRIZE = Pattern.compile("Prize: X=(\\d+), Y=(\\d+)");

    public static void main(String[] args) throws IOException {
        var input = Files.readString(Path.of("input13.txt"));

        var games = new ArrayList<Game>();
        for (var element : input.split("\n\n")) {
            var lines = element.split("\n");
            var aMatcher = BUTTON.matcher(lines[0]);
            aMatcher.find();
            var aButton = new Point(Integer.parseInt(aMatcher.group(1)), Integer.parseInt(aMatcher.group(2)));

            var bMatcher = BUTTON.matcher(lines[1]);
            bMatcher.find();
            var bButton = new Point(Integer.parseInt(bMatcher.group(1)), Integer.parseInt(bMatcher.group(2)));

            var prizeMatcher = PRIZE.matcher(lines[2]);
            prizeMatcher.find();
            var prize = new Point(Integer.parseInt(prizeMatcher.group(1)), Integer.parseInt(prizeMatcher.group(2)));

            games.add(new Game(aButton, bButton, prize));
        }

        part1(games);
        part2(games);
    }

    private static void part1(List<Game> games) {
        var sum = 0L;
        for (var game : games) {
            var score = solveCramer(game, false);
            if (score != Long.MAX_VALUE) {
                sum += score;
            }
        }
        System.out.println(sum);
    }

    private static void part2(List<Game> games) {
        var sum = 0L;
        for (var game : games) {
            var score = solveCramer(game, true);
            if (score != Long.MAX_VALUE) {
                sum += score;
            }
        }
        System.out.println(sum);
    }

    private static long solveCramer(Game game, boolean large) {
        long prizeX = game.prize.x;
        long prizeY = game.prize.y;
        if (large) {
            prizeX += 10000000000000L;
            prizeY += 10000000000000L;
        }

        var determinant = determinant(game.a.x, game.b.x, game.a.y, game.b.y);
        if (determinant == 0) return Long.MAX_VALUE;

        var determinantA = determinant(prizeX, prizeY, game.b.x, game.b.y);
        if (determinantA % determinant != 0) return Long.MAX_VALUE;

        var a = determinantA / determinant;

        var determinantB = determinant(game.a.x, game.a.y, prizeX, prizeY);
        if (determinantB % determinant != 0) return Long.MAX_VALUE;

        var b = determinantB / determinant;

        return a * 3 + b;
    }

    private static long determinant(long a, long b, long c, long d) {
        return a * d - b * c;
    }

    record Game(Point a, Point b, Point prize) {
    }

    record Point(int x, int y) {
    }
}

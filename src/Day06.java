import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class Day06 {
    private static final Point[] ORIENTATIONS = new Point[]{
        new Point(0, -1),
        new Point(1, 0),
        new Point(0, 1),
        new Point(-1, 0),
    };

    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Path.of("input06.txt"));

        var obstacles = new HashSet<Point>();
        Point start = null;

        var minX = Integer.MAX_VALUE;
        var maxX = Integer.MIN_VALUE;
        var minY = Integer.MAX_VALUE;
        var maxY = Integer.MIN_VALUE;
        for (int y = 0; y < input.size(); y++) {
            var line = input.get(y).toCharArray();
            for (int x = 0; x < line.length; x++) {
                if (line[x] == '#') {
                    obstacles.add(new Point(x, y));
                    if (x > maxX) maxX = x;
                    if (x < minX) minX = x;
                    if (y > maxY) maxY = y;
                    if (y < minY) minY = y;
                } else if (line[x] == '^') {
                    start = new Point(x, y);
                }
            }
        }

        assert start != null;

        part1(start, maxX, minX, maxY, minY, obstacles);
        part2(start, maxX, minX, maxY, minY, obstacles);
    }

    private static void part1(Point start, int maxX, int minX, int maxY, int minY, HashSet<Point> obstacles) {
        var orientation = 0;
        var guard = new Point(start.x, start.y);
        var visited = new HashSet<Point>();
        visited.add(guard);
        while (guard.x <= maxX && guard.x >= minX && guard.y <= maxY && guard.y >= minY) {
            var next = guard.plus(ORIENTATIONS[orientation]);
            while (obstacles.contains(next)) {
                orientation = (orientation + 1) % ORIENTATIONS.length;
                next = guard.plus(ORIENTATIONS[orientation]);
            }
            visited.add(next);
            guard = next;
        }
        System.out.println(visited.size() - 1);
    }

    private static void part2(Point start, int maxX, int minX, int maxY, int minY, Set<Point> obstacles) {
        var valid = IntStream.range(minY, maxY + 1)
            .mapToObj(y -> IntStream.range(minX, maxX + 1)
                .mapToObj(x -> new Point(x, y)))
            .flatMap(Function.identity())
            .parallel()
            .filter(Predicate.not(obstacles::contains))
            .filter(p -> !p.equals(start))
            .filter(p -> {
                var altSet = new HashSet<>(obstacles);
                altSet.add(p);
                return simulation(start, maxX, minX, maxY, minY, altSet);
            })
            .count();
        System.out.println(valid);
    }

    private static boolean simulation(Point start, int maxX, int minX, int maxY, int minY, Set<Point> obstacles) {
        var orientation = 0;
        var guard = new Point(start.x, start.y);
        var visited = new HashSet<OrientedPoint>();
        visited.add(new OrientedPoint(guard, orientation));
        while (guard.x <= maxX && guard.x >= minX && guard.y <= maxY && guard.y >= minY) {

            var next = guard.plus(ORIENTATIONS[orientation]);
            while (obstacles.contains(next)) {
                orientation = (orientation + 1) % ORIENTATIONS.length;
                next = guard.plus(ORIENTATIONS[orientation]);
            }
            if (visited.contains(new OrientedPoint(next, orientation))) break;
            visited.add(new OrientedPoint(next, orientation));
            guard = next;
        }
        return guard.x >= minX && guard.x <= maxX && guard.y >= minY && guard.y <= maxY;
    }

    record OrientedPoint(Point point, int orientation) {
    }

    record Point(int x, int y) {
        Point plus(Point other) {
            return new Point(other.x + x, other.y + y);
        }
    }
}

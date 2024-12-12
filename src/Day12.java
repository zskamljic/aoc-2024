import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class Day12 {
    private static final Point RIGHT = new Point(1, 0);
    private static final Point LEFT = new Point(-1, 0);
    private static final Point UP = new Point(0, -1);
    private static final Point DOWN = new Point(0, 1);

    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Path.of("input12.txt"));

        var areas = new ArrayList<Set<NamedPoint>>();

        var seen = new HashSet<Point>();
        for (int y = 0; y < input.size(); y++) {
            for (int x = 0; x < input.get(y).length(); x++) {
                var point = new Point(x, y);
                if (!seen.contains(point)) {
                    areas.add(floodFill(seen, input, point, input.get(y).charAt(x)));
                }
            }
        }

        var part1 = areas.stream()
            .mapToLong(Day12::areaAndPerimeter)
            .sum();
        System.out.println(part1);
        part2(areas);
    }

    private static void part2(List<Set<NamedPoint>> areas) {
        var sum = 0;
        for (var area : areas) {
            var value = sides(area) * area.size();
            sum += value;
        }
        System.out.println(sum);
    }

    private static int sides(Set<NamedPoint> area) {
        var allPoints = area.stream()
            .map(NamedPoint::point)
            .toList();

        var allCorners = 0;
        for (var point : allPoints) {
            var corners = 0;
            var up = allPoints.contains(point.plus(UP));
            var down = allPoints.contains(point.plus(DOWN));
            var left = allPoints.contains(point.plus(LEFT));
            var right = allPoints.contains(point.plus(RIGHT));

            // Convex corners
            if (!up && !left) corners++; // ┌
            if (!up && !right) corners++; // ┐
            if (!down && !left) corners++; // └
            if (!down && !right) corners++; // ┘

            var upLeft = allPoints.contains(point.plus(UP).plus(LEFT));
            var upRight = allPoints.contains(point.plus(UP).plus(RIGHT));
            var downLeft = allPoints.contains(point.plus(DOWN).plus(LEFT));
            var downRight = allPoints.contains(point.plus(DOWN).plus(RIGHT));

            // Concave corners
            if (!upLeft && up && left) corners++; // ┘
            if (!upRight && up && right) corners++; // └
            if (!downLeft && down && left) corners++; // ┐
            if (!downRight && down && right) corners++; // ┌

            allCorners += corners;
        }
        return allCorners;
    }

    private static Set<NamedPoint> floodFill(Set<Point> seen, List<String> input, Point point, char c) {
        var currentGroup = new HashSet<NamedPoint>();

        var candidates = new ArrayDeque<Point>();
        candidates.add(point);
        while (!candidates.isEmpty()) {
            var item = candidates.poll();
            if (seen.contains(item)) continue;

            seen.add(item);
            currentGroup.add(new NamedPoint(c, item));
            item.neighbours()
                .stream()
                .filter(Predicate.not(seen::contains))
                .filter(p -> p.x >= 0 && p.y >= 0 && p.y < input.size() && p.x < input.getFirst().length())
                .filter(p -> input.get(p.y()).charAt(p.x()) == c)
                .forEach(candidates::add);
        }
        return currentGroup;
    }

    static long areaAndPerimeter(Set<NamedPoint> points) {
        var area = points.size();
        var perimeter = 0L;
        for (var point : points) {
            perimeter += point.neighbours()
                .stream()
                .filter(Predicate.not(points::contains))
                .count();
        }
        return area * perimeter;
    }

    record Point(int x, int y) {
        public List<Point> neighbours() {
            return List.of(
                new Point(x - 1, y),
                new Point(x + 1, y),
                new Point(x, y - 1),
                new Point(x, y + 1)
            );
        }

        public Point plus(Point other) {
            return new Point(x + other.x, y + other.y);
        }
    }

    record NamedPoint(char name, Point point) implements Comparable<NamedPoint> {
        public List<NamedPoint> neighbours() {
            return point.neighbours()
                .stream()
                .map(p -> new NamedPoint(name, p))
                .toList();
        }

        @Override
        public int compareTo(NamedPoint o) {
            if (point.y < o.point.y) return -1;
            if (point.y > o.point.y) return 1;
            return Integer.compare(point.x, o.point.x);
        }
    }
}

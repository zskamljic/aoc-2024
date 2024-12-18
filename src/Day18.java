import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Day18 {
    private static final int SIZE = 71;
    private static final Point[] DIRECTIONS = {
        new Point(0, -1),
        new Point(1, 0),
        new Point(0, 1),
        new Point(-1, 0),
    };

    public static void main() throws IOException {
        var input = Files.lines(Path.of("input18.txt"));

        var fallenBytes = input.map(l -> l.split(","))
            .map(l -> new Point(Integer.parseInt(l[0]), Integer.parseInt(l[1])))
            .toList();

        part1(fallenBytes);
        part2(fallenBytes);
    }

    private static void part1(List<Point> fallenBytes) {
        var grid = new boolean[SIZE][SIZE];
        for (int i = 0; i < 1024; i++) {
            var currentPoint = fallenBytes.get(i);
            grid[currentPoint.y()][currentPoint.x()] = true;
        }

        System.out.println(dijkstra(grid));
    }

    private static void part2(List<Point> fallenBytes) {
        var grid = new boolean[SIZE][SIZE];
        for (var current : fallenBytes) {
            grid[current.y()][current.x()] = true;
            var result = dijkstra(grid);
            if (result == Integer.MAX_VALUE) {
                System.out.println(current.x + "," + current.y);
                break;
            }
        }
    }

    private static int dijkstra(boolean[][] grid) {
        var distances = new int[SIZE * SIZE];
        Arrays.fill(distances, Integer.MAX_VALUE);
        distances[0] = 0;
        var queue = new PriorityQueue<Point>(Comparator.comparing(p -> distances[p.y() * grid.length + p.x()]));
        queue.add(new Point(0, 0));

        while (!queue.isEmpty()) {
            var u = queue.poll();
            u.neighbours()
                .filter(Predicate.not(p -> grid[u.y()][u.x()]))
                .forEach(v -> {
                    var alt = distances[u.y() * grid.length + u.x()] + 1;
                    if (alt < distances[v.y() * grid.length + v.x()]) {
                        distances[v.y() * grid.length + v.x()] = alt;
                        queue.add(v);
                    }
                });
        }
        return distances[SIZE * SIZE - 1];
    }

    record Point(int x, int y) {
        public Point plus(Point other) {
            return new Point(other.x() + x, other.y() + y);
        }

        public Stream<Point> neighbours() {
            return Arrays.stream(DIRECTIONS)
                .map(this::plus)
                .filter(p -> p.x >= 0 && p.x < SIZE && p.y >= 0 && p.y < SIZE);
        }
    }
}

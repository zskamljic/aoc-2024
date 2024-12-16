import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

public class Day16 {
    private static final int UP = 0;
    private static final int RIGHT = 1;
    private static final int DOWN = 2;
    private static final int LEFT = 3;

    private static final Point[] DIRECTIONS = {
        new Point(0, -1),
        new Point(1, 0),
        new Point(0, 1),
        new Point(-1, 0),
    };

    public static void main(String[] args) throws IOException {
        var input = Files.readString(Path.of("input16.txt"));

        var grid = parseGrid(input);

        dijkstra(grid.grid, grid.start, grid.end);
    }

    private static void dijkstra(byte[][] grid, Point start, Point end) {
        var queue = new PriorityQueue<>(Comparator.comparing(State::score));

        var previous = new HashMap<RotatedPoint, List<RotatedPoint>>();
        var visited = new HashMap<RotatedPoint, Integer>();
        queue.add(new State(new RotatedPoint(start, RIGHT), 0, null));

        var bestScore = Integer.MAX_VALUE;

        while (!queue.isEmpty()) {
            var u = queue.poll();

            if (u.score() > bestScore) continue;

            if (visited.containsKey(u.point())) {
                if (u.score() == visited.get(u.point())) {
                    previous.computeIfAbsent(u.point(), ignored -> new ArrayList<>())
                        .add(u.previous());
                }
                continue;
            }
            visited.put(u.point(), u.score());
            if (u.previous() != null) {
                previous.put(u.point(), new ArrayList<>(List.of(u.previous())));
            }
            if (u.point().point().equals(end)) {
                if (u.score() < bestScore) bestScore = u.score();
                continue;
            }

            var forward = u.point().forward();
            if (isInBounds(forward.point(), grid)) {
                queue.add(new State(forward, u.score() + 1, u.point()));
            }

            for (var neighbor : u.point().rotated()) {
                queue.add(new State(neighbor, u.score() + 1000, u.point()));
            }
        }
        var bestPathVisited = new HashSet<Point>();
        var toVisit = new ArrayDeque<RotatedPoint>();
        previous.keySet()
            .stream()
            .filter(e -> e.point().equals(end))
            .forEach(toVisit::add);
        while (!toVisit.isEmpty()) {
            var current = toVisit.poll();
            bestPathVisited.add(current.point());

            var parents = previous.get(current);
            if (parents != null) {
                toVisit.addAll(parents);
            }
        }

        System.out.println(bestScore);
        System.out.println(bestPathVisited.size());
    }

    private static int rotatedIndex(int direction, int delta) {
        direction += delta;
        if (direction < 0) direction += 4;
        if (direction >= 4) direction -= 4;

        return direction;
    }

    private static boolean isInBounds(Point point, byte[][] grid) {
        return point.x >= 0 && point.y >= 0 && point.x < grid[0].length && point.y < grid.length &&
            grid[point.y][point.x] != '#';
    }

    private static Grid parseGrid(String input) {
        var lines = input.split("\n");
        var grid = new byte[lines.length][];

        Point start = null;
        Point end = null;
        for (var y = 0; y < grid.length; y++) {
            var line = lines[y];
            grid[y] = new byte[line.length()];
            var bytes = line.getBytes();
            for (var x = 0; x < bytes.length; x++) {
                grid[y][x] = switch (bytes[x]) {
                    case '#', '.' -> bytes[x];
                    case 'S' -> {
                        start = new Point(x, y);
                        yield '.';
                    }
                    case 'E' -> {
                        end = new Point(x, y);
                        yield '.';
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + (char) bytes[x]);
                };
            }
        }
        return new Grid(grid, start, end);
    }

    record Grid(byte[][] grid, Point start, Point end) {
    }

    record State(RotatedPoint point, int score, RotatedPoint previous) {
    }

    record RotatedPoint(Point point, int direction) {
        public RotatedPoint forward() {
            return new RotatedPoint(point.plus(DIRECTIONS[direction]), direction);
        }

        public List<RotatedPoint> rotated() {
            return List.of(
                new RotatedPoint(point, rotatedIndex(direction, -1)),
                new RotatedPoint(point, rotatedIndex(direction, 1))
            );
        }
    }

    record Point(int x, int y) {
        public Point plus(Point other) {
            return new Point(x + other.x, y + other.y);
        }
    }
}

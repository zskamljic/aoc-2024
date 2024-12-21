import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Day20 {
    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Path.of("input20.txt"));

        Point start = null;
        Point end = null;

        var grid = new char[input.size()][];
        for (int y = 0; y < input.size(); y++) {
            var line = input.get(y);
            var startX = line.indexOf('S');
            if (startX != -1) {
                start = new Point(startX, y);
            }
            var endX = line.indexOf('E');
            if (endX != -1) {
                end = new Point(endX, y);
            }
            grid[y] = line.replaceAll("[SE]", ".").toCharArray();
        }

        var result = dijkstra(grid, start, end);
        part1(result);
        part2(result);
    }

    private static void part1(PathResult result) {
        var cheats = new HashMap<Integer, Integer>();
        for (int i = 0; i < result.path().size() - 1; i++) {
            for (int j = i + 1; j < result.path().size(); j++) {
                var a = result.path().get(i);
                var b = result.path().get(j);

                var distance = a.distanceTo(b);
                if (distance != 2) continue;

                var distanceA = result.distances().get(a) + 2;
                var distanceB = result.distances().get(b);
                var skipped = distanceB - distanceA;
                if (skipped == 0) continue;

                cheats.merge(skipped, 1, Integer::sum);
            }
        }
        var solution = cheats.entrySet()
            .stream()
            .filter(e -> e.getKey() >= 100)
            .mapToInt(Map.Entry::getValue)
            .sum();
        System.out.println(solution);
    }

    private static void part2(PathResult result) {
        var cheats = new HashMap<Integer, Set<Cheat>>();
        for (int i = 0; i < result.path().size() - 1; i++) {
            for (int j = i + 1; j < result.path().size(); j++) {
                var a = result.path().get(i);
                var b = result.path().get(j);

                var distance = a.distanceTo(b);
                if (distance > 20) continue;

                var distanceA = result.distances().get(a) + distance;
                var distanceB = result.distances().get(b);
                var skipped = distanceB - distanceA;
                if (skipped == 0) continue;

                cheats.computeIfAbsent(skipped, ignored -> new HashSet<>())
                    .add(new Cheat(a, b));
            }
        }

        var solution = cheats.entrySet()
            .stream()
            .filter(e -> e.getKey() >= 100)
            .map(Map.Entry::getValue)
            .mapToInt(Set::size)
            .sum();
        System.out.println(solution);
    }

    private static PathResult dijkstra(char[][] grid, Point start, Point end) {
        var distances = new HashMap<Point, Integer>();
        var queue = new PriorityQueue<Point>(Comparator.comparingInt(distances::get));

        var previous = new HashMap<Point, Point>();
        distances.put(start, 0);
        queue.add(start);

        while (!queue.isEmpty()) {
            var u = queue.poll();
            for (var v : u.neighbours()) {
                if (grid[v.y][v.x] != '.') continue;

                var alt = distances.get(u) + 1;
                if (alt < distances.getOrDefault(v, Integer.MAX_VALUE)) {
                    distances.put(v, alt);
                    previous.put(v, u);
                    queue.add(v);
                }
            }
        }

        var path = new ArrayList<Point>();
        var pathQueue = new ArrayDeque<Point>();
        pathQueue.add(end);
        while (!pathQueue.isEmpty()) {
            var current = pathQueue.poll();
            if (!current.equals(start)) {
                pathQueue.add(previous.get(current));
            }
            path.addFirst(current);
        }
        return new PathResult(distances, path);
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

        public int distanceTo(Point end) {
            return Math.abs(end.x - x) + Math.abs(end.y - y);
        }
    }

    record Cheat(Point start, Point end) {
    }

    record PathResult(Map<Point, Integer> distances, List<Point> path) {
    }
}

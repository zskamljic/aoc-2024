import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Day10 {
    private static final Point[] DIRECTIONS = new Point[]{
        new Point(0, -1),
        new Point(0, 1),
        new Point(-1, 0),
        new Point(1, 0),
    };

    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Path.of("input10.txt"));

        var trailheads = new ArrayList<Point>();

        var grid = new char[input.size()][];
        for (int y = 0; y < input.size(); y++) {
            var line = input.get(y);
            grid[y] = line.toCharArray();
            var trailhead = line.indexOf('0');
            while (trailhead != -1) {
                trailheads.add(new Point(trailhead, y));
                trailhead = line.indexOf('0', trailhead + 1);
            }
        }

        var part1 = 0;
        var part2 = 0;
        for (var trailhead : trailheads) {
            var scores = findScore(grid, trailhead);
            part1 += scores.size();
            part2 += scores.values()
                .stream()
                .mapToInt(i -> i)
                .sum();
        }
        System.out.println(part1);
        System.out.println(part2);
    }

    private static Map<Point, Integer> findScore(char[][] grid, Point trailhead) {
        var reachedDestinations = new HashMap<Point, Integer>();

        var stack = new ArrayDeque<Point>();
        stack.push(trailhead);
        while (!stack.isEmpty()) {
            var point = stack.pop();

            for (var direction : DIRECTIONS) {
                var newX = point.x + direction.x;
                var newY = point.y + direction.y;
                if (newX < 0 || newY < 0 || newY >= grid.length || newX >= grid[0].length ||
                    grid[newY][newX] != grid[point.y][point.x] + 1) {
                    continue;
                }
                var next = new Point(newX, newY);
                if (grid[newY][newX] == '9') {
                    reachedDestinations.merge(next, 1, Integer::sum);
                } else {
                    stack.push(next);
                }
            }
        }

        return reachedDestinations;
    }

    record Point(int x, int y) {
    }
}

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.IntUnaryOperator;

public class Day08 {
    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Path.of("input08.txt"));

        var maxX = input.getFirst().length();
        var maxY = input.size();
        var antennas = parseAntennas(input);
        part1(antennas, maxX, maxY);
        part2(antennas, maxX, maxY);
    }

    private static void part1(Map<Byte, List<Point>> antennas, int maxX, int maxY) {
        var antinodes = new HashSet<Point>();
        for (var list : antennas.values()) {
            for (int i = 0; i < list.size() - 1; i++) {
                for (int j = i + 1; j < list.size(); j++) {
                    var a = list.get(i);
                    var b = list.get(j);
                    var deltaX = a.x - b.x;
                    if (deltaX != 0) {
                        var slope = (b.y - a.y) / ((float) b.x - a.x);
                        var intercept = a.y - slope * a.x;
                        IntUnaryOperator equation = x -> Math.round(slope * x + intercept);
                        antinodes.add(new Point(a.x + deltaX, equation.applyAsInt(a.x + deltaX)));
                        antinodes.add(new Point(b.x - deltaX, equation.applyAsInt(b.x - deltaX)));
                    } else { // Vertical line
                        var deltaY = b.y - a.y;
                        antinodes.add(new Point(a.x, a.y - deltaY));
                        antinodes.add(new Point(a.x, b.y + deltaY));
                    }
                }
            }
        }
        antinodes.removeIf(a -> a.x < 0 || a.x >= maxX || a.y < 0 || a.y >= maxY);
        System.out.println(antinodes.size());
    }

    private static void part2(Map<Byte, List<Point>> antennas, int maxX, int maxY) {
        var antinodes = new HashSet<Point>();
        for (var list : antennas.values()) {
            for (int i = 0; i < list.size() - 1; i++) {
                for (int j = i + 1; j < list.size(); j++) {
                    var a = list.get(i);
                    var b = list.get(j);
                    var deltaX = a.x - b.x;
                    if (deltaX != 0) {
                        var slope = (b.y - a.y) / ((float) b.x - a.x);
                        var intercept = a.y - slope * a.x;
                        IntUnaryOperator equation = x -> Math.round(slope * x + intercept);
                        var x = a.x;
                        var y = a.y;
                        while (x >= 0 && x < maxX && y >= 0 && y < maxY) {
                            antinodes.add(new Point(x, y));
                            x += deltaX;
                            y = equation.applyAsInt(x);
                        }
                        x = b.x;
                        y = b.y;
                        while (x >= 0 && x < maxX && y >= 0 && y < maxY) {
                            antinodes.add(new Point(x, y));
                            x -= deltaX;
                            y = equation.applyAsInt(x);
                        }
                    } else { // Vertical line
                        var deltaY = b.y - a.y;
                        var y = a.y;
                        while (y >= 0) {
                            antinodes.add(new Point(a.x, y));
                            y -= deltaY;
                        }
                        y = b.y;
                        while (y < maxY) {
                            antinodes.add(new Point(a.x, y));
                            y += deltaY;
                        }
                    }
                }
            }
        }
        System.out.println(antinodes.size());
    }

    private static Map<Byte, List<Point>> parseAntennas(List<String> input) {
        var allAntennas = new HashMap<Byte, List<Point>>();
        for (int y = 0; y < input.size(); y++) {
            var line = input.get(y).getBytes(StandardCharsets.UTF_8);
            for (int x = 0; x < line.length; x++) {
                if (line[x] != '.') allAntennas.merge(line[x], new ArrayList<>(List.of(new Point(x, y))), (a, b) -> {
                    a.addAll(b);
                    return a;
                });
            }
        }
        return allAntennas;
    }

    private record Point(int x, int y) {
    }
}

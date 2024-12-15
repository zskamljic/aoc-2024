import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day15 {
    private static final Point UP = new Point(0, -1);
    private static final Point DOWN = new Point(0, 1);
    private static final Point LEFT = new Point(-1, 0);
    private static final Point RIGHT = new Point(1, 0);

    public static void main(String[] args) throws IOException {
        var input = Files.readString(Path.of("input15.txt"));

        var parts = input.split("\n\n");

        var grid = parseGrid(parts[0]);
        var movements = parseMovements(parts[1]);

        part1(grid, movements, new ArrayList<>(grid.boxes));
        part2(grid, movements, new ArrayList<>(grid.boxes));
    }

    private static void part1(Grid grid, List<Point> movements, List<Point> boxes) {
        var robot = grid.robot;
        for (var movement : movements) {
            robot = move(grid.grid, boxes, robot, movement);
        }
        System.out.println(score(boxes));
    }

    private static void part2(Grid grid, List<Point> movements, List<Point> boxes) {
        var robot = new Point(grid.robot.x * 2, grid.robot.y);
        var wideGrid = extendWidth(grid.grid);
        var wideBoxes = widenBoxes(boxes);
        for (var movement : movements) {
            robot = moveWide(wideGrid, wideBoxes, robot, movement);
        }
        System.out.println(score(wideBoxes.stream().map(WideBox::left).toList()));
    }

    private static byte[][] extendWidth(byte[][] grid) {
        var newGrid = new byte[grid.length][grid[0].length * 2];
        for (var y = 0; y < grid.length; y++) {
            for (var x = 0; x < grid[0].length; x++) {
                newGrid[y][x * 2] = grid[y][x];
                newGrid[y][x * 2 + 1] = grid[y][x];
            }
        }
        return newGrid;
    }

    private static List<WideBox> widenBoxes(List<Point> boxes) {
        return boxes.stream()
            .map(p -> new WideBox(new Point(p.x * 2, p.y), new Point(p.x * 2 + 1, p.y)))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    private static int score(List<Point> boxes) {
        var score = 0;
        for (var box : boxes) {
            score += box.x + box.y * 100;
        }
        return score;
    }

    private static Point move(byte[][] grid, List<Point> boxes, Point robot, Point movement) {
        var targetMovement = robot.plus(movement);
        if (grid[targetMovement.y][targetMovement.x] == '#') return robot;

        var movedBox = new Point(targetMovement.x, targetMovement.y);
        var toRemove = new ArrayList<Point>();
        var toAdd = new ArrayList<Point>();
        while (boxes.contains(movedBox)) {
            toRemove.add(movedBox);
            movedBox = movedBox.plus(movement);
            if (grid[movedBox.y][movedBox.x] == '#') return robot;
            toAdd.add(movedBox);
        }
        boxes.removeAll(toRemove);
        boxes.addAll(toAdd);
        return targetMovement;
    }

    private static Point moveWide(byte[][] grid, List<WideBox> boxes, Point robot, Point movement) {
        var targetMovement = robot.plus(movement);
        if (grid[targetMovement.y][targetMovement.x] == '#') return robot;

        var collisionPoints = List.of(new Point(targetMovement.x, targetMovement.y));
        var toRemove = new HashSet<WideBox>();
        var toAdd = new HashSet<WideBox>();
        while (collides(boxes, collisionPoints)) {
            var finalPoints = collisionPoints;
            var removeCandidates = boxes.stream()
                .filter(Predicate.not(toRemove::contains))
                .filter(b -> b.collides(finalPoints))
                .toList();
            toRemove.addAll(removeCandidates);

            var moved = removeCandidates.stream()
                .map(b -> b.plus(movement))
                .toList();
            collisionPoints = moved.stream()
                .flatMap(b -> Stream.of(b.left, b.right))
                .toList();
            if (collisionPoints.stream().anyMatch(p -> grid[p.y][p.x] == '#')) return robot;
            toAdd.addAll(moved);
        }
        boxes.removeAll(toRemove);
        boxes.addAll(toAdd);
        return targetMovement;
    }

    private static boolean collides(List<WideBox> boxes, List<Point> collisionPoints) {
        return boxes.stream().anyMatch(b -> b.collides(collisionPoints));
    }

    private static Grid parseGrid(String part) {
        var boxes = new ArrayList<Point>();
        Point robot = null;

        var lines = part.split("\n");
        var grid = new byte[lines.length][];
        for (int y = 0; y < lines.length; y++) {
            var line = lines[y].toCharArray();
            grid[y] = new byte[line.length];
            for (int x = 0; x < line.length; x++) {
                grid[y][x] = switch (line[x]) {
                    case '#' -> '#';
                    case 'O' -> {
                        boxes.add(new Point(x, y));
                        yield '.';
                    }
                    case '@' -> {
                        robot = new Point(x, y);
                        yield '.';
                    }
                    default -> '.';
                };
            }
        }
        return new Grid(grid, boxes, robot);
    }

    private static List<Point> parseMovements(String part) {
        var movements = new ArrayList<Point>();
        for (var c : part.toCharArray()) {
            if (c == '\n') continue;

            var movement = switch (c) {
                case '^' -> UP;
                case 'v' -> DOWN;
                case '<' -> LEFT;
                case '>' -> RIGHT;
                default -> throw new IllegalArgumentException();
            };
            movements.add(movement);
        }
        return movements;
    }

    record Grid(byte[][] grid, List<Point> boxes, Point robot) {
    }

    record Point(int x, int y) {
        public Point plus(Point other) {
            return new Point(other.x + x, other.y + y);
        }
    }

    record WideBox(Point left, Point right) {
        public WideBox plus(Point other) {
            return new WideBox(left.plus(other), right.plus(other));
        }

        public boolean collides(List<Point> points) {
            return points.contains(left) || points.contains(right);
        }
    }
}

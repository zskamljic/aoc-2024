import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day21 {
    private static final Map<Character, Point> NUMPAD = Map.ofEntries(
        Map.entry('7', new Point(0, 0)),
        Map.entry('8', new Point(1, 0)),
        Map.entry('9', new Point(2, 0)),
        Map.entry('4', new Point(0, 1)),
        Map.entry('5', new Point(1, 1)),
        Map.entry('6', new Point(2, 1)),
        Map.entry('1', new Point(0, 2)),
        Map.entry('2', new Point(1, 2)),
        Map.entry('3', new Point(2, 2)),
        Map.entry('0', new Point(1, 3)),
        Map.entry('A', new Point(2, 3))
    );
    private static final Map<Character, Point> DIRECTION = Map.of(
        '^', new Point(1, 0),
        'A', new Point(2, 0),
        '<', new Point(0, 1),
        'v', new Point(1, 1),
        '>', new Point(2, 1)
    );

    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Path.of("input21.txt"));

        runPart(input, 2);
        runPart(input, 25);
    }

    private static void runPart(List<String> input, int steps) {
        var sum = 0L;
        for (var line : input) {
            var current = enterDigits(line, steps);
            sum += current;
        }
        System.out.println(sum);
    }

    private static long enterDigits(String sequence, int steps) {
        var previous = NUMPAD.get('A');

        var cache = new HashMap<State, Long>();

        int initialMultiplier = Integer.parseInt(sequence.substring(0, 3));
        var sequenceResult = 0L;
        for (var c : sequence.toCharArray()) {
            var destination = NUMPAD.get(c);
            var delta = new Point(previous.x - destination.x, previous.y - destination.y);
            var current = previous;
            previous = destination;
            if (current.y == 3 && destination.x == 0) {
                sequenceResult += enterArrows(delta, steps, false, cache);
            } else if (current.x == 0 && destination.y == 3) {
                sequenceResult += enterArrows(delta, steps, true, cache);
            } else {
                sequenceResult += Math.min(
                    enterArrows(delta, steps, true, cache),
                    enterArrows(delta, steps, false, cache)
                );
            }
        }

        return initialMultiplier * sequenceResult;
    }

    private static long enterArrows(Point delta, int steps, boolean horizontalFirst, Map<State, Long> cache) {
        var state = new State(delta, steps, horizontalFirst);
        if (cache.containsKey(state)) {
            return cache.get(state);
        }

        int absY = Math.abs(delta.y);
        int absX = Math.abs(delta.x);

        var chunk = new StringBuilder();
        chunk.append((delta.y > 0 ? "^" : "v").repeat(absY));
        chunk.append((delta.x > 0 ? "<" : ">").repeat(absX));

        if (horizontalFirst) {
            chunk.reverse();
        }

        chunk.append('A');

        var result = 0L;
        if (steps == 0) {
            result = chunk.length();
        } else {
            var previous = DIRECTION.get('A');

            for (var c : chunk.toString().toCharArray()) {
                var next = DIRECTION.get(c);
                var position = previous;
                previous = next;
                var newDelta = new Point(position.x - next.x, position.y - next.y);
                if (newDelta.y == 0 || newDelta.x == 0) {
                    result += enterArrows(newDelta, steps - 1, false, cache);
                } else if (next.y == 1 && next.x == 0 && position.y == 0) {
                    result += enterArrows(newDelta, steps - 1, false, cache);
                } else if (position.y == 1 && position.x == 0 && next.y == 0) {
                    result += enterArrows(newDelta, steps - 1, true, cache);
                } else {
                    result += Math.min(
                        enterArrows(newDelta, steps - 1, false, cache),
                        enterArrows(newDelta, steps - 1, true, cache)
                    );
                }
            }
        }

        cache.put(state, result);
        return result;
    }

    record State(Point delta, int steps, boolean horizontalFirst) {
    }

    record Point(int x, int y) {
    }
}

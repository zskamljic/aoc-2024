import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;

public class Day07 {
    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Path.of("input07.txt"));

        var equations = parseEquations(input);
        part1(equations);
        part2(equations);
    }

    private static void part1(List<Equation> equations) {
        var calibrationResult = equations.parallelStream()
            .filter(e -> e.evaluates(false))
            .mapToLong(Equation::result)
            .sum();
        System.out.println(calibrationResult);
    }

    private static void part2(List<Equation> equations) {
        var calibrationResult = equations.parallelStream()
            .filter(e -> e.evaluates(true))
            .mapToLong(Equation::result)
            .sum();
        System.out.println(calibrationResult);
    }

    private static List<Equation> parseEquations(List<String> input) {
        return input.stream()
            .map(Equation::parse)
            .toList();
    }

    record Equation(long result, List<Long> numbers) {
        public boolean evaluates(boolean concat) {
            var stack = new ArrayDeque<EvaluationState>();
            stack.add(new EvaluationState(numbers.getFirst(), numbers.subList(1, numbers.size())));
            while (!stack.isEmpty()) {
                var state = stack.pop();
                var sum = state.result() + state.remaining().getFirst();
                var product = state.result() * state.remaining().getFirst();
                var concated = state.result() * (long) Math.pow(10, 1 + Math.floor(Math.log10(state.remaining().getFirst()))) + state.remaining().getFirst();
                if (state.remaining().size() == 1) {
                    if (sum == result || product == result || (concat && concated == result)) return true;
                    continue;
                }

                if (sum <= result) stack.push(new EvaluationState(sum, state.remaining().subList(1, state.remaining().size())));
                if (product <= result) stack.push(new EvaluationState(product, state.remaining().subList(1, state.remaining().size())));
                if (concat && concated <= result) stack.push(new EvaluationState(concated, state.remaining().subList(1, state.remaining().size())));
            }
            return false;
        }

        static Equation parse(String equation) {
            var parts = equation.split(":? ");

            return new Equation(Long.parseLong(parts[0]), Arrays.stream(parts).skip(1).map(Long::parseLong).toList());
        }
    }

    record EvaluationState(long result, List<Long> remaining) {
    }
}

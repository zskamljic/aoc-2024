import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day17 {
    public static void main(String[] args) throws IOException {
        var input = Files.readString(Path.of("input17.txt"));

        var state = parseState(input);

        part1(state);
        part2(state);
    }

    private static void part2(State state) {
        var a = 0L;
        // Searching for each digit
        var expectedOutput = new ArrayList<Long>();
        for (int i = state.program.length - 1; i >= 0; i--) {
            a <<= 3; // 3 bit integers, when searching for the next shift left
            expectedOutput.addFirst((long) state.program[i]);
            // Find exact integer
            while (!expectedOutput.equals(execute(a, state.b, state.c, state.program))) {
                a++;
            }
        }
        System.out.println(a);
    }

    private static void part1(State state) {
        var part1 = execute(state.a, state.b, state.c, state.program);
        System.out.println(part1.stream().map(String::valueOf).collect(Collectors.joining(",")));
    }

    private static List<Long> execute(long a, long b, long c, int[] program) {
        var output = new ArrayList<Long>();
        var pc = 0;
        while (pc < program.length) {
            var instruction = program[pc];
            var argument = program[pc + 1];

            switch (instruction) {
                case 0 -> a /= pow2(a, b, c, argument); // ADV
                case 1 -> b ^= argument;// BXL
                case 2 -> b = resolve(a, b, c, argument) % 8; // BST
                case 3 -> {
                    // JNZ
                    if (a != 0) {
                        pc = argument;
                        continue;
                    }
                }
                case 4 -> b ^= c; // BXC
                case 5 -> output.add(resolve(a, b, c, argument) % 8); // OUT
                case 6 -> b = a / pow2(a, b, c, argument); // BDV
                case 7 -> c = a / pow2(a, b, c, argument); // CDV
            }

            pc += 2;
        }
        return output;
    }

    private static long pow2(long a, long b, long c, int argument) {
        var resolved = resolve(a, b, c, argument) - 1;
        if (resolved < 0) return 1;
        return 2 << resolved;
    }

    private static long resolve(long a, long b, long c, int argument) {
        return switch (argument) {
            case 0 -> 0;
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 3;
            case 4 -> a;
            case 5 -> b;
            case 6 -> c;
            default -> throw new IllegalStateException("Unexpected value: " + argument);
        };
    }

    private static State parseState(String input) {
        var lines = input.split("\n");
        var a = Integer.parseInt(lines[0].substring(12));
        var b = Integer.parseInt(lines[1].substring(12));
        var c = Integer.parseInt(lines[2].substring(12));

        var program = Arrays.stream(lines[4].substring(9).split(","))
            .mapToInt(Integer::parseInt)
            .toArray();

        return new State(a, b, c, program);
    }

    record State(int a, int b, int c, int[] program) {
    }
}

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day24 {
    private static final Pattern INSTRUCTION = Pattern.compile("(\\w+) (AND|XOR|OR) (\\w+) -> (\\w+)");

    public static void main(String[] args) throws IOException {
        var input = """
            x00: 0
            x01: 1
            x02: 0
            x03: 1
            x04: 0
            x05: 1
            y00: 0
            y01: 0
            y02: 1
            y03: 1
            y04: 0
            y05: 1
            
            x00 AND y00 -> z05
            x01 AND y01 -> z02
            x02 AND y02 -> z01
            x03 AND y03 -> z03
            x04 AND y04 -> z04
            x05 AND y05 -> z00""";
        input =
            Files.readString(Path.of("input24-alt.txt"));

        var parts = input.split("\n\n");
        var registers = parseRegisters(parts[0]);
        var instructions = parseInstructions(parts[1]);

        part1(instructions, new HashMap<>(registers));
        part2(instructions, registers);
    }

    private static void part1(List<Instruction> instructions, Map<String, Integer> registers) {
        execute(instructions, registers);

        var result = getNumber(registers, "z");
        System.out.println(result);
    }

    private static void execute(List<Instruction> instructions, Map<String, Integer> registers) {
        var queue = new ArrayDeque<>(instructions);
        while (!queue.isEmpty()) {
            var instruction = queue.poll();
            if (!registers.containsKey(instruction.a) || !registers.containsKey(instruction.b)) {
                queue.add(instruction);
                continue;
            }

            var a = registers.get(instruction.a);
            var b = registers.get(instruction.b);

            switch (instruction.op) {
                case "AND" -> registers.put(instruction.c, a & b);
                case "OR" -> registers.put(instruction.c, a | b);
                case "XOR" -> registers.put(instruction.c, a ^ b);
            }
        }
    }

    private static long getNumber(Map<String, Integer> registers, String register) {
        var bitset = new BitSet();
        registers.entrySet()
            .stream()
            .filter(entry -> entry.getKey().startsWith(register))
            .forEach(entry -> {
                var value = entry.getValue();

                if (value == 1) {
                    var key = Integer.parseInt(entry.getKey().substring(1));
                    bitset.set(key);
                }
            });
        return bitset.toLongArray()[0];
    }

    private static void part2(List<Instruction> instructions, Map<String, Integer> registers) {
        var x = getNumber(registers, "x");
        var y = getNumber(registers, "y");
        var expectedZ = x + y;

        execute(instructions, registers);

        var actualZ = getNumber(registers, "z");

        var binaryExpected = Long.toBinaryString(expectedZ);
        var binaryActual = Long.toBinaryString(actualZ);
        System.out.println("          " + "3210" + "9876543210".repeat(6));
        System.out.println("Expected: " + "0".repeat(64 - binaryExpected.length()) + binaryExpected);
        System.out.println("Actual:   " + "0".repeat(64 - binaryActual.length()) + binaryActual);

        var invalidBits = BitSet.valueOf(new long[]{expectedZ});
        var actualBits = BitSet.valueOf(new long[]{actualZ});
        invalidBits.xor(actualBits);

        System.out.println(invalidBits);

        var output = new StringBuilder();
        output.append("digraph {\n");
        for (var instruction : instructions) {
            output.append("  {").append(instruction.a).append(",").append(instruction.b).append("} -> ").append(instruction.c)
                .append(" [color=").append(switch (instruction.op) {
                    case "AND" -> "red";
                    case "OR" -> "green";
                    case "XOR" -> "blue";
                    default -> throw new IllegalStateException("Unexpected value: " + instruction.op);
                }).append("];\n");
        }
        output.append("}\n");
        try {
            Files.writeString(Path.of("day24.txt"), output.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var swapped = List.of("z10", "mwk", "z18","qgd", "jmh", "hsw", "z33", "gqp");
        var joined = swapped.stream()
            .sorted()
            .collect(Collectors.joining(","));
        System.out.println(joined);
    }

    private static Map<String, Integer> parseRegisters(String part) {
        return part.lines()
            .map(l -> l.split(": "))
            .collect(Collectors.toMap(a -> a[0], a -> Integer.parseInt(a[1])));
    }

    private static List<Instruction> parseInstructions(String instructions) {
        var matcher = INSTRUCTION.matcher(instructions);
        var instructionList = new ArrayList<Instruction>();
        while (matcher.find()) {
            instructionList.add(new Instruction(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4)));
        }
        return instructionList;
    }

    record Instruction(String a, String op, String b, String c) {
    }
}

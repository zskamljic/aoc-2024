import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day25 {
    public static void main(String[] args) throws IOException {
        var input = """
            #####
            .####
            .####
            .####
            .#.#.
            .#...
            .....
            
            #####
            ##.##
            .#.##
            ...##
            ...#.
            ...#.
            .....
            
            .....
            #....
            #....
            #...#
            #.#.#
            #.###
            #####
            
            .....
            .....
            #.#..
            ###..
            ###.#
            ###.#
            #####
            
            .....
            .....
            .....
            #....
            #.#..
            #.#.#
            #####""".split("\n\n");
        input = Files.readString(Path.of("input25.txt")).split("\n\n");

        var inputs = parseInputs(input);
        var fits = 0;
        for (var lock : inputs.locks()) {
            key:
            for (var key : inputs.keys()) {
                for (int i = 0; i < 5; i++) {
                    var sum = lock[i] + key[i];
                    if (sum > 5) continue key;
                }
                fits++;
            }
        }
        System.out.println(fits);
    }

    private static Inputs parseInputs(String[] inputs) {
        var locks = new ArrayList<int[]>();
        var keys = new ArrayList<int[]>();

        for (var input : inputs) {
            if (input.startsWith("#####")) {
                locks.add(parseLock(input));
            } else {
                keys.add(parseKey(input));
            }
        }
        return new Inputs(locks, keys);
    }

    public static int[] parseLock(String input) {
        var array = input.toCharArray();
        var result = new int[5];
        for (int i = 0; i < 5; i++) {
            int j;
            for (j = 0; j < 6; j++) {
                if (array[6 + i + j * 6] == '.') break;
            }
            result[i] = j;
        }
        return result;
    }

    public static int[] parseKey(String input) {
        var array = input.toCharArray();
        var result = new int[5];
        for (int i = 0; i < 5; i++) {
            int j;
            for (j = 0; j < 6; j++) {
                if (array[30 + i - j * 6] == '.') break;
            }
            result[i] = j;
        }
        return result;
    }

    record Inputs(List<int[]> locks, List<int[]> keys) {
        @Override
        public String toString() {
            return "Inputs[locks=" + locks.stream().map(Arrays::toString).toList() + ", keys=" + keys.stream().map(Arrays::toString).toList() + "]";
        }
    }
}

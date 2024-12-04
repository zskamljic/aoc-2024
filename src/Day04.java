import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class Day04 {
    private static final char[] MAS = {'M', 'A', 'S'};
    private static final int UP = 0;
    private static final int DOWN = 1;
    private static final int LEFT = 2;
    private static final int RIGHT = 3;
    private static final int UP_LEFT = 4;
    private static final int DOWN_LEFT = 5;
    private static final int UP_RIGHT = 6;
    private static final int DOWN_RIGHT = 7;

    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Path.of("/home/zan/Downloads/input04.txt")).toArray(String[]::new);

        var grid = new char[input.length][];
        for (int y = 0; y < input.length; y++) {
            grid[y] = input[y].toCharArray();
        }

        part1(grid);
        part2(grid);
    }

    private static void part1(char[][] grid) {
        var found = 0;
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x] == 'X') {
                    found += searchAll(grid, x, y);
                }
            }
        }
        System.out.println(found);
    }

    private static void part2(char[][] grid) {
        var found = 0;
        for (int y = 0; y < grid.length - 2; y++) {
            for (int x = 0; x < grid[y].length - 2; x++) {
                if (grid[y + 1][x + 1] == 'A') {
                    found += checkPart(grid, x, y);
                }
            }
        }
        System.out.println(found);
    }

    private static int searchAll(char[][] grid, int x, int y) {
        var directions = new boolean[8];
        Arrays.fill(directions, true);
        var score = 8;
        for (int i = 1; i < 4; i++) {
            if (directions[RIGHT] && (x + i >= grid[y].length || grid[y][x + i] != MAS[i - 1])) {
                directions[RIGHT] = false;
                score--;
            }
            if (directions[UP_RIGHT] && (x + i >= grid[y].length || y - i < 0 || grid[y - i][x + i] != MAS[i - 1])) {
                directions[UP_RIGHT] = false;
                score--;
            }
            if (directions[DOWN_RIGHT] && (x + i >= grid[y].length || y + i >= grid.length || grid[y + i][x + i] != MAS[i - 1])) {
                directions[DOWN_RIGHT] = false;
                score--;
            }
            if (directions[LEFT] && (x - i < 0 || grid[y][x - i] != MAS[i - 1])) {
                directions[LEFT] = false;
                score--;
            }
            if (directions[UP_LEFT] && (x - i < 0 || y - i < 0 || grid[y - i][x - i] != MAS[i - 1])) {
                directions[UP_LEFT] = false;
                score--;
            }
            if (directions[DOWN_LEFT] && (x - i < 0 || y + i >= grid.length || grid[y + i][x - i] != MAS[i - 1])) {
                directions[DOWN_LEFT] = false;
                score--;
            }
            if (directions[DOWN] && (y + i >= grid.length || grid[y + i][x] != MAS[i - 1])) {
                directions[DOWN] = false;
                score--;
            }
            if (directions[UP] && (y - i < 0 || grid[y - i][x] != MAS[i - 1])) {
                directions[UP] = false;
                score--;
            }
        }

        return score;
    }

    private static int checkPart(char[][] grid, int x, int y) {
        var directions = new boolean[4];
        Arrays.fill(directions, true);
        var found = 4;
        for (int i = 0; i < 3; i++) {
            // Down right
            if (directions[0] && grid[y + i][x + i] != MAS[i]) {
                directions[0] = false;
                found--;
            }
            // Up right
            if (directions[1] && grid[y + 2 - i][x + i] != MAS[i]) {
                directions[1] = false;
                found--;
            }
            // Down left
            if (directions[2] && grid[y + i][x + 2 - i] != MAS[i]) {
                directions[2] = false;
                found--;
            }
            // Up left
            if (directions[3] && grid[y + 2 - i][x + 2 - i] != MAS[i]) {
                directions[3] = false;
                found--;
            }
        }
        if (found == 2) {
            return 1;
        }
        return 0;
    }
}

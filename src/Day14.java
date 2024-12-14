import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Day14 {
    private static final Pattern ROBOT = Pattern.compile("p=(-?\\d+),(-?\\d+) v=(-?\\d+),(-?\\d+)");

    private static final int WIDTH = 101;
    private static final int HEIGHT = 103;

    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Path.of("input14.txt"));

        List<Robot> robots = new ArrayList<>();
        for (var line : input) {
            var matcher = ROBOT.matcher(line);
            matcher.find();

            var x = Integer.parseInt(matcher.group(1));
            var y = Integer.parseInt(matcher.group(2));
            var vx = Integer.parseInt(matcher.group(3));
            var vy = Integer.parseInt(matcher.group(4));

            robots.add(new Robot(new Point(x, y), new Point(vx, vy)));
        }

        var result = part1(robots);
        System.out.println(result);
        part2(robots);
    }

    private static int part1(List<Robot> robots) {
        for (int i = 0; i < 100; i++) {
            robots = simulate(robots);
        }

        return score(robots);
    }

    private static void part2(List<Robot> robots) throws IOException {
        var step = 0;
        while (robotsOverlap(robots)) {
            step++;
            robots = simulate(robots);
        }
        var image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_BINARY);
        var graphics = image.createGraphics();
        graphics.setColor(Color.WHITE);
        for (var robot : robots) {
            var point = robot.position();
            graphics.fillRect(point.x, point.y, 1, 1);
        }
        ImageIO.write(image, "png", new File("output14.png"));
        System.out.println(step - 1);
    }

    private static boolean robotsOverlap(List<Robot> robots) {
        return robots.stream()
            .map(Robot::position)
            .distinct()
            .count() != robots.size();
    }

    private static int score(List<Robot> robots) {
        var q0 = 0;
        var q1 = 0;
        var q2 = 0;
        var q3 = 0;
        for (var robot : robots) {
            if (robot.position().x() == WIDTH / 2 || robot.position().y() == HEIGHT / 2) continue;

            if (robot.position().x() < WIDTH / 2) {
                if (robot.position().y() < HEIGHT / 2) q0++;
                if (robot.position().y() > HEIGHT / 2) q1++;
            }
            if (robot.position().x() > WIDTH / 2) {
                if (robot.position().y() < HEIGHT / 2) q2++;
                if (robot.position().y() > HEIGHT / 2) q3++;
            }
        }
        return q0 * q1 * q2 * q3;
    }

    private static List<Robot> simulate(List<Robot> robots) {
        return robots.stream()
            .map(robot -> {
                var newX = robot.position().x() + robot.velocity().x();
                var newY = robot.position().y() + robot.velocity().y();

                if (newX >= WIDTH) newX -= WIDTH;
                if (newY >= HEIGHT) newY -= HEIGHT;
                if (newX < 0) newX += WIDTH;
                if (newY < 0) newY += HEIGHT;
                return new Robot(new Point(newX, newY), robot.velocity());
            })
            .toList();
    }

    record Robot(Point position, Point velocity) {
    }

    record Point(int x, int y) {
    }
}

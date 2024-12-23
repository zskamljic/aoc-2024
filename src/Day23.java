import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Day23 {
    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Path.of("input23.txt"));

        var groups = new HashMap<String, Set<String>>();
        for (var line : input) {
            var nodes = line.split("-");

            var setA = groups.computeIfAbsent(nodes[0], k -> new HashSet<>());
            var setB = groups.computeIfAbsent(nodes[1], k -> new HashSet<>());
            setA.add(nodes[1]);
            setB.add(nodes[0]);
        }

        part1(groups);
        part2(groups);
    }

    private static void part1(HashMap<String, Set<String>> groups) {
        var tripplets = new HashSet<Set<String>>();
        for (var entry : groups.entrySet()) {
            var key = entry.getKey();
            if (!key.startsWith("t")) continue;

            var group = entry.getValue();

            var queue = new ArrayDeque<List<String>>();
            for (var item : group) {
                var visited = new ArrayList<String>();
                visited.add(key);
                visited.add(item);
                queue.add(visited);
            }

            while (!queue.isEmpty()) {
                var current = queue.poll();
                var connectedTo = groups.get(current.getLast());
                if (current.size() == 3) {
                    if (connectedTo.contains(key)) {
                        tripplets.add(new HashSet<>(current));
                    }
                    continue;
                }

                for (var connection : connectedTo) {
                    if (current.contains(connection)) continue;

                    var newPath = new ArrayList<>(current);
                    newPath.add(connection);
                    queue.add(newPath);
                }
            }
        }
        System.out.println(tripplets.size());
    }

    private static void part2(Map<String, Set<String>> groups) {
        var allComputers = new HashSet<>(groups.keySet());
        var connectedSet = new HashSet<String>();
        var largestSet = findLargestSet(allComputers, groups, connectedSet);

        var result = largestSet.stream()
            .sorted()
            .collect(Collectors.joining(","));
        System.out.println(result);
    }

    private static Set<String> findLargestSet(Set<String> computers, Map<String, Set<String>> groups, Set<String> connectedSet) {
        Set<String> largestSet = new HashSet<>(connectedSet);
        if (computers.isEmpty()) return connectedSet;

        for (var iterator = computers.iterator(); iterator.hasNext(); ) {
            var current = iterator.next();
            connectedSet.add(current);

            var remainingCandidates = new HashSet<>(computers);
            remainingCandidates.retainAll(groups.get(current));

            var result = new HashSet<>(findLargestSet(remainingCandidates, groups, connectedSet));

            if (result.size() > largestSet.size()) largestSet = result;

            connectedSet.remove(current);
            iterator.remove();
        }
        return largestSet;
    }
}

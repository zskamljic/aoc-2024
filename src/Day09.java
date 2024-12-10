import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Day09 {
    public static void main(String[] args) throws IOException {
        var input = Files.readString(Path.of("input09.txt"));

        var id = 0;
        var totalLength = 0;
        var ranges = new ArrayList<IdRange>();
        var gaps = new ArrayList<IdRange>();
        boolean empty = false;
        for (var c : input.toCharArray()) {
            var length = c - '0';
            if (!empty) {
                ranges.add(new IdRange(id, totalLength, totalLength + length));
                id++;
            } else {
                gaps.add(new IdRange(-1, totalLength, totalLength + length));
            }
            empty = !empty;
            totalLength += length;
        }
        defragment(new ArrayList<>(ranges), new ArrayList<>(gaps));
        defragmentWhole(ranges, gaps);
    }

    private static void defragment(List<IdRange> ranges, List<IdRange> gaps) {
        while (!gaps.isEmpty()) {
            var gap = gaps.removeFirst();
            var lastBlock = ranges.removeLast();
            if (gap.start() > lastBlock.end()) {
                ranges.add(lastBlock);
                break;
            }
            if (gap.size() > lastBlock.size()) {
                var movedBlock = new IdRange(lastBlock.id(), gap.start(), gap.start() + lastBlock.size());
                ranges.addFirst(movedBlock);
                var remainder = new IdRange(-1, movedBlock.end(), gap.end());
                gaps.addFirst(remainder);
            } else if (gap.size() < lastBlock.size()) {
                var movedBlock = new IdRange(lastBlock.id(), gap.start(), gap.end());
                ranges.addFirst(movedBlock);
                var remainder = new IdRange(lastBlock.id(), lastBlock.start(), lastBlock.end() - gap.size());
                ranges.add(remainder);
            } else {
                ranges.addFirst(new IdRange(lastBlock.id(), gap.start(), gap.end()));
            }
        }
        System.out.println(checksum(ranges));
    }

    private static void defragmentWhole(List<IdRange> ranges, List<IdRange> gaps) {
        range:
        for (int i = ranges.size() - 1; i >= 0; i--) {
            var file = ranges.get(i);
            for (int gapIndex = 0; gapIndex < gaps.size(); gapIndex++) {
                var gap = gaps.get(gapIndex);
                if (gap.start() > file.start()) break;
                if (gap.size() >= file.size()) {
                    gaps.remove(gapIndex);
                    var movedFile = new IdRange(file.id(), gap.start(), gap.start() + file.size());
                    ranges.set(i, movedFile);
                    gaps.add(new IdRange(-1, file.start(), file.end()));
                    var newGapSize = gaps.size() - file.size();
                    if (newGapSize > 0) {
                        gaps.add(gapIndex, new IdRange(-1, movedFile.end(), gap.end()));
                    }
                    continue range;
                }
            }
        }
        System.out.println(checksum(ranges));
    }

    private static long checksum(List<IdRange> ranges) {
        var checksum = 0L;
        ranges.sort(Comparator.comparing(IdRange::start));
        for (var range : ranges) {
            for (long i = 0; i < range.size(); i++) {
                checksum += (range.start() + i) * range.id();
            }
        }
        return checksum;
    }

    record IdRange(int id, int start, int end) {
        int size() {
            return end - start;
        }
    }
}

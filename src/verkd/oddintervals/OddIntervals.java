package verkd.oddintervals;

import com.beust.ah.A;
import org.testng.internal.junit.ArrayAsserts;

import java.util.ArrayList;
import java.util.List;

public class OddIntervals {
    class Point {
        int pos;
        int starts;
        int ends;

        public Point(int pos, int starts, int ends) {
            this.pos = pos;
            this.starts = starts;
            this.ends = ends;
        }

        @Override
        public String toString() {
            return String.format("Point [pos=%s, starts=%s, ends=%s]", pos, starts, ends);
        }
    }
    List<int[]> getOddIntervals(List<int[]> input) {
        if (input == null || input.size() == 0) {
            return null;
        }
        List<Point> rawEvents = new ArrayList<>();
        // first pass, to count all raw events
        for (int i = 0; i < input.size(); i++) {
            int[] interval = input.get(i);
            rawEvents.add(new Point(interval[0], 1, 0));
            rawEvents.add(new Point(interval[1], 0, 1));
        }
        rawEvents.sort((a, b) -> Integer.compare(a.pos, b.pos));
        System.out.printf("The raw events are %s %n", rawEvents);
        List<Point> aggregatedEvents = new ArrayList<>();
        Point curr = new Point(rawEvents.get(0).pos, rawEvents.get(0).starts, rawEvents.get(0).ends);
        for (int i = 1; i < rawEvents.size(); i++) {
            Point next = rawEvents.get(i);
            if (next.pos == curr.pos) {
                // the points should merge
                curr.starts += next.starts;
                curr.ends += next.ends;
            } else {
                aggregatedEvents.add(curr);
                curr = new Point(next.pos, next.starts, next.ends);
            }
        }
        aggregatedEvents.add(curr); // merge in the last event
        System.out.printf("The aggregated events are %s %n", aggregatedEvents);
        int activeIntervals = 0;
        List<int[]> results = new ArrayList<>();
        for (int i = 0; i < aggregatedEvents.size(); i++) {
            Point aggregatedPtr = aggregatedEvents.get(i);
            activeIntervals += aggregatedPtr.starts;
            activeIntervals -= aggregatedPtr.ends;
            System.out.printf("i = %s, At position %s, activeIntervals %s %n", i, aggregatedPtr.pos, activeIntervals);
            if (activeIntervals > 0 && activeIntervals % 2 == 1) {
                if (i != aggregatedEvents.size() - 1) { // not the last point
                    System.out.printf("Adding interval to final result [%s, %s] %n", aggregatedPtr.pos, aggregatedEvents.get(i+1).pos);
                    results.add(new int[]{aggregatedPtr.pos, aggregatedEvents.get(i+1).pos});
                }
            }
        }
        System.out.printf("The final results are %s %n", results);
        return results;
    }
}

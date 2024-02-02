package rippling.reporting;


/**
 * This is for the question mentioned in this post
 * https://leetcode.com/discuss/interview-question/1650545/Rippling-or-SE-or-DSAlgo-Round-orHighest-performing-rating-employee-team
 *
 * There are some things that are not super clear from the post, so making some assumptions:
 * 1. The question asks for the highest avg score of the team, but the test case given in the post returns a leave node.
 * Meaning if there is only 1 person on the leave node, it is also considered as a "team". It feels more reasonable
 * to only compute avg for "manager" nodes. But still going with the test case for simplicity for now. The change
 * should be simple to make.
 *
 * Other than that, it feels like a normal tree traversal problem. With java, it is not super easy to pass temporary
 * variables around. So I created {@link RateAccumulator} to help with it.
 */
public class HighestRating {
    public Employee getHighestRating(Employee root) {
        return getRating(root).highestManager;
    }

    private RateAccumulator getRating(Employee root) {
        if (root == null) {
            return new RateAccumulator(null, 0, 0);
        }

        RateAccumulator curr = new RateAccumulator(root, root.getRating(), 1);
        double highest = 0;
        Employee highestEmp = root;
        for (Employee employee: root.getReports()) {
            RateAccumulator acc = getRating(employee);
            if (acc.highestManager != null) {
                curr.merge(acc);
                if (acc.highest > highest) {
                    highestEmp = acc.highestManager;
                    highest = acc.highest;
                }
            }
        }
        // need to discuss how to break ties. assuming the top level wins
        if (curr.avg >= highest) {
            curr.setHighest(curr.avg);
            curr.setHighestManager(root);
        } else {
            curr.setHighest(highest);
            curr.setHighestManager(highestEmp);
        }
        return curr;
    }

    public static class RateAccumulator {
        Employee highestManager;
        int ratings;
        int reportCnt;
        double avg;
        double highest;

        public RateAccumulator(Employee highestManager, int ratings, int reportCnt) {
            this.highestManager = highestManager;
            this.ratings = ratings;
            this.reportCnt = reportCnt;
            this.avg = (double) ratings / reportCnt;
            this.highest = this.avg;
        }

        public void merge(RateAccumulator other) {
            this.ratings = ratings + other.ratings;
            this.reportCnt = reportCnt + other.reportCnt;
            this.avg = (double) ratings / reportCnt;
        }

        public void setHighest(double highest) {
            this.highest = highest;
        }

        public void setHighestManager(Employee manager) {
            this.highestManager = manager;
        }
    }
}

package zukesi.tickets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RollerCoaster {
    public static void main(String[] args) {

        ArrayList<String> strings = new ArrayList<String>();
        String[] inputs = new String[] {
                "Wooden 4 1.0 Chain",
                "Steel 20 2.0 Cable",
                "Suspended 2 1.5 Cable",
                "Suspended 2 1.5 Chain"

        };
        System.out.println(computeScores(inputs));
    }

    static List<Double> computeScores(String[] inputs) {
        List<Double> scores = new ArrayList<>();
        for (String input: inputs) {
            String[] parts = input.split(" ");
            // TODO: assume the inputs are valid. add validation error handling later
            RollerCoasterType rollerCoasterType = RollerCoasterType.valueOf(parts[0].trim());
            int maxSpeed = Integer.parseInt(parts[1].trim());
            double bumpsPerSec = Double.parseDouble(parts[2].trim());
            LiftType liftType = LiftType.valueOf(parts[3].trim());
            scores.add(ScoreComputerFactory.getComputer(rollerCoasterType).compute(maxSpeed, bumpsPerSec, liftType));
        }
        return scores;
    }

    static class ScoreComputerFactory {
        private ScoreComputerFactory () { }

        static Map<RollerCoasterType, ScoreComputer> rcComputerMap;

        static {
            rcComputerMap = new HashMap<>();
            rcComputerMap.put(RollerCoasterType.Wooden, new RollerCoaster.WoodenScoreComputer());
            rcComputerMap.put(RollerCoasterType.Steel, new RollerCoaster.SteelScoreComputer());
            rcComputerMap.put(RollerCoasterType.Suspended, new RollerCoaster.SuspendedScoreComputer());
        }
        static ScoreComputer getComputer(RollerCoasterType rollerCoasterType) {
            if (rcComputerMap.containsKey(rollerCoasterType)) {
                return rcComputerMap.get(rollerCoasterType);
            } else {
                throw new IllegalArgumentException("Invalid roller coaster type: " + rollerCoasterType);
            }

        }
    }

    interface ScoreComputer {
        double compute(int maxspeed, double bumpsPerSec, LiftType liftType);
    }

    static abstract class AbstractScoreComputer implements ScoreComputer {

        abstract double getScaleFactor();
        double comfortScore(double bumpsPerSec, int maxspeed, LiftType liftType) {
            // validate the inputs can't be 0
            return Math.min(Math.min(1.0 / bumpsPerSec, 1.0 / maxspeed), 1.0);
        }

        @Override
        public double compute(int maxspeed, double bumpsPerSec, LiftType liftType) {
            return getScaleFactor() * comfortScore(bumpsPerSec, maxspeed, liftType) * maxspeed;
        }
    }

    static class WoodenScoreComputer extends AbstractScoreComputer {
        @Override
        public double getScaleFactor() {
            return 1.0;
        }
    }

    static class SteelScoreComputer extends AbstractScoreComputer {
        @Override
        public double getScaleFactor() {
            return 2.0;
        }
        @Override
        public double comfortScore(double bumpsPerSec, int maxspeed, LiftType liftType) {
            return Math.min(1.0, Math.min(1.0 / bumpsPerSec, (1.0) * 5 / maxspeed));
        }
    }

    static class SuspendedScoreComputer extends AbstractScoreComputer {
        @Override
        public double getScaleFactor() {
            return 0.5;
        }
        @Override
        public double comfortScore(double bumpsPerSec, int maxspeed, LiftType liftType) {
            double comfortScore = super.comfortScore(bumpsPerSec, maxspeed, liftType);
            if (liftType.equals(LiftType.Cable)) {
                comfortScore += 0.5;
            }
            return comfortScore;
        }
    }

    enum RollerCoasterType {
        Wooden,
        Steel,
        Suspended
    }

    enum LiftType {
        Cable,
        Chain,
        Launched
    }
    private static int findBreakPoint(String expression) {
        int brackets = 0;
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '(') {
                brackets++;
            } else if (c == ')') {
                brackets--;
            } else if (c == ' ' && brackets == 0) {
                return i;
            }
        }
        return -1; // this won't happen for a well formatted string
    }

    private static String[] breakIntoVals(String expression) {
        int breakPoint = findBreakPoint(expression);
        String val1 = expression.substring(0, breakPoint);
        String val2 = expression.substring(breakPoint + 1);
        return new String[] {val1, val2};
    }
}

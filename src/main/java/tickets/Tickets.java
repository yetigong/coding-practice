package tickets;

/*
 * Click `Run` to execute the snippet below!
 */

import java.util.*;

/*
 * To execute Java, please define "static void main" on a class
 * named Solution.
 *
 * If you need more classes, simply define them inline.
 */

public class Tickets {
    public static void main(String[] args) {

        ArrayList<String> strings = new ArrayList<String>();
        String[] inputs = new String[] {
                "United\t150.0\tPremium",
                "Delta\t60.0 \tBusiness",
                "Southwest\t1000.0\tEconomy",
                "LuigiAir\t50.0\tBusiness"
        };
        System.out.println(computePrices(inputs));
    }

    static List<String> computePrices(String[] inputs) {
        List<String> result = new ArrayList<>();
        for (String input: inputs) {
            String[] parts = input.split("\t");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid input: " + input);
            }
            double miles = Double.parseDouble(parts[1].trim());
            SeatClass seatClass = SeatClass.valueOf(parts[2].trim());

            double price = PriceCalculatorFactory.getCalculator(parts[0].trim()).compute(seatClass, miles);
            result.add(String.format("%.2f", price));
        }
        return result;
    }
    enum Airlines {
        United,
        Delta,
        Southwest,
        LuigiAir
    }

    enum SeatClass {
        Economy,
        Premium,
        Business
    }
    interface PriceCalculator {
        double compute(SeatClass seatClass, double miles);

    }

    static abstract class AbstractPriceCalculator implements PriceCalculator {

        public double operationalCost(SeatClass seatClass, double miles) {
            switch (seatClass) {
                case Economy:
                    return 0;
                case Premium:
                    return 25;
                case Business:
                    return 50 + 0.25 * miles;
                default:
                    throw new IllegalArgumentException("Unsupported seat class " + seatClass);
            }
        }

    }

    static class UnitedCalculator extends AbstractPriceCalculator {

        @Override
        public double compute(SeatClass seatClass, double miles) {
            double cost = 0;
            if (seatClass.equals(SeatClass.Premium)) {
                cost += 0.1 * miles;
            }
            cost += 0.75 * miles + operationalCost(seatClass, miles);
            return cost;
        }
    }

    static class DeltaCalculator extends AbstractPriceCalculator {

        @Override
        public double compute(SeatClass seatClass, double miles) {
            return 0.5 * miles + operationalCost(seatClass, miles);
        }
    }
    static class SouthWestCalculator extends AbstractPriceCalculator {

        @Override
        public double compute(SeatClass seatClass, double miles) {
            return 1.00 * miles;
        }
    }

    static class LuigiCalculator extends AbstractPriceCalculator {

        @Override
        public double compute(SeatClass seatClass, double miles) {
            return Math.max(100, 2 * operationalCost(seatClass, miles));
        }
    }

    static class PriceCalculatorFactory {
        static Map<Airlines, PriceCalculator> airlinesPriceCalculatorMap;

        //TODO initialize the maps
        static {
            airlinesPriceCalculatorMap = new HashMap<>();
            airlinesPriceCalculatorMap.put(Airlines.United, new UnitedCalculator());
            airlinesPriceCalculatorMap.put(Airlines.Delta, new DeltaCalculator());
            airlinesPriceCalculatorMap.put(Airlines.Southwest, new SouthWestCalculator());
            airlinesPriceCalculatorMap.put(Airlines.LuigiAir, new LuigiCalculator());
        }
        public static PriceCalculator getCalculator(String airline) {
            Airlines airlineEnum = Airlines.valueOf(airline);
            if (airlinesPriceCalculatorMap.containsKey(airlineEnum)) {
                return airlinesPriceCalculatorMap.get(airlineEnum);
            } else {
                throw new IllegalArgumentException("Unsupported Airlines for " + airline);
            }
        }
    }
}


// Your previous Plain Text content is preserved below:

// This is just a simple shared plaintext pad, with no execution capabilities.

// When you know what language you'd like to use for your interview,
// simply choose it from the dots menu on the tab, or add a new language
// tab using the Languages button on the left.

// You can also change the default language your pads are created with
// in your account settings: https://app.coderpad.io/settings

// Enjoy your interview!

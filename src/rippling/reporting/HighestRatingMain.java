package rippling.reporting;

import org.testng.Assert;
import org.testng.annotations.Test;

public class HighestRatingMain {
    @Test
    public void testHappyPath() {
        Employee a = new Employee("A", 5);
        Employee b = new Employee("B", 3);
        Employee c = new Employee("C", 1);
        Employee d = new Employee("D", 4);
        Employee e = new Employee("E", 10);
        a.getReports().add(b);
        a.getReports().add(c);
        c.getReports().add(d);
        c.getReports().add(e);

        HighestRating solution = new HighestRating();
        Employee highestRating = solution.getHighestRating(a);

        Assert.assertEquals(highestRating.getName(), "E");
    }

    @Test
    public void testHappyPath2() {
        Employee a = new Employee("A", 5);

        HighestRating solution = new HighestRating();
        Employee highestRating = solution.getHighestRating(a);

        Assert.assertEquals(highestRating.getName(), "A");
    }

    @Test
    public void testHighRatingOnTopLevel() {
        Employee a = new Employee("A", 10);
        Employee b = new Employee("B", 3);
        Employee c = new Employee("C", 5);
        Employee d = new Employee("D", 2);
        Employee e = new Employee("E", 1);
        a.getReports().add(b);
        a.getReports().add(c);
        c.getReports().add(d);
        c.getReports().add(e);

        HighestRating solution = new HighestRating();
        Employee highestRating = solution.getHighestRating(a);

        Assert.assertEquals(highestRating.getName(), "A");
    }

    @Test
    public void testHighRatingOnMidLevel() {
        Employee a = new Employee("A", 10);
        Employee b = new Employee("B", 6);
        Employee c = new Employee("C", 5);
        Employee d = new Employee("D", 2);
        Employee e = new Employee("E", 1);
        a.getReports().add(b);
        a.getReports().add(c);
        c.getReports().add(d);
        c.getReports().add(e);

        HighestRating solution = new HighestRating();
        Employee highestRating = solution.getHighestRating(a);

        Assert.assertEquals(highestRating.getName(), "B");
    }

    @Test
    public void testHighRatingOnMidLevel2() {
        Employee a = new Employee("A", 5);
        Employee b = new Employee("B", 3);
        Employee c = new Employee("C", 10);
        Employee d = new Employee("D", 2);
        Employee e = new Employee("E", 1);
        a.getReports().add(b);
        a.getReports().add(c);
        c.getReports().add(d);
        c.getReports().add(e);

        HighestRating solution = new HighestRating();
        Employee highestRating = solution.getHighestRating(a);

        Assert.assertEquals(highestRating.getName(), "C");
    }
}

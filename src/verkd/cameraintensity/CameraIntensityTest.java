package verkd.cameraintensity;

import com.google.common.collect.ImmutableList;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class CameraIntensityTest {
    @Test
    public void testSingleCase() {
        List<Item> items = ImmutableList.of(
                new Item(1, 0.8)
        );
        List<MotionPeriod> expected = ImmutableList.of(
                new MotionPeriod(1, 1)
        );

        CameraIntensity intensity = new CameraIntensity();
        List<MotionPeriod> actual = intensity.motionPeriodsForCamera(items, 0.5);

        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testSingleCase2() {
        List<Item> items = ImmutableList.of(
                new Item(1, 0.8)
        );
        List<MotionPeriod> expected = ImmutableList.of(
        );

        CameraIntensity intensity = new CameraIntensity();
        List<MotionPeriod> actual = intensity.motionPeriodsForCamera(items, 0.9);

        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testSingleCase3() {
        List<Item> items = ImmutableList.of(
                new Item(2, 0.5),
                new Item(7, 0.8),
                new Item(10, 0.9),
                new Item(11, 0.9),
                new Item(16, 0.4)
        );
        List<MotionPeriod> expected = ImmutableList.of(
                new MotionPeriod(7, 11)
        );

        CameraIntensity intensity = new CameraIntensity();
        List<MotionPeriod> actual = intensity.motionPeriodsForCamera(items, 0.8);

        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testSingleCase4() {
        List<Item> items = ImmutableList.of(
                new Item(3, 0.5),
                new Item(5, 0.8),
                new Item(7, 0.9),
                new Item(10, 0.6),
                new Item(15, 0.8),
                new Item(20, 0.9)
        );
        List<MotionPeriod> expected = ImmutableList.of(
                new MotionPeriod(5, 7),
                new MotionPeriod(15, 20)
        );

        CameraIntensity intensity = new CameraIntensity();
        List<MotionPeriod> actual = intensity.motionPeriodsForCamera(items, 0.8);

        Assert.assertEquals(actual, expected);
    }

    @Test
    public void multiCamsTest1() {
        List<Item> item1 = ImmutableList.of(
                new Item(2, 0.5), new Item(7, 0.8), new Item(10, 0.9), new Item(11, 0.9), new Item(16, 0.4)
        );

        List<Item> item2 = ImmutableList.of(
                new Item(5, 0.8), new Item(8, 0.9), new Item(9, 0.8), new Item(13, 0.5), new Item(20, 0.5)
        );

        List<Item> item3 = ImmutableList.of(
                new Item(6, 0.1), new Item(7, 0.8), new Item(8, 0.9), new Item(17, 0.8)
        );

        List<MotionPeriod> expected = ImmutableList.of(
                new MotionPeriod(7, 9)
        );

        CameraIntensity intensity = new CameraIntensity();
        List<MotionPeriod> actual = intensity.multiCams(ImmutableList.of(item1, item2, item3), 0.8);

        Assert.assertEquals(actual, expected);
    }

    @Test
    public void multiCamsTest2() {
        List<Item> item1 = ImmutableList.of(
                new Item(2, 0.9), new Item(7, 0.9), new Item(10, 0.5), new Item(11, 0.9), new Item(14, 0.9)
                );

        List<Item> item2 = ImmutableList.of(
                new Item(5, 0.9), new Item(11, 0.9)
        );

        List<Item> item3 = ImmutableList.of(
                new Item(1, 0.8), new Item(8, 0.9), new Item(11, 0.9), new Item(21, 0.9)
        );

        List<MotionPeriod> expected = ImmutableList.of(
                new MotionPeriod(5, 7), new MotionPeriod(11, 11)
        );

        CameraIntensity intensity = new CameraIntensity();
        List<MotionPeriod> actual = intensity.multiCams(ImmutableList.of(item1, item2, item3), 0.8);

        Assert.assertEquals(actual, expected);
    }
}

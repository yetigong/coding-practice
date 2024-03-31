package verkd.commonprefix;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;

public class CommonPrefixesTest {
    @Test
    public void testSimpleCase() {
        CommonPrefixes solver = new CommonPrefixes();
        List<String> input = ImmutableList.of(
                "a1/o1/o2", "a1/o1", "a1/o2"
        );
        Set<String> expected = ImmutableSet.of(
                "a1/o1", "a1/o2"
        );

        Assert.assertEquals(solver.getCommonPrefixes(input), expected);
    }
}

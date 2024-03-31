package verkd.cameraintensity;

import java.util.Objects;

public class MotionPeriod {
    int start;
    int end;

    public MotionPeriod(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;

        MotionPeriod that = (MotionPeriod) obj;

        return this.start == that.start && this.end == that.end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}

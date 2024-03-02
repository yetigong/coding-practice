package verkd.cameraintensity;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * 面了有一阵了，具体有点记不清了，coding的题分两个part，
 * 第一个part是给一组time跟intensity的vector输入，以及一个threshold，求输出motion intensity above the threshold的time periods.
 * 其中，给了两个struct
 * struct Item {
 * int time;
 * double intensity;
 * };
 *
 * struct MotionPeriod {
 * int start;
 * int end;
 * };
 *
 * function定义：
 * vector<MotionPeriod> motionPeriodsForCamera(vector<Item>& cameraOutput, double threshold)
 *
 * 例子：
 * [2, 0.5), (7, 0.8), (10, 0.9), (11, 0.9), (16, 0.4)]
 * threshold 0.8
 * 输出 [(7, 11)]
 */
public class CameraIntensity {
    /**
     * This implementation assumes the input is always in ascending order for time.
     *
     * @param cameraOutput
     * @param threshold
     * @return
     */
    public List<MotionPeriod> motionPeriodsForCamera(List<Item> cameraOutput, double threshold) {
        if (cameraOutput == null || cameraOutput.isEmpty()) {
            return null;
        }
        List<MotionPeriod> result = new ArrayList<>();
        MotionPeriod motionPeriod = null;
        for (Item item: cameraOutput) {
            if (item.intensity >= threshold) {
                if (motionPeriod == null) {
                    motionPeriod = new MotionPeriod(item.time, item.time);
                } else {
                    motionPeriod.end = item.time;
                }
            } else {
                if (motionPeriod != null) {
                    result.add(motionPeriod);
                    motionPeriod = null;
                }
            }
        }
        // handle the last period properly
        if (motionPeriod != null) {
            result.add(motionPeriod);
        }
        return result;
    }

    /**
     * the implementation assumes there are at least 1 item in each of the camInputs
     * @param multiCamInputs
     * @param threshold
     * @return
     */
    public List<MotionPeriod> multiCams(List<List<Item>> multiCamInputs, double threshold) {
        List<List<MotionPeriod>> camsActivePeriods = this.individualPeriods(multiCamInputs, threshold);

        int camNum = camsActivePeriods.size();
        // List<MotionPeriod> current = new ArrayList<>();
        int[] index = new int[camsActivePeriods.size()];

        List<MotionPeriod> result = new ArrayList<>();
        while (true) {
            // merge all intervals in current list
            int maxStart = 0;
            int minEnd = Integer.MAX_VALUE;
            int camsToFetch = 0;
            boolean hasValidCommonInterval = true;
            for (int i = 0; i < camNum; i++) {
                MotionPeriod current = camsActivePeriods.get(i).get(index[i]);
                maxStart = Math.max(maxStart, current.start);
                if (current.end < minEnd) {
                    minEnd = current.end;
                    camsToFetch = i;
                }
                if (maxStart > minEnd) {
                    hasValidCommonInterval = false;
                }
                // this can probably be more efficient to update all indexes whose end is < maxStart
            }
            if (hasValidCommonInterval) {
                // it's a valid common interval
                System.out.printf("Found one valid interval [%s, %s]", maxStart, minEnd);
                result.add(new MotionPeriod(maxStart, minEnd));
            } else {
                System.out.printf("Found can't find valid interval, found [%s, %s], with cam %s ", maxStart, minEnd, camsToFetch);
            }
            // if we already reached the end
            if (index[camsToFetch] == camsActivePeriods.get(camsToFetch).size() - 1 ) {
                break;
            } else {
                // else fetch next period of the cam
                index[camsToFetch] += 1;
            }
        }
        return result;
    }

    private List<List<MotionPeriod>> individualPeriods(List<List<Item>> multiCamInputs, double threshold) {
        List<List<MotionPeriod>> results = new ArrayList<>();
        for (int i = 0 ; i < multiCamInputs.size(); i++) {
            List<MotionPeriod> camActivePeriod = this.motionPeriodsForCamera(multiCamInputs.get(i), threshold);
            results.add(camActivePeriod);
        }
        return results;
    }
}

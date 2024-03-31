package verkd.commonprefix;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommonPrefixes {
    public Set<String> getCommonPrefixes(List<String> input) {
        if (input == null || input.size() == 0) {
            return null;
        }

        Set<String> inputSet = new HashSet<>();
        for (int i = 0; i < input.size(); i++) {
            inputSet.add(input.get(i));
        }

        for (int i = 0; i < input.size(); i++) {
            String path = input.get(i);
            String[] parts = path.split("/");
            StringBuilder pathBuilder = new StringBuilder();
            for (int j = 0; j < parts.length; j++) {
                if (parts[j].isEmpty()) continue;
                if (j == 0) {
                    pathBuilder.append(parts[j]);
                } else {
                    pathBuilder.append("/").append(parts[j]);
                }
                if (inputSet.contains(pathBuilder.toString()) && !pathBuilder.toString().equals(path)) {
                    // it means we have found a shorter prefix, and it is not itself
                    inputSet.remove(path);
                    break;
                }
            }
        }
        return inputSet;
    }
}

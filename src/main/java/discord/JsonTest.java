package discord;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonTest {
    static class Log {
        int userId;
        int channelId;
        LocalDateTime timestamp;

        //        public Log(int userId, int channelId, long timestamp) {
//            this.userId = userId;
//            this.channelId = channelId;
//            this.timestamp = timestamp;
//        }
//
        public Log(String entry) {
            String[] parts = entry.split(",");
            timestamp = LocalDateTime.parse(parts[0].substring(0, parts[0].length() - 1));
            userId = Integer.parseInt(parts[2]);
            channelId = Integer.parseInt(parts[3]);
        }

        @Override
        public String toString() {
            return "Log{" +
                    "userId=" + userId +
                    ", channelId=" + channelId +
                    ", timestamp=" + timestamp +
                    '}';
        }

    }

    static class AggregatedLog {
        int userId;
        LocalDateTime sessionStart;
        LocalDateTime sessionEnd;
        int messagesSent;
        int topChannelId;
        int topChannelMessagesSent;

        @Override
        public String toString() {
            return "AggregatedLog{" +
                    "userId=" + userId +
                    ", sessionStart=" + sessionStart +
                    ", sessionEnd=" + sessionEnd +
                    ", messagesSent=" + messagesSent +
                    ", topChannelId=" + topChannelId +
                    ", topChannelMessagesSent=" + topChannelMessagesSent +
                    '}';
        }
    }

    static class Storage {
        Map<Integer, List<JsonSimple.Log>> userLogsMap;

        public Storage() {
            userLogsMap = new HashMap<>();
        }
        public void insert(JsonSimple.Log log) {
            userLogsMap.putIfAbsent(log.userId, new ArrayList<>());
            userLogsMap.get(log.userId).add(log);
            checkAndDumpSessions(log);
        }

        private void checkAndDumpSessions(JsonSimple.Log log) {
            Map<Integer, List<JsonSimple.Log>> usersSessions = new HashMap<>();
            userLogsMap.entrySet().stream().forEach(entry -> {
                int userId = entry.getKey();
                List<JsonSimple.Log> logs = entry.getValue();
                if (log.timestamp.minusMinutes(15).isAfter(logs.get(0).timestamp)) {
                    // the session for user id has ended
                    usersSessions.put(userId, logs);
                }
            });
            usersSessions.keySet().forEach(userId -> userLogsMap.remove(userId));

            usersSessions.entrySet().forEach(entry -> {
                System.out.println(aggregateLog(entry.getKey(), entry.getValue()));
            });
        }

        private JsonSimple.AggregatedLog aggregateLog(int user, List<JsonSimple.Log> logs) {
            // logs are always non empty
            JsonSimple.AggregatedLog aggregatedLog = new JsonSimple.AggregatedLog();
            aggregatedLog.sessionStart = logs.get(0).timestamp;
            aggregatedLog.sessionEnd = logs.get(0).timestamp;

            aggregatedLog.userId = user;
            Map<Integer, Integer> channelMessageMap = new HashMap<>();
            int topChannelCount = 0;
            int topChannelId = 0;
            for (JsonSimple.Log log: logs) {
                aggregatedLog.messagesSent += 1;
                if (log.timestamp.isBefore(aggregatedLog.sessionStart)) {
                    aggregatedLog.sessionStart = log.timestamp;
                }
                if (log.timestamp.isAfter(aggregatedLog.sessionEnd)) {
                    aggregatedLog.sessionEnd = log.timestamp;
                }
                channelMessageMap.putIfAbsent(log.channelId, 0);
                int currChannelCount = channelMessageMap.get(log.channelId) + 1;
                if (currChannelCount > topChannelCount) {
                    topChannelId = log.channelId;
                    topChannelCount = currChannelCount;
                }
                channelMessageMap.put(log.channelId, currChannelCount);
            }
            aggregatedLog.topChannelId = topChannelId;
            aggregatedLog.topChannelMessagesSent = topChannelCount;
            return aggregatedLog;
        }
    }

    public static void main(String[] args) {
        String filePath = "/Users/bopan/IdeaProjects/discord/messages.csv";
        JsonSimple.Storage storage = new JsonSimple.Storage();
        // BufferedReader br = null;
        int limit = 10;
        int lineCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String headers = br.readLine();
            String line = null;
            while ((line = br.readLine()) != null && !line.isBlank()) {
                lineCount++;
                JsonSimple.Log log = new JsonSimple.Log(line);
                // System.out.printf("The log message we read is %s %n", log);
                storage.insert(log);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

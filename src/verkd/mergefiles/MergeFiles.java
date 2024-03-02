package verkd.mergefiles;

import com.google.common.collect.ImmutableList;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class MergeFiles {
    /***
     * Assuming the list of files in the input is valid
     * @param fileNames
     * @throws IOException
     */
    static class Log {
        long timestamp;
        String logMessage;
        String fileName;

        public Log(long timestamp, String logMessage, String fileName) {
            this.timestamp = timestamp;
            this.logMessage = logMessage;
            this.fileName = fileName;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("file name: " + fileName + " ");
            sb.append("timestamp name: " + timestamp + " ");
            sb.append("message: " + logMessage + " \n");
            return sb.toString();
        }

        /**
         * This assumes the format of the log is always valid, following [timstamp][space][message].
         * This should be expanded to a LogParser class eventually.
         *
         * @param rawLog
         * @param fileName
         * @return
         */
        public static Log parse(String rawLog, String fileName) {
            System.out.printf("Handling log file %s with raw log %s. %n", fileName, rawLog);
            if (rawLog == null || rawLog.length() == 0) {
                return null;
            }
            int index = rawLog.indexOf(" ");
            if (index < 0) {
                return null;
            }
            long timestamp = Long.parseLong(rawLog.substring(0, index));
            String message = rawLog.substring(index + 1);
            return new Log(timestamp, message, fileName);
        }
    }
    public static void mergeFiles(List<String> fileNames) throws IOException {
        if (fileNames == null || fileNames.size() < 1) {
            return;
        }
        // initialization and create BufferedReader for all files in a Map<fileName, handler>, initiate and create PriorityQueue
        // PriorityQueue should take a log with <Log (ts, log, file)>, compare with ts order as a min heap
        // while (priorityQueue.notEmpty()) -
        // poll from it
        // write to output file
        // get the file name and retrieve from the file handle if there is more
        PriorityQueue<Log> merger = new PriorityQueue<>((a, b) -> (Long.compare(a.timestamp, b.timestamp)));
        Map<String, BufferedReader> fileReaders = new HashMap<>();
        String outputName = "output." + System.currentTimeMillis();
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputName));
        for (int i = 0; i < fileNames.size(); i++) {
            String fileName = fileNames.get(i);
            BufferedReader br = getBufferedReaderForFile(fileName);
            if (br != null) {
                String firstLine = br.readLine();
                Log log = Log.parse(firstLine, fileName);
                if (log != null) {
                    merger.offer(log);
                }
                fileReaders.put(fileName, br);
            }
        }

        while (!merger.isEmpty()) {
            Log log = merger.poll();
            bw.append(String.format("%s %s %n", log.timestamp, log.logMessage));
            bw.flush(); // can optimize to batch flush

            String nextLine = fileReaders.get(log.fileName).readLine();
            Log nextLog = Log.parse(nextLine, log.fileName);
            if (nextLog != null) {
                merger.offer(nextLog);
            }
        }
    }

    private static BufferedReader getBufferedReaderForFile(String fileName) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            System.err.printf("File %s cannot be found. Ignoring this file. %n", fileName);
        }
        return br;
    }

    public static void main(String[] args) {
        List<String> files = ImmutableList.of("src/verkd/mergefiles/log1",
                "src/verkd/mergefiles/log2",
                "src/verkd/mergefiles/log3",
                "src/verkd/mergefiles/log4",
                "src/verkd/mergefiles/log5");
        try {
            mergeFiles(files);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

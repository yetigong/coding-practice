package oai;

import java.io.*;
import java.util.*;

public class IteratorTest {
    static class ResumableIterator<T> implements Iterator<T> {
        private Iterator<T> iterator;
        private int currentPosition;
        private List<T> data;
        private String stateFilePath;

        public ResumableIterator(List<T> data, String stateFilePath) {
            this.data = data;
            this.stateFilePath = stateFilePath;
            this.currentPosition = 0;
            this.iterator = data.iterator();
            // Initialize iterator and currentPosition based on potentially existing state
            thaw();
        }

        @Override
        public boolean hasNext() {
            boolean result = this.currentPosition < this.data.size();
            return result;
        }

        @Override
        public T next() {
            if (hasNext()) {
                T nextItem = this.data.get(currentPosition);
                currentPosition++;
                return nextItem;
            }
            return null;
        }

        public synchronized void freeze() {
            // Serialize currentPosition and any other needed state to stateFilePath
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(stateFilePath))) {
                bw.write(String.valueOf(this.currentPosition));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public synchronized void thaw() {
            try (BufferedReader br = new BufferedReader(new FileReader(stateFilePath))){
                String line = br.readLine();
                if (line != null && !line.isBlank()) {
                    this.currentPosition = Integer.parseInt(line.trim());
                }
            } catch (FileNotFoundException e) {
                System.out.println("File not exist. Nothing to thaw from.");
            } catch (IOException e) {
                System.out.println("Cannot read from the file. Nothing to thaw from.");
            }
            // Deserialize state from stateFilePath and set up iterator and currentPosition
        }

        // Additional methods and error handling as needed
    }

    /***
     * Need to clarify (single thread cases):
     * 1. if multiple instances of iterator on the same collection, they should normally iterate independently.
     * 2. If one of the iterator freeze(), new iterators created will resume from the checkpoint?
     * 3. If other iterator or multiple instances calls thaw(), they will all resume from the saved checkpoint?
     * 4. If only 1 iterator is supposed to resume, then we need to delete the checkpoint file.
     *
     * thoughts on test cases:
     * 1. single thread happy path - try next, fake some exception, and call freeze. call resume.
     * 2. single thread corner - try next, fake exception, call freeze, call freeze again , then resume
     * 3. single thread resume - call resume without freeze, should do nothing.

     * @param args
     */
    public static void main(String [] args) {
        List<Integer> data = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            data.add(i);
        }
        String filePath = "resumableIterator-" + System.currentTimeMillis() + ".txt";
        List<Integer> result = new ArrayList<>();
        // create two instance for testing the thaw()
        ResumableIterator<Integer> resumableIterator1 = new ResumableIterator<>(data, filePath);
        result.add(resumableIterator1.next());
        result.add(resumableIterator1.next());
        resumableIterator1.freeze();
//        resumableIterator1.freeze();

        ResumableIterator<Integer> resumableIterator2 = new ResumableIterator<>(data, filePath);
        result.add(resumableIterator2.next());

        ResumableIterator<Integer> resumableIterator3 = new ResumableIterator<>(data, filePath);
        result.add(resumableIterator3.next());
        result.add(resumableIterator3.next());
        resumableIterator3.freeze(); // it should index 4, which is out of bound

        resumableIterator2.thaw();
        System.out.println("Iterator 2 has element left? result: " + resumableIterator2.hasNext());
        // expected to have 1, 2, 3, 4
        System.out.printf("The actual output is %s %n", result);
    }
}

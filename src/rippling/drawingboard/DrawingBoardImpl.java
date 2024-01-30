package rippling.drawingboard;

import utils.ArrayUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * this is an implementation based on the question described in
 *  <a href="https://leetcode.com/discuss/interview-question/1990578/rippling-onsite-reject"> the discussion!</a>
 *
 * Given this is not a formal question, there are some assumptions made to the original post:
 * 1. Assuming we don't take multiple "draw" operations for the same character, as we don't know how to handle the layering.
 *
 * 2. Assuming out of bound operations will be handled in a way that only valid positions will be displayed. But the original
 * positions are all kepted. So move operations could potentially move the out of bound characters back to canvas.
 *
 * 3. There is no "delete" or "clear" operations.
 *
 * 4. If move a char that does not exist, the operation
 *
 * <p>
 * Note:
 * 1. in my naive approach, display() simply iterates all layers, and determine what's the output for each point. A potential
 * optimization is to compute the diff points impacted by the move operation. And only compute the output for these points.
 * Worse case scenario does not change, but can improve average cases.
 * </p>
 *
 * 2. Further improvements can be made, for each point, we store all states.
 * So only need to update the result, if there is no higher layers on these diff points.
 *
 * 3. There are two design choices for move/display: (1) do we consolidate final state of board at the time of display, or
 * (2) do we consolidate the state after each move. This is some good discussion to be had with interviewer. At display time,
 * it is more resource efficient. But if we care about display latency, then move time is better.
 * Or even asynchronously consolidated after move. This then becomes very complicated, as we need to make sure the operations are atomic,
 * so that we don't end up displaying partial results.
 *
 * In this naive implementation, the move operation takes O(m*n), display operation takes O(m*n*l).
 *
 */
public class DrawingBoardImpl implements DrawingBoard {
    private int bwidth;
    private int bheight;

    private List<Layer> layers;

    /**
     * this layer index is helping to quickly identify the layer when we call "move". We don't need it, but we would
     * have to end up going through all layers to find the layer needed.
     */
    private Map<Character, Integer> layerIndex;

    public DrawingBoardImpl(int bwidth, int bheight) {
        this.bwidth = bwidth;
        this.bheight = bheight;
        this.layers = new ArrayList<>();
        this.layerIndex = new HashMap<>();
        // initialize the first layer to be all 0s
        this.draw(Character.MIN_VALUE, bwidth, bheight, 0, 0);
    }

    public void display() {
        char[][] board = new char[bwidth][bheight];
        for (int i = 0; i < bwidth; i++) {
            for (int j = 0; j < bheight; j++) {
                for (int l = 0; l < this.layers.size(); l++) {
                    Layer currLayer = this.layers.get(l);
                    /** only display the top layer so far.
                     * the traversing order guarantees if there is a character on the top layer, we would have something to display **/
                    if (currLayer.board[i][j] != '\u0000') {
                        board[i][j] = currLayer.key;
                    }
                }
            }
        }
        System.out.println(ArrayUtils.printArray(board));
    }

    public void draw(char c, int width, int height, int pos_x, int pos_y) {
        if (layerIndex.containsKey(c)) {
            throw new RuntimeException("Key " + c + " already exist. Cannot create again!");
        }
        int index = this.layerIndex.size();
        Layer layer = new Layer(c, bwidth, bheight, width, height, pos_x, pos_y);
        this.layers.add(layer);
        this.layerIndex.put(c, index);
    }

    /**
     * move the c into pos_x, pos_y
     * @param c     the character to move. This assumes there won't be duplicate characters on multiple layers.
     * @param pos_x the destination X position, instead of the diffs of position to move
     * @param pos_y the destination Y position, instead of the diffs of position to move
     */
    public void move(char c, int pos_x, int pos_y) {
        if (!this.layerIndex.containsKey(c)) {
            throw new RuntimeException("Key " + c + " not found in map! Did you draw it?");
        }
        int index = this.layerIndex.get(c);
        Layer layer = this.layers.get(index);
        layer.move(pos_x, pos_y);
    }
}

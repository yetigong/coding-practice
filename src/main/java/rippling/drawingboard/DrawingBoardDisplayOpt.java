package rippling.drawingboard;

import utils.ArrayUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * originally thought we could leverage the index to optimize for move operation. Whenever a draw or move operation happens,
 * we update the current board, if the destination has lower priority, then we can overwrite the new position.
 * But the solution won't work, because we cannot easily recover the moved-out positions  (it should be restored to the
 * previous character with lower priority.)
 *
 * So this still requires we keep track of all layers for each position. When a move operation happens, for each position, we need to
 *  manage the ordering of the layers, and display the correct character.
 *
 * The complexity still seems to be O(m*n*h).
 *
 */
public class DrawingBoardDisplayOpt implements DrawingBoard{
    private int bwidth;
    private int bheight;

    private List<Layer> layers;

    /**
     * this layer index is helping to quickly identify the layer when we call "move". We don't need it, but we would
     * have to end up going through all layers to find the layer needed.
     */
    private Map<Character, Integer> layerIndex;

    private char[][] currBoard;

    public DrawingBoardDisplayOpt(int bwidth, int bheight) {
        this.bwidth = bwidth;
        this.bheight = bheight;
        this.layers = new ArrayList<>();
        this.layerIndex = new HashMap<>();
        // initialize the first layer to be all 0s
        this.currBoard = new char[bwidth][bheight];
        this.draw(Character.MIN_VALUE, bwidth, bheight, 0, 0);
    }

    @Override
    public void display() {
        System.out.println(ArrayUtils.printArray(this.currBoard));
    }

    /**
     * two things need to be done for draw:
     * 1. we need to store the complete shape of char c, into the layers and layersIndex;
     * 2. we need to update currBoard with the priority of the layers;
     *
     * @param c
     * @param width
     * @param height
     * @param pos_x
     * @param pos_y
     */
    @Override
    public void draw(char c, int width, int height, int pos_x, int pos_y) {
        if (layerIndex.containsKey(c)) {
            throw new RuntimeException("The key " + c + "already exist!");
        }

        Layer layer = new Layer(c, this.bwidth, this.bheight, width, height, pos_x, pos_y);
        int index = this.layers.size();
        this.layers.add(layer);
        this.layerIndex.put(c, index);

        // update board with latest layer, as it has highest priority
        this.updateCurrBoard(c);
    }

    /**
     * this method updates the latest board with the updated position of char c.
     *
     * It can be used for both draw method and move method to update the position of the characters.
     * @param c
     */
    private void updateCurrBoard(char c) {
        if (!layerIndex.containsKey(c)) {
            throw new RuntimeException("the key" + c + " does not exist!");
        }
        this.currBoard = new char[bwidth][bheight];
        // to make sure we can only update the positions, that has a lower priority (index)
        for (int i = 0; i < bwidth; i++) {
            for (int j = 0; j < bheight; j++) {
                for (int l = 0; l < this.layers.size(); l++) {
                    Layer layer = this.layers.get(l);
                    if (layer.board[i][j] != Character.MIN_VALUE) {
                        this.currBoard[i][j] = layer.board[i][j];
                    }
                }
            }
        }
    }

    @Override
    public void move(char c, int pos_x, int pos_y) {
        if (!layerIndex.containsKey(c)) {
            throw new RuntimeException("The key " + c + "does not exist!");
        }

        int index = this.layerIndex.get(c);
        Layer layer = this.layers.get(index);
        layer.move(pos_x, pos_y);
        this.updateCurrBoard(c);
    }
}

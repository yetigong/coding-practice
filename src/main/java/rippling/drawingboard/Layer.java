package rippling.drawingboard;

public class Layer {
    char key;
    private int bwidth;

    private int bheight;
    int width;
    int height;
    int pos_x;
    int pos_y;
    char[][] board;

    public Layer(char key, int bwidth, int bheight, int width, int height, int pos_x, int pos_y) {
        this.key = key;
        this.bwidth = bwidth;
        this.bheight = bheight;
        this.width = width;
        this.height = height;
        this.pos_x = pos_x;
        this.pos_y = pos_y;
        this.board = new char[bwidth][bheight];
        this.updateLayerBoard();
    }

    public void move(int pos_x, int pos_y) {
        this.pos_x = pos_x;
        this.pos_y = pos_y;
        this.updateLayerBoard();
    }

    private void updateLayerBoard() {
        this.board = new char[bwidth][bheight];
        for (int i = pos_x; i < pos_x + width; i++) {
            for (int j = pos_y; j < pos_y + height; j++) {
                if (i < bwidth && j < bheight) {
                    this.board[i][j] = this.key;
                }
            }
        }
    }
}
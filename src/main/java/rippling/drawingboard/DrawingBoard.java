package rippling.drawingboard;

public interface DrawingBoard {
    void display();

    void draw(char c, int width, int height, int pos_x, int pos_y);

    void move(char c, int pos_x, int pos_y);
}

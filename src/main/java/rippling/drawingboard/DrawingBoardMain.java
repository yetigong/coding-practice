package rippling.drawingboard;

public class DrawingBoardMain {
    public static void main(String[] args){
        DrawingBoard board = new DrawingBoardDisplayOpt(5, 5);
        board.display();

        board.draw('a', 2, 2, 0, 0);
        board.display();

        board.draw('b', 2, 2, 1, 0);
        board.display();

        board.move('a', 1, 1);
        board.display();

        board.move('a', 1, 0);
        board.display();

        board.move('a', 4, 0);
        board.display();

        board.move('a', 1, 1);
        board.display();

        board.move('b', 0, 1);
        board.display();

    }

    private static String test(long input) {
        return "test";
    }
}

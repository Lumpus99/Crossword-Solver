package Swing.UI;

import java.awt.*;
import java.util.List;

public class CrosswordState {
    private char[][] board;
    private List words;
    private Point point;
    private int type; //vertical or horiz

    public CrosswordState(char[][] board, List words, Point point, int type) {
        this.board = board;
        this.words = words;
        this.point = point;
        this.type = type;
    }


    public char[][] getBoard() {
        return board;
    }

    public void setBoard(char[][] board) {
        this.board = board;
    }

    public List getWords() {
        return words;
    }

    public void setWords(List words) {
        this.words = words;
    }

    public Point getPoint() {
        return point;
    }

    public void remove_One(){
        words.remove(0);
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public boolean isEmpty(){
        return words.isEmpty();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

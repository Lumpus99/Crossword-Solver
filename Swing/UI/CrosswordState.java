package Swing.UI;

import java.util.List;

public class CrosswordState {
    private char[][] board;
    private List words;
    int x,y;

    public CrosswordState(char[][] board, List words, int x, int y) {
        this.board = board;
        this.words = words;
        this.x = x;
        this.y = y;
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
}

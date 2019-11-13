package Swing.UI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.*;
import javax.swing.border.*;

import static java.lang.Math.floor;
import static java.lang.Math.log;

public class CrosswordGui {

    private final JPanel gui = new JPanel(new BorderLayout(3, 3));
    private JButton[][] squares;
    private JLabel[][] matrix;
    private JFrame frame = new JFrame("Crossword Puzzle Solver");
    private final JLabel message = new JLabel("Crossword Puzzle");
    private int x_size,y_size;

    public CrosswordGui(int x, int y) {
        x_size=x;
        y_size=y;

        initializeGui(x, y);

        frame.add(this.getGui());
        frame.setLocationByPlatform(true);

        // ensures the frame is the minimum size it needs to be
        // in order display the components within it
        frame.pack();
        // ensures the minimum size is enforced.
        frame.setMinimumSize(frame.getSize());

        frame.setVisible(true);

    }

    private void initializeGui(int x, int y) {
        // set up the main GUI
        gui.setBorder(new EmptyBorder(10, 5, 10, 5));
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        gui.add(tools, BorderLayout.PAGE_START);

        JButton newButton = new JButton("New");
        newButton.addActionListener((ActionEvent e) -> {
            new SizePicker();
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));

        });

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener((ActionEvent e) -> {
            for (int row = 0; row < matrix.length; row++) {
                for (int col = 0; col < matrix[row].length; col++) {
                    if (matrix[row][col] != null)
                        matrix[row][col].setText("\0");
                }
            }

        });

        JButton closeButton = new JButton("Quit");
        closeButton.setBackground(new Color(135,27,19));
        closeButton.setForeground(new Color(255,255,255));
        closeButton.addActionListener((ActionEvent e) ->
            System.exit(0)
        );

        JButton solveButton = new JButton("Solve");
        solveButton.addActionListener(new CrosswordSolution(this));

        tools.add(newButton);
        tools.addSeparator();
        tools.add(solveButton);
        tools.addSeparator();
        tools.add(clearButton);
        tools.addSeparator();
        tools.add(closeButton);
        tools.addSeparator();
        tools.addSeparator();
        tools.add(message);

        JPanel crossword;
        crossword = new JPanel(new GridLayout(0, y + 1));
        crossword.setBorder(new LineBorder(Color.BLACK));
        gui.add(crossword);

        squares = new JButton[x][y];
        matrix = new JLabel[x][y];

        String[] cols = getString(y);

        // create the crossword squares
        Insets buttonMargin = new Insets(5, 5, 5, 5);
        final double WHITE_FREQUENCY = 0.75; //Between 0 and 1
        for (int ii = 0; ii < squares.length; ii++) {
            for (int jj = 0; jj < squares[ii].length; jj++) {
                JButton b = new JButton();
                JLabel label;
                b.setMargin(buttonMargin);
                b.setForeground(Color.BLACK);
                b.setLayout(new BorderLayout());

                ImageIcon icon = new ImageIcon(
                        new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB));
                b.setIcon(icon);
                b.setSize(new Dimension(32,32));

                if (Math.random() < WHITE_FREQUENCY) {
                    b.setBackground(Color.WHITE);
                    label = new JLabel("\0", SwingConstants.CENTER);
                    b.add(label, BorderLayout.CENTER);
                    b.addActionListener((ActionEvent e) ->
                        new InputBox(label, b)
                    );
                    matrix[ii][jj] = label;
                } else {
                    b.setBackground(Color.BLACK);
                    b.setEnabled(false);
                }
                squares[ii][jj] = b;
            }
        }


        //fill the crossword
        crossword.add(new JLabel(""));
        // fill the top row
        for (int ii = 0; ii < y; ii++) {
            crossword.add(
                    new JLabel(cols[ii],
                            SwingConstants.CENTER));
        }
        for (int ii = 0; ii < x; ii++) {
            for (int jj = 0; jj < y; jj++) {
                switch (jj) {
                    case 0:
                        crossword.add(new JLabel("" + (ii + 1),
                                SwingConstants.CENTER));
                    default:
                        crossword.add(squares[ii][jj]);
                }
            }
        }
    }

    private static String[] getString(int size) {
        StringBuilder sb = new StringBuilder();
        int temp = size;
        for (int n = size; size >= 0; size--) {
            n = temp - size;
            char[] buf = new char[(int) floor(log(25 * (n + 1)) / log(26))];
            for (int i = buf.length - 1; i >= 0; i--) {
                n--;
                buf[i] = (char) ('A' + n % 26);
                n /= 26;
            }
            sb.append(new String(buf)).append("-");
        }
        String result = sb.toString();
        return result.substring(1,result.length()).split("-");
    }

    public final boolean setLetter(int x, int y, char letter) {
        letter = Character.toUpperCase(letter);
        JButton square = squares[x][y];
        if (square.getBackground() != Color.WHITE) {
            return false;
        } else {
            matrix[x][y].setText(Character.toString(letter));
            return true;
        }
    }

    public final boolean isUserEntered(int x, int y) {
        return squares[x][y].getBackground() == Color.lightGray;
    }

    public final char getLetter(int x, int y) {
        JButton square = squares[x][y];
        if (square.getBackground() == Color.BLACK) {
            return '\0';
        } else {
            return matrix[x][y].getText().charAt(0);
        }
    }
    public final boolean isBlack(int x,int y)
    {
        JButton square = squares[x][y];
        if (square.getBackground() == Color.BLACK)
            return true;
        return false;
    }

    public final void result(String text, boolean success){
        message.setText(text);
        if(success)
            message.setForeground(new Color(34,139,34));
        else
            message.setForeground(new Color(139,0,0));
    }

    public final int getX_size() {
        return x_size;
    }

    public final int getY_size() {
        return y_size;
    }

    private final JComponent getGui() {
        return gui;
    }


}
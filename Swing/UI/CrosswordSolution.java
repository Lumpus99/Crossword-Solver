package Swing.UI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CrosswordSolution implements ActionListener {

    private CrosswordGui gui;
    boolean success = true;

    CrosswordSolution(CrosswordGui crosswordGui){
        gui = crosswordGui;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("X= "+gui.getX_size()+"Y= "+gui.getY_size());
        //returns true if white - false if black
        gui.setLetter(0,0,'A');
        gui.setLetter(0,1,'A');
        gui.setLetter(1,0,'A');
        gui.setLetter(1,1,'A');

        //returns '\0' if black
        System.out.println(gui.getLetter(0,0));
        System.out.println(gui.getLetter(0,1));
        System.out.println(gui.getLetter(1,0));
        System.out.println(gui.getLetter(1,1));

    }

}

package Swing.UI;


import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.*;



public class InputBox extends JFrame {

    public InputBox(JLabel label, JButton button) {
        super("Crossword Solver");

        JLabel labelChar = new JLabel("Letter:");
        JButton set = new JButton("Set");
        JButton clear = new JButton("Clear");
        JButton cancel = new JButton("Cancel");


        // create a new panel with GridBagLayout manager
        JPanel newPanel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);

        // add components to the panel

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        newPanel.add(labelChar, constraints);

        JTextField textField = new JTextField(10);
        textField.setDocument(new JTextFieldLimit(1));

        constraints.gridx = 1;
        newPanel.add(textField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.CENTER;

        newPanel.add(set, constraints);
        constraints.gridx = 1;
        newPanel.add(clear, constraints);
        constraints.gridx = 2;
        newPanel.add(cancel, constraints);

        // set border for the panel
        newPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Please enter a letter"));

        // add the panel to this frame
        add(newPanel);

        //Set button functions
        set.addActionListener((ActionEvent)->{
            String input = textField.getText();
            if( !input.equals("") && Character.isLetter(input.charAt(0))){
                label.setText(textField.getText().toUpperCase());
                button.setBackground(Color.lightGray);
                this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            }
        });

        clear.addActionListener((ActionEvent)->{
            label.setText("\0");
            button.setBackground(Color.white);

            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));

        });

        cancel.addActionListener((ActionEvent)->
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING))
        );


        pack();
        setLocationRelativeTo(null);
        setSize(380,180);

        this.setVisible(true);
    }
}
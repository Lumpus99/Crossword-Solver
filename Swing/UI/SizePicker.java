package Swing.UI;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.*;



public class SizePicker extends JFrame {

    public SizePicker() {
        super("Crossword Solver");

        JLabel labelRows = new JLabel("Number of rows: ");
        JLabel labelColumns = new JLabel("Number of columns: ");
        JButton generate = new JButton("Generate");

        // create a new panel with GridBagLayout manager
        JPanel newPanel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 10, 10, 10);

        // add components to the panel
        constraints.gridx = 0;
        constraints.gridy = 0;
        newPanel.add(labelRows, constraints);

        SpinnerNumberModel x_model = new SpinnerNumberModel(5, 5, 50, 1);
        SpinnerNumberModel y_model = new SpinnerNumberModel(5, 5, 50, 1);

        JSpinner x_size = new JSpinner(x_model);
        JSpinner y_size = new JSpinner(y_model);

        Dimension d_x =x_size.getPreferredSize();
        Dimension d_y =y_size.getPreferredSize();

        d_x.width = 50;
        d_y.width = 50;
        d_x.height = 30;
        d_y.height = 30;

        x_size.setPreferredSize(d_x);
        y_size.setPreferredSize(d_y);

        x_size.setBounds(70, 70, 50, 40);
        y_size.setBounds(70, 70, 50, 40);

        constraints.gridx = 1;
        newPanel.add(x_size, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        newPanel.add(labelColumns, constraints);

        constraints.gridx = 1;
        newPanel.add(y_size, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        newPanel.add(generate, constraints);

        generate.addActionListener((ActionEvent)->{
            if((Integer)x_size.getValue()>0 || (Integer)y_size.getValue() >0){
                new CrosswordGui((Integer)x_size.getValue(),(Integer)y_size.getValue());
                this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            }

        });

        // set border for the panel
        newPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Please enter size values"));

        // add the panel to this frame
        add(newPanel);

        pack();
        setLocationRelativeTo(null);
        setSize(380,230);

        this.setVisible(true);
    }
}
package Swing.UI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import java.util.HashMap;
import java.io.*;


public class CrosswordSolution implements ActionListener {

    private CrosswordGui gui;
    boolean success = true;
    private final HashMap<String, Integer>  WORDS_MAP = new HashMap<>();
    CrosswordSolution(CrosswordGui crosswordGui) throws IOException {
        gui = crosswordGui;
        BufferedReader br = new BufferedReader(new FileReader("Crossword-Solver\\words.txt"));
        String line = "";
        while((line=br.readLine())!=null)
            WORDS_MAP.put(line,line.length());

        //Its optinal for printing our hash table
        /*System.out.println(WORDS_MAP.size());
        for (String name: WORDS_MAP.keySet()){
            String key = name.toString();
            int value = WORDS_MAP.get(name);
            System.out.println(key + " " + value);
        }*/


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("X= "+gui.getX_size()+"Y= "+gui.getY_size());
        //returns true if white - false if black
        gui.setLetter(0,0,' ');
        gui.setLetter(0,1,' ');
        gui.setLetter(1,0,' ');
        gui.setLetter(1,1,' ');

        //returns '\0' if black
        System.out.println(gui.getLetter(0,0));
        System.out.println(gui.getLetter(0,1));
        System.out.println(gui.getLetter(1,0));
        System.out.println(gui.getLetter(1,1));



    }
    private CrosswordGui solve_Puzzle(CrosswordGui gui)
    {
        if(WORDS_MAP.size()== 0)
            return null;
        for(int i = 0; i < gui.getX_size(); i++) {
            for (int j = 0; j < gui.getY_size(); j++)
            {
                if(!gui.isBlack(i,j))
                {
                    for (String name: WORDS_MAP.keySet()) {
                        //We make a copy here.
                        CrosswordGui cgui = gui;
                        boolean t1 = true,t2 = true;
                        String key = name;
                        int value = WORDS_MAP.get(name);
                        t1 = canbeFilled(cgui,i,j,-1,value);
                        t2 = canbeFilled(cgui,i,j,0,value);
                        if ( !(t1 || t2) )
                            break;
                        if (t1)
                            fillGrid(cgui,i,j,-1,name);
                        else
                            fillGrid(cgui,i,j,0,name);

                    }
                }
            }
        }
        return null;
    }
    private boolean canbeFilled(CrosswordGui cgui,int x,int y,int xory,int value)
    {
        boolean t = true;
        if(xory == -1){ // we look for y
            int z = y,count = 0;
            do{
                count++;
                z++;
            }while(!cgui.isBlack(x,z) && z < cgui.getY_size());
            if(value != count )
                t = false;
        }
        else
        {
            int z = x,count = 0;
            do{
                count++;
                z++;
            }while(!cgui.isBlack(z,y) && z < cgui.getX_size());
            if(value != count )
                t = false;
        }
        return t;
    }
    private void fillGrid(CrosswordGui cgui,int x,int y,int xory,String name)
    {
        if(xory == -1){ // we fill for y
            int z = y,count = 0;
            while(!cgui.isBlack(x,z) && z < cgui.getY_size())
            {
                cgui.setLetter(x,z,name.charAt(count));
                count++;
                z++;
            }
        }
        else{ // we fill for x
            int z = x,count = 0;
            while(!cgui.isBlack(z,y) && z < cgui.getX_size())
            {
                cgui.setLetter(z,y,name.charAt(count));
                count++;
                z++;
            }
        }
    }

}

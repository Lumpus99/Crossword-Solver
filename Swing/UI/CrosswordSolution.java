package Swing.UI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import java.util.HashMap;
import java.io.*;


public class CrosswordSolution implements ActionListener {

    private CrosswordGui gui;
    boolean success = true;
    private final HashMap<String, Integer>  WORDS_MAP = new HashMap<>();
    CrosswordSolution(CrosswordGui crosswordGui) {
        gui = crosswordGui;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        /*System.out.println("X= "+gui.getX_size()+"Y= "+gui.getY_size());
        //returns true if white - false if black
        gui.setLetter(0,0,' ');
        gui.setLetter(0,1,' ');
        gui.setLetter(1,0,' ');
        gui.setLetter(1,1,' ');

        //returns '\0' if black
        System.out.println(gui.getLetter(0,0));
        System.out.println(gui.getLetter(0,1));
        System.out.println(gui.getLetter(1,0));
        System.out.println(gui.getLetter(1,1));*/
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("Crossword-Solver\\words.txt"));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        String line = "";
        try {
                while ((line=br.readLine()) != null)
                    WORDS_MAP.put(line,line.length());
        } catch (IOException ex) {
                ex.printStackTrace();
        }
        //Its optinal for printing our hash table
        /*System.out.println(WORDS_MAP.size());
        for (String name: WORDS_MAP.keySet()){
            String key = name.toString();
            int value = WORDS_MAP.get(name);
            System.out.println(key + " " + value);
        }*/
        //FILLING with ' ' empty spaces.
        for(int i = 0; i < gui.getX_size(); i++)
            for (int j = 0; j < gui.getY_size(); j++)
                gui.setLetter(i, j,' ');
        if(solve_Puzzle() == null)
            System.out.println("NO SOLUTION!");
        else
            System.out.println("A solution exists.");
    }
    private CrosswordGui solve_Puzzle()
    {
        /*if(everycharacterisnotEmpty(gui))
            return gui;
        if(WORDS_MAP.isEmpty())
            return null;*/
        outer:
        for(int i = 0; i < gui.getX_size(); i++) {
            for (int j = 0; j < gui.getY_size(); j++)
            {
                if(!gui.isBlack(i,j))
                {
                    for (String name: WORDS_MAP.keySet()) {
                        if(gui.getLetter(i,j)==' ' || (gui.getLetter(i,j)!=' ' && name.charAt(0) == gui.getLetter(i,j) && ((i < gui.getX_size()-1 && gui.getLetter(i + 1, j )==' ') || (j < gui.getY_size()-1 && gui.getLetter(i, j + 1 )==' ')))) {
                            //We make a copy here.
                            boolean t1 = true, t2 = true ,t3 = true, t4 = true;
                            int value = WORDS_MAP.get(name);
                            t1 = canbeFilled(i, j, -1, value);
                            t2 = canbeFilled(i, j, 0, value);
                            if (!(t1 || t2))
                                continue;
                            if(t1)
                                t3 = fillGrid(i , j, -1, name);
                            else
                                t4 = fillGrid(i, j,0,name);
                            if(!(t3 || t4))
                                continue;
                            WORDS_MAP.remove(name);
                            if (everycharacterisnotEmpty())
                                return gui;
                            i = -1;
                            continue outer;
                            //return solve_Puzzle(cgui);
                        }
                    }
                }
            }
        }
        return null;
    }
    private boolean everycharacterisnotEmpty()
    {
        for(int i = 0; i < gui.getX_size(); i++)
            for (int j = 0; j < gui.getY_size(); j++)
                if(gui.getLetter(i,j)==' ')
                    return false;
        return true;
    }
    private boolean canbeFilled(int x,int y,int xory,int value)
    {
        boolean t = true;
        if(xory == -1){ // we look for y
            int z = y,count = 0;
            do{
                count++;
                z++;
            }while(z < gui.getY_size() && !gui.isBlack(x,z) );
            if(value != count )
                t = false;
        }
        else
        {
            int z = x,count = 0;
            do{
                count++;
                z++;
            }while(z < gui.getX_size() && !gui.isBlack(z,y) );
            if(value != count )
                t = false;
        }
        return t;
    }
    private boolean fillGrid(int x,int y,int xory,String name)
    {
        int[] marker = new int[name.length()];
        if(xory == -1){ // we fill for y
            if(!((y >= 1 && gui.isBlack(x,y-1)) || y == 0))
                return false;
            int z = y,count = 0;
            while(z < gui.getY_size() && !gui.isBlack(x,z) )
            {
                if(gui.getLetter(x,z) != ' ' && gui.getLetter(x,z) != name.charAt(count)){
                    z--; count--;
                    while ( z >= y)
                    {
                        if (marker[count] == 0)
                            gui.setLetter(x,z,' ');
                        z--;
                        count--;
                    }
                    return false;
                }
                if(gui.getLetter(x,z) == ' ')
                    marker[count] = 0;
                else
                    marker[count] = 1;
                gui.setLetter(x,z,name.charAt(count));
                count++;
                z++;
            }
        }
        else{ // we fill for x
            if(!((x >= 1 && gui.isBlack(x - 1,y)) || x == 0))
                return false;
            int z = x,count = 0;
            while(z < gui.getX_size() && !gui.isBlack(z,y) )
            {
                if(gui.getLetter(z,y) != ' ' && gui.getLetter(z,y) != name.charAt(count)) {
                    z--; count--;
                    while ( z >= x)
                    {
                        if (marker[count] == 0)
                            gui.setLetter(z,y,' ');
                        z--;
                        count--;
                    }
                    return false;
                }
                if(gui.getLetter(z,y) == ' ')
                    marker[count] = 0;
                else
                    marker[count] = 1;
                gui.setLetter(z,y,name.charAt(count));
                count++;
                z++;
            }
        }
        return true;
    }
}

package Swing.UI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Stack;
import java.util.HashMap;
import java.io.*;

public class CrosswordSolution implements ActionListener {

    private CrosswordGui gui;
    private HashMap<String, Integer>  WORDS_MAP = new HashMap<>();
    private Stack<char[][]> crosswords;
    private Stack<ArrayList<String>> words;
    private Stack<Integer> currentposes;
    CrosswordSolution(CrosswordGui crosswordGui) {
        gui = crosswordGui;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        /*System.out.println("X= "+gui.getX_size()+"Y= "+gui.getY_size());
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

        //returns false if gray
        System.out.println(gui.isUserEntered(0,0));
        System.out.println(gui.isUserEntered(0,1));
        System.out.println(gui.isUserEntered(1,0));
        System.out.println(gui.isUserEntered(1,1));*/
        long start = System.currentTimeMillis();
        crosswords = new Stack<>();
        words = new Stack<>();
        currentposes = new Stack<>();
        WORDS_MAP = new HashMap<>();
        ArrayList<String> allwords = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("C:\\Users\\Süleyman\\IdeaProjects\\Crossword-Solver\\words3.txt"));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        String line = "";
        try {
                while ((line=br.readLine()) != null) {
                    WORDS_MAP.put(line, line.length());
                    allwords.add(line);
                }
        } catch (IOException ex) {
                ex.printStackTrace();
        }
        /*//Its optinal for printing our hash table
        System.out.println(WORDS_MAP.size());
        for (String name: WORDS_MAP.keySet()){
            String key = name.toString();
            int value = WORDS_MAP.get(name);
            System.out.println(key + " " + value);
        }*/
        char[][] guimatrix = new char[gui.getX_size()][gui.getY_size()];
        changetoMatrix(guimatrix,gui);
        crosswords.push(guimatrix);
        words.push(allwords);
        currentposes.push(0);
        char[][] solved = solve_Puzzle();
        if(solved == null) {
            gui.result("No solution found",false);
        }else {
            changetoGui(solved);
            gui.result("A solution is found",true);
        }
        long end = System.currentTimeMillis();
        float sec = (end - start) / 1000F;
        System.out.println("All conditions searched for " + sec+ " seconds");
    }
    private char[][] solve_Puzzle()
    {
        /*if(everycharacterisnotEmpty(gui))
            return gui;
        if(WORDS_MAP.isEmpty())
            return null;*/
        char[][] nextmatrix;
        char[][] cmatrix;
        ArrayList<String> nextmap;
        ArrayList<String> cmap;
        int current;
        outer:
        while(!crosswords.isEmpty()) {
            cmatrix = crosswords.peek();
            cmap = words.peek();
            for (int i = 0; i < cmatrix.length; i++) {
                for (int j = 0; j < cmatrix[i].length; j++) {
                    if (cmatrix[i][j] != '?') {
                        for (current = currentposes.peek(); current < cmap.size(); current++) {
                            String name = cmap.get(current);
                            if (cmatrix[i][j] == '\0' || (cmatrix[i][j] != '\0' && name.charAt(0) == cmatrix[i][j] && ((i < cmatrix.length - 1 && cmatrix[i + 1][j] == '\0') || (j < cmatrix[i].length - 1 && cmatrix[i][j + 1] == '\0'))))
                            {
                                //We make a copy here.
                                boolean t1 = true, t2 = true, t3 = true, t4 = true;
                                int value = name.length();
                                nextmatrix = new char[gui.getX_size()][gui.getY_size()];
                                copyGui(nextmatrix, cmatrix);
                                nextmap = new ArrayList<>();
                                copyMap(nextmap, cmap);
                                t1 = canbeFilled(i, j, -1, value ,nextmatrix);
                                t2 = canbeFilled(i, j, 0, value ,nextmatrix);
                                if (!(t1 || t2)) {
                                    nextmap = null;
                                    nextmatrix = null;
                                    continue;
                                }
                                if (t1)
                                    t3 = fillGrid(i, j, -1, name, nextmatrix, nextmap);
                                else
                                    t4 = fillGrid(i, j, 0, name, nextmatrix, nextmap);
                                if (!(t3 || t4)) {
                                    nextmap = null;
                                    nextmatrix = null;
                                    continue;
                                }
                                if (everycharacterisnotEmpty(nextmatrix))
                                    return nextmatrix;
                                currentposes.pop();
                                current++;
                                currentposes.push(current);
                                nextmap.remove(name);
                                currentposes.push(0);
                                words.push(nextmap);
                                crosswords.push(nextmatrix);
                                //System.out.println(words.size() +" WORDS AND POS "+ currentposes.size() +" CROSSWORDS SIZES " + crosswords.size() ); printmatrix(nextmatrix);
                                continue outer;
                                //return solve_Puzzle(cgui);

                            }
                        }
                    }
                }
            }
            crosswords.pop();
            words.pop();
            currentposes.pop();
        }
        return null;
    }
    private void printmatrix(char[][] matrix){
        for(int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++)
                System.out.print(matrix[i][j]);
            System.out.println();
        }
    }
    private boolean canbeFilled(int x, int y, int xory, int value, char[][] nextmatrix)
    {
        boolean t = true;
        if(xory == -1){ // we look for y
            int z = y,count = 0;
            do{
                count++;
                z++;
            }while(z < nextmatrix[x].length && nextmatrix[x][z] != '?' );
            if(value != count )
                t = false;
        }
        else
        {
            int z = x,count = 0;
            do{
                count++;
                z++;
            }while(z < nextmatrix.length && nextmatrix[z][y] != '?' );
            if(value != count )
                t = false;
        }
        return t;
    }
    private boolean fillGrid(int x, int y, int xory, String name, char[][] nextmatrix,ArrayList<String> nextmap)
    {
        int[] marker = new int[name.length()];
        if(xory == -1){ // we fill for y
            if(!((y >= 1 && nextmatrix[x][y - 1] == '?') || y == 0))
                return false;
            int z = y,count = 0;
            while(z < nextmatrix[x].length && nextmatrix[x][z] != '?' )
            {
                if(nextmatrix[x][z] != '\0' && nextmatrix[x][z] != name.charAt(count)){
                    clearWord(z ,x, y, marker, count, xory ,nextmatrix );
                    return false;
                }
                if(nextmatrix[x][z] == '\0')
                    marker[count] = 0;
                else
                    marker[count] = 1;
                nextmatrix[x][z] = name.charAt(count);
                count++;
                z++;
            }
            for(int i = y; i < y + name.length(); i++)
            {
                int j = x , empty = 0;
                String word = "";
                while ( j != 0 && nextmatrix[j][i] != '?')
                    j--;
                if (nextmatrix[j][i] == '?')
                    j++;
                while ( j < nextmatrix.length && nextmatrix[j][i] != '?' && nextmatrix[j][i] != '\0') {
                    word += nextmatrix[j][i];
                    j++;
                }
                if (j < nextmatrix.length && nextmatrix[j][i] != '?' && nextmatrix[j][i] == '\0')
                    empty = 1;
                if(!WORDS_MAP.containsKey(word) && !word.equals("")) {
                    if (!containsorassub(z, y, x, marker, count, xory, nextmatrix, empty, word, name))
                        return false;
                }
                else if(!word.equals(""))
                    nextmap.remove(word);

            }
        }
        else{ // we fill for x
            if(!((x >= 1 && nextmatrix[x - 1][y] == '?') || x == 0))
                return false;
            int z = x,count = 0;
            while(z < nextmatrix.length && nextmatrix[z][y] != '?' )
            {
                if(nextmatrix[z][y] != '\0' && nextmatrix[z][y] != name.charAt(count)) {
                    clearWord(z ,x, y, marker, count, xory,nextmatrix);
                    return false;
                }
                if(nextmatrix[z][y] == '\0')
                    marker[count] = 0;
                else
                    marker[count] = 1;
                nextmatrix[z][y] = name.charAt(count);
                count++;
                z++;
            }
            for(int i = x; i < x + name.length(); i++)
            {
                int j = y , empty = 0;
                String word = "";
                while ( j != 0 && nextmatrix[i][j] != '?')
                    j--;
                if (nextmatrix[i][j] == '?')
                    j++;
                while ( j < nextmatrix[x].length && nextmatrix[i][j] != '?' && nextmatrix[i][j] != '\0') {
                    word += nextmatrix[i][j];
                    j++;
                }
                if (j < nextmatrix[x].length && nextmatrix[i][j] != '?' && nextmatrix[i][j] == '\0')
                    empty = 1;
                if(!WORDS_MAP.containsKey(word) && !word.equals("")) {
                    if (!containsorassub(z, y, x, marker, count, xory, nextmatrix, empty, word, name))
                        return false;
                }
                else if(!word.equals(""))
                    nextmap.remove(word);
            }
        }
        return true;
    }
    // We clear word when conditions are not appropriate
    public void clearWord(int z, int x, int y, int[] marker, int count, int xory, char[][] nextgui)
    {
        z--; count--;
        if(xory == -1) { // deleting from x
            while (z >= y) {
                if (marker[count] == 0)
                    nextgui[x][z] = '\0';
                z--;
                count--;
            }
        }
        else {
            while ( z >= x)
            {
                if (marker[count] == 0)
                    nextgui[z][y] = '\0';
                z--;
                count--;
            }
        }
    }
    private boolean containsorassub(int z, int y, int x,int[] marker, int count, int xory, char[][] nextmatrix,int empty,String word,String name)
    {
        if (empty == 1) {
            boolean t = false, f;
            for (String keys : WORDS_MAP.keySet()) {
                if(!name.equals(keys)) {
                    t = true;
                    f = false;
                    for (int e = 0; e < word.length(); e++) {
                        if (WORDS_MAP.get(keys) >= word.length() && keys.charAt(e) != word.charAt(e)) {
                            t = false;
                            break;
                        }
                        if (!f && WORDS_MAP.get(keys) < word.length())
                            f = false;
                        else
                            f = true;
                    }
                    if (t && f)
                        break;
                }
            }
            if (!t) {
                //System.out.println("Deleted because cannot its substring");
                clearWord(z, x, y, marker, count, xory,nextmatrix);
                return false;
            }
        }
        else {
            //System.out.println("Deleted because cannot be as substring");
            clearWord(z ,x, y, marker, count, xory,nextmatrix);
            return false;
        }
        return true;
    }
    private boolean everycharacterisnotEmpty(char[][] nextmatrix)
    {
        for(int i = 0; i < nextmatrix.length; i++)
            for (int j = 0; j < nextmatrix[i].length; j++)
                if(nextmatrix[i][j] != '?' && nextmatrix[i][j] == '\0')
                    return false;
        return true;
    }
    private void changetoMatrix(char[][] matrix,CrosswordGui gui)
    {
        for(int i = 0; i < gui.getX_size(); i++)
            for (int j = 0; j < gui.getY_size(); j++)
                if( gui.isBlack(i,j) )
                    matrix[i][j] = '?';
                else
                    matrix[i][j] = gui.getLetter(i,j);
    }
    private void changetoGui(char[][] matrix)
    {
        for(int i = 0; i < matrix.length; i++)
            for (int j = 0; j < matrix[i].length; j++)
                if( matrix[i][j] != '?' ) //which means gui's that position is black itself
                    gui.setLetter(i, j, matrix[i][j]);
    }
    private void copyMap(ArrayList<String> nextmap, ArrayList<String> tmap) {
        for (String name : tmap)
            nextmap.add(name);
    }
    private void copyGui(char[][] copied,char[][] copiedone)
    {
        for (int i = 0; i < copiedone.length; i++)
            for (int j = 0; j < copiedone[i].length; j++)
                copied[i][j] = copiedone[i][j];
    }
    //PREVIOUS That works only one time
    /*private CrosswordGui solve_Puzzle()
    {
        /*if(everycharacterisnotEmpty(gui))
            return gui;
        if(WORDS_MAP.isEmpty())
            return null;
        outer:
        for(int i = 0; i < gui.getX_size(); i++) {
            for (int j = 0; j < gui.getY_size(); j++)
            {
                if(!gui.isBlack(i,j))
                {
                    for (String name: WORDS_MAP.keySet()) {
                        if(gui.getLetter(i,j)=='\0' || (gui.getLetter(i,j)!='\0' && name.charAt(0) == gui.getLetter(i,j) && ((i < gui.getX_size()-1 && gui.getLetter(i + 1, j )=='\0') || (j < gui.getY_size()-1 && gui.getLetter(i, j + 1 )=='\0')))) {
                            //We make a copy here.
                            boolean t1 = true, t2 = true ,t3 = true, t4 = true;
                            int value = WORDS_MAP.get(name);
                            t1 = canbeFilled(i, j, -1 ,value);
                            t2 = canbeFilled(i, j, 0 ,value);
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
    private boolean canbeFilled(int x, int y, int xory,int value)
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
    private boolean fillGrid(int x, int y, int xory, String name)
    {
        int[] marker = new int[name.length()];
        if(xory == -1){ // we fill for y
            if(!((y >= 1 && gui.isBlack(x,y-1)) || y == 0))
                return false;
            int z = y,count = 0;
            while(z < gui.getY_size() && !gui.isBlack(x,z) )
            {
                if(gui.getLetter(x,z) != '\0' && gui.getLetter(x,z) != name.charAt(count)){
                    clearWord(z ,x, y, marker, count, xory );
                    return false;
                }
                if(gui.getLetter(x,z) == '\0')
                    marker[count] = 0;
                else
                    marker[count] = 1;
                gui.setLetter(x,z,name.charAt(count));
                count++;
                z++;
            }
            for(int i = y; i < y + name.length(); i++)
            {
                int j = x , empty = 0;
                String word = "";
                while ( j != 0 && !gui.isBlack(j , i))
                    j--;
                if (gui.isBlack(j , i))
                    j++;
                while ( j < gui.getX_size() && !gui.isBlack(j , i) && gui.getLetter(j ,i) != '\0') {
                    word += gui.getLetter(j, i);
                    j++;
                }
                if (j < gui.getX_size() && !gui.isBlack(j , i) && gui.getLetter(j ,i) == '\0')
                    empty = 1;

                System.out.println("WORD for x'th column = " +word);
                if(!WORDS_MAP.containsKey(word) && !word.equals(""))
                {
                    if (empty == 1) {
                        boolean t = false;
                        System.out.println("ENTERED in WORDS_MAP key set for x!");
                        for (String keys : WORDS_MAP.keySet()) {
                            t = true;
                            for (int e = 0; e < word.length(); e++) {
                                if (WORDS_MAP.get(keys) < word.length() || keys.charAt(e) != word.charAt(e)) {
                                    t = false;
                                    break;
                                }
                            }
                            if (t)
                                break;
                        }
                        if (!t) {
                            System.out.println("ENTERED!");
                            clearWord(z, x, y, marker, count, xory);
                            return false;
                        }
                    }
                    else {
                        clearWord(z ,x, y, marker, count, xory);
                        return false;
                    }
                }
                else
                    WORDS_MAP.remove(word);
            }
        }
        else{ // we fill for x
            if(!((x >= 1 && gui.isBlack(x - 1,y)) || x == 0))
                return false;
            int z = x,count = 0;
            while(z < gui.getX_size() && !gui.isBlack(z,y) )
            {
                if(gui.getLetter(z,y) != '\0' && gui.getLetter(z,y) != name.charAt(count)) {
                    clearWord(z ,x, y, marker, count, xory);
                    return false;
                }
                if(gui.getLetter(z,y) == '\0')
                    marker[count] = 0;
                else
                    marker[count] = 1;
                gui.setLetter(z,y,name.charAt(count));
                count++;
                z++;
            }
            for(int i = x; i < x + name.length(); i++)
            {
                int j = y , empty = 0;
                String word = "";
                while ( j != 0 && !gui.isBlack(i , j))
                    j--;
                if (gui.isBlack(i , j))
                    j++;
                while ( j < gui.getY_size() && !gui.isBlack(i , j) && gui.getLetter(i ,j) != '\0') {
                    word += gui.getLetter(i, j);
                    j++;
                }
                if (j < gui.getY_size() && !gui.isBlack(i , j) && gui.getLetter(i ,j) == '\0')
                    empty = 1;
                System.out.println("WORD for y'th column = " +word);
                if(!WORDS_MAP.containsKey(word) && !word.equals(""))
                {
                    if (empty == 1) {
                        boolean t = false;
                        System.out.println("ENTERED in WORDS_MAP key set for y!");
                        for (String keys : WORDS_MAP.keySet()) {
                            t = true;
                            for (int e = 0; e < word.length(); e++) {
                                if (WORDS_MAP.get(keys) < word.length() || keys.charAt(e) != word.charAt(e)) {
                                    t = false;
                                    break;
                                }
                            }
                            if (t)
                                break;
                        }
                        if (!t) {
                            System.out.println("ENTERED for y in empty !=1");
                            clearWord(z, x, y, marker, count, xory );
                            return false;
                        }
                    }
                    else {
                        clearWord(z ,x, y, marker, count, xory);
                        return false;
                    }
                }
                else
                    WORDS_MAP.remove(word);
            }
        }
        return true;
    }
    // We clear word when conditions are not appropriate
    public void clearWord(int z, int x, int y, int[] marker, int count, int xory)
    {
        z--; count--;
        if(xory == -1) { // deleting from x
            while (z >= y) {
                if (marker[count] == 0)
                    gui.setLetter(x, z, '\0');
                z--;
                count--;
            }
        }
        else {
            while ( z >= x)
            {
                if (marker[count] == 0)
                    gui.setLetter(z, y,'\0');
                z--;
                count--;
            }
        }
    }
    private boolean everycharacterisnotEmpty()
    {
        for(int i = 0; i < gui.getX_size(); i++)
            for (int j = 0; j < gui.getY_size(); j++)
                if(!gui.isBlack(i , j) && gui.getLetter(i,j)=='\0')
                    return false;
        return true;
    }*/
}


package Swing.UI;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    public static ArrayList<char[][]> matrixes;
    private JLabel time;
    CrosswordSolution(CrosswordGui crosswordGui, JLabel time) {
        this.gui = crosswordGui;
        this.time = time;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        long start = System.currentTimeMillis();
        crosswords = new Stack<>();
        words = new Stack<>();
        currentposes = new Stack<>();
        WORDS_MAP = new HashMap<>();
        matrixes = new ArrayList<>();
        ArrayList<String> allwords = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("C:\\Users\\Süleyman\\IdeaProjects\\Crossword-Solver\\popular.txt"));
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
        time.setText("Time to solve: "+(end - start) / 1000F + " seconds");
    }
    private char[][] solve_Puzzle()
    {
        if(!lookforonewords())
            return null;
        char[][] vnextmatrix,hnextmatrix ;
        char[][] cmatrix = crosswords.peek();
        ArrayList<String> vnextmap, hnextmap;
        ArrayList<String> cmap = words.peek();
        for (int i = 0; i < cmatrix.length; i++) {
            for (int j = 0; j < cmatrix[i].length; j++) {
                if (cmatrix[i][j] != '?' &&  (cmatrix[i][j] == '\0' || (j < cmatrix[i].length - 1 && cmatrix[i][j + 1] == '\0') || (i < cmatrix.length - 1 && cmatrix[i + 1][j] == '\0'))  &&  (i == 0 || j == 0 || cmatrix[i][j - 1] == '?' || cmatrix[i - 1][j] == '?')) {
                    boolean wput = false;
                    for (int current = 0; current < cmap.size(); current++) {
                        String name = cmap.get(current);
                        //We make a copy here.
                        boolean t1, t2, t3 = false, t4 = false;
                        int value = name.length();
                        t1 = (j == 0 || cmatrix[i][j - 1] == '?') && canbeFilled(i, j, -1, value ,cmatrix);
                        t2 = (i == 0 || cmatrix[i - 1][j] == '?') &&canbeFilled(i, j, 0, value ,cmatrix);
                        if (!(t1 || t2))   //If cannot be filled anyway we look another word
                            continue;
                        ArrayList<String> nnextmap = new ArrayList<>();
                        copyMap(nnextmap,cmap);
                        nnextmap.remove(name);
                        char[][] nnextmatrix = new char[cmatrix.length][cmatrix[0].length];
                        copyGui(nnextmatrix,cmatrix);
                        if (t1)
                            t3 = fillGrid(i, j, -1, name, nnextmatrix, nnextmap);
                        if (!t3 && t2)
                            t4 = fillGrid(i, j, 0, name, nnextmatrix, nnextmap);
                        if (!(t3 || t4))  //If cannot be filled even with conditions we look another word again
                            continue;
                        copyGui(cmatrix,nnextmatrix);
                        cmap = new ArrayList<>();
                        copyMap(cmap,nnextmap);
                        for (int o = 0; o < cmatrix.length; o++) {
                            for (int n = 0; n < cmatrix[o].length; n++) {
                                System.out.print(cmatrix[o][n]);
                            }
                            System.out.println();
                        }
                        System.out.println("NAME  "+name);
                        if (everycharacterisnotEmpty(cmatrix)) //We control both types for looking solution
                            return cmatrix;
                        wput = true;
                        break;
                    }
                    /*if(!wput) {
                        System.out.println("NO SLOT!!");
                        return null;
                    }*/
                }
            }
        }
        return null;
    }
    private boolean lookformatrixes(char[][] matrix)
    {
        for(int i = 0; i < matrixes.size(); i++){
            boolean t = true;
            for(int z = 0; z < matrixes.get(i).length; z++) {
                for (int j = 0; j < matrixes.get(i)[z].length; j++) {
                    if (matrix[z][j] != matrixes.get(i)[z][j]){
                        t = false;
                        break;
                    }
                }
                if(!t)
                    break;
            }
            if(t)
                return true;
        }
        return false;
    }
    private boolean lookforonewords() // In this METHOD we look for one-lengthed words. THeir count , we put them randomly to boxes surrounded by black points
    {
        int countsurrounded = 0, olenwords = 0;
        char[][] tempm = crosswords.pop();
        ArrayList<String> tempa = words.pop();
        for (String name : tempa)
            if (name.length() == 1)
                olenwords++;
        for (int i = 0; i < tempm.length; i++) {
            for (int j = 0; j < tempm[i].length; j++) {
                if (tempm[i][j] != '?' && ((i == 0 && j == 0 && tempm[i][j + 1] == '?' && tempm[i + 1][j] == '?') || (i == tempm.length - 1 && j == tempm[i].length - 1 && tempm[i - 1][j] == '?' && tempm[i][j - 1] == '?') || (i == 0 && j == tempm[i].length - 1 && tempm[i][j - 1] == '?' && tempm[i + 1][j] == '?') || (i == tempm.length - 1 && j == 0 && tempm[i - 1][j] == '?' && tempm[i][j + 1] == '?') || (i > 0 && i < tempm.length - 1 && j == 0 && tempm[i - 1][j] == '?' && tempm[i + 1][j] == '?' && tempm[i][j + 1] == '?') || (i > 0 && i < tempm.length - 1 && j == tempm[i].length - 1 && tempm[i - 1][j] == '?' && tempm[i + 1][j] == '?' && tempm[i][j - 1] == '?') || (j > 0 && j < tempm[i].length - 1 && i == tempm.length - 1 && tempm[i - 1][j] == '?' && tempm[i][j + 1] == '?' && tempm[i][j - 1] == '?') || (j > 0 && j < tempm[i].length - 1 && i == 0 && tempm[i + 1][j] == '?' && tempm[i][j + 1] == '?' && tempm[i][j - 1] == '?') || (j > 0 && j < tempm[i].length - 1 && i > 0 && i < tempm.length - 1 && tempm[i + 1][j] == '?' && tempm[i - 1][j] == '?' && tempm[i][j - 1] == '?' && tempm[i][j + 1] == '?'))) {
                    if (tempm[i][j] != '\0' && !WORDS_MAP.containsKey(tempm[i][j] + "")) // Maybe user put word here, it MUST be in text otherwise we return false.
                        return false;
                    countsurrounded++;
                }
            }
        }
        if (olenwords < countsurrounded){ // If text has less one-lengthed words we return false
            System.out.println("Not enough one-length words to fill!");
            return false;
        }
        for(int i = 0; i < tempm.length; i++){
            for(int j = 0; j < tempm[i].length; j++) {
                if (tempm[i][j] != '?' && ((i == 0 && j == 0 && tempm[i][j + 1] == '?' && tempm[i + 1][j] == '?') || (i == tempm.length - 1 && j == tempm[i].length - 1 && tempm[i - 1][j] == '?' && tempm[i][j - 1] == '?') || (i == 0 && j == tempm[i].length - 1 && tempm[i][j - 1] == '?' && tempm[i + 1][j] == '?') || (i == tempm.length - 1 && j == 0 && tempm[i - 1][j] == '?' && tempm[i][j + 1] == '?') || (i > 0 && i < tempm.length - 1 && j == 0 && tempm[i - 1][j] == '?' && tempm[i + 1][j] == '?' && tempm[i][j + 1] == '?') || (i > 0 && i < tempm.length - 1 && j == tempm[i].length - 1 && tempm[i - 1][j] == '?' && tempm[i + 1][j] == '?' && tempm[i][j - 1] == '?') || (j > 0 && j < tempm[i].length - 1 && i == tempm.length - 1 && tempm[i - 1][j] == '?' && tempm[i][j + 1] == '?' && tempm[i][j - 1] == '?') || (j > 0 && j < tempm[i].length - 1 && i == 0 && tempm[i + 1][j] == '?' && tempm[i][j + 1] == '?' && tempm[i][j - 1] == '?') || (j > 0 && j < tempm[i].length - 1 && i > 0 && i < tempm.length - 1 && tempm[i + 1][j] == '?' && tempm[i - 1][j] == '?' && tempm[i][j - 1] == '?' && tempm[i][j + 1] == '?'))) {
                    if (tempm[i][j] == '\0') {
                        for (String name : tempa) {
                            if (name.length() == 1) {   //We put those one-lenghted words and remove them
                                tempm[i][j] = name.charAt(0);
                                tempa.remove(name);
                                break;
                            }
                        }
                    }
                }
            }
        }
        crosswords.push(tempm);
        int maxblank = findMaxsizeBlank();
        boolean avalenword = false;
        for(int i = 0; i < tempa.size(); i++)
            if( tempa.get(i).length() >= maxblank){
                avalenword = true;
                break;
            }
        if(!avalenword) {
            System.out.println("Not enough word length to fill!");
            return false;
        }
        for(int i = 0; i < tempa.size(); i++) //WE remove rest of the one-length words.
            if(tempa.get(i).length() == 1 || tempa.get(i).length() > maxblank) {
                tempa.remove(tempa.get(i));
                i = 0; continue;
            }
        System.out.println("MAX BLANK=  "+ findMaxsizeBlank() +" And  "+  tempa.size());
        words.push(tempa);
        return true;

    }
    private int findMaxsizeBlank()
    {
        int maxblank = 0;
        char[][] cmatrix = crosswords.peek();
        for(int i = 0; i < cmatrix.length; i++) {
            for (int j = 0; j < cmatrix[i].length; j++) {
                if (cmatrix[i][j] != '?' && (i == 0 || j == 0  || cmatrix[i][j - 1] == '?' || cmatrix[i - 1][j] == '?')){
                    int count = j;
                    do{
                        count++;
                    }while(count < cmatrix[i].length && cmatrix[i][count] != '?' );
                    maxblank = Math.max(maxblank, count - j);
                    count = i;
                    do{
                        count++;
                    }while(count < cmatrix.length && cmatrix[count][j] != '?' );
                    maxblank = Math.max(maxblank, count - i);
                }
            }
        }
        return maxblank;
    }
    private boolean canbeFilled(int x, int y, int xory, int value, char[][] nextmatrix) //FIRSTLY we are looking for word can be filled with its length
    {
        boolean t = true;
        if(xory == -1){ // we look for y
            int z = y,count = 0;
            while(z < nextmatrix[x].length && nextmatrix[x][z] != '?' ){
                count++;
                z++;
            }
            if(value != count )
                t = false;
        }
        else // we look for x
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
    private boolean fillGrid(int x, int y, int xory, String name, char[][] nextmatrix,ArrayList<String> nextmap) // MOST IMPORTANT PART
    {
        int[] marker = new int[name.length()];
        if(xory == -1){ // we fill for y
            int z = y,count = 0;
            while(z < nextmatrix[x].length && nextmatrix[x][z] != '?' )
            {
                if(nextmatrix[x][z] != '\0' && nextmatrix[x][z] != name.charAt(count)){  //If it converges with another word's letter and not appropriate we remove it.
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
            ArrayList<String> nnextmap = new ArrayList<>();
            copyMap(nnextmap,nextmap);
            nnextmap.remove(name);
            char[][] nnextmatrix = new char[nextmatrix.length][nextmatrix[0].length];
            copyGui(nnextmatrix,nextmatrix);
            for(int i = y; i < y + name.length(); i++)  //WE look here for horizontal lines for all they must be meaningful words
            {
                int j = x , empty = 0;
                String word = "";
                while ( j != 0 && nnextmatrix[j][i] != '?') //we go terrain or black zone
                    j--;
                if (nnextmatrix[j][i] == '?')
                    j++;
                while ( j < nnextmatrix.length && nnextmatrix[j][i] != '?' && nnextmatrix[j][i] != '\0') { //Now we build our word
                    word += nnextmatrix[j][i];
                    j++;
                }
                if (j < nnextmatrix.length &&  nnextmatrix[j][i] == '\0') // If we reach empty box we make our empty
                    empty = 1;
                if(empty == 0) {  //WE are looking for this since empty=0 means we reached terrain or black zone.
                    if (!WORDS_MAP.containsKey(word)) { //If it NOT available in hashmap which means we cannot use that word!
                        clearWord(z, x, y, marker, count, xory, nextmatrix);
                        return false;
                    }
                }
                else{
                    if(!word.equals(""))
                        while ( j != 0 && nnextmatrix[j][i] != '?' && nnextmatrix[j - 1][i] != '?')
                            j--;

                    if(!containsorassub( y, x, xory, nnextmatrix, word, name, nnextmap, i, j)) {
                        clearWord(z, x, y, marker, count, xory, nextmatrix);
                        return false;
                    }

                    //BURADA VAR OLAN MATRİSE BAKMADA KALDIM

                }
            }
            copyGui(nextmatrix, nnextmatrix);
            nextmap = new ArrayList<>();
            copyMap(nextmap, nnextmap);
        }
        else{ // we fill for x  (SAME ACTIONS happen at above ) only looking conditions about positions are changed
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
            ArrayList<String> nnextmap = new ArrayList<>();
            copyMap(nnextmap,nextmap);
            nnextmap.remove(name);
            char[][] nnextmatrix = new char[nextmatrix.length][nextmatrix[0].length];
            copyGui(nnextmatrix,nextmatrix);
            for(int i = x; i < x + name.length(); i++)
            {
                int j = y , empty = 0;
                String word = "";
                while ( j != 0 && nnextmatrix[i][j] != '?')
                    j--;
                if (nnextmatrix[i][j] == '?')
                    j++;
                while ( j < nnextmatrix[x].length && nnextmatrix[i][j] != '?' && nnextmatrix[i][j] != '\0') {
                    word += nnextmatrix[i][j];
                    j++;
                }
                if (j < nnextmatrix[x].length && nnextmatrix[i][j] == '\0')
                    empty = 1;
                if(empty == 0) {
                    if (!WORDS_MAP.containsKey(word) ) {
                        clearWord(z, x, y, marker, count, xory, nextmatrix);
                        return false;
                    }
                }
                else{
                    if(!word.equals(""))
                        while ( j != 0 && nnextmatrix[i][j] != '?' && nnextmatrix[i][j - 1] != '?')
                            j--;
                    if(!containsorassub( y, x, xory, nnextmatrix, word, name, nnextmap, i, j)) {
                        clearWord(z, x, y, marker, count, xory, nextmatrix);
                        return false;
                    }


                }
            }
            copyGui(nextmatrix, nnextmatrix);
            nextmap = new ArrayList<>();
            copyMap(nextmap, nnextmap);

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
    private boolean containsorassub(int y, int x, int xory, char[][] nnextmatrix,String word,String name,ArrayList<String> nnextmap, int i, int j) {
        //THIS METHOD works when need to look at meaningful words when its available to put
        boolean k = false;
        for (String keys : nnextmap) {
            if(keys.equals(name) || keys.equals(word))
                continue;
            boolean t = true;
            if(word.equals(""))
                t = false;
            if (keys.length() >= word.length() && !word.equals("") )
                for (int e = 0; e < word.length(); e++) {
                    if (keys.charAt(e) != word.charAt(e)) { //IN here we are looking for substrings
                        t = false;
                        break;
                    }
                }
            if (word.equals("") ||  t) { //IF one put word is substring at least once then we're done.
                if (xory == 0 && ((!word.equals("") && canbeFilled(i, j, -1, keys.length(), nnextmatrix) && fillGrid(i, j, -1, keys, nnextmatrix, nnextmap)) || (word.equals("") && ((canbeFilled(i, j, -1, keys.length(), nnextmatrix) && fillGrid(i, j, -1, keys, nnextmatrix, nnextmap)) || (canbeFilled(i, j, 0, keys.length(), nnextmatrix) && fillGrid(i, j, 0, keys, nnextmatrix, nnextmap))) ))) {
                //if (xory == 0 && ((!word.equals("") && canbeFilled(i, y, -1, keys.length(), nnextmatrix) && fillGrid(i, y, -1, keys, nnextmatrix, nnextmap)) || (word.equals("") && canbeFilled(i, j, -1, keys.length(), nnextmatrix) && fillGrid(i, j, -1, keys, nnextmatrix, nnextmap)))) {

                    //System.out.println("1=SUB WORD= " + word + "  SUBBED " + keys + " AND pos =" + x + " " + y);
                    /*for (int o = 0; o < nnextmatrix.length; o++) {
                        for (int n = 0; n < nnextmatrix[o].length; n++) {
                            System.out.print(nnextmatrix[o][n]);
                        }
                        System.out.println();
                    }*/
                    k = true;
                    break;

                } else if (xory == -1 && ((!word.equals("") && canbeFilled(j, i, 0, keys.length(), nnextmatrix) && fillGrid(j, i, 0, keys, nnextmatrix, nnextmap)) || (word.equals("") && ((canbeFilled(j, i, 0, keys.length(), nnextmatrix) && fillGrid(j, i, 0, keys, nnextmatrix, nnextmap)) || (canbeFilled(j, i, -1, keys.length(), nnextmatrix) && fillGrid(j, i, -1, keys, nnextmatrix, nnextmap))) ))) {
                    //System.out.println("2=SUB WORD= " + word + "  SUBBED " + keys + " AND pos =" + x + " " + y);
                    /*for (int o = 0; o < nnextmatrix.length; o++) {
                        for (int n = 0; n < nnextmatrix[o].length; n++) {
                            System.out.print(nnextmatrix[o][n]);
                        }
                        System.out.println();
                    }*/
                    k = true;
                    break;
                }
            }
        }
        if (!k)    //Which means we did not find any substring then we must remove it.
            return false;
        return true;
    }
    //This line is needed for SOLUTION
    private boolean everycharacterisnotEmpty(char[][] nextmatrix)
    {
        for(int i = 0; i < nextmatrix.length; i++)
            for (int j = 0; j < nextmatrix[i].length; j++)
                if(nextmatrix[i][j] != '?' && nextmatrix[i][j] == '\0')
                    return false;
        return true;
    }
    private void changetoMatrix(char[][] matrix,CrosswordGui gui) //Before we start we change our GUI to matrix
    {
        for(int i = 0; i < gui.getX_size(); i++)
            for (int j = 0; j < gui.getY_size(); j++)
                if( gui.isBlack(i,j) )
                    matrix[i][j] = '?';
                else
                    matrix[i][j] = gui.getLetter(i,j);
    }
    private void changetoGui(char[][] matrix)  // When we found solution we change to our GUI again
    {
        for(int i = 0; i < matrix.length; i++)
            for (int j = 0; j < matrix[i].length; j++)
                if( matrix[i][j] != '?' ) //which means gui's that position is black itself
                    gui.setLetter(i, j, matrix[i][j]);
    }
    private void copyMap(ArrayList<String> nextmap, ArrayList<String> tmap) { // WE COPY GIVEN MAP
        for (String name : tmap)
            nextmap.add(name);
    }
    private void copyGui(char[][] copied,char[][] copiedone) // We are going to copy our gui for array
    {
        for (int i = 0; i < copiedone.length; i++)
            for (int j = 0; j < copiedone[i].length; j++)
                copied[i][j] = copiedone[i][j];
    }
}
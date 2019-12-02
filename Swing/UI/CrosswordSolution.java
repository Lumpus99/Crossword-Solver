package Swing.UI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.HashMap;
import java.io.*;

public class CrosswordSolution implements ActionListener {

    private CrosswordGui gui;
    private final List<String> WORDS = new ArrayList<>();
    private Stack<char[][]> states = new Stack<>();
    private static final boolean VERTICAL = true;
    private static final boolean HORIZONTAL = false;



    CrosswordSolution(CrosswordGui crosswordGui) {
        gui = crosswordGui;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        long start = System.currentTimeMillis();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("D:\\Crossword-Tests\\Test1\\words2.txt"));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return;
        }
        try {
            String line;
            while ((line=br.readLine()) != null) {
                    WORDS.add(line);
                }
        } catch (IOException ex) {
                ex.printStackTrace();
                return;
        }

        initialize();
        long end = System.currentTimeMillis();
        float sec = (end - start) / 1000F;
        System.out.println("All conditions searched for " + sec+ " seconds");
    }
    private void initialize(){
        char[][] initial = new char[gui.getX_size()+2][gui.getY_size()+2];
        for(int i = 0; i < initial.length; i++) {
            for(int j = 0; j < initial[i].length; j++) {
                if(i == 0|| j==0|| i==gui.getX_size()+1||j==gui.getY_size()+1){
                    initial[i][j]='$';
                }else if(i < gui.getX_size()&&j < gui.getY_size() && gui.isBlack(i-1,j-1))
                    initial[i][j]='$';
            }
        }
    }

    private char[][] insertWord(char[][] state, String word, int x, int y, boolean type){
        if(getSpaceLength(state,x,y,type)!=word.length())
            return null;
        if(type = CrosswordSolution.HORIZONTAL) {
            for (int counter = 0; state[x+counter][y] != '$'; counter++){
                state[x+counter][y] = word.charAt(counter);
            }
        }else{
            for (int counter = 0; state[x][y+counter] != '$'; counter++){
                state[x][y+counter] = word.charAt(counter);
            }
        }
        return state;
    }
    //İlk harfinin x ve y koordinatlarını alıyor ve sığabilecek kelime büyüklüğünü veriyor
    private int getSpaceLength(char[][] state, int x, int y, boolean type){
        int counter;
        if(type == CrosswordSolution.HORIZONTAL) {
            for (counter = 0; state[x+counter][y] != '$'; counter++);
        }else{
            for (counter = 0; state[x][y+counter] != '$'; counter++);
        }
        return counter;
    }
    /*
    private char[][] solve_Puzzle()
    {
        if(!lookforonewords())
            return null;
        char[][] vnextmatrix,hnextmatrix ;
        char[][] cmatrix;
        ArrayList<String> vnextmap, hnextmap;
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
                                vnextmatrix = new char[gui.getX_size()][gui.getY_size()];
                                copyGui(vnextmatrix, cmatrix);
                                hnextmatrix = new char[gui.getX_size()][gui.getY_size()];
                                copyGui(hnextmatrix, cmatrix);
                                vnextmap = new ArrayList<>();
                                copyMap(vnextmap, cmap);
                                hnextmap = new ArrayList<>();
                                copyMap(hnextmap, cmap);
                                t1 = canbeFilled(i, j, -1, value ,hnextmatrix);
                                t2 = canbeFilled(i, j, 0, value ,vnextmatrix);
                                if (!(t1 || t2)) {  //If cannot be filled anyway we look another word
                                    vnextmap = null;
                                    vnextmatrix = null;
                                    hnextmatrix = null;
                                    hnextmap = null;
                                    continue;
                                }
                                if (t1)
                                    t3 = fillGrid(i, j, -1, name, hnextmatrix, hnextmap);
                                if (t2)
                                    t4 = fillGrid(i, j, 0, name, vnextmatrix, vnextmap);
                                if (!(t3 || t4)) {  //If cannot be filled even with conditions we look another word again
                                    vnextmap = null;
                                    vnextmatrix = null;
                                    hnextmatrix = null;
                                    hnextmap = null;
                                    continue;
                                }
                                if (everycharacterisnotEmpty(vnextmatrix)) //We control both types for looking solution
                                    return vnextmatrix;
                                if (everycharacterisnotEmpty(hnextmatrix))
                                    return hnextmatrix;
                                currentposes.pop();
                                currentposes.push(current + 1);  // we update current position of this matrix's wordlist
                                if(t3) {
                                    hnextmap.remove(name);
                                    currentposes.push(0);
                                    words.push(hnextmap);
                                    crosswords.push(hnextmatrix);
                                }
                                if(t4) {  //We put our matrix, both horizontically and vertically if possible, since when this word is put with only horizontically maybe it has a solution in vertical and vice versa(or both)
                                    vnextmap.remove(name);
                                    currentposes.push(0);
                                    words.push(vnextmap);
                                    crosswords.push(vnextmatrix);
                                }
                                continue outer;
                            }
                        }
                    }
                }
            }
            crosswords.pop(); //When we cannot do anything with these matrix with its words we pop these.
            words.pop();
            currentposes.pop();
        }
        return null;
    }
    private boolean lookforonewords() // In this METHOD we look for one-lengthed words. THeir count , we put them randomly to boxes surrounded by black points
    {
        int countsurrounded = 0, olenwords = 0;
        char[][] tempm = crosswords.pop();
        ArrayList<String> tempa = words.pop();
        for (String name : tempa)
            if(name.length() == 1)
                olenwords++;
        for(int i = 0; i < tempm.length; i++) {
            for (int j = 0; j < tempm[i].length; j++){
                if (tempm[i][j] != '?' && ((i == 0 && j == 0 && tempm[i][j + 1] == '?' && tempm[i + 1][j] == '?') || (i == tempm.length - 1 && j == tempm[i].length - 1 && tempm[i - 1][j] == '?' && tempm[i][j - 1] == '?') || (i == 0 && j == tempm[i].length - 1 && tempm[i][j - 1] == '?' && tempm[i + 1][j] == '?') || (i == tempm.length - 1 && j == 0 && tempm[i - 1][j] == '?' && tempm[i][j + 1] == '?') || (i > 0 && i < tempm.length - 1 && j == 0 && tempm[i - 1][j] == '?' && tempm[i + 1][j] == '?' && tempm[i][j + 1] == '?') || (i > 0 && i < tempm.length - 1 && j == tempm[i].length - 1 && tempm[i - 1][j] == '?' && tempm[i + 1][j] == '?' && tempm[i][j - 1] == '?') || (j > 0 && j < tempm[i].length - 1 && i == tempm.length - 1 && tempm[i - 1][j] == '?' && tempm[i][j + 1] == '?' && tempm[i][j - 1] == '?') || (j > 0 && j < tempm[i].length - 1 && i == 0 && tempm[i + 1][j] == '?' && tempm[i][j + 1] == '?' && tempm[i][j - 1] == '?') || (j > 0 && j < tempm[i].length - 1 && i > 0 && i < tempm.length - 1 && tempm[i + 1][j] == '?' && tempm[i - 1][j] == '?' && tempm[i][j - 1] == '?' && tempm[i][j + 1] == '?'))) {
                    if (tempm[i][j] != '\0' && !WORDS_MAP.containsKey(tempm[i][j] + "")) // Maybe user put word here, it MUST be in text otherwise we return false.
                        return false;
                    countsurrounded++;
                }
            }
        }
        if(olenwords < countsurrounded) // If text has less one-lengthed words we return false
            return false;
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
        for(int i = 0; i < tempa.size(); i++) //WE remove rest of the one-length words.
            if(tempa.get(i).length() == 1) {
                tempa.remove(tempa.get(i));
                i = 0; continue;
            }
        crosswords.push(tempm);
        words.push(tempa);
        return true;

    }
    private boolean canbeFilled(int x, int y, int xory, int value, char[][] nextmatrix) //FIRSTLY we are looking for word can be filled with its length
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
            if(!((y >= 1 && nextmatrix[x][y - 1] == '?') || y == 0))  //WE look that position must be at terrain or behind of black zone
                return false;
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
            for(int i = y; i < y + name.length(); i++)  //WE look here for horizontal lines for all they must be meaningful words
            {
                int j = x , empty = 0;
                String word = "";
                while ( j != 0 && nextmatrix[j][i] != '?') //we go terrain or black zone
                    j--;
                if (nextmatrix[j][i] == '?')
                    j++;
                while ( j < nextmatrix.length && nextmatrix[j][i] != '?' && nextmatrix[j][i] != '\0') { //Now we build our word
                    word += nextmatrix[j][i];
                    j++;
                }
                if (j < nextmatrix.length && nextmatrix[j][i] != '?' && nextmatrix[j][i] == '\0') // If we reach empty box we make our empty
                    empty = 1;
                if(empty == 0) {  //WE are looking for this since empty=0 means we reached terrain or black zone.
                    if (!WORDS_MAP.containsKey(word) && !word.equals("")) { //If it NOT available in hashmap which means we cannot use that word!
                        clearWord(z, x, y, marker, count, xory, nextmatrix);
                        return false;
                    }
                }
                else{
                    if (!word.equals("") && !containsorassub(z, y, x, marker, count, xory, nextmatrix, word, name)) // Here we look for substring if there is its ok otherwise we remove it.
                        return false;
                }
            }
        }
        else{ // we fill for x  (SAME ACTIONS happen at above ) only looking conditions about positions are changed
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
                if(empty == 0) {
                    if (!WORDS_MAP.containsKey(word) && !word.equals("")) {
                        clearWord(z, x, y, marker, count, xory, nextmatrix);
                        return false;
                    }
                }
                else{
                    if (!word.equals("") && !containsorassub(z, y, x, marker, count, xory, nextmatrix, word, name))
                        return false;
                }
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
    private boolean containsorassub(int z, int y, int x,int[] marker, int count, int xory, char[][] nextmatrix,String word,String name) {
        //THIS METHOD works when need to look at meaningful words when its available to put
        boolean t = false, f;
        for (String keys : WORDS_MAP.keySet()) {
            if(!name.equals(keys)) {
                t = true;
                f = false;
                if (WORDS_MAP.get(keys) < word.length())
                    f = false;
                else
                    f = true;
                for (int e = 0; e < word.length(); e++) {
                    if (WORDS_MAP.get(keys) >= word.length() && keys.charAt(e) != word.charAt(e)) { //IN here we are looking for substrings
                        t = false;
                        break;
                    }
                }
                if (t && f) //IF one put word is substring at least once then we're done.
                    break;
            }
        }
        if (!t) {   //Which means we did not find any substring then we must remove it.
            clearWord(z, x, y, marker, count, xory,nextmatrix);
            return false;
        }
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
    */
}

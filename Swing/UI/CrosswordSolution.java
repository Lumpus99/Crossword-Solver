package Swing.UI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.*;
import java.io.*;
import java.util.List;

public class CrosswordSolution implements ActionListener {

    private CrosswordGui gui;
    private final List<String> WORDS = new ArrayList<>();
    private static final int NOT_A_HEAD = -1;
    private static final int VERTICAL = 0;
    private static final int HORIZONTAL = 1;
    private static final int VERTICAL_AND_HORIZONTAL = 2;


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
            while ((line = br.readLine()) != null) {
                WORDS.add(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        char[][] solution = solve();
        if(solution == null)
            System.out.println("No solution.");
        else
            for(int i=0; i<solution.length; i++) {
                for(int j=0; j<solution[i].length; j++) {
                    System.out.print(solution[i][j]);
                }
                System.out.println("");
            }

        long end = System.currentTimeMillis();
        float sec = (end - start) / 1000F;
        System.out.println("All conditions searched for " + sec + " seconds");
    }
    private char[][] initialize() {
        char[][] initial = new char[gui.getX_size() + 2][gui.getY_size() + 2];
        for (int i = 0; i < initial.length; i++) {
            for (int j = 0; j < initial[i].length; j++) {
                if (i == 0 || j == 0 || i == gui.getX_size() + 1 || j == gui.getY_size() + 1) {
                    initial[i][j] = '$';
                } else if (i <= gui.getX_size() && j <= gui.getY_size() && gui.isBlack(i - 1, j - 1)) {
                    initial[i][j] = '$';
                } else if (i < gui.getX_size() && j < gui.getY_size() && gui.isUserEntered(i - 1, j - 1)) {
                    initial[i][j] = gui.getLetter(i - 1, j - 1);
                }else{
                    initial[i][j] = '\0';
                }
            }
        }
        return initial;
    }

    private char[][] solve() {
        Stack<CrosswordState> state_stack = new Stack<>();

        char[][] current_puzzle = initialize();
        List<Point> heads = getSpaceList(current_puzzle);
        if (heads.isEmpty())
            return null;
        int type = verticalOrHorizontal(current_puzzle, heads.get(0).x, heads.get(0).y);
        List<String> possible_words = null;
        List<String> possible_words_v = null;
        List<String> possible_words_h = null;

        if (type == CrosswordSolution.HORIZONTAL) {
            possible_words = getSuitableWords(current_puzzle, heads.get(0).x, heads.get(0).y, CrosswordSolution.HORIZONTAL);
        } else if (type == CrosswordSolution.VERTICAL) {
            possible_words = getSuitableWords(current_puzzle, heads.get(0).x, heads.get(0).y, CrosswordSolution.VERTICAL);
        } else if (type == CrosswordSolution.VERTICAL_AND_HORIZONTAL) {
            possible_words_h = getSuitableWords(current_puzzle, heads.get(0).x, heads.get(0).y, CrosswordSolution.HORIZONTAL);
            boolean backtrack = false;
            do {
                if (backtrack) {
                    possible_words_h.remove(0);
                }
                if (possible_words_h.isEmpty())
                    return null;

                current_puzzle = insertWord(current_puzzle, possible_words_h.get(0), heads.get(0).x, heads.get(0).y, CrosswordSolution.HORIZONTAL);

                possible_words_v = getSuitableWords(current_puzzle, heads.get(0).x, heads.get(0).y, CrosswordSolution.VERTICAL);
                if (!possible_words_v.isEmpty())
                    current_puzzle = insertWord(current_puzzle, possible_words_v.get(0), heads.get(0).x, heads.get(0).y, CrosswordSolution.VERTICAL);
                backtrack = true;
            } while (possible_words_v.isEmpty());

            CrosswordState crosswordState_h = new CrosswordState(copyOf(current_puzzle), possible_words_h, heads.get(0), CrosswordSolution.HORIZONTAL);
            CrosswordState crosswordState_v = new CrosswordState(copyOf(current_puzzle), possible_words_v, heads.get(0), CrosswordSolution.VERTICAL);
            state_stack.push(crosswordState_h);
            state_stack.push(crosswordState_v);
        }

        if (possible_words != null) {
            if (possible_words.isEmpty())
                return null;
            current_puzzle = insertWord(current_puzzle, possible_words.get(0), heads.get(0).x, heads.get(0).y, type);
            CrosswordState crosswordState = new CrosswordState(copyOf(current_puzzle), possible_words, heads.get(0), type);
            state_stack.push(crosswordState);
        }

        for (int count = 1;; count++) {
            if(state_stack.isEmpty())
                return null;
            print(state_stack.peek().getBoard());
            if (count == heads.size()) {
                return state_stack.peek().getBoard();
            }
            CrosswordState currentState = state_stack.peek();
            int head_type = verticalOrHorizontal(currentState.getBoard(), heads.get(count).x, heads.get(count).y);
            if(head_type == CrosswordSolution.VERTICAL_AND_HORIZONTAL && currentState.getPoint() == new Point(heads.get(count).x, heads.get(count).y))
                head_type = CrosswordSolution.VERTICAL;

            if (head_type == CrosswordSolution.HORIZONTAL || head_type == CrosswordSolution.VERTICAL) {
                List<String> head_possible_words = getSuitableWords(currentState.getBoard(), heads.get(count).x, heads.get(count).y, head_type);
                if (head_possible_words.isEmpty()) {
                    state_stack = backtrack(state_stack);
                    if(state_stack == null)
                        return null;

                    state_stack.peek().remove_One();
                    count = heads.indexOf(state_stack.peek().getPoint()) - 1;
                } else {
                    char[][] newBoard = insertWord(currentState.getBoard(), head_possible_words.get(0), heads.get(count).x, heads.get(count).y, head_type);
                    CrosswordState newState = new CrosswordState(copyOf(newBoard), head_possible_words, heads.get(count), head_type);
                    state_stack.push(newState);
                }
            }else if(head_type == CrosswordSolution.VERTICAL_AND_HORIZONTAL){
                List<String> head_possible_words_h = getSuitableWords(currentState.getBoard(), heads.get(count).x, heads.get(count).y, CrosswordSolution.HORIZONTAL);
                if (head_possible_words_h.isEmpty()) {
                    state_stack = backtrack(state_stack);
                    if(state_stack == null)
                        return null;

                    state_stack.peek().remove_One();
                    count = heads.indexOf(state_stack.peek().getPoint()) - 1;
                } else {
                    char[][] newBoard = insertWord(currentState.getBoard(), head_possible_words_h.get(0), heads.get(count).x, heads.get(count).y, CrosswordSolution.HORIZONTAL);
                    CrosswordState newState = new CrosswordState(copyOf(newBoard), head_possible_words_h, heads.get(count), CrosswordSolution.HORIZONTAL);
                    state_stack.push(newState);
                }
                List<String> head_possible_words_v = getSuitableWords(currentState.getBoard(), heads.get(count).x, heads.get(count).y, CrosswordSolution.VERTICAL);
                if (head_possible_words_v.isEmpty()) {
                    state_stack = backtrack(state_stack);
                    if(state_stack == null)
                        return null;

                    state_stack.peek().remove_One();
                    count = heads.indexOf(state_stack.peek().getPoint()) - 1;
                } else {
                    char[][] newBoard = insertWord(currentState.getBoard(), head_possible_words_v.get(0), heads.get(count).x, heads.get(count).y, CrosswordSolution.VERTICAL);
                    CrosswordState newState = new CrosswordState(copyOf(newBoard), head_possible_words_v, heads.get(count), CrosswordSolution.VERTICAL);
                    state_stack.push(newState);
                }
            }else{
                System.out.println("This shouldn't print...");
            }
        }
    }

    private char[][] insertWord(char[][] state, String word, int x, int y, int type) {
        if (getSpaceLength(state, x, y, type) != word.length())
            return null;
        if (type == CrosswordSolution.VERTICAL) {
            for (int counter = 0; state[x + counter][y] != '$'; counter++) {
                if (!Character.isUpperCase(state[x + counter][y]))
                    state[x + counter][y] = word.charAt(counter);
            }
        } else {
            for (int counter = 0; state[x][y + counter] != '$'; counter++) {
                if (!Character.isUpperCase(state[x][y + counter]))
                    state[x][y + counter] = word.charAt(counter);
            }
        }
        return state;
    }

    //İlk harfinin x ve y koordinatlarını alıyor ve sığabilecek kelime büyüklüğünü veriyor
    private int getSpaceLength(char[][] state, int x, int y, int type) {
        int counter;
        if (type == CrosswordSolution.VERTICAL) {
            for (counter = 0; state[x + counter][y] != '$'; counter++) ;
        } else {
            for (counter = 0; state[x][y + counter] != '$'; counter++) ;
        }
        return counter;
    }

    private boolean isStartofWord(char[][] state, int x, int y) {
        if (state[x][y] == '$')
            return false;
        return (state[x + 1][y] == '$' || state[x][y - 1] != '$');

    }

    private Stack<CrosswordState> backtrack(Stack<CrosswordState> state_stack){
        System.out.println("Backtrack");
        while (true) {
            if (state_stack.isEmpty())
                return null;
            if (state_stack.peek().isRemainsOne())
                state_stack.pop();
            else
                return state_stack;
        }
    }
    public static char[][] copyOf(char[][] original) {
        char [][] newArr = new char[original.length][];
        for(int i = 0; i < original.length; i++)
            newArr[i] = original[i].clone();
        return newArr;
    }
    /* private static final int NOT_A_HEAD = -1;
    private static final int VERTICAL = 0;
    private static final int HORIZONTAL = 1;
    private static final int VERTICAL_AND_HORIZONTAL = 2;*/
    //TODO icini doldur
    private List<Point> getSpaceList (char[][] state){
        List<Point> pospoints = new ArrayList<>();
        for (int i = 1; i < state.length; i++) {
            for (int j = 1; j < state[i].length; j++) {
                int availability = verticalOrHorizontal(state, i, j);
                if (availability != CrosswordSolution.NOT_A_HEAD) {
                    Point point = new Point(i,j);
                    pospoints.add(point);
                }
            }
        }
        return pospoints;
    }
    //TODO icini doldur
    private List<String> getSuitableWords(char[][] state, int x, int y, int type){
        List<String> poswords = new ArrayList<>();
        if(type == CrosswordSolution.HORIZONTAL)
            lookforwords(state, x, y, poswords, HORIZONTAL);
        else if(type == CrosswordSolution.VERTICAL)
            lookforwords(state, x, y, poswords, VERTICAL);
        return poswords;
    }
    private void lookforwords(char[][] state, int x, int y, List<String> poswords, int vertical) {
        for(String name : WORDS) {
            if (state[x][y] == '\0' || (state[x][y] != '$' && (state[x][y] + "").equalsIgnoreCase(name.charAt(0) + ""))) {
                int count = getSpaceLength(state, x, y, vertical);
                if (count == name.length()) {
                    boolean t = true;
                    count = 0;
                    if(vertical == CrosswordSolution.HORIZONTAL)
                        for(int j = y; state[x][j] != '$'; j++) {
                            if (state[x][j] != '\0' && !(state[x][j] + "").equalsIgnoreCase(name.charAt(count) + "")) {
                                t = false;
                                break;
                            }
                            count++;
                        }
                    else if(vertical == CrosswordSolution.VERTICAL)
                        for(int i = x; state[i][y] != '$'; i++) {
                            if (state[i][y] != '\0' && !(state[i][y] + "").equalsIgnoreCase(name.charAt(count) + "")) {
                                t = false;
                                break;
                            }
                            count++;
                        }
                    if(t)
                        poswords.add(name);
                }
            }
        }
    }
    //TODO icini doldur
    private int verticalOrHorizontal(char[][] state,  int x, int y){
        if(state[x][y] == '$')
            return CrosswordSolution.NOT_A_HEAD;
        if (state[x][y] == '$' || (state[x - 1][y] == '$' && state[x + 1][y] == '$' && state[x][y + 1] != '$' && state[x][y - 1] != '$') || (state[x][y - 1] == '$' && state[x][y + 1] == '$' && state[x - 1][y] != '$' && state[x + 1][y] != '$'))
            return CrosswordSolution.NOT_A_HEAD;
        if (state[x - 1][y] == '$' && state[x][y - 1] == '$' && state[x + 1][y] != '$' && state[x][y + 1] != '$')
            return CrosswordSolution.VERTICAL_AND_HORIZONTAL;
        if ((state[x][y + 1] == '$' && state[x - 1][y] == '$' && state[x + 1][y] == '$' && state[x][y - 1] == '$') || (state[x][y - 1] == '$' && state[x][y + 1] != '$'))
            return CrosswordSolution.HORIZONTAL;
        if (state[x - 1][y] == '$' && state[x + 1][y] != '$')
            return CrosswordSolution.VERTICAL;
        return -1;
    }
    private void print(char[][] state){
        for(int i=0; i<state.length; i++) {
            for(int j=0; j<state[i].length; j++) {
                if(state[i][j] == '\0')
                    System.out.print(" ");
                else
                    System.out.print(state[i][j]);
            }
            System.out.println();
        }
        System.out.println("--------");
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

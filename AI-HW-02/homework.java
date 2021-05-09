import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

class CheckersPiece implements Cloneable{
    int row;
    int col;
    String color;
    boolean isKing;

    public CheckersPiece(int row, int col, String color, boolean isKing){
        this.row = row;
        this.col = col;
        this.color = color;
        this.isKing = isKing;
    }

    public CheckersPiece() {
    }

    public void move(int row2, int col2) {
        this.row = row2;
        this.col = col2;
    }

    public void crownItAsKing() {
        this.isKing = true;
    }

    protected Object clone() throws CloneNotSupportedException{
        CheckersPiece p = (CheckersPiece) super.clone();
        p.row = this.row;
        p.col = this.col;
        p.color = this.color;
        return p;
     }

}


class CheckersBoard implements Cloneable{

    CheckersPiece[][] boardConfig;
    int blacksOnBoard; int whitesOnBoard; int blackKingsOnBoard; int whiteKingsOnBoard;

    public CheckersBoard(){
    }

    public CheckersBoard(CheckersPiece[][] boardConfig, int blacksOnBoard, int whitesOnBoard, int blackKingsOnBoard, int whiteKingsOnBoard){
        this.boardConfig = boardConfig;
        this.blacksOnBoard = blacksOnBoard;
        this.whitesOnBoard = whitesOnBoard;
        this.blackKingsOnBoard = blackKingsOnBoard;
        this.whiteKingsOnBoard = whiteKingsOnBoard;
    }

    public CheckersPiece getPiece(int r, int c) {
        CheckersPiece p = boardConfig[r][c];
        return p;
    }

    public ArrayList<CheckersPiece> getAllPieces(String player){
        ArrayList<CheckersPiece> pieces = new ArrayList<CheckersPiece>();
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                if(boardConfig[i][j].color.equals(player)){
                    pieces.add(boardConfig[i][j]);
                }
            }
        }
        return pieces;
    }

    public void move(CheckersPiece p, String move) {

        int row = Character.getNumericValue(move.charAt(0));
        int col = Character.getNumericValue(move.charAt(2));

        CheckersPiece temp = boardConfig[p.row][p.col];
        boardConfig[p.row][p.col] = boardConfig[row][col];
        boardConfig[row][col] = temp;

        p.move(row, col);

        if(p.isKing == false && p.color.equals("BLACK") && row == 7){
            p.crownItAsKing();
            this.blackKingsOnBoard = this.blackKingsOnBoard + 1;
        }
        else if(p.isKing == false && p.color.equals("WHITE") && row == 0){
            p.crownItAsKing();
            this.whiteKingsOnBoard = this.whiteKingsOnBoard + 1;
        }
        
    }

    public void remove(ArrayList<String> skips) {
        for(String s : skips){
            int row = Character.getNumericValue(s.charAt(0));
            int col = Character.getNumericValue(s.charAt(2));

            if(boardConfig[row][col].color.equals("BLACK")){
                this.blacksOnBoard = this.blacksOnBoard - 1;
            }
            else{
                this.whitesOnBoard = this.whitesOnBoard - 1;
            }

            boardConfig[row][col].color = "none";
            boardConfig[row][col].isKing = false;
        }
    }
    
    public HashMap<String, ArrayList<String>> getMovesForPiece1(CheckersPiece p){
        ArrayList<String> jumpPath = new ArrayList<>();
        int direction = 1;

        if(p.color.equals("BLACK")){
            direction = 1;
        }
        else{
            direction = -1;
        }

        HashMap<String, ArrayList<String>> moves1 =  getValidMove(p, p.row, p.col, direction, jumpPath, 1);
        return moves1;
    }

    public HashMap<String, ArrayList<String>> getMovesForPiece2(CheckersPiece p){
        ArrayList<String> jumpPath = new ArrayList<>();
        int direction = 1;

        if(p.color.equals("BLACK")){
            direction = 1;
        }
        else{
            direction = -1;
        }

        HashMap<String, ArrayList<String>> moves2 =  getValidMove(p, p.row, p.col, direction, jumpPath, 2);
        return moves2;
    }

    private boolean checkValidityOfJump(CheckersPiece p, int row, int col, int newRow, int newCol, int direction, int stepSize){

        if(!(p.isKing || newRow == row + direction*stepSize)){
            return false;
        }

        if(((newRow > 7 || newRow < 0) || (newCol > 7 || newCol < 0))){
            return false;
        }

        CheckersPiece newPiece = getPiece(newRow, newCol);

        if(!(newPiece.color.equals("none") || newPiece.equals(p))){
            return false;
        }

        if(stepSize == 2){
            int midRow = (newRow + row) / 2;
            int midCol = (newCol + col) / 2;
            CheckersPiece newPiece2 = getPiece(midRow, midCol);

            if(newPiece2.color.equals("none") || newPiece2.color.equals(p.color)){
                return false;
            }
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    private HashMap<String, ArrayList<String>> getValidMove(CheckersPiece p, int row, int col, int direction, ArrayList<String> jumpPath, int stepSize) {

        int up = row - stepSize;
        int down = row + stepSize;
        int right = col + stepSize;
        int left = col - stepSize;

        int[] colArr = new int[2];
        colArr[0] = left;
        colArr[1] = right;

        int[] rowArr = new int[2];
        rowArr[0] = up;
        rowArr[1] = down;
        
        HashMap<String, ArrayList<String>> moves = new HashMap<>();
        ArrayList<String> newJumpPath = new ArrayList<>();

        for(int i=0; i<2; i++){
            for(int j=0; j<2; j++){

                if(!(checkValidityOfJump(p, row, col, rowArr[j], colArr[i], direction, stepSize))){
                    continue;
                }

                if(stepSize == 1){
                    moves.put(rowArr[j] + " " + colArr[i], null);
                }
                else{
                    int middle_row = (rowArr[j] + row) / 2;
                    int middle_col = (colArr[i] + col) / 2;
                    
                    if(jumpPath.contains(middle_row + " " + middle_col)){
                        continue;
                    }
                    
                    newJumpPath = (ArrayList<String>) jumpPath.clone();

                    newJumpPath.add(middle_row + " " + middle_col);

                    HashMap<String, ArrayList<String>> moves2 = getValidMove(p, rowArr[j], colArr[i], direction, newJumpPath, stepSize);

                    if(moves2.size() == 0){
                        moves.put(rowArr[j] + " " + colArr[i], newJumpPath);
                    }
                    else{
                        moves.putAll(moves2);
                    }
                }
            }
        }

        return moves;
    }

    public void displayBoard() {
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                if(boardConfig[i][j].color.equals("none"))  
                    System.out.print(".");
                else if(boardConfig[i][j].color.equals("BLACK") && boardConfig[i][j].isKing == true)  
                    System.out.print("B");
                else if(boardConfig[i][j].color.equals("BLACK") && boardConfig[i][j].isKing == false)  
                    System.out.print("b");
                else if(boardConfig[i][j].color.equals("WHITE") && boardConfig[i][j].isKing == true)  
                    System.out.print("W");
                else if(boardConfig[i][j].color.equals("WHITE") && boardConfig[i][j].isKing == false)  
                    System.out.print("w");
            }
            System.out.println();
        }
    }

    protected Object clone() throws CloneNotSupportedException{
        CheckersBoard b = (CheckersBoard) super.clone();

        b.boardConfig = new CheckersPiece[8][8];
        b.blacksOnBoard = this.blacksOnBoard;
        b.whitesOnBoard = this.whitesOnBoard;
        b.blackKingsOnBoard = this.blackKingsOnBoard;
        b.whiteKingsOnBoard = this.whiteKingsOnBoard;

        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                b.boardConfig[i][j] = (CheckersPiece) this.boardConfig[i][j].clone();
            }
        }
        return b;
    }

    public int calculateEvaluation(String whichPlayer, int flag) {
        String mainPlayer = "";

        if(flag == 0){
            if(whichPlayer.equals("BLACK")){
                mainPlayer = "WHITE";
            }
            else{
                mainPlayer = "BLACK";
            }
        }
        else{
            mainPlayer = whichPlayer;
        }

        int blackPiecesOnBoard = blacksOnBoard;
        int blackKingPiecesOnBoard = blackKingsOnBoard;
        int whitePiecesOnBoard = whitesOnBoard;
        int whiteKingPiecesOnBoard = whiteKingsOnBoard;
        
        int backRowCount = piecesInBackRow(whichPlayer);
        int midBoxCount = piecesInMidBox(whichPlayer);
        int midRows = piecesInMidRows(whichPlayer);
        // int vulnerableCount = piecesVulnerable(whichPlayer, mainPlayer);
        int cornerPiecesOnBoard = piecesInCornerPos(whichPlayer);
        
        if(whichPlayer.equals("BLACK"))
            return ( (blackPiecesOnBoard * 500 + blackKingPiecesOnBoard * 775 + backRowCount * 400 + midBoxCount * 50 + midRows * 10 + cornerPiecesOnBoard * 25) - (whitePiecesOnBoard * 500 + whiteKingPiecesOnBoard * 775) );
        
        return ( (whitePiecesOnBoard * 500 + whiteKingPiecesOnBoard * 775 + backRowCount * 400 + midBoxCount * 50 + midRows * 10 + cornerPiecesOnBoard * 25) - (blackPiecesOnBoard * 500 + blackKingPiecesOnBoard * 775) );
    }



    private int piecesInBackRow(String whichPlayer) {
        int backRowCountBlack = 0;
        int backRowCountWhite = 0;
        
        if(boardConfig[0][1].color.equals("BLACK")){
            backRowCountBlack++;
        }
        if(boardConfig[0][3].color.equals("BLACK")){
            backRowCountBlack++;
        }
        if(boardConfig[0][5].color.equals("BLACK")){
            backRowCountBlack++;
        }
        if(boardConfig[0][7].color.equals("BLACK")){
            backRowCountBlack++;
        }
        if(boardConfig[7][0].color.equals("WHITE")){
            backRowCountWhite++;
        }
        if(boardConfig[7][2].color.equals("WHITE")){
            backRowCountWhite++;
        }
        if(boardConfig[7][4].color.equals("WHITE")){
            backRowCountWhite++;
        }
        if(boardConfig[7][6].color.equals("WHITE")){
            backRowCountWhite++;
        }

        if(whichPlayer.equals("BLACK")){
            return (backRowCountBlack - backRowCountWhite);
        }

        return (backRowCountWhite - backRowCountBlack);
    }


    private int piecesInMidBox(String whichPlayer) {
        int midBoxCountBlack = 0;
        int midBoxCountWhite = 0;

        for(int i=3; i<5; i++){
            for(int j=2; j<6; j++){
                if(boardConfig[i][j].color.equals("BLACK")){
                    midBoxCountBlack++;
                }
                if(boardConfig[i][j].color.equals("WHITE")){
                    midBoxCountWhite++;
                }
            }
        }

        if(whichPlayer.equals("BLACK")){
            return (midBoxCountBlack - midBoxCountWhite);
        }   

        return (midBoxCountWhite - midBoxCountBlack);
    }


    private int piecesInMidRows(String whichPlayer) {
        int midRowsCountBlack = 0;
        int midRowsCountWhite = 0;

        for(int i=3; i<5; i++){
            for(int j=0; j<2; j++){
                if(boardConfig[i][j].color.equals("BLACK")){
                    midRowsCountBlack++;
                }
                if(boardConfig[i][j].color.equals("WHITE")){
                    midRowsCountWhite++;
                }
            }
        }
        for(int i=3; i<5; i++){
            for(int j=6; j<8; j++){
                if(boardConfig[i][j].color.equals("BLACK")){
                    midRowsCountBlack++;
                }
                if(boardConfig[i][j].color.equals("WHITE")){
                    midRowsCountWhite++;
                }
            }
        }

        if(whichPlayer.equals(("BLACK")))
            return (midRowsCountBlack - midRowsCountWhite);
        
        return (midRowsCountWhite - midRowsCountBlack);
    }

    private int piecesInCornerPos(String whichPlayer) {
        int cornerPieces = 0;
        if(whichPlayer.equals("BLACK")){
            if(boardConfig[0][1].color.equals("BLACK")){
                cornerPieces++;
            }
            if(boardConfig[1][0].color.equals("BLACK")){
                cornerPieces++;
            }
        }

        else if(whichPlayer.equals("WHITE")){
            if(boardConfig[6][7].color.equals("WHITE")){
                cornerPieces++;
            }
            if(boardConfig[7][6].color.equals("WHITE")){
                cornerPieces++;
            }
        }

        return cornerPieces;
    }

}


class Result{

    CheckersBoard cb; int evalValue; HashMap<String, HashMap.Entry<String, ArrayList<String>>> bestMove;

    public Result(CheckersBoard cb, int evalValue){
        this.cb = cb;
        this.evalValue = evalValue;
    }

    public Result(CheckersBoard cb, int evalValue, HashMap<String, HashMap.Entry<String, ArrayList<String>>> bestMove){
        this.cb = cb;
        this.evalValue = evalValue;
        this.bestMove = bestMove;
    }

    public CheckersBoard getBoard(){
        return cb;
    }

    public int getEvalValue() {
        return evalValue;
    }

    public HashMap<String, HashMap.Entry<String, ArrayList<String>>> getBestMove() {
        return bestMove;
    }

}


public class homework {
    static String typeOfProblem;
    static String player;
    static float timeRemaining;
    static CheckersPiece[][] boardConfiguration = new CheckersPiece[8][8];
    static int blacksOnBoard = 0; static int whitesOnBoard = 0; 
    static int blackKingsOnBoard = 0; static int whiteKingsOnBoard = 0;
    static HashMap<String, String> boardNotation = new HashMap<>();
    

    public static void main(String[] args) throws IOException, CloneNotSupportedException {
        
        long startTime = System.currentTimeMillis();
        String finalOutput = "";
        File inputFile = new File("input.txt");
        FileWriter fileWriter = new FileWriter("output.txt");
        ArrayList<String> inputData = new ArrayList<>();
    
        boardNotation.put("0 0", "a8");
        boardNotation.put("1 0", "a7");
        boardNotation.put("2 0", "a6");
        boardNotation.put("3 0", "a5");
        boardNotation.put("4 0", "a4");
        boardNotation.put("5 0", "a3");
        boardNotation.put("6 0", "a2");
        boardNotation.put("7 0", "a1");

        boardNotation.put("0 1", "b8");
        boardNotation.put("1 1", "b7");
        boardNotation.put("2 1", "b6");
        boardNotation.put("3 1", "b5");
        boardNotation.put("4 1", "b4");
        boardNotation.put("5 1", "b3");
        boardNotation.put("6 1", "b2");
        boardNotation.put("7 1", "b1");

        boardNotation.put("0 2", "c8");
        boardNotation.put("1 2", "c7");
        boardNotation.put("2 2", "c6");
        boardNotation.put("3 2", "c5");
        boardNotation.put("4 2", "c4");
        boardNotation.put("5 2", "c3");
        boardNotation.put("6 2", "c2");
        boardNotation.put("7 2", "c1");

        boardNotation.put("0 3", "d8");
        boardNotation.put("1 3", "d7");
        boardNotation.put("2 3", "d6");
        boardNotation.put("3 3", "d5");
        boardNotation.put("4 3", "d4");
        boardNotation.put("5 3", "d3");
        boardNotation.put("6 3", "d2");
        boardNotation.put("7 3", "d1");

        boardNotation.put("0 4", "e8");
        boardNotation.put("1 4", "e7");
        boardNotation.put("2 4", "e6");
        boardNotation.put("3 4", "e5");
        boardNotation.put("4 4", "e4");
        boardNotation.put("5 4", "e3");
        boardNotation.put("6 4", "e2");
        boardNotation.put("7 4", "e1");

        boardNotation.put("0 5", "f8");
        boardNotation.put("1 5", "f7");
        boardNotation.put("2 5", "f6");
        boardNotation.put("3 5", "f5");
        boardNotation.put("4 5", "f4");
        boardNotation.put("5 5", "f3");
        boardNotation.put("6 5", "f2");
        boardNotation.put("7 5", "f1");

        boardNotation.put("0 6", "g8");
        boardNotation.put("1 6", "g7");
        boardNotation.put("2 6", "g6");
        boardNotation.put("3 6", "g5");
        boardNotation.put("4 6", "g4");
        boardNotation.put("5 6", "g3");
        boardNotation.put("6 6", "g2");
        boardNotation.put("7 6", "g1");

        boardNotation.put("0 7", "h8");
        boardNotation.put("1 7", "h7");
        boardNotation.put("2 7", "h6");
        boardNotation.put("3 7", "h5");
        boardNotation.put("4 7", "h4");
        boardNotation.put("5 7", "h3");
        boardNotation.put("6 7", "h2");
        boardNotation.put("7 7", "h1");

        Scanner fileScanner = new Scanner(inputFile);
        int count = 1; int rowCount = 0;

        while (fileScanner.hasNextLine()) {
            String data = fileScanner.nextLine();
            inputData.add(data);
            if (count == 1) {
                typeOfProblem = data;
            }
            else if(count == 2){
                player = data;
            }
            else if(count == 3){
                timeRemaining = Float.parseFloat(data);
            }
            else{
                for(int j=0; j<8; j++){
                    if(data.charAt(j) == '.'){
                        CheckersPiece p = new CheckersPiece(rowCount, j, "none", false);
                        boardConfiguration[rowCount][j] = p;
                    }
                    if(data.charAt(j) == 'b'){
                        CheckersPiece p = new CheckersPiece(rowCount, j, "BLACK", false);
                        boardConfiguration[rowCount][j] = p;
                        blacksOnBoard++;
                    }
                    if(data.charAt(j) == 'B'){
                        CheckersPiece p = new CheckersPiece(rowCount, j, "BLACK", true);
                        boardConfiguration[rowCount][j] = p;
                        blacksOnBoard++;
                        blackKingsOnBoard++;
                    }
                    if(data.charAt(j) == 'w'){
                        CheckersPiece p = new CheckersPiece(rowCount, j, "WHITE", false);
                        boardConfiguration[rowCount][j] = p;
                        whitesOnBoard++;
                    }
                    if(data.charAt(j) == 'W'){
                        CheckersPiece p = new CheckersPiece(rowCount, j, "WHITE", true);
                        boardConfiguration[rowCount][j] = p;
                        whitesOnBoard++;
                        whiteKingsOnBoard++;
                    }
                }
                rowCount++;
            }
            count++;
        } 

        CheckersBoard cb = new CheckersBoard(boardConfiguration, blacksOnBoard, whitesOnBoard, blackKingsOnBoard, whiteKingsOnBoard);

        if(typeOfProblem.equals("SINGLE")){
            finalOutput = findSingleMove(cb, player);
            System.out.println(finalOutput);
        }
        else{
            String opponent;
            if(player.equals("BLACK")){
                opponent = "WHITE";
            }
            else{
                opponent = "BLACK";
            }

            Result returnedAns = minimax(cb, 0, player, opponent, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
            HashMap<String, HashMap.Entry<String, ArrayList<String>>> result = returnedAns.getBestMove();
            finalOutput = computePath(result);
            System.out.println(finalOutput);
        }
        
        fileWriter.write(finalOutput);
        fileScanner.close();
        fileWriter.close();

        long endTime = System.currentTimeMillis();
        
        System.out.println("This the CPU Time - " + ((endTime - startTime)/1000.0) + " secs");
    }


    private static String computePath(HashMap<String, HashMap.Entry<String, ArrayList<String>>> hm) {

        StringBuilder output = new StringBuilder(); 

        for(HashMap.Entry<String, HashMap.Entry<String, ArrayList<String>>> entry : hm.entrySet()){
            String startPos = entry.getKey();
            if(entry.getValue().getValue() == null){
                String destPos = entry.getValue().getKey();
                output.append("E ");
                output.append(boardNotation.get(startPos.charAt(0) + " " + startPos.charAt(2)));
                output.append(" ");
                output.append(boardNotation.get(destPos.charAt(0) + " " + destPos.charAt(2)));
            }
            else{
                String startR = String.valueOf(startPos.charAt(0));
                String startC = String.valueOf(startPos.charAt(2));
                String destR = "";
                String destC = "";
                int checkFlag = 0;

                for(String str : entry.getValue().getValue()){
                    if(Integer.parseInt(String.valueOf(str.charAt(0))) > Integer.parseInt(startR)){
                        if(Integer.parseInt(String.valueOf(str.charAt(2))) > Integer.parseInt(startC)){
                            destR = String.valueOf(Integer.parseInt(String.valueOf(str.charAt(0))) + 1);
                            destC = String.valueOf(Integer.parseInt(String.valueOf(str.charAt(2))) + 1);
                        }
                        else{
                            destR = String.valueOf(Integer.parseInt(String.valueOf(str.charAt(0))) + 1);
                            destC = String.valueOf(Integer.parseInt(String.valueOf(str.charAt(2))) - 1);
                        }
                    }
                    else{
                        if(Integer.parseInt(String.valueOf(str.charAt(2))) > Integer.parseInt(startC)){
                            destR = String.valueOf(Integer.parseInt(String.valueOf(str.charAt(0))) - 1);
                            destC = String.valueOf(Integer.parseInt(String.valueOf(str.charAt(2))) + 1);
                        }
                        else{
                            destR = String.valueOf(Integer.parseInt(String.valueOf(str.charAt(0))) - 1);
                            destC = String.valueOf(Integer.parseInt(String.valueOf(str.charAt(2))) - 1);
                        }
                    }

                    if(checkFlag == 0){
                        output.append("J ");
                        checkFlag = 1;
                    }
                    else{
                        output.append("\nJ ");
                    }
                    output.append(boardNotation.get(startR + " " + startC));
                    output.append(" ");
                    output.append(boardNotation.get(destR + " " + destC));
                    startR = destR;
                    startC = destC;
                }
            }
        }

        return output.toString();
    }


    private static String findSingleMove(CheckersBoard board, String player) {

        ArrayList<CheckersPiece> pieces = board.getAllPieces(player);
        StringBuilder output = new StringBuilder();
        int flag = 0;

        for(int i=0; i<pieces.size(); i++){
            HashMap<String, ArrayList<String>> possibleMoves = board.getMovesForPiece2(pieces.get(i));
            if(possibleMoves.size() != 0){
                for(HashMap.Entry<String, ArrayList<String>> entry : possibleMoves.entrySet()){
                        String startR = String.valueOf(pieces.get(i).row);
                        String startC = String.valueOf(pieces.get(i).col);
                        String destR = "";
                        String destC = "";
                        int checkFlag = 0;

                        for(String str : entry.getValue()){
                            if(Integer.parseInt(String.valueOf(str.charAt(0))) > Integer.parseInt(startR)){
                                if(Integer.parseInt(String.valueOf(str.charAt(2))) > Integer.parseInt(startC)){
                                    destR = String.valueOf(Integer.parseInt(String.valueOf(str.charAt(0))) + 1);
                                    destC = String.valueOf(Integer.parseInt(String.valueOf(str.charAt(2))) + 1);
                                }
                                else{
                                    destR = String.valueOf(Integer.parseInt(String.valueOf(str.charAt(0))) + 1);
                                    destC = String.valueOf(Integer.parseInt(String.valueOf(str.charAt(2))) - 1);
                                }
                            }
                            else{
                                if(Integer.parseInt(String.valueOf(str.charAt(2))) > Integer.parseInt(startC)){
                                    destR = String.valueOf(Integer.parseInt(String.valueOf(str.charAt(0))) - 1);
                                    destC = String.valueOf(Integer.parseInt(String.valueOf(str.charAt(2))) + 1);
                                }
                                else{
                                    destR = String.valueOf(Integer.parseInt(String.valueOf(str.charAt(0))) - 1);
                                    destC = String.valueOf(Integer.parseInt(String.valueOf(str.charAt(2))) - 1);
                                }
                            }

                            if(checkFlag == 0){
                                output.append("J ");
                                checkFlag = 1;
                            }
                            else{
                                output.append("\nJ ");
                            }
                            output.append(boardNotation.get(startR + " " + startC));
                            output.append(" ");
                            output.append(boardNotation.get(destR + " " + destC));
                            startR = destR;
                            startC = destC;
                        }

                    flag = 1;
                    break;
                }
            }

            if(flag == 1)
                break;
        }

        if(output.toString().equals("")){
            for(int i=0; i<pieces.size(); i++){
                HashMap<String, ArrayList<String>> possibleMoves = board.getMovesForPiece1(pieces.get(i));
                if(possibleMoves.size() != 0){
                    for(HashMap.Entry<String, ArrayList<String>> entry : possibleMoves.entrySet()){
                        output.append("E ");
                        output.append(boardNotation.get(String.valueOf(pieces.get(i).row) + " " + String.valueOf(pieces.get(i).col)));
                        output.append(" ");
                        output.append(boardNotation.get(entry.getKey()));

                        flag = 1;
                        break;
                    }
                }
    
                if(flag == 1)
                    break;
            }
        }
    
        return  output.toString();
    }


    private static Result minimax(CheckersBoard board, int depth, String player, String opponent, boolean maxPlaying, int alpha, int beta)
            throws CloneNotSupportedException {
        
        if(depth == 4){
            int calcEval = 0;
            if(maxPlaying == false)
                calcEval = board.calculateEvaluation(opponent, 0);
            else
                calcEval = board.calculateEvaluation(player, 1);

            return new Result(board, calcEval);
        }

        if(maxPlaying){
            int maxEval = Integer.MIN_VALUE;
            CheckersBoard bestBoard = new CheckersBoard();
            HashMap<String, HashMap.Entry<String, ArrayList<String>>> bestMove = new HashMap<>();

            HashMap<CheckersBoard, HashMap<String, HashMap.Entry<String, ArrayList<String>>>> moves = getAllMoves(board, player);

            for(HashMap.Entry<CheckersBoard, HashMap<String, HashMap.Entry<String, ArrayList<String>>>> entry : moves.entrySet()){
                Result returnedEvaluation = minimax(entry.getKey(), depth + 1, player, opponent, false, alpha, beta);
                int evalValue = returnedEvaluation.getEvalValue();
                maxEval = Math.max(maxEval, evalValue);
                alpha = Math.max(alpha, maxEval); 

                if(maxEval == evalValue){
                    bestBoard = entry.getKey();
                    bestMove = entry.getValue();
                }

                if(beta <= alpha){
                    break;
                }
            }

            return new Result(bestBoard, maxEval, bestMove);
        }
        else{

            int minEval = Integer.MAX_VALUE;
            CheckersBoard bestBoard = new CheckersBoard();
            HashMap<String, HashMap.Entry<String, ArrayList<String>>> bestMove = new HashMap<>();

            HashMap<CheckersBoard, HashMap<String, HashMap.Entry<String, ArrayList<String>>>> moves2 = getAllMoves(board, opponent);

            for(HashMap.Entry<CheckersBoard, HashMap<String, HashMap.Entry<String, ArrayList<String>>>> entry : moves2.entrySet()){
                Result returnedEvaluation = minimax(entry.getKey(), depth + 1, player, opponent, true, alpha, beta);
                int evalValue = returnedEvaluation.getEvalValue();
                minEval = Math.min(minEval, evalValue);
                beta = Math.min(beta, minEval); 

                if(minEval == evalValue){
                    bestBoard = entry.getKey();
                    bestMove = entry.getValue();
                }

                if(beta <= alpha){
                    break;
                }

            }
            
            return new Result(bestBoard, minEval, bestMove);
        }

    }


    private static HashMap<CheckersBoard, HashMap<String, HashMap.Entry<String, ArrayList<String>>>> getAllMoves(CheckersBoard board, String player) throws CloneNotSupportedException {
        HashMap<CheckersBoard, HashMap<String, HashMap.Entry<String, ArrayList<String>>>> moves = new HashMap<>();
        ArrayList<CheckersPiece> pieces = board.getAllPieces(player);

        for(int i=0; i<pieces.size(); i++){
            HashMap<String, ArrayList<String>> possibleMoves = board.getMovesForPiece2(pieces.get(i));
            for(HashMap.Entry<String, ArrayList<String>> entry : possibleMoves.entrySet()){
                HashMap<String, HashMap.Entry<String, ArrayList<String>>> moveMade = new HashMap<>();
                moveMade.put(pieces.get(i).row + " " + pieces.get(i).col , entry);
                CheckersBoard tempBoard = (CheckersBoard) board.clone();
                CheckersPiece tempPiece = tempBoard.getPiece(pieces.get(i).row, pieces.get(i).col);
                CheckersBoard newBoard = makeTheMove(tempPiece, entry.getKey(), tempBoard, entry.getValue());
                moves.put(newBoard, moveMade);
            }
        }

        if(moves.size() == 0){
            for(int i=0; i<pieces.size(); i++){
                HashMap<String, ArrayList<String>> possibleMoves = board.getMovesForPiece1(pieces.get(i));
                for(HashMap.Entry<String, ArrayList<String>> entry : possibleMoves.entrySet()){
                    HashMap<String, HashMap.Entry<String, ArrayList<String>>> moveMade = new HashMap<>();
                    moveMade.put(pieces.get(i).row + " " + pieces.get(i).col , entry);
                    CheckersBoard tempBoard = (CheckersBoard) board.clone();
                    CheckersPiece tempPiece = tempBoard.getPiece(pieces.get(i).row, pieces.get(i).col);
                    CheckersBoard newBoard = makeTheMove(tempPiece, entry.getKey(), tempBoard, entry.getValue());
                    moves.put(newBoard, moveMade);
                }
            }
        }
        
        return moves;
    }


    private static CheckersBoard makeTheMove(CheckersPiece p, String move, CheckersBoard b, ArrayList<String> skips) {
        b.move(p, move);
        if(skips != null){
            b.remove(skips);
        }
        return b;
    }

}
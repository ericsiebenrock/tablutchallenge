package server;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/*
estensioen della classe state per compatibilita con algoritmo min-max
 */
public class ExtendedState{

    private StateTablut state;

    private double valoreAssegnato;

    public void setState(State state){
        this.state=(StateTablut)state;
    }

    public StateTablut getState() {
        return state;
    }

    public Action getAction(ExtendedState oldState, String player){
        //System.out.println("old: \n"+oldState.state.boardString());
        //System.out.println("new: \n"+state.boardString());
        Action a=null;
        String from= "";
        String to= "";
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!this.getState().getBoard()[i][j].equals(oldState.getState().getBoard()[i][j])) {
                    if (((oldState.getState().getBoard()[i][j] == State.Pawn.BLACK && player.equals("BLACK")) || (oldState.getState().getBoard()[i][j] == State.Pawn.WHITE && player.equals("WHITE"))|| (oldState.getState().getBoard()[i][j] == State.Pawn.KING && player.equals("WHITE")))
                            && this.getState().getBoard()[i][j] == State.Pawn.EMPTY) {
                        char col = (char) (j + 97);
                        int row = i + 1;
                        from = "" + col + row;
                        continue;
                    }
                    if (((this.getState().getBoard()[i][j] == State.Pawn.BLACK && player.equals("BLACK")) || (this.getState().getBoard()[i][j] == State.Pawn.WHITE && player.equals("WHITE")) || (this.getState().getBoard()[i][j] == State.Pawn.KING && player.equals("WHITE")))
                            && oldState.getState().getBoard()[i][j] == State.Pawn.EMPTY) {
                        char col = (char) (j + 97);
                        int row = i + 1;
                        to = "" + col + row;
                        continue;
                    }
                }
            }
        }
        //System.out.println("from: "+from+" to: "+to);
        if(this.getState().getTurn().equals(State.Turn.BLACK)) {
            try {
                a=new Action(from,to,State.Turn.WHITE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(this.getState().getTurn().equals(State.Turn.WHITE)) {
            try {
                a=new Action(from,to,State.Turn.BLACK);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return a;
    }

    public List<ExtendedState> getActions(){
        //System.out.println("GetActions - stato iniziale: \n"+ this.state.boardString());
        List<ExtendedState> actions = new LinkedList<>();

        //ciclo sulle righe
        for (int row = 0; row < this.state.board.length; row++){
            //ciclo sulle colonne della riga
            for (int column = 0; column < this.state.board.length; column++){

                //turno del BIANCO
                if(this.state.turn==State.Turn.WHITE){
                    //ricerca di una pedina bianca da muovere
                    if(this.state.board[row][column]==State.Pawn.WHITE) {
                        columnMovements(actions, row, column, State.Pawn.WHITE);
                        rowMovements(actions, row, column, State.Pawn.WHITE);
                    }
                    else if(this.state.board[row][column]==State.Pawn.KING){
                        columnMovements(actions, row, column, State.Pawn.KING);
                        rowMovements(actions, row, column, State.Pawn.KING);
                    }
                }

                //turno del NERO
                if(this.state.turn==State.Turn.BLACK){
                    //ricerca di una pedina bianca da muovere
                    if(this.state.board[row][column]==State.Pawn.BLACK) {
                        //System.out.println("Trovata pedina nera da spostare (row: "+row+" column: "+column+")");
                        rowMovements(actions, row, column, State.Pawn.BLACK);
                        columnMovements(actions, row, column, State.Pawn.BLACK);
                    }
                }
            }
        }

        return actions;
    }

    private void columnMovements(List<ExtendedState> actions, int row, int column, State.Pawn movingPiece) {
        boolean legitMove=true, inBlackCamp=false;
        //ricerca di caselle vuote nella stessa colonna (controllando anche che non si tratti della stessa casella o del trono)
        for(int newRow=0; newRow<9; newRow++){
            if(newRow==row) continue;
            if(this.state.board[newRow][column]==State.Pawn.EMPTY && this.state.board[newRow][column] != State.Pawn.THRONE){
                if(movingPiece==State.Pawn.WHITE || movingPiece==State.Pawn.KING){
                    //controllo che non entri negli accampamenti
                    if(isBlackCamp(newRow,column)) continue;
                    if(newRow==4 && column==4) continue; // non si può andare sul trono
                }
                if(movingPiece==State.Pawn.BLACK){
                    //controllo che non entri negli altri accampamenti. Se è nell accampamento (isBlackCamp(row, column)) si può muovere in esso.
                    //la distanza massima di caselle dello stesso accampamento infatti è 2
                    if(isBlackCamp(row, column) && isBlackCamp(newRow,column) && Math.abs(newRow-row)>2) continue;//prossimo ciclo for
                    if(isBlackCamp(row,column)) inBlackCamp=true;

                    if(isBlackCamp(newRow,column) && !isBlackCamp(row,column)) continue;

                    if(newRow==4 && column==4) continue; // non si può andare sul trono
                }
                //riga prima (spostamento verso alto)
                if(newRow<row && (newRow+1)!=row){ // se è la riga adiacente la mossa è lecita (già controllato che sia vuota)
                    //System.out.println("[cMovs] newRow<row: pedina su "+row+" "+column+" (newRow: "+newRow+")");
                    //controllo che le caselle in mezzo non siano occupate o non sia trono o accampamento
                    for (int i = (newRow + 1); i < row; i++) {

                        if (this.state.board[i][column] != State.Pawn.EMPTY || this.state.board[i][column]==State.Pawn.THRONE || (isBlackCamp(i,column) && (Math.abs(row-i)>2 || !inBlackCamp ))) {
                            legitMove = false; //casella in mezzo non vuota oppure trono oppure accampamento
                            break;
                        } else legitMove = true;
                    }
                }
                //riga dopo (spostamento verso basso)
                else if(newRow>row && (newRow-1)!=row){ // se è la riga adiacente la mossa è lecita (già controllato che sia vuota)
                    //System.out.println("[cMovs] newRow>row: pedina su "+row+" "+column+" (newRow: "+newRow+")");
                    for(int i=(newRow-1); i>row; i--) {
                        if (this.state.board[i][column] != State.Pawn.EMPTY || this.state.board[row][i]==State.Pawn.THRONE || (isBlackCamp(i,column) && (Math.abs(row-i)>2 || !inBlackCamp ))) {
                            legitMove = false; //casella in mezzo non vuota
                            break;
                        } else legitMove = true;
                    }
                }
                if(legitMove){
                    //System.out.println("GetActions - this.state.board: \n"+ this.state.boardString());
                    StateTablut newWrappedState=this.state.clone();
                    State.Pawn newBoard[][]=newWrappedState.getBoard();
                    newBoard[row][column]=State.Pawn.EMPTY;
                    newBoard[newRow][column]=movingPiece;
                    newBoard=mangia(newBoard, newRow, column, movingPiece); // elimina le pedine mangiate se ce ne sono
                    newWrappedState.setBoard(newBoard);
                    if(state.turn==State.Turn.WHITE) newWrappedState.setTurn(State.Turn.BLACK);
                    ExtendedState newState= new ExtendedState();
                    newState.setState(newWrappedState);
                    actions.add(newState);
                    //System.out.println("mossa lecita: \n"+newState.state.boardString());
                    //System.out.println("GetActions - movimento verticale: \n"+ newState.getState().boardString());
                }
            }
        }
    }

    private void rowMovements(List<ExtendedState> actions, int row, int column, State.Pawn movingPiece){
        boolean legitMove=true, inBlackCamp=false;
        //ricerca di caselle vuote nella stessa riga (controllando anche che non si tratti della stessa casella o del trono)
        for(int newColumn=0; newColumn<9; newColumn++){
            if(newColumn==column) continue;
            if(this.state.board[row][newColumn]==State.Pawn.EMPTY && this.state.board[row][newColumn] != State.Pawn.THRONE){
                if(movingPiece==State.Pawn.WHITE || movingPiece==State.Pawn.KING){
                    //controllo che non entri negli accampamenti
                    if(isBlackCamp(row,newColumn)) continue;//prossimo ciclo for
                    //if(row==4 && newColumn==4) continue; // non si può andare sul trono
                }
                if(movingPiece==State.Pawn.BLACK){
                    //controllo che non entri negli altri accampamenti. Se è nell accampamento (isBlackCamp(row, column)) si può muovere in esso.
                    //la distanza massima di caselle dello stesso accampamento infatti è 2
                    if(isBlackCamp(row, column) && isBlackCamp(row,newColumn) && Math.abs(newColumn-column)>2) continue;
                    if(isBlackCamp(row,column))inBlackCamp=true;
                    if(isBlackCamp(row,newColumn)&&!isBlackCamp(row,column)) continue;
                    //if(row==4 && newColumn==4) continue; // non si può andare sul trono
                }
                //colonna prima (spostamento a sinistra)
                if(newColumn<column && (newColumn+1)!=column){ // se è la colonna adiacente la mossa è lecita (già controllato che sia vuota)
                    //System.out.println("[rMovs] newColumn<column: pedina su "+row+" "+column+" (newColumn: "+newColumn+")");
                    //controllo che le caselle in mezzo non siano occupate o non sia trono o accampamento
                    for (int i = (newColumn + 1); i < column; i++) {
                        // può passare dal (suo) blackCamp solo se ci è già dentro
                        if (this.state.board[row][i] != State.Pawn.EMPTY || this.state.board[row][i]==State.Pawn.THRONE || (isBlackCamp(row,i) && (Math.abs(column-i)>2 || !inBlackCamp )) ) {
                            legitMove = false; //casella in mezzo non vuota oppure trono oppure accampamento
                            break;
                        } else legitMove = true;
                    }
                }
                //colonna dopo (spostamento a destra)
                else if(newColumn>column && (newColumn-1)!=column){ // se è la colonna adiacente la mossa è lecita (già controllato che sia vuota)
                    //System.out.println("[rMovs] newColumn>column: pedina su "+row+" "+column+" (newColumn: "+newColumn+")");
                    for(int i=(newColumn-1); i>column; i--) {
                        if (this.state.board[row][i] != State.Pawn.EMPTY || this.state.board[row][i]==State.Pawn.THRONE || (isBlackCamp(row,i) && (Math.abs(column-i)>2 || !inBlackCamp )) ) {
                            legitMove = false; //casella in mezzo non vuota
                            break;
                        } else legitMove = true;
                    }
                }
                if(legitMove){
                    //System.out.println("GetActions - this.state.board: \n"+ this.state.boardString());
                    StateTablut newWrappedState=this.state.clone();
                    State.Pawn newBoard[][]=newWrappedState.getBoard();
                    newBoard[row][column]=State.Pawn.EMPTY;
                    newBoard[row][newColumn]=movingPiece;
                    newBoard=mangia(newBoard, row, newColumn, movingPiece); // elimina le pedine mangiate se ce ne sono
                    newWrappedState.setBoard(newBoard);
                    if(state.turn==State.Turn.WHITE) newWrappedState.setTurn(State.Turn.BLACK);
                    else newWrappedState.setTurn(State.Turn.WHITE);
                    ExtendedState newState= new ExtendedState();
                    newState.setState(newWrappedState);
                    actions.add(newState);
                    //System.out.println("mossa lecita: \n"+newState.state.boardString());
                    //System.out.println("GetActions - movimento orizzontale: \n"+ newState.getState().boardString());
                }
            }
        }
    }

    private boolean isBlackCamp(int row, int column){
        boolean res=false;
        switch (row){
            case 0:
            case 8:
                if((column==3 || column==4 || column==5)) res=true;
                break;
            case 1:
            case 7:
                if(column==4) res=true;
                break;
            case 3:
            case 5:
                if(column==0 || column==8)res=true;
                break;
            case 4:
                if(column==0 || column==1 || column==7 || column==8) res=true;
                break;

        }
        return res;
    }

    private State.Pawn[][] mangia(State.Pawn[][] board, int newRow, int newColumn, State.Pawn movingPiece) {
        String rc = "" + newRow + newColumn;
        if(movingPiece == State.Pawn.WHITE){
            if(newRow<7){ // la pedina non è al bordo sotto o nella penultima riga
                if(board[newRow+1][newColumn] == State.Pawn.BLACK && (board[newRow+2][newColumn] == State.Pawn.WHITE || (isBlackCamp(newRow+2, newColumn) && !isBlackCamp(newRow+1,newColumn)))){ //viene mangiata la nera sotto (se non è nel campo)
                    board[newRow+1][newColumn] = State.Pawn.EMPTY;
                }
            }
            if(newRow>1){ // la pedina non è al bordo sopra o nella seconda riga
                if(board[newRow-1][newColumn] == State.Pawn.BLACK && (board[newRow-2][newColumn] == State.Pawn.WHITE || (isBlackCamp(newRow-2, newColumn) && !isBlackCamp(newRow-1,newColumn)))){ //viene mangiata la nera sopra (se non è nel campo)
                    board[newRow-1][newColumn] = State.Pawn.EMPTY;
                }
            }
            if(newColumn<7){ // la pedina non è al bordo destro o nella penultima colonna
                if(board[newRow][newColumn+1] == State.Pawn.BLACK && (board[newRow][newColumn+2] == State.Pawn.WHITE || (isBlackCamp(newRow, newColumn+2) && !isBlackCamp(newRow,newColumn+1)))){ //viene mangiata la nera a destra (se non è nel campo)
                    board[newRow][newColumn+1] = State.Pawn.EMPTY;
                }
            }
            if(newColumn>1) { // la pedina non è al bordo sinistro o nella seconda colonna
                if (board[newRow][newColumn - 1] == State.Pawn.BLACK && (board[newRow][newColumn - 2] == State.Pawn.WHITE || (isBlackCamp(newRow, newColumn - 2) && !isBlackCamp(newRow,newColumn-1)))) { //viene mangiata la nera a sinistra (se non è nel campo)
                    board[newRow][newColumn - 1] = State.Pawn.EMPTY;
                }
            }
            if(rc.equals("24") && board[3][4] == State.Pawn.BLACK){ // viene mangiata la pedina sopra al castello
                board[3][4] = State.Pawn.EMPTY;
            }
            if(rc.equals("46") && board[4][5] == State.Pawn.BLACK){ // viene mangiata la pedina a destra del castello
                board[4][5] = State.Pawn.EMPTY;
            }
            if(rc.equals("64") && board[5][4] == State.Pawn.BLACK){ // viene mangiata la pedina sotto al castello
                board[5][4] = State.Pawn.EMPTY;
            }
            if(rc.equals("42") && board[4][3] == State.Pawn.BLACK){ // viene mangiata la pedina a sinstra del castello
                board[4][3] = State.Pawn.EMPTY;
            }
        }
        if(movingPiece == State.Pawn.BLACK){
            if(newRow<7){ // la pedina non è al bordo sotto o nella penultima riga
                if((board[newRow+1][newColumn] == State.Pawn.WHITE || board[newRow+1][newColumn] == State.Pawn.KING) && (board[newRow+2][newColumn] == State.Pawn.BLACK || isBlackCamp(newRow+2, newColumn))){ //viene mangiata la bianca o il re sotto
                    board[newRow+1][newColumn] = State.Pawn.EMPTY;
                }
            }
            if(newRow>1){ // la pedina non è al bordo sopra o nella seconda riga
                if((board[newRow-1][newColumn] == State.Pawn.WHITE || board[newRow-1][newColumn] == State.Pawn.KING) && (board[newRow-2][newColumn] == State.Pawn.BLACK || isBlackCamp(newRow-2, newColumn))){ //viene mangiata la bianca o il re sopra
                    board[newRow-1][newColumn] = State.Pawn.EMPTY;
                }
            }
            if(newColumn<7){ // la pedina non è al bordo destro o nella penultima colonna
                if((board[newRow][newColumn+1] == State.Pawn.WHITE || board[newRow][newColumn+1] == State.Pawn.KING) && (board[newRow][newColumn+2] == State.Pawn.BLACK || isBlackCamp(newRow, newColumn+2))){ //viene mangiata la bianca o il re a destra
                    board[newRow][newColumn+1] = State.Pawn.EMPTY;
                }
            }
            if(newColumn>1){ // la pedina non è al bordo sinistro o nella seconda colonna
                if((board[newRow][newColumn-1] == State.Pawn.WHITE || board[newRow][newColumn-1] == State.Pawn.WHITE) && (board[newRow][newColumn-2] == State.Pawn.BLACK|| isBlackCamp(newRow, newColumn-2))){ //viene mangiata la bianca o il re a sinistra
                    board[newRow][newColumn-1] = State.Pawn.EMPTY;
                }
            }
            if(rc.equals("24") && board[3][4] == State.Pawn.WHITE){ // viene mangiata la pedina sopra al castello
                board[3][4] = State.Pawn.EMPTY;
            }
            if(rc.equals("46") && board[4][5] == State.Pawn.WHITE){ // viene mangiata la pedina a destra del castello
                board[4][5] = State.Pawn.EMPTY;
            }
            if(rc.equals("64") && board[5][4] == State.Pawn.WHITE){ // viene mangiata la pedina sotto al castello
                board[5][4] = State.Pawn.EMPTY;
            }
            if(rc.equals("42") && board[4][3] == State.Pawn.WHITE){ // viene mangiata la pedina a sinstra del castello
                board[4][3] = State.Pawn.EMPTY;
            }
            String kingRowCol = "";
            for (int row = 0; row < 9; row++) { // cerco la posizione del re
                //ciclo sulle colonne della riga
                for (int column = 0; column < 9; column++) {
                    if(board[row][column]==State.Pawn.KING){
                        kingRowCol=""+row+""+column;
                        break;
                    }
                }
                if(!kingRowCol.equals("")) break;
            }
            if(kingRowCol.equals("44") && state.board[3][4]==State.Pawn.BLACK && state.board[5][4]==State.Pawn.BLACK && state.board[4][3]==State.Pawn.BLACK && state.board[4][5]==State.Pawn.BLACK){ //re mangiato nel trono
                board[4][4] = State.Pawn.EMPTY;
            }
            if(kingRowCol.equals("34") && state.board[2][4]==State.Pawn.BLACK && state.board[3][3]==State.Pawn.BLACK && state.board[3][5]==State.Pawn.BLACK) { //re mangiato da lato superiore trono
                board[3][4] = State.Pawn.EMPTY;
            }
            if(kingRowCol.equals("43") && state.board[3][3]==State.Pawn.BLACK && state.board[4][2]==State.Pawn.BLACK && state.board[5][3]==State.Pawn.BLACK){ //re mangiato da lato sinistro trono
                board[4][3] = State.Pawn.EMPTY;
            }
            if(kingRowCol.equals("54") && state.board[5][3]==State.Pawn.BLACK && state.board[6][4]==State.Pawn.BLACK && state.board[5][5]==State.Pawn.BLACK){ //re mangiato da lato inferiore trono
                board[5][4] = State.Pawn.EMPTY;
            }
            if(kingRowCol.equals("45") && state.board[3][5]==State.Pawn.BLACK && state.board[4][5]==State.Pawn.BLACK && state.board[5][5]==State.Pawn.BLACK){ //re mangiato da lato destro trono
                board[4][5] = State.Pawn.EMPTY;
            }

        }
        return board;
    }



    public double getValoreAssegnato() {
        return valoreAssegnato;
    }

    public void setValoreAssegnato(double valoreAssegnato) {
        this.valoreAssegnato = valoreAssegnato;
    }


    //restituisce: -1 sconfitta, 0 pareggio, 1 vittoria, 2 stato terminale comunque (ci fermiamo TODO), -2 non è terminale
    public int isTerminal(String player, int numWhiteOld, int numBlackOld) {
        int whiteNum=0, blackNum=0, kingRow=-1, kingCol=-1;
        String kingRowCol="";

        for (int row = 0; row < this.state.board.length; row++) {
            //ciclo sulle colonne della riga
            for (int column = 0; column < this.state.board.length; column++) {
                if(state.board[row][column]==State.Pawn.WHITE) whiteNum++;
                if(state.board[row][column]==State.Pawn.KING){
                    whiteNum++;
                    //kingRow=row; kingCol=column;
                    kingRowCol=""+row+""+column;
                }
                if(state.board[row][column]==State.Pawn.BLACK) blackNum++;
            }
        }

        if(whiteNum==0){
            if(player=="WHITE") return -1;
            else return 1;
        }
        else if(blackNum==0){
            if(player=="BLACK") return -1;
            else return 1;
        }
        else if(kingRowCol.equals("01")||kingRowCol.equals("02")||kingRowCol.equals("06")||kingRowCol.equals("07")||kingRowCol.equals("10")||kingRowCol.equals("18")||kingRowCol.equals("20")||kingRowCol.equals("28")||kingRowCol.equals("60")||kingRowCol.equals("68")||kingRowCol.equals("70")||kingRowCol.equals("78")||kingRowCol.equals("81")||kingRowCol.equals("82")||kingRowCol.equals("86")||kingRowCol.equals("87")){
            //il re è scappato
            if(player=="WHITE") return 1;
            else return -1;
        }

        //caso re mangiato
        if(kingRowCol.equals("")){
            //il re non è stato trovato nella scacchiera (significa che è stato mangiato)
            if(player=="BLACK") return 1;
            else return -1;
        }

        if((player == "WHITE" && numBlackOld > blackNum) || (player == "BLACK" && numWhiteOld > whiteNum)){
            return 2;
        }

        return -2;
    }

    private boolean isNearCamp(int row, int column){
        if(isBlackCamp(row+1,column) || isBlackCamp(row-1,column) || isBlackCamp(row,column+1) || isBlackCamp(row,column-1))
            return true;
        else
            return false;
    }
    private boolean isNearThrone(int row, int column){
        if(this.getState().getBoard()[row+1][column] == State.Pawn.THRONE || this.getState().getBoard()[row-1][column] == State.Pawn.THRONE
                || this.getState().getBoard()[row][column+1] == State.Pawn.THRONE || this.getState().getBoard()[row][column-1] == State.Pawn.THRONE)
            return true;
        else
            return false;
    }


    private int moveUp(int row, int column){
        int finalRow=0;
        if(row==0 || row==8)
            return row;
        for (  finalRow = row-1; finalRow > 0; finalRow--)
            if (!(this.getState().getBoard()[finalRow][column] == State.Pawn.EMPTY) && !isBlackCamp(finalRow,column))
                break;
        return finalRow+1;
    }

    private int moveLeft(int row, int column){
        int finalColumn=0;
        if(column==0 || column==8)
            return column;
        for (  finalColumn = column-1; finalColumn>0; finalColumn--)
            if (!(this.getState().getBoard()[row][finalColumn] == State.Pawn.EMPTY) && !isBlackCamp(row,finalColumn))
                break;
        return finalColumn+1;
    }

    private int moveRight(int row, int column){
        int finalColumn=0;
        if(column==0 || column==8)
            return column;
        for (  finalColumn = column+1; finalColumn < 9; finalColumn++)
            if (!(this.getState().getBoard()[row][finalColumn] == State.Pawn.EMPTY) && !isBlackCamp(row,finalColumn))
                break;
        return finalColumn-1;
    }

    private int moveDown(int row, int column){
        int finalRow=0;
        if(row==0 || row==8)
            return row;
        for (  finalRow = row+1; finalRow <9; finalRow++)
            if (!(this.getState().getBoard()[finalRow][column] == State.Pawn.EMPTY) && !isBlackCamp(finalRow,column))
                break;
        return finalRow-1;
    }


    public double getUtility(String player, int esitoPartita, int numBianche, int numNere) {
        double value=0;
        int mosse=0;
        int bianchi=0;
        int neri=0;
        boolean onTheThrone=false;
        int kingAccerchiato=0;
        int kingProtetto=0;
        boolean nearCamp=false;
        boolean nearThrone=false;
        boolean vantaggioMangiate=false;
        boolean svantaggioPedine=false;
        boolean kingMangiabile=false;
        boolean pedinaMangiabile= false;
        boolean pedinaMangiata= false;
        if(player.equals("WHITE")) {
            //WHITE
            if(esitoPartita != 2 && esitoPartita !=-2)
                return esitoPartita;
            for (int i = 0; i < 9; i++)
                for (int j = 0; j < 9; j++) {
                    if (this.getState().getBoard()[i][j] == State.Pawn.KING) {
                        //RE SUL TRONO
                        if(this.getState().getBoard()[i][j] == State.Pawn.THRONE)
                            onTheThrone=true;
                        //KING VINCE IN QUANTE MOSSE
                        if(this.moveUp(i,j)==0 || this.moveDown(i,j)==8 || this.moveLeft(i,j)==0 || this.moveRight(i,j)==8)
                            mosse=1;
                        //KING PROTETTO
                        if(this.getState().getBoard()[i+1][j] == State.Pawn.WHITE || this.getState().getBoard()[i+1][j] == State.Pawn.THRONE)
                            kingProtetto++;
                        if(this.getState().getBoard()[i-1][j] == State.Pawn.WHITE|| this.getState().getBoard()[i+1][j] == State.Pawn.THRONE)
                            kingProtetto++;
                        if(this.getState().getBoard()[i][j+1] == State.Pawn.WHITE|| this.getState().getBoard()[i+1][j] == State.Pawn.THRONE)
                            kingProtetto++;
                        if(this.getState().getBoard()[i][j-1] == State.Pawn.WHITE|| this.getState().getBoard()[i+1][j] == State.Pawn.THRONE)
                            kingProtetto++;
                        //KING MANGIABILE
                        if(this.getState().getBoard()[i+1][j] == State.Pawn.BLACK || this.getState().getBoard()[i-1][j] == State.Pawn.BLACK
                                || this.getState().getBoard()[i][j+1] == State.Pawn.BLACK || this.getState().getBoard()[i][j-1] == State.Pawn.BLACK || isNearCamp(i, j))
                            kingMangiabile=true;
                    }
                    //PEDINE MANGIATE BIANCHE O NERE
                    if(this.getState().getBoard()[i][j] == State.Pawn.WHITE)
                        bianchi++;
                    if(this.getState().getBoard()[i][j] == State.Pawn.BLACK)
                        neri++;
                    //PEDINE MANGIATE O MANGIABILI
                    if(this.getState().getBoard()[i][j] == State.Pawn.WHITE){
                        if(i!=8) {
                            if (this.getState().getBoard()[i + 1][j] == State.Pawn.BLACK || isBlackCamp(i + 1, j) || this.getState().getBoard()[i + 1][j] == State.Pawn.THRONE) {
                                if(i!=0) {
                                    if (this.getState().getBoard()[this.moveDown(i - 1, j)][j] == State.Pawn.BLACK || this.getState().getBoard()[i - 1][this.moveRight(i - 1, j)] == State.Pawn.BLACK
                                            || this.getState().getBoard()[i - 1][this.moveLeft(i - 1, j)] == State.Pawn.BLACK)
                                        pedinaMangiabile = true;
                                }
                                if(i!=7) {
                                    if (this.getState().getBoard()[this.moveUp(i + 2, j)][j] == State.Pawn.WHITE || this.getState().getBoard()[i + 2][this.moveRight(i + 2, j)] == State.Pawn.WHITE
                                            || this.getState().getBoard()[i + 2][this.moveLeft(i + 2, j)] == State.Pawn.WHITE)
                                        pedinaMangiata = true;
                                }
                            }
                        }
                        if(i!=0) {
                            if (this.getState().getBoard()[i - 1][j] == State.Pawn.BLACK || isBlackCamp(i - 1, j) || this.getState().getBoard()[i - 1][j] == State.Pawn.THRONE) {
                                if(i!=8) {
                                    if (this.getState().getBoard()[this.moveUp(i + 1, j)][j] == State.Pawn.BLACK || this.getState().getBoard()[i + 1][this.moveRight(i + 1, j)] == State.Pawn.BLACK
                                            || this.getState().getBoard()[i + 1][this.moveLeft(i + 1, j)] == State.Pawn.BLACK)
                                        pedinaMangiabile = true;
                                }
                                if(i!=1) {
                                    if (this.getState().getBoard()[this.moveDown(i - 2, j)][j] == State.Pawn.WHITE || this.getState().getBoard()[i - 2][this.moveRight(i - 2, j)] == State.Pawn.WHITE
                                            || this.getState().getBoard()[i - 2][this.moveLeft(i - 2, j)] == State.Pawn.WHITE)
                                        pedinaMangiata = true;
                                }
                            }
                        }
                        if(j!=0) {
                            if (this.getState().getBoard()[i][j - 1] == State.Pawn.BLACK || isBlackCamp(i, j - 1) || this.getState().getBoard()[i][j - 1] == State.Pawn.THRONE) {
                                if(j!=8) {
                                    if (this.getState().getBoard()[this.moveUp(i, j + 1)][j + 1] == State.Pawn.BLACK || this.getState().getBoard()[i][this.moveRight(i, j + 1)] == State.Pawn.BLACK
                                            || this.getState().getBoard()[this.moveDown(i, j + 1)][j + 1] == State.Pawn.BLACK)
                                        pedinaMangiabile = true;
                                }
                                if(j!=1) {
                                    if (this.getState().getBoard()[this.moveUp(i, j - 2)][j - 2] == State.Pawn.WHITE || this.getState().getBoard()[i][this.moveLeft(i, j - 2)] == State.Pawn.WHITE
                                            || this.getState().getBoard()[this.moveDown(i, j - 2)][j - 2] == State.Pawn.WHITE)
                                        pedinaMangiata = true;
                                }
                            }
                        }
                        if(j!=8) {
                            if (this.getState().getBoard()[i][j + 1] == State.Pawn.BLACK || isBlackCamp(i, j + 1) || this.getState().getBoard()[i][j + 1] == State.Pawn.THRONE) {
                                if(j!=0) {
                                    if (this.getState().getBoard()[this.moveUp(i, j - 1)][j - 1] == State.Pawn.BLACK || this.getState().getBoard()[i][this.moveLeft(i, j - 1)] == State.Pawn.BLACK
                                            || this.getState().getBoard()[this.moveDown(i, j - 1)][j - 1] == State.Pawn.BLACK)
                                        pedinaMangiabile = true;
                                }
                                if(j!=7) {
                                    if (this.getState().getBoard()[this.moveUp(i, j + 2)][j + 2] == State.Pawn.WHITE || this.getState().getBoard()[i][this.moveRight(i, j + 2)] == State.Pawn.WHITE
                                            || this.getState().getBoard()[this.moveDown(i, j + 2)][j + 2] == State.Pawn.WHITE)
                                        pedinaMangiata = true;
                                }
                            }
                        }
                    }
                }
            //CHI HA MANGIATO PIU PEDINE
            if(numBianche-bianchi>numNere-neri+1)
                svantaggioPedine=true;
            if(numNere-neri>=numBianche-bianchi+1)
                vantaggioMangiate=true;
            if(kingProtetto==1)
                value=value+0.05;
            if(kingProtetto==2)
                value=value+0.15;
            if(kingProtetto==3)
                value=value+0.0;
            if(kingProtetto==4)
                value=value+0.0;
            if(kingMangiabile)
                value=value-0.5;
            if(mosse==1)
                value=value+0.5;
            if(onTheThrone)
                value=value+0.1;
            if(vantaggioMangiate)
                value=value+0.4;
            if(svantaggioPedine)
                value=value-0.3;
            if(pedinaMangiabile)
                value=value-0.1;
            if(pedinaMangiata)
                value=value+0.1;

        }else{
            //BLACK
            if(esitoPartita != 2 && esitoPartita !=-2)
                return esitoPartita;
            for (int i = 0; i < 9; i++)
                for (int j = 0; j < 9; j++) {
                    if (this.getState().getBoard()[i][j] == State.Pawn.KING) {
                        //RE SUL TRONO
                        if (this.getState().getBoard()[i][j] == State.Pawn.THRONE)
                            onTheThrone = true;
                        //KING VINCE IN QUANTE MOSSE
                        if (this.moveUp(i, j) == 0 || this.moveDown(i, j) == 8 || this.moveLeft(i, j) == 0 || this.moveRight(i, j) == 8)
                            mosse = 1;
                        //KING ACCERCHIATO
                        if (this.getState().getBoard()[i + 1][j] == State.Pawn.BLACK || isBlackCamp(i + 1, j) || this.getState().getBoard()[i + 1][j] == State.Pawn.THRONE)
                            kingAccerchiato = kingAccerchiato++;
                        if (this.getState().getBoard()[i - 1][j] == State.Pawn.BLACK || isBlackCamp(i - 1, j) || this.getState().getBoard()[i - 1][j] == State.Pawn.THRONE)
                            kingAccerchiato = kingAccerchiato++;
                        if (this.getState().getBoard()[i][j + 1] == State.Pawn.BLACK || isBlackCamp(i, j + 1) || this.getState().getBoard()[i][j + 1] == State.Pawn.THRONE)
                            kingAccerchiato = kingAccerchiato++;
                        if (this.getState().getBoard()[i][j - 1] == State.Pawn.BLACK || isBlackCamp(i, j - 1) || this.getState().getBoard()[i][j - 1] == State.Pawn.THRONE)
                            kingAccerchiato = kingAccerchiato++;
                        //KING MANGIABILE
                        if (isNearCamp(i, j)) {
                            nearCamp = true;
                            if (isBlackCamp(i + 1, j))
                                if (this.getState().getBoard()[this.moveDown(i - 1, j)][j] == State.Pawn.BLACK || this.getState().getBoard()[i - 1][this.moveRight(i - 1, j)] == State.Pawn.BLACK
                                        || this.getState().getBoard()[i - 1][this.moveLeft(i - 1, j)] == State.Pawn.BLACK)
                                    kingMangiabile = true;
                            if (isBlackCamp(i - 1, j))
                                if (this.getState().getBoard()[this.moveUp(i + 1, j)][j] == State.Pawn.BLACK || this.getState().getBoard()[i + 1][this.moveRight(i + 1, j)] == State.Pawn.BLACK
                                        || this.getState().getBoard()[i + 1][this.moveLeft(i + 1, j)] == State.Pawn.BLACK)
                                    kingMangiabile = true;
                            if (isBlackCamp(i, j + 1))
                                if (this.getState().getBoard()[this.moveUp(i, j - 1)][j - 1] == State.Pawn.BLACK || this.getState().getBoard()[i][this.moveLeft(i, j - 1)] == State.Pawn.BLACK
                                        || this.getState().getBoard()[this.moveDown(i, j - 1)][j - 1] == State.Pawn.BLACK)
                                    kingMangiabile = true;
                            if (isBlackCamp(i, j - 1))
                                if (this.getState().getBoard()[this.moveUp(i, j + 1)][j + 1] == State.Pawn.BLACK || this.getState().getBoard()[i][this.moveRight(i, j + 1)] == State.Pawn.BLACK
                                        || this.getState().getBoard()[this.moveDown(i, j + 1)][j + 1] == State.Pawn.BLACK)
                                    kingMangiabile = true;
                        }
                        if (isNearThrone(i, j)) {
                            nearThrone = true;
                            if (this.getState().getBoard()[i + 1][j] == State.Pawn.EMPTY && kingAccerchiato == 3)
                                if (this.getState().getBoard()[this.moveUp(i + 1, j)][j] == State.Pawn.BLACK || this.getState().getBoard()[i + 1][this.moveRight(i + 1, j)] == State.Pawn.BLACK
                                        || this.getState().getBoard()[i + 1][this.moveLeft(i + 1, j)] == State.Pawn.BLACK)
                                    kingMangiabile = true;
                            if (this.getState().getBoard()[i - 1][j] == State.Pawn.EMPTY && kingAccerchiato == 3)
                                if (this.getState().getBoard()[this.moveUp(i - 1, j)][j] == State.Pawn.BLACK || this.getState().getBoard()[i - 1][this.moveRight(i - 1, j)] == State.Pawn.BLACK
                                        || this.getState().getBoard()[i - 1][this.moveLeft(i - 1, j)] == State.Pawn.BLACK)
                                    kingMangiabile = true;
                            if (this.getState().getBoard()[i][j + 1] == State.Pawn.EMPTY && kingAccerchiato == 3)
                                if (this.getState().getBoard()[this.moveUp(i, j + 1)][j + 1] == State.Pawn.BLACK || this.getState().getBoard()[i][this.moveRight(i, j + 1)] == State.Pawn.BLACK
                                        || this.getState().getBoard()[this.moveDown(i, j + 1)][j + 1] == State.Pawn.BLACK)
                                    kingMangiabile = true;
                            if (this.getState().getBoard()[i][j - 1] == State.Pawn.EMPTY && kingAccerchiato == 3)
                                if (this.getState().getBoard()[this.moveUp(i, j - 1)][j - 1] == State.Pawn.BLACK || this.getState().getBoard()[i][this.moveLeft(i, j - 1)] == State.Pawn.BLACK
                                        || this.getState().getBoard()[this.moveDown(i, j - 1)][j - 1] == State.Pawn.BLACK)
                                    kingMangiabile = true;
                        }

                        if (onTheThrone && kingAccerchiato == 3) {
                            if (this.getState().getBoard()[i + 1][j] == State.Pawn.EMPTY && kingAccerchiato == 3)
                                if (this.getState().getBoard()[this.moveUp(i + 1, j)][j] == State.Pawn.BLACK || this.getState().getBoard()[i + 1][this.moveRight(i + 1, j)] == State.Pawn.BLACK
                                        || this.getState().getBoard()[i + 1][this.moveLeft(i + 1, j)] == State.Pawn.BLACK)
                                    kingMangiabile = true;
                            if (this.getState().getBoard()[i - 1][j] == State.Pawn.EMPTY && kingAccerchiato == 3)
                                if (this.getState().getBoard()[this.moveUp(i - 1, j)][j] == State.Pawn.BLACK || this.getState().getBoard()[i - 1][this.moveRight(i - 1, j)] == State.Pawn.BLACK
                                        || this.getState().getBoard()[i - 1][this.moveLeft(i - 1, j)] == State.Pawn.BLACK)
                                    kingMangiabile = true;
                            if (this.getState().getBoard()[i][j + 1] == State.Pawn.EMPTY && kingAccerchiato == 3)
                                if (this.getState().getBoard()[this.moveUp(i, j + 1)][j + 1] == State.Pawn.BLACK || this.getState().getBoard()[i][this.moveRight(i, j + 1)] == State.Pawn.BLACK
                                        || this.getState().getBoard()[this.moveDown(i, j + 1)][j + 1] == State.Pawn.BLACK)
                                    kingMangiabile = true;
                            if (this.getState().getBoard()[i][j - 1] == State.Pawn.EMPTY && kingAccerchiato == 3)
                                if (this.getState().getBoard()[this.moveUp(i, j - 1)][j - 1] == State.Pawn.BLACK || this.getState().getBoard()[i][this.moveLeft(i, j - 1)] == State.Pawn.BLACK
                                        || this.getState().getBoard()[this.moveDown(i, j - 1)][j - 1] == State.Pawn.BLACK)
                                    kingMangiabile = true;
                        }
                        if (this.getState().getBoard()[i + 1][j] == State.Pawn.BLACK && !isNearThrone(i, j) && !isNearCamp(i, j) && !onTheThrone) {
                            if (this.getState().getBoard()[this.moveDown(i - 1, j)][j] == State.Pawn.BLACK || this.getState().getBoard()[i - 1][this.moveRight(i - 1, j)] == State.Pawn.BLACK
                                    || this.getState().getBoard()[i - 1][this.moveLeft(i - 1, j)] == State.Pawn.BLACK)
                                kingMangiabile = true;
                        }
                        if (this.getState().getBoard()[i - 1][j] == State.Pawn.BLACK && !isNearThrone(i, j) && !isNearCamp(i, j) && !onTheThrone) {
                            if (this.getState().getBoard()[this.moveUp(i + 1, j)][j] == State.Pawn.BLACK || this.getState().getBoard()[i + 1][this.moveRight(i + 1, j)] == State.Pawn.BLACK
                                    || this.getState().getBoard()[i + 1][this.moveLeft(i + 1, j)] == State.Pawn.BLACK)
                                kingMangiabile = true;
                        }
                        if (this.getState().getBoard()[i][j - 1] == State.Pawn.BLACK && !isNearThrone(i, j) && !isNearCamp(i, j) && !onTheThrone) {
                            if (this.getState().getBoard()[this.moveUp(i, j + 1)][j + 1] == State.Pawn.BLACK || this.getState().getBoard()[i][this.moveRight(i, j + 1)] == State.Pawn.BLACK
                                    || this.getState().getBoard()[this.moveDown(i, j + 1)][j + 1] == State.Pawn.BLACK)
                                kingMangiabile = true;
                        }
                        if (this.getState().getBoard()[i][j + 1] == State.Pawn.BLACK && !isNearThrone(i, j) && !isNearCamp(i, j) && !onTheThrone) {
                            if (this.getState().getBoard()[this.moveUp(i, j - 1)][j - 1] == State.Pawn.BLACK || this.getState().getBoard()[i][this.moveLeft(i, j - 1)] == State.Pawn.BLACK
                                    || this.getState().getBoard()[this.moveDown(i, j - 1)][j - 1] == State.Pawn.BLACK)
                                kingMangiabile = true;
                        }
                    }
                    //PEDINE MANGIATE BIANCHE O NERE
                    if(this.getState().getBoard()[i][j] == State.Pawn.WHITE)
                        bianchi++;
                    if(this.getState().getBoard()[i][j] == State.Pawn.BLACK)
                        neri++;
                    //PEDINE MANGIATE O MANGIABILI
                    if(this.getState().getBoard()[i][j] == State.Pawn.WHITE) {
                        if (i != 8) {
                            if (this.getState().getBoard()[i + 1][j] == State.Pawn.BLACK || isBlackCamp(i + 1, j) || this.getState().getBoard()[i + 1][j] == State.Pawn.THRONE) {
                                if (i != 0) {
                                    if (this.getState().getBoard()[this.moveDown(i - 1, j)][j] == State.Pawn.BLACK || this.getState().getBoard()[i - 1][this.moveRight(i - 1, j)] == State.Pawn.BLACK
                                            || this.getState().getBoard()[i - 1][this.moveLeft(i - 1, j)] == State.Pawn.BLACK)
                                        pedinaMangiata = true;
                                }
                                if (i != 7) {
                                    if (this.getState().getBoard()[this.moveUp(i + 2, j)][j] == State.Pawn.WHITE || this.getState().getBoard()[i + 2][this.moveRight(i + 2, j)] == State.Pawn.WHITE
                                            || this.getState().getBoard()[i + 2][this.moveLeft(i + 2, j)] == State.Pawn.WHITE)
                                        pedinaMangiabile = true;
                                }
                            }
                        }
                        if (i != 0) {
                            if (this.getState().getBoard()[i - 1][j] == State.Pawn.BLACK || isBlackCamp(i - 1, j) || this.getState().getBoard()[i - 1][j] == State.Pawn.THRONE) {
                                if (i != 8) {
                                    if (this.getState().getBoard()[this.moveUp(i + 1, j)][j] == State.Pawn.BLACK || this.getState().getBoard()[i + 1][this.moveRight(i + 1, j)] == State.Pawn.BLACK
                                            || this.getState().getBoard()[i + 1][this.moveLeft(i + 1, j)] == State.Pawn.BLACK)
                                        pedinaMangiata = true;
                                }
                                if (i != 1) {
                                    if (this.getState().getBoard()[this.moveDown(i - 2, j)][j] == State.Pawn.WHITE || this.getState().getBoard()[i - 2][this.moveRight(i - 2, j)] == State.Pawn.WHITE
                                            || this.getState().getBoard()[i - 2][this.moveLeft(i - 2, j)] == State.Pawn.WHITE)
                                        pedinaMangiabile = true;
                                }
                            }
                        }
                        if (j != 0) {
                            if (this.getState().getBoard()[i][j - 1] == State.Pawn.BLACK || isBlackCamp(i, j - 1) || this.getState().getBoard()[i][j - 1] == State.Pawn.THRONE) {
                                if (j != 8) {
                                    if (this.getState().getBoard()[this.moveUp(i, j + 1)][j + 1] == State.Pawn.BLACK || this.getState().getBoard()[i][this.moveRight(i, j + 1)] == State.Pawn.BLACK
                                            || this.getState().getBoard()[this.moveDown(i, j + 1)][j + 1] == State.Pawn.BLACK)
                                        pedinaMangiata = true;
                                }
                                if (j != 1) {
                                    if (this.getState().getBoard()[this.moveUp(i, j - 2)][j - 2] == State.Pawn.WHITE || this.getState().getBoard()[i][this.moveLeft(i, j - 2)] == State.Pawn.WHITE
                                            || this.getState().getBoard()[this.moveDown(i, j - 2)][j - 2] == State.Pawn.WHITE)
                                        pedinaMangiabile = true;
                                }
                            }
                        }
                        if (j != 8) {
                            if (this.getState().getBoard()[i][j + 1] == State.Pawn.BLACK || isBlackCamp(i, j + 1) || this.getState().getBoard()[i][j + 1] == State.Pawn.THRONE) {
                                if (j != 0) {
                                    if (this.getState().getBoard()[this.moveUp(i, j - 1)][j - 1] == State.Pawn.BLACK || this.getState().getBoard()[i][this.moveLeft(i, j - 1)] == State.Pawn.BLACK
                                            || this.getState().getBoard()[this.moveDown(i, j - 1)][j - 1] == State.Pawn.BLACK)
                                        pedinaMangiata = true;
                                }
                                if (j != 7) {
                                    if (this.getState().getBoard()[this.moveUp(i, j + 2)][j + 2] == State.Pawn.WHITE || this.getState().getBoard()[i][this.moveRight(i, j + 2)] == State.Pawn.WHITE
                                            || this.getState().getBoard()[this.moveDown(i, j + 2)][j + 2] == State.Pawn.WHITE)
                                        pedinaMangiabile = true;
                                }
                            }
                        }
                    }
                }
            //CHI HA MANGIATO PIU PEDINE
            if(numNere-neri>numBianche-bianchi+1)
                svantaggioPedine=true;
            if(numBianche-bianchi>=numNere-neri+1)
                vantaggioMangiate=true;
            //EURISTICA
            if(onTheThrone && kingAccerchiato==1)
                value=value+0.2;
            if(onTheThrone && kingAccerchiato==2)
                value=value+0.4;
            if(onTheThrone && kingAccerchiato==3)
                value=value+0.6;
            if(nearCamp)
                value=value+0.5;
            if(nearThrone && kingAccerchiato==1)
                value=value+0.2;
            if(nearThrone && kingAccerchiato==2)
                value=value+0.4;
            if(nearThrone && kingAccerchiato==3)
                value=value+0.6;
            if(!nearThrone && !nearCamp && !onTheThrone && kingAccerchiato== 1)
                value=value+0.5;
            if(kingMangiabile)
                value=value+0.2;
            if(mosse==1)
                value=value-0.6;
            if(onTheThrone)
                value=value-0.2;
            if(vantaggioMangiate)
                value=value+0.6;
            if(svantaggioPedine)
                value=value-0.2;
            if(pedinaMangiabile)
                value=value-0.15;
            if(pedinaMangiata)
                value=value+0.1;
        }
        return value;
    }
}
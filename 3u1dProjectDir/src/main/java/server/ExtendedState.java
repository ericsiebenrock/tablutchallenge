package server;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/*
estensioen della classe state per compatibilita con algoritmo min-max
 */
public class ExtendedState{

    private StateTablut state;

    private double valoreProvvisorio;

    public void setState(State state){
        this.state=(StateTablut)state;
    }

    public StateTablut getState() {
        return state;
    }

    public Action getAction(ExtendedState oldState){
        Action a=null;
        String from= "";
        String to= "";
        for (int i = 0; i < oldState.getState().getBoard().length; i++)
            for (int j = 0; j < this.getState().getBoard().length; j++)
                if (!this.getState().getBoard()[i][j].equals(oldState.getState().getBoard()[i][j])) {
                    if((oldState.getState().getBoard()[i][j]== State.Pawn.BLACK || oldState.getState().getBoard()[i][j]== State.Pawn.WHITE || oldState.getState().getBoard()[i][j]== State.Pawn.KING)
                    && this.getState().getBoard()[i][j]== State.Pawn.EMPTY)
                        from=from+i+j;
                    if((this.getState().getBoard()[i][j]== State.Pawn.BLACK || this.getState().getBoard()[i][j]== State.Pawn.WHITE || this.getState().getBoard()[i][j]== State.Pawn.KING)
                            && oldState.getState().getBoard()[i][j]== State.Pawn.EMPTY)
                        to=to+i+j;
                }
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
        List<ExtendedState> actions = new LinkedList<>();

        //ciclo sulle righe
        for (int row = 0; row < this.state.board.length; row++){
            //ciclo sulle colonne della riga
            for (int column = 0; column < this.state.board.length; column++){

                //turno del BIANCO
                if(state.turn==State.Turn.WHITE){
                    //ricerca di una pedina bianca da muovere
                    if(this.state.board[row][column]==State.Pawn.WHITE) {
                        rowMovements(actions, row, column, State.Pawn.WHITE);
                        columnMovements(actions, row, column, State.Pawn.WHITE);
                    }
                }

                //turno del BIANCo
                if(state.turn==State.Turn.BLACK){
                    //ricerca di una pedina bianca da muovere
                    if(this.state.board[row][column]==State.Pawn.BLACK) {
                        rowMovements(actions, row, column, State.Pawn.BLACK);
                        columnMovements(actions, row, column, State.Pawn.BLACK);
                    }
                }
            }
        }

        return actions;
    }

    private void columnMovements(List<ExtendedState> actions, int row, int column, State.Pawn movingPiece) {
        boolean legitMove=false;
        //ricerca di caselle vuote nella stessa colonna (controllando anche che non si tratti della stessa casella o del trono)
        for(int newRow=0; newRow<this.state.board.length && newRow!=row; newRow++){

            if(this.state.board[newRow][column]==State.Pawn.EMPTY && this.state.board[newRow][column] != State.Pawn.THRONE){
                if(movingPiece==State.Pawn.WHITE){
                    //controllo che non entri negli accampamenti
                    if(isBlackCamp(newRow,column)) continue;
                }
                if(movingPiece==State.Pawn.BLACK){
                    //controllo che non entri negli altri accampamenti. Se è nell accampamento (isBlackCamp(row, column)) si può muovere in esso.
                    //la distanza massima di caselle dello stesso accampamento infatti è 2
                    if(isBlackCamp(row, column) && isBlackCamp(newRow,column) && Math.abs(newRow-row)>2) continue;//prossimo ciclo for
                }
                //riga prima (spostamento verso alto)
                if(newRow<row){
                    //controllo che le caselle in mezzo non siano occupate o non sia trono o accampamento
                    for (int i = (newRow + 1); i < row; i++) {
                        if (this.state.board[i][column] != State.Pawn.EMPTY || this.state.board[i][column]==State.Pawn.THRONE || isBlackCamp(i,column)) {
                            legitMove = false; //casella in mezzo non vuota oppure trono oppure accampamento
                            break;
                        } else legitMove = true;
                    }
                }
                //riga dopo (spostamento verso basso)
                else{
                    for(int i=(newRow-1); i>row; i--) {
                        if (this.state.board[i][column] != State.Pawn.EMPTY || this.state.board[row][i]==State.Pawn.THRONE || isBlackCamp(row,i)) {
                            legitMove = false; //casella in mezzo non vuota
                            break;
                        } else legitMove = true;
                    }
                }
                if(legitMove){
                    State.Pawn newBoard[][]=this.state.board.clone();
                    newBoard[row][column]=State.Pawn.EMPTY;
                    newBoard[newRow][column]=movingPiece;
                    StateTablut newWrappedState= new StateTablut();
                    newWrappedState.setBoard(newBoard);
                    if(state.turn==State.Turn.WHITE) newWrappedState.setTurn(State.Turn.BLACK);
                    ExtendedState newState= new ExtendedState();
                    newState.setState(newWrappedState);
                    actions.add(newState);
                }
            }
        }
    }

    private void rowMovements(List<ExtendedState> actions, int row, int column, State.Pawn movingPiece){
        boolean legitMove=false;
        //ricerca di caselle vuote nella stessa riga (controllando anche che non si tratti della stessa casella o del trono)
        for(int newColumn=0; newColumn<this.state.board.length && newColumn!=column; newColumn++){
            if(this.state.board[row][newColumn]==State.Pawn.EMPTY && this.state.board[row][newColumn] != State.Pawn.THRONE){
                if(movingPiece==State.Pawn.WHITE){
                    //controllo che non entri negli accampamenti
                    if(isBlackCamp(row,newColumn)) continue;//prossimo ciclo for
                }
                if(movingPiece==State.Pawn.BLACK){
                    //controllo che non entri negli altri accampamenti. Se è nell accampamento (isBlackCamp(row, column)) si può muovere in esso.
                    //la distanza massima di caselle dello stesso accampamento infatti è 2
                    if(isBlackCamp(row, column) && isBlackCamp(row,newColumn) && Math.abs(newColumn-column)>2) continue;
                }
                //colonna prima (spostamento a sinistra)
                if(newColumn<column){
                    //controllo che le caselle in mezzo non siano occupate o non sia trono o accampamento
                    for (int i = (newColumn + 1); i < column; i++) {
                        if (this.state.board[row][i] != State.Pawn.EMPTY || this.state.board[row][i]==State.Pawn.THRONE || isBlackCamp(row,i)) {
                            legitMove = false; //casella in mezzo non vuota oppure trono oppure accampamento
                            break;
                        } else legitMove = true;
                    }
                }
                //colonna dopo (spostamento a destra)
                else{
                    for(int i=(newColumn-1); i>column; i--) {
                        if (this.state.board[row][i] != State.Pawn.EMPTY || this.state.board[row][i]==State.Pawn.THRONE || isBlackCamp(row,i)) {
                            legitMove = false; //casella in mezzo non vuota
                            break;
                        } else legitMove = true;
                    }
                }
                if(legitMove){
                    State.Pawn newBoard[][]=this.state.board.clone();
                    newBoard[row][column]=State.Pawn.EMPTY;
                    newBoard[row][newColumn]=movingPiece;
                    StateTablut newWrappedState= new StateTablut();
                    newWrappedState.setBoard(newBoard);
                    if(state.turn==State.Turn.WHITE) newWrappedState.setTurn(State.Turn.BLACK);
                    else newWrappedState.setTurn(State.Turn.WHITE);
                    ExtendedState newState= new ExtendedState();
                    newState.setState(newWrappedState);
                    actions.add(newState);
                }
            }
        }
    }
    
    private boolean isBlackCamp(int row, int column){
        boolean res=false;
        switch (row){
            case 0:
                if((column==3 || column==4 || column==5)) res=true;
                break;
            case 1:
                if(column==4) res=true;
                break;
            case 3:
                if(column==0 || column==8)res=true;
                break;
            case 4:
                if(column==0 || column==1 || column==7 || column==8) res=true;
                break;
            case 5:
                if(column==0 || column==8)res=true;
                break;
            case 7:
                if(column==4)res=true;
                break;
            case 8:
                if((column==3 || column==4 || column==5)) res=true;
                break;

        }
        return res;
    }

    public double getValoreProvvisorio() {
        return valoreProvvisorio;
    }

    public void setValoreProvvisorio(double valoreProvvisorio) {
        this.valoreProvvisorio = valoreProvvisorio;
    }


    //restituisce: -1 sconfitta, 0 pareggio, 1 vittoria, 2 stato terminale comunque (ci fermiamo TODO), -2 non è terminale
    public int isTerminal(String player) {
        int whiteNum=0, blackNum=0, kingRow=-1, kingCol=-1;
        String kingRowCol="";

        for (int row = 0; row < this.state.board.length; row++) {
            //ciclo sulle colonne della riga
            for (int column = 0; column < this.state.board.length; column++) {
                if(state.board[row][column]==State.Pawn.WHITE) whiteNum++;
                if(state.board[row][column]==State.Pawn.KING){
                    whiteNum++;
                    kingRow=row; kingCol=column;
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
        if(kingRowCol.equals("44") && state.board[3][4]==State.Pawn.BLACK && state.board[5][4]==State.Pawn.BLACK && state.board[4][3]==State.Pawn.BLACK && state.board[4][5]==State.Pawn.BLACK){
            //re mangiato nel trono
            if(player=="BLACK") return 1;
            else return -1;
        }
        if(kingRowCol.equals("34") && state.board[2][4]==State.Pawn.BLACK && state.board[3][3]==State.Pawn.BLACK && state.board[3][5]==State.Pawn.BLACK) {
            //re mangiato da lato superiore trono
            if(player=="BLACK") return 1;
            else return -1;
        }
        if(kingRowCol.equals("43") && state.board[3][3]==State.Pawn.BLACK && state.board[4][2]==State.Pawn.BLACK && state.board[5][3]==State.Pawn.BLACK){
            //re mangiato da lato sinistro trono
            if(player=="BLACK") return 1;
            else return -1;
        }
        if(kingRowCol.equals("54") && state.board[5][3]==State.Pawn.BLACK && state.board[6][4]==State.Pawn.BLACK && state.board[5][5]==State.Pawn.BLACK){
            //re mangiato da lato inferiore trono
            if(player=="BLACK") return 1;
            else return -1;
        }
        if(kingRowCol.equals("45") && state.board[3][5]==State.Pawn.BLACK && state.board[4][5]==State.Pawn.BLACK && state.board[5][5]==State.Pawn.BLACK){
            //re mangiato da lato destro trono
            if(player=="BLACK") return 1;
            else return -1;
        }
        if((state.board[kingRow-1][kingCol]==State.Pawn.BLACK || isBlackCamp(kingRow-1,kingCol)) && (state.board[kingRow+1][kingCol]==State.Pawn.BLACK || isBlackCamp(kingRow+1,kingCol))){
            //re circondato in verticale
            if(player=="BLACK") return 1;
            else return -1;
        }
        if((state.board[kingRow][kingCol-1]==State.Pawn.BLACK || isBlackCamp(kingRow,kingCol-1)) && (state.board[kingRow][kingCol+1]==State.Pawn.BLACK || isBlackCamp(kingRow,kingCol+1))){
            //re circondato in orizzontale
            if(player=="BLACK") return 1;
            else return -1;
        }
        return -2;
    }

    public double getUtility() {
        //add some logic ritorna un double a partire dallo stato (euristica)
        return 0;
    }

}

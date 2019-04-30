package server;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/*
estensioen della classe state per compatibilita con algoritmo min-max
 */
public class ExtendedState{

    private StateTablut state;

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
        //generate actions
        return actions;
    }

    public boolean isTerminal() {
        if (this.getState().getTurn().equals(State.Turn.BLACKWIN) || this.getState().getTurn().equals(State.Turn.WHITEWIN) || this.getState().getTurn().equals(State.Turn.DRAW))
            return true;
        else
            return false;
    }

    public double getUtility() {
        //add some logic ritorna un double a partire dallo stato (euristica)
        return 0;
    }

}

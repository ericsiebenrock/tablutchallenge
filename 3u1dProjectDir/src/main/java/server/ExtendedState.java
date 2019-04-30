package server;

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
        Action a = null;
        // DA FARE
        return a;
    }

    public List<ExtendedState> getActions(){
        List<ExtendedState> actions = new LinkedList<>();
        //generate actions
        return actions;
    }

    public boolean isTerminal() {
        //add some logic
        return false;
    }

    public double getUtility() {
        //add some logic ritorna un double a partire dallo stato (euristica)
        return 0;
    }

}

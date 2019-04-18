package server;

import java.util.LinkedList;
import java.util.List;

/*
estensioen della classe state per compatibilita con algoritmo min-max
 */
public class ExtendedState extends State {

    List<State> getActions(){
        List<State> actions = new LinkedList<>();
        //generate actions
        return actions;
    }

    boolean isTerminal() {
        //add some logic
        return false;
    }

    double getUtility() {
        //add some logic
        return 0;
    }

}

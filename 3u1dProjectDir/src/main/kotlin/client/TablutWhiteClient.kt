package client


//import javafx.application.Application.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import server.ExtendedState
import server.State
import server.StateTablut
import java.lang.Double.valueOf

class TablutWhiteClient(player : String, name : String) : TablutClient(player,name) {
    private lateinit var listaNodi: ArrayList<ExtendedState>;


    override fun run() = runBlocking {
        /*launch: lancia la coroutine nello stesso scope della coroutine che esegue run
        la quale aspetta la sua terminazione a causa di runBlocking
        Il dispatcher di default è adatto a coroutine con CPU-intensive tasks
        */
        launch(Dispatchers.Default) {
            //esecuzione del algoritmo min-max e scelta del nodo
            //var currentExtendedState = ExtendedState()
            var extendedOldState = ExtendedState()
            while (true) {
                super.read()
                var state = super.getCurrentState()
                extendedOldState.setState(state)
                var extendedNewState = minMaxDecision(extendedOldState)
                super.write(extendedNewState.getAction(extendedOldState))
            }
        }
    }


    companion object {

        fun minMaxDecision(extendedState: ExtendedState) : ExtendedState{
            return extendedState.getActions().stream().max(Comparator.comparing(::minValue)).get()
        }

        fun maxValue(state: ExtendedState) : Double {
            if(state.isTerminal()){
                return state.getUtility()
            }
            return state.getActions().stream().map(::minValue).max(Comparator.comparing(::valueOf)).get();
        }

        fun minValue(state: ExtendedState) : Double{
            if(state.isTerminal()){
                return state.getUtility()
            }
            return state.getActions().stream().map(::maxValue).min(Comparator.comparing(::valueOf)).get();
        }

    }
}
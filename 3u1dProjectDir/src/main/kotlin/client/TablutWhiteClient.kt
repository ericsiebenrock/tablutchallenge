package client


import javafx.application.Application.launch
import server.ExtendedState
import server.State

class TablutWhiteClient(player : String, name : String) : TablutClient(player,name) {
    private lateinit var listaNodi:ArrayList<ExtendedState>;


    override fun run() = runBlocking{
        /*launch: lancia la coroutine nello stesso scope della coroutine che esegue run
        la quale aspetta la sua terminazione a causa di runBlocking
        Il dispatcher di default è adatto a coroutine con CPU-intensive tasks
        */
        launch(Dispatchers.Default) {
            //esecuzione del algoritmo min-max e scelta del nodo
            this.minMaxDecision(currentState)
        }
    }

}

companion object {

    fun minMaxDecision(stato : ExtendedState){

    }

    fun maxValue(stato : ExtendedState){

    }

    fun minValue(stato : ExtendedState){

    }

}
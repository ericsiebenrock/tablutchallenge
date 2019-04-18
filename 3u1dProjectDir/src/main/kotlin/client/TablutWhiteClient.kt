package client


import javafx.application.Application.launch

class TablutWhiteClient(player: String, name: String) : TablutClient(player,name) {



    override fun run() = runBlocking{
        /*launch: lancia la coroutine nello stesso scope della coroutine che esegue run
        la quale aspetta la sua terminazione a causa di runBlocking
        Il dispatcher di default è adatto a coroutine con CPU-intensive tasks
        */
        launch(Dispatchers.Default) {
            //esecuzione del algoritmo min-max e scelta del nodo
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }




}
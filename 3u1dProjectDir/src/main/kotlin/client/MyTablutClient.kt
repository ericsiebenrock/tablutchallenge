package client


//import javafx.application.Application.launch
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import server.Action
import server.ExtendedState
import server.State
import server.StateTablut
import java.io.IOException
import java.lang.Double.valueOf
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import sun.misc.VM.getState



class MyTablutClient(player : String, name : String, game: Int) : TablutClient(player,name) {
    private lateinit var listaNodi: ArrayList<ExtendedState>
    private var termina = false
    private lateinit var extendedOldState : ExtendedState

    override fun run() = runBlocking {
        /*launch: lancia la coroutine nello stesso scope della coroutine che esegue run
        la quale aspetta la sua terminazione a causa di runBlocking
        Il dispatcher di default è adatto a coroutine con CPU-intensive tasks
        */
        try {
            declareName()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        listaNodi= ArrayList()
        extendedOldState = ExtendedState()
        extendedOldState.setState(StateTablut())
        extendedOldState.state.turn = State.Turn.WHITE
        println("Ashton Tablut game")
        println("You are player $player!")
        //val channel = Channel<Int>()
        /* PER PROVARE
        while(true){
            println("cominciato turno")
            termina=false
            launch{
                delay(60000)
                //channel.send(1)
                termina=true
            }
            while(true){
                if(termina)
                    break
            }
            println("terminato turno")
        }

         */

        while (true) {
            termina=false
            try {
				read()
			} catch (e: IOException) {
				e.printStackTrace()
				System.exit(1)
			}
            launch{
                delay(60000)
                //channel.send(1)
                termina=true
            }
            var state = getCurrentState()
            println(state.toString())
            extendedOldState.setState(state)
            if (player == State.Turn.WHITE) {
                // è il mio turno
                if (currentState.turn == State.Turn.WHITE) {
                    var extendedNewState = minMaxDecision2(extendedOldState)
                    var a = extendedNewState.getAction(extendedOldState)
                    println("Mossa scelta: $a")
                    try {
                        write(a)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        System.exit(1)
                    }
                }
                // è il turno dell'avversario
                else if (state.turn == State.Turn.BLACK) {
                    System.out.println("Waiting for your opponent move... ");
                }
                // ho vinto
                else if (state.turn == State.Turn.WHITEWIN) {
                    System.out.println("YOU WIN!")
                    System.exit(0)
                }
                // ho perso
                else if (state.turn == State.Turn.BLACKWIN) {
                    System.out.println("YOU LOSE!")
                    System.exit(0)
                }
                // pareggio
                else if (state.turn == State.Turn.DRAW) {
                    System.out.println("DRAW!")
                    System.exit(0)
                }

            } else {
                // è il mio turno
                if (currentState.turn == State.Turn.BLACK) {
                    var extendedNewState = minMaxDecision2(extendedOldState)
                    var a = extendedNewState.getAction(extendedOldState)
                    println("Mossa scelta: $a")
                    try {
                        write(a)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        System.exit(1)
                    }
                }
                // è il turno dell'avversario
                else if (state.turn == State.Turn.WHITE) {
                    System.out.println("Waiting for your opponent move... ");
                }
                // ho vinto
                else if (state.turn == State.Turn.BLACKWIN) {
                    System.out.println("YOU WIN!")
                    System.exit(0)
                }
                // ho perso
                else if (state.turn == State.Turn.WHITEWIN) {
                    System.out.println("YOU LOSE!")
                    System.exit(0)
                }
                // pareggio
                else if (state.turn == State.Turn.DRAW) {
                    System.out.println("DRAW!")
                    System.exit(0)
                }
            }
        }
    }

    fun minMaxDecision2(state: ExtendedState): Double{
        listaNodi.clear()
        var v:Double
        if(state.state.turn==player) {
            v=maxValue3(state)

        }
        else v=minValue3(state)
    }


        fun maxValue3(state: ExtendedState):Double{
            var terminale=state.isTerminal(player.toString())
            if(terminale != -2 || termina){   //isTerminal restituisce -> -2 = non terminale | -1 = terminale sconfitta | 0 = terminale pareggio | 1 = terminale vittoria | 2 = terminale
                var numWhite=0
                var numBlack=0
                for (i in 0 until 8)
                    for (j in 0 until 8) {
                        if (extendedOldState.state.board[i][j] === State.Pawn.WHITE)
                            numWhite++
                        if (extendedOldState.state.board[i][j] === State.Pawn.BLACK)
                            numBlack++
                    }
                val ev=state.getUtility(player.toString(), terminale, numWhite, numBlack)
                state.valoreProvvisorio=ev
                println("nodo terminale: ${state.state.boardString()}, valore: ${ev}")
                return ev
            }
            println("nodo max: ${state.state.boardString()}")
            v = Double.NEGATIVE_INFINITY
            state.getActions().forEach {
                v = max(v,minValue3(it))
                //listaNodi.remove(it)
            }
            state.valoreProvvisorio=v
            return v
        }

    fun minValue3(state: ExtendedState):Double{
        if(terminale != -2 || termina){   //isTerminal restituisce -> -2 = non terminale | -1 = terminale sconfitta | 0 = terminale pareggio | 1 = terminale vittoria | 2 = terminale
            var numWhite=0
            var numBlack=0
            for (i in 0 until 8)
                for (j in 0 until 8) {
                    if (extendedOldState.state.board[i][j] === State.Pawn.WHITE)
                        numWhite++
                    if (extendedOldState.state.board[i][j] === State.Pawn.BLACK)
                        numBlack++
                }
            val ev=state.getUtility(player.toString(), terminale, numWhite, numBlack)
            state.valoreProvvisorio=ev
            println("nodo terminale: ${state.state.boardString()}, valore: ${ev}")
            return ev
        }
        println("nodo min: ${state.state.boardString()}")
        v = Double.POSITIVE_INFINITY
        state.getActions().forEach {
            v = min(v,maxValue3(it))
            //listaNodi.remove(it)
        }
        state.valoreProvvisorio=v
        return v
    }

       /*
        fun minMaxDecision(state: ExtendedState) : ExtendedState {
            return extendedState.getActions().stream().max(Comparator.comparing(::minValue)).get()
        }

       fun maxValue(state: ExtendedState) : Double {
            if(state.isTerminal() || termina){
                return state.getUtility()
            }
            return state.getActions().stream().map(::minValue).max(Comparator.comparing(Double::toDouble)).get();
        }

        fun minValue(state: ExtendedState) : Double{
            if(state.isTerminal() || termina){
                return state.getUtility()
            }
            return state.getActions().stream().map(::maxValue).min(Comparator.comparing(Double::toDouble)).get();
        }*/

    companion object{
        fun main(args: Array<String>) {
            var gametype = 4
            var role = ""
            var name = "IA"

            if (args.isEmpty()) {
                println("You must specify which player you are (WHITE or BLACK)");
                System.exit(-1)
            } else {
                println(args[0])
                role = args[0]
            }
            if (args.size == 2) {
                println(args[1])
                gametype = args[1].toInt()
            }
            if (args.size == 3) {
                name = args[2]
            }
            println("Selected client: ${args[0]}")

            var client = MyTablutClient(role, name, gametype)
            client.run()
        }
    }

}

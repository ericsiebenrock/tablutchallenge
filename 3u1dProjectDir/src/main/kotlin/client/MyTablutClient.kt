package client

import kotlinx.coroutines.*
import server.ExtendedState
import server.State
import server.StateTablut
import java.io.IOException
import kotlin.math.max
import kotlin.math.min


class MyTablutClient(playerColor : String, maxTimeForMove : Long, name : String, game: Int) : TablutClient(playerColor ,name) {
    private lateinit var listaNodi: ArrayList<ExtendedState>
    private var tempoTerminato = false
    private var player=playerColor
    //private lateinit var initialState:ExtendedState
    private var maxDepth: Int = 6
    private var maxTime = maxTimeForMove
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

        while (true) {
            tempoTerminato=false
            try {
                read()
            } catch (e: IOException) {
                e.printStackTrace()
                System.exit(1)
            }
            var state = getCurrentState()
            println(state.toString())
            extendedOldState.setState(state)
            if (player == "WHITE") {
                // è il mio turno
                if (currentState.turn == State.Turn.WHITE) {
                    var timerJob = GlobalScope.launch{
                        delay(maxTime)
                        //channel.send(1)
                        println("tempo terminato")
                        //setTermina(true)
                        tempoTerminato = true
                    }
                    var extendedNewState = minMaxDecision(extendedOldState)
                    var a = extendedNewState.getAction(extendedOldState, player)
                    //println("Mossa scelta: $a")
                    timerJob.cancel()
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
                    var timerJob = GlobalScope.launch{
                        delay(maxTime)
                        //channel.send(1)
                        println("tempo terminato")
                        //setTermina(true)
                        tempoTerminato = true
                    }
                    var extendedNewState = minMaxDecision(extendedOldState)
                    var a = extendedNewState.getAction(extendedOldState, player)
                    //println("Mossa scelta: $a")
                    timerJob.cancel()
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

    fun minMaxDecision(state: ExtendedState): ExtendedState{

        var alfa=Double.NEGATIVE_INFINITY
        var beta=Double.POSITIVE_INFINITY
        listaNodi.clear()
        var nextState=ExtendedState()
        var v:Double

        v=maxValue0(state, alfa, beta)

        for(nodo in listaNodi){
            if(nodo.valoreAssegnato==v) {
                nextState=nodo
                break
            }
        }
        return nextState
    }

    private fun maxValue0(state: ExtendedState, alfa: Double, beta: Double): Double {
        var newAlfa=alfa
        var v: Double
        v = Double.NEGATIVE_INFINITY
        var actions=state.actions
        for(nextState in actions) {
            //println("nel loop getActions")
            //se sono i figli dello stato attuale del gioco li salvo in listaNodi per scegliere il migliore dopo
            //listaNodi.add(nextState)
            //var nextState_copia = ExtendedState()
            //nextState_copia.setState(nextState.state)
            //GlobalScope.launch {
                v = max(v, minValue(nextState, 1, newAlfa, beta))
                nextState.valoreAssegnato=v
                listaNodi.add(nextState)
                newAlfa = max(alfa, v)
                if(beta<=newAlfa) break
            //}
        }
        return v
    }


    fun maxValue(state: ExtendedState, depth: Int, alfa: Double, beta: Double):Double{
        var newAlfa=alfa //i parametri sono sempre val
        var v:Double
        var numWhite=0
        var numBlack=0
        for (i in 0..8) {
            for (j in 0..8) {
                if (extendedOldState.state.board[i][j] === State.Pawn.WHITE)
                    numWhite++
                if (extendedOldState.state.board[i][j] === State.Pawn.BLACK)
                    numBlack++
            }
        }
        var terminale=state.isTerminal(player, numWhite, numBlack)
        if(terminale != -2 || depth==maxDepth || tempoTerminato){   //isTerminal restituisce -> -2 = non terminale | -1 = terminale sconfitta | 0 = terminale pareggio | 1 = terminale vittoria | 2 = terminale
            val ev: Double
            if(terminale==1 || terminale==0 || terminale==-1) ev=terminale.toDouble()
            else ev=state.getUtility(player, terminale, numWhite, numBlack)
            //state.valoreAssegnato=ev
            //println("nodo terminale: \n${state.state.boardString()}, valore: ${ev}")
            return ev
        }
        //println("nodo max: \n${state.state.boardString()}")
        v = Double.NEGATIVE_INFINITY
        //println("caso depth>0")
        var actions=state.actions
        for(nextState in actions){
            //println("nel loop getActions")
            v = max(v, minValue(nextState, depth+1, newAlfa, beta))
            newAlfa = max(alfa, v)
            if(beta<=newAlfa) break
        }
        //state.valoreAssegnato =v
        return v
    }

    fun minValue(state: ExtendedState, depth: Int, alfa: Double, beta: Double):Double{
        var newBeta=beta
        var v:Double
        var numWhite=0
        var numBlack=0
        for (i in 0..8) {
            for (j in 0..8) {
                if (extendedOldState.state.board[i][j] === State.Pawn.WHITE)
                    numWhite++
                if (extendedOldState.state.board[i][j] === State.Pawn.BLACK)
                    numBlack++
            }
        }
        var terminale=state.isTerminal(player, numWhite, numBlack)
        if(terminale != -2 || depth==maxDepth || tempoTerminato){   //isTerminal restituisce -> -2 = non terminale | -1 = terminale sconfitta | 0 = terminale pareggio | 1 = terminale vittoria | 2 = terminale
            val ev: Double
            if(terminale==1 || terminale==0 || terminale==-1) ev=terminale.toDouble()
            else ev=state.getUtility(player, terminale, numWhite, numBlack)
            //state.valoreAssegnato =ev
            //println("nodo terminale: \n${state.state.boardString()}, valore: ${ev}")
            //readLine()
            return ev
        }
        //println("nodo min: \n${state.state.boardString()}")
        v = Double.POSITIVE_INFINITY
        var actions=state.actions
        for(nextState in actions){
            v = min(v, maxValue(nextState, depth+1, alfa, newBeta))
            newBeta = min(beta,v)
            if(newBeta<=alfa) break

        }
        //state.valoreAssegnato =v
        return v
    }


    companion object{
        fun main(args: Array<String>) {
            var gametype = 4
            var role = ""
            var name = "IA"
            var maxTime : Long = 50000

            if (args.isEmpty()) {
                println("You must specify which player you are (WHITE or BLACK) and Time")
                System.exit(-1)
            } else {
                println(args[0])
                role = args[0]
                println(args[1])
                maxTime = args[1].toLong() - 3500
            }
            if (args.size == 3) {
                println(args[2])
                gametype = args[2].toInt()
            }
            if (args.size == 4) {
                name = args[3]
            }
            println("Selected client: ${args[0]}, maxTime: ${args[1]}")

            var client = MyTablutClient(role, maxTime, name, gametype)
            client.run()
        }
    }

}
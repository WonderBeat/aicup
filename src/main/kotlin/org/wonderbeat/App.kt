package org.wonderbeat

import model.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("org.wonderbeat.aicup")

fun main(args : Array<String>) {
    Runner(if (args.size == 3) args else arrayOf("127.0.0.1", "31001", "0000000000000000")).run()
}


class Runner(args: Array<String>) {
    private val remoteProcessClient: RemoteProcessClient = RemoteProcessClient(args[0], Integer.parseInt(args[1]))
    private val token: String = args[2]

    fun run() {
        try {
            remoteProcessClient.writeToken(token)
            remoteProcessClient.writeProtocolVersion()
            val teamSize = remoteProcessClient.readTeamSize()
            val game = remoteProcessClient.readGameContext()

            val strategies = (0..teamSize - 1).map { FakeStrat() }

            var playerContext: PlayerContext

            while (true) {
                val playerContext = remoteProcessClient.readPlayerContext() ?: break
                val playerWizards = playerContext.getWizards()
                if (playerWizards == null || playerWizards.size != teamSize) {
                    break
                }

                val moves = arrayOfNulls<Move>(teamSize)

                for (wizardIndex in 0..teamSize - 1) {
                    val playerWizard = playerWizards[wizardIndex]

                    val move = Move()
                    moves[wizardIndex] = move
                    strategies[wizardIndex /*playerWizard.getTeammateIndex()*/].move(
                            playerWizard, playerContext.getWorld(), game, move
                    )
                }

                remoteProcessClient.writeMoves(moves)
            }
        } finally {
            remoteProcessClient.close()
        }
    }
}
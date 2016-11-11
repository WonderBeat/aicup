package org.wonderbeat

import model.*
import org.funktionale.utils.identity
import org.jgrapht.alg.AStarShortestPath
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic
import org.jgrapht.graph.AbstractBaseGraph
import org.jgrapht.graph.Pseudograph
import org.jgrapht.graph.SimpleGraph
import org.jgrapht.graph.builder.UndirectedGraphBuilder
import org.jgrapht.util.MathUtil
import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.IntStream
import java.util.stream.Stream


class Edge

class PathNavigator(val mapSize: Int,
                    positionsFilter: (Position) -> Boolean = { false },
                    val probesDistance: Int = 1) {

    private fun permulations(from: Int, to: Int, step: Int): Stream<Pair<Int,Int>> {
        val limit = ((to - from) / step).toLong()
        return IntStream.iterate(0, { it + step} ).limit(limit)
                .mapToObj { first ->
                    IntStream.iterate(0, { it + step} ).mapToObj { second -> Pair(first, second) }.limit(limit) }
                .flatMap(identity())
    }

    private val oneStepUpAndOneStepRight = listOf(Pair(probesDistance,0), Pair(0, probesDistance))

    private val graph = permulations(0, mapSize, probesDistance)
            .parallel()
            .map { Position(it.first, it.second)}
            .filter { !positionsFilter(it)}
            .collect(Collectors.toList<Position>())
            .fold(UndirectedGraphBuilder(
                    Pseudograph<Position, Edge>(Edge::class.java)),
                    { acc, current ->
                        oneStepUpAndOneStepRight
                                .forEach { pair ->
                                    val nextX = current.x + pair.first
                                    val nextY = current.y + pair.second
                                    if (nextX >= 0 && nextY >= 0 &&
                                            nextX <= mapSize && nextY <= mapSize) {
                                        acc.addEdge(current,
                                                Position(nextX, nextY))
                                    }
                                }
                        acc
                    }).build()

    private val pathPredictor = AStarShortestPath(graph)

    fun path(from: Position, to: Position): List<Position>? {
        val fromRounded = Position(from.x / probesDistance * probesDistance, from.y / probesDistance * probesDistance)
        val toRounded = Position(to.x / probesDistance * probesDistance, to.y / probesDistance * probesDistance)
        val toNearest = graph.vertexSet().sortedBy { StrictMath.hypot(it.x.toDouble() - toRounded.x, it.y.toDouble() - toRounded.y) }.first()
        return pathPredictor.getShortestPath(fromRounded, toNearest,
                    { s, d -> StrictMath.hypot((s.x - d.x).toDouble(), (s.y - d.y).toDouble()) })?.vertexList
    }

}
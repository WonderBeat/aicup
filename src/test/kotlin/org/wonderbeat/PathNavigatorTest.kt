package org.wonderbeat

import org.jgrapht.alg.AStarShortestPath
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic
import org.jgrapht.graph.Pseudograph
import org.jgrapht.graph.SimpleGraph
import org.junit.Assert.*
import org.junit.Test


class PathNavigatorTest {


    @Test
    fun shouldShowPath() {
        val graph = Pseudograph<Position, Edge>(Edge::class.java)
        val one = Position(0, 0)
        graph.addVertex(one)
        val two = Position(1, 0)
        graph.addVertex(two)
        val three = Position(1, 1)
        graph.addVertex(three)
        graph.addEdge(one, two)
        graph.addEdge(two, three)
        graph.addEdge(three, one)
        assert(AStarShortestPath(graph).getShortestPath(three, two,
                (AStarAdmissibleHeuristic { s, d -> StrictMath.hypot(s.x.toDouble() - d.x, s.y.toDouble() - d.y) })) != null)
    }

    @Test
    fun shouldReturnPathForMap() {
        val navi = PathNavigator(100)
        assert(navi.path(Position(99, 99), Position(19, 99)) != null)
    }
}
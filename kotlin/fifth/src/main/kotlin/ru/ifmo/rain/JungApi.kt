package ru.ifmo.rain

import edu.uci.ics.jung.algorithms.layout.KKLayout
import edu.uci.ics.jung.graph.DirectedGraph
import edu.uci.ics.jung.graph.DirectedSparseGraph
import edu.uci.ics.jung.visualization.BasicVisualizationServer
import edu.uci.ics.jung.visualization.renderers.Renderer
import java.awt.Dimension
import javax.swing.JFrame
import kotlin.math.log2


class JungApi : DrawingApi {
    private var graph: DirectedGraph<Int, Int>? = null
    private var edgeId = 0

    override fun drawVertex(v: Int) {
        graph!!.addVertex(v)
    }

    override fun drawEdge(e: Pair<Int, Int>) {
        graph!!.addEdge(edgeId++, e.first, e.second)
    }

    override fun render(builder: (DrawingApi) -> Unit) {
        graph = DirectedSparseGraph()
        edgeId = 0

        builder.invoke(this)

        val layout = KKLayout(graph)
        val view = BasicVisualizationServer(layout).apply {
            val v = log2(graph!!.vertexCount.toDouble())
            size = Dimension((v * 180).toInt() , (v * 150).toInt())
            renderer.vertexLabelRenderer.position = Renderer.VertexLabel.Position.AUTO
            renderContext.setVertexLabelTransformer { it.toString() }
        }

        JFrame("Jung Visualiser").apply {
            defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
            contentPane.add(view)
            pack()
            isVisible = true
        }
    }
}
package ru.ifmo.rain

import com.mxgraph.layout.mxFastOrganicLayout
import com.mxgraph.swing.mxGraphComponent
import com.mxgraph.view.mxGraph
import java.awt.Dimension
import java.util.*
import javax.swing.JFrame
import kotlin.collections.HashMap
import kotlin.math.log2

class JGraphXApi: DrawingApi {
    private var graph: mxGraph? = null
    private var parent: Any? = null
    private var vertexMap: MutableMap<Int, Any>? = null

    override fun drawVertex(v: Int) {
        vertexMap!![v] = graph!!.insertVertex(parent, null, v, 100.0, 100.0,
                20.0, 17.0, "shape=circle")
    }

    override fun drawEdge(e: Pair<Int, Int>) {
        graph!!.insertEdge(parent, null, "", vertexMap!![e.first], vertexMap!![e.second])
    }

    override fun render(builder: (DrawingApi) -> Unit) {
        graph = mxGraph()
        with(graph!!) {
            parent = this.defaultParent
            vertexMap = HashMap()
            model.beginUpdate()
            builder.invoke(this@JGraphXApi)
            mxFastOrganicLayout(graph).execute(parent)
            model.endUpdate()
        }

        JFrame("JGraphX Visualiser").apply {
            contentPane.add(mxGraphComponent(graph))
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            val v = log2(vertexMap!!.size.toDouble())
            size = Dimension((v * 180).toInt() , (v * 150).toInt())
            isVisible = true
        }

    }
}
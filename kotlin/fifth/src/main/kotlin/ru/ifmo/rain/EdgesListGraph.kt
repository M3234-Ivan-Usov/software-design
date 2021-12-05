package ru.ifmo.rain

class EdgesListGraph(drawingApi: DrawingApi): AbstractGraph(drawingApi) {
    val edges = ArrayList<Pair<Int, Int>>()

    override fun show() {
        drawingApi.render {
            val v = HashSet<Int>()
            edges.forEach{ e -> v.add(e.first); v.add(e.second) }
            v.forEach(it::drawVertex)
            edges.forEach(it::drawEdge)
        }
    }

}

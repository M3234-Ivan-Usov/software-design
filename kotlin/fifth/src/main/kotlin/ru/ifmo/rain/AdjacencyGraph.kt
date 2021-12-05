package ru.ifmo.rain

class AdjacencyGraph(private val matrix: Array<Array<Boolean>>, drawingApi: DrawingApi): AbstractGraph(drawingApi) {
    override fun show() {
        drawingApi.render {
            (matrix.indices).forEach(it::drawVertex)
            matrix.forEachIndexed { x, a ->
                assert(a.size == matrix.size) { "Dimensions do not match" }
                a.forEachIndexed { y, b -> if (b) it.drawEdge(x to y)}
            }
        }
    }
}

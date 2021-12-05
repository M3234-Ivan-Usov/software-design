package ru.ifmo.rain


interface DrawingApi {
    fun drawVertex(v: Int)

    fun drawEdge(e: Pair<Int, Int>)

    fun render(builder: (DrawingApi) -> Unit)
}
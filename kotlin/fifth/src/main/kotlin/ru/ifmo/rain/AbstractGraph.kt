package ru.ifmo.rain

abstract class AbstractGraph(protected val drawingApi: DrawingApi) {
    abstract fun show()
}


object RequestApi {
    private val apiMap: Map<String, () -> DrawingApi> = mapOf(
            "jung" to ::JungApi, "jgraphx" to ::JGraphXApi
    )

    fun get(name: String): DrawingApi? = apiMap[name]?.invoke()
}
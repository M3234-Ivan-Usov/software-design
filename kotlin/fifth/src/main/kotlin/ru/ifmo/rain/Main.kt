package ru.ifmo.rain

fun main(args: Array<String>) {
    val api = RequestApi.get(args[0])

    AdjacencyGraph(arrayOf(
            arrayOf(false, false, true, false, true),
            arrayOf(true, false, true, false, false),
            arrayOf(false, true, false, false, true),
            arrayOf(true, false, false, true, false),
            arrayOf(false, true, true, false, true)
    ), api!!).show()

    with(EdgesListGraph(api)) {
        edges.addAll(listOf(1 to 2, 2 to 3, 4 to 2, 1 to 4, 5 to 3))
        show()
    }
}
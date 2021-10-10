package ru.ifmo.rain

import kotlin.collections.ArrayList
import kotlin.math.sqrt

class LRUCache<A, R>(private val capacity: Int, private val objective: (A) -> R) {
    private val cells = getPrevPrime(capacity)
    private val storage = Array<MutableList<LRUNode<A, R>>>(cells) { ArrayList() }
    private var elements = 0

    private class LRUNode<X, Y>(val arg: X?, val result: Y?) {
        var prev: LRUNode<X, Y>? = null
        var next: LRUNode<X, Y>? = null
    }

    private val head = LRUNode<A, R>(null, null)
    private val tail = LRUNode<A, R>(null, null)

    init {
        head.next = tail
        tail.prev = head
    }

    private fun getPrevPrime(n: Int): Int {
        assert(n > 2) { "Too small number given: $n" }
        return (n downTo 2).find { x ->
            val upper = sqrt(x.toFloat()).toInt()
            for (y in 2..upper) {
                if (x % y == 0) return@find false
            }
            true
        }!!
    }

    private fun hash(arg: A) = arg.hashCode() % cells

    private fun linkAfter(prev: LRUNode<A, R>, node: LRUNode<A, R>) {
        assert(prev !== tail) { "Cannot link node after tail" }
        assert(node !== head && node !== tail) { "Attempt to link head/tail" }
        val next = prev.next!!
        assert(next.prev === prev) { "Double-link invariant is broken" }

        node.prev = prev; prev.next = node
        node.next = next; next.prev = node
        elements++
    }

    private fun cut(node: LRUNode<A, R>) {
        assert(node !== tail && node !== head) { "Attempt to cut head/tail" }
        val prev = node.prev!!; val next = node.next!!
        assert(prev.next === node && next.prev === node) { "Double-link invariant is broken" }

        prev.next = next; next.prev = prev
        node.prev = null; node.next = null
        elements--
    }

    private fun evict() {
        assert(tail.prev !== head && head.next !== tail && elements > 0) { "Attempt to evict from empty cache" }
        val evictNode = tail.prev!!
        val bucket = hash(evictNode.arg!!)
        cut(evictNode); assert(storage[bucket].remove(evictNode)) { "Failed to evict element" }
    }

    private fun warm(node: LRUNode<A, R>) {
        cut(node); linkAfter(head, node)
    }

    fun invalidate() {
        storage.forEach { bucket -> bucket.forEach(this::cut); bucket.clear() }
        assert(head.next === tail && tail.prev === head && elements == 0) { "Failed to invalidate cache" }
    }

    fun call(arg: A): R {
        val bucket = hash(arg)
        val node = storage[bucket].find { x -> x.arg == arg }
        return if (node == null) {
            val result = objective.invoke(arg)
            val newNode = LRUNode(arg, result)
            if (elements == capacity) evict()
            assert(elements < capacity) { "No place to store new node" }
            newNode.also { storage[bucket].add(it); linkAfter(head, it) }.result!!
        }
        else {
            node.apply(this::warm).result!!
        }
    }
}
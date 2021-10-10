package ru.ifmo.rain

import org.junit.jupiter.api.Test
import java.math.BigInteger
import kotlin.test.*

class LRUCacheTest {
    private fun objective(n: BigInteger): BigInteger =
            if (n <= BigInteger.ONE) BigInteger.ONE else n * objective(n.dec())

    @Test
    fun testSameReturnValue() {
        val cache = LRUCache(5, this::objective)
        val firstCall = cache.call(BigInteger("5"))
        assertEquals(BigInteger("120"), firstCall)
        assertSame(firstCall, cache.call(BigInteger("5")))
        cache.invalidate()
        assertNotSame(firstCall, cache.call(BigInteger("5")))
    }

    @Test
    fun testEviction() {
        val cache = LRUCache(5, this::objective)
        val firstCall = cache.call(BigInteger("10"))
        for (x in 1..5) cache.call(BigInteger(x.toString()))
        assertNotSame(firstCall, cache.call(BigInteger("10")))
    }

    @Test
    fun testWarming() {
        val cache = LRUCache(8, this::objective)
        val firstArgInitialCall = cache.call(BigInteger("10"))
        val secondArgInitialCall = cache.call(BigInteger("15"))
        for (x in 3..7) cache.call(BigInteger(x.toString()))
        val firstArgWarmCall = cache.call(BigInteger("10"))
        assertSame(firstArgInitialCall, firstArgWarmCall)
        for (x in 7..10) cache.call(BigInteger(x.toString()))
        assertNotSame(secondArgInitialCall, cache.call(BigInteger("15")))
        assertSame(firstArgWarmCall, cache.call(BigInteger("10")))
    }

}
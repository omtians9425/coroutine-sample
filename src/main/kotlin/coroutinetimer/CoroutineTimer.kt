package coroutinetimer

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    val condition: () -> Boolean = { count >= 15 }

    startTimer(100L, 2500L, condition) {
        println("$count")
        count++
    }
}

var count = 0
fun startTimer(interval: Long, timeLimit: Long = 2500L, endCondition: () -> Boolean, block: () -> Unit) = runBlocking {
    val job = launch {
        while (!endCondition()) {
            delay(interval)
            block()
        }
    }
    delay(timeLimit)
    println("time limit exceeded!")
    job.cancel()
}

package errorhandling

import kotlinx.coroutines.*
import java.io.IOException

fun main() {
    notCrash()
    crash()
}

suspend fun doHello() {
    delay(100L)
    println("Hello")
}

suspend fun doWorld() {
    delay(200L)
    throw IOException("cancel coroutine") //throws exception in order to cancel coroutine forcibly.
    println("World")
}

/**
 * This implementation does not follow structured concurrency.
 * This version cannot handle exception of coroutine's cancel completely because all coroutines are canceled if children are canceled.
 */
fun crash() = runBlocking {
    launch {
        try {
            val hello = async {
                doHello()
            }
            val world = async {
                doWorld()
            }
            hello.await()
            world.await()
        } catch (e: IOException) {
            //This is called but also be thrown externally and crash.
            println("catch exception but cannot handle...")
        }
    }
}

/**
 * This follows structured concurrency.
 * This version can handle cancellation because parent launch is not canceled if its children are canceled.
 * Thanks to coroutineScope, we only wrap with try-catch to handle exception.
 */
fun notCrash() = runBlocking {
    launch {
        try {
            //wrap with coroutineScope builder that defines the children scope.
            coroutineScope {
                val hello = async {
                    doHello()
                }
                val world = async {
                    doWorld()
                }
                hello.await()
                world.await()
            }
        } catch (e: IOException) {
            println("handle cancel!! $e")
        }
    }
}
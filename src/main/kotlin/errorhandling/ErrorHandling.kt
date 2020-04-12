package errorhandling

import kotlinx.coroutines.*
import java.io.IOException
import java.lang.AssertionError

fun main() {
    notCrash()
    alsoOk()
//    crash()
//    recallAlreadyCancelledScope()

    applicationScopeSample()
}

suspend fun doHello() {
    delay(100L)
    println("Hello")
}

@Suppress("UNREACHABLE_CODE")
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

// forcibly (brute force) catching: wrap the most outer scope by try-catch
fun alsoOk() {
    try {
        runBlocking {
            launch {
                val hello = async {
                    doHello()
                }
                val world = async {
                    doWorld()
                }
                hello.await()
                world.await()

            }
        }
    } catch (e: IOException) {
        println("handle cancel!! $e")
    }
}

// Anti pattern: Once scope is cancelled, you won’t be able to launch new coroutines in the cancelled scope.
fun recallAlreadyCancelledScope() {
    val handler = CoroutineExceptionHandler { _, _ ->
        println("error") // called infinitely.
    }
    val scope = CoroutineScope(Job() + handler)
    var count = 0
    while (true) {
        val job = scope.launch {
            count++
            if (count % 2 == 0) throw AssertionError("") // dies here
            println("increment")
        }
        println("scope status. isActive: ${scope.isActive}.")
    }
}

fun applicationScopeSample() {
    // larger scope. ex:  "application scope" that defined at Application class
    // SupervisorJobは下のキャンセルとは無関係だが、他のクライアントがこのスコープで例外を投げた場合に処理が止まらないようにできる
    val externalScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Smaller scope. ex: viewModelScope
    val innerScope = CoroutineScope(Job())

    val innerJob = innerScope.launch {
        println("something operation")
        delay(100L)

        // not canceled by innerScope's cancel
        externalScope.launch {
            delay(600L)
            println("very important operation") // This should be shown
        }.join()
    }
    Thread.sleep(500L)
    innerJob.cancel()
    Thread.sleep(500L)
}
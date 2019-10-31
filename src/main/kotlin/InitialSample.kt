import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    println("#####helloWorld")
    helloWorld()
    println("#####\n")

    println("#####helloWorldWithBlocking")
    helloWorldWithBlocking()
    println("#####\n")

    println("#####helloWorldWithWaiting")
    helloWorldWithWaiting()
    println("#####\n")

    println("#####helloWorldWithStructuredConcurrency")
    helloWorldWithStructuredConcurrency()
    println("#####\n")
}

fun helloWorld() {
    GlobalScope.launch {
        delay(1000L)
        println("World")
    }
    println("Hello,")
    Thread.sleep(1100L)
}

fun helloWorldWithBlocking() = runBlocking { //main coroutine
    GlobalScope.launch { //new coroutine that doesn't block main coroutine
        println("suspended")
        delay(1000L)
        println("World")
    }
    println("Hello,")
    delay(2000L)
}


fun helloWorldWithWaiting() = runBlocking{
    val job = GlobalScope.launch {
        delay(1000L)
        println("World")
    }
    println("Hello,")
    job.join()
}

/**
 * Structured Concurrency version.
 * A New Coroutine is build using the scope of its parent coroutine so there is no need to join() explicitly.
 * The parent coroutine waits for all coroutines that start in parent scope to complete.
 * This principle also enables make error handling more easy.
 */
fun helloWorldWithStructuredConcurrency() = runBlocking { //parent, main coroutine
    launch {
        delay(1000L)
        println("World")
    }
    println("Hello,")
}
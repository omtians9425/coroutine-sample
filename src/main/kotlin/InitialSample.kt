import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    helloWorld()
    println("#####")
    helloWorldWithBlocking()
    println("#####")
    helloWorldWaiting()
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

fun helloWorldWaiting() = runBlocking{
    val job = GlobalScope.launch {
        delay(1000L)
        println("World")
    }
    println("Hello,")
    job.join()
}
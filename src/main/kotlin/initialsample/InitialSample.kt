package initialsample

import kotlinx.coroutines.*

fun main() {
    println("#####initialSample.helloWorld")
    helloWorld()
    println("#####\n")

    println("#####initialSample.helloWorldWithBlocking")
    helloWorldWithBlocking()
    println("#####\n")

    println("#####initialSample.helloWorldWithWaiting")
    helloWorldWithWaiting()
    println("#####\n")

    println("#####initialSample.helloWorldWithStructuredConcurrency")
    helloWorldWithStructuredConcurrency()
    println("#####\n")

    println("#####initialSample.helloWorldWithStructuredConcurrency2")
    helloWorldWithStructuredConcurrency2()
    println("#####\n")

    coroutineScopeSample()
    doHelloWorld()
}

fun helloWorld() {
    GlobalScope.launch {
        delay(100L)
        println("World")
    }
    println("Hello,")
    Thread.sleep(200L)
}

fun helloWorldWithBlocking() = runBlocking { //initialSample.errorhandling.main coroutine
    GlobalScope.launch { //new coroutine that doesn't block initialSample.errorhandling.main coroutine/
        println("suspended")
        delay(100L)
        println("World")
    }
    println("Hello,")
    delay(200L) //still need to wait because the scope of launch() is not parent scope but Global scope.
}


fun helloWorldWithWaiting() = runBlocking{
    val job = GlobalScope.launch {
        delay(100L)
        println("World")
    }
    println("Hello,")
    job.join()
}

/**
 * Structured Concurrency version.
 * A New Coroutine is build using the scope of its parent coroutine so there is no need to join() explicitly.
 * The parent coroutine waits for all coroutines that start in parent(or its child) scope to complete.
 * This principle also enables make error handling more easy.
 */
fun helloWorldWithStructuredConcurrency() = runBlocking { //parent, initialSample.errorhandling.main coroutine
    launch {//"this" is omitted
        delay(100L)
        println("World")
    }
    println("Hello,")
}

fun helloWorldWithStructuredConcurrency2() = runBlocking { //parent, initialSample.errorhandling.main coroutine
    /**
     *  This is also a coroutine builder that creates a new scope.
     *  This blocks parent coroutine except for new coroutines created in this coroutine.
     */
    coroutineScope {
        delay(100L)
        println("Hello,")
    }
    println("World")
}

fun coroutineScopeSample() = runBlocking {
    launch {
        delay(200L)
        println("2. task from runBlocking")
    }

    //This new scope is child scope of runBlocking scope.
    coroutineScope {
        launch {
            delay(500L)
            println("3. task from nested launch")
        }

        delay(100L)
        println("1. task from coroutineScope")
    }
    println("4. scope ended.")
}

var scope: CoroutineScope? = null
fun doHelloWorld() = runBlocking() {
    scope = this
    doWorld()
    println("Hello,")
}

//forcibly use parent scope
fun doWorld() {
    scope?.launch {
        delay(100L)
        println("World")
    }
}
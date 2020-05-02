package flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

fun main () {
    val f  = flow {
        delay(100L)
        emit(1)
    }
    val scope = CoroutineScope(Dispatchers.Default)
    scope.launch {
        f.collect {
            print("collect: $it")
        }
    }
}
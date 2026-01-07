import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileReader

object RoutineManager {
    val sendChannel = Channel<String>(capacity = 1000)
    val receiveChannel = Channel<Map<String,Int>>(capacity = 5)
    suspend fun initializeRoutines(path: String, coCount: Int) {
        val file = File(path)
        if (!file.exists()) {
            println("File does not exist at path: $path")
            return
        }
        for (i in 0..coCount){
            LineProcessor(GenUtils.separator, sendChannel)
        }
        coroutineScope {
            withContext(Dispatchers.IO) {
                file.useLines { lines ->
                    for (line in lines) {
                        sendChannel.send(line)
                    }
                }
            }
        }
    }

    suspend fun mergeMaps() {
        val finalMap = mutableMapOf<String, Int>()
        while (!receiveChannel.isClosedForReceive)
        {
            val partialMap = receiveChannel.receiveCatching()
            if (partialMap.isSuccess) {
                try {
                    for ((key, value) in partialMap.getOrThrow()) {
                        finalMap[key] = finalMap.getOrDefault(key, 0) + value
                    }
                } catch (e: Exception) {break}
            }
            for ((key, value) in finalMap) {
                println("$key: $value")
            }
        }
    }
}

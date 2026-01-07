import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object RoutineLauncher {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun initializeRoutines(path: String, coCount: Int, sendChannel: Channel<String>, resultChannel: Channel<Map<String,Int>>) {
        val file = File(path)
        if (!file.exists()) {
            println("File does not exist at path: $path")
            sendChannel.close()
            resultChannel.close()
            return
        }
        coroutineScope {
            launch {
                val jobs = (1..coCount).map {
                    println("Processing $it")
                    launch {
                        LineProcessor(GenUtils.separator, sendChannel, resultChannel).processLine()
                    }
                }
                jobs.joinAll()
                resultChannel.close()
            }
           launch {
               println("Reading file and sending lines to processing channels...")
               withContext(Dispatchers.IO) {
                   file.useLines { lines ->
                       for (line in lines) {
                           sendChannel.send(line)
                       }
                   }
                   sendChannel.close()
               }
           }
        }
    }

}

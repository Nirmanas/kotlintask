import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.ln
import kotlin.math.pow

object RoutineLauncher {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun initializeRoutines(path: String, coCount: Int, sendChannel: Channel<List<String>>, resultChannel: Channel<Map<String,Int>>) {
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
                    launch (Dispatchers.Default) {
                        LineProcessor(GenUtils.separator, sendChannel, resultChannel).processLine()
                    }
                }
                jobs.joinAll()
                resultChannel.close()
            }
           launch {
               println("Reading file and sending lines to processing channels...")
               withContext(Dispatchers.IO) {
                   file.readLines()
                       .chunked(250)
                       .forEach { lines ->
                       sendChannel.send(lines) }
                   while (!sendChannel.isEmpty)
                   {
                       // wait for processing to complete and then close the channel
                   }
                   sendChannel.close()
               }
           }
        }
    }

}

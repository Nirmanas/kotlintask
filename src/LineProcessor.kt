import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.Channel

class LineProcessor(separators: List<String>, val receiver: Channel<String>) {
    companion object {
        val sender = Channel<Map<String,Int>>()
    }
    val map = mutableMapOf<String, Int>()
    val regex = ("[" + separators.reduce { acc, s -> acc + s } + "]+").toRegex()
    var processedLines = 0;
    @OptIn(DelicateCoroutinesApi::class)
    suspend fun processLine() {
        while (!this.receiver.isClosedForReceive) {
            try {
                val words = this.receiver.receiveCatching().getOrThrow().split(regex = this.regex)
            for (word in words) {
                if (word.isNotEmpty()) {
                    val key = word.lowercase()
                    map[key] = map.getOrDefault(key, 0) + 1
                }
            }
                processedLines++;
            } catch (e: Exception) {
                sender.send(map)
            }
            if (processedLines % 100 == 0){
                sender.send(map)
                map.clear()
            }
        }
    }
}

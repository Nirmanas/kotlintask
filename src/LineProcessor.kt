import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.Channel

class LineProcessor(separators: List<String>, val receiver: Channel<List<String>>, val sender: Channel<Map<String, Int>>) {
    val regex = ("[" + separators.reduce { acc, s -> acc + s } + "\\s]+").toRegex()
    var processedLines = 0

    val map = mutableMapOf<String, Int>()
    @OptIn(DelicateCoroutinesApi::class)
    suspend fun processLine() {
        while (!this.receiver.isClosedForReceive) {
            try {
                val lines = this.receiver.receiveCatching().getOrThrow()
                for (line in lines) {
                    for (word in line.split(regex=this.regex).filter { !it.isEmpty() }) {
                        if (word.isNotEmpty()) {
                            val key = word.lowercase()
                            map[key] = map.getOrDefault(key, 0) + 1
                        }
                    }
                    processedLines++
                }
            } catch (e: Exception) {
                break
            }
        }

        if (map.isNotEmpty()) {
            sender.send(map)
        }

    }
}

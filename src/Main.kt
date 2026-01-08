import GenUtils.Companion.generateFile
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator


/*
    16 gijų veikia greičiausiai, nesiekia 2 sekundžių .
    12 gijų trunka panašiai skaičiavimo trukmė apie 2-2,1 sekundes.
    8  gijos trunka apie 2,3 sekundes.
    4  gijos trunka 3,2 sekundes.
    1  gija skaičiavimai svyruoja tarp 9,7 - 10 sekundžių,
    Kai duomenų faile yra 100000 eilučių po 1000 žodžių.
 */
object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        var path = "default_data.txt"
        val listOfArgs: List<String> = listOf(*args)

        // generate file if --generate-file is provided
        if (listOfArgs.contains("--generate-file")) {
            var lineCount = 100000


            if (listOfArgs.contains("-n")) {
                val index = listOfArgs.indexOf("-n")
                if (index != -1) lineCount =
                    (if (listOfArgs.size > index + 1) listOfArgs[index + 1] else listOfArgs[index]).toInt()
            }


            path = "data_file" + LocalDateTime.now() + ".txt"

            if (listOfArgs.contains("-f")) {
                val index = listOfArgs.indexOf("-f")
                if (index != -1) path = (if (listOfArgs.size > index + 1) listOfArgs[index + 1] else path)
            }

            if (!generateFile(path, lineCount, 1000)) {
                throw RuntimeException("File could not be generated.")
            }

            println("File generated successfully at $path with $lineCount lines.")
        } else if (listOfArgs.contains("-f")) {
            val index = listOfArgs.indexOf("-f")
            if (index == -1) throw RuntimeException("Provide a file path after -f flag.")
            else path = (if (listOfArgs.size > index + 1) listOfArgs[index + 1] else null)!!
        }
        var coroutineCount = 10
        if (listOfArgs.contains("-t")) {
            val index = listOfArgs.indexOf("-t")
            if (index == -1) throw RuntimeException("Provide a thread count after -t")
            coroutineCount = if (listOfArgs.size > index + 1) listOfArgs[index + 1].toInt() else 10
        }

        val startTime = LocalDateTime.now()
        val sendChannel = Channel<List<String>>(capacity = coroutineCount*10)
        val receiveChannel = Channel<Map<String,Int>>(capacity = coroutineCount)
        runBlocking {
                launch { RoutineLauncher.initializeRoutines(path, coroutineCount, sendChannel, receiveChannel) }
                launch { mergeMaps(receiveChannel) }
        }
        val endTime = LocalDateTime.now()

        println("Duration: ${java.time.Duration.between(startTime, endTime).toMillis()} ms")
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun mergeMaps(receiveChannel:Channel<Map<String,Int>>) {
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
        }
        var acc = 0
        for ((key, value) in finalMap) {
            println("$key: $value")
            acc += value
        }
        println("Total: $acc")
    }
}

import GenUtils.Companion.generateFile
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        var path = "default_data.txt"
        val listOfArgs: List<String> = listOf(*args)

        // generate file if --generate-file is provided
        if (listOfArgs.contains("--generate-file")) {
            var lineCount = 1000000
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

            if (!generateFile(path, lineCount)) {
                throw RuntimeException("File could not be generated.")
            }

            println("File generated successfully at $path with $lineCount lines.")
            println()
        } else if (listOfArgs.contains("-f")) {
            val index = listOfArgs.indexOf("-f")
            if (index == -1) throw RuntimeException("Provide a file path after -f flag.")
            else path = (if (listOfArgs.size > index + 1) listOfArgs[index + 1] else null)!!
        }

        runBlocking {
            RoutineManager.initializeRoutines(path, 10)
        }

    }
}

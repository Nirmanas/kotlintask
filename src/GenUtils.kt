import java.io.File

class GenUtils {
    companion object{

        private val wordList = (
            "apple, river, sky," +
            " mountain, forest," +
            " ocean, breeze, sun," +
            " moon, star, cloud, rain," +
            " thunder, stone, fire, ember, shadow, light," +
            " dawn, dusk, path, road, bridge, field, meadow, valley," +
            " hill, leaf, root, branch, seed, flower, grass, moss, bark," +
            " wave, tide, shore, sand, shell, coral, fish, bird, wing, feather," +
            " claw, fur, scale, echo, sound, silence, whisper, song," +
            " rhythm, beat, pulse, breath, heart, mind, thought," +
            " dream, hope, fear, courage, strength, balance," +
            " peace, calm, storm, chaos, order, time," +
            " moment, memory, story, myth, legend," +
            " truth, reason, logic, wisdom," +
            " knowledge, insight, vision," +
            " focus, clarity, purpose," +
            " goal, effort, action, motion," +
                " energy, power, force, gravity," +
                " space, matter, form, shape, change")
            .split(", ")
            .toList()
        val separator = listOf(",", ";", ":", "!", "?", ".", "")
        @JvmStatic
        fun generateFile(path: String, lnCount: Int): Boolean {
            try {
                val file = File(path)

                try {
                    file.parentFile.mkdirs()
                } catch (e: Exception){
                    println("Path does not have a parent directory.")
                }
                file.createNewFile()
                if (!file.exists())
                {
                    return false
                }
                file
                    .bufferedWriter(Charsets.UTF_8)
                    .use {
                        writer -> (1..lnCount).forEach{
                            writer.write(
                                (1..23)
                                .map { wordList.random() }
                                .reduce { acc, s -> s + separator.random() + " " + acc })
                        writer.newLine()
                        }
                    }
                return true;
            } catch (e: Exception) {
                println(e);
                return false
            }
        }
    }
}

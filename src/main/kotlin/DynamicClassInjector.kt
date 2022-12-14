import java.io.File

fun injectDynamicClass(className: String, rawKotlinCode: String): Class<*> {
    val file = File("$className.kt")
    file.writeText(rawKotlinCode)
    file.deleteOnExit()

    val durationTimer = DurationTimer()
    val proc = Runtime.getRuntime().exec("kotlinc -no-jdk -no-reflect $file")
    val exitCode = proc.waitFor()
    if (exitCode != 0) {
        val error = proc.errorReader().readLine()
        throw Error("kotlinc exited with code $exitCode: $error")
    }

    println("Successfully compiled $className.class in ${durationTimer.duration()}ms")
    return DynamicClassInjector().injectClass(className)
}

class DynamicClassInjector : ClassLoader() {
    fun injectClass(name: String): Class<*> {
        val file = File("$name.class")
        file.deleteOnExit()
        val bytes = file.readBytes()
        return defineClass(
            name,
            bytes, 0, bytes.size
        )
    }
}
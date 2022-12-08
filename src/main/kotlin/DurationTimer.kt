class DurationTimer {
    private val startTime = System.currentTimeMillis()

    fun duration() = System.currentTimeMillis() - startTime
}
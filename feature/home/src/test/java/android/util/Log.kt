package android.util

@Suppress("unused")
object Log {
    const val VERBOSE: Int = 2
    const val DEBUG: Int = 3
    const val INFO: Int = 4
    const val WARN: Int = 5
    const val ERROR: Int = 6
    const val ASSERT: Int = 7

    @JvmStatic
    fun isLoggable(tag: String?, level: Int): Boolean = false

    @JvmStatic
    fun d(tag: String?, msg: String?): Int = 0

    @JvmStatic
    fun i(tag: String?, msg: String?): Int = 0

    @JvmStatic
    fun w(tag: String?, msg: String?): Int = 0

    @JvmStatic
    fun e(tag: String?, msg: String?): Int = 0

    @JvmStatic
    fun v(tag: String?, msg: String?): Int = 0
}

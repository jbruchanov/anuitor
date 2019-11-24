package groovy.lang

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.File

class GrooidShellKt(tmp: File,
                    classLoader: ClassLoader) {
    private val shell = GrooidShell(tmp, classLoader)

    suspend fun executeAsync(code: String) = GlobalScope.async {
        val script = withContext(Dispatchers.Default) {
            shell.compile(code)
        }
        val result: Any? = withContext(Dispatchers.Main) {
            script.run()
        }
        result
    }
}
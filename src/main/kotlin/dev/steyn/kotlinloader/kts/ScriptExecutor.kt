package dev.steyn.kotlinloader.kts

import java.io.Reader
import javax.script.ScriptEngineManager
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.api.hostConfiguration
import kotlin.script.experimental.api.with
import kotlin.script.experimental.host.with
import kotlin.script.experimental.jvm.baseClassLoader
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.jsr223.KotlinJsr223ScriptEngineImpl

internal class ScriptExecutor<T>(
        val source: Reader,
        vararg val imports: String,
        loader: ClassLoader
) {

    val engine by lazy {
        val x = ScriptEngineManager().getEngineByExtension("kts") as KotlinJsr223ScriptEngineImpl
        val compile = x.compilationConfiguration.with {
            this[defaultImports] = listOf(
                    *imports
            )
            this[hostConfiguration].with{
                this[jvm.baseClassLoader] = loader
            }
        }
        val eval = x.evaluationConfiguration.with {
            this[hostConfiguration].with {
                this[jvm.baseClassLoader] = loader
            }
        }

        val result = KotlinJsr223ScriptEngineImpl(x.factory, compile, eval, x.getScriptArgs)

        result
    }

    fun execute() = engine.compile(source).eval() as T

}
fun <T> executeScript(src: Reader, loader: ClassLoader, vararg import: String) : T? {
    var result: T? = null
    Thread {
        Thread.currentThread().contextClassLoader = loader
        result = ScriptExecutor<T>(src, *import, loader = loader).execute()
    }.run {
        start()
        join()
    }
    return result
}
package dev.steyn.kotlinloader.kts

import dev.steyn.kotlinloader.kts.plugin.KtsPluginBuilder
import java.io.File
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.baseClassLoader
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

internal class ScriptExecutor<T>(
        val file: File,
        vararg val imports: String,
        val loader: ClassLoader
) {

    fun execute0(): ResultWithDiagnostics<EvaluationResult> {
        val scriptDef = createJvmCompilationConfigurationFromTemplate<KtsPluginBuilder> {
            defaultImports(arrayListOf(*imports))
        }
        val evaluationEnv = ScriptEvaluationConfiguration {
            jvm {
                baseClassLoader(loader)
            }
            constructorArgs(emptyArray<String>())
            enableScriptsInstancesSharing()
        }
        return BasicJvmScriptingHost().eval(file.toScriptSource(), scriptDef, evaluationEnv)
    }

    fun execute(): T? = when (val result = execute0()) {
        is ResultWithDiagnostics.Failure -> TODO()
        is ResultWithDiagnostics.Success<EvaluationResult> -> {
            with(result.value.returnValue) {
                when (this) {
                    is ResultValue.Value -> {
                        this.value as T?
                    }
                    is ResultValue.Error -> {
                        throw this.error
                    }
                    else -> TODO()
                }
            }
        }
    }

}

fun <T> executeScript(src: File, loader: ClassLoader, vararg import: String): T? {
    var result: T? = null
    //Thread {
    //  Thread.currentThread().contextClassLoader = loader
    result = ScriptExecutor<T>(src, *import, loader = loader).execute()
    //}.run {
    // start()
    //  join()
    //}
    return result
}
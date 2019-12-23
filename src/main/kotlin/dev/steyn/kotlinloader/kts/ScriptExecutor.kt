package dev.steyn.kotlinloader.kts

import java.io.Reader
import javax.script.Compilable
import javax.script.ScriptEngineManager

class ScriptExecutor<T> (
        val source: Reader,
        val engine: Compilable = ScriptEngineManager().getEngineByExtension("kts") as Compilable

){

    fun execute() : T {
        val script = engine.compile(source)
        return script.eval() as T
    }




}
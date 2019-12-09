package dev.steyn.kotlinloader.spigot.edit

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassReader.EXPAND_FRAMES
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

class ClassManager(val data: ByteArray, val name: String) : ClassVisitor(Opcodes.ASM7) {

    val reader = ClassReader(data)
    val pipeline = arrayListOf<ClassHandler>()

    init {
        reader.accept(this, EXPAND_FRAMES)
    }

    fun perform() {
        var data = this.data
        this.pipeline.forEach {
            data = it.handle(data)
        }
    }

}
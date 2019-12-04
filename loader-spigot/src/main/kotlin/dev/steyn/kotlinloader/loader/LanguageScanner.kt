package dev.steyn.kotlinloader.loader

import dev.steyn.kotlinloader.api.KotlinPlugin
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassReader.EXPAND_FRAMES
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes


class LanguageScanner(val data: ByteArray) : ClassVisitor(ASM_API_VERSION) {

    companion object {
        private val ASM_API_VERSION: Int = Opcodes.ASM7
    }

    fun isKotlinPlugin() : Boolean {
        val reader = ClassReader(data)
        reader.accept(this, EXPAND_FRAMES)
        val superClass = reader.superName
        return superClass.contains("KotlinPlugin")
    }


}
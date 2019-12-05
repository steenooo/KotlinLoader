package dev.steyn.kotlinloader.loader.reflect

import org.bukkit.plugin.PluginDescriptionFile
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassReader.EXPAND_FRAMES
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import java.io.File
import java.util.jar.JarFile


class LanguageScanner(val data: ByteArray) : ClassVisitor(ASM_API_VERSION) {


    companion object {
        private val ASM_API_VERSION: Int = Opcodes.ASM7


        fun createScanner(file: File, descriptionFile: PluginDescriptionFile) : LanguageScanner {
            return JarFile(file).use { LanguageScanner(it.readClass(descriptionFile.main)) }
        }

    }

    fun isKotlinPlugin() : Boolean {
        val reader = ClassReader(data)
        reader.accept(this, EXPAND_FRAMES)
        val superClass = reader.superName
        return superClass.contains("KotlinPlugin")
    }


}
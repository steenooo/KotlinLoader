package dev.steyn.kotlinloader.loader.reflect

import dev.steyn.kotlinloader.desc.KotlinPluginDescription
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassReader.EXPAND_FRAMES
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import java.io.File
import java.util.jar.JarFile


class LanguageScanner(data: ByteArray) : ClassVisitor(ASM_API_VERSION) {


    companion object {
        private const val ASM_API_VERSION: Int = Opcodes.ASM7

        private const val KOTLIN_PLUGIN = "dev/steyn/kotlinloader/KotlinPlugin"
        private const val KOTLIN_ANNOTATION = "Ldev/steyn/kotlinloader/Kotlin;"

        fun createScanner(file: File, descriptionFile: KotlinPluginDescription): LanguageScanner {
            return JarFile(file).use { LanguageScanner(it.readClass(descriptionFile.main)) }
        }

    }

    init {
        val reader = ClassReader(data)
        reader.accept(this, EXPAND_FRAMES)
    }

    var extendsKotlin: Boolean = false
    var hasKotlinAnnotation: Boolean = false



    override fun visit(version: Int, access: Int, name: String?, signature: String?, superName: String?, interfaces: Array<out String>?) {
        if(superName.equals(KOTLIN_PLUGIN)) {
            extendsKotlin = true
        }
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor? {
        if(descriptor.equals(KOTLIN_ANNOTATION)) {
            hasKotlinAnnotation = true
            //We'll assume that this class is a subclass of KotlinPlugin
        }
        return super.visitAnnotation(descriptor, visible)
    }


    fun isKotlinPlugin() : Boolean {
        return extendsKotlin || hasKotlinAnnotation
    }



}
package dev.steyn.kotlinloader.event

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassReader.EXPAND_FRAMES
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.ClassWriter.COMPUTE_FRAMES
import org.objectweb.asm.ClassWriter.COMPUTE_MAXS

object EventManager {

    fun translateEvent(name: String, bytes: ByteArray): ByteArray {

        val reader = ClassReader(bytes)
        val scanner = EventScanner()

        reader.accept(scanner, EXPAND_FRAMES)
        if (!scanner.read) {
            return bytes
        }
        val writer = ClassWriter(COMPUTE_FRAMES or COMPUTE_MAXS)
        reader.accept(EventTranslator(scanner, writer), EXPAND_FRAMES)


        return writer.toByteArray()
    }

}
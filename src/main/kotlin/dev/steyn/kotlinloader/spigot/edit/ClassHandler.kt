package dev.steyn.kotlinloader.spigot.edit

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor


interface ClassHandler {


    fun handle(byteArray: ByteArray, name: String) : ByteArray
}
package dev.steyn.kotlinloader.spigot.api

import org.objectweb.asm.ClassReader

interface ClassHandler {



    fun handle(ctx: HandlerContext) : ByteArray

    class HandlerContext(
            val data: ByteArray,
            val name: String,
            val reader: ClassReader
    )

}
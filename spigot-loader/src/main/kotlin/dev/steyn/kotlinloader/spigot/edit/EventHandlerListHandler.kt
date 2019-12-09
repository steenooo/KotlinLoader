package dev.steyn.kotlinloader.spigot.edit

import org.bukkit.event.HandlerList
import org.objectweb.asm.*
import org.objectweb.asm.ClassReader.EXPAND_FRAMES
import org.objectweb.asm.Opcodes.*

object EventHandlerListHandler : ClassHandler {


    val HANDLER_LIST_METHOD = "getHandlerList"
    val HANDLER_LIST_TYPE = Type.getDescriptor(HandlerList::class.java)
    val HANDLER_LIST_DESC = "()${HANDLER_LIST_TYPE}"

    override fun handle(byteArray: ByteArray): ByteArray {

        val reader = ClassReader(byteArray)
        val writer = ClassWriter(Opcodes.ASM7)
        reader.accept(writer, EXPAND_FRAMES)

        val mv = writer.visitMethod(
                ACC_PUBLIC,
                HANDLER_LIST_METHOD,
                HANDLER_LIST_DESC,
                null, null)

        mv.visitCode()
        mv.visitFieldInsn(GETSTATIC, null,
                "dev/steyn/kotlinloader/spigot/edit/TestEvent.LIST",
                "Lorg/bukkit/event/HandlerList")
        mv.visitEnd()
    }
}
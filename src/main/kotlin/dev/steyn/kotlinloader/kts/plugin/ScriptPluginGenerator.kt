package dev.steyn.kotlinloader.kts.plugin

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes.*


object ScriptPluginGenerator {

    fun generate(ktsPluginBuilder: KtsPluginBuilder) : ByteArray {
        val cw = ClassWriter(0)
        cw.visit(52, ACC_PUBLIC + ACC_SUPER + ACC_FINAL, ktsPluginBuilder.getMainClass().replace(".", "/"),
                null, "dev/steyn/kotlinloader/kts/KtsPlugin", null)
            val mv = cw.visitMethod(ACC_PRIVATE, "<init>",
                    "(Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;)V",
                    "(Lkotlin/jvm/functions/Function1<-Ldev/steyn/kotlinloader/kts/KtsPlugin;Lkotlin/Unit;>;Lkotlin/jvm/functions/Function1<-Ldev/steyn/kotlinloader/kts/KtsPlugin;Lkotlin/Unit;>;Lkotlin/jvm/functions/Function1<-Ldev/steyn/kotlinloader/kts/tsPlugin;Lkotlin/Unit;>;)V",
                    null)
            mv.visitCode()
            val l0 = Label()
            mv.visitLabel(l0)
            mv.visitLineNumber(13, l0)
            mv.visitVarInsn(ALOAD, 0)
            mv.visitVarInsn(ALOAD, 1)
            mv.visitVarInsn(ALOAD, 2)
            mv.visitVarInsn(ALOAD, 3)
            mv.visitMethodInsn(INVOKESPECIAL, "dev/steyn/kotlinloader/kts/KtsPlugin",
                    "<init>",
                    "(Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;)V",
                    false)
            val l1 = Label()
            mv.visitLabel(l1)
            mv.visitLineNumber(14, l1)
            mv.visitInsn(RETURN)
            val l2 = Label()
            mv.visitLabel(l2)
            mv.visitLocalVariable("this", "Ldev/steyn/kotlinloader/kts/plugin/GeneratedKtsPlugin;",
                    null, l0, l2, 0)
            mv.visitLocalVariable("onEnable", "Lkotlin/jvm/functions/Function1;",
                    "Lkotlin/jvm/functions/Function1<-Ldev/steyn/kotlinloader/kts/KtsPlugin;Lkotlin/Unit;>;",
                    l0, l2, 1)
            mv.visitLocalVariable("onDisable", "Lkotlin/jvm/functions/Function1;",
                    "Lkotlin/jvm/functions/Function1<-Ldev/steyn/kotlinloader/kts/KtsPlugin;Lkotlin/Unit;>;",
                    l0, l2, 2)
            mv.visitLocalVariable("onLoad", "Lkotlin/jvm/functions/Function1;",
                    "Lkotlin/jvm/functions/Function1<-Ldev/steyn/kotlinloader/kts/KtsPlugin;Lkotlin/Unit;>;",
                    l0, l2, 3)
            mv.visitMaxs(4, 4)
            mv.visitEnd()

        cw.visitEnd()
        return cw.toByteArray()
    }
}
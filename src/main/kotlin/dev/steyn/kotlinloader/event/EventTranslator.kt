package dev.steyn.kotlinloader.event

import dev.steyn.kotlinloader.event.Constants.B_EVENT_NAME
import dev.steyn.kotlinloader.event.Constants.EVENT
import dev.steyn.kotlinloader.event.Constants.GETHANDLERLIST_DESCRIPTOR
import dev.steyn.kotlinloader.event.Constants.GETHANDLERLIST_METHODNAME
import dev.steyn.kotlinloader.event.Constants.GETHANDLERS_DESCRIPTOR
import dev.steyn.kotlinloader.event.Constants.GETHANDLERS_METHODNAME
import dev.steyn.kotlinloader.event.Constants.HANDLERLIST_DESCRIPTOR
import dev.steyn.kotlinloader.event.Constants.HANDLERLIST_NAME
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*


class EventTranslator(val scanner: EventScanner, visitor: ClassVisitor) : ClassVisitor(ASM7, visitor) {

    override fun visit(version: Int, access: Int, name: String?, signature: String?, superName: String?, interfaces: Array<out String>?) {
        if (superName == EVENT) {
            super.visit(version, access, name, signature, B_EVENT_NAME, interfaces)
        } else {
            super.visit(version, access, name, signature, superName, interfaces)
        }
    }

    override fun visitMethod(access: Int, name: String?, descriptor: String?, signature: String?, exceptions: Array<out String>?): MethodVisitor {
        if(name == "<init>") {
            return object : MethodVisitor(ASM7, super.visitMethod(access, name, descriptor, signature, exceptions)) {
                override fun visitMethodInsn(opcode: Int, owner: String?, name: String?, descriptor: String?, isInterface: Boolean) {
                    if(owner == EVENT) {
                        super.visitMethodInsn(opcode, B_EVENT_NAME, name, descriptor, isInterface)
                    } else {
                        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
                    }
                }

            }
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }

    override fun visitEnd() {
        if(scanner.isAbstract) {
            super.visitEnd()
            return
        }
        val constantName = if (scanner.foundConstantConflict) "\$HANDLER_LIST" else "HANDLER_LIST"
        if (!scanner.foundConstant) {
            visitField(ACC_STATIC + ACC_FINAL, constantName, HANDLERLIST_DESCRIPTOR, null, null).apply {
                visitEnd()
            }
        }
        if (!scanner.foundGet) {
            visitMethod(ACC_FINAL + ACC_SYNTHETIC + ACC_PUBLIC, GETHANDLERS_METHODNAME, GETHANDLERS_DESCRIPTOR, null, null).apply {
                visitCode()
                val l0 = Label()
                visitLabel(l0)
                visitLineNumber(12, l0)
                visitFieldInsn(GETSTATIC, scanner.className,
                        constantName, "Lorg/bukkit/event/HandlerList;")
                visitInsn(ARETURN)
                val l1 = Label()
                visitLabel(l1)
                visitLocalVariable("this", "L${scanner.className};", null, l0, l1, 0)
                visitMaxs(1, 1)
                visitEnd()
            }
        }
        if (!scanner.foundStaticGet) {
            visitMethod(ACC_FINAL + ACC_SYNTHETIC + ACC_PUBLIC + ACC_STATIC, GETHANDLERLIST_METHODNAME, GETHANDLERLIST_DESCRIPTOR, null, null).apply {
                visitCode()
                val l0 = Label()
                visitLabel(l0)
                visitFieldInsn(GETSTATIC, scanner.className,
                        constantName, "Lorg/bukkit/event/HandlerList;")
                visitInsn(ARETURN)
                visitMaxs(1, 0)
                visitEnd()
            }
        }
        if (!scanner.foundCLInit) {
            visitMethod(ACC_STATIC, "<clinit>", "()V", null, null).apply {
                visitCode()
                val l0 = Label()
                visitLabel(l0)
                visitTypeInsn(NEW, HANDLERLIST_NAME)
                visitInsn(DUP)
                visitMethodInsn(INVOKESPECIAL, HANDLERLIST_NAME, "<init>", "()V",
                        false)
                visitFieldInsn(PUTSTATIC, scanner.className,
                        constantName, HANDLERLIST_DESCRIPTOR);
                visitInsn(RETURN)
                visitMaxs(2, 0)
                visitEnd()
            }
        }
        super.visitEnd()
    }
}





package dev.steyn.kotlinloader.event

import dev.steyn.kotlinloader.event.Constants.CINIT_METHOD
import dev.steyn.kotlinloader.event.Constants.CINIT_METHOD_DESC
import dev.steyn.kotlinloader.event.Constants.EVENT
import dev.steyn.kotlinloader.event.Constants.EVENT_DESC
import dev.steyn.kotlinloader.event.Constants.GETHANDLERLIST_DESCRIPTOR
import dev.steyn.kotlinloader.event.Constants.GETHANDLERLIST_METHODNAME
import dev.steyn.kotlinloader.event.Constants.GETHANDLERS_DESCRIPTOR
import dev.steyn.kotlinloader.event.Constants.GETHANDLERS_METHODNAME
import dev.steyn.kotlinloader.event.Constants.HANDLERLIST_DESCRIPTOR
import dev.steyn.kotlinloader.event.Constants.HANDLERLIST_NAME
import dev.steyn.kotlinloader.notNullAndEquals
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.lang.reflect.Modifier


class EventScanner : ClassVisitor(Opcodes.ASM7) {

    var version: Int = 52
    var signature: String? = null
    var className: String? = null

    var read: Boolean = false
    var foundGet: Boolean = false
    var foundStaticGet: Boolean = false
    var foundConstant: Boolean = false
    var foundConstantConflict: Boolean = false
    var foundCInit: Boolean = false


    override fun visit(version: Int, access: Int, name: String?, signature: String?, superName: String?, interfaces: Array<out String>?) {
       if(superName != null) {
           if(superName == EVENT) {
               read = true
           }
       }
        if(read) {
            this.version = version
            this.signature = signature
            this.className = name
        }
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitField(access: Int, name: String?, descriptor: String?, signature: String?, value: Any?): FieldVisitor? {
        if(name.notNullAndEquals(HANDLERLIST_NAME)) {

            if(descriptor.notNullAndEquals(HANDLERLIST_DESCRIPTOR) && Modifier.isStatic(access)) {
                foundConstant = true
            } else {
                foundConstantConflict = true
            }
        }
        return super.visitField(access, name, descriptor, signature, value)
    }

    override fun visitMethod(access: Int, name: String?, descriptor: String?, signature: String?, exceptions: Array<out String>?): MethodVisitor? {
        if(name.notNullAndEquals(CINIT_METHOD)) {
            if(descriptor.notNullAndEquals(CINIT_METHOD_DESC)) {
                foundCInit = true
            }
        }
        if (name.notNullAndEquals(GETHANDLERLIST_METHODNAME)) {
            if (descriptor.notNullAndEquals(GETHANDLERLIST_DESCRIPTOR)) {
                if (Modifier.isStatic(access)) {
                    foundStaticGet = true
                }
            }
        }
        if(name.notNullAndEquals(GETHANDLERS_METHODNAME)) {
            if(descriptor.notNullAndEquals(GETHANDLERS_DESCRIPTOR)) {
                foundGet = true
            }
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }

}
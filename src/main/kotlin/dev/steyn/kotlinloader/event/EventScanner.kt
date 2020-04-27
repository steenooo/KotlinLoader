package dev.steyn.kotlinloader.event

import dev.steyn.kotlinloader.event.Constants.CLINIT_METHOD
import dev.steyn.kotlinloader.event.Constants.CLINIT_METHOD_DESC
import dev.steyn.kotlinloader.event.Constants.EVENT
import dev.steyn.kotlinloader.event.Constants.GENHANDLERS_DESC
import dev.steyn.kotlinloader.event.Constants.GETHANDLERLIST_DESCRIPTOR
import dev.steyn.kotlinloader.event.Constants.GETHANDLERLIST_METHODNAME
import dev.steyn.kotlinloader.event.Constants.GETHANDLERS_DESCRIPTOR
import dev.steyn.kotlinloader.event.Constants.GETHANDLERS_METHODNAME
import dev.steyn.kotlinloader.event.Constants.HANDLERLIST_DESCRIPTOR
import dev.steyn.kotlinloader.event.Constants.HANDLERLIST_NAME
import dev.steyn.kotlinloader.notNullAndEquals
import org.objectweb.asm.*
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
    var foundCLInit: Boolean = false
    var isAbstract: Boolean = false


    override fun visit(version: Int, access: Int, name: String?, signature: String?, superName: String?, interfaces: Array<out String>?) {
        if (superName != null) {
            if (superName == EVENT) {
                read = true
            }
        }
        if (Modifier.isAbstract(access)) {
            isAbstract = true
        }
        this.version = version
        this.signature = signature
        this.className = name
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor? {
        if (descriptor.notNullAndEquals(GENHANDLERS_DESC)) {
            read = true
        }
        return super.visitAnnotation(descriptor, visible)
    }

    override fun visitField(access: Int, name: String?, descriptor: String?, signature: String?, value: Any?): FieldVisitor? {
        if (name.notNullAndEquals(HANDLERLIST_NAME)) {

            if (descriptor.notNullAndEquals(HANDLERLIST_DESCRIPTOR) && Modifier.isStatic(access)) {
                foundConstant = true
            } else {
                foundConstantConflict = true
            }
        }
        return super.visitField(access, name, descriptor, signature, value)
    }

    override fun visitMethod(access: Int, name: String?, descriptor: String?, signature: String?, exceptions: Array<out String>?): MethodVisitor? {
        if (name.notNullAndEquals(CLINIT_METHOD)) {
            if (descriptor.notNullAndEquals(CLINIT_METHOD_DESC)) {
                foundCLInit = true
            }
        }
        if (name.notNullAndEquals(GETHANDLERLIST_METHODNAME)) {
            if (descriptor.notNullAndEquals(GETHANDLERLIST_DESCRIPTOR)) {
                if (Modifier.isStatic(access)) {
                    foundStaticGet = true
                }
            }
        }
        if (name.notNullAndEquals(GETHANDLERS_METHODNAME)) {
            if (descriptor.notNullAndEquals(GETHANDLERS_DESCRIPTOR)) {
                foundGet = true
            }
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }

}
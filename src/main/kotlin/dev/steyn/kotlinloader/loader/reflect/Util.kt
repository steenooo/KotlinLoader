package dev.steyn.kotlinloader.loader.reflect

import com.google.common.io.ByteStreams
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.jar.JarEntry
import java.util.jar.JarFile
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


//fun Field.makeMutable(): Boolean {
//    try {
//        val modifiersField = javaClass.getDeclaredField("modifiers")
//        modifiersField.isAccessible = true
//
//        modifiersField.setInt(this, (modifiers and Modifier.FINAL.inv()))
//    } catch (e: Exception) {
//        e.printStackTrace()
//        return false
//
//    }
//    return true
//}


private fun JarFile.getClass(name: String): JarEntry {
    val entryName = name.replace('.', '/') + ".class"
    return getJarEntry(entryName)
}

internal fun JarFile.readClass(name: String) = this.getInputStream(getClass(name)).use {
    ByteStreams.toByteArray(it)
}

fun <R> reflect(target: Any?, field: Field) = ReflectiveFieldDelegation<R>(target, field)
fun <R> reflect(target: Any?, field: () -> Field) = ReflectiveFieldDelegation<R>(target, field())

fun <R> reflectLazy(target: Any?, field: Field) = LazyReflectiveFieldDelegation<R>(target, field)
fun <R> reflectLazy(target: Any?, field: () -> Field) = LazyReflectiveFieldDelegation<R>(target, field())

fun <R> reflectMutable(target: Any?, field: Field) = MutableReflectiveFieldDelegation<R>(target, field)
fun <R> reflectMutable(target: Any?, field: () -> Field) = MutableReflectiveFieldDelegation<R>(target, field())

open class ReflectiveFieldDelegation<R : Any?>(val target: Any?, val field: Field) : ReadOnlyProperty<Any, R> {

    init {
        field.isAccessible = true
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): R {
        return field.get(target) as R
    }
}

class LazyReflectiveFieldDelegation<R : Any?>(val target: Any?, val field: Field) : ReadOnlyProperty<Any, R> {

    init {
        field.isAccessible = true
    }
    private val value: R by lazy {
        field.get(target) as R
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): R {
        return value
    }
}

class MutableReflectiveFieldDelegation<R : Any?>(target: Any?, field: Field) : ReflectiveFieldDelegation<R>(target, field), ReadWriteProperty<Any, R>  {
// Not sure why this no longer necessary, but it seems to work.
//    init {
//        field.makeMutable()
//    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: R) {
        field.set(target, value)
    }
}



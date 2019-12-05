package dev.steyn.kotlinloader.loader.reflect

import com.google.common.io.ByteStreams
import dev.steyn.kotlinloader.KotlinLoader
import java.lang.Exception
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.jar.JarEntry
import java.util.jar.JarFile


fun Field.makeMutable() : Boolean {
    try {
        val modifiersField = javaClass.getDeclaredField("modifiers")
        modifiersField.isAccessible = true

        modifiersField.setInt(this, (modifiers and Modifier.FINAL.inv()))
    } catch(e: Exception) {
        e.printStackTrace()
        return false

    }
    return true
}


fun JarFile.getClass(name: String) : JarEntry {
    val entryName = name.replace('.', '/') + ".class"
    return getJarEntry(entryName)
}

fun JarFile.readClass(name: String) = this.getInputStream(getClass(name)).use {

        ByteStreams.toByteArray(it)
    }


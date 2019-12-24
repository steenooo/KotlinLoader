package dev.steyn.kotlinloader.plugin

import dev.steyn.kotlinloader.bootstrap.KotlinLoaderPlugin
import dev.steyn.kotlinloader.loader.KotlinInjector
import org.bukkit.ChatColor.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class KotlinCommand : CommandExecutor {

    companion object {
        val LINE = "${GRAY}[${AQUA}KotlinLoader${GRAY}]${STRIKETHROUGH} ---------------------------"
    }

    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {

        fun sendLine(prefix: String, value: Any) {
            sender.sendMessage("    ${GRAY}${prefix}:${AQUA} $value")
        }
        sender.sendMessage(LINE)
        sendLine("Plugin", KotlinLoaderPlugin.getInstance().description.version)
        sendLine("Kotlin", KotlinVersion.CURRENT)
        KotlinInjector.kotlinPluginLoader.plugins.let {a ->
            sendLine("Plugins ${GRAY}(${AQUA}${a.size}${GRAY})", a.map { it.name }.joinToString("${GRAY},${AQUA} "))
        }
        sender.sendMessage(LINE)
        return true
    }

}
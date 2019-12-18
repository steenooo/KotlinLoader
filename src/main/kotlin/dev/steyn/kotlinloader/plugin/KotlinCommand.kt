package dev.steyn.kotlinloader.plugin

import dev.steyn.kotlinloader.KotlinPlugin
import dev.steyn.kotlinloader.bootstrap.KotlinLoaderPlugin
import org.bukkit.ChatColor.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class KotlinCommand : CommandExecutor {

    companion object {
        val LINE = "${GRAY}[${BLUE}KotlinLoader${GRAY}]${STRIKETHROUGH} --------------------------------------"
    }

    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {

        fun sendLine(prefix: String, value: String) {
            sender.sendMessage("${GRAY}${prefix}:${AQUA} $value")
        }
        sender.sendMessage(LINE)
        sendLine("Plugin", KotlinLoaderPlugin.getInstance().description.version)
        sendLine("Kotlin", KotlinVersion.CURRENT.toString())
        sendLine("Plugins", KotlinPlugin.COUNT.get().toString())
        sender.sendMessage(LINE)
        return true
    }

}
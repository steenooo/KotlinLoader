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

        val config = KotlinLoaderPlugin.getInstance().config
        sender.sendMessage(LINE)
        sendLine("Plugin", KotlinLoaderPlugin.getInstance().description.version)
        sender.sendMessage("${GRAY}Kotlin:")
        sendLine("  stdlib", config.getString("kotlin.library.stdlib")!!)
        sendLine("  coroutines", config.getString("kotlin.library.coroutines")!!)
        sendLine("  reflect", config.getString("kotlin.library.reflect")!!)
        sender.sendMessage("${GRAY}Plugins: ${AQUA}${KotlinPlugin.COUNT.get()}")
        sender.sendMessage(LINE)
        return true
    }

}
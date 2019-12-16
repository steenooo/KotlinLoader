package dev.steyn.kotlinloader

import dev.steyn.kotlinloader.api.KotlinPlugin
import org.bukkit.Color.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import java.awt.font.TextAttribute.STRIKETHROUGH

class KotlinCommand : CommandExecutor {

    companion object {
        val LINE = "${GRAY}[${BLUE}KotlinLoader${GRAY}]${STRIKETHROUGH} --------------------------------------"
    }

    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        sender.sendMessage(LINE)
        sender.sendMessage("${GRAY}Version: ${AQUA}${KotlinLoader.instance.description.version}")
        sender.sendMessage("${GRAY}Kotlin Version: ${KotlinVersion.CURRENT}")
        sender.sendMessage("${GRAY}Plugins: ${AQUA}${KotlinPlugin.COUNT.get()}")
        sender.sendMessage(LINE)

        return true;
    }
}
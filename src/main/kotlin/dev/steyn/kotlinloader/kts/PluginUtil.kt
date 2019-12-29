package dev.steyn.kotlinloader.kts

import dev.steyn.kotlinloader.kts.command.Commands
import dev.steyn.kotlinloader.kts.plugin.KtsPluginBuilder
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginCommand
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.plugin.EventExecutor

infix fun PluginCommand.with(action: Command.() -> Unit) {
    action(this)
}
fun plugin(x : KtsPluginBuilder.() -> Unit) : KtsPluginBuilder {
    val builder = KtsPluginBuilder()
    x(builder)
    return builder
}
inline fun <reified E : Event> KtsPlugin.listen(priority: EventPriority = EventPriority.NORMAL, crossinline handler: E.() -> Unit) {
    val executor = EventExecutor { _, event -> handler(event as E) }
    server.pluginManager.registerEvent(E::class.java, this, priority, executor, this)
}

fun KtsPlugin.command(name: String, exec: (CommandSender, Command, String, Array<out String>) -> Boolean): PluginCommand {
    return Commands.register(this, description.name, name, CommandExecutor { sender, command, label, args -> exec(sender, command, label, args) })
}
fun KtsPlugin.command(name: String, exec: (CommandSender, args: Array<out String>) -> Boolean) : PluginCommand {
    return command(name) { sender, _, _, args ->
        exec(sender, args)
    }
}
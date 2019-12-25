package dev.steyn.kotlinloader.kts

import dev.steyn.kotlinloader.KotlinPlugin
import dev.steyn.kotlinloader.exception.notAvailableInScript
import dev.steyn.kotlinloader.kts.command.Commands
import dev.steyn.kotlinloader.kts.plugin.KtsPluginBuilder
import dev.steyn.kotlinloader.kts.plugin.PluginInitializer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginCommand
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import java.io.InputStream

abstract class KtsPlugin internal constructor(
        internal val onEnable: PluginInitializer,
        internal val onDisable: PluginInitializer,
        internal val onLoad: PluginInitializer
) : KotlinPlugin(), Listener {

    override fun onEnable() {
        onEnable(this)
    }

    override fun onDisable() {
        onDisable(this)
    }

    override fun onLoad() {
        onLoad(this)
    }


    inline fun <reified E : Event> listen(priority: EventPriority = EventPriority.NORMAL, noinline handler: E.() -> Unit) {
        val executor = EventExecutor { _, event -> handler(event as E) }
        server.pluginManager.registerEvent(E::class.java, this, priority, executor, this)
    }

    fun command(name: String, exec: (CommandSender, Command, String, Array<out String>) -> Boolean): PluginCommand {
       return Commands.register(this, description.name, name, CommandExecutor { sender, command, label, args -> exec(sender, command, label, args) })
    }
    fun command(name: String, exec: (CommandSender, args: Array<out String>) -> Boolean) : PluginCommand {
        return command(name) { sender, _, _, args ->
            exec(sender, args)
        }
    }

    infix fun PluginCommand.with(action: Command.() -> Unit) {
        action(this)
    }

    override fun isScript() = true

    override fun getResource(filename: String): InputStream? {
        notAvailableInScript()
    }

    override fun saveResource(_resourcePath: String, replace: Boolean) {
        notAvailableInScript()
    }

    override fun saveDefaultConfig() {
        notAvailableInScript()
    }

    override fun getConfig(): FileConfiguration {
        notAvailableInScript()
    }

    override fun saveConfig() {
        notAvailableInScript()
    }


}
fun plugin(x : KtsPluginBuilder.() -> Unit) : KtsPluginBuilder {
    val builder = KtsPluginBuilder()
    x(builder)
    return builder
}

# KotlinLoader
[![](https://jitpack.io/v/steenooo/KotlinLoader.svg)](https://jitpack.io/#steenooo/KotlinLoader)\
Kotlin PluginLoader for Spigot.


### KotlinLoader
Kotlin is a wonderful growing programming language with support for the JVM.
This allows us to write Minecraft Plugins in Kotlin.
While this works fine, it does bring some inconviences.
To run kotlin, it is mandatory for the kotlin library files to be available at runtime.
While it is possible to provide these classes with the -classpath option, when starting the JVM, most spigot servers run in managed environments.
The go-to workaround for this problem is to add these library files to the final jar by shading them. 
This will increase your jar size alot. 

KotlinLoader provides a custom PluginLoader, that will provide the Kotlin library classes for you.\
 

#### Included Kotlin Libraries
- kotlin-stdlib
- kotlinx-coroutines
- kotlin-reflect

#### Pros
- Write Idiomatic Kotlin!
- The main class can be an `object` singleton, instead of a normal class.
- Decrease size of the final plugin jar.


#### Cons
- This plugin uses alot of reflection and injection hacks to allows Kotlin classes to be accessed by JavaPlugins
- Plugins extending KotlinPlugin should be located in the <server_root>/plugins/kotlin folder.


#### Roadmap
- Add support for .kts configuration
- Add support for annotation based plugin descriptions
- Add BungeeCord support

#### Usage
##### In the spigot server
Build or download the plugin and place it into your plugins folder like any other plugin.
Note that plugins running with this pluginloader should go into the <server_root>/plugins/kotlin folder!

##### When writing a plugin
The KotlinLoader offers two ways to write a plugin. 

A regelar KotlinPlugin should be written as any other Spigot Plugin. The main class should extend `KotlinPlugin`.
It is possible to use `object` for the main class.

KotlinLoader also provides the ability to run plugins from KotlinScript. 
KotlinScript Plugins are written in a DSL format. 
The script will be executed **before** the server is properly setup.\
Configuring the plugin should be done within the `plugin` tag.\
It is mandatory to provide a name and a version for the plugin, just like a normal plugin.yml.

The actual plugin class will be generated on runtime by the plugin. It is possible to provide a classname, but it is not mandatory.

Important notes when writing a KotlinScript Plugin:
- The actual plugin instance is only available within the `load`, `enable` and `disable` tag.
- Listening for events should be done with the `listen` function.
- Commands should be handled with the `command` function.
- Classes should be imported just like a normal plugin. 

It is currently not possible to import other scripts.


Example Script
````kotlin
import org.bukkit.ChatColor.BLUE
import org.bukkit.command.CommandSender
import org.bukkit.event.player.PlayerJoinEvent

plugin {
    name = "JoinMessage"
    version = "1.0"
    description = "Simple JoinMessage Plugin"

    val message = "${BLUE}Welcome to your server {name}!"

    enable {
        listen<PlayerJoinEvent> {
            joinMessage = message.replace("{name}", player.name)
        }

        command("testjoinmessage") { sender: CommandSender, args: Array<out String> ->
            sender.sendMessage(message.replace("{name}", sender.name))
            true
        } with {
            description = "Test the joinmessage"
            permission = "joinmessage.test"
            aliases = listOf("tjm", "testmessage")
        }
    }   
}
````

#### Compiling
KotlinLoader is a maven project. 
Use `mvn package` to build to project. 


#### Dependency Information
Maven
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.steenooo.KotlinLoader</groupId>
    <artifactId>kotlin-loader</artifactId>
    <version>1.0.1</version>
    <scope>provided</scope>
</dependency>
```

Gradle
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
dependencies {
	        implementation 'com.github.steenooo:kotlin-loader:1.0.1'
	}
```




This project was inspired by [ScalaLoader](https://github.com/Jannyboy11/ScalaPluginLoader)

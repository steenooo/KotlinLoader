
# KotlinLoader
[![](https://jitpack.io/v/steenooo/kotlin-loader.svg)](https://jitpack.io/#steenooo/kotlin-loader)\
Kotlin PluginLoader for Spigot.


### KotlinLoader
Kotlin is a wonderful growing programming language with support for the JVM.
This allows us to write Minecraft Plugins in Kotlin.
While this works fine, it does bring some inconviences.
To run kotlin, it is mandatory for the kotlin library files to be available at runtime.
While it is possible to provide these classes with the -classpath option, when starting the JVM, most spigot servers run in managed environments.
The go-to workaround for this problem is to add these library files to the final jar by shading them. 
This will increase your jar size alot. 

KotlinLoader provides a custom PluginLoader, that will provide the Kotlin library classes for you.

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

#### Usage
##### In the spigot server
Build or download the plugin and place it into your plugins folder like any other plugin.
Note that plugins running with this pluginloader should go into the <server_root>/plugins/kotlin folder!

##### When writing a plugin
A KotlinPlugin should be written as any other Spigot Plugin. The main class should extend `KotlinPlugin`.
It is possible to use `object` for the main class.  

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
    <groupId>com.github.steenooo.kotlin-loader</groupId>
    <artifactId>kotlin-loader</artifactId>
    <version>1.0.0</version>
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
	        implementation 'com.github.steenooo:kotlin-loader:Tag'
	}
```

package dev.steyn.kotlinloader.bootstrap;

import dev.steyn.kotlinloader.plugin.KotlinCommand;
import dev.steyn.kotlinloader.plugin.KotlinLoader;
import java.util.Objects;
import org.bukkit.plugin.java.JavaPlugin;

public class KotlinLoaderPlugin extends JavaPlugin {

    public static KotlinLoaderPlugin getInstance() {
        return JavaPlugin.getPlugin(KotlinLoaderPlugin.class);
    }


    @Override
    public void onLoad() {

        saveDefaultConfig();
        KotlinBootstrap bootstrap = new KotlinBootstrap();
        bootstrap.init(this);
        KotlinLoader loader = new KotlinLoader(this);
        loader.load();
    }

    @Override
    public void onEnable() {
        Objects.requireNonNull(getCommand("kotlinloader")).setExecutor(new KotlinCommand());
    }

    public ClassLoader getLoader() {
        return getClassLoader();
    }

}

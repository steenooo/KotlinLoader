package dev.steyn.kotlinloader.bootstrap;

import com.google.common.io.ByteStreams;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public class KotlinBootstrap {

    public void init(KotlinLoaderPlugin plugin) {
        plugin.getLogger().info("Loading libraries..");
        FileConfiguration config = plugin.getConfig();
        File libraries = new File(plugin.getDataFolder(), "libraries");
        if (!libraries.exists()) {
            libraries.mkdirs();
        }
        String repository = config.getString("kotlin.repository");
        List<String> dependencies = config.getStringList("kotlin.dependencies");
        for (String dependency : dependencies) {
            try {
                String url;
                String name;
                String[] parts = dependency.split(":");
                if (dependency.startsWith("maven")) {
                    String groupId = parts[1];
                    String artifactId = parts[2];
                    String version = parts[3];
                    name = parts[4];
                    url = String
                        .format("%s%s/%s/%s/%s", repository, groupId.replace(".", "/"), artifactId,
                            version, String.format("%s-%s.jar", artifactId, version));
                } else {
                    url = parts[0];
                    name = parts[1];
                }

                URL x = new URL(url);
                File file = new File(libraries, name);
                if (!file.exists()) {
                    file.createNewFile();
                    plugin.getLogger().info(String.format("Downloading %s %s..", name, url));
                    try (InputStream inputStream = x.openStream()) {
                        try (BufferedInputStream in = new BufferedInputStream(inputStream)) {
                            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                                try (BufferedOutputStream out = new BufferedOutputStream(
                                    outputStream)) {
                                    ByteStreams.copy(in, out);
                                }
                            }
                        }
                    }
                }
                addFileToLoader(Bukkit.class.getClassLoader(), file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    private void addFileToLoader(ClassLoader loader, File file) throws Exception {
        Method m = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        m.setAccessible(true);
        m.invoke(loader, file.toURI().toURL());
    }

}




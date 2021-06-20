package me.pixelizedgaming.hidenseek.files;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {

    private static File file;
    private static FileConfiguration customFile;

    public static FileConfiguration get() {
        return customFile;
    }

    public static void save() {
        try {
            customFile.save(file);
        } catch (IOException e) {
            System.err.println("Couldn't save config! Is something wrong?");
        }
    }

    public static void reload(){
        customFile = YamlConfiguration.loadConfiguration(file);
    }

    public static void setup(){
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("HideNSeek").getDataFolder(), "config.yml");
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Couldn't create config!");
            }
        }
        customFile = YamlConfiguration.loadConfiguration(file);
    }
}

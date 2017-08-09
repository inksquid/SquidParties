package me.inksquid.squidparties.handlers;

import me.inksquid.squidparties.SquidParties;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigHandler {

    private FileConfiguration config;
    private File file;

    public ConfigHandler(File folder, String name) {
        this.file = new File(folder, name);
        reloadConfig();
    }

    public void saveConfig() {
        try {
            config.save(file);
        } catch (Exception e) {
            SquidParties.getInstance().getLogger().severe
                    (String.format("Couldn't save '%s', because: '%s'", file.getName(), e.getMessage()));
        }
    }


    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    public <T> void set(String path, T value, boolean save) {
        config.set(path, value);

        if (save) saveConfig();
    }

    public <T> void set(String path, T value) {
        set(path, value, false);
    }

    public boolean getBoolean(String path, boolean revert) {
        return config.getBoolean(path, revert);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public File getFile() {
        return file;
    }
}

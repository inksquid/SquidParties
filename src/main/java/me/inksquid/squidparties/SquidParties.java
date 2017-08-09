package me.inksquid.squidparties;

import com.vexsoftware.votifier.model.VotifierEvent;
import me.inksquid.squidparties.event.PartyEvent;
import me.inksquid.squidparties.handlers.ConfigHandler;
import me.inksquid.squidparties.reward.Reward;
import me.inksquid.squidparties.util.PartyUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStreamReader;

public final class SquidParties extends JavaPlugin implements Listener {

    private static SquidParties instance;
    private static ConfigHandler players, rewards;
    private static boolean running = false;
    private int time = 11;

    @Override
    public void onEnable() {
        instance = this;
        getConfig().options().copyDefaults(true);
        saveConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }


    @Override
    public void reloadConfig() {
        File folder = getDataFolder();

        super.reloadConfig();
        players = new ConfigHandler(folder, "players.yml");
        rewards = new ConfigHandler(folder, "rewards.yml");
        Config.loadConfig(getConfig());

        FileConfiguration plugin = YamlConfiguration.loadConfiguration(new InputStreamReader(getResource("plugin.yml")));

        for (String command : plugin.getConfigurationSection("commands").getKeys(false)) {
            getCommand(command).setPermissionMessage(Config.getNoPermMessage());
        }
    }

    public void startCountdown() {
        time = 11;
        running = true;
        getServer().getScheduler().cancelTasks(this);
        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> countdown(), 0L, 20L);
    }

    public void countdown() {
        time--;

        if (time == 10 || (time < 6 && time > 0)) {
            getServer().broadcastMessage(Config.getCountMessage().replace("<time>", String.valueOf(time)));
        } else if (time == 0) {
            startDp();
        }
    }

    public void startDp() {
        getServer().broadcastMessage(Config.getStartMessage());
        getServer().getScheduler().cancelTasks(this);
        getServer().getScheduler().runTaskLater(this, () -> stopDp(), Config.getLength());
        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> giveRewards(), 0L, Config.getRewardDelay());
    }

    public void stopDp() {
        running = false;
        getServer().broadcastMessage(Config.getStopMessage());
        getServer().getScheduler().cancelTasks(this);
    }

    public void giveRewards() {
        if (running) {
            for (Player player : getServer().getOnlinePlayers()) {
                if (players.getBoolean(player.getUniqueId().toString(), true)) {
                    Reward reward = Config.getReward();

                    if (reward.hasCommands()) {
                        PartyUtil.executeCommands(player, reward.getCommands());
                    }

                    if (reward.hasItems()) {
                        player.getInventory().addItem(PartyUtil.cloneItems(reward.getItems()));
                    }

                    //PartyUtil.spawnFirework(player.getLocation(), PartyUtil.createRandomFirework(), PartyUtil.getRandomPower());
                    PartyUtil.playSounds(player, Config.getSounds());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onVotifier(VotifierEvent event) {
        new Thread(() -> {
            int votes = Config.getVotes() + 1;

            if (!running && votes >= Config.getVotesRequired()) {
                votes = 0;
                startCountdown();
            }

            Config.setVotes(votes);
            getConfig().set("settings.votes", votes);
            saveConfig();
            getServer().getPluginManager().callEvent(new PartyEvent(Config.getVotesRequired()));
        }).start();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        new Thread(() -> {
            if (commandLabel.equalsIgnoreCase("squidparty")) {
                if (args.length == 0) {
                    PartyUtil.sendMessages(sender, Config.getHelpMessage());
                } else if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("squidparties.reload")) {
                        reloadConfig();
                        sender.sendMessage(Config.getReloadMessage());
                    } else {
                        sender.sendMessage(Config.getNoPermMessage());
                    }
                } else if (args[0].equalsIgnoreCase("start")) {
                    if (sender.hasPermission("squidparties.start")) {
                        startCountdown();
                    } else {
                        sender.sendMessage(Config.getNoPermMessage());
                    }
                } else if (args[0].equalsIgnoreCase("stop")) {
                    if (sender.hasPermission("squidparties.stop")) {
                        stopDp();
                    } else {
                        sender.sendMessage(Config.getNoPermMessage());
                    }
                } else if (args[0].equalsIgnoreCase("ponyland")) {
                    sender.sendMessage("So here is the stuff in the map..");

                    for (Reward reward : Config.getRewards().getMap().values()) {
                        for (ItemStack item : reward.getItems()) {
                            sender.sendMessage("material = " + item.getType().name());
                            sender.sendMessage("amount = " + item.getAmount());
                        }
                    }
                } else {
                    PartyUtil.sendMessages(sender, Config.getHelpMessage());
                }
            } else if (commandLabel.equalsIgnoreCase("dp")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    String uuid = player.getUniqueId().toString();

                    players.set(uuid, !players.getBoolean(uuid, true), true);
                    player.sendMessage(players.getBoolean(uuid, true) ? Config.getEnableMessage() : Config.getDisableMessage());
                } else {
                    sender.sendMessage(Config.getConsoleMessage());
                }
            }
        }).start();

        return true;
    }

    public static ConfigHandler getPlayers() {
        return players;
    }

    public static ConfigHandler getRewards() {
        return rewards;
    }

    public static boolean isRunning() {
        return running;
    }

    public static void setRunning(boolean running) {
        SquidParties.running = running;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public static SquidParties getInstance() {
        return instance;
    }
}

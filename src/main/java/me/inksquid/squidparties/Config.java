package me.inksquid.squidparties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.inksquid.squidparties.reward.Reward;
import me.inksquid.squidparties.util.PartyUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Config {

    private static ConfigurationSection general;
    private static ConfigurationSection settings;
    private static String reloadMessage;
    private static String noPermMessage;
    private static String startMessage;
    private static String stopMessage;
    private static String enableMessage;
    private static String disableMessage;
    private static String countMessage;
    private static String consoleMessage;
    private static List<String> helpMessage;
    private static int length;
    private static int rewardDelay;
    private static int votesRequired;
    private static List<Sound> sounds;
    private static int votes;
    private static RandomCollection<Reward> rewards = new RandomCollection<Reward>();

    public static void loadConfig(FileConfiguration config) {
        general = config.getConfigurationSection("general");
        settings = config.getConfigurationSection("settings");
        reloadMessage = PartyUtil.colorize(general.getString("reloadMessage"));
        noPermMessage = PartyUtil.colorize(general.getString("noPermMessage"));
        startMessage = PartyUtil.colorize(general.getString("startMessage"));
        stopMessage = PartyUtil.colorize(general.getString("stopMessage"));
        enableMessage = PartyUtil.colorize(general.getString("enableMessage"));
        disableMessage = PartyUtil.colorize(general.getString("disableMessage"));
        countMessage = PartyUtil.colorize(general.getString("countMessage"));
        consoleMessage = PartyUtil.colorize(general.getString("consoleMessage"));
        helpMessage = PartyUtil.colorize(general.getStringList("helpMessage"));
        length = settings.getInt("length") * 20;
        rewardDelay = settings.getInt("rewardDelay") * 20;
        votesRequired = settings.getInt("votesRequired");
        sounds = PartyUtil.parseSounds(settings.getStringList("sounds"));
        votes = settings.getInt("votes");

        loadRewards(SquidParties.getRewards().getConfig());
    }

    public static void loadRewards(FileConfiguration config) {
        rewards.clear();

        for (String key : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(key);

            if (section != null) {
                double chance = section.getDouble("chance");

                if (chance > 0.0) {
                    List<ItemStack> items = new ArrayList<ItemStack>();
                    ConfigurationSection allItems = section.getConfigurationSection("items");

                    if (allItems != null) {
                        for (String key2 : allItems.getKeys(false)) {
                            ConfigurationSection itemSection = allItems.getConfigurationSection(key2);

                            if (itemSection != null) {
                                Material material = PartyUtil.parseMaterial(itemSection.getString("material"));

                                if (material != null && !material.equals(Material.AIR)) {
                                    int amount = itemSection.getInt("amount", 1);
                                    short damage = (short) itemSection.getInt("damage");
                                    String name = PartyUtil.colorize(itemSection.getString("name"));
                                    List<String> lore = PartyUtil.colorize(itemSection.getStringList("lore"));
                                    Map<Enchantment, Integer> enchants = PartyUtil.parseEnchants(itemSection.getStringList("enchants"));
                                    ItemStack item = new ItemStack(material, amount, damage);
                                    ItemMeta meta = item.getItemMeta();

                                    if (name != null && !name.isEmpty()) {
                                        meta.setDisplayName(name);
                                    }

                                    if (lore != null && !lore.isEmpty()) {
                                        meta.setLore(lore);
                                    }

                                    item.setItemMeta(meta);

                                    if (enchants != null && !enchants.isEmpty()) {
                                        item.addUnsafeEnchantments(enchants);
                                    }

                                    items.add(item);
                                }
                            }
                        }
                    }

                    rewards.add(chance, new Reward(section.getStringList("rewards"), items.toArray(new ItemStack[items.size()])));
                }
            }
        }
    }

    public static ConfigurationSection getGeneral() {
        return general;
    }

    public static ConfigurationSection getSettings() {
        return settings;
    }

    public static String getReloadMessage() {
        return reloadMessage;
    }

    public static String getNoPermMessage() {
        return noPermMessage;
    }

    public static String getStartMessage() {
        return startMessage;
    }

    public static String getStopMessage() {
        return stopMessage;
    }

    public static String getEnableMessage() {
        return enableMessage;
    }

    public static String getDisableMessage() {
        return disableMessage;
    }

    public static String getCountMessage() {
        return countMessage;
    }

    public static String getConsoleMessage() {
        return consoleMessage;
    }

    public static List<String> getHelpMessage() {
        return helpMessage;
    }

    public static int getLength() {
        return length;
    }

    public static int getRewardDelay() {
        return rewardDelay;
    }

    public static int getVotesRequired() {
        return votesRequired;
    }

    public static List<Sound> getSounds() {
        return sounds;
    }

    public static int getVotes() {
        return votes;
    }

    public static RandomCollection<Reward> getRewards() {
        return rewards;
    }

    public static Reward getReward() {
        return rewards.next();
    }

    public static void setVotes(int votes) {
        Config.votes = votes;
    }
}

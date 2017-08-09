package me.inksquid.squidparties.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class PartyUtil {

    private static Random random = new Random();

    public static String colorize(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static List<String> colorize(List<String> list) {
        for (int i = 0, s = list.size(); i < s; i++) {
            list.set(i, colorize(list.get(i)));
        }

        return list;
    }

    public static void sendMessages(CommandSender sender, List<String> messages) {
        for (String message : messages) {
            sender.sendMessage(message);
        }
    }

    public static String convertToEnum(String string) {
        return string.toUpperCase().replace(" ", "_");
    }

    public static int parseInteger(String string, int fallback) {
        try {
            return Integer.valueOf(string);
        } catch (Exception e) {
            return fallback;
        }
    }

    public static List<Sound> parseSounds(List<String> list) {
        List<Sound> sounds = new ArrayList<Sound>();

        if (!list.isEmpty()) {
            for (String string : list) {
                try {
                    sounds.add(Sound.valueOf(convertToEnum(string)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return sounds;
    }

    public static Map<Enchantment, Integer> parseEnchants(List<String> list) {
        Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();

        if (!list.isEmpty()) {
            for (String string : list) {
                String[] split = string.split(":");

                if (split.length > 1) {
                    Enchantment enchant = Enchantment.getByName(convertToEnum(split[0]));

                    if (enchant != null) {
                        enchants.put(enchant, parseInteger(split[1], 1));
                    }
                }
            }
        }

        return enchants;
    }

    public static Material parseMaterial(String string) {
        return string == null ? null : Material.getMaterial(convertToEnum(string));
    }

    public static void playSounds(Player player, List<Sound> sounds) {
        if (!sounds.isEmpty()) {
            Location location = player.getLocation();

            for (Sound sound : sounds) {
                player.playSound(location, sound, 0.7F, 1.0F);
            }
        }
    }

    public static ItemStack[] cloneItems(ItemStack[] array) {
        ItemStack[] items = new ItemStack[array.length];

        for (int i = 0, s = array.length; i < s; i++) {
            items[i] = array[i].clone();
        }

        return items;
    }

    public static void executeCommands(Player player, List<String> commands) {
        if (!commands.isEmpty()) {
            for (String command : commands) {
                command = command.replace("<name>", player.getName());

                if (command.startsWith("c:")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.substring(2));
                } else {
                    Bukkit.dispatchCommand(player, command);
                }
            }
        }
    }

    public static Random getRandom() {
        return random;
    }

    /*

    public static Color getRandomColor() {
        int number = random.nextInt(17);

        switch (number) {
            case 0:
                return Color.AQUA;
            case 1:
                return Color.BLACK;
            case 2:
                return Color.BLUE;
            case 3:
                return Color.FUCHSIA;
            case 4:
                return Color.GRAY;
            case 5:
                return Color.GREEN;
            case 6:
                return Color.LIME;
            case 7:
                return Color.MAROON;
            case 8:
                return Color.NAVY;
            case 9:
                return Color.OLIVE;
            case 10:
                return Color.ORANGE;
            case 11:
                return Color.PURPLE;
            case 12:
                return Color.RED;
            case 13:
                return Color.SILVER;
            case 14:
                return Color.TEAL;
            case 15:
                return Color.WHITE;
            case 16:
                return Color.YELLOW;
            default:
                return Color.fromRGB(random.nextInt(255), random.nextInt(255), random.nextInt(255));
        }
    }

    public static FireworkEffect createRandomFirework() {
        Builder builder = FireworkEffect.builder();
        Type[] types = Type.values();
        builder.with(types[random.nextInt(types.length)]);
        builder.withColor(getRandomColor());
        builder.withFade(getRandomColor());

        if (random.nextBoolean()) {
            builder.withFlicker();
        }

        if (random.nextBoolean()) {
            builder.withTrail();
        }

        return builder.build();
    }

    public static int getRandomPower() {
        return random.nextInt(2) + 1;
    }

    public static void spawnFirework(Location location, FireworkEffect effect, int power) {
        Bukkit.getScheduler().runTask(SquidParties.getInstance(), () -> {
            Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
            FireworkMeta meta = firework.getFireworkMeta();

            meta.addEffect(effect);
            meta.setPower(power);
            firework.setFireworkMeta(meta);
        });
    }*/
}

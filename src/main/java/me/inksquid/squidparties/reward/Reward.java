package me.inksquid.squidparties.reward;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Reward {

    private List<String> commands;
    private ItemStack[] items;

    public Reward(List<String> rewards, ItemStack[] itemStacks) {

    }

    public boolean hasCommands() {
        return commands != null && !commands.isEmpty();
    }

    public boolean hasItems() {
        return items != null && items.length > 0;
    }

    public List<String> getCommands() {
        return commands;
    }

    public ItemStack[] getItems() {
        return items;
    }
}

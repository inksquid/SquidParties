package me.inksquid.squidparties.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PartyEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private int votesRequired;

    public PartyEvent(int votesRequired) {
        super(true);
        this.votesRequired = votesRequired;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public int getVotesRequired() {
        return votesRequired;
    }
}

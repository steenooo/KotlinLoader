package dev.steyn.kotlinloader.spigot.edit;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TestEvent extends Event {

    static final HandlerList LIST = new HandlerList();

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return LIST;
    }
}

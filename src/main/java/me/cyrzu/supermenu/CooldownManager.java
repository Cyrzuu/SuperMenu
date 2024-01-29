package me.cyrzu.supermenu;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final long cooldownMillis;

    public CooldownManager(long cooldownMillis) {
        this.cooldownMillis = cooldownMillis;
    }

    public boolean hasCooldown(@NotNull Player player) {
        return hasCooldown(player.getUniqueId());
    }

    public boolean hasCooldown(@NotNull UUID uuid) {
        Long cooldown = cooldowns.get(uuid);
        return cooldown != null && (System.currentTimeMillis() - cooldown) < cooldownMillis;
    }

    public void setCooldown(Player player) {
        setCooldown(player.getUniqueId());
    }

    public void setCooldown(@NotNull UUID uuid) {
        cooldowns.put(uuid, System.currentTimeMillis());
    }

}

package me.cyrzu.git.supermenu;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CooldownManager {

    @NotNull
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    private long cooldownMillis;

    private Set<Integer> disabled = new HashSet<>();

    public CooldownManager(long cooldownMillis) {
        this.cooldownMillis = cooldownMillis;
    }

    public void disabledCooldown(@NotNull Integer... slots) {
        disabled.addAll(Arrays.asList(slots));
    }

    public boolean hasCooldown(@NotNull Player player, int slot) {
        return hasCooldown(player.getUniqueId(), slot);
    }

    public boolean hasCooldown(@NotNull UUID uuid, int slot) {
        if(disabled.contains(slot)) {
            return false;
        }

        Long cooldown = cooldowns.get(uuid);
        return cooldown != null && (System.currentTimeMillis() - cooldown) < cooldownMillis;
    }

    public void setCooldown(Player player) {
        setCooldown(player.getUniqueId());
    }

    public void setCooldown(@NotNull UUID uuid) {
        cooldowns.put(uuid, System.currentTimeMillis());
    }

    public void setNewCooldown(long cooldown) {
        this.cooldownMillis = cooldown;
    }

}

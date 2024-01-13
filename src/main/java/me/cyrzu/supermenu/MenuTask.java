package me.cyrzu.supermenu;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MenuTask {

    private final JavaPlugin instance;

    private final long period;

    private final @NotNull Runnable runnable;

    private @Nullable BukkitTask task;

    public MenuTask(JavaPlugin instance, @NotNull Runnable runnable) {
        this(instance, 20, runnable);
    }

    public MenuTask(JavaPlugin instance, long period, @NotNull Runnable runnable) {
        this.instance = instance;
        this.period = Math.max(1, period);
        this.runnable = runnable;
    }

    public void run() {
        if(task != null) {
            return;
        }

        this.task = Bukkit.getScheduler().runTaskTimer(instance, runnable, 0, period);
    }

    public void cancel() {
        if(task == null || task.isCancelled()) {
            return;
        }

        task.cancel();
    }

}

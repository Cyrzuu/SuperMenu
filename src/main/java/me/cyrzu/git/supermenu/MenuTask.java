package me.cyrzu.git.supermenu;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MenuTask {

    private final JavaPlugin instance;

    private final boolean async;

    private final long period;

    private final @NotNull Runnable runnable;

    private @Nullable BukkitTask task;

    public MenuTask(JavaPlugin instance, @NotNull Runnable runnable) {
        this(instance, runnable, 20, false);
    }

    public MenuTask(JavaPlugin instance, @NotNull Runnable runnable, long period, boolean async) {
        this.instance = instance;
        this.period = Math.max(1, period);
        this.runnable = runnable;
        this.async = async;
    }

    public void run() {
        if(task != null) {
            return;
        }

        this.task = async ?
                Bukkit.getScheduler().runTaskTimerAsynchronously(instance, runnable, 0, period) :
                Bukkit.getScheduler().runTaskTimer(instance, runnable, 0, period);
    }

    public void cancel() {
        if(task == null || task.isCancelled()) {
            return;
        }

        task.cancel();
    }

}

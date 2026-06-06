package com.fastasyncworldedit.bukkit.util;

import com.fastasyncworldedit.core.util.TaskManager;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class FoliaTaskManager extends TaskManager {

    private final Plugin plugin;
    private final AtomicInteger ids = new AtomicInteger(1);
    private final Map<Integer, ScheduledTask> tasks = new ConcurrentHashMap<>();

    public FoliaTaskManager(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int repeat(@Nonnull final Runnable runnable, final int interval) {
        return remember(Bukkit.getGlobalRegionScheduler().runAtFixedRate(
                plugin,
                task -> runnable.run(),
                1L,
                Math.max(1L, interval)
        ));
    }

    @Override
    public int repeatAsync(@Nonnull final Runnable runnable, final int interval) {
        return remember(Bukkit.getAsyncScheduler().runAtFixedRate(
                plugin,
                task -> runnable.run(),
                0L,
                ticksToMillis(interval),
                TimeUnit.MILLISECONDS
        ));
    }

    @Override
    public void async(@Nonnull final Runnable runnable) {
        Bukkit.getAsyncScheduler().runNow(plugin, task -> runnable.run());
    }

    @Override
    public void task(@Nonnull final Runnable runnable) {
        Bukkit.getGlobalRegionScheduler().run(plugin, task -> runnable.run());
    }

    @Override
    public void later(@Nonnull final Runnable runnable, final int delay) {
        Bukkit.getGlobalRegionScheduler().runDelayed(plugin, task -> runnable.run(), Math.max(1L, delay));
    }

    @Override
    public void laterAsync(@Nonnull final Runnable runnable, final int delay) {
        Bukkit.getAsyncScheduler().runDelayed(plugin, task -> runnable.run(), ticksToMillis(delay), TimeUnit.MILLISECONDS);
    }

    @Override
    public void cancel(final int task) {
        ScheduledTask scheduledTask = tasks.remove(task);
        if (scheduledTask != null) {
            scheduledTask.cancel();
        }
    }

    public void cancelAll() {
        tasks.values().forEach(ScheduledTask::cancel);
        tasks.clear();
    }

    private int remember(ScheduledTask task) {
        int id = ids.getAndIncrement();
        tasks.put(id, task);
        return id;
    }

    private long ticksToMillis(int ticks) {
        return Math.max(1L, ticks) * 50L;
    }
}

package com.fastasyncworldedit.core.util;

public final class FoliaSupport {

    private static final boolean FOLIA = isClassPresent("io.papermc.paper.threadedregions.RegionizedServer");
    private static final Class<?> TICK_THREAD_CLASS = loadTickThreadClass();

    private FoliaSupport() {
    }

    public static boolean isFolia() {
        return FOLIA;
    }

    public static boolean isTickThread() {
        Thread thread = Thread.currentThread();
        if (TICK_THREAD_CLASS.isInstance(thread)) {
            return true;
        }
        if (!FOLIA) {
            return false;
        }
        String name = thread.getName();
        return name.startsWith("Folia Region Scheduler Thread")
                || name.startsWith("Region Scheduler Thread");
    }

    private static boolean isClassPresent(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    private static Class<?> loadTickThreadClass() {
        if (!FOLIA) {
            return String.class;
        }
        try {
            return Class.forName("io.papermc.paper.util.TickThread");
        } catch (ClassNotFoundException ignored) {
            return String.class;
        }
    }
}

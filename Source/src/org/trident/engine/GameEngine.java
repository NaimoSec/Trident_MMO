package org.trident.engine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.trident.engine.task.TaskManager;
import org.trident.engine.task.impl.MinigamesProcessorTask;
import org.trident.world.World;
import org.trident.world.entity.player.PlayerHandler;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * A sequential task ran by the {@link #gameExecutor} that executes game related
 * code such as cycled tasks, network events, and the updating of entities every
 * <tt>600</tt>ms.
 * 
 * @author lare96
 */
public final class GameEngine implements Runnable {

    /** A sequential executor that acts as the main game thread. */
    private static ScheduledExecutorService gameExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(
        "GameThread").setPriority(Thread.MAX_PRIORITY).build());

    /**
     * A thread pool that executes code in a sequential fashion. This thread
     * pool should be used to carry out any short lived tasks that don't have to
     * be done on the game thread.
     */
    private static ExecutorService serviceExecutor = GameEngine.createThreadPool(
        1, 1, TimeUnit.MINUTES, new ThreadFactoryBuilder().setNameFormat(
            "ServiceThread").setPriority(Thread.MIN_PRIORITY));

    /** The default constructor, can only be instantiated in this class. */
    private GameEngine() {}

    /**
     * Schedule the task that will execute game code at 600ms intervals. This
     * method should only be called <b>once</b> when the server is launched.
     */
    public static void init() {
        gameExecutor.scheduleAtFixedRate(new GameEngine(), 0, 600, TimeUnit.MILLISECONDS);
        TaskManager.submit(new MinigamesProcessorTask());
    }

    @Override
    public void run() {
        try {

            // Handle all cycle-based tasks.
            TaskManager.tick();
            

            // Handle processing for entities.
            World.tick();
        } catch (Exception e) {

            // Exceptions should never be thrown this far up, but if somehow
            // they are then we print the error and save all online players.
            e.printStackTrace();
            PlayerHandler.saveAll();
        }
    }

    /**
     * Creates a new {@link ThreadPoolExecutor} with the specified
     * {@link ThreadFactoryBuilder} and timeout values. All thread pools created
     * through this method <b>do not</b> have their core threads started
     * automatically.
     * 
     * @param size
     *            the size of this thread pool.
     * @param timeout
     *            the amount of time in <code>unit</code> it takes for threads
     *            in this pool to timeout.
     * @param unit
     *            the time unit <code>timeout</code> is measured in.
     * @param builder
     *            the thread factory builder that will be used to create the
     *            thread factory.
     * @return the new thread pool with the argued settings.
     */
    public static ThreadPoolExecutor createThreadPool(int size, long timeout,
        TimeUnit unit, ThreadFactoryBuilder builder) {
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(size);
        threadPool.setThreadFactory(builder.build());
        threadPool.setRejectedExecutionHandler(new IndicationCallerRunsPolicy());
        threadPool.setKeepAliveTime(timeout, unit);
        threadPool.allowCoreThreadTimeOut(true);
        return threadPool;
    }

    /**
     * Gets the thread pool that executes code in a sequential fashion. This
     * thread pool should be used to carry out any short lived tasks that don't
     * have to be done on the game thread.
     * 
     * @return the thread pool that executes code in a sequential fashion.
     */
    public static ExecutorService getServiceExecutor() {
        return serviceExecutor;
    }
}

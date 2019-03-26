package com.biubiu.util;

import java.util.concurrent.*;

/**
 * Created by Haibiao.Zhang on 2019-03-26 12:43
 */
public class ThreadPoolUtil {

    private static final int POOL_SIZE = Runtime.getRuntime().availableProcessors() * 100;

    private static final int QUEUE_SIZE = 0x00010000;

    private static final ExecutorService pool = new ThreadPoolExecutor(POOL_SIZE, POOL_SIZE,
            0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(QUEUE_SIZE));

    /**
     * 提交任务
     */
    public static void addTask(Runnable task) {
        pool.execute(task);
    }

}

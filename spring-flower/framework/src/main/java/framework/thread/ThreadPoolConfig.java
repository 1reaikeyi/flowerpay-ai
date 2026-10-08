package framework.thread;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池配置
 */
@Slf4j
@Configuration
public class ThreadPoolConfig {

    /**
     * 核心线程数 = cpu 核心数 + 1
     */
    private final int core = 1 + 1;

    /**
     * 有界队列容量，防止任务无限堆积导致 OOM
     */
    private static final int QUEUE_CAPACITY = 32;

    private ExecutorService flowerExecutor;
    private ExecutorService flowerDetailExecutor;
    private ExecutorService festivalExecutor;
    private ExecutorService festivalDetailExecutor;

    /**
     * 构建命名线程工厂：线程名按 namePattern 递增（如 flower-handler-1），且为守护线程
     */
    private ThreadFactory buildThreadFactory(String namePattern) {
        AtomicInteger threadNumber = new AtomicInteger(1);
        return r -> {
            Thread t = new Thread(r, String.format(namePattern, threadNumber.getAndIncrement()));
            t.setDaemon(true);
            return t;
        };
    }

    /**
     * 构建缓存重建线程池：固定核心线程数，有界队列，CallerRunsPolicy 拒绝策略，
     * 并重写 afterExecute 统一捕获并打印任务异常。
     * `AbortPolicy` （JDK 默认） 直接抛出`RejectedExecutionException` 任务丢失，异常会上抛给提交者
     * `CallerRunsPolicy` 由 提交任务的线程 自己执行该任务 不丢任务、不抛异常，但会阻塞调用线程（形成背压）
     * `DiscardPolicy` 静默丢弃，什么都不做 任务直接丢失，无任何通知
     * `DiscardOldestPolicy` 丢弃队列里**最旧（队首）**的任务，再重试提交当前任务 牺牲老任务换新任务进来
     */
    private ExecutorService buildCacheRebuildExecutor(String namePattern) {
        return new ThreadPoolExecutor(
                core, core, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                buildThreadFactory(namePattern),
                new ThreadPoolExecutor.DiscardPolicy()
        ) {
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                printException(r, t);
            }
        };
    }

    /**
     * 打印线程异常信息
     */
    public static void printException(Runnable r, Throwable t) {
        if (t == null && r instanceof Future<?>) {
            try {
                Future<?> future = (Future<?>) r;
                if (future.isDone()) {
                    future.get();
                }
            } catch (CancellationException ce) {
                t = ce;
            } catch (ExecutionException ee) {
                t = ee.getCause();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
        if (t != null) {
            log.error(t.getMessage(), t);
        }
    }

    @Bean
    public ExecutorService flowerExecutor() {
        flowerExecutor = buildCacheRebuildExecutor("flower-handler-%d");
        log.info("Flower 缓存重建线程池初始化完成, 核心线程数: {}", core);
        return flowerExecutor;
    }

    @Bean
    public ExecutorService flowerDetailExecutor() {
        flowerDetailExecutor = buildCacheRebuildExecutor("flowerDetail-handler-%d");
        log.info("flowerDetail 缓存重建线程池初始化完成, 核心线程数: {}", core);
        return flowerDetailExecutor;
    }

    @Bean
    public ExecutorService festivalExecutor() {
        festivalExecutor = buildCacheRebuildExecutor("festival-handler-%d");
        log.info("Festival 缓存重建线程池初始化完成, 核心线程数: {}", core);
        return festivalExecutor;
    }

    @Bean
    public ExecutorService festivalDetailExecutor() {
        festivalDetailExecutor = buildCacheRebuildExecutor("festivalDetail-handler-%d");
        log.info("FestivalDetail 缓存重建线程池初始化完成, 核心线程数: {}", core);
        return festivalDetailExecutor;
    }

    /**
     * 销毁事件：停止线程池。
     * 先使用 shutdown 停止接收新任务并尝试完成所有已存在任务；
     * 如果超时则调用 shutdownNow，取消队列中 Pending 的任务并中断所有阻塞函数；
     * 如果仍然超时则记录日志。对 shutdown 时线程本身被中断也做了处理。
     */
    @PreDestroy
    public void destroy() {
        shutdownGracefully(flowerExecutor, "Flower");
        shutdownGracefully(flowerDetailExecutor, "flowerDetail");
        shutdownGracefully(festivalExecutor, "Festival");
        shutdownGracefully(festivalDetailExecutor, "FestivalDetail");
    }

    private void shutdownGracefully(ExecutorService executor, String name) {
        if (executor == null || executor.isShutdown()) {
            return;
        }
        try {
            log.info("关闭 {} 缓存重建线程池", name);
            executor.shutdown();
            if (!executor.awaitTermination(120, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(120, TimeUnit.SECONDS)) {
                    log.info("{} 线程池未能终止", name);
                }
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}

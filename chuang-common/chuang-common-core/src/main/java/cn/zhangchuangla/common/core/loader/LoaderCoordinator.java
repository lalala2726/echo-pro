package cn.zhangchuangla.common.core.loader;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 数据加载协调器，负责在应用启动时协调各模块数据加载
 *
 * @author Chuang
 */
@Component
@Order(1) // 确保较早执行
@RequiredArgsConstructor
public class LoaderCoordinator implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(LoaderCoordinator.class);
    private final Map<String, LoaderStatus> loaderStatusMap = new ConcurrentHashMap<>();
    private final List<DataLoader> loaders;

    /**
     * 加载超时时间
     */
    @Value("${app.loader.timeout:60}")
    private int loaderTimeoutSeconds;

    /**
     * 异步加载器线程池大小
     */
    @Value("${app.loader.async-pool-size:10}")
    private int asyncPoolSize;

    /**
     * 是否快速失败，如果为true，则遇到异常就立即停止加载器执行
     */
    @Value("${app.loader.fail-fast:false}")
    private boolean failFast;

    @Override
    public void run(String... args) {
        logger.info("================ 开始数据加载过程 ================");
        long startTime = System.currentTimeMillis();
        loaderStatusMap.clear();

        // 检查是否有DataLoader
        if (loaders == null || loaders.isEmpty()) {
            logger.warn("没有找到可用的数据加载器，跳过加载过程");
            return;
        }

        // 按优先级排序
        loaders.sort(Comparator.comparingInt(DataLoader::getOrder));

        // 初始化状态跟踪
        for (DataLoader loader : loaders) {
            loaderStatusMap.put(loader.getName(), new LoaderStatus(loader.getName()));
        }

        // 分离同步和异步加载器
        List<DataLoader> syncLoaders = loaders.stream()
                .filter(loader -> !loader.isAsync())
                .toList();

        List<DataLoader> asyncLoaders = loaders.stream()
                .filter(DataLoader::isAsync)
                .toList();

        logger.info("发现 {} 个同步加载器, {} 个异步加载器", syncLoaders.size(), asyncLoaders.size());

        // 首先执行同步加载器（顺序执行）
        boolean syncLoadersSuccess = executeSyncLoaders(syncLoaders);

        // 如果同步加载器失败并且配置为快速失败，则不执行异步加载器
        if (!syncLoadersSuccess && failFast) {
            logger.error("同步加载器执行失败，系统配置为快速失败，跳过异步加载器执行");
            printSummary(startTime);
            return;
        }

        // 然后并行执行异步加载器
        executeAsyncLoaders(asyncLoaders);

        // 打印最终摘要
        printSummary(startTime);
    }

    /**
     * 执行同步加载器
     *
     * @return 是否全部成功
     */
    private boolean executeSyncLoaders(List<DataLoader> syncLoaders) {
        boolean allSuccess = true;

        for (DataLoader loader : syncLoaders) {
            String loaderName = loader.getName();
            LoaderStatus status = loaderStatusMap.get(loaderName);
            status.markStart();

            logger.info("开始执行同步加载器: {}", loaderName);
            try {
                boolean load = loader.load();
                if (load) {
                    status.markSuccess();
                } else {
                    allSuccess = false;
                    String errorMsg = "加载器返回false，表示加载失败";
                    status.markFailure(errorMsg);
                    logger.error("同步加载器执行失败: {} - {}", loaderName, errorMsg);
                    if (loader.allowStartupOnFailure()) {
                        throw new RuntimeException("加载器 " + loaderName + " 加载失败，阻止项目启动");
                    }
                }
                logger.info("同步加载器执行完成: {} (耗时: {}ms)", loaderName, status.getDuration());
            } catch (Exception e) {
                allSuccess = false;
                String errorMsg = e.getMessage();
                status.markFailure(errorMsg);
                logger.error("同步加载器执行失败: {} - {}", loaderName, errorMsg, e);
                if (loader.allowStartupOnFailure()) {
                    throw new RuntimeException("加载器 " + loaderName + " 加载失败，阻止项目启动", e);
                }

                if (failFast) {
                    logger.error("系统配置为快速失败模式，中止后续加载器执行");
                    break;
                }
            }
        }

        return allSuccess;
    }

    /**
     * 执行异步加载器
     */
    private void executeAsyncLoaders(List<DataLoader> asyncLoaders) {
        if (asyncLoaders.isEmpty()) {
            return;
        }

        // 创建线程池，大小可配置
        ExecutorService executorService = new ThreadPoolExecutor(
                asyncPoolSize, asyncPoolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                r -> {
                    Thread t = new Thread(r, "async-loader-thread");
                    t.setDaemon(true);
                    return t;
                });

        try {
            // 创建异步任务
            List<CompletableFuture<Void>> futures = asyncLoaders.stream()
                    .map(loader -> CompletableFuture.runAsync(() -> {
                        String loaderName = loader.getName();
                        LoaderStatus status = loaderStatusMap.get(loaderName);
                        status.markStart();

                        logger.info("开始执行异步加载器: {}", loaderName);
                        try {
                            boolean load = loader.load();
                            if (load) {
                                status.markSuccess();
                            } else {
                                String errorMsg = "加载器返回false，表示加载失败";
                                status.markFailure(errorMsg);
                                logger.error("异步加载器执行失败: {} - {}", loaderName, errorMsg);
                                if (loader.allowStartupOnFailure()) {
                                    throw new RuntimeException("加载器 " + loaderName + " 加载失败，阻止项目启动");
                                }
                            }
                            logger.info("异步加载器执行完成: {} (耗时: {}ms)", loaderName, status.getDuration());
                        } catch (Exception e) {
                            String errorMsg = e.getMessage();
                            status.markFailure(errorMsg);
                            logger.error("异步加载器执行失败: {} - {}", loaderName, errorMsg, e);
                            if (loader.allowStartupOnFailure()) {
                                throw new RuntimeException("加载器 " + loaderName + " 加载失败，阻止项目启动", e);
                            }
                        }
                    }, executorService))
                    .toList();

            // 设置超时等待所有异步加载完成
            try {
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                        .get(loaderTimeoutSeconds, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("异步加载器执行被中断", e);
            } catch (ExecutionException e) {
                logger.error("异步加载器执行发生错误", e);
            } catch (TimeoutException e) {
                logger.error("异步加载器执行超时 ({}秒)", loaderTimeoutSeconds, e);
            }
        } finally {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    logger.warn("异步加载器线程池未能在5秒内正常关闭，尝试强制关闭");
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("等待线程池关闭时被中断", e);
                executorService.shutdownNow();
            }
        }
    }

    /**
     * 打印加载摘要
     */
    private void printSummary(long startTime) {
        long totalTime = System.currentTimeMillis() - startTime;

        // 计算成功和失败的加载器数量
        long successCount = loaderStatusMap.values().stream().filter(s -> s.success).count();
        long failureCount = loaderStatusMap.size() - successCount;

        logger.info("================ 数据加载过程摘要 ================");
        logger.info("总耗时: {}ms, 成功: {}, 失败: {}", totalTime, successCount, failureCount);

        // 如果有失败的加载器，打印它们的信息
        if (failureCount > 0) {
            logger.error("以下加载器执行失败:");
            loaderStatusMap.values().stream()
                    .filter(status -> !status.success)
                    .forEach(status -> logger.error("  - {}", status));
        }

        // 打印所有加载器的详细信息（按耗时排序）
        logger.info("所有加载器执行详情 (按耗时排序):");
        loaderStatusMap.values().stream()
                .sorted(Comparator.comparingLong(LoaderStatus::getDuration).reversed())
                .forEach(status -> logger.info("  - {}", status));

        logger.info("================ 数据加载过程结束 ================");
    }

    /**
     * 加载器状态
     */
    private static class LoaderStatus {
        private final String name;
        private long startTime;
        private long endTime;
        private boolean success;
        private String errorMessage;

        public LoaderStatus(String name) {
            this.name = name;
        }

        public void markStart() {
            this.startTime = System.currentTimeMillis();
        }

        public void markSuccess() {
            this.endTime = System.currentTimeMillis();
            this.success = true;
        }

        public void markFailure(String errorMessage) {
            this.endTime = System.currentTimeMillis();
            this.success = false;
            this.errorMessage = errorMessage;
        }

        public long getDuration() {
            return endTime - startTime;
        }

        @Override
        public String toString() {
            return String.format("%s [%s] %dms %s",
                    name,
                    success ? "成功" : "失败",
                    getDuration(),
                    errorMessage != null ? "- " + errorMessage : "");
        }
    }
}

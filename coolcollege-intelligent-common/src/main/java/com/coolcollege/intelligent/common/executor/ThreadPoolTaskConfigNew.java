package com.coolcollege.intelligent.common.executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

/**
 * 线程池配置
 *
 * @author chenyupeng
 * @since 2021/11/16
 */
@Configuration
public class ThreadPoolTaskConfigNew {

    /**
     * 通用线程池
     */
    @Bean
    public TaskExecutor generalThreadPool() {
        int cores = Runtime.getRuntime().availableProcessors();

        ThreadPoolTaskExecutor executor = new MdcTaskExecutor();
        // 核心线程数目
        executor.setCorePoolSize(cores*2);
        // 指定最大线程数
        executor.setMaxPoolSize(200);
        // 队列中最大的数目
        executor.setQueueCapacity(5000);
        // 线程名称前缀
        executor.setThreadNamePrefix("generalThreadPool_");
        // 对拒绝task的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 线程空闲后的最大存活时间
        executor.setKeepAliveSeconds(60);
        // 加载
        executor.initialize();
        return executor;
    }

    /**
     * 导入导出线程池
     */
    @Bean
    public TaskExecutor importExportThreadPool() {
        int cores = Runtime.getRuntime().availableProcessors();

        ThreadPoolTaskExecutor executor = new MdcTaskExecutor();
        // 核心线程数目
        executor.setCorePoolSize(cores*2);
        // 指定最大线程数
        executor.setMaxPoolSize(100);
        // 队列中最大的数目
        executor.setQueueCapacity(5000);
        // 线程名称前缀
        executor.setThreadNamePrefix("importExportThreadPool_");
        // 线程空闲后的最大存活时间
        executor.setKeepAliveSeconds(60);
        // 加载
        executor.initialize();
        return executor;
    }

    /**
     * 发送通知线程池
     */
    @Bean
    public TaskExecutor noticeThreadPool() {
        int cores = Runtime.getRuntime().availableProcessors();

        ThreadPoolTaskExecutor executor = new MdcTaskExecutor();
        // 核心线程数目
        executor.setCorePoolSize(cores*2);
        // 指定最大线程数
        executor.setMaxPoolSize(100);
        // 队列中最大的数目
        executor.setQueueCapacity(10000);
        // 线程名称前缀
        executor.setThreadNamePrefix("noticeThreadPool_");
        // 对拒绝task的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 线程空闲后的最大存活时间
        executor.setKeepAliveSeconds(60);
        // 加载
        executor.initialize();
        return executor;
    }

    /**
     * 企业同步线程池
     */
    @Bean
    public TaskExecutor syncThreadPool() {
        int cores = Runtime.getRuntime().availableProcessors();

        ThreadPoolTaskExecutor executor = new MdcTaskExecutor();
        // 核心线程数目
        executor.setCorePoolSize(cores*2);
        // 指定最大线程数
        executor.setMaxPoolSize(100);
        // 队列中最大的数目
        executor.setQueueCapacity(10000);
        // 线程名称前缀
        executor.setThreadNamePrefix("syncThreadPool_");
        // 对拒绝task的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 线程空闲后的最大存活时间
        executor.setKeepAliveSeconds(60);
        // 加载
        executor.initialize();
        return executor;
    }

    /**
     * es数据处理线程池
     * @return
     */
    @Bean(name = "elasticSearchExecutorService")
    public ThreadPoolTaskExecutor elasticSearchExecutorService() {
        int cores = Runtime.getRuntime().availableProcessors();
        cores = Math.max(cores, 5);
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(cores);
        taskExecutor.setMaxPoolSize(cores * 10);
        taskExecutor.setKeepAliveSeconds(60);
        taskExecutor.setQueueCapacity(cores * 100);
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return taskExecutor;
    }

    /**
     * 企业同步线程池
     */
    @Bean(name = "thirdPartyThreadPool")
    public TaskExecutor thirdPartyThreadPool() {
        ThreadPoolTaskExecutor executor = new MdcTaskExecutor();
        // 核心线程数目
        executor.setCorePoolSize(10);
        // 指定最大线程数
        executor.setMaxPoolSize(15);
        // 队列中最大的数目
        executor.setQueueCapacity(10000);
        // 线程名称前缀
        executor.setThreadNamePrefix("thirdPartyThreadPool_");
        // 对拒绝task的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 线程空闲后的最大存活时间
        executor.setKeepAliveSeconds(60);
        // 加载
        executor.initialize();
        return executor;
    }

    /**
     * 企业同步线程池
     */
    @Bean(name = "isvDingDingQwThreadPool")
    public TaskExecutor isvDingDingQwThreadPool() {
        ThreadPoolTaskExecutor executor = new MdcTaskExecutor();
        // 核心线程数目
        executor.setCorePoolSize(10);
        // 指定最大线程数
        executor.setMaxPoolSize(10);
        // 队列中最大的数目
        executor.setQueueCapacity(50000);
        // 线程名称前缀
        executor.setThreadNamePrefix("isvDingDingQwThreadPool_");
        // 对拒绝task的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 线程空闲后的最大存活时间
        executor.setKeepAliveSeconds(60);
        // 加载
        executor.initialize();
        return executor;
    }

    /**
     * 企业同步线程池
     */
    @Bean(name = "syncUserThreadPool")
    public TaskExecutor syncUserThreadPool() {
        ThreadPoolTaskExecutor executor = new MdcTaskExecutor();
        // 核心线程数目
        executor.setCorePoolSize(10);
        // 指定最大线程数
        executor.setMaxPoolSize(15);
        // 队列中最大的数目
        executor.setQueueCapacity(50000);
        // 线程名称前缀
        executor.setThreadNamePrefix("syncUserThreadPool_");
        // 对拒绝task的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 线程空闲后的最大存活时间
        executor.setKeepAliveSeconds(60);
        // 加载
        executor.initialize();
        return executor;
    }

    /**
     * 用户管辖门店备份线程池
     */
    @Bean(name = "userStoreThreadPool")
    public TaskExecutor userStoreThreadPool() {
        ThreadPoolTaskExecutor executor = new MdcTaskExecutor();
        // 核心线程数目
        executor.setCorePoolSize(5);
        // 指定最大线程数
        executor.setMaxPoolSize(5);
        // 队列中最大的数目
        executor.setQueueCapacity(10000);
        // 线程名称前缀
        executor.setThreadNamePrefix("userStoreThreadPool_");
        // 对拒绝task的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 线程空闲后的最大存活时间
        executor.setKeepAliveSeconds(60);
        // 加载
        executor.initialize();
        return executor;
    }


    @Bean(name = "syncHikPassengerFlowPool")
    public TaskExecutor syncHikPassengerFlowPool() {
        ThreadPoolTaskExecutor executor = new MdcTaskExecutor();
        // 核心线程数目
        executor.setCorePoolSize(2);
        // 指定最大线程数
        executor.setMaxPoolSize(5);
        // 队列中最大的数目
        executor.setQueueCapacity(10000);
        // 线程名称前缀
        executor.setThreadNamePrefix("syncHikPassengerFlowPool_");
        // 对拒绝task的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 线程空闲后的最大存活时间
        executor.setKeepAliveSeconds(60);
        // 加载
        executor.initialize();
        return executor;
    }


    @Bean(name = "sendCardMessage")
    public TaskExecutor sendCardMessage() {
        ThreadPoolTaskExecutor executor = new MdcTaskExecutor();
        // 核心线程数目
        executor.setCorePoolSize(10);
        // 指定最大线程数
        executor.setMaxPoolSize(15);
        // 队列中最大的数目
        executor.setQueueCapacity(10000);
        // 线程名称前缀
        executor.setThreadNamePrefix("sendCardMessage_");
        // 对拒绝task的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 线程空闲后的最大存活时间
        executor.setKeepAliveSeconds(60);
        // 加载
        executor.initialize();
        return executor;
    }

    @Bean(name = "deleteRecord")
    public TaskExecutor deleteRecord() {
        ThreadPoolTaskExecutor executor = new MdcTaskExecutor();
        // 核心线程数目
        executor.setCorePoolSize(10);
        // 指定最大线程数
        executor.setMaxPoolSize(10);
        // 队列中最大的数目
        executor.setQueueCapacity(10000);
        // 线程名称前缀
        executor.setThreadNamePrefix("deleteRecord_");
        // 对拒绝task的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 线程空闲后的最大存活时间
        executor.setKeepAliveSeconds(60);
        // 加载
        executor.initialize();
        return executor;
    }

}

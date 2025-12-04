package com.coolcollege.intelligent.service.sync;

import com.coolcollege.intelligent.common.util.ThreadMdcUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author zhangchenbiao
 * @FileName: CustomerThreadPoolExecutor
 * @Description:
 * @date 2023-09-01 11:01
 */
@Slf4j
public class CustomerThreadPoolExecutor extends ThreadPoolExecutor {

    public CustomerThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public CustomerThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public CustomerThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public CustomerThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    public Future<?> submit(Runnable task) {
        log.info("mdc thread pool task executor execute");
        Map<String, String> context = MDC.getCopyOfContextMap();
        return super.submit(() -> {
            if (context != null) {
                //将父线程的MDC内容传给子线程
                MDC.setContextMap(context);
            }
            //直接给子线程设置MDC
            ThreadMdcUtil.setTraceIdIfAbsent();
            try {
                //执行任务
                task.run();
            } finally {
                try {
                    MDC.clear();
                } catch (Exception e) {
                    log.warn("MDC clear exception", e);
                }
            }
        });
    }
}

package com.coolcollege.intelligent.service.ai.impl;

import cn.hutool.core.collection.CollStreamUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.metatable.TbMetaColumnResultMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.model.ai.AICommonPromptDTO;
import com.coolcollege.intelligent.model.ai.AIResolveDTO;
import com.coolcollege.intelligent.model.ai.EnterpriseModelAlgorithmDTO;
import com.coolcollege.intelligent.model.ai.entity.AiModelLibraryDO;
import com.coolcollege.intelligent.model.enums.AICommentStyleEnum;
import com.coolcollege.intelligent.model.metatable.TbMetaColumnResultDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDataTableColumnDO;
import com.coolcollege.intelligent.service.ai.AIOpenService;
import com.coolcollege.intelligent.service.ai.EnterpriseModelAlgorithmService;
import com.coolcollege.intelligent.util.AIHelper;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.volcengine.ark.runtime.model.completion.chat.*;
import com.volcengine.ark.runtime.service.ArkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 火山引擎平台
 * </p>
 *
 * @author wangff
 * @since 2025/7/15
 */
@Service("huoshanAIOpenServiceImpl")
@RequiredArgsConstructor
@Slf4j
public class HuoshanAIOpenServiceImpl implements AIOpenService {
    private final RedisUtilPool redisUtilPool;
    @Value("${ai.huoshan.apiKey}")
    private String apiKey;
    @Value("${ai.huoshan.baseUrl}")
    private String baseUrl;
    @Resource
    private TbMetaColumnResultMapper tbMetaColumnResultMapper;
    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;

    // 2. 单次任务总量
    private static final int TOTAL_TASKS_PER_RUN = 2000;
    // 3. 线程池配置：核心线程数（CPU核心数*2，避免线程过多），最大线程数，空闲线程存活时间
    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;
    private static final int MAX_POOL_SIZE = CORE_POOL_SIZE * 2;
    private static final long KEEP_ALIVE_TIME = 5;
    // 4. 超时配置：单个请求超时、单次任务总超时
    private static final Duration REQUEST_TIMEOUT = Duration.ofMinutes(3);
    private static final Duration TOTAL_TASK_TIMEOUT = Duration.ofHours(1);
    // 线程池（复用，避免每次定时任务创建新线程池，减少资源开销）
    private ExecutorService taskExecutor;
    // Ark服务实例（复用，单例创建，避免重复初始化连接池）
    public ArkService batchArkService;

    @PostConstruct
    public void init() {
        // 2. 初始化Ark服务（单例复用，避免每次任务重建连接池）
        ConnectionPool connectionPool = new ConnectionPool(MAX_POOL_SIZE, 10, TimeUnit.MINUTES);
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(MAX_POOL_SIZE); // 最大并发请求数=线程池最大线程数
        dispatcher.setMaxRequestsPerHost(MAX_POOL_SIZE / 2); // 单个主机最大并发，避免压垮节点
        this.batchArkService = ArkService.builder()
                .dispatcher(dispatcher)
                .timeout(REQUEST_TIMEOUT)
                .connectionPool(connectionPool)
                .apiKey(apiKey)
                .build();
        log.info("Ark服务初始化完成，最大并发请求数：{}", MAX_POOL_SIZE);

        // 3. 初始化线程池（复用，核心线程常驻，空闲线程自动回收）
        this.taskExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(TOTAL_TASKS_PER_RUN), // 队列容量=任务总量，避免任务溢出
                new ThreadFactory() { // 自定义线程名，便于问题排查
                    private final AtomicInteger threadIdx = new AtomicInteger(0);
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName("batch-ai-task-thread-" + threadIdx.getAndIncrement());
                        thread.setDaemon(false); // 非守护线程，确保任务执行完再退出
                        return thread;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy() { // 拒绝策略：超出线程+队列时，由调用线程（定时任务线程）执行，避免任务丢失
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        log.warn("线程池任务已满（核心线程：{}，队列容量：{}），触发拒绝策略，由调用线程执行", CORE_POOL_SIZE, TOTAL_TASKS_PER_RUN);
                        try {
                            r.run();
                        } catch (Exception e) {
                            log.error("拒绝策略执行任务失败", e);
                        }
                    }
                }
        );
        log.info("任务线程池初始化完成，核心线程数：{}，最大线程数：{}，队列容量：{}", CORE_POOL_SIZE, MAX_POOL_SIZE, TOTAL_TASKS_PER_RUN);
    }

    @PreDestroy
    public void destroy() {
        log.info("开始释放批量任务服务资源");
        // 1. 关闭线程池（平缓关闭，等待已提交任务完成）
        if (taskExecutor != null && !taskExecutor.isShutdown()) {
            taskExecutor.shutdown();
            try {
                if (!taskExecutor.awaitTermination(10, TimeUnit.MINUTES)) {
                    taskExecutor.shutdownNow(); // 超时后强制关闭
                    log.warn("任务线程池强制关闭，可能存在未完成任务");
                } else {
                    log.info("任务线程池正常关闭");
                }
            } catch (InterruptedException e) {
                taskExecutor.shutdownNow();
                log.error("线程池关闭被中断", e);
                Thread.currentThread().interrupt();
            }
        }
        // 2. 关闭Ark服务（释放连接池）
        if (batchArkService != null) {
            batchArkService.shutdownExecutor();
            log.info("Ark服务正常关闭");
        }
        log.info("批量任务服务资源释放完成");
    }

    @Override
    public void batchDealStoreWorkAiResolve(String enterpriseId, String dbName, List<SwStoreWorkDataTableColumnDO> processColumnList) {
        if(CollectionUtils.isEmpty(processColumnList)){
            return;
        }
        DataSourceHelper.changeToSpecificDataSource(dbName);
        List<Long> metaColumnIds = processColumnList.stream().map(SwStoreWorkDataTableColumnDO::getMetaColumnId).collect(Collectors.toList());
        List<TbMetaStaTableColumnDO> columnList = tbMetaStaTableColumnMapper.getDetailByIdList(enterpriseId, metaColumnIds);
        List<TbMetaColumnResultDO> columnResultList = tbMetaColumnResultMapper.selectByColumnIds(enterpriseId, metaColumnIds);
        Map<Long, TbMetaStaTableColumnDO> columnMap = ListUtils.emptyIfNull(columnList).stream().collect(Collectors.toMap(TbMetaStaTableColumnDO::getId, v -> v));
        Map<Long, List<TbMetaColumnResultDO>> columnResultMap = ListUtils.emptyIfNull(columnResultList).stream().collect(Collectors.groupingBy(TbMetaColumnResultDO::getMetaColumnId));
        //执行批量处理
        executeBatchProcessing(enterpriseId, processColumnList, columnMap, columnResultMap);
    }

    private void executeBatchProcessing(String enterpriseId, List<SwStoreWorkDataTableColumnDO> processColumnList, Map<Long, TbMetaStaTableColumnDO> columnMap, Map<Long, List<TbMetaColumnResultDO>> columnResultMap) {
        int totalTasks = processColumnList.size();
        CountDownLatch taskLatch = new CountDownLatch(totalTasks);
        // 3. 统计任务结果（成功/失败数，便于监控）
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        long startTime = System.currentTimeMillis();
        try {
            // 4. 提交所有任务到线程池
            for (SwStoreWorkDataTableColumnDO dataColumn : processColumnList) {
                taskExecutor.submit(() -> {
                    boolean isSuccess = processSingleTask(enterpriseId, dataColumn, columnMap, columnResultMap);
                    if (isSuccess) {
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                    taskLatch.countDown(); // 无论成功失败，都计数减1
                });
            }
            // 5. 等待所有任务完成（设置总超时，避免永久阻塞）
            boolean allCompleted = taskLatch.await(TOTAL_TASK_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
            if (!allCompleted) {
                log.error("批量任务执行超时（超时时间：{}分钟），部分任务未完成",TOTAL_TASK_TIMEOUT.toMinutes());
                failCount.addAndGet((int) taskLatch.getCount()); // 未完成的任务记为失败
            }
        } catch (InterruptedException e) {
            log.error("批量任务被中断，强制停止", e);
            Thread.currentThread().interrupt(); // 恢复中断状态，便于上层感知
            failCount.addAndGet((int) taskLatch.getCount());
        } catch (Exception e) {
            log.error("批量任务调度异常", e);
            failCount.addAndGet((int) taskLatch.getCount());
        } finally {
            // 6. 任务执行完成，输出统计日志（便于监控告警）
            long costTime = (System.currentTimeMillis() - startTime) / 1000;
            log.info("定时批量任务执行结束，总耗时：{}秒，总任务数：{}，成功数：{}，失败数：{}", costTime, TOTAL_TASKS_PER_RUN, successCount.get(), failCount.get());
            // 7. 失败任务告警（实际业务中可对接钉钉/企业微信/Prometheus告警）
            if (failCount.get() > 0) {
                log.error("【告警】批量任务存在失败任务，失败数：{}，请及时排查", failCount.get());
            }
        }
    }

    private boolean processSingleTask(String enterpriseId, SwStoreWorkDataTableColumnDO dataColumn, Map<Long, TbMetaStaTableColumnDO> columnMap,Map<Long, List<TbMetaColumnResultDO>> columnResultMap) {
        Long metaColumnId = dataColumn.getMetaColumnId();
        TbMetaStaTableColumnDO staTableColumn = columnMap.get(metaColumnId);
        if (Objects.isNull(staTableColumn)) {
            log.warn("未找到数据项: metaColumnId={}", metaColumnId);
            return true;
        }
        List<TbMetaColumnResultDO> staColumnResultList = columnResultMap.get(metaColumnId);
        if (CollectionUtils.isEmpty(staColumnResultList)) {
            log.warn("未找到结果项: metaColumnId={}", metaColumnId);
            return true;
        }
        JSONArray jsonArray = JSONObject.parseArray(dataColumn.getCheckPics());
        List<String> imageList = CollStreamUtil.toList(jsonArray,v -> ((JSONObject) v).getString("handle"));
        if(CollectionUtils.isEmpty(imageList)){
            log.info("图片为空");
            return true;
        }
        AICommonPromptDTO storeWorkPrompt = getStoreWorkPrompt(enterpriseId, staTableColumn.getAiCheckStdDesc());
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content(storeWorkPrompt.getSystemPrompt()).build();
        List<ChatCompletionContentPart> multiContent = new ArrayList<>();
        multiContent.add(ChatCompletionContentPart.builder().type("text").text(storeWorkPrompt.getFinishPrompt()).build());
        for (String url : imageList) {
            multiContent.add(ChatCompletionContentPart.builder().type("image_url").imageUrl(new ChatCompletionContentPart.ChatCompletionContentPartImageURL(url)).build());
        }
        ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER).multiContent(multiContent).build();
        messages.add(systemMessage);
        messages.add(userMessage);
        ChatCompletionRequest request = ChatCompletionRequest.builder().model("ep-bi-20250902151953-5h2sv").messages(messages).build();
        try {
            ChatCompletionResult result = batchArkService.createBatchChatCompletion(request);
            String content = result.getChoices().get(0).getMessage().getContent().toString();
            AIResolveDTO aiResolveDTO = AIHelper.matchPatrolResult(content, staColumnResultList);
            log.info("处理数据完成: metaColumnId={}, result={}",dataColumn.getMetaColumnId(), JSONObject.toJSONString(aiResolveDTO));
            return true;
        } catch (Exception e) {
            log.error("AI处理异常: metaColumnId={}", dataColumn.getMetaColumnId(), e);
            return false;
        }
    }

    public AICommonPromptDTO getStoreWorkPrompt(String enterpriseId, String aiCheckStdDesc) {
        String processPromptKey = MessageFormat.format(Constants.STORE_WORK_AI.STORE_WORK_PROMPT_KEY, enterpriseId);
        String prompt = redisUtilPool.getString(processPromptKey);
        return JSONObject.parseObject(String.format(prompt, aiCheckStdDesc), AICommonPromptDTO.class);
    }

    @Override
    public String aiResolve(String enterpriseId, AICommonPromptDTO aiCommonPromptDTO, List<String> imageList, AiModelLibraryDO aiModel) {
        String url = baseUrl + "/chat/completions";
        return AIHelper.openAIRestApiExecute(url, aiModel.getCode(), apiKey, aiCommonPromptDTO, imageList);
    }

    @Autowired
    private EnterpriseModelAlgorithmService enterpriseModelAlgorithmService;

    public ChatCompletionResult aiInspectionResolve(String enterpriseId,
                                                    Long sceneId,
                                                    List<String> imageList) {
        DataSourceHelper.reset();
        EnterpriseModelAlgorithmDTO modelAlgorithmDTO = enterpriseModelAlgorithmService.detail(enterpriseId, sceneId);
        if (CollectionUtils.isEmpty(imageList)) {
            throw new ServiceException(ErrorCodeEnum.AI_PICTURE_EMPTY);
        }
        String style = AICommentStyleEnum.DETAIL.getStyle();
        AICommonPromptDTO patrolPrompt = new AICommonPromptDTO(modelAlgorithmDTO.getSystemPrompt(), modelAlgorithmDTO.getSpecialPrompt(), modelAlgorithmDTO.getUserPrompt());
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content(patrolPrompt.getSystemPrompt()).build();
        List<ChatCompletionContentPart> multiContent = new ArrayList<>();
        multiContent.add(ChatCompletionContentPart.builder().type("text").text(patrolPrompt.getFinishPrompt()).build());
        for (String url : imageList) {
            multiContent.add(ChatCompletionContentPart.builder().type("image_url").imageUrl(new ChatCompletionContentPart.ChatCompletionContentPartImageURL(url)).build());
        }
        ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER).multiContent(multiContent).build();
        messages.add(systemMessage);
        messages.add(userMessage);
        ChatCompletionRequest request = ChatCompletionRequest.builder().model("ep-bi-20250902151953-5h2sv").messages(messages).build();

        ChatCompletionResult result = batchArkService.createBatchChatCompletion(request);
        log.info("result:{}", JSONObject.toJSONString(result));
        return result;
    }

}

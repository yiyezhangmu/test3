package com.coolcollege.intelligent.service.elasticsearch.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.ElasticSearchConstants;
import com.coolcollege.intelligent.common.enums.AIEnum;
import com.coolcollege.intelligent.common.enums.ElasticSearchQueueMsgTypeEnum;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.patrol.PatrolStoreRecordStatusEnum;
import com.coolcollege.intelligent.common.enums.patrol.QuestionTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.facade.dto.openApi.display.DisplayTaskDTO;
import com.coolcollege.intelligent.mapper.elasticsearch.ElasticSearchMapper;
import com.coolcollege.intelligent.model.elasticSearch.request.MetaTableStatisticsRequest;
import com.coolcollege.intelligent.model.elasticSearch.request.RegionPatrolStatisticsRequest;
import com.coolcollege.intelligent.model.elasticSearch.response.*;
import com.coolcollege.intelligent.model.enums.BusinessCheckType;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.coolcollege.intelligent.model.enums.UnifyStatus;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifySubStatisticsDTO;
import com.coolcollege.intelligent.model.unifytask.query.DisplayTaskQuery;
import com.coolcollege.intelligent.model.unifytask.query.TaskStoreLoopQuery;
import com.coolcollege.intelligent.service.elasticsearch.ElasticSearchService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.FiltersAggregator;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: ElasticSearchServiceImpl
 * @Description: es搜索
 * @date 2021-10-25 10:33
 */
@Slf4j
@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {

    @Resource
    private ElasticSearchMapper elasticSearchMapper;

    @Value("${es.index.prefix}")
    private String esIndexPrefix;

    /**
     * 单独 索引文档的大企业
     */
    @Value("${es.vip.enterpriseIds}")
    private List<String> enterpriseIds;

    @Override
    public List<PatrolStatisticsDataDTO> patrolStoreStatisticsRegionRecord(RegionPatrolStatisticsRequest param) {
        long startTime = System.currentTimeMillis();
        //获取索引名称
        String indexName = getEsIndexName(ElasticSearchQueueMsgTypeEnum.TB_PATROL_STORE_RECORD, param.getEnterpriseId());
        List<String> regionIdsList = getRegionIds(param.getRegionIds(), param.getStoreIds());
        if(CollectionUtils.isEmpty(regionIdsList)){
            return Lists.newArrayList();
        }
        //针对每一个区域或者门店新增一个filter
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilder(param);
        boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.STATUS, PatrolStoreRecordStatusEnum.FINISH.getStatus()));
        boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.DELETED, Constants.NO));
        boolQueryBuilder.mustNot(QueryBuilders.matchQuery(ElasticSearchConstants.SUPERVISOR_ID, AIEnum.AI_ID.getCode()));
        sourceBuilder.query(boolQueryBuilder);
        FiltersAggregator.KeyedFilter[] keyedFilters = getKeyedFilter(regionIdsList);
        //配置filter
        AggregationBuilder aggregationBuilder = AggregationBuilders.filters(ElasticSearchConstants.REGION_ID, keyedFilters);
        //新增filter的聚合项
        aggregationBuilder.subAggregation(AggregationBuilders.cardinality(ElasticSearchConstants.PATROL_STORE_NUM).field(ElasticSearchConstants.STORE_ID).precisionThreshold(ElasticSearchConstants.PRECISION_THRESHOLD));
        aggregationBuilder.subAggregation(AggregationBuilders.count(ElasticSearchConstants.PATROL_NUM).field(ElasticSearchConstants.ID));
        aggregationBuilder.subAggregation(AggregationBuilders.cardinality(ElasticSearchConstants.PATROL_PERSON_NUM).field(ElasticSearchConstants.SUPERVISOR_ID).precisionThreshold(ElasticSearchConstants.PRECISION_THRESHOLD));
        aggregationBuilder.subAggregation(AggregationBuilders.sum(ElasticSearchConstants.TASK_PATROL_NUM).script(new Script("if(doc['subTaskId'].value == 0){return 0}else{return 1;}")));
        sourceBuilder.aggregation(aggregationBuilder);
        sourceBuilder.size(Constants.ES_HITS_COUNT);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(sourceBuilder);
        List<PatrolStatisticsDataDTO> resultList = elasticSearchMapper.aggregationSearch(searchRequest, PatrolStatisticsDataDTO.class);
        getResult(param.getStoreIds(), resultList);
        log.info("es 耗时：{}", System.currentTimeMillis() - startTime);
        return resultList;
    }

    @Override
    public List<TaskStoreStatisticsQuestionDTO> patrolStoreStatisticsRegionColumn(RegionPatrolStatisticsRequest param) {
        long startTime = System.currentTimeMillis();
        //获取索引名称
        String indexName = getEsIndexName(ElasticSearchQueueMsgTypeEnum.UNIFY_TASK_STORE, param.getEnterpriseId());
        List<String> regionIdsList = getRegionIds(param.getRegionIds(), param.getStoreIds());
        if(CollectionUtils.isEmpty(regionIdsList)){
            return Lists.newArrayList();
        }
        //针对每一个区域或者门店新增一个filter
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilder(param);
        boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.TASK_TYPE, TaskTypeEnum.QUESTION_ORDER.getCode()));
        boolQueryBuilder.must(QueryBuilders.termsQuery(ElasticSearchConstants.TASK_DETAIL_TYPE, new String[]{QuestionTypeEnum.PATROL_STORE.getCode(), QuestionTypeEnum.AI.getCode()}));
        sourceBuilder.query(boolQueryBuilder);
        FiltersAggregator.KeyedFilter[] keyedFilters =getKeyedFilter(regionIdsList);
        //配置filter
        AggregationBuilder aggregationBuilder = AggregationBuilders.filters(ElasticSearchConstants.REGION_ID, keyedFilters);
        //新增filter的聚合项
        aggregationBuilder.subAggregation(AggregationBuilders.count(ElasticSearchConstants.TOTAL_QUESTION_NUM).field(ElasticSearchConstants.ID));
        aggregationBuilder.subAggregation(AggregationBuilders.sum(ElasticSearchConstants.TODO_QUESTION_NUM).script(new Script("if(doc['nodeNo'].value == '1'){return 1}else{return 0;}")));
        aggregationBuilder.subAggregation(AggregationBuilders.sum(ElasticSearchConstants.UN_RECHECK_QUESTION_NUM).script(new Script("if(doc['nodeNo'].value == '2'){return 1}else{return 0;}")));
        aggregationBuilder.subAggregation(AggregationBuilders.sum(ElasticSearchConstants.FINISH_QUESTION_NUM).script(new Script("if(doc['nodeNo'].value == 'endNode'){return 1}else{return 0;}")));
        sourceBuilder.aggregation(aggregationBuilder);
        sourceBuilder.size(Constants.ES_HITS_COUNT);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(sourceBuilder);
        List<TaskStoreStatisticsQuestionDTO> resultList = elasticSearchMapper.aggregationSearch(searchRequest, TaskStoreStatisticsQuestionDTO.class);
        getResult(param.getStoreIds(), resultList);
        log.info("es 耗时：{}", System.currentTimeMillis() - startTime);
        return resultList;
    }

    @Override
    public List<PatrolStatisticsGroupByPatrolTypeDTO> getNumByPatrolType(RegionPatrolStatisticsRequest param) {
        long startTime = System.currentTimeMillis();
        //获取索引名称
        String indexName = getEsIndexName(ElasticSearchQueueMsgTypeEnum.TB_PATROL_STORE_RECORD, param.getEnterpriseId());
        List<String> regionIdsList = getRegionIds(param.getRegionIds(), param.getStoreIds());
        if(CollectionUtils.isEmpty(regionIdsList)){
            return Lists.newArrayList();
        }
        //针对每一个区域或者门店新增一个filter
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilder(param);
        boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.STATUS, PatrolStoreRecordStatusEnum.FINISH.getStatus()));
        boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.DELETED, Constants.NO));
        boolQueryBuilder.mustNot(QueryBuilders.matchQuery(ElasticSearchConstants.SUPERVISOR_ID, AIEnum.AI_ID.getCode()));
        sourceBuilder.query(boolQueryBuilder);
        FiltersAggregator.KeyedFilter[] keyedFilters = getKeyedFilter(regionIdsList);
        //配置filter
        AggregationBuilder aggregationBuilder = AggregationBuilders.filters(ElasticSearchConstants.REGION_ID, keyedFilters);
        TermsAggregationBuilder patrolTypeGroup = AggregationBuilders.terms(ElasticSearchConstants.PATROL_TYPE_LIST).field(ElasticSearchConstants.PATROL_TYPE);
        //新增filter的聚合项
        patrolTypeGroup.subAggregation(AggregationBuilders.count(ElasticSearchConstants.PATROL_TYPE_NUM).field(ElasticSearchConstants.ID));
        aggregationBuilder.subAggregation(patrolTypeGroup);
        sourceBuilder.aggregation(aggregationBuilder);
        sourceBuilder.size(Constants.ES_HITS_COUNT);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(sourceBuilder);
        List<PatrolStatisticsGroupByPatrolTypeDTO> resultList = elasticSearchMapper.aggregationSearch(searchRequest, PatrolStatisticsGroupByPatrolTypeDTO.class);
        getResult(param.getStoreIds(), resultList);
        log.info("es 耗时：{}", System.currentTimeMillis() - startTime);
        return resultList;
    }

    @Override
    public List<PatrolStoreRankDTO> regionPatrolNumRank(RegionPatrolStatisticsRequest param) {
        long startTime = System.currentTimeMillis();
        //获取索引名称
        String indexName = getEsIndexName(ElasticSearchQueueMsgTypeEnum.TB_PATROL_STORE_RECORD, param.getEnterpriseId());
        List<String> regionIdsList = getRegionIds(param.getRegionIds(), param.getStoreIds());
        if(CollectionUtils.isEmpty(regionIdsList)){
            return Lists.newArrayList();
        }
        //针对每一个区域或者门店新增一个filter
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilder(param);
        boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.STATUS, PatrolStoreRecordStatusEnum.FINISH.getStatus()));
        boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.DELETED, Constants.NO));
        sourceBuilder.query(boolQueryBuilder);
        //配置filter
        TermsAggregationBuilder storeIdGroup = AggregationBuilders.terms(ElasticSearchConstants.STORE_ID).field(ElasticSearchConstants.STORE_ID).size(ElasticSearchQueueMsgTypeEnum.AGGS_TERMS_MAX_SIZE);
        sourceBuilder.aggregation(storeIdGroup);
        sourceBuilder.size(Constants.ES_HITS_COUNT);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(sourceBuilder);
        List<PatrolStoreRankDTO> resultList = elasticSearchMapper.aggregationSearch(searchRequest, PatrolStoreRankDTO.class);
        if(CollectionUtils.isNotEmpty(resultList) && resultList.size() > Constants.DEFAULT_RANKS){
            resultList = resultList.subList(Constants.INDEX_ZERO, Constants.DEFAULT_RANKS);
        }
        log.info("es 耗时：{}", System.currentTimeMillis() - startTime);
        return resultList;
    }

    @Override
    public List<StoreQuestionRankDTO> regionQuestionNumRank(RegionPatrolStatisticsRequest param) {
        long startTime = System.currentTimeMillis();
        //获取索引名称
        String indexName = getEsIndexName(ElasticSearchQueueMsgTypeEnum.UNIFY_TASK_STORE, param.getEnterpriseId());
        List<String> regionIdsList = getRegionIds(param.getRegionIds(), param.getStoreIds());
        if(CollectionUtils.isEmpty(regionIdsList)){
            return Lists.newArrayList();
        }
        //针对每一个区域或者门店新增一个filter
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilder(param);
        boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.TASK_TYPE, TaskTypeEnum.QUESTION_ORDER.getCode()));
        boolQueryBuilder.must(QueryBuilders.termsQuery(ElasticSearchConstants.TASK_DETAIL_TYPE, new String[]{QuestionTypeEnum.PATROL_STORE.getCode(), QuestionTypeEnum.AI.getCode()}));
        sourceBuilder.query(boolQueryBuilder);
        //配置filter
        TermsAggregationBuilder storeIdGroup = AggregationBuilders.terms(ElasticSearchConstants.STORE_ID).field(ElasticSearchConstants.STORE_ID).size(ElasticSearchQueueMsgTypeEnum.AGGS_TERMS_MAX_SIZE);
        storeIdGroup.subAggregation(AggregationBuilders.count(ElasticSearchConstants.TOTAL_NUM).field(ElasticSearchConstants.ID));
        storeIdGroup.subAggregation(AggregationBuilders.sum(ElasticSearchConstants.FINISH_QUESTION_NUM).script(new Script("if(doc['nodeNo'].value == 'endNode'){return 1}else{return 0;}")));
        sourceBuilder.aggregation(storeIdGroup);
        sourceBuilder.size(Constants.ES_HITS_COUNT);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(sourceBuilder);
        List<StoreQuestionRankDTO> resultList = elasticSearchMapper.aggregationSearch(searchRequest, StoreQuestionRankDTO.class);
        if(CollectionUtils.isNotEmpty(resultList) && resultList.size() > Constants.DEFAULT_RANKS){
            resultList = resultList.subList(Constants.INDEX_ZERO, Constants.DEFAULT_RANKS);
        }
        log.info("es 耗时：{}", System.currentTimeMillis() - startTime);
        return resultList;
    }

    @Override
    public List<PatrolTypeUnFinishTaskCountDTO> unFinishTaskStatistics(RegionPatrolStatisticsRequest param) {
        long startTime = System.currentTimeMillis();
        //获取索引名称
        String indexName = getEsIndexName(ElasticSearchQueueMsgTypeEnum.TB_PATROL_STORE_RECORD, param.getEnterpriseId());
        //针对每一个区域或者门店新增一个filter
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilder(param);
        boolQueryBuilder.must(QueryBuilders.termsQuery(ElasticSearchConstants.STATUS, new int[]{PatrolStoreRecordStatusEnum.UPCOMING_HANDLE.getStatus(), PatrolStoreRecordStatusEnum.UPCOMING_APPROVE.getStatus()}));
        boolQueryBuilder.mustNot(QueryBuilders.termQuery(ElasticSearchConstants.TASK_ID, Constants.ZERO));
        sourceBuilder.query(boolQueryBuilder);
        //配置filter
        AggregationBuilder aggregationBuilder = AggregationBuilders.filters(ElasticSearchConstants.PATROL_TYPE, getKeyedFilter());
        //新增filter的聚合项
        aggregationBuilder.subAggregation(AggregationBuilders.sum(ElasticSearchConstants.UN_EXPIRE_TASK_NUM).script(new Script("if(doc['subEndTime'].value.getMillis() > System.currentTimeMillis()){return 1}else{return 0;}")));
        aggregationBuilder.subAggregation(AggregationBuilders.sum(ElasticSearchConstants.EXPIRE_TASK_NUM).script(new Script("if(doc['subEndTime'].value.getMillis() < System.currentTimeMillis()){return 1}else{return 0;}")));
        sourceBuilder.aggregation(aggregationBuilder);
        sourceBuilder.size(Constants.ES_HITS_COUNT);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(sourceBuilder);
        List<PatrolTypeUnFinishTaskCountDTO> resultList = elasticSearchMapper.aggregationSearch(searchRequest, PatrolTypeUnFinishTaskCountDTO.class);
        log.info("es 耗时：{}", System.currentTimeMillis() - startTime);
        return resultList;
    }

    @Override
    public List<PatrolTypeTaskCountDTO> finishTaskStatistics(RegionPatrolStatisticsRequest param) {
        long startTime = System.currentTimeMillis();
        //获取索引名称
        String indexName = getEsIndexName(ElasticSearchQueueMsgTypeEnum.TB_PATROL_STORE_RECORD, param.getEnterpriseId());
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilder(param);
        boolQueryBuilder.must(QueryBuilders.termQuery(ElasticSearchConstants.STATUS, PatrolStoreRecordStatusEnum.FINISH.getStatus()));
        boolQueryBuilder.must(QueryBuilders.termQuery(ElasticSearchConstants.DELETED, Constants.NO));
        boolQueryBuilder.mustNot(QueryBuilders.termQuery(ElasticSearchConstants.SUB_TASK_ID, Constants.ZERO));
        sourceBuilder.query(boolQueryBuilder);
        //配置filter
        AggregationBuilder aggregationBuilder = AggregationBuilders.filters(ElasticSearchConstants.PATROL_TYPE, getKeyedFilter());
        //新增filter的聚合项
        aggregationBuilder.subAggregation(AggregationBuilders.count(ElasticSearchConstants.TOTAL_NUM).field(ElasticSearchConstants.ID));
        aggregationBuilder.subAggregation(AggregationBuilders.sum(ElasticSearchConstants.ON_TIME_NUM).script(new Script("if(doc['signEndTime'].value.getMillis() < doc['subEndTime'].value.getMillis()){return 1}else{return 0;}")));
        sourceBuilder.aggregation(aggregationBuilder);
        sourceBuilder.size(Constants.ES_HITS_COUNT);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(sourceBuilder);
        List<PatrolTypeTaskCountDTO> resultList = elasticSearchMapper.aggregationSearch(searchRequest, PatrolTypeTaskCountDTO.class);
        log.info("es 耗时：{}", System.currentTimeMillis() - startTime);
        return resultList;
    }

    @Override
    public List<PatrolTypeTaskCountDTO> patrolStoreStatisticsByPatrolType(RegionPatrolStatisticsRequest request) {
        long startTime = System.currentTimeMillis();
        //获取索引名称
        String indexName = getEsIndexName(ElasticSearchQueueMsgTypeEnum.TB_PATROL_STORE_RECORD, request.getEnterpriseId());
        //配置filter
        AggregationBuilder aggregationBuilder = AggregationBuilders.filters(ElasticSearchConstants.PATROL_TYPE, getKeyedFilter());
        //新增filter的聚合项
        aggregationBuilder.subAggregation(AggregationBuilders.count(ElasticSearchConstants.TOTAL_NUM).field(ElasticSearchConstants.ID));
        aggregationBuilder.subAggregation(AggregationBuilders.sum(ElasticSearchConstants.TASK_NUM).script(new Script("if(doc['taskId'].value == 0){return 0}else{return 1;}")));
        //设置参数
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilder(request);
        boolQueryBuilder.must(QueryBuilders.termQuery(ElasticSearchConstants.STATUS, PatrolStoreRecordStatusEnum.FINISH.getStatus()));
        boolQueryBuilder.must(QueryBuilders.termQuery(ElasticSearchConstants.DELETED, Constants.NO));
        boolQueryBuilder.must(QueryBuilders.termsQuery(ElasticSearchConstants.BUSINESS_CHECK_TYPE, BusinessCheckType.PATROL_STORE.getCode()));
        sourceBuilder.aggregation(aggregationBuilder);
        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.size(Constants.ES_HITS_COUNT);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(sourceBuilder);
        List<PatrolTypeTaskCountDTO> resultList = elasticSearchMapper.aggregationSearch(searchRequest, PatrolTypeTaskCountDTO.class);
        log.info("es 耗时：{}", System.currentTimeMillis() - startTime);
        return resultList;
    }

    @Override
    public List<PatrolRuleTimeDTO> getPatrolRuleTime(RegionPatrolStatisticsRequest param) {
        long startTime = System.currentTimeMillis();
        //获取索引名称
        String indexName = getEsIndexName(ElasticSearchQueueMsgTypeEnum.TB_PATROL_STORE_RECORD, param.getEnterpriseId());
        List<String> regionIdsList = getRegionIds(param.getRegionIds(), param.getStoreIds());
        if(CollectionUtils.isEmpty(regionIdsList)){
            return Lists.newArrayList();
        }
        //针对每一个区域或者门店新增一个filter
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilder(param);
        boolQueryBuilder.filter(QueryBuilders.matchQuery(ElasticSearchConstants.STATUS, PatrolStoreRecordStatusEnum.FINISH.getStatus()));
        boolQueryBuilder.filter(QueryBuilders.matchQuery(ElasticSearchConstants.DELETED, Constants.NO));
        sourceBuilder.query(boolQueryBuilder);
        FiltersAggregator.KeyedFilter[] keyedFilters = getKeyedFilter(regionIdsList);
        //配置filter
        AggregationBuilder aggregationBuilder = AggregationBuilders.filters(ElasticSearchConstants.REGION_ID, keyedFilters);
        aggregationBuilder.subAggregation(AggregationBuilders.sum(ElasticSearchConstants.TOTAL_TOUR_TIME).field(ElasticSearchConstants.TOUR_TIME));
        sourceBuilder.aggregation(aggregationBuilder);
        sourceBuilder.size(Constants.ES_HITS_COUNT);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(sourceBuilder);
        List<PatrolRuleTimeDTO> resultList = elasticSearchMapper.aggregationSearch(searchRequest, PatrolRuleTimeDTO.class);
        getResult(param.getStoreIds(), resultList);
        log.info("es 耗时：{}", System.currentTimeMillis() - startTime);
        return resultList;
    }

    @Override
    public List<PatrolTimeDTO> getPatrolTime(RegionPatrolStatisticsRequest param) {
        long startTime = System.currentTimeMillis();
        //获取索引名称
        String indexName = getEsIndexName(ElasticSearchQueueMsgTypeEnum.TB_PATROL_STORE_RECORD, param.getEnterpriseId());
        List<String> regionPathList = getRegionIds(param.getRegionIds(), param.getStoreIds());
        if(CollectionUtils.isEmpty(regionPathList)){
            return Lists.newArrayList();
        }
        //针对每一个区域或者门店新增一个filter
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilder(param);
        boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.STATUS, PatrolStoreRecordStatusEnum.FINISH.getStatus()));
        boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.DELETED, Constants.NO));
        sourceBuilder.query(boolQueryBuilder);
        FiltersAggregator.KeyedFilter[] keyedFilters = getKeyedFilter(regionPathList);
        //配置filter
        AggregationBuilder aggregationBuilder = AggregationBuilders.filters(ElasticSearchConstants.REGION_ID, keyedFilters);
        aggregationBuilder.subAggregation(AggregationBuilders.sum(ElasticSearchConstants.TOTAL_PATROL_TIME).script(new Script("doc['signEndTime'].value.getMillis()  - doc['signStartTime'].value.getMillis()")));
        sourceBuilder.aggregation(aggregationBuilder);
        sourceBuilder.size(Constants.ES_HITS_COUNT);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(sourceBuilder);
        List<PatrolTimeDTO> resultList = elasticSearchMapper.aggregationSearch(searchRequest, PatrolTimeDTO.class);
        getResult(param.getStoreIds(), resultList);
        log.info("es 耗时：{}", System.currentTimeMillis() - startTime);
        return resultList;
    }

    @Override
    public PageVO<TaskStoreDO> getTaskStoreList(String eid, TaskStoreLoopQuery query) {
        long startTime = System.currentTimeMillis();
        //获取索引名称
        String indexName = getEsIndexName(ElasticSearchQueueMsgTypeEnum.UNIFY_TASK_STORE, eid);
//        if (query.getUnifyTaskId() == null) {
//            return new PageVO<>();
//        }
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = getTaskBoolQueryBuilder(eid, query);
        //es分页根据条数来
//        sourceBuilder.from(query.getPageNumber());
        sourceBuilder.from((query.getPageNumber() - 1) * query.getPageSize());
        sourceBuilder.size(query.getPageSize());
        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.sort(ElasticSearchConstants.SUB_END_TIME);
        sourceBuilder.sort(ElasticSearchConstants.ID);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(sourceBuilder);
        PageVO<TaskStoreDO> resultList = elasticSearchMapper.pageSearch(searchRequest, TaskStoreDO.class);
        log.info("es 耗时：{}", System.currentTimeMillis() - startTime);
        return resultList;
    }

    @Override
    public UnifySubStatisticsDTO getDisplayTaskCount(String eid, TaskStoreLoopQuery query) {
        long startTime = System.currentTimeMillis();
        //获取索引名称
        String indexName = getEsIndexName(ElasticSearchQueueMsgTypeEnum.UNIFY_TASK_STORE, eid);
//        if(query.getUnifyTaskId() == null){
//            return new UnifySubStatisticsDTO(0, 0, 0, 0, 0);
//        }
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.aggregation(AggregationBuilders.count("all").field("id"));
        sourceBuilder.aggregation(AggregationBuilders.sum("handle").script(new Script("if(doc['nodeNo'].value == '1'){return 1}else{return 0;}")));
        sourceBuilder.aggregation(AggregationBuilders.sum("approver").script(new Script("if(doc['nodeNo'].value == '2'){return 1}else{return 0;}")));
        sourceBuilder.aggregation(AggregationBuilders.sum("recheck").script(new Script("if(doc['nodeNo'].value == '3'){return 1}else{return 0;}")));
        sourceBuilder.aggregation(AggregationBuilders.sum("thirdApprove").script(new Script("if(doc['nodeNo'].value == '4'){return 1}else{return 0;}")));
        sourceBuilder.aggregation(AggregationBuilders.sum("fourApprove").script(new Script("if(doc['nodeNo'].value == '5'){return 1}else{return 0;}")));
        sourceBuilder.aggregation(AggregationBuilders.sum("fiveApprove").script(new Script("if(doc['nodeNo'].value == '6'){return 1}else{return 0;}")));
        sourceBuilder.aggregation(AggregationBuilders.sum("complete").script(new Script("if(doc['nodeNo'].value == 'endNode'){return 1}else{return 0;}")));
        //统计数据不能查询具体状态，否则统计数据有问题
        String nodeNo = query.getNodeNo();
        query.setNodeNo(null);
        BoolQueryBuilder boolQueryBuilder = getDisplayTaskCountQueryBuilder(eid, query);
        sourceBuilder.query(boolQueryBuilder);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(sourceBuilder);
        UnifySubStatisticsDTO result = null;
        result = elasticSearchMapper.multiAggregationSearch(searchRequest, UnifySubStatisticsDTO.class);
        //需要回滚数据，否则查询门店任务的时候出现问题
        query.setNodeNo(nodeNo);
        log.info("es 耗时：{}", System.currentTimeMillis() - startTime);
        return result;
    }

    @Override
    public UnifySubStatisticsDTO getHandleTaskStoreCount(String eid, TaskStoreLoopQuery query) {
        long startTime = System.currentTimeMillis();
        //获取索引名称
        String indexName = getEsIndexName(ElasticSearchQueueMsgTypeEnum.UNIFY_TASK_STORE, eid);
        if(query.getUnifyTaskId() == null){
            return new UnifySubStatisticsDTO(0, 0, 0, 0, 0);
        }
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.aggregation(AggregationBuilders.count("all").field("id"));
        sourceBuilder.aggregation(AggregationBuilders.sum("handle").script(new Script("if(doc['nodeNo'].value == '1'){return 1}else{return 0;}")));
        //统计数据不能查询具体状态，否则统计数据有问题
        String nodeNo = query.getNodeNo();
        query.setNodeNo(null);
        BoolQueryBuilder boolQueryBuilder = getDisplayTaskCountQueryBuilder(eid, query);
        sourceBuilder.query(boolQueryBuilder);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(sourceBuilder);
        UnifySubStatisticsDTO result = null;
        result = elasticSearchMapper.multiAggregationSearch(searchRequest, UnifySubStatisticsDTO.class);
        //需要回滚数据，否则查询门店任务的时候出现问题
        query.setNodeNo(nodeNo);
        log.info("es 耗时：{}", System.currentTimeMillis() - startTime);
        return result;
    }

    @Override
    public PatrolStatisticsDataDTO patrolStoreStatisticsRegionRecordSum(RegionPatrolStatisticsRequest param) {
        long startTime = System.currentTimeMillis();
        //获取索引名称
        String indexName = getEsIndexName(ElasticSearchQueueMsgTypeEnum.TB_PATROL_STORE_RECORD, param.getEnterpriseId());
        List<String> regionIdsList = getRegionIds(param.getRegionIds(), param.getStoreIds());
        if(CollectionUtils.isEmpty(regionIdsList)){
            return null;
        }
        //针对每一个区域或者门店新增一个filter
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilder(param);
        boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.STATUS, PatrolStoreRecordStatusEnum.FINISH.getStatus()));
        boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.DELETED, Constants.NO));
        boolQueryBuilder.mustNot(QueryBuilders.matchQuery(ElasticSearchConstants.SUPERVISOR_ID, AIEnum.AI_ID.getCode()));
        sourceBuilder.query(boolQueryBuilder);
        //新增filter的聚合项
        CardinalityAggregationBuilder patrolStoreNum = AggregationBuilders.cardinality(ElasticSearchConstants.PATROL_STORE_NUM).field(ElasticSearchConstants.STORE_ID).precisionThreshold(ElasticSearchConstants.PRECISION_THRESHOLD);
        ValueCountAggregationBuilder patrolNum = AggregationBuilders.count(ElasticSearchConstants.PATROL_NUM).field(ElasticSearchConstants.ID);
        CardinalityAggregationBuilder patrolPersonNum = AggregationBuilders.cardinality(ElasticSearchConstants.PATROL_PERSON_NUM).field(ElasticSearchConstants.SUPERVISOR_ID).precisionThreshold(ElasticSearchConstants.PRECISION_THRESHOLD);
        SumAggregationBuilder taskPatrolNum = AggregationBuilders.sum(ElasticSearchConstants.TASK_PATROL_NUM).script(new Script("if(doc['subTaskId'].value == 0){return 0}else{return 1;}"));
        sourceBuilder.aggregation(patrolStoreNum);
        sourceBuilder.aggregation(patrolNum);
        sourceBuilder.aggregation(patrolPersonNum);
        sourceBuilder.aggregation(taskPatrolNum);
        sourceBuilder.size(Constants.ES_HITS_COUNT);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(sourceBuilder);
        PatrolStatisticsDataDTO result = elasticSearchMapper.multiAggregationSearch(searchRequest, PatrolStatisticsDataDTO.class);
        log.info("es 耗时：{}", System.currentTimeMillis() - startTime);
        return result;
    }

    @Override
    public TaskStoreStatisticsQuestionDTO patrolStoreStatisticsRegionColumnSum(RegionPatrolStatisticsRequest param) {
        long startTime = System.currentTimeMillis();
        //获取索引名称
        String indexName = getEsIndexName(ElasticSearchQueueMsgTypeEnum.UNIFY_TASK_STORE, param.getEnterpriseId());
        List<String> regionIdsList = getRegionIds(param.getRegionIds(), param.getStoreIds());
        if(CollectionUtils.isEmpty(regionIdsList)){
            return null;
        }
        //针对每一个区域或者门店新增一个filter
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilder(param);
        boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.TASK_TYPE, TaskTypeEnum.QUESTION_ORDER.getCode()));
        boolQueryBuilder.must(QueryBuilders.termsQuery(ElasticSearchConstants.TASK_DETAIL_TYPE, new String[]{QuestionTypeEnum.PATROL_STORE.getCode(), QuestionTypeEnum.AI.getCode()}));
        sourceBuilder.query(boolQueryBuilder);
        //新增filter的聚合项
        sourceBuilder.aggregation(AggregationBuilders.count(ElasticSearchConstants.TOTAL_QUESTION_NUM).field(ElasticSearchConstants.ID));
        sourceBuilder.aggregation(AggregationBuilders.sum(ElasticSearchConstants.TODO_QUESTION_NUM).script(new Script("if(doc['nodeNo'].value == '1'){return 1}else{return 0;}")));
        sourceBuilder.aggregation(AggregationBuilders.sum(ElasticSearchConstants.UN_RECHECK_QUESTION_NUM).script(new Script("if(doc['nodeNo'].value == '2'){return 1}else{return 0;}")));
        sourceBuilder.aggregation(AggregationBuilders.sum(ElasticSearchConstants.FINISH_QUESTION_NUM).script(new Script("if(doc['nodeNo'].value == 'endNode'){return 1}else{return 0;}")));
        sourceBuilder.size(Constants.ES_HITS_COUNT);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(sourceBuilder);
        TaskStoreStatisticsQuestionDTO result = elasticSearchMapper.multiAggregationSearch(searchRequest, TaskStoreStatisticsQuestionDTO.class);
        log.info("es 耗时：{}", System.currentTimeMillis() - startTime);
        return result;
    }

    @Override
    public List<PatrolNumRankDataDTO> patrolStoreNumberOfRank(RegionPatrolStatisticsRequest param) {
        long startTime = System.currentTimeMillis();
        //获取索引名称
        String indexName = getEsIndexName(ElasticSearchQueueMsgTypeEnum.TB_PATROL_STORE_RECORD, param.getEnterpriseId());
        List<String> regionIdsList = getRegionIds(param.getRegionIds(), param.getStoreIds());
        if(CollectionUtils.isEmpty(regionIdsList)){
            return Lists.newArrayList();
        }
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilder(param);
        boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.STATUS, PatrolStoreRecordStatusEnum.FINISH.getStatus()));
        boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.DELETED, Constants.NO));
        sourceBuilder.query(boolQueryBuilder);
        //配置filter
        TermsAggregationBuilder userIdGroup = AggregationBuilders.terms(ElasticSearchConstants.SUPERVISOR_ID).field(ElasticSearchConstants.SUPERVISOR_ID).size(ElasticSearchQueueMsgTypeEnum.AGGS_TERMS_MAX_SIZE);
        sourceBuilder.aggregation(userIdGroup);
        sourceBuilder.size(Constants.ES_HITS_COUNT);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(sourceBuilder);
        List<PatrolNumRankDataDTO> resultList = elasticSearchMapper.aggregationSearch(searchRequest, PatrolNumRankDataDTO.class);
        int defaultRanks = Constants.DEFAULT_RANKS;
        if(Constants.HUALAISHI_ENTERPRISE_ID.equals(param.getEnterpriseId())){
            defaultRanks = Constants.THIRTY_RANKS;
        }
        if(CollectionUtils.isNotEmpty(resultList) && resultList.size() > defaultRanks){
            resultList = resultList.subList(Constants.INDEX_ZERO, defaultRanks);
        }
        log.info("es 耗时：{}， resultList:{}", System.currentTimeMillis() - startTime, JSON.toJSONString(resultList));
        return resultList;
    }

    @Override
    public List<CheckEntryStatisticsDTO> getCheckEntryStatistics(MetaTableStatisticsRequest param) {
        long startTime = System.currentTimeMillis();
        //获取索引名称
        String indexName = getEsIndexName(ElasticSearchQueueMsgTypeEnum.TB_DATA_TABLE_COLUMN, param.getEnterpriseId());
        List<String> regionIdsList = getRegionIds(param.getRegionIds(), param.getStoreIds());
        if(CollectionUtils.isEmpty(regionIdsList)){
            return Lists.newArrayList();
        }
        //针对每一个区域或者门店新增一个filter
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilder(param);
        boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.BUSINESS_STATUS, PatrolStoreRecordStatusEnum.FINISH.getStatus()));
        boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.DELETED, Constants.NO));
        if(Objects.nonNull(param.getMetaTableId())){
            boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.META_TABLE_ID, param.getMetaTableId()));
        }
        sourceBuilder.query(boolQueryBuilder);
        FiltersAggregator.KeyedFilter[] keyedFilters = getKeyedFilter(regionIdsList);
        //配置filter
        AggregationBuilder aggregationBuilder = AggregationBuilders.filters(ElasticSearchConstants.REGION_ID, keyedFilters);
        //新增filter的聚合项
        aggregationBuilder.subAggregation(AggregationBuilders.cardinality(ElasticSearchConstants.CHECK_STORE_NUM).field(ElasticSearchConstants.STORE_ID).precisionThreshold(ElasticSearchConstants.PRECISION_THRESHOLD));
        aggregationBuilder.subAggregation(AggregationBuilders.cardinality(ElasticSearchConstants.CHECK_NUM).field(ElasticSearchConstants.BUSINESS_ID));
        aggregationBuilder.subAggregation(AggregationBuilders.sum(ElasticSearchConstants.TODO_QUESTION_NUM).script(new Script("if(doc['taskQuestionId'].value != 0 && doc['taskQuestionStatus'].value == 'HANDLE'){return 1;}else{return 0;}")));
        aggregationBuilder.subAggregation(AggregationBuilders.sum(ElasticSearchConstants.UN_RECHECK_QUESTION_NUM).script(new Script("if(doc['taskQuestionId'].value != 0 && doc['taskQuestionStatus'].value == 'RECHECK'){return 1;}else{return 0;}")));
        aggregationBuilder.subAggregation(AggregationBuilders.sum(ElasticSearchConstants.FINISH_QUESTION_NUM).script(new Script("if(doc['taskQuestionId'].value != 0 && doc['taskQuestionStatus'].value == 'FINISH'){return 1;}else{return 0;}")));
        aggregationBuilder.subAggregation(AggregationBuilders.sum(ElasticSearchConstants.QUESTION_NUM).script(new Script("if(doc['taskQuestionId'].value == 0){return 0;}else{return 1;}")));
        aggregationBuilder.subAggregation(AggregationBuilders.sum(ElasticSearchConstants.QUALIFIED_NUM).script(new Script("if(doc['checkResult'].value == 'PASS'){return 1;}else{return 0;}")));
        sourceBuilder.aggregation(aggregationBuilder);
        sourceBuilder.size(Constants.ES_HITS_COUNT);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(sourceBuilder);
        List<CheckEntryStatisticsDTO> resultList = elasticSearchMapper.aggregationSearch(searchRequest, CheckEntryStatisticsDTO.class);
        getResult(param.getStoreIds(), resultList);
        log.info("es 耗时：{}", System.currentTimeMillis() - startTime);
        return resultList;
    }

    @Override
    public List<TaskStoreDO> getTaskStoreWorkList(String eid, String userId, String taskType, Integer pageNum, Integer pageSize) {
        long startTime = System.currentTimeMillis();
        //获取索引名称
        String indexName = getEsIndexName(ElasticSearchQueueMsgTypeEnum.UNIFY_TASK_STORE, eid);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("enterpriseId", eid));
        boolQueryBuilder.must(QueryBuilders.termQuery(ElasticSearchConstants.SUB_STATUS, UnifyStatus.ONGOING.getCode()));
        boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.EXTEND_INFO, userId));
        boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.TASK_TYPE, taskType));
        //es分页根据条数来
        sourceBuilder.from((pageNum - 1) * pageSize);
        sourceBuilder.size(pageSize);
        sourceBuilder.query(boolQueryBuilder);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(sourceBuilder);
        PageVO<TaskStoreDO> resultList = elasticSearchMapper.pageSearch(searchRequest, TaskStoreDO.class);
        log.info("es 耗时：{}", System.currentTimeMillis() - startTime);
        return resultList.getList();
    }

    @Override
    public List<TaskStoreDO> getDisplayStoreTaskList(String eid, DisplayTaskQuery query) {
        long startTime = System.currentTimeMillis();
        //获取索引名称
        String indexName = getEsIndexName(ElasticSearchQueueMsgTypeEnum.UNIFY_TASK_STORE, eid);
        BoolQueryBuilder boolQueryBuilder = getDisplayTaskBoolQueryBuilder(eid, query);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(0);
        sourceBuilder.size(query.getReturnLimit());
        sourceBuilder.query(boolQueryBuilder);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(sourceBuilder);
        List<TaskStoreDO> resultList = elasticSearchMapper.listSearch(searchRequest, TaskStoreDO.class);
        log.info("es 耗时：{}", System.currentTimeMillis() - startTime);
        return resultList;
    }

    /**
     * 获取搜索Builder
     * @param param
     * @return
     */
    private BoolQueryBuilder getBoolQueryBuilder(RegionPatrolStatisticsRequest param){
        if(StringUtils.isBlank(param.getEnterpriseId()) || Objects.isNull(param.getBeginDate()) || Objects.isNull(param.getEndDate())){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery(ElasticSearchConstants.ENTERPRISE_ID, param.getEnterpriseId()));
        String regionPath = getRegionWay(param.getRegionIds(), param.getStoreIds());
        if(StringUtils.isNotBlank(regionPath)){
            boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.REGION_WAY, regionPath));
        }
        if(Objects.nonNull(param.getIsGetDirectStore()) && param.getIsGetDirectStore()){
            if(CollectionUtils.isNotEmpty(param.getRegionIds())){
                boolQueryBuilder.must(QueryBuilders.termsQuery(ElasticSearchConstants.REGION_ID, param.getRegionIds()));
            }
            if(CollectionUtils.isNotEmpty(param.getStoreIds())){
                boolQueryBuilder.must(QueryBuilders.termsQuery(ElasticSearchConstants.STORE_ID, param.getStoreIds()));
            }
        }
        boolQueryBuilder.must(QueryBuilders.rangeQuery(ElasticSearchConstants.CREATE_TIME).from(param.getBeginDate().getTime()).to(param.getEndDate().getTime()));
        return boolQueryBuilder;
    }

    /**
     * 获取搜索Builder
     * @param eid
     * @param query
     * @return
     */
    private BoolQueryBuilder getTaskBoolQueryBuilder(String eid, TaskStoreLoopQuery query){
        if(StringUtils.isBlank(eid)){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("enterpriseId", eid));
        if (CollectionUtils.isEmpty(query.getUnifyTaskIds())){
            boolQueryBuilder.must(QueryBuilders.termQuery(ElasticSearchConstants.UNIFY_TASK_ID, query.getUnifyTaskId()));
        }else {
            TermsQueryBuilder termsQuery = QueryBuilders.termsQuery(ElasticSearchConstants.UNIFY_TASK_ID, query.getUnifyTaskIds());
//            boolQueryBuilder.terms(QueryBuilders.termQuery(ElasticSearchConstants.UNIFY_TASK_ID, query.getUnifyTaskIds()));
            boolQueryBuilder.must(termsQuery);
        }

        if (query.getLoopCount() != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery(ElasticSearchConstants.LOOP_COUNT, query.getLoopCount()));
        }
        if (StringUtils.isNotBlank(query.getStoreName())) {
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery(ElasticSearchConstants.STORE_NAME, query.getStoreName()));
        }
        if (StringUtils.isNotBlank(query.getNodeNo())) {
            boolQueryBuilder.must(QueryBuilders.termQuery(ElasticSearchConstants.NODE_NO, query.getNodeNo()));
        }
        //查询全部待审批
        if(query.getApproveAll() != null && query.getApproveAll()){
            boolQueryBuilder.must(QueryBuilders.termsQuery(ElasticSearchConstants.NODE_NO, UnifyNodeEnum.getApproveNoList()));

        }
        if (StringUtils.isNotBlank(query.getCcUserId())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.CC_USER_IDS, query.getCcUserId()));
        }
        if (StringUtils.isNotBlank(query.getUserId())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.EXTEND_INFO, query.getUserId()));
        }
//        boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.DELETED, Constants.NO));
        boolQueryBuilder.mustNot(QueryBuilders.matchQuery(ElasticSearchConstants.SUB_STATUS, Constants.STRING_DELETE));
        if (StringUtils.isNotBlank(query.getHandleUserId())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.HANDLER_USER_IDS, query.getHandleUserId()));
        }
        if (query.getCreateTime() != null && query.getEndTime() != null) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(ElasticSearchConstants.CREATE_TIME)
                    .gte(query.getCreateTime().getTime())
                    .lte(query.getEndTime().getTime());
            boolQueryBuilder.must(rangeQuery);
        }
//        if (query.getSubStatus()){
//
//        }
        return boolQueryBuilder;
    }


    /**
     * 获取统计数据的搜索Builder
     * @param eid
     * @param query
     * @return
     */
    private BoolQueryBuilder getDisplayTaskCountQueryBuilder(String eid, TaskStoreLoopQuery query){
        if(StringUtils.isBlank(eid)){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("enterpriseId", eid));
        if (CollectionUtils.isNotEmpty(query.getUnifyTaskIds())){
            TermsQueryBuilder termsQuery = QueryBuilders.termsQuery(ElasticSearchConstants.UNIFY_TASK_ID, query.getUnifyTaskIds());
            boolQueryBuilder.must(termsQuery);
        }else {
            boolQueryBuilder.must(QueryBuilders.termQuery(ElasticSearchConstants.UNIFY_TASK_ID, query.getUnifyTaskId()));
        }

        if (query.getLoopCount() != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery(ElasticSearchConstants.LOOP_COUNT, query.getLoopCount()));
        }
        if (StringUtils.isNotBlank(query.getCcUserId())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.CC_USER_IDS, query.getCcUserId()));
        }
        if (StringUtils.isNotBlank(query.getUserId())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.EXTEND_INFO, query.getUserId()));
        }
        if (StringUtils.isNotBlank(query.getHandleUserId())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.HANDLER_USER_IDS, query.getHandleUserId()));
        }
        boolQueryBuilder.mustNot(QueryBuilders.matchQuery(ElasticSearchConstants.SUB_STATUS, Constants.STRING_DELETE));
        return boolQueryBuilder;
    }
    
    

    /**
     * 处理区域和门店的分词匹配
     * @param regionIds
     * @param storeIds
     * @return
     */
    private String getRegionWay(List<Long> regionIds, List<String> storeIds){
        if(CollectionUtils.isEmpty(regionIds) && CollectionUtils.isEmpty(storeIds)){
            return "";
        }
        StringBuilder regionPath = new StringBuilder();
        if(CollectionUtils.isNotEmpty(regionIds)){
            for (Long regionId : regionIds) {
                regionPath.append(regionId).append(Constants.SPRIT);
            }
        }
        if(CollectionUtils.isNotEmpty(storeIds)){
            for (String storeId : storeIds) {
                if(!regionPath.toString().endsWith(Constants.SPRIT)){
                    regionPath.append(Constants.SPRIT);
                }
                regionPath.append(storeId);
            }
        }
        return regionPath.toString();
    }

    private List<String> getRegionIds(List<Long> regionIds, List<String> storeIds){
        if(CollectionUtils.isEmpty(regionIds) && CollectionUtils.isEmpty(storeIds)){
            return Lists.newArrayList();
        }
        List<String> regionPathList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(storeIds)){
            regionPathList.addAll(storeIds);
        }
        if(CollectionUtils.isNotEmpty(regionIds)){
            regionPathList.addAll(regionIds.stream().map(String::valueOf).collect(Collectors.toList()));
        }
        return regionPathList;
    }

    /**
     * 获取索引名称
     * @param typeEnum
     * @param enterpriseId
     * @return
     */
    private String getEsIndexName(ElasticSearchQueueMsgTypeEnum typeEnum, String enterpriseId){
        String indexName = MessageFormat.format(typeEnum.getIndexName(), esIndexPrefix);
        if(enterpriseIds.contains(enterpriseId)){
            //部分企业走单独的文档
            indexName = indexName + "_" + enterpriseId;
        }
        return indexName;
    }

    /**
     * 获取filter key
     * @param regionPathList
     * @return
     */
    private FiltersAggregator.KeyedFilter[] getKeyedFilter(List<String> regionPathList){
        FiltersAggregator.KeyedFilter[] keyedFilters = new FiltersAggregator.KeyedFilter[regionPathList.size()];
        int i = Constants.INDEX_ZERO;
        for (String regionPath : regionPathList) {
            if(StringUtils.isBlank(regionPath)){
                continue;
            }
            keyedFilters[i++] = new FiltersAggregator.KeyedFilter(regionPath, QueryBuilders.matchQuery(ElasticSearchConstants.REGION_WAY, regionPath));
        }
        return keyedFilters;
    }

    /**
     * 处理门店和区域的返回数据
     * @param storeIds 门店id
     * @param resultList  es返回结果
     */
    public <T extends RegionStoreBaseDTO> void getResult(List<String> storeIds, List<T> resultList){
        if(CollectionUtils.isEmpty(storeIds)){
            return;
        }
        //由于es函数关系 将门店数据的结果都映射到了regionId中 需要单独处理下门店的数据
        Map<String, T> regionMap = resultList.stream().collect(Collectors.toMap(k -> k.getRegionId(), Function.identity()));
        for (String storeId : storeIds) {
            RegionStoreBaseDTO patrolStatisticsData = regionMap.get(storeId);
            if(Objects.isNull(patrolStatisticsData)){
                continue;
            }
            patrolStatisticsData.setStoreId(storeId);
            patrolStatisticsData.setRegionId(null);
        }
    }

    /**
     * 获取线上巡店 线下巡店的KeyedFilter
     * @return
     */
    private FiltersAggregator.KeyedFilter[] getKeyedFilter(){
        FiltersAggregator.KeyedFilter[] keyedFilters = new FiltersAggregator.KeyedFilter[Constants.INDEX_TWO];
        keyedFilters[Constants.INDEX_ZERO] = new FiltersAggregator.KeyedFilter(TaskTypeEnum.PATROL_STORE_ONLINE.getCode(), QueryBuilders.termQuery(ElasticSearchConstants.PATROL_TYPE, TaskTypeEnum.PATROL_STORE_ONLINE.getCode()));
        keyedFilters[Constants.INDEX_ONE] = new FiltersAggregator.KeyedFilter(TaskTypeEnum.PATROL_STORE_OFFLINE.getCode(), QueryBuilders.termQuery(ElasticSearchConstants.PATROL_TYPE, TaskTypeEnum.PATROL_STORE_OFFLINE.getCode()));
        return keyedFilters;
    }

    /**
     * 获取搜索Builder
     * @param eid
     * @param query
     * @return
     */
    private BoolQueryBuilder getUserTaskBoolQueryBuilder(String eid, TaskStoreLoopQuery query){
        if(StringUtils.isBlank(eid) || Objects.isNull(query.getUserId())){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("enterpriseId", eid));
        boolQueryBuilder.must(QueryBuilders.termQuery(ElasticSearchConstants.SUB_STATUS, UnifyStatus.ONGOING.getCode()));
        boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.EXTEND_INFO, query.getUserId()));
        return boolQueryBuilder;
    }

    /**
     * 陈列任务搜索Builder
     * @param eid 企业id
     * @param query 陈列任务ES查询对象
     * @return org.elasticsearch.index.query.BoolQueryBuilder
     */
    private BoolQueryBuilder getDisplayTaskBoolQueryBuilder(String eid, DisplayTaskQuery query){
        if(StringUtils.isBlank(eid)){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("enterpriseId", eid));

        boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.TASK_TYPE, TaskTypeEnum.TB_DISPLAY_TASK));
        if (StringUtils.isNotBlank(query.getUserId())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.EXTEND_INFO, query.getUserId()));
        }
        if (StringUtils.isNotBlank(query.getStoreId())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchConstants.STORE_ID, query.getStoreId()));
        }
        if (StringUtils.isNotBlank(query.getStartTime())) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(ElasticSearchConstants.CREATE_TIME).gte(query.getStartTime()));
        }
        if (StringUtils.isNotBlank(query.getEndTime())) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(ElasticSearchConstants.HANDLER_END_TIME).lt(query.getEndTime()));
        }
        if (CollectionUtils.isNotEmpty(query.getUnifyTaskIds())) {
            boolQueryBuilder.must(QueryBuilders.termsQuery(ElasticSearchConstants.UNIFY_TASK_ID, query.getUnifyTaskIds()));
        }
        if (StringUtils.isNotBlank(query.getStatus())) {
            String[] statusArray = StringUtils.split(query.getStatus(), ",");
            BoolQueryBuilder builder1 = QueryBuilders.boolQuery();
            for (String status : statusArray) {
                BoolQueryBuilder builder2 = QueryBuilders.boolQuery();
                if ("handle".equals(status)) {
                    builder2.must(QueryBuilders.termQuery(ElasticSearchConstants.NODE_NO, "1"));
                } else if ("approval".equals(status)) {
                    builder2.must(QueryBuilders.termsQuery(ElasticSearchConstants.NODE_NO, UnifyNodeEnum.getApproveNoList()));
                } else if ("complete".equals(status)) {
                    builder2.must(QueryBuilders.termQuery(ElasticSearchConstants.NODE_NO, "endNode"));
                } else continue;
                builder1.should(builder2);
            }
            boolQueryBuilder.must(builder1);
        }
        return boolQueryBuilder;
    }
}

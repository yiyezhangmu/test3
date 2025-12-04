package com.coolcollege.intelligent.mapper.elasticsearch.impl;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.mapper.elasticsearch.ElasticSearchMapper;
import com.coolcollege.intelligent.model.elasticSearch.annotation.DocCount;
import com.coolcollege.intelligent.model.elasticSearch.annotation.GroupKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.ParsedMultiBucketAggregation;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: ElasticSearchMapperImpl
 * @Description: es查询实现
 * @date 2021-10-25 10:09
 */
@Slf4j
@Repository
public class ElasticSearchMapperImpl implements ElasticSearchMapper {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Override
    public SearchResponse search(SearchRequest param) {
        SearchResponse response = null;
        try {
            response = restHighLevelClient.search(param);
        } catch (IOException e) {
            log.error("ElasticSearchMapperImpl search:{}", e);
        }
        return response;
    }

    @Override
    public <T> ResponseResult<PageVO<T>> search(SearchRequest param, Class<T> responseDataClass) {
        SearchResponse response = null;
        PageVO<T> resultPage = new PageVO<T>();
        try {
            int size = param.source().size();
            int from = param.source().from();
            long startTime = System.currentTimeMillis();
            response = restHighLevelClient.search(param);
            log.info("search response:{}", System.currentTimeMillis() - startTime);
            if(Objects.nonNull(response)){
                SearchHits hits = response.getHits();
                List<Map<String, Object>> dataMap = Arrays.stream(hits.getHits()).map(o -> o.getSourceAsMap()).collect(Collectors.toList());
                List<T> dateList = JSON.parseArray(JSON.toJSONString(dataMap), responseDataClass);
                resultPage.setPageNum(from / size + 1);
                resultPage.setPageSize(size);
                resultPage.setTotal(hits.getTotalHits());
                resultPage.setList(dateList);
            }
        } catch (IOException e) {
            log.error("ElasticSearchMapperImpl search:{}", e);
        }
        return ResponseResult.success(resultPage);
    }

    @Override
    public <T> List<T> listSearch(SearchRequest param, Class<T> responseDataClass) {
        SearchResponse response = null;
        List<T> dateList = new ArrayList<>();
        try {
            long startTime = System.currentTimeMillis();
            response = restHighLevelClient.search(param);
            log.info("search response:{}", System.currentTimeMillis() - startTime);
            if(Objects.nonNull(response)){
                SearchHits hits = response.getHits();
                List<Map<String, Object>> dataMap = Arrays.stream(hits.getHits()).map(o -> o.getSourceAsMap()).collect(Collectors.toList());
                dateList = JSON.parseArray(JSON.toJSONString(dataMap), responseDataClass);
            }
        } catch (IOException e) {
            log.error("ElasticSearchMapperImpl search:{}", e);
        }
        return dateList;
    }

    @Override
    public <T> PageVO<T> pageSearch(SearchRequest param, Class<T> responseDataClass) {
        SearchResponse response = null;
        PageVO<T> resultPage = new PageVO<T>();
        try {
            int size = param.source().size();
            int from = param.source().from();
            long startTime = System.currentTimeMillis();
            response = restHighLevelClient.search(param);
            log.info("search response:{}", System.currentTimeMillis() - startTime);
            if(Objects.nonNull(response)){
                SearchHits hits = response.getHits();
                List<Map<String, Object>> dataMap = Arrays.stream(hits.getHits()).map(o -> o.getSourceAsMap()).collect(Collectors.toList());
                List<T> dateList = JSON.parseArray(JSON.toJSONString(dataMap), responseDataClass);
                resultPage.setPageNum(from / size + 1);
                resultPage.setPageSize(size);
                resultPage.setTotal(hits.getTotalHits());
                resultPage.setList(dateList);
            }
        } catch (IOException e) {
            log.error("ElasticSearchMapperImpl search:{}", e);
        }
        return resultPage;
    }

    @Override
    public <T> List<T> aggregationSearch(SearchRequest param, Class<T> responseDataClass) {
        SearchResponse response = search(param);
        if(Objects.isNull(response)){
            log.error("es 查询异常：{}", JSON.toJSONString(param));
            return Lists.newArrayList();
        }
        //获取聚合结果  aggregations 只有一个  即下面的for循环 只循环一次
        Aggregations aggregations = response.getAggregations();
        List<T> resultList = new ArrayList<>();
        //只循环一次  如果是多个聚合结果 慎用
        for (Aggregation agg : aggregations) {
            ParsedMultiBucketAggregation aggregation = (ParsedMultiBucketAggregation)agg;
            List<? extends MultiBucketsAggregation.Bucket> buckets = aggregation.getBuckets();
            //处理聚合桶中的元素
            resultList = dealBuckets(buckets, responseDataClass);
        }
        return resultList;
    }

    @Override
    public <T> T multiAggregationSearch(SearchRequest param, Class<T> responseDataClass) {
        SearchResponse response = search(param);
        if(Objects.isNull(response)){
            log.error("es 查询异常：{}", JSON.toJSONString(param));
            return null;
        }
        //获取聚合结果  aggregations 只有一个  即下面的for循环 只循环一次
        Aggregations aggregations = response.getAggregations();
        try {
            T result = responseDataClass.newInstance();
            Field[] fields = responseDataClass.getDeclaredFields();
            Map<String, Field> fieldMap = Arrays.stream(fields).collect(Collectors.toMap(k -> k.getName(), Function.identity()));
            //只循环一次  如果是多个聚合结果 慎用
            for (Aggregation agg : aggregations) {
                Map<String, Object> fieldValueMap= JSONObject.parseObject(JSON.toJSONString(agg), Map.class);
                String name = agg.getName();
                Field innerField = fieldMap.get(name);
                innerField.setAccessible(true);
                Object targetValue = Convert.convert(innerField.getType(), fieldValueMap.get("value"));
                innerField.set(result, targetValue);
            }
            return result;
        } catch (InstantiationException e) {
            log.error("es 返回异常", e);
        } catch (IllegalAccessException e) {
            log.error("es 返回异常", e);
        }
        return null;
    }

    /**
     * 处理聚合桶元素
     * @param buckets
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> List<T> dealBuckets(List<? extends MultiBucketsAggregation.Bucket> buckets, Class<T> clazz){
        List<T> resultList = new ArrayList<>();
        for (MultiBucketsAggregation.Bucket bucket : buckets) {
            try {
                T result = clazz.newInstance();
                //当前类的属性
                Field[] declaredFields = clazz.getDeclaredFields();
                //当前类父类的属性
                Field[] superDeclaredFields = clazz.getSuperclass().getDeclaredFields();
                //当前类和父类属性聚合求和
                Field[] fields = ArrayUtils.addAll(declaredFields, superDeclaredFields);
                //转成field类型的Map
                Map<String, Field> fieldMap = Arrays.stream(fields).collect(Collectors.toMap(k -> k.getName(), Function.identity()));
                //获取group by 或者filters  映射的字段
                Optional<Field> groupKeyFields = Arrays.stream(fields).filter(o -> Objects.nonNull(o.getAnnotation(GroupKey.class))).findAny();
                Field groupKeyField = groupKeyFields.orElse(null);
                if(Objects.nonNull(groupKeyField)){
                    groupKeyField.setAccessible(true);
                    Object key = Convert.convert(groupKeyField.getType(), bucket.getKey());
                    groupKeyField.set(result, key);
                }
                //获取count 映射的字段
                Optional<Field> docCountFieldOpt = Arrays.stream(fields).filter(o -> Objects.nonNull(o.getAnnotation(DocCount.class))).findAny();
                Field docCountField = docCountFieldOpt.orElse(null);
                if(Objects.nonNull(docCountField)){
                    docCountField.setAccessible(true);
                    Object docCount = Convert.convert(docCountField.getType(), bucket.getDocCount());
                    docCountField.set(result, docCount);
                }
                Map<String, Aggregation> innerBucketAggregationMap = bucket.getAggregations().asMap();
                for (Map.Entry<String, Aggregation> innerAggregationEntry : innerBucketAggregationMap.entrySet()) {
                    Field innerField = fieldMap.get(innerAggregationEntry.getKey());
                    //是否还有子聚合 如果有 递归处理
                    if(innerAggregationEntry.getValue() instanceof ParsedMultiBucketAggregation){
                        ParsedMultiBucketAggregation value = (ParsedMultiBucketAggregation)innerAggregationEntry.getValue();
                        List<? extends MultiBucketsAggregation.Bucket> innerBuckets = value.getBuckets();
                        //获取内嵌对象的类型
                        ParameterizedType parameterizedType = (ParameterizedType)innerField.getGenericType();
                        Class actualTypeArgument = (Class) parameterizedType.getActualTypeArguments()[0];
                        if(CollectionUtils.isNotEmpty(innerBuckets)){
                            List list = dealBuckets(innerBuckets, actualTypeArgument);
                            innerField.setAccessible(true);
                            innerField.set(result, list);
                        }
                    }else{
                        Map<String, Object> fieldValueMap= JSONObject.parseObject(JSON.toJSONString(innerAggregationEntry.getValue()), Map.class);
                        //将获取到的value转换成属性对应的类型
                        Object v2 = Convert.convert(innerField.getType(), fieldValueMap.get("value"));
                        innerField.setAccessible(true);
                        innerField.set(result, v2);
                    }
                }
                resultList.add(result);
            } catch (InstantiationException e) {
                log.error("ElasticSearchMapperImpl dealBuckets:{}", e);
            } catch (IllegalAccessException e) {
                log.error("ElasticSearchMapperImpl dealBuckets:{}", e);
            }
        }
        return resultList;
    }

}

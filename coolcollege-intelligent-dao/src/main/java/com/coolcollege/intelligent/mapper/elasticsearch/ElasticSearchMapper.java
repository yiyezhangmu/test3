package com.coolcollege.intelligent.mapper.elasticsearch;

import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.response.ResponseResult;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: ElasticSearchMapper
 * @Description: es 查询mapper
 * @date 2021-10-25 10:09
 */
public interface ElasticSearchMapper {

    /**
     * 搜索
     * @param param
     * @return
     */
    SearchResponse search(SearchRequest param);

    /**
     * 普通的分页搜索
     * @param param
     * @param responseDataClass
     * @param <T>
     * @return
     */
    <T> ResponseResult<PageVO<T>> search(SearchRequest param, Class<T> responseDataClass);

    /**
     * 获得列表数据
     * 注意：使用这个方法，需要自行处理分页的from，es的分页根据条数进行。示例：(pageNumber - 1) * pageSize
     * @param param
     * @param responseDataClass
     * @author: xugangkun
     * @return java.util.List<T>
     * @date: 2021/11/22 15:43
     */
    <T> List<T> listSearch(SearchRequest param, Class<T> responseDataClass);

    /**
     * 获得列表数据,同时获得分页信息
     * @param param
     * @param responseDataClass
     * @author: xugangkun
     * @return java.util.List<T>
     * @date: 2021/11/22 15:43
     */
    <T> PageVO<T> pageSearch(SearchRequest param, Class<T> responseDataClass);

    /**
     * 注意 使用改方法之前先明确是否适用  支持单层嵌套聚合
     * 仅处理获取单个group by 的搜索结果  或者es中的filters的结果
     * 例如sql: select a, b, c from table group by a;
     * 最终的返回结果为[{a1,b1,c1},{a2,b2,c2}]
     * responseDataClass 中的字段名称 group by 的字段使用GroupKey注解， 需要count的使用DocCount注解，其他字段 es脚本中的name 需要跟属性名一致，支持处理父类中的字段
     * @param param
     * @param responseDataClass
     * @param <T>
     * @return
     */
    <T> List<T> aggregationSearch(SearchRequest param, Class<T> responseDataClass);

    /**
     * 多个聚合
     * @param param
     * @param responseDataClass
     * @param <T>
     * @return
     */
    <T> T multiAggregationSearch(SearchRequest param, Class<T> responseDataClass);
}

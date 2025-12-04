package com.coolcollege.intelligent.service.course.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.config.PlaformApiConfig;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.HttpRestTemplateService;
import com.coolcollege.intelligent.service.course.CourseService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学习
 *
 * @author zhucg
 */
@Service
@Slf4j
public class CourseServiceImpl implements CourseService {

    @Autowired
    private PlaformApiConfig plaformApiConfig;

    @Autowired
    private HttpRestTemplateService httpRestTemplateService;

    /**
     * @Title: listCoursePacks @Description: 查询课程包列表 @return
     * com.coolcollege.intelligent.model.study.CoursePackageDTO @throws
     */
    @Override
    public List listCoursePacks() {

        try {
            Map<String, Object> map = new HashMap<>();
            String coursePackageApi =
                    String.format(plaformApiConfig.getCoursePackageApi(), plaformApiConfig.getAccessToken());
            return httpRestTemplateService.postForObject(coursePackageApi, map, List.class);
        } catch (Exception e) {
            log.error("listCoursePacks error", e);
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    /**
     * @Title: listCourses @Description: 查询课程列表 @param map 查询参数 @return com.github.pagehelper.PageInfo @throws
     */
    @Override
    public PageInfo listCourses(Map<String, Object> map) {

        log.info("listCourses, map={}", JSONObject.toJSONString(map));
        try {
            String coursePackageApi = String.format(plaformApiConfig.getCourseApi(), plaformApiConfig.getAccessToken());
            return httpRestTemplateService.postForObject(coursePackageApi, map, PageInfo.class);
        } catch (Exception e) {
            log.error("listCourses error", e);
            throw new ServiceException(600001, "获取课程失败");
        }
    }

    /**
     * @Title: getCourseInfo @Description: 查询课程详情 @param courseId 课程id @return
     * com.coolcollege.intelligent.model.study.CourseDTO @throws
     */
    @Override
    public Map getCourseInfo(Long courseId) {

        try {
            Map<String, Object> map = new HashMap<>();
            String coursePackageApi =
                    String.format(plaformApiConfig.getCourseInfoApi(), courseId, plaformApiConfig.getAccessToken());
            return httpRestTemplateService.postForObject(coursePackageApi, map, Map.class);
        } catch (Exception e) {
            log.error("getCourseInfo error, courseId={}", courseId, e);
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }
}

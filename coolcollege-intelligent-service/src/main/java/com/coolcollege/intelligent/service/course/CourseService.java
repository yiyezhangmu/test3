package com.coolcollege.intelligent.service.course;

import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName CourseService
 * @Description 学习
 * @author zhucg
 */
public interface CourseService {

    /**
     * @Title: listCoursePacks
     * @Description: 查询课程包列表
     * @return com.coolcollege.intelligent.model.study.CoursePackageDTO
     * @throws
     */
    List listCoursePacks();

    /**
     * @Title: listCourses
     * @Description: 查询课程列表
     * @param map 查询参数
     * @return com.github.pagehelper.PageInfo
     * @throws
     */
    PageInfo listCourses(Map<String, Object> map);

    /**
     * @Title: getCourseInfo
     * @Description: 查询课程详情
     * @param courseId 课程id
     * @return com.coolcollege.intelligent.model.study.CourseDTO
     * @throws
     */
    Map getCourseInfo(Long courseId);
}

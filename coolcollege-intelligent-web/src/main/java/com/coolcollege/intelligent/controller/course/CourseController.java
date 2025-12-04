package com.coolcollege.intelligent.controller.course;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.service.course.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * @ClassName LearningController
 * @Description 学习
 * @author zhucg
 */
@RestController
@Slf4j
@BaseResponse
@RequestMapping("/v2/enterprises/{enterprise-id}/courses")
public class CourseController {

    @Autowired
    private CourseService learningService;

    /**
     * @Title listCoursePacks
     * @Description 学习页课程包导航列表
     * @param enterpriseId 企业id
     * @param map 参数
     * @return 课程包导航列表
     */
    @PostMapping(value = "/get_packages")
    public Object listCoursePacks(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                  @RequestBody Map<String, Object> map) {
        return learningService.listCoursePacks();
    }

    /**
     * @Title listCourses
     * @Description 学习页课程列表
     * @param enterpriseId 企业id
     * @param map 参数
     * @return 课程列表
     */
    @PostMapping(value = "/get")
    public Object listCourses(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                              @RequestBody Map<String, Object> map) {
        return learningService.listCourses(map);
    }

    /**
     * @Title getCourseInfo
     * @Description 学习页课程详情查询
     * @param enterpriseId 企业id
     * @param courseId 课程id
     * @param map 参数
     * @return 课程详情
     */
    @PostMapping(value = "/get/{course-id}")
    public Object getCourseInfo(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                @PathVariable(value = "course-id", required = false) Long courseId,
                                @RequestBody Map<String, Object> map) {
        return learningService.getCourseInfo(courseId);
    }

}

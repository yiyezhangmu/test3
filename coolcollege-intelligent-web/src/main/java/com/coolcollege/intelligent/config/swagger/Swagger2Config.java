package com.coolcollege.intelligent.config.swagger;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangnan
 * @description:
 * @date 2021/11/15 2:52 下午
 */
@Profile({"local", "dev", "dev2", "ab", "local2", "localTest", "test", "test4"})
@Configuration
@EnableSwagger2
@EnableKnife4j
public class Swagger2Config {

    /**
     * 扫描接口地址的包名
     */
    public static final String BASE_PACKAGE = "com.coolcollege.intelligent.controller";

    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                .title("coolcollege-intelligent")
                .description("接口文档")
                .version("1.0")
                .build();
    }

    private Docket createDocket (String groupName, String... packages){
        List<Parameter> pars = getParameters();
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(Boolean.TRUE)
                .apiInfo(this.getApiInfo())
                .groupName(groupName)
                .select()
                .apis(this.scanBasePackage(packages))
                .build()
                .globalOperationParameters(pars);
    }

    private Docket createDocketByPath (String groupName, String... paths){
        List<Parameter> pars = getParameters();
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(Boolean.TRUE)
                .apiInfo(this.getApiInfo())
                .groupName(groupName)
                .select()
                .paths(this.scanPath(paths))
                .build()
                .globalOperationParameters(pars);
    }

    private List<Parameter> getParameters() {
        List<Parameter> pars = new ArrayList<>();
        pars.add(new ParameterBuilder().name("access_token").description("令牌").required(true)
                .modelRef(new ModelRef("string"))
                .defaultValue("{{access_token}}")
                .parameterType("query").build());
        pars.add(new ParameterBuilder().name("enterprise-id").required(true)
                .modelRef(new ModelRef("string"))
                .defaultValue("45f92210375346858b6b6694967f44de")
                .parameterType("path").build());
        return pars;
    }

    private Predicate<RequestHandler> scanBasePackage(final String... controllerPack) {
        Predicate<RequestHandler> predicate = null;
        for (String strBasePackage : controllerPack) {
            if(StringUtils.isNotBlank(strBasePackage)){
                Predicate<RequestHandler> tempPredicate = RequestHandlerSelectors.basePackage(strBasePackage);
                predicate = predicate == null ? tempPredicate : Predicates.or(tempPredicate,predicate);
            }
        }
        return predicate;
    }

    private Predicate<String> scanPath(final String... paths) {
        Predicate<String> predicate = null;
        for (String path : paths) {
            if(StringUtils.isNotBlank(path)){
                Predicate<String> tempPredicate = PathSelectors.ant(path);
                predicate = predicate == null ? tempPredicate : Predicates.or(tempPredicate,predicate);
            }
        }
        return predicate;
    }


    @Bean
    public Docket allApi() {
        return this.createDocket("全部", BASE_PACKAGE);
    }

    @Bean
    public Docket zhouDaFuApi() {
        return this.createDocketByPath("周大福需求", "/v3/enterprises/**/files/getUploadUrl*", "/v3/qyLogin","/v3/wx_qrcode_login");
    }

    @Bean
    public Docket hlsStaffPlan() {
        return this.createDocketByPath("华莱士-按人任务", "/v3/**/unifytask/*Person*", "/v3/**/unifytask/del/batch", "/v3/**/nearbyStores",
                "/v3/**/getStaffPlanPatrolRecordList", "/v3/**/getPatrolMetaTable");
    }

    @Bean
    public Docket homePage() {
        return this.createDocketByPath("首页模板", "/v3/**/homepage/*","/home/**/get*");
    }
    @Bean
    public Docket oneParty() {
        return this.createDocketByPath("门店通", "/v3/**/oneParty/*");
    }

    @Bean
    public Docket storeWork() {
        return this.createDocketByPath("店务", "/v3/**/storeWork/*", "/v3/**/storeWorkRecord/*", "/v3/**/storeWorkStatistics/*");
    }

    @Bean
    public Docket videoPatrol() {
        return this.createDocketByPath("视频巡店", "/v3/**/devices/*", "/v3/**/videos/*");
    }

    @Bean
    public Docket resourcePermission() {
        return this.createDocketByPath("百丽权限", "/v3/**/userGroup/*", "/v3/**/users/update", "/v3/**/users/dept/list", "/v3/**/users/dept/userList", "/v3/**/users/*/query");
    }


    @Bean
    public Docket supervision() {
        return this.createDocketByPath("督导助手", "/v3/**/supervision/**");
    }

    @Bean
    public Docket storeOpenRule() {
        return this.createDocketByPath("门店规则", "/v3/**/storeOpenRule/**", "/v3/**/storeSignInfo/**");
    }

    @Bean
    public Docket storePlan() {
        return this.createDocketByPath("巡店计划", "/v3/**/patrolStorePlan/**");
    }

    @Bean
    public Docket safetyCheck() {
        return this.createDocketByPath("茶颜稽核", "/v3/**/safetyCheckFlow/*", "/v3/**/safetyCheckUpcoming/*", "/v3/**/dataColumnAppeal/*");
    }

    @Bean
    public Docket oaPlugin() {
        return this.createDocketByPath("OA插件", "/v3/enterprises/oaPlugin/**");
    }

    @Bean
    public Docket messageBoard() {
        return this.createDocketByPath("留言板", "/v3/**/messageboard/**");
    }

    @Bean
    public Docket wechat() {
        return this.createDocketByPath("微信", "/wechat/**");
    }

    @Bean
    public Docket patrolPlan() {
        return this.createDocketByPath("行事历", "/v3/**/patrol/plan/**", "/v3/enterprises/**/group/config/**");
    }

    @Bean
    public Docket deviceAuth() {
        return this.createDocketByPath("0702", "/v3/enterprises/**/devices/auth/**");
    }

    @Bean
    public Docket yuNi0922() {
        return this.createDocketByPath("0922", "/v3/enterprises/**/tbdisplay/tbdisplayTableRecord/batchDeleteRecord",
                "/v3/enterprises/**/taskReport/getTaskFinishStorePage",
                "/v3/enterprises/**/unifytask/batchReallocateStoreTask",
                "/v2/enterprises/**/unifytask/taskReminder");
    }

    @Bean
    public Docket aiPatrol() {
        return this.createDocketByPath("AI巡检", "/boss/manage/aiModelScene/**",
                "/boss/manage/enterpriseAlgorithm/**", "/boss/manage/enterpriseAlgorithm/**",
                "/v3/enterprise/**/aiAlgorithm/**", "/v3/enterprises/*/aiInspection/**", "/v3/enterprises/*/aiInspectionStatistics/**");
    }


}


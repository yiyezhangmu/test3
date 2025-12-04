package ${packageName};

import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
<#if preprocessTypeList?has_content>
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
</#if>
import com.coolcollege.intelligent.model.syslog.SysLogDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

<#if (specialTypeList?has_content) || (preprocessTypeList?has_content)>
import java.util.Map;
</#if>

<#if preprocessTypeList?has_content>
import static com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum.*;
</#if>

/**
* describe: ${classDesc}操作内容处理
*
* @author ${author}
* @date ${createTime}
*/
@Service
@Slf4j
public class ${className} extends AbstractOpContentResolve {
    <#if specialTypeList?has_content>
    @Override
    protected void init() {
        super.init();
        <#list specialTypeList as specialType>
        funcMap.put(${specialType.name!}, this::${specialType.funcName!});
        </#list>
    }
    </#if>

    @Override
    public OpModuleEnum getOpModule() {
        return OpModuleEnum.${opModule};
    }

    <#if commonTypeList?has_content>
    <#list commonTypeList as commonType>
    @Override
    protected String ${commonType.funcName}(String enterpriseId, SysLogDO sysLogDO) {
        // TODO: 处理内容逻辑
        return null;
    }

    </#list>
    </#if>

    <#if specialTypeList?has_content>
    <#list specialTypeList as specialType>
    <#if specialType.msg??>
    /**
     * ${specialType.msg}
     */
    </#if>
    private String ${specialType.funcName!}(String enterpriseId, SysLogDO sysLogDO) {
        // TODO: 处理内容逻辑
        return null;
    }

    </#list>
    </#if>
    <#if preprocessTypeList?has_content>
    @Override
    public String preprocess(String enterpriseId, Map<String, Object> reqParams, OpTypeEnum typeEnum) {
        <#if (preprocessTypeList?size > 1)>
        switch (typeEnum) {
            <#list preprocessTypeList as preprocessType>
            case ${preprocessType.name!}:
                return ${preprocessType.preprocessFuncName!}(enterpriseId, reqParams);
            </#list>
        }
        <#else>
        // TODO: 前置操作
        </#if>
        return null;
    }

    <#if (preprocessTypeList?has_content)>
    <#list preprocessTypeList as preprocessType>
    /**
     * ${preprocessType.name!}前置操作逻辑
     */
    private String ${preprocessType.preprocessFuncName!}(String enterpriseId, Map<String, Object> reqParams) {
        // TODO: ${preprocessType.name!}前置操作逻辑
        return null;
    }

    </#list>
    </#if>
    </#if>
}

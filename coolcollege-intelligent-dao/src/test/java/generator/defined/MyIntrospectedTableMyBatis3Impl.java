package generator.defined;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.codegen.mybatis3.IntrospectedTableMyBatis3Impl;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.javamapper.JavaMapperGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.XMLMapperGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.InsertSelectiveElementGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.UpdateByPrimaryKeySelectiveElementGenerator;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.util.ArrayList;
import java.util.List;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * @author zhangchenbiao
 * @FileName: MyIntrospectedTableMyBatis3Impl
 * @Description:
 * @date 2021-11-18 15:38
 */
public class MyIntrospectedTableMyBatis3Impl extends IntrospectedTableMyBatis3Impl {

    private String enterpriseIdTableSuffix = "e17cd2dc350541df8a8b0af9bd27f77d";

    @Override
    public String getFullyQualifiedTableNameAtRuntime() {
        return getTableName();
    }

    @Override
    public String getAliasedFullyQualifiedTableNameAtRuntime() {
        return getTableName();
    }

    private String getTableName() {
        String tableName = this.getTableConfiguration().getTableName();
        String tableSuffix = tableName.substring(tableName.lastIndexOf("_") + 1);
        if(StringUtils.isNotBlank(tableSuffix) && tableSuffix.length() == 32){
            //企业库
            return tableName.substring(0, tableName.lastIndexOf("_")) + enterpriseIdTableSuffix;
        }
        //平台库
        return tableName;
    }

    private String getTableNameFromConfigFile() {
        String tableName = getTableName();
        if(tableName.contains(enterpriseIdTableSuffix)){
            tableName = tableName.replace(enterpriseIdTableSuffix,"");
        }
        return JavaBeansUtil.getCamelCaseString(tableName,true);
    }

    private boolean isConfigTable() {
        String tableName = this.getTableConfiguration().getTableName();
        String tableSuffix = tableName.substring(tableName.lastIndexOf("_") + 1);
        if(StringUtils.isNotBlank(tableSuffix) && tableSuffix.length() == 32){
            //企业库
            return false;
        }
        //平台库
        return true;
    }

    @Override
    protected String calculateMyBatis3XmlMapperFileName() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTableNameFromConfigFile());
        sb.append("Mapper.xml"); //$NON-NLS-1$
        return sb.toString();
    }

    @Override
    protected void calculateJavaClientAttributes() {
        if (context.getJavaClientGeneratorConfiguration() == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(calculateJavaClientImplementationPackage());
        sb.append('.');
        sb.append(getTableNameFromConfigFile());
        sb.append("DAOImpl"); //$NON-NLS-1$
        setDAOImplementationType(sb.toString());

        sb.setLength(0);
        sb.append(calculateJavaClientInterfacePackage());
        sb.append('.');
        sb.append(getTableNameFromConfigFile());
        sb.append("DAO"); //$NON-NLS-1$
        setDAOInterfaceType(sb.toString());

        sb.setLength(0);
        sb.append(calculateJavaClientInterfacePackage());
        sb.append('.');
        if (stringHasValue(tableConfiguration.getMapperName())) {
            sb.append(tableConfiguration.getMapperName());
        } else {
            if (stringHasValue(fullyQualifiedTable.getDomainObjectSubPackage())) {
                sb.append(fullyQualifiedTable.getDomainObjectSubPackage());
                sb.append('.');
            }
            sb.append(getTableNameFromConfigFile());
            sb.append("Mapper"); //$NON-NLS-1$
        }
        setMyBatis3JavaMapperType(sb.toString());

        sb.setLength(0);
        sb.append(calculateJavaClientInterfacePackage());
        sb.append('.');
        if (stringHasValue(tableConfiguration.getSqlProviderName())) {
            sb.append(tableConfiguration.getSqlProviderName());
        } else {
            if (stringHasValue(fullyQualifiedTable.getDomainObjectSubPackage())) {
                sb.append(fullyQualifiedTable.getDomainObjectSubPackage());
                sb.append('.');
            }
            sb.append(getTableNameFromConfigFile());
            sb.append("SqlProvider"); //$NON-NLS-1$
        }
        setMyBatis3SqlProviderType(sb.toString());

        sb.setLength(0);
        sb.append(calculateJavaClientInterfacePackage());
        sb.append('.');
        sb.append(getTableNameFromConfigFile());
        sb.append("DynamicSqlSupport"); //$NON-NLS-1$
        setMyBatisDynamicSqlSupportType(sb.toString());
    }

    @Override
    protected void calculateModelAttributes() {
        String pakkage = calculateJavaModelPackage();

        StringBuilder sb = new StringBuilder();
        sb.append(pakkage);
        sb.append('.');
        sb.append(getTableNameFromConfigFile());
        sb.append("Key"); //$NON-NLS-1$
        setPrimaryKeyType(sb.toString());

        sb.setLength(0);
        sb.append(pakkage);
        sb.append('.');
        sb.append(getTableNameFromConfigFile() + "DO");
        setBaseRecordType(sb.toString());

        sb.setLength(0);
        sb.append(pakkage);
        sb.append('.');
        sb.append(getTableNameFromConfigFile());
        sb.append("WithBLOBs"); //$NON-NLS-1$
        setRecordWithBLOBsType(sb.toString());

        sb.setLength(0);
        sb.append(pakkage);
        sb.append('.');
        sb.append(getTableNameFromConfigFile());
        sb.append("Example"); //$NON-NLS-1$
        setExampleType(sb.toString());
    }

    @Override
    protected void calculateXmlMapperGenerator(AbstractJavaClientGenerator javaClientGenerator,
                                               List<String> warnings,
                                               ProgressCallback progressCallback) {
        xmlMapperGenerator = new MyXMLMapperGenerator();

        initializeAbstractGenerator(xmlMapperGenerator, warnings, progressCallback);
    }

    public class MyXMLMapperGenerator extends XMLMapperGenerator {

        public MyXMLMapperGenerator() {
            super();
        }

        @Override
        protected XmlElement getSqlMapElement() {
            FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
            progressCallback.startTask(getString(
                    "Progress.12", table.toString())); //$NON-NLS-1$
            XmlElement answer = new XmlElement("mapper"); //$NON-NLS-1$
            String namespace = introspectedTable.getMyBatis3SqlMapNamespace();
            answer.addAttribute(new Attribute("namespace", //$NON-NLS-1$
                    namespace));

            context.getCommentGenerator().addRootComment(answer);
            addResultMapWithoutBLOBsElement(answer);
            addResultMapWithBLOBsElement(answer);
            addExampleWhereClauseElement(answer);
            addMyBatis3UpdateByExampleWhereClauseElement(answer);
            addBaseColumnListElement(answer);
            addBlobColumnListElement(answer);
            addInsertSelectiveElement(answer);
            addUpdateByPrimaryKeySelectiveElement(answer);
            return answer;
        }

        @Override
        protected void addInsertSelectiveElement(XmlElement parentElement) {
            if (introspectedTable.getRules().generateInsertSelective()) {
                AbstractXmlElementGenerator elementGenerator = new MyInsertSelectiveElementGenerator();
                initializeAndExecuteGenerator(elementGenerator, parentElement);
            }
        }

        @Override
        protected void addUpdateByPrimaryKeySelectiveElement(XmlElement parentElement) {
            if (introspectedTable.getRules().generateUpdateByPrimaryKeySelective()) {
                AbstractXmlElementGenerator elementGenerator = new MyUpdateByPrimaryKeySelectiveElementGenerator();
                initializeAndExecuteGenerator(elementGenerator, parentElement);
            }
        }
    }
    @Override
    protected AbstractJavaClientGenerator createJavaClientGenerator() {
        if (context.getJavaClientGeneratorConfiguration() == null) {
            return null;
        }
        AbstractJavaClientGenerator javaGenerator;
        javaGenerator = new MyJavaMapperGenerator();

        return javaGenerator;
    }

    public class MyJavaMapperGenerator extends JavaMapperGenerator {

        @Override
        public List<CompilationUnit> getCompilationUnits() {
            boolean isConfig = isConfigTable();
            progressCallback.startTask(getString("Progress.17", //$NON-NLS-1$
                    introspectedTable.getFullyQualifiedTable().toString()));
            CommentGenerator commentGenerator = context.getCommentGenerator();

            FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                    introspectedTable.getMyBatis3JavaMapperType());
            Interface interfaze = new Interface(type);
            interfaze.setVisibility(JavaVisibility.PUBLIC);
            commentGenerator.addJavaFileComment(interfaze);

            String rootInterface = introspectedTable
                    .getTableConfigurationProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
            if (!stringHasValue(rootInterface)) {
                rootInterface = context.getJavaClientGeneratorConfiguration()
                        .getProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
            }

            if (stringHasValue(rootInterface)) {
                FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(
                        rootInterface);
                interfaze.addSuperInterface(fqjt);
                interfaze.addImportedType(fqjt);
            }
            if(!isConfig){
                interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param"));
            }
            addInsertSelectiveMethod(interfaze);
            addUpdateByPrimaryKeySelectiveMethod(interfaze);
            List<Method> methods = interfaze.getMethods();
            for (Method method : methods) {
                List<Parameter> parameters = method.getParameters();
                for (Parameter parameter : parameters) {
                    if(!isConfig){
                        String name = parameter.getName();
                        parameter.addAnnotation("@Param(\""+ name+"\")");
                    }
                }
                if(!isConfig){
                    Parameter enterpriseId = new Parameter(FullyQualifiedJavaType.getStringInstance(), "enterpriseId");
                    enterpriseId.addAnnotation("@Param(\"enterpriseId\")");
                    method.addParameter(enterpriseId);
                }
            }

            List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
            if (context.getPlugins().clientGenerated(interfaze, null,
                    introspectedTable)) {
                answer.add(interfaze);
            }

            List<CompilationUnit> extraCompilationUnits = getExtraCompilationUnits();
            if (extraCompilationUnits != null) {
                answer.addAll(extraCompilationUnits);
            }

            return answer;
        }
    }

    public class MyInsertSelectiveElementGenerator extends InsertSelectiveElementGenerator{

        public MyInsertSelectiveElementGenerator() {
            super();
        }

        @Override
        public void addElements(XmlElement parentElement) {
            boolean isConfig = isConfigTable();
            XmlElement answer = new XmlElement("insert"); //$NON-NLS-1$

            answer.addAttribute(new Attribute(
                    "id", introspectedTable.getInsertSelectiveStatementId())); //$NON-NLS-1$

            /*
            FullyQualifiedJavaType parameterType = introspectedTable.getRules()
                    .calculateAllFieldsClass();
            answer.addAttribute(new Attribute("parameterType", //$NON-NLS-1$
                    parameterType.getFullyQualifiedName()));*/

            context.getCommentGenerator().addComment(answer);

            GeneratedKey gk = introspectedTable.getGeneratedKey();
            if (gk != null) {
                IntrospectedColumn introspectedColumn = introspectedTable
                        .getColumn(gk.getColumn());
                /*if (introspectedColumn != null) {
                    if (gk.isJdbcStandard()) {
                        answer.addAttribute(new Attribute("useGeneratedKeys", "true")); //$NON-NLS-1$ //$NON-NLS-2$
                        answer.addAttribute(new Attribute("keyProperty", introspectedColumn.getJavaProperty())); //$NON-NLS-1$
                        answer.addAttribute(new Attribute("keyColumn", introspectedColumn.getActualColumnName())); //$NON-NLS-1$
                    } else {
                        answer.addElement(getSelectKey(introspectedColumn, gk));
                    }
                }*/
                if(introspectedColumn != null){
                    answer.addAttribute(new Attribute("useGeneratedKeys", "true")); //$NON-NLS-1$ //$NON-NLS-2$
                    answer.addAttribute(new Attribute("keyProperty", "record."+introspectedColumn.getJavaProperty())); //$NON-NLS-1$
                    answer.addAttribute(new Attribute("keyColumn", introspectedColumn.getActualColumnName())); //$NON-NLS-1$
                }
            }

            StringBuilder sb = new StringBuilder();

            sb.append("insert into "); //$NON-NLS-1$
            sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
            answer.addElement(new TextElement(sb.toString()));

            XmlElement insertTrimElement = new XmlElement("trim"); //$NON-NLS-1$
            insertTrimElement.addAttribute(new Attribute("prefix", "(")); //$NON-NLS-1$ //$NON-NLS-2$
            insertTrimElement.addAttribute(new Attribute("suffix", ")")); //$NON-NLS-1$ //$NON-NLS-2$
            insertTrimElement.addAttribute(new Attribute("suffixOverrides", ",")); //$NON-NLS-1$ //$NON-NLS-2$
            answer.addElement(insertTrimElement);

            XmlElement valuesTrimElement = new XmlElement("trim"); //$NON-NLS-1$
            valuesTrimElement.addAttribute(new Attribute("prefix", "values (")); //$NON-NLS-1$ //$NON-NLS-2$
            valuesTrimElement.addAttribute(new Attribute("suffix", ")")); //$NON-NLS-1$ //$NON-NLS-2$
            valuesTrimElement.addAttribute(new Attribute("suffixOverrides", ",")); //$NON-NLS-1$ //$NON-NLS-2$
            answer.addElement(valuesTrimElement);

            for (IntrospectedColumn introspectedColumn : ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns())) {

                if (introspectedColumn.isSequenceColumn()  || introspectedColumn.getFullyQualifiedJavaType().isPrimitive()) {
                    sb.setLength(0);
                    sb.append(MyBatis3FormattingUtilities
                            .getEscapedColumnName(introspectedColumn));
                    sb.append(',');
                    insertTrimElement.addElement(new TextElement(sb.toString()));

                    sb.setLength(0);
                    sb.append(MyBatis3FormattingUtilities
                            .getParameterClause(introspectedColumn));
                    sb.append(',');
                    valuesTrimElement.addElement(new TextElement(sb.toString()));

                    continue;
                }

                sb.setLength(0);
                if(!isConfig){
                    sb.append("record.");
                }
                sb.append(introspectedColumn.getJavaProperty());
                sb.append(" != null"); //$NON-NLS-1$
                XmlElement insertNotNullElement = new XmlElement("if"); //$NON-NLS-1$
                insertNotNullElement.addAttribute(new Attribute(
                        "test", sb.toString())); //$NON-NLS-1$

                sb.setLength(0);
                sb.append(MyBatis3FormattingUtilities
                        .getEscapedColumnName(introspectedColumn));
                sb.append(',');
                insertNotNullElement.addElement(new TextElement(sb.toString()));
                insertTrimElement.addElement(insertNotNullElement);

                sb.setLength(0);
                if(!isConfig){
                    sb.append("record.");
                }
                sb.append(introspectedColumn.getJavaProperty());
                sb.append(" != null"); //$NON-NLS-1$
                XmlElement valuesNotNullElement = new XmlElement("if"); //$NON-NLS-1$
                valuesNotNullElement.addAttribute(new Attribute(
                        "test", sb.toString())); //$NON-NLS-1$

                sb.setLength(0);
                if(isConfig){
                    sb.append("#{"+introspectedColumn.getJavaProperty() + "}");
                }else{
                    sb.append("#{record."+introspectedColumn.getJavaProperty() + "}");
                }
                sb.append(',');
                valuesNotNullElement.addElement(new TextElement(sb.toString()));
                valuesTrimElement.addElement(valuesNotNullElement);
            }

            if (context.getPlugins().sqlMapInsertSelectiveElementGenerated(answer, introspectedTable)) {
                parentElement.addElement(answer);
            }
        }
    }

    public class MyUpdateByPrimaryKeySelectiveElementGenerator extends UpdateByPrimaryKeySelectiveElementGenerator{
        public MyUpdateByPrimaryKeySelectiveElementGenerator() {
            super();
        }

        @Override
        public void addElements(XmlElement parentElement) {
            boolean isConfig = isConfigTable();
            XmlElement answer = new XmlElement("update"); //$NON-NLS-1$

            answer.addAttribute(new Attribute(
                    "id", introspectedTable.getUpdateByPrimaryKeySelectiveStatementId())); //$NON-NLS-1$

            String parameterType;

            if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
                parameterType = introspectedTable.getRecordWithBLOBsType();
            } else {
                parameterType = introspectedTable.getBaseRecordType();
            }

            /*answer.addAttribute(new Attribute("parameterType", //$NON-NLS-1$
                    parameterType));*/

            context.getCommentGenerator().addComment(answer);

            StringBuilder sb = new StringBuilder();

            sb.append("update "); //$NON-NLS-1$
            sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
            answer.addElement(new TextElement(sb.toString()));

            XmlElement dynamicElement = new XmlElement("set"); //$NON-NLS-1$
            answer.addElement(dynamicElement);

            for (IntrospectedColumn introspectedColumn : ListUtilities.removeGeneratedAlwaysColumns(introspectedTable.getNonPrimaryKeyColumns())) {
                sb.setLength(0);
                if(!isConfig){
                    sb.append("record.");
                }
                sb.append(introspectedColumn.getJavaProperty());
                sb.append(" != null"); //$NON-NLS-1$
                XmlElement isNotNullElement = new XmlElement("if"); //$NON-NLS-1$
                isNotNullElement.addAttribute(new Attribute("test", sb.toString())); //$NON-NLS-1$
                dynamicElement.addElement(isNotNullElement);

                sb.setLength(0);
                sb.append(MyBatis3FormattingUtilities
                        .getEscapedColumnName(introspectedColumn));
                sb.append(" = ");
                if(!isConfig){
                    sb.append("#{record."+introspectedColumn.getJavaProperty() + "}");
                }else{
                    sb.append("#{"+introspectedColumn.getJavaProperty() + "}");
                }
                sb.append(',');

                isNotNullElement.addElement(new TextElement(sb.toString()));
            }

            boolean and = false;
            for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
                sb.setLength(0);
                if (and) {
                    sb.append("  and "); //$NON-NLS-1$
                } else {
                    sb.append("where "); //$NON-NLS-1$
                    and = true;
                }

                sb.append(MyBatis3FormattingUtilities
                        .getEscapedColumnName(introspectedColumn));
                sb.append(" = ");
                if(isConfig){
                    sb.append("#{"+introspectedColumn.getJavaProperty() + "}");
                }else{
                    sb.append("#{record."+introspectedColumn.getJavaProperty() + "}");
                }
                answer.addElement(new TextElement(sb.toString()));
            }

            if (context.getPlugins()
                    .sqlMapUpdateByPrimaryKeySelectiveElementGenerated(answer,
                            introspectedTable)) {
                parentElement.addElement(answer);
            }
        }
    }
}

package generator.defined;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * @author zhangchenbiao
 * @FileName: MyPluginAdapter
 * @Description: 自定义生成器
 * @date 2021-11-18 14:11
 */
public class MyPluginAdapter extends PluginAdapter {

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    /**
     * model新增import
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType("java.io.Serializable");
        topLevelClass.addImportedType("lombok.Data");
        topLevelClass.addImportedType("lombok.Builder");
        topLevelClass.addImportedType("lombok.NoArgsConstructor");
        topLevelClass.addImportedType("lombok.AllArgsConstructor");
        topLevelClass.addImportedType("io.swagger.annotations.ApiModelProperty");
        topLevelClass.addAnnotation("@Data");
        topLevelClass.addAnnotation("@Builder");
        topLevelClass.addAnnotation("@NoArgsConstructor");
        topLevelClass.addAnnotation("@AllArgsConstructor");
        topLevelClass.addSuperInterface(new FullyQualifiedJavaType("java.io.Serializable"));
        return true;
    }

    /**
     * model不生成set方法
     * @param method
     * @param topLevelClass
     * @param introspectedColumn
     * @param introspectedTable
     * @param modelClassType
     * @return
     */
    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

    /**
     * model不生成get方法
     * @param method
     * @param topLevelClass
     * @param introspectedColumn
     * @param introspectedTable
     * @param modelClassType
     * @return
     */
    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

    /**
     * sql文件 新增动态sql
     * @param document
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        AbstractXmlElementGenerator elementGenerator = new CustomAbstractXmlElementGenerator();
        elementGenerator.setContext(context);
        elementGenerator.setIntrospectedTable(introspectedTable);
        elementGenerator.addElements(document.getRootElement());
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    /**
     * mapper文件备注
     * @param interfaze
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        interfaze.addJavaDocLine("/**");
        interfaze.addJavaDocLine(" * @author zhangchenbiao" );
        interfaze.addJavaDocLine(" * @date " + (new SimpleDateFormat("yyyy-MM-dd hh:mm")).format(new Date()));
        interfaze.addJavaDocLine(" */");
        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }

    /**
     * sql新增动态sql
     */
    public class CustomAbstractXmlElementGenerator extends AbstractXmlElementGenerator {

        @Override
        public void addElements(XmlElement parentElement) {
            /*String tableName =getTableName();
            // 增加base_query
            XmlElement sql = new XmlElement("sql");
            sql.addAttribute(new Attribute("id", "dynamicQuery"));
            //在这里添加where条件
            XmlElement selectTrimElement = new XmlElement("trim"); //设置trim标签
            selectTrimElement.addAttribute(new Attribute("prefix", "WHERE"));
            selectTrimElement.addAttribute(new Attribute("prefixOverrides", "AND | OR")); //添加where和and
            StringBuilder sb = new StringBuilder();
            for(IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
                XmlElement selectNotNullElement = new XmlElement("if"); //$NON-NLS-1$
                sb.setLength(0);
                sb.append("null != ");
                sb.append(introspectedColumn.getJavaProperty());
                selectNotNullElement.addAttribute(new Attribute("test", sb.toString()));
                sb.setLength(0);
                // 添加and
                sb.append(" and ");
                // 添加别名t
                sb.append("t.");
                sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
                // 添加等号
                sb.append(" = ");
                sb.append("#{"+ introspectedColumn.getJavaProperty() +"}");
                selectNotNullElement.addElement(new TextElement(sb.toString()));
                selectTrimElement.addElement(selectNotNullElement);
            }

            //sql.addElement(selectTrimElement);
            parentElement.addElement(sql);

            // 公用include
            XmlElement include = new XmlElement("include");
            include.addAttribute(new Attribute("refid", "dynamicQuery"));*/

        }

        private String getTableName() {
            String tableName = introspectedTable.getTableConfiguration().getTableName();
            String tableSuffix = tableName.substring(tableName.lastIndexOf("_") + 1);
            if(StringUtils.isNotBlank(tableSuffix) && tableSuffix.length() == 32){
                //企业库
                return tableName.substring(0, tableName.lastIndexOf("_")) + "";
            }
            //平台库
            return tableName;
        }

    }

    /**
     * 对数据库中的tinyint->byte 处理为 tinyint->integer
     * @param field
     * @param topLevelClass
     * @param introspectedColumn
     * @param introspectedTable
     * @param modelClassType
     * @return
     */
    @Override
    public boolean modelFieldGenerated(Field field,
                                       TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                       IntrospectedTable introspectedTable,
                                       ModelClassType modelClassType){
        int jdbcType = introspectedColumn.getJdbcType();
        if(jdbcType == Types.TINYINT){
            field.setType(new FullyQualifiedJavaType(Integer.class.getName()));
        }
        return true;
    }

}

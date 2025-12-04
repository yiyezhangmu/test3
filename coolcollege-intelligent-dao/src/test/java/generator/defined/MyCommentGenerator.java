package generator.defined;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.DefaultCommentGenerator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @author zhangchenbiao
 * @FileName: MyCommentGenerator
 * @Description:
 * @date 2021-11-18 11:09
 */
public class MyCommentGenerator extends DefaultCommentGenerator implements CommentGenerator {
    private String author;
    /**
     * 当前时间
     */
    private String currentDateStr;
    public static final ThreadLocal<String> authorName = new ThreadLocal<String>();

    public MyCommentGenerator() {
        currentDateStr = (new SimpleDateFormat("yyyy-MM-dd hh:mm")).format(new Date());
    }


    @Override
    public void addConfigurationProperties(Properties properties) {
        author = properties.getProperty("author");
        authorName.set(author);
    }

    /**
     * 字段注释
     * @param field
     * @param introspectedTable
     * @param introspectedColumn
     */
    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        String remarks = introspectedColumn.getRemarks();
        field.addAnnotation("@ApiModelProperty(\""+ remarks +"\")");
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {

    }

    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String remarks = introspectedTable.getRemarks();
        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" * " + remarks);
        topLevelClass.addJavaDocLine(" * @author   " + author);
        topLevelClass.addJavaDocLine(" * @date   " + currentDateStr);
        topLevelClass.addJavaDocLine(" */");
    }

    @Override
    public void addSetterComment(Method method,
                                 IntrospectedTable introspectedTable,
                                 IntrospectedColumn introspectedColumn) {

    }

    @Override
    public void addGeneralMethodComment(Method method,
                                        IntrospectedTable introspectedTable) {
        StringBuilder sb = new StringBuilder();

        method.addJavaDocLine("/**");
        method.addJavaDocLine(" *");
        sb.append(" * " + MethodName.getMethodComment(method.getName()));
        method.addJavaDocLine(sb.toString());
        method.addJavaDocLine(" * dateTime:" +currentDateStr);
        method.addJavaDocLine(" */");
    }

    @Override
    public void addComment(XmlElement xmlElement) {

    }

    public enum MethodName{
        INSERT_SELECTIVE("insertSelective","默认插入方法，只会给有值的字段赋值\n\t * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null"),
        SELECT_BY_PRIMARY_KEY("selectByPrimaryKey","默认查询方法，通过主键获取所有字段的值"),
        UPDATE_BY_PRIMARY_KEY_SELECTIVE("updateByPrimaryKeySelective","默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的"),
        DELETE_BY_PRIMARY_KEY("deleteByPrimaryKey","默认更新方法，根据主键物理删除"),;
        private String methodName;
        private String comment;

        MethodName(String methodName, String comment) {
            this.methodName = methodName;
            this.comment = comment;
        }

        public static String getMethodComment(String methodName){
            for (MethodName value : MethodName.values()) {
                if(value.methodName.equals(methodName)){
                    return value.comment;
                }
            }
            return null;
        }
    }

}

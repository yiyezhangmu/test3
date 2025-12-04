package com.coolcollege.intelligent.common.util;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: ScriptUtil
 * @Description: 脚本工具
 * @date 2022-01-21 16:48
 */
@Component
public class ScriptUtil {

    /**
     * Default statement separator within SQL scripts: {@code ";"}.
     */
    public static final String DEFAULT_STATEMENT_SEPARATOR = ";";

    /**
     * Fallback statement separator within SQL scripts: {@code "\n"}.
     * <p>Used if neither a custom separator nor the
     * {@link #DEFAULT_STATEMENT_SEPARATOR} is present in a given script.
     */
    public static final String FALLBACK_STATEMENT_SEPARATOR = "\n";


    public static final String EOF_STATEMENT_SEPARATOR = "^^^ END OF SCRIPT ^^^";

    /**
     * Default prefix for single-line comments within SQL scripts: {@code "--"}.
     */
    public static final String DEFAULT_COMMENT_PREFIX = "--";

    /**
     * Default start delimiter for block comments within SQL scripts: {@code "/*"}.
     */
    public static final String DEFAULT_BLOCK_COMMENT_START_DELIMITER = "/*";

    /**
     * Default end delimiter for block comments within SQL scripts: <code>"*&#47;"</code>.
     */
    public static final String DEFAULT_BLOCK_COMMENT_END_DELIMITER = "*/";

    /**
     * 变量开始字符$
     */
    public static final String VARIABLE_START_DELIMITER = "\\$\\{";

    /**
     * 变量开始字符#
     */
    public static final String VARIABLE_START_DELIMITER2 = "\\#\\{";

    /**
     * 变量结束字符
     */
    public static final String VARIABLE_END_DELIMITER = "}";

    /**
     * 单引号
     */
    public static final String SINGLE_QUOTE = "'";


    private final SqlSessionTemplate sqlSessionTemplate;
    private final DataSourceTransactionManager dataSourceTransactionManager;


    @Autowired
    public ScriptUtil(SqlSessionTemplate sqlSessionTemplate, DataSourceTransactionManager dataSourceTransactionManager) {
        this.sqlSessionTemplate = sqlSessionTemplate;
        this.dataSourceTransactionManager = dataSourceTransactionManager;
    }


    private static final Log logger = LogFactory.getLog(ScriptUtil.class);

    /**
     * Split an SQL script into separate statements delimited by the provided
     * separator string. Each individual statement will be added to the provided
     * {@code List}.
     * <p>Within the script, the provided {@code commentPrefix} will be honored:
     * any text beginning with the comment prefix and extending to the end of the
     * line will be omitted from the output. Similarly, the provided
     * {@code blockCommentStartDelimiter} and {@code blockCommentEndDelimiter}
     * delimiters will be honored: any text enclosed in a block comment will be
     * omitted from the output. In addition, multiple adjacent whitespace characters
     * will be collapsed into a single space.
     * @param resource the resource from which the script was read
     * @param script the SQL script; never {@code null} or empty
     * @param separator text separating each statement &mdash; typically a ';' or
     * newline character; never {@code null}
     * @param commentPrefix the prefix that identifies SQL line comments &mdash;
     * typically "--"; never {@code null} or empty
     * @param blockCommentStartDelimiter the <em>start</em> block comment delimiter;
     * never {@code null} or empty
     * @param blockCommentEndDelimiter the <em>end</em> block comment delimiter;
     * never {@code null} or empty
     * @param statements the list that will contain the individual statements
     * @throws ScriptException if an error occurred while splitting the SQL script
     */
    public static void splitSqlScript(@Nullable EncodedResource resource, String script,
                                      String separator, String commentPrefix, String blockCommentStartDelimiter,
                                      String blockCommentEndDelimiter, List<String> statements) throws ScriptException {

        Assert.hasText(script, "'script' must not be null or empty");
        Assert.notNull(separator, "'separator' must not be null");
        Assert.hasText(commentPrefix, "'commentPrefix' must not be null or empty");
        Assert.hasText(blockCommentStartDelimiter, "'blockCommentStartDelimiter' must not be null or empty");
        Assert.hasText(blockCommentEndDelimiter, "'blockCommentEndDelimiter' must not be null or empty");

        StringBuilder sb = new StringBuilder();
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean inEscape = false;

        for (int i = 0; i < script.length(); i++) {
            char c = script.charAt(i);
            if (inEscape) {
                inEscape = false;
                sb.append(c);
                continue;
            }
            // MySQL style escapes
            if (c == '\\') {
                inEscape = true;
                sb.append(c);
                continue;
            }
            if (!inDoubleQuote && (c == '\'')) {
                inSingleQuote = !inSingleQuote;
            }
            else if (!inSingleQuote && (c == '"')) {
                inDoubleQuote = !inDoubleQuote;
            }
            if (!inSingleQuote && !inDoubleQuote) {
                if (script.startsWith(separator, i)) {
                    // We've reached the end of the current statement
                    if (sb.length() > 0) {
                        statements.add(sb.toString());
                        sb = new StringBuilder();
                    }
                    i += separator.length() - 1;
                    continue;
                }
                else if (script.startsWith(commentPrefix, i)) {
                    // Skip over any content from the start of the comment to the EOL
                    int indexOfNextNewline = script.indexOf('\n', i);
                    if (indexOfNextNewline > i) {
                        i = indexOfNextNewline;
                        continue;
                    }
                    else {
                        // If there's no EOL, we must be at the end of the script, so stop here.
                        break;
                    }
                }
                else if (script.startsWith(blockCommentStartDelimiter, i)) {
                    // Skip over any block comments
                    int indexOfCommentEnd = script.indexOf(blockCommentEndDelimiter, i);
                    if (indexOfCommentEnd > i) {
                        i = indexOfCommentEnd + blockCommentEndDelimiter.length() - 1;
                        continue;
                    }
                    else {
                        throw new ScriptParseException(
                                "Missing block comment end delimiter: " + blockCommentEndDelimiter, resource);
                    }
                }
                else if (c == ' ' || c == '\n' || c == '\t') {
                    // Avoid multiple adjacent whitespace characters
                    if (sb.length() > 0 && sb.charAt(sb.length() - 1) != ' ') {
                        c = ' ';
                    }
                    else {
                        continue;
                    }
                }
            }
            sb.append(c);
        }

        if (StringUtils.hasText(sb)) {
            statements.add(sb.toString());
        }
    }

    /**
     * Read a script from the provided resource, using the supplied comment prefix
     * and statement separator, and build a {@code String} containing the lines.
     * <p>Lines <em>beginning</em> with the comment prefix are excluded from the
     * results; however, line comments anywhere else &mdash; for example, within
     * a statement &mdash; will be included in the results.
     * @param resource the {@code EncodedResource} containing the script
     * to be processed
     * @param commentPrefix the prefix that identifies comments in the SQL script &mdash;
     * typically "--"
     * @param separator the statement separator in the SQL script &mdash; typically ";"
     * @return a {@code String} containing the script lines
     * @throws IOException in case of I/O errors
     */
    private static String readScript(EncodedResource resource, @Nullable String commentPrefix,
                                     @Nullable String separator) throws IOException {

        LineNumberReader lnr = new LineNumberReader(resource.getReader());
        try {
            return readScript(lnr, commentPrefix, separator);
        }
        finally {
            lnr.close();
        }
    }

    /**
     * Read a script from the provided {@code LineNumberReader}, using the supplied
     * comment prefix and statement separator, and build a {@code String} containing
     * the lines.
     * <p>Lines <em>beginning</em> with the comment prefix are excluded from the
     * results; however, line comments anywhere else &mdash; for example, within
     * a statement &mdash; will be included in the results.
     * @param lineNumberReader the {@code LineNumberReader} containing the script
     * to be processed
     * @param commentPrefix the prefix that identifies comments in the SQL script &mdash;
     * typically "--"
     * @param separator the statement separator in the SQL script &mdash; typically ";"
     * @return a {@code String} containing the script lines
     * @throws IOException in case of I/O errors
     */
    public static String readScript(LineNumberReader lineNumberReader, @Nullable String commentPrefix,
                                    @Nullable String separator) throws IOException {

        String currentStatement = lineNumberReader.readLine();
        StringBuilder scriptBuilder = new StringBuilder();
        while (currentStatement != null) {
            if (commentPrefix != null && !currentStatement.startsWith(commentPrefix)) {
                if (scriptBuilder.length() > 0) {
                    scriptBuilder.append('\n');
                }
                scriptBuilder.append(currentStatement);
            }
            currentStatement = lineNumberReader.readLine();
        }
        appendSeparatorToScriptIfNecessary(scriptBuilder, separator);
        return scriptBuilder.toString();
    }

    private static void appendSeparatorToScriptIfNecessary(StringBuilder scriptBuilder, @Nullable String separator) {
        if (separator == null) {
            return;
        }
        String trimmed = separator.trim();
        if (trimmed.length() == separator.length()) {
            return;
        }
        // separator ends in whitespace, so we might want to see if the script is trying
        // to end the same way
        if (scriptBuilder.lastIndexOf(trimmed) == scriptBuilder.length() - trimmed.length()) {
            scriptBuilder.append(separator.substring(trimmed.length()));
        }
    }

    /**
     * Does the provided SQL script contain the specified delimiter?
     * @param script the SQL script
     * @param delim the string delimiting each statement - typically a ';' character
     */
    public static boolean containsSqlScriptDelimiters(String script, String delim) {
        boolean inLiteral = false;
        boolean inEscape = false;

        for (int i = 0; i < script.length(); i++) {
            char c = script.charAt(i);
            if (inEscape) {
                inEscape = false;
                continue;
            }
            // MySQL style escapes
            if (c == '\\') {
                inEscape = true;
                continue;
            }
            if (c == '\'') {
                inLiteral = !inLiteral;
            }
            if (!inLiteral && script.startsWith(delim, i)) {
                return true;
            }
        }

        return false;
    }


    public void executeSqlScript(EncodedResource resource, Map<String, Object> params) throws ScriptException {
        String commentPrefix = DEFAULT_COMMENT_PREFIX;
        String separator = DEFAULT_STATEMENT_SEPARATOR;
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Executing SQL script from " + resource);
            }
            long startTime = System.currentTimeMillis();
            String script;
            try {
                script = readScript(resource, commentPrefix, separator);
            }catch (IOException ex) {
                throw new CannotReadScriptException(resource, ex);
            }
            if (!EOF_STATEMENT_SEPARATOR.equals(separator) && !containsSqlScriptDelimiters(script, separator)) {
                separator = FALLBACK_STATEMENT_SEPARATOR;
            }
            List<String> statements = new ArrayList<>();
            splitSqlScript(resource, script, separator, commentPrefix, DEFAULT_BLOCK_COMMENT_START_DELIMITER,
                    DEFAULT_BLOCK_COMMENT_END_DELIMITER, statements);
            int stmtNumber = 0;
            SqlSession sqlSession = SqlSessionUtils.getSqlSession(
                    sqlSessionTemplate.getSqlSessionFactory(), ExecutorType.BATCH,
                    sqlSessionTemplate.getPersistenceExceptionTranslator());
            Connection connection = null;
            Statement stmt = null;
            try {
                connection = sqlSession.getConnection();
                stmt = connection.createStatement();
                connection.setAutoCommit(false);
                for (String statement : statements) {
                    stmtNumber++;
                    try {
                        statement = sqlVariableReplace(statement, params);
                        stmt.execute(statement);
                        int rowsAffected = stmt.getUpdateCount();
                        logger.info(rowsAffected + " returned as update count for SQL: " + statement);
                    }catch (SQLException ex) {
                        logger.error(ex);
                        ex.printStackTrace();
                        connection.rollback();
                        sqlSession.clearCache();
                        throw new ScriptStatementFailedException(statement, stmtNumber, resource, ex);
                    }
                }
                connection.commit();
                sqlSession.clearCache();
            }catch (Exception e){
                logger.error(e);
                e.printStackTrace();
                connection.rollback();
                sqlSession.clearCache();
                throw new Exception(e);
            }finally {
                try {
                    if(Objects.nonNull(stmt)){
                        stmt.close();
                    }
                }catch (Throwable ex) {
                    logger.trace("Could not close JDBC Statement", ex);
                }
                try {
                    if(Objects.nonNull(connection)){
                        connection.close();
                    }
                } catch (SQLException throwables) {
                    logger.trace("Could not close JDBC Statement", throwables);
                }
            }
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (logger.isDebugEnabled()) {
                logger.debug("Executed SQL script from " + resource + " in " + elapsedTime + " ms.");
            }
        }catch (Exception ex) {
            if (ex instanceof ScriptException) {
                throw (ScriptException) ex;
            }
            throw new UncategorizedScriptException(
                    "Failed to execute database script from resource [" + resource + "]", ex);
        }
    }

    /**
     * 处理sql变量
     * @param statement
     * @param params
     * @return
     */
    public String sqlVariableReplace(String statement, Map<String, Object> params){
        if(MapUtils.isEmpty(params)){
            return statement;
        }
        if(!statement.contains(VARIABLE_START_DELIMITER) && !statement.contains(VARIABLE_END_DELIMITER)){
            return statement;
        }
        for (String key : params.keySet()) {
            String realKey = VARIABLE_START_DELIMITER + key + VARIABLE_END_DELIMITER;
            String realKey2 = VARIABLE_START_DELIMITER2 + key + VARIABLE_END_DELIMITER;
            statement = statement.replaceAll(realKey, params.get(key).toString());
            statement = statement.replaceAll(realKey2, SINGLE_QUOTE + params.get(key) + SINGLE_QUOTE);

        }
        return statement;
    }
}

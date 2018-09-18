package com.dtstack.util.pagination;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.DefaultParameterHandler;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.dtstack.util.dialet.Dialect;
import com.dtstack.util.pagination.Page.CountMethod;
import com.dtstack.util.pulgin.ReflectHelper;

/**
 * Configuration demo in spring application context:
 * 
 * <pre>
 * &lt;bean id="pagePlugin" class="com.mytijian.pulgin.mybatis.pagination.PaginationInterceptor"&gt;
 * 		&lt;property name="properties"&gt;
 * 			&lt;props&gt;
 * 				&lt;prop key="dialect"&gt;com.mytijian.pulgin.jdbc.dialet.MySQLDialect&lt;/prop&gt;
 * 				&lt;prop key="sqlIdPattern"&gt;.*ByPage.*&lt;/prop&gt;
 * 			&lt;/props&gt;
 * 		&lt;/property&gt;
 * 	&lt;/bean&gt;
 * 	&lt;bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean"&gt;  
 * 		....
 * 		&lt;property name="plugins"&gt;
 * 			&lt;array&gt;
 * 				&lt;ref bean="pagePlugin" /&gt;
 * 			&lt;/array&gt;
 * 		&lt;/property&gt;
 *    &lt;/bean&gt;
 * </pre>
 * 
 * @author twu
 * 
 * 
 */
@Intercepts({
		@Signature(method = "query", type = Executor.class, args = {
				MappedStatement.class, Object.class, RowBounds.class,
				ResultHandler.class }),
		@Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }) })
public class PaginationInterceptor implements Interceptor,
		ApplicationContextAware {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private String sqlIdPattern = "";
	private Dialect dialectObject = null; // 数据库方言

	private ApplicationContext context;

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object target = invocation.getTarget();
		if (target instanceof RoutingStatementHandler) {
			RoutingStatementHandler statementHandler = (RoutingStatementHandler) target;
			BaseStatementHandler delegate = (BaseStatementHandler) ReflectHelper
					.getValueByFieldName(statementHandler, "delegate");
			MappedStatement mappedStatement = (MappedStatement) ReflectHelper
					.getValueByFieldName(delegate, "mappedStatement");
			/**
			 * 通过ＩＤ来区分是否需要分页．.*ByPage.*
			 */
			if (mappedStatement != null
					&& mappedStatement.getId().matches(sqlIdPattern)) { // 拦截需要分页的SQL
				BoundSql boundSql = delegate.getBoundSql();
				String sql = boundSql.getSql();

				Connection connection = (Connection) invocation.getArgs()[0];
				handlePagination(connection, boundSql, mappedStatement);

				Page page = getPageParameter(boundSql.getParameterObject());
				if (page != null) {
					if (page.getPageSize() == -1) {
						ReflectHelper.setValueByFieldName(boundSql, "sql", sql); // pageSize == -1,还原sql,不进行分页
					} else {
						String pageSql = generatePagesSql(sql, page);
						ReflectHelper.setValueByFieldName(boundSql, "sql", pageSql); // 将分页sql语句反射回BoundSql.
					}
				}
			}
		}
		if (target instanceof CachingExecutor) {
			MappedStatement mappedStatement = (MappedStatement) invocation
					.getArgs()[0];
			if (mappedStatement != null
					&& mappedStatement.getId().matches(sqlIdPattern)
					&& mappedStatement.getCache() != null) {
				mappedStatement.getCache().clear();
			}

			// Object parameterObject = invocation.getArgs()[1];
			// BoundSql boundSql = mappedStatement.getSqlSource().getBoundSql(
			// parameterObject);
		}
		return invocation.proceed();
	}

	/**
	 * Fill rowCount in Page object and add page related SQLs
	 * 
	 * @param connection
	 * @param boundSql
	 * @param mappedStatement
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 */
	private void handlePagination(Connection connection, BoundSql boundSql,
			MappedStatement mappedStatement) throws InvocationTargetException,
			IllegalAccessException, SQLException {
		Object parameterObject = boundSql.getParameterObject();// 分页SQL<select>中parameterType属性对应的实体参数，即Mapper接口中执行分页方法的参数,该参数不得为空
		if (parameterObject == null) {
			return;
		}

		Page page = getPageParameter(parameterObject);
		if (page == null) {
			return;
		}

		if (page.getRowCount() < 0 && page.getCountMethod()==CountMethod.Count) { // Just get row count on first page
			int rowCount = getRowCount(connection, boundSql, mappedStatement,
					parameterObject);
			page.setRowCount(rowCount);
		}
		
		//if more than last page, set to last page. if the whole row count is 0, do not change 
		// current page
		int totalPage = (page.getRowCount() + page.getPageSize() -1) / page.getPageSize();
		if(page.getCurrentPage() > totalPage && page.getRowCount() > 0) {
			page.setCurrentPage(totalPage);
		}

	}

	private int getRowCount(Connection connection, BoundSql boundSql,
			MappedStatement mappedStatement, Object parameterObject)
			throws SQLException {
		PreparedStatement countStmt = null;
		ResultSet rs = null;
		try {
			String sql = boundSql.getSql();
			if (sql.toUpperCase().indexOf("ORDER BY")!=-1) {
				sql= sql.substring(0, sql.toUpperCase().indexOf("ORDER BY"));
			}
			
			String countSql = "select count(0) from (" + sql
					+ ") tmp_count"; // 记录统计
			countStmt = connection.prepareStatement(countSql);
			ReflectHelper.setValueByFieldName(boundSql, "sql", countSql);
			DefaultParameterHandler parameterHandler = new DefaultParameterHandler(
					mappedStatement, parameterObject, boundSql);
			parameterHandler.setParameters(countStmt);
			rs = countStmt.executeQuery();
			int count = 0;
			if (rs.next()) {
				count = ((Number) rs.getObject(1)).intValue();
			}
			return count;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					log.warn("execute count SQL failed on resultset close", e);
				}
			}
			if (countStmt != null) {
				try {
					countStmt.close();
				} catch (Exception e) {
					log.warn("execute count SQL failed on statement close", e);
				}
			}
		}
	}

	private Page getPageParameter(Object parameterObject) {
		Page page = null;
		if (parameterObject instanceof Page) { // just page
			page = (Page) parameterObject;
		} else if (parameterObject instanceof Map) { // get page from map
			Map<?, ?> paramMap = (Map<?, ?>) parameterObject;
			for (Map.Entry<?, ?> entry : paramMap.entrySet()) {
				if (entry.getValue() instanceof Page) {
					page = (Page) entry.getValue();
					break;
				}
			}
		} else { // get page from class
			page = ReflectHelper.getValueByFieldType(parameterObject,
					Page.class);
		}
		return page;
	}

	/**
	 * 根据数据库方言，生成特定的分页sql
	 * 
	 * @param sql
	 * @param page
	 * @return
	 */
	private String generatePagesSql(String sql, Page page) {
		if (page.getOrderBys() != null) {
			List<String> orderByList = new ArrayList<String>(Arrays.asList(page
					.getOrderBys()));
			orderByList.removeAll(Arrays.asList(null, ""));
			String orderBys = StringUtils.join(orderByList, ',');
			if (orderByList.size() > 0) {
				int idx = sql.toUpperCase().indexOf("ORDER BY") + 9;
				if (idx > 9) {
					sql = sql.substring(0, idx) + orderBys + ','
							+ sql.substring(idx);
				} else {
					sql = sql + " ORDER BY " + orderBys;
				}
			}
		}

		if (dialectObject != null) {
			int pageSize = page.getPageSize();
			int startIndex = (page.getCurrentPage() <= 0 ? 0 : page
					.getCurrentPage() - 1) * pageSize;
			if(page.getOffset() != 0) {
				startIndex = startIndex + page.getOffset();
				pageSize = pageSize - page.getOffset();
			}
			if (startIndex < 0) {
				startIndex = 0;
			}
			//For NextPage count Mehtod
			if(page.getCountMethod() == CountMethod.NextPage) {
				pageSize = pageSize + 1;
			}
			sql = dialectObject.getLimitString(sql, startIndex,
					pageSize);
		}
		return sql;
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties props) {
		String dialect = props.getProperty("dialect");
		if (!StringUtils.isBlank(dialect)) {
			if (context != null) {
				dialectObject = (Dialect) context.getBean(dialect);
			}
			if (dialectObject == null) {
				try {
					dialectObject = (Dialect) Class.forName(dialect)
							.getDeclaredConstructor().newInstance();
				} catch (Exception e) {
					throw new RuntimeException(dialect + ", init fail!\n", e);
				}
			}
		}
		sqlIdPattern = props.getProperty("sqlIdPattern");
	}

	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.context = context;
	}

}

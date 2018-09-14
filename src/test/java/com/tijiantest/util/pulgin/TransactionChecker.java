package com.tijiantest.util.pulgin;

import java.sql.Connection;
import java.util.Properties;

import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 由于数据库读写分离是通过是否加事务来决定写主库还是从库，所以所有修改数据的操作都应该加上事务。
 * 这个类的作用就是检查除了select以外所有的SQL操作都被加上事务
 * @author twu
 *
 */
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }) })
public class TransactionChecker implements Interceptor {

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		boolean transactioned = TransactionSynchronizationManager
				.isActualTransactionActive();

		Object target = invocation.getTarget();
		if (target instanceof RoutingStatementHandler) {
			RoutingStatementHandler statementHandler = (RoutingStatementHandler) target;
			BaseStatementHandler delegate = (BaseStatementHandler) ReflectHelper
					.getValueByFieldName(statementHandler, "delegate");

			BoundSql boundSql = delegate.getBoundSql();
			String sql = boundSql.getSql();
			if (!sql.trim().substring(0, 6).toUpperCase().equals("SELECT")
					&& !transactioned) {
				throw new java.lang.IllegalStateException(
						"This Service method should be transactioned!");
			}
		}
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		// TODO Auto-generated method stub

	}

}

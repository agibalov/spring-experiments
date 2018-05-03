package io.agibalov.transactions;

import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.sql.Connection;
import java.sql.SQLException;

// based on http://www.byteslounge.com/tutorials/spring-change-transaction-isolation-level-example
public class TransactionIsolationLevelAwareHibernateJpaDialect extends HibernateJpaDialect {
    @Override
    public Object beginTransaction(
            EntityManager entityManager,
            TransactionDefinition definition) throws PersistenceException, SQLException, TransactionException {

        TransactionData transactionData = new TransactionData();

        Session session = (Session)entityManager.getDelegate();
        session.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                Integer originalIsolationLevel = DataSourceUtils.prepareConnectionForTransaction(connection, definition);

                transactionData.originalIsolationLevel = originalIsolationLevel;
                transactionData.connection = connection;
            }
        });

        entityManager.getTransaction().begin();
        Object springTransactionData = prepareTransaction(entityManager, definition.isReadOnly(), definition.getName());
        transactionData.springTransactionData = springTransactionData;

        return transactionData;
    }

    @Override
    public void cleanupTransaction(Object transactionData) {
        TransactionData myTransactionData = (TransactionData)transactionData;
        super.cleanupTransaction(myTransactionData.springTransactionData);

        if(myTransactionData.originalIsolationLevel != null) {
            DataSourceUtils.resetConnectionAfterTransaction(
                    myTransactionData.connection,
                    myTransactionData.originalIsolationLevel);
        }
    }

    private static class TransactionData {
        public Integer originalIsolationLevel;
        public Connection connection;
        public Object springTransactionData;
    }
}

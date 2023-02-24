package com.solace.samples;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class SolaceQueuePublisherWithPool {
    private static final String QUEUE_NAME = "Newq";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";
    private static final String HOST = "localhost:55555";
    private static final int MAX_CONNECTIONS = 10;

    static int i;

    public static void main(String[] args) {
        try {
            // Create connection factory
            ConnectionFactory connectionFactory = SolJmsUtility.createConnectionFactory();
            ((SolConnectionFactory) connectionFactory).setHost(HOST);
           // ((SolConnectionFactory) connectionFactory).setVPN("your-vpn");
            ((SolConnectionFactory) connectionFactory).setUsername(USERNAME);
            ((SolConnectionFactory) connectionFactory).setPassword(PASSWORD);

            // Create object pool for connections
            GenericObjectPool<Connection> connectionPool = new GenericObjectPool<>(new ConnectionFactoryPooledObjectFactory(connectionFactory));
            connectionPool.setMaxTotal(MAX_CONNECTIONS);

            // Get a connection from the pool
            for (i=0 ; i<1000; i++) {
                Connection connection = connectionPool.borrowObject();
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                // Create message producer
                MessageProducer producer = session.createProducer(session.createQueue(QUEUE_NAME));

                // Create message
                TextMessage message = session.createTextMessage("Hello, Solace!");

                // Send message
                producer.send(message);

                System.out.println("Message sent successfully!");

                // Cleanup
                producer.close();
                session.close();

                // Return connection to the pool
                connectionPool.returnObject(connection);

                // Shutdown the pool

            }
            connectionPool.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class ConnectionFactoryPooledObjectFactory extends org.apache.commons.pool2.BasePooledObjectFactory<Connection> {
        private final ConnectionFactory connectionFactory;

        public ConnectionFactoryPooledObjectFactory(ConnectionFactory connectionFactory) {
            this.connectionFactory = connectionFactory;
        }

        @Override
        public Connection create() throws Exception {
            return connectionFactory.createConnection();
        }

        @Override
        public PooledObject<Connection> wrap(Connection connection) {
            return new DefaultPooledObject<>(connection);
        }

        @Override
        public void destroyObject(PooledObject<Connection> p) throws Exception {
            p.getObject().close();
        }

        @Override
        public boolean validateObject(PooledObject<Connection> p) {
            try {
                return p.getObject().getMetaData() != null;
            } catch (Exception e) {
                return false;
            }
        }
    }
}

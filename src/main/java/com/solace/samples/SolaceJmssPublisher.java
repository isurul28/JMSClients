package com.solace.samples;

import javax.jms.*;
import javax.naming.*;
import java.util.*;

public class SolaceJmssPublisher {
    private static final int MAX_CONNECTIONS = 10;

    public static void main(String[] args) throws Exception {

        // Set up Solace JMS connection factory and destination

        Properties properties = new Properties();
        properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, "com.solacesystems.jndi.SolJNDIInitialContextFactory");
        properties.setProperty(Context.PROVIDER_URL, "smf://localhost:55555");
        properties.setProperty(Context.SECURITY_PRINCIPAL, "admin");
        properties.setProperty(Context.SECURITY_CREDENTIALS, "admin");
        Context context = new InitialContext(properties);
        ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("/jms/cf/default");
        Destination destination = (Destination) context.lookup("Newq");



        // Create JMS connection, session, and message producer
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageProducer messageProducer = session.createProducer(destination);

        // Create JMS message
        TextMessage message = session.createTextMessage("Hello, Solace!");

        // Publish message to Solace queue
        messageProducer.send(message);

        // Clean up resources
        messageProducer.close();
        session.close();
        connection.close();
    }
}

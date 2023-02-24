package com.solace.samples;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;

public class SolaceQueuePublisher {
    private static final String QUEUE_NAME = "Newq";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";
    private static final String HOST = "localhost:55555";

    public static void main(String[] args) {
        try {
            // Create connection factory
            ConnectionFactory connectionFactory = SolJmsUtility.createConnectionFactory();
            ((SolConnectionFactory) connectionFactory).setHost(HOST);
            //((SolConnectionFactory) connectionFactory).setVPN("your-vpn");
            ((SolConnectionFactory) connectionFactory).setUsername(USERNAME);
            ((SolConnectionFactory) connectionFactory).setPassword(PASSWORD);

            // Create connection and session
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create message producer
            MessageProducer producer = session.createProducer(session.createQueue(QUEUE_NAME));

            // Create message
            TextMessage message = session.createTextMessage("Hello, Solace!");

            // Send message
            producer.send(message);

            System.out.println("Message sent successfully!");

            // Cleanup
            session.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


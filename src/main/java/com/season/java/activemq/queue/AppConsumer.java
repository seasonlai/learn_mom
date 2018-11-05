package com.season.java.activemq.queue;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by Administrator on 2018/11/3.
 */
public class AppConsumer {

    private static final String url = "tcp://localhost:61616";
    private static final String queueName = "queue-test";

    public static void main(String... args) throws JMSException {
        //具体是ActiveMQ实现的工厂
        ConnectionFactory factory = new ActiveMQConnectionFactory("season","123456",url);
        //创建连接对象
        Connection connection = factory.createConnection();
        //开始连接
        connection.start();
        //创建会话
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //创建目标
        Queue queue = session.createQueue(queueName);
        //创建消费者
        MessageConsumer consumer = session.createConsumer(queue);
        //接收消息
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                TextMessage msg = (TextMessage) message;
                try {
                    System.out.println("接受到信息：" + msg.getText());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
        //最后关闭
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }));
    }

}

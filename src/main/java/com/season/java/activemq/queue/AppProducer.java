package com.season.java.activemq.queue;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by Administrator on 2018/11/3.
 */
public class AppProducer {

    private static final String url="tcp://localhost:61616";
    private static final String queueName="queue-test";

    public static void main(String... args) throws JMSException {
        //具体是ActiveMQ实现的工厂(没密码可只传url)
        ConnectionFactory factory = new ActiveMQConnectionFactory("season","123456",url);
        //创建连接对象
        Connection connection = factory.createConnection();
        //开始连接
        connection.start();
        //创建会话，第一个参数表示是否用事务，第二个参数用以下值
        //AUTO_ACKNOWLEDGE 自动确认，客户端发送和接收消息不需要做额外的工作
        //CLIENT_ACKNOWLEDGE 客户端确认。客户端接收到消息后，必须调用javax.activemq.Message的acknowledge方法。jms服务器才会删除消息
        //DUPS_OK_ACKNOWLEDGE 允许副本的确认模式。一旦接收方应用程序的方法调用从处理消息处返回，会话对象就会确认消息的接收；而且允许重复确认。
        //SESSION_TRANSACTED 事务方式，如果第一个参数为true，就会用改模式
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //创建目标
        Queue queue = session.createQueue(queueName);
        //创建生产者
        MessageProducer producer = session.createProducer(queue);
        //发送消息
        for (int i = 0; i < 100; i++) {
            TextMessage tmsg = session.createTextMessage("test" + i);
            producer.send(tmsg);
            System.out.println("发送消息："+tmsg.getText());
        }
        //最后关闭
        connection.close();
    }

}

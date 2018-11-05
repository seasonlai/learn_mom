package com.season.spring.activemq.consumer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * Created by Administrator on 2018/11/3.
 */
public class ConsumerMessageListener implements MessageListener {

    @Override
    public void onMessage(Message message) {
        TextMessage tmsg= (TextMessage) message;
        try {
            System.out.println("接收到消息："+tmsg.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}

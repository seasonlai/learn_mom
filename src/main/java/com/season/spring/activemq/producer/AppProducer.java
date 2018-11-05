package com.season.spring.activemq.producer;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Administrator on 2018/11/3.
 */
public class AppProducer {

    public static void main(String... args){

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("activemq/jms-producer.xml");
        ProducerService service = context.getBean(ProducerServiceImpl.class);
        for (int i = 0; i < 100; i++) {
            service.sendMessage("test "+i);
        }
        context.close();
    }

}

package com.season.spring.activemq.consumer;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Administrator on 2018/11/3.
 */
public class AppConsumer {

    public static void main(String... args){
        ApplicationContext context=new ClassPathXmlApplicationContext("activemq/jms-consumer.xml");
    }

}

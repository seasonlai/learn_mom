package com.season.java.rabbitmq.rpc;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2018/11/6.
 */
public class RpcServer {

    private static final String IP_ADDRESS = "120.79.12.83";
    private static final int PORT = 5672;
    private static final String RPC_QUEUE_NAME = "rpc_queue";

    public static void main(String... args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(IP_ADDRESS);
        factory.setPort(PORT);
        factory.setUsername("root");
        factory.setPassword("root");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //--------分割线-----------
        channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
        channel.basicQos(1);
        System.out.println("Awaiting RPC request");
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder()
                        .correlationId(properties.getCorrelationId())
                        .build();
                StringBuilder response = new StringBuilder();
                String req = new String(body);
                System.out.println("收到请求："+req);
                StringTokenizer tokenizer = new StringTokenizer(req, ",");
                int count = 0;
                try {
                    while (tokenizer.hasMoreTokens()) {
                        String token = tokenizer.nextToken();
                        count += Integer.parseInt(token);
                        response.append(token).append("+");
                    }
                    response.deleteCharAt(response.length() - 1).append("=").append(count);
                } catch (RuntimeException e) {
                    response.delete(0,response.length()).append(e.getMessage());
                }finally {
                    channel.basicPublish("",properties.getReplyTo(),replyProps,response.toString().getBytes("utf-8"));
                    channel.basicAck(envelope.getDeliveryTag(),false);
                }
            }
        };
        channel.basicConsume(RPC_QUEUE_NAME,false,consumer);

        //等待回调函数执行完毕之后，关闭资源
        TimeUnit.SECONDS.sleep(30);
        channel.close();
        connection.close();
    }

}

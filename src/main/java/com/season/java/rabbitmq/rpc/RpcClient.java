package com.season.java.rabbitmq.rpc;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Created by Administrator on 2018/11/6.
 */
public class RpcClient {


    private final String REQUEST_QUEUE_NAME = "rpc_queue";
    private final String IP_ADDRESS = "120.79.12.83";
    private final int PORT = 5672;
    private final String replyQueueName;
    private final DefaultConsumer consumer;
    private Connection connection;
    private Channel channel;
    private String corrId;
    private CyclicBarrier barrier;
    private String response;

    public RpcClient() throws IOException, TimeoutException {
        Address[] addresses = new Address[]{
                new Address(IP_ADDRESS, PORT)
        };
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("root");
        factory.setPassword("root");

        connection = factory.newConnection(addresses);
        channel = connection.createChannel();
        replyQueueName = channel.queueDeclare().getQueue();
        barrier = new CyclicBarrier(2);
        consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                if (properties.getCorrelationId().equals(corrId)) {
                    response = new String(body);
                    try {
                        barrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        channel.basicConsume(replyQueueName, true, consumer);
    }

    public String call(String req) throws IOException, InterruptedException, BrokenBarrierException {
        corrId = UUID.randomUUID().toString();
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();
        barrier.reset();
        channel.basicPublish("", REQUEST_QUEUE_NAME, props, req.getBytes());
        barrier.await();
        return response;
    }

    public void close() throws IOException {
        connection.close();
    }

    public static void main(String... args) throws IOException, TimeoutException, InterruptedException, BrokenBarrierException {
        RpcClient client = new RpcClient();
        String req = "1,2,3,5";
        System.out.println("开始请求求和：" + req);
        String result = client.call(req);
        System.out.println("结果：" + result);
        client.close();
    }
}
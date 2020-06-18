package com.ymy.rabbitmq.exchange.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

/**
 * 消费者 接收Topic Exchange绑定的Queue的消息
 * 消费者先启动：声明Exchange、Queue和绑定关系
 */
public class ConsumerTopicExchange {
    public static final String HOST = "192.168.110.133";
    public static final String EXCHANGE_NAME = "test_topic_exchange";
    public static final String EXCHANGE_TYPE = "topic";
    public static final String ROUTING_KEY = "user.#";
    public static final String QUEUE_NAME = "test_topic_queue";

    public static void main(String[] args) throws Exception {
        // 1、创建ConnectionFactory
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(HOST); // ConnectionFactory 默认端口是5672 virtualHost是"/"

        // 2、通过创建ConnectionFactory创建Connection
        Connection connection = connectionFactory.newConnection();

        // 3、通过Connection创建Channel
        Channel channel = connection.createChannel();

        /**
         *  4、声明Exchange
         *  exchangeDeclare(String exchange,String type,boolean durable,boolean autoDelete,boolean internal,Map<String, Object> arguments)
         */
        channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE, true, false, false, null);

        /**
         * 5、声明队列
         * queueDeclare(String queue, boolean durable, boolean exclusive, boolean autoDelete,Map<String, Object> arguments)
         */
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        /**
         * 6、Exchange和Queue的绑定关系
         * queueBind(String queue, String exchange, String routingKey, Map<String, Object> arguments)
         */
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY, null);

        // 7、创建消费者
        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);

        /**
         * 8、channel的设置
         * basicConsume(String queue, boolean autoAck, Consumer callback)
         */
        channel.basicConsume(QUEUE_NAME, true, queueingConsumer);

        while (true) {
            // 9、接收消息
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
            String msg = new String(delivery.getBody());
            System.out.println("*******消费端接收到的消息为=====>" + msg);
        }
    }
}

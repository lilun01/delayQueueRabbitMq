/**
 * @Copyright (c) 2018/8/19, Lianjia Group All Rights Reserved.
 */
package com.wilson.rabbitmq.receives;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.wilson.rabbitmq.model.CreateOrderVo;
import com.wilson.rabbitmq.utils.Message;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

/**
 * 
 * @Title: OrderDelayConsumer.java
 * @Description: 消息队列消费者，消费从其他队列过期来的消息，实现延迟消费
 * @author lilun
 * @date 2020-05-21 03:50:02 
 * @version 1.0
 */
@Component
@RabbitListener(queues = "process_queue")
@Slf4j
public class OrderDelayConsumer {

    @RabbitHandler
    public void orderConsumer(Message message,@Headers Map<String, Object> headers, Channel channel) throws IOException{

        // 接收到处理队列中的消息的，就是指定时间过期的消息
        // 这里处理每一条消息中的订单编号，去查询对应的订单支付状态，如果处于未支付状态，就取消用户的订单
    	Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
    	boolean multiple = false;
        try {
            CreateOrderVo orderVo = JSONObject.parseObject(message.getContent(), CreateOrderVo.class);
            // 获取订单编号，去查询对应的支付结果
            log.info("处理process_queue队列订单编号为: {}", orderVo.getOrderNo());
            channel.basicAck(deliveryTag, multiple);
        } catch (Exception e) {
            log.error("订单消息解析异常，请检查消息格式是否正确", message.getContent());
            channel.basicNack(deliveryTag, multiple,false);
        }
    }
}

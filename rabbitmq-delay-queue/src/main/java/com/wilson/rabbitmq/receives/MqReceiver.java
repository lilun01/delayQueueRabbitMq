package com.wilson.rabbitmq.receives;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

/**
 * 
 * @Title: MqReceiver.java
 * @Description: 消息处理者 处理json消息
 * @author lilun
 * @date 2020-05-21 04:15:07 
 * @version 1.0
 */
@Component
public class MqReceiver {
	private static Log logger = LogFactory.getLog(MqReceiver.class);
	
	 
	
	
	/**
	 * @Description: 接收mq发来的消息,动态配置queue，使用时放开注释即可
	 * @param content
	 * @param headers
	 * @param channel
	 * @param msg
	 * @throws IOException
	 * @author lilun
	 * @date 2020-05-21 04:13:14
	 */
	//@RabbitListener(bindings = @QueueBinding(value = @Queue(value = "${rmpirs-mq.heartbeat-manage.queue}", durable = "true"), exchange = @Exchange(name = "${rmpirs-mq.heartbeat-manage.exchange}", durable = "true", type = ExchangeTypes.TOPIC), key = "${rmpirs-mq.heartbeat-manage.routing-key}"))
	//@RabbitHandler
	public void receiveMessage(@Payload byte[] content, @Headers Map<String, Object> headers, Channel channel,Message msg)
			throws IOException {
		Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
		boolean multiple = false;
		try {
			String result=new String(content,"UTF-8");
			logger.info("收到发来的消息="+result);
			channel.basicAck(deliveryTag, multiple);
		}catch (Exception e) {
			logger.error(e.getMessage());
			channel.basicNack(deliveryTag, multiple,false);
		}
	}
	
 
}

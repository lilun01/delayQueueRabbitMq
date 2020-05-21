/**
 * @Copyright (c) 2018/8/19, Lianjia Group All Rights Reserved.
 */
package com.wilson.rabbitmq.service;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilson.rabbitmq.config.RabbitQueueConfig;
import com.wilson.rabbitmq.enums.ErrorCodeEnum;
import com.wilson.rabbitmq.exception.SystemException;
import com.wilson.rabbitmq.utils.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 延迟消息service
 *
 * @author wilson wei
 * @version 1.0
 * @since 2018/8/19 10:02
 */
@Service
@Slf4j
public class DelaySendService {

    @Autowired
    private AmqpTemplate rabbitTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 发送延时消息，每个消息都自己有自己的过期时间
     *
     * @param msg 消息
     * @author wilson wei
     * @date 10:03 2018/8/19
     */
    public void sendDelayMessage(Message msg) {

        // 消息发送时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("消息发送时间为: {}", sdf.format(new Date()));

        // 设置发送时间，开始发送
        try {
            rabbitTemplate.convertAndSend(RabbitQueueConfig.DELAY_EXCHANGE, RabbitQueueConfig.DELAY_ROUTING_KEY, msg,
                    message -> {
                        message.getMessageProperties().setExpiration(String.valueOf(msg.getTtl()));
                        return message;
                    });
        } catch (AmqpException e) {
            log.error("消息发送失败，请检查消息中间件是否正常", JSONObject.toJSONString(msg));
            throw new SystemException(ErrorCodeEnum.UNKNOWING_ERROR);
        }
    }

    /**
     * 发送消息，至指定过期时间的队列中。
     * 适用于一些定时任务
     * @param msg
     * @author wilson wei
     * @date 10:40 2018/8/19
     */
    public void sendDelayQueueMessage(Message msg) {

        // 消息发送时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("消息发送时间为: {}", sdf.format(new Date()));

        // 设置发送时间，开始发送
        try {
            rabbitTemplate.convertAndSend(RabbitQueueConfig.DELAY_QUEUE_EXCHANGE, RabbitQueueConfig.DELAY_ROUTING_KEY, msg);
        } catch (AmqpException e) {
            log.error("消息发送失败，请检查消息中间件是否正常", JSONObject.toJSONString(msg));
            throw new SystemException(ErrorCodeEnum.UNKNOWING_ERROR);
        }
    }
    
    /**
	* 描述:  运营平台下发设备配置数据数据到物联网平台
	* @author lilun
	* @param Object jsonStr 需要发送的json传 JSONObject.toJSON(sendMqInfoDto) ;
	* @return boolean  true 成功  false 失败
	* @date 2019-09-24 15:13:42
	 */	
	public boolean sendMsg(Object jsonStr) {
		boolean result = true;
		try {
			org.springframework.amqp.core.Message msg = MessageBuilder.withBody(objectMapper.writeValueAsBytes(jsonStr)).build();
			rabbitTemplate.convertAndSend(RabbitQueueConfig.DELAY_QUEUE_EXCHANGE, RabbitQueueConfig.DELAY_ROUTING_KEY,msg);
			log.info("发送成功");
		} catch (Exception e) {
			log.error(e.getMessage());
			result = false;
		}
		return result;
	}
    
    
}

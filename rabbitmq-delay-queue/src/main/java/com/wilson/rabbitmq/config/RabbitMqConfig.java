package com.wilson.rabbitmq.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
* <p>描述: mq配置</p>
* <p>公司: 瑞华康源科技有限公司</p>
* <p>版权: rivamed-2019</p>
* @author lilun
* @date 2019年9月26日
* @version V1.0
 */
@Configuration
public class RabbitMqConfig {
	Logger log = LoggerFactory.getLogger(RabbitMqConfig.class);
    
    /**
     * 连接工厂
     */
    @Autowired
    private ConnectionFactory connectionFactory;
    
	/**
	 * 描述:队列名,定义rabbit template用于数据的接收和发送;
	 *  备注: 配置exchange和queue
	 * @author lilun
	 * @date 2019-09-19 15:12:30
	 */
    @Bean
    public RabbitTemplate rabbitTemplate() {
    	RabbitTemplate template = new RabbitTemplate(connectionFactory);
    	// 使用jackson 消息转换器
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        template.setEncoding("UTF-8");
        // 消息发送失败返回到队列中，yml需要配置 publisher-returns: true
        template.setMandatory(true);
        template.setReplyTimeout(10*1000L);
        template.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
        });
        // 消息确认，yml需要配置 publisher-confirms: true
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("消息发送到exchange成功");
            } else {
                log.info("消息发送到exchange失败,原因: "+cause);
            }
        });
    	
        return template;
    }
    
    @Bean
    public Jackson2JsonMessageConverter jackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
   
}
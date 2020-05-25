package com.imooc.distribution.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * kafka相关接口定义
 */
public interface KafkaService {

    /**
     * 消费优惠券Kafka信息
     * @param record {@link ConsumerRecord}
     */
    void consumeCouponKafkaMessage(ConsumerRecord<?,?> record);
}

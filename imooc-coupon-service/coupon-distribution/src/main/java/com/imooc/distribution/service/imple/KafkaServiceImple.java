package com.imooc.distribution.service.imple;

import com.alibaba.fastjson.JSON;
import com.imooc.distribution.constant.Constant;
import com.imooc.distribution.constant.CouponStatus;
import com.imooc.distribution.dao.CouponDao;
import com.imooc.distribution.pojo.Coupon;
import com.imooc.distribution.service.KafkaService;
import com.imooc.distribution.vo.CouponKafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * kafka 相关服务接口实现
 * 核心思想 : 是将Cache 中的Coupon 状态变化同步到DB中
 */

@Slf4j
@Component
public class KafkaServiceImple implements KafkaService {

    /*Coupon Dao*/
    private final CouponDao couponDao;

    @Autowired
    public KafkaServiceImple(CouponDao couponDao) {
        this.couponDao = couponDao;
    }

    /**
     * 消费优惠券Kafka信息
     *
     * @param record {@link ConsumerRecord}
     */
    @Override
    @KafkaListener(topics = {Constant.TOPIC}, groupId = "imooc-distribution-1")
    public void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record) {
        //可以为空
        Optional<?> kafkaOption = Optional.ofNullable(record.value());
        //如果存在消息
        if (kafkaOption.isPresent()) {
            Object message = kafkaOption.get();
            CouponKafkaMessage couponInfo = JSON.parseObject(
                    message.toString(), CouponKafkaMessage.class
            );
            log.info("Receive CouponKafkaMessage: {}", message.toString());

            CouponStatus status = CouponStatus.of(couponInfo.getStatus());
            switch (status) {
                case USABLE:
                    break;
                case USED:
                    processUsedCoupons(couponInfo, status);
                    break;
                case EXPIRED:
                    processExpireCoupons(couponInfo, status);
                    break;
            }
        }
    }

    /**
     * 处理已使用的优惠券
     *
     * @param kafkaMessage
     * @param status
     */
    private void processUsedCoupons(CouponKafkaMessage kafkaMessage,
                                    CouponStatus status) {
        //可以根据优惠券的类型对用户进行不同的操作
        //TODO 给用户发送短信
        processCouponByStatus(kafkaMessage, status);
    }

    /**
     * 处理已过期的优惠券
     *
     * @param kafkaMessage
     * @param status
     */
    private void processExpireCoupons(CouponKafkaMessage kafkaMessage,
                                      CouponStatus status) {
        //TODO 给用户推送消息
        processCouponByStatus(kafkaMessage, status);
    }


    /**
     * 根据状态处理优惠券信息
     *
     * @param kafkaMessage
     * @param status
     */
    private void processCouponByStatus(CouponKafkaMessage kafkaMessage,
                                       CouponStatus status) {
        List<Coupon> coupons = couponDao.findAllById(kafkaMessage.getIds());
        if (CollectionUtils.isEmpty(coupons)
                || coupons.size() != kafkaMessage.getIds().size()) {
            log.error("Can not find Right Coupon Info: {}",
                    JSON.toJSONString(kafkaMessage));
            //TODO 发送邮件
            return;
        }
        coupons.forEach(c -> c.setStatus(status));
        log.info("CouponKafkaMessage Op Coupon Count: {}",
                couponDao.saveAll(coupons).size());
    }


}

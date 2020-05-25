package com.imooc.distribution.service.imple;

import com.imooc.distribution.dao.CouponTemplateDao;
import com.imooc.distribution.exception.CouponException;
import com.imooc.distribution.pojo.CouponTemplate;
import com.imooc.distribution.service.TemplateBaseService;
import com.imooc.distribution.vo.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TemplateBaseServiceImple implements TemplateBaseService {

    private final CouponTemplateDao couponTemplateDao;

    @Autowired
    public TemplateBaseServiceImple(CouponTemplateDao couponTemplateDao) {
        this.couponTemplateDao = couponTemplateDao;
    }

    /**
     * 根据优惠券模板 id 获取优惠券模板信息
     * @param id 模板id
     * @return {@link CouponTemplate} 优惠券实体
     * @throws CouponException
     */
    @Override
    public CouponTemplate buildTemplateInfo(Integer id) throws CouponException {
        Optional<CouponTemplate> template = couponTemplateDao.findById(id);
        return template.get();
    }

    /**
     * 查找所有可用的优惠券模板
     * @return {@link CouponTemplate} s
     */
    @Override
    public List<CouponTemplateSDK> findAllUsableTemplate() {
        List<CouponTemplate> couponTemplates = couponTemplateDao.
                findAllByAvailableAndExpired(true, false);
        return couponTemplates.stream().map(this::templateToTemplateSDK)
                .collect(Collectors.toList());
    }

    /**
     * 获取模板 ids 到 CouponTemplateSDK的映射
     * @param ids 模板 ids
     * @return Map<key: ids, value: CouponTemplateSDK>
     */
    @Override
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids) {
        List<CouponTemplate> couponTemplateList = couponTemplateDao.findAllById(ids);
        return couponTemplateList.stream().map(this::templateToTemplateSDK)
                .collect(Collectors.toMap(
                        CouponTemplateSDK::getId,Function.identity()));
    }

    /**
     * 将CouponTemplate 转换为 CouponTemplateSDK
     * @param couponTemplate
     * @return
     */
    private CouponTemplateSDK  templateToTemplateSDK(CouponTemplate couponTemplate){
        return new CouponTemplateSDK(
                couponTemplate.getId(),
                couponTemplate.getName(),
                couponTemplate.getLogo(),
                couponTemplate.getDesc(),
                couponTemplate.getCategory().getCode(),
                couponTemplate.getProductLine().getCode(),
                couponTemplate.getKey(),
                couponTemplate.getTarget().getCode(),
                couponTemplate.getRule());
    }
}

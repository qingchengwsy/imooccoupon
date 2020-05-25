package com.imooc.distribution.service.imple;

import com.imooc.distribution.dao.CouponTemplateDao;
import com.imooc.distribution.exception.CouponException;
import com.imooc.distribution.pojo.CouponTemplate;
import com.imooc.distribution.service.AsyncService;
import com.imooc.distribution.service.BuildTemplateService;
import com.imooc.distribution.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@SuppressWarnings("all")
public class BuildTemlateServiceImple implements BuildTemplateService {

    private CouponTemplateDao couponTemplateDao;

    private AsyncService asyncService;

    @Autowired
    public BuildTemlateServiceImple(CouponTemplateDao couponTemplateDao, AsyncService asyncService) {
        this.couponTemplateDao = couponTemplateDao;
        this.asyncService = asyncService;
    }


    /**
     * 创建优惠券模板
     * @param request {@link TemplateRequest} 模板信息请求对象
     * @return {@link CouponTemplate} 优惠券模板实体
     * @throws CouponException
     */
    @Override
    public CouponTemplate buildTemplate(TemplateRequest request) throws CouponException {
      //校验参数和合法性
        if (!request.validate()){
          throw new CouponException("BuildTemplate Param Is Not valid");
      }
      //判断同名的模板是否存在
        if (null!=couponTemplateDao.findByName(request.getName())){
            throw new CouponException("Exist Same Name Template");
        }
        //保存到数据库
        CouponTemplate couponTemplate=requestToTemplate(request);
        couponTemplateDao.save(couponTemplate);
        //根据优惠券模板异步生成优惠券码
        asyncService.asyncConstructCouponByTemplate(couponTemplate);
        return couponTemplate;
    }

    /**
     * 将参数TemplateRequest 转换为 CouponTemplate
     * @param request
     * @return
     */
    private CouponTemplate requestToTemplate(TemplateRequest request){
        return new CouponTemplate(
                request.getName(),
                request.getLogo(),
                request.getDesc(),
                request.getCategory(),
                request.getProductLine(),
                request.getCount(),
                request.getUserId(),
                request.getTarget(),
                request.getRule());
    }
}

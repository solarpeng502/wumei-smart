package com.ruoyi.iot.service.impl;

import java.util.List;
import java.util.Random;

import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.iot.model.IdAndName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.iot.mapper.ProductMapper;
import com.ruoyi.iot.domain.Product;
import com.ruoyi.iot.service.IProductService;

import static com.ruoyi.common.utils.SecurityUtils.getLoginUser;

/**
 * 产品Service业务层处理
 * 
 * @author kerwincui
 * @date 2021-12-16
 */
@Service
public class ProductServiceImpl implements IProductService 
{
    private String tslPreKey ="tsl";

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RedisCache redisCache;

    /**
     * 查询产品
     * 
     * @param productId 产品主键
     * @return 产品
     */
    @Override
    public Product selectProductByProductId(Long productId)
    {
        return productMapper.selectProductByProductId(productId);
    }

    /**
     * 查询产品列表
     * 
     * @param product 产品
     * @return 产品
     */
    @Override
    public List<Product> selectProductList(Product product)
    {
        return productMapper.selectProductList(product);
    }

    /**
     * 查询产品简短列表
     *
     * @return 产品
     */
    @Override
    public List<IdAndName> selectProductShortList()
    {
        SysUser user = getLoginUser().getUser();
        return productMapper.selectProductShortList(user.getUserId());
    }

    /**
     * 新增产品
     * 
     * @param product 产品
     * @return 结果
     */
    @Override
    public Product insertProduct(Product product)
    {
        SysUser user = getLoginUser().getUser();
        List<SysRole> roles=user.getRoles();
        if(roles==null || roles.size()==0) {return null;}
        // 系统管理员
        if(roles.stream().anyMatch(a->a.getRoleKey().equals("admin"))){
            product.setIsSys(1);
        }
        // mqtt账号密码
        product.setMqttAccount("A"+getStringRandom(15));
        product.setMqttPassword("P"+getStringRandom(15));
        product.setStatus(product.getStatus()==null?1:product.getStatus());
        product.setTenantId(user.getUserId());
        product.setTenantName(user.getUserName());
        product.setCreateTime(DateUtils.getNowDate());
        productMapper.insertProduct(product);
        return product;
    }

    //生成随机数字和字母,
    private String getStringRandom(int length) {
        String val = "";
        Random random = new Random();
        //参数length，表示生成几位随机数
        for(int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if( "char".equalsIgnoreCase(charOrNum) ) {
                //输出是大写字母还是小写字母
                // int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char)(random.nextInt(26) + 65);
            } else if( "num".equalsIgnoreCase(charOrNum) ) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }

    /**
     * 修改产品
     * 
     * @param product 产品
     * @return 结果
     */
    @Override
    public int updateProduct(Product product)
    {
        product.setUpdateTime(DateUtils.getNowDate());
        return productMapper.updateProduct(product);
    }

    /**
     * 批量删除产品
     * 
     * @param productIds 需要删除的产品主键
     * @return 结果
     */
    @Override
    public int deleteProductByProductIds(Long[] productIds)
    {
        // 删除物模型JSON缓存
        for(int i=0;i<productIds.length;i++){
            redisCache.deleteObject(tslPreKey+productIds[i]);
        }
        return productMapper.deleteProductByProductIds(productIds);
    }

    /**
     * 删除产品信息
     * 
     * @param productId 产品主键
     * @return 结果
     */
    @Override
    public int deleteProductByProductId(Long productId)
    {
        // 删除物模型JSON缓存
        redisCache.deleteObject(tslPreKey+productId);
        return productMapper.deleteProductByProductId(productId);
    }
}

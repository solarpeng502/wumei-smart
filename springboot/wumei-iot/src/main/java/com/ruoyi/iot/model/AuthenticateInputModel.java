package com.ruoyi.iot.model;

/**
 * 动作
 * @author kerwincui
 * @date 2021-12-16
 */
public class AuthenticateInputModel
{
    public AuthenticateInputModel(String serialNumber,Long productId){
        serialNumber=serialNumber;
        productId=productId;
    }
    /** 设备编号 */
    private String serialNumber;

    /** 产品ID */
    private Long productId;

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}

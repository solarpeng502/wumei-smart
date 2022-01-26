package com.ruoyi.iot.model.ThingsModels;

import com.ruoyi.iot.model.ThingsModelItem.ThingsModelValueItemInput;

import java.util.List;

/**
 * 设备输入物模型值参数
 *
 * @author kerwincui
 * @date 2021-12-16
 */
public class ThingsModelValuesInput
{
    /** 产品ID **/
    private Long productId;

    /** 设备ID **/
    private Long deviceId;

    /** 设备物模型值的字符串格式 **/
    private String stringValue;

    /** 设备物模型值的集合 **/
    private List<ThingsModelValueItemInput> thingsModelValueItemInputs;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public List<ThingsModelValueItemInput> getThingsModelValueItemInputs() {
        return thingsModelValueItemInputs;
    }

    public void setThingsModelValueItemInputs(List<ThingsModelValueItemInput> thingsModelValueItemInputs) {
        this.thingsModelValueItemInputs = thingsModelValueItemInputs;
    }
}

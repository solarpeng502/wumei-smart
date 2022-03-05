package com.ruoyi.iot.model;

/**
 * 简洁物模型
 * 
 * @author kerwincui
 * @date 2021-12-16
 */
public class SimpleThingsModel
{
    private String id;

    private String value;


    public void setId(String id)
    {
        this.id = id;
    }
    public String getId()
    {
        return id;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
    public String getValue()
    {
        return value;
    }
}

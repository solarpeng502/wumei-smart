package com.ruoyi.iot.model;

import java.util.Date;
import java.util.List;

/**
 * 动作
 * @author kerwincui
 * @date 2021-12-16
 */
public class MonitorStatisticModel
{
    /** 标识符 */
    private String identifier;

    /** 名称 */
    private String name;

    /** 单位 */
    private String unit;

    private List<Data> dataList;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public List<Data> getDataList() {
        return dataList;
    }

    public void setDataList(List<Data> dataList) {
        this.dataList = dataList;
    }

    private class Data{
        private Date time;
        private String value;

        public Date getTime() {
            return time;
        }

        public void setTime(Date time) {
            this.time = time;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

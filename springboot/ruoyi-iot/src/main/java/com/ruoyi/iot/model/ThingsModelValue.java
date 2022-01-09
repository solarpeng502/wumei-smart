package com.ruoyi.iot.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * 产品分类的Id和名称输出
 * 
 * @author kerwincui
 * @date 2021-12-16
 */
public class ThingsModelValue
{
    /** 属性 */
    private List<IdAndValue> properties;
    /** 功能 */
    private List<IdAndValue> functions;
    /** 事件 */
    private List<IdAndValue> events;

    public List<IdAndValue> getProperties() {
        return properties;
    }
    public void setProperties(List<IdAndValue> properties) {
        this.properties = properties;
    }

    public List<IdAndValue> getFunctions() {
        return functions;
    }
    public void setFunctions(List<IdAndValue> functions) {
        this.functions = functions;
    }

    public List<IdAndValue> getEvents() {
        return events;
    }
    public void setEvents(List<IdAndValue> events) {
        this.events = events;
    }

    private class IdAndValue{
        public String id;
        public String value;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

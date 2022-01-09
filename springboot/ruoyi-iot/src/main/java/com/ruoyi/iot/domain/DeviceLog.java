package com.ruoyi.iot.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 设备日志对象 iot_device_log
 * 
 * @author kerwincui
 * @date 2021-12-16
 */
public class DeviceLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 设备日志ID */
    private Long deviceLogId;

    /** 日志名称（例如：功能名称、温度过高、上下线、固件升级） */
    @Excel(name = "日志名称", readConverterExp = "例=如：功能名称、温度过高、上下线、固件升级")
    private String logName;

    /** 类型（1-属性、2-服务、3-事件、4-其他） */
    @Excel(name = "类型", readConverterExp = "1=-属性、2-服务、3-事件、4-其他")
    private Integer logType;

    /** 日志级别（info-提醒通知，error-轻微问题，alert-严重警告） */
    @Excel(name = "日志级别", readConverterExp = "i=nfo-提醒通知，error-轻微问题，alert-严重警告")
    private Integer logLevel;

    /** 设备ID */
    @Excel(name = "设备ID")
    private Long deviceId;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String deviceName;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 用户昵称 */
    @Excel(name = "用户昵称")
    private String userName;

    /** 租户ID */
    @Excel(name = "租户ID")
    private Long tenantId;

    /** 租户名称 */
    @Excel(name = "租户名称")
    private String tenantName;

    /** 触发源（1-设备自身，2-遥控设备，3-手机端，4-PC端） */
    @Excel(name = "触发源", readConverterExp = "1=-设备自身，2-遥控设备，3-手机端，4-PC端")
    private Integer triggerSource;

    /** 是否生成告警（1-是，0-否） */
    @Excel(name = "是否生成告警", readConverterExp = "1=-是，0-否")
    private Integer isAlert;

    /** 告警处理（0-不需要处理，1-已处理，2-未处理） */
    @Excel(name = "告警处理", readConverterExp = "0=-不需要处理，1-已处理，2-未处理")
    private Integer status;

    /** 日志收到的值 */
    @Excel(name = "日志收到的值")
    private String logValue;

    /** 是否置顶 */
    @Excel(name = "是否置顶")
    private Integer istop;

    /** 是否监测 */
    @Excel(name = "是否监测")
    private Long ismonitor;

    public void setDeviceLogId(Long deviceLogId) 
    {
        this.deviceLogId = deviceLogId;
    }

    public Long getDeviceLogId() 
    {
        return deviceLogId;
    }
    public void setLogName(String logName) 
    {
        this.logName = logName;
    }

    public String getLogName() 
    {
        return logName;
    }
    public void setLogType(Integer logType) 
    {
        this.logType = logType;
    }

    public Integer getLogType() 
    {
        return logType;
    }
    public void setLogLevel(Integer logLevel) 
    {
        this.logLevel = logLevel;
    }

    public Integer getLogLevel() 
    {
        return logLevel;
    }
    public void setDeviceId(Long deviceId) 
    {
        this.deviceId = deviceId;
    }

    public Long getDeviceId() 
    {
        return deviceId;
    }
    public void setDeviceName(String deviceName) 
    {
        this.deviceName = deviceName;
    }

    public String getDeviceName() 
    {
        return deviceName;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setUserName(String userName) 
    {
        this.userName = userName;
    }

    public String getUserName() 
    {
        return userName;
    }
    public void setTenantId(Long tenantId) 
    {
        this.tenantId = tenantId;
    }

    public Long getTenantId() 
    {
        return tenantId;
    }
    public void setTenantName(String tenantName) 
    {
        this.tenantName = tenantName;
    }

    public String getTenantName() 
    {
        return tenantName;
    }
    public void setTriggerSource(Integer triggerSource) 
    {
        this.triggerSource = triggerSource;
    }

    public Integer getTriggerSource() 
    {
        return triggerSource;
    }
    public void setIsAlert(Integer isAlert) 
    {
        this.isAlert = isAlert;
    }

    public Integer getIsAlert() 
    {
        return isAlert;
    }
    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }
    public void setLogValue(String logValue) 
    {
        this.logValue = logValue;
    }

    public String getLogValue() 
    {
        return logValue;
    }
    public void setIstop(Integer istop) 
    {
        this.istop = istop;
    }

    public Integer getIstop() 
    {
        return istop;
    }
    public void setIsmonitor(Long ismonitor) 
    {
        this.ismonitor = ismonitor;
    }

    public Long getIsmonitor() 
    {
        return ismonitor;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("deviceLogId", getDeviceLogId())
            .append("logName", getLogName())
            .append("logType", getLogType())
            .append("logLevel", getLogLevel())
            .append("deviceId", getDeviceId())
            .append("deviceName", getDeviceName())
            .append("userId", getUserId())
            .append("userName", getUserName())
            .append("tenantId", getTenantId())
            .append("tenantName", getTenantName())
            .append("triggerSource", getTriggerSource())
            .append("isAlert", getIsAlert())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("remark", getRemark())
            .append("logValue", getLogValue())
            .append("istop", getIstop())
            .append("ismonitor", getIsmonitor())
            .toString();
    }
}

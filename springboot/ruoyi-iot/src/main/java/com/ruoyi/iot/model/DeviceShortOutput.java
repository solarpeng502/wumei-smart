package com.ruoyi.iot.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 设备对象 iot_device
 * 
 * @author kerwincui
 * @date 2021-12-16
 */
public class DeviceShortOutput
{
    public DeviceShortOutput(){
        this.stringList=new ArrayList<>();
        this.integerList=new ArrayList<>();
        this.decimalList=new ArrayList<>();
        this.enumList=new ArrayList<>();
        this.arrayList=new ArrayList<>();
        this.monitorList=new ArrayList<>();
        this.boolList=new ArrayList<>();
    }

    private static final long serialVersionUID = 1L;

    /** 产品分类ID */
    private Long deviceId;

    /** 产品分类名称 */
    @Excel(name = "设备名称")
    private String deviceName;

    /** 产品ID */
    @Excel(name = "产品ID")
    private Long productId;

    /** 产品名称 */
    @Excel(name = "产品名称")
    private String productName;

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

    /** 设备编号 */
    @Excel(name = "设备编号")
    private String serialNumber;

    /** 固件版本 */
    @Excel(name = "固件版本")
    private BigDecimal firmwareVersion;

    /** 设备状态（1-未激活，2-禁用，3-在线，4-离线） */
    @Excel(name = "设备状态")
    private Integer status;

    /** wifi信号强度（信号极好4格[-55— 0]，信号好3格[-70— -55]，信号一般2格[-85— -70]，信号差1格[-100— -85]） */
    @Excel(name = "wifi信号强度")
    private Integer rssi;

    @Excel(name = "物模型")
    private String thingsModelValue;

    /** 激活时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "激活时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date activeTime;

    private List<StringModel> stringList;
    private List<IntegerModel> integerList;
    private List<DecimalModel> decimalList;
    private List<EnumModel> enumList;
    private List<ArrayModel> arrayList;
    private List<BoolModel> boolList;
    private List<ThingsModelDto> monitorList;

    public List<BoolModel> getBoolList() {
        return boolList;
    }

    public void setBoolList(List<BoolModel> boolList) {
        this.boolList = boolList;
    }

    public List<ThingsModelDto> getMonitorList() {
        return monitorList;
    }

    public void setMonitorList(List<ThingsModelDto> monitorList) {
        this.monitorList = monitorList;
    }

    public List<StringModel> getStringList() {
        return stringList;
    }

    public void setStringList(List<StringModel> stringList) {
        this.stringList = stringList;
    }

    public List<IntegerModel> getIntegerList() {
        return integerList;
    }

    public void setIntegerList(List<IntegerModel> integerList) {
        this.integerList = integerList;
    }

    public List<DecimalModel> getDecimalList() {
        return decimalList;
    }

    public void setDecimalList(List<DecimalModel> decimalList) {
        this.decimalList = decimalList;
    }

    public List<EnumModel> getEnumList() {
        return enumList;
    }

    public void setEnumList(List<EnumModel> enumList) {
        this.enumList = enumList;
    }

    public List<ArrayModel> getArrayList() {
        return arrayList;
    }

    public void setArrayList(List<ArrayModel> arrayList) {
        this.arrayList = arrayList;
    }
    public void setRssi(Integer rssi)
    {
        this.rssi = rssi;
    }

    public Integer getRssi()
    {
        return rssi;
    }
    public void setThingsModelValue(String thingsModelValue)
    {
        this.thingsModelValue = thingsModelValue;
    }

    public String getThingsModelValue()
    {
        return thingsModelValue;
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
    public void setProductId(Long productId) 
    {
        this.productId = productId;
    }

    public Long getProductId() 
    {
        return productId;
    }
    public void setProductName(String productName) 
    {
        this.productName = productName;
    }

    public String getProductName() 
    {
        return productName;
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

    public void setStatus(Integer status)
    {
        this.status = status;
    }
    public Integer getStatus()
    {
        return status;
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
    public void setSerialNumber(String serialNumber) 
    {
        this.serialNumber = serialNumber;
    }

    public String getSerialNumber() 
    {
        return serialNumber;
    }
    public void setFirmwareVersion(BigDecimal firmwareVersion) 
    {
        this.firmwareVersion = firmwareVersion;
    }

    public BigDecimal getFirmwareVersion() 
    {
        return firmwareVersion;
    }

    public void setActiveTime(Date activeTime) 
    {
        this.activeTime = activeTime;
    }

    public Date getActiveTime() 
    {
        return activeTime;
    }

}

package com.ruoyi.iot.service.impl;

import java.beans.Transient;
import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.iot.domain.DeviceUser;
import com.ruoyi.iot.domain.ThingsModel;
import com.ruoyi.iot.mapper.DeviceUserMapper;
import com.ruoyi.iot.mapper.ProductMapper;
import com.ruoyi.iot.mapper.ThingsModelMapper;
import com.ruoyi.iot.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.iot.mapper.DeviceMapper;
import com.ruoyi.iot.domain.Device;
import com.ruoyi.iot.service.IDeviceService;

import static com.ruoyi.common.utils.SecurityUtils.getLoginUser;

/**
 * 设备Service业务层处理
 * 
 * @author kerwincui
 * @date 2021-12-16
 */
@Service
public class DeviceServiceImpl implements IDeviceService 
{
    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private DeviceUserMapper deviceUserMapper;

    @Autowired
    private ThingsModelMapper thingsModelMapper;

    /**
     * 查询设备
     * 
     * @param deviceId 设备主键
     * @return 设备
     */
    @Override
    public Device selectDeviceByDeviceId(Long deviceId)
    {
        return deviceMapper.selectDeviceByDeviceId(deviceId);
    }

    /**
     * 查询设备
     *
     * @param deviceId 设备主键
     * @return 设备
     */
    @Override
    public DeviceShortOutput selectDeviceRunningStatusByDeviceId(Long deviceId)
    {
        DeviceShortOutput device=deviceMapper.selectDeviceRunningStatusByDeviceId(deviceId);
        ThingsModel thingsModel=new ThingsModel();
        JSONObject thingsObject=JSONObject.parseObject(device.getThingsModelValue());
        JSONArray properties=thingsObject.getJSONArray("properties");
        JSONArray functions=thingsObject.getJSONArray("functions");
        convertJsonToThingsModel(properties,device,false);
        convertJsonToThingsModel(functions,device,false);
        device.setThingsModelValue("");
        return device;
    }

    /**
     * 查询设备的物模型值
     *
     * @param deviceId 设备主键
     * @return 设备
     */
    @Override
    public String selectDeviceThingsModelValueById(Long deviceId)
    {
        return deviceMapper.selectDeviceThingsModelValueById(deviceId);
    }

    /**
     * 更新设备的物模型值
     *
     * @param idAndName 设备ID和物模型值
     * @return 设备
     */
    @Override
    public int updateDeviceThingsModelValue(IdAndName idAndName)
    {
        String json=deviceMapper.selectDeviceThingsModelValueById(idAndName.getId());
        // 赋值，传入对象需要修改，包含物模型类
        // TODO 赋值


        idAndName.setName(json);
        // TODO 添加到设备日志
        return deviceMapper.updateDeviceThingsModelValue(idAndName);
    }

    /**
     * 查询设备列表
     * 
     * @param device 设备
     * @return 设备
     */
    @Override
    public List<Device> selectDeviceList(Device device)
    {
        return deviceMapper.selectDeviceList(device);
    }

    /**
     * 查询设备简短列表
     *
     * @param device 设备
     * @return 设备
     */
    @Override
    public List<DeviceShortOutput> selectDeviceShortList(Device device)
    {
        // TODO 关联设备用户表

        List<DeviceShortOutput> deviceList=deviceMapper.selectDeviceShortList(device);
        for(int i=0;i<deviceList.size();i++){
            JSONObject thingsObject=JSONObject.parseObject(deviceList.get(i).getThingsModelValue());
            JSONArray properties=thingsObject.getJSONArray("properties");
            JSONArray functions=thingsObject.getJSONArray("functions");
            // 物模型转换为对象中的不同类别集合
            convertJsonToThingsModel(properties,deviceList.get(i),true);
            convertJsonToThingsModel(functions,deviceList.get(i),true);
            deviceList.get(i).setThingsModelValue("");
        }
        return deviceList;
    }

    /**
     * Json物模型集合转换为对象中的分类集合
     * @param jsonArray Json集合
     * @param device 设备
     */
    private void convertJsonToThingsModel(JSONArray jsonArray,DeviceShortOutput device,boolean isTop){
        for(int i=0;i<jsonArray.size();i++){
            JSONObject thingsJson=jsonArray.getJSONObject(i);
            JSONObject datatypeJson=thingsJson.getJSONObject("datatype");
            ThingsModelDto thingsModel=new ThingsModelDto();
            thingsModel.setIsTop(thingsJson.getInteger("isTop"));
            // 只显示isTop数据
            if(isTop==true && thingsModel.getIsTop()==0){ break; }
            thingsModel.setId(thingsJson.getString("id"));
            thingsModel.setName(thingsJson.getString("name"));
            thingsModel.setValue(thingsJson.getString("value"));
            thingsModel.setIsMonitor(thingsJson.getInteger("isMonitor"));
            // 根据分类不同，存储到不同集合
            if(datatypeJson.getString("datatype").equals("decimal")){
                DecimalModel model=new DecimalModel();
                BeanUtils.copyProperties(thingsModel, model);
                model.setMax(datatypeJson.getBigDecimal("max"));
                model.setMin(datatypeJson.getBigDecimal("min"));
                model.setStep(datatypeJson.getBigDecimal("step"));
                model.setUnit(datatypeJson.getString("unit"));
                if(model.getIsMonitor()==1){
                    model.setValue(model.getValue()+" "+model.getUnit());
                    device.getMonitorList().add(model);
                }else {
                    device.getDecimalList().add(model);
                }
            }else if(datatypeJson.getString("datatype").equals("integer")){
                IntegerModel model=new IntegerModel();
                BeanUtils.copyProperties(thingsModel, model);
                model.setMax(datatypeJson.getBigDecimal("max"));
                model.setMin(datatypeJson.getBigDecimal("min"));
                model.setStep(datatypeJson.getBigDecimal("step"));
                model.setUnit(datatypeJson.getString("unit"));
                if(model.getIsMonitor()==1){
                    model.setValue(model.getValue()+" "+model.getUnit());
                    device.getMonitorList().add(model);
                }else {
                    device.getIntegerList().add(model);
                }
            }else if(datatypeJson.getString("datatype").equals("bool")){
                BoolModel model=new BoolModel();
                BeanUtils.copyProperties(thingsModel, model);
                model.setFalseText(datatypeJson.getString("maxLength"));
                model.setTrueText(datatypeJson.getString("maxLength"));
                if(model.getIsMonitor()==1){
                    device.getMonitorList().add(model);
                }else {
                    device.getBoolList().add(model);
                }
            }else if(datatypeJson.getString("datatype").equals("string")){
                StringModel model=new StringModel();
                BeanUtils.copyProperties(thingsModel, model);
                model.setMaxLength(datatypeJson.getInteger("maxLength"));
                if(model.getIsMonitor()==1){
                    device.getMonitorList().add(model);
                }else {
                    device.getStringList().add(model);
                }
            }else if(datatypeJson.getString("datatype").equals("array")){
                ArrayModel model=new ArrayModel();
                BeanUtils.copyProperties(thingsModel, model);
                model.setArrayType(datatypeJson.getString("arrayType"));
                if(model.getIsMonitor()==1){
                    device.getMonitorList().add(model);
                }else {
                    device.getArrayList().add(model);
                }
            }else if(datatypeJson.getString("datatype").equals("enum")){
                EnumModel model=new EnumModel();
                BeanUtils.copyProperties(thingsModel, model);
                List<EnumItem> enumItemList = JSONObject.parseArray(datatypeJson.getString("enumList"),EnumItem.class);
                model.setEnumList(enumItemList);
                if(model.getIsMonitor()==1){
                    device.getMonitorList().add(model);
                }else {
                    device.getEnumList().add(model);
                }
            }
        }
    }

    /**
     * 新增设备
     * 
     * @param device 设备
     * @return 结果
     */
    @Override
    @Transient
    public int insertDevice(Device device)
    {
        //添加设备
        device.setCreateTime(DateUtils.getNowDate());
        // 产品物模型赋值给设备

        deviceMapper.insertDevice(device);
        // 添加设备用户
        SysUser sysUser = getLoginUser().getUser();
        DeviceUser deviceUser=new DeviceUser();
        deviceUser.setUserId(sysUser.getUserId());
        deviceUser.setUserName(sysUser.getUserName());
        deviceUser.setPhonenumber(sysUser.getPhonenumber());
        deviceUser.setDeviceId(device.getDeviceId());
        deviceUser.setDeviceName(device.getDeviceName());
        deviceUser.setIsOwner(1);
        deviceUser.setLimitCount(10);
        return deviceUserMapper.insertDeviceUser(deviceUser);
    }

    /**
     * 修改设备
     * 
     * @param device 设备
     * @return 结果
     */
    @Override
    public int updateDevice(Device device)
    {
        device.setUpdateTime(DateUtils.getNowDate());
        return deviceMapper.updateDevice(device);
    }

    /**
     * 批量删除设备
     * 
     * @param deviceIds 需要删除的设备主键
     * @return 结果
     */
    @Override
    public int deleteDeviceByDeviceIds(Long[] deviceIds)
    {
        return deviceMapper.deleteDeviceByDeviceIds(deviceIds);
    }

    /**
     * 删除设备信息
     * 
     * @param deviceId 设备主键
     * @return 结果
     */
    @Override
    public int deleteDeviceByDeviceId(Long deviceId)
    {
        return deviceMapper.deleteDeviceByDeviceId(deviceId);
    }
}

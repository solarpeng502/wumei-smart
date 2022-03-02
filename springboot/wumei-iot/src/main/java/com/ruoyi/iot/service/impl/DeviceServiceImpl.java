package com.ruoyi.iot.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.iot.domain.Device;
import com.ruoyi.iot.domain.DeviceLog;
import com.ruoyi.iot.domain.DeviceUser;
import com.ruoyi.iot.mapper.DeviceLogMapper;
import com.ruoyi.iot.mapper.DeviceMapper;
import com.ruoyi.iot.mapper.DeviceUserMapper;
import com.ruoyi.iot.model.DeviceAuthenticateModel;
import com.ruoyi.iot.model.DeviceShortOutput;
import com.ruoyi.iot.model.ThingsModelItem.*;
import com.ruoyi.iot.model.ThingsModels.ThingsModelValueItemDao;
import com.ruoyi.iot.model.ThingsModels.ThingsModelValueItemDto;
import com.ruoyi.iot.model.ThingsModels.ThingsModelValuesInput;
import com.ruoyi.iot.model.ThingsModels.ThingsModelValuesOutput;
import com.ruoyi.iot.service.IDeviceService;
import com.ruoyi.iot.service.IToolService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.ruoyi.common.utils.SecurityUtils.getLoginUser;

/**
 * 设备Service业务层处理
 *
 * @author kerwincui
 * @date 2021-12-16
 */
@Service
public class DeviceServiceImpl implements IDeviceService {
    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private DeviceUserMapper deviceUserMapper;

    @Autowired
    private ThingsModelServiceImpl thingsModelService;

    @Autowired
    private DeviceLogMapper deviceLogMapper;

    @Autowired
    private IToolService toolService;

    /**
     * 查询设备
     *
     * @param deviceId 设备主键
     * @return 设备
     */
    @Override
    public Device selectDeviceByDeviceId(Long deviceId) {
        return deviceMapper.selectDeviceByDeviceId(deviceId);
    }

    /**
     * 根据设备编号查询设备
     *
     * @param serialNumber 设备主键
     * @return 设备
     */
    @Override
    public Device selectDeviceBySerialNumber(String serialNumber) {
        return deviceMapper.selectDeviceBySerialNumber(serialNumber);
    }

    /**
     * 根据设备编号查询设备认证信息
     *
     * @param serialNumber 设备主键
     * @return 设备
     */
    @Override
    public DeviceAuthenticateModel selectDeviceAuthenticateBySerialNumber(String serialNumber) {
        return deviceMapper.selectDeviceAuthenticateBySerialNumber(serialNumber);
    }

    /**
     * 查询设备
     *
     * @param deviceId 设备主键
     * @return 设备
     */
    @Override
    public DeviceShortOutput selectDeviceRunningStatusByDeviceId(Long deviceId) {
        DeviceShortOutput device = deviceMapper.selectDeviceRunningStatusByDeviceId(deviceId);
        JSONObject thingsModelObject = JSONObject.parseObject(thingsModelService.getCacheThingsModelByProductId(device.getProductId()));
        JSONArray properties = thingsModelObject.getJSONArray("properties");
        JSONArray functions = thingsModelObject.getJSONArray("functions");
        // 物模型转换为对象中的不同类别集合
        convertJsonToCategoryList(properties, device, false, false);
        convertJsonToCategoryList(functions, device, false, false);
        device.setThingsModelValue("");
        return device;
    }


    /**
     * 更新设备的物模型
     *
     * @param input 设备ID和物模型值
     * @param type  1=属性 2=功能
     * @return 设备
     */
    @Override
    @Transient
    public int reportDeviceThingsModelValue(ThingsModelValuesInput input, int type) {
        // 查询物模型
        String thingsModels = thingsModelService.getCacheThingsModelByProductId(input.getProductId());
        JSONObject thingsModelObject = JSONObject.parseObject(thingsModels);
        List<ThingsModelValueItemDto> valueList =null;
        if(type==1){
            JSONArray properties = thingsModelObject.getJSONArray("properties");
            valueList = properties.toJavaList(ThingsModelValueItemDto.class);
        }else if(type==2){
            JSONArray functions = thingsModelObject.getJSONArray("functions");
            valueList = functions.toJavaList(ThingsModelValueItemDto.class);
        }

        // 查询物模型值
        ThingsModelValuesOutput deviceThings = deviceMapper.selectDeviceThingsModelValueBySerialNumber(input.getDeviceNumber());
        List<ThingsModelValueItemDao> thingsModelValues = JSONObject.parseArray(deviceThings.getThingsModelValue(), ThingsModelValueItemDao.class);

        for(int i=0;i<input.getThingsModelValueItemInputs().size();i++){
            // 赋值
            for(int j=0;j<thingsModelValues.size();j++){
                if (input.getThingsModelValueItemInputs().get(i).getId().equals(thingsModelValues.get(j).getId())) {
                    thingsModelValues.get(j).setValue(input.getThingsModelValueItemInputs().get(i).getValue());
                    break;
                }
            }

            //日志
            for(int k=0;k<valueList.size();k++){
                if (valueList.get(k).getId().equals(input.getThingsModelValueItemInputs().get(i).getId())) {
                    valueList.get(k).setValue(input.getThingsModelValueItemInputs().get(i).getValue());
                    // TODO 场景联动、告警规则匹配处理

                    // 添加到设备日志
                    DeviceLog deviceLog = new DeviceLog();
                    deviceLog.setDeviceId(deviceThings.getDeviceId());
                    deviceLog.setDeviceName(deviceThings.getDeviceName());
                    deviceLog.setLogName(input.getThingsModelValueItemInputs().get(i).getName());
                    deviceLog.setLogValue(input.getThingsModelValueItemInputs().get(i).getValue());
                    deviceLog.setDatatype(valueList.get(k).getDataType().getType());
                    if (deviceLog.getDatatype().equals("decimal") || deviceLog.getDatatype().equals("integer")) {
                        deviceLog.setLogValue(deviceLog.getLogValue() + valueList.get(k).getDataType().getUnit());
                    } else if (deviceLog.getDatatype().equals("enum")) {
                        for (int e = 0; e < valueList.get(k).getDataType().getEnumList().size(); e++) {
                            if (deviceLog.getLogValue().equals(valueList.get(k).getDataType().getEnumList().get(e).getValue())) {
                                deviceLog.setLogValue(valueList.get(k).getDataType().getEnumList().get(e).getText());
                                break;
                            }
                        }
                    }
                    deviceLog.setRemark(input.getThingsModelValueItemInputs().get(i).getRemark());
                    deviceLog.setIdentity(input.getThingsModelValueItemInputs().get(i).getId());
                    deviceLog.setCreateTime(DateUtils.getNowDate());
                    deviceLog.setIsMonitor(valueList.get(k).getIsMonitor());
                    deviceLog.setLogType(type);
                    deviceLogMapper.insertDeviceLog(deviceLog);
                    break;
                }
            }
        }
        input.setStringValue(JSONObject.toJSONString(thingsModelValues));
        input.setDeviceId(deviceThings.getDeviceId());
        return deviceMapper.updateDeviceThingsModelValue(input);
    }

    /**
     * 查询设备列表
     *
     * @param device 设备
     * @return 设备
     */
    @Override
    public List<Device> selectDeviceList(Device device) {
        return deviceMapper.selectDeviceList(device);
    }

    /**
     * 查询设备简短列表
     *
     * @param device 设备
     * @return 设备
     */
    @Override
    public List<DeviceShortOutput> selectDeviceShortList(Device device) {
        // TODO 关联设备用户表

        List<DeviceShortOutput> deviceList = deviceMapper.selectDeviceShortList(device);
        for (int i = 0; i < deviceList.size(); i++) {
            JSONObject thingsModelObject = JSONObject.parseObject(thingsModelService.getCacheThingsModelByProductId(deviceList.get(i).getProductId()));
            JSONArray properties = thingsModelObject.getJSONArray("properties");
            JSONArray functions = thingsModelObject.getJSONArray("functions");
            // 物模型转换为对象中的不同类别集合
            convertJsonToCategoryList(properties, deviceList.get(i), true, true);
            convertJsonToCategoryList(functions, deviceList.get(i), true, false);
            deviceList.get(i).setThingsModelValue("");
        }
        return deviceList;
    }

    /**
     * Json物模型集合转换为对象中的分类集合
     *
     * @param jsonArray  物模型集合
     * @param isOnlyTop  是否只显示置顶数据
     * @param isOnlyRead 是否设置为只读
     * @param device     设备
     */
    @Async
    public void convertJsonToCategoryList(JSONArray jsonArray, DeviceShortOutput device, boolean isOnlyTop, boolean isOnlyRead) {
        // 获取物模型值
        JSONArray thingsValueArray = JSONObject.parseArray(device.getThingsModelValue());
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject thingsJson = jsonArray.getJSONObject(i);
            JSONObject datatypeJson = thingsJson.getJSONObject("datatype");
            ThingsModelItemBase thingsModel = new ThingsModelItemBase();
            thingsModel.setIsTop(thingsJson.getInteger("isTop"));
            // 只显示isTop数据
            if (thingsModel.getIsTop() == 0 && isOnlyTop == true) {
                continue;
            }

            thingsModel.setId(thingsJson.getString("id"));
            thingsModel.setName(thingsJson.getString("name"));
            thingsModel.setIsMonitor(thingsJson.getInteger("isMonitor") == null ? 0 : thingsJson.getInteger("isMonitor"));
            thingsModel.setType(datatypeJson.getString("type"));
            thingsModel.setValue("");
            // 获取value
            for (int j = 0; j < thingsValueArray.size(); j++) {
                if (thingsValueArray.getJSONObject(j).getString("id").equals(thingsModel.getId())) {
                    thingsModel.setValue(thingsValueArray.getJSONObject(j).getString("value"));
                    break;
                }
            }
            // 根据分类不同，存储到不同集合
            if (datatypeJson.getString("type").equals("decimal")) {
                DecimalModelOutput model = new DecimalModelOutput();
                BeanUtils.copyProperties(thingsModel, model);
                model.setMax(datatypeJson.getBigDecimal("max"));
                model.setMin(datatypeJson.getBigDecimal("min"));
                model.setStep(datatypeJson.getBigDecimal("step"));
                model.setUnit(datatypeJson.getString("unit"));
                if (model.getIsMonitor() == 1 || isOnlyRead == true) {
                    ReadOnlyModelOutput readonlyModel = new ReadOnlyModelOutput();
                    BeanUtils.copyProperties(model, readonlyModel);
                    device.getReadOnlyList().add(readonlyModel);
                } else {
                    device.getDecimalList().add(model);
                }
            } else if (datatypeJson.getString("type").equals("integer")) {
                IntegerModelOutput model = new IntegerModelOutput();
                BeanUtils.copyProperties(thingsModel, model);
                model.setMax(datatypeJson.getBigDecimal("max"));
                model.setMin(datatypeJson.getBigDecimal("min"));
                model.setStep(datatypeJson.getBigDecimal("step"));
                model.setUnit(datatypeJson.getString("unit"));
                if (model.getIsMonitor() == 1 || isOnlyRead == true) {
                    ReadOnlyModelOutput readonlyModel = new ReadOnlyModelOutput();
                    BeanUtils.copyProperties(model, readonlyModel);
                    device.getReadOnlyList().add(readonlyModel);
                } else {
                    device.getIntegerList().add(model);
                }
            } else if (datatypeJson.getString("type").equals("bool")) {
                BoolModelOutput model = new BoolModelOutput();
                BeanUtils.copyProperties(thingsModel, model);
                model.setFalseText(datatypeJson.getString("falseText"));
                model.setTrueText(datatypeJson.getString("trueText"));
                if (model.getIsMonitor() == 1 || isOnlyRead == true) {
                    ReadOnlyModelOutput readonlyModel = new ReadOnlyModelOutput();
                    BeanUtils.copyProperties(model, readonlyModel);
                    device.getReadOnlyList().add(readonlyModel);
                } else {
                    device.getBoolList().add(model);
                }
            } else if (datatypeJson.getString("type").equals("string")) {
                StringModelOutput model = new StringModelOutput();
                BeanUtils.copyProperties(thingsModel, model);
                model.setMaxLength(datatypeJson.getInteger("maxLength"));
                if (model.getIsMonitor() == 1 || isOnlyRead == true) {
                    ReadOnlyModelOutput readonlyModel = new ReadOnlyModelOutput();
                    BeanUtils.copyProperties(model, readonlyModel);
                    device.getReadOnlyList().add(readonlyModel);
                } else {
                    device.getStringList().add(model);
                }
            } else if (datatypeJson.getString("type").equals("array")) {
                ArrayModelOutput model = new ArrayModelOutput();
                BeanUtils.copyProperties(thingsModel, model);
                model.setArrayType(datatypeJson.getString("arrayType"));
                if (model.getIsMonitor() == 1 || isOnlyRead == true) {
                    ReadOnlyModelOutput readonlyModel = new ReadOnlyModelOutput();
                    BeanUtils.copyProperties(model, readonlyModel);
                    device.getReadOnlyList().add(readonlyModel);
                } else {
                    device.getArrayList().add(model);
                }
            } else if (datatypeJson.getString("type").equals("enum")) {
                EnumModelOutput model = new EnumModelOutput();
                BeanUtils.copyProperties(thingsModel, model);
                List<EnumItemOutput> enumItemList = JSONObject.parseArray(datatypeJson.getString("enumList"), EnumItemOutput.class);
                model.setEnumList(enumItemList);
                if (model.getIsMonitor() == 1 || isOnlyRead == true) {
                    ReadOnlyModelOutput readonlyModel = new ReadOnlyModelOutput();
                    BeanUtils.copyProperties(model, readonlyModel);
                    device.getReadOnlyList().add(readonlyModel);
                } else {
                    device.getEnumList().add(model);
                }
            }
        }
        // 排序
        device.setReadOnlyList(device.getReadOnlyList().stream().sorted(Comparator.comparing(ThingsModelItemBase::getIsMonitor).reversed()).collect(Collectors.toList()));
    }

    /**
     * 新增设备
     *
     * @param device 设备
     * @return 结果
     */
    @Override
    @Transient
    public Device insertDevice(Device device) {
        SysUser sysUser = getLoginUser().getUser();
        //添加设备
        device.setCreateTime(DateUtils.getNowDate());
        device.setThingsModelValue(JSONObject.toJSONString(getThingsModelDefaultValue(device.getProductId())));
        device.setUserId(sysUser.getUserId());
        device.setUserName(sysUser.getUserName());
        device.setTenantId(sysUser.getUserId());
        device.setTenantName(sysUser.getUserName());
        deviceMapper.insertDevice(device);
        // 添加设备用户
        DeviceUser deviceUser = new DeviceUser();
        deviceUser.setUserId(sysUser.getUserId());
        deviceUser.setUserName(sysUser.getUserName());
        deviceUser.setPhonenumber(sysUser.getPhonenumber());
        deviceUser.setDeviceId(device.getDeviceId());
        deviceUser.setDeviceName(device.getDeviceName());
        deviceUser.setTenantId(device.getDeviceId());
        deviceUser.setTenantName(device.getTenantName());
        deviceUser.setIsOwner(1);
        deviceUserMapper.insertDeviceUser(deviceUser);
        return device;
    }

    /**
     * 获取物模型值
     * @param productId
     * @return
     */
    private List<ThingsModelValueItemDao> getThingsModelDefaultValue(Long productId) {
        // 获取物模型,设置默认值
        String thingsModels = thingsModelService.getCacheThingsModelByProductId(productId);
        JSONObject thingsModelObject = JSONObject.parseObject(thingsModels);
        JSONArray properties = thingsModelObject.getJSONArray("properties");
        JSONArray functions = thingsModelObject.getJSONArray("functions");
        List<ThingsModelValueItemDao> valueList = properties.toJavaList(ThingsModelValueItemDao.class);
        valueList.addAll(functions.toJavaList(ThingsModelValueItemDao.class));
        valueList.forEach(x -> x.setValue(""));
        return valueList;
    }


    /**
     * 修改设备
     * @param device 设备
     * @return 结果
     */
    @Override
    public int updateDevice(Device device) {
        device.setUpdateTime(DateUtils.getNowDate());
        // 未激活状态,可以修改产品以及物模型值
        if (device.getStatus() == 1) {
            device.setThingsModelValue(JSONObject.toJSONString(getThingsModelDefaultValue(device.getProductId())));
        } else {
            device.setProductId(null);
            device.setProductName(null);
        }
        return deviceMapper.updateDevice(device);
    }

    /**
     * 生成设备唯一编号
     * @return 结果
     */
    @Override
    public String generationDeviceNum() {
        String number= "D"+toolService.getStringRandom(15);
        int count= deviceMapper.getDeviceNumCount(number);
        if(count==0) {
            return number;
        }else{
            generationDeviceNum();
        }
        return "";
    }

    /**
     *
     * @param deviceNum 设备编号
     * @param status 设备状态（1-未激活，2-禁用，3-在线，4-离线）
     * @return 结果
     */
    @Override
    public int updateDeviceStatus(String deviceNum,int status) {
        Device device=new Device();
        device.setStatus(status);
        device.setSerialNumber(deviceNum);
        return deviceMapper.updateDeviceStatus(device);
    }

    /**
     * 上报设备信息
     * @param device 设备
     * @return 结果
     */
    @Override
    public int reportDevice(Device device) {
        Device deviceEntity=deviceMapper.selectDeviceBySerialNumber(device.getSerialNumber());
        int result=0;
        if(deviceEntity!=null){
            // 更新设备信息
            device.setUpdateTime(DateUtils.getNowDate());
            if(deviceEntity.getActiveTime()==null || deviceEntity.getActiveTime().equals("")) {
                device.setActiveTime(DateUtils.getNowDate());
            }
            device.setThingsModelValue(null);
            result= deviceMapper.updateDeviceBySerialNumber(device);
        }
        return result;
    }


    /**
     * 批量删除设备
     * @param deviceIds 需要删除的设备主键
     * @return 结果
     */
    @Override
    @Transient
    public int deleteDeviceByDeviceIds(Long[] deviceIds) {
        // 删除设备分组
        deviceMapper.deleteDeviceGroupByDeviceIds(deviceIds);
        return deviceMapper.deleteDeviceByDeviceIds(deviceIds);
    }

    /**
     * 删除设备信息
     * @param deviceId 设备主键
     * @return 结果
     */
    @Override
    public int deleteDeviceByDeviceId(Long deviceId) {
        return deviceMapper.deleteDeviceByDeviceId(deviceId);
    }
}

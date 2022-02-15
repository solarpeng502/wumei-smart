package com.ruoyi.iot.service;

import java.util.List;
import com.ruoyi.iot.domain.Device;
import com.ruoyi.iot.model.DeviceShortOutput;
import com.ruoyi.iot.model.IdAndName;
import com.ruoyi.iot.model.ThingsModels.ThingsModelValuesInput;

/**
 * 设备Service接口
 * 
 * @author kerwincui
 * @date 2021-12-16
 */
public interface IDeviceService 
{
    /**
     * 查询设备
     * 
     * @param deviceId 设备主键
     * @return 设备
     */
    public Device selectDeviceByDeviceId(Long deviceId);

    /**
     * 根据设备编号查询设备
     *
     * @param serialNumber 设备主键
     * @return 设备
     */
    public Device selectDeviceBySerialNumber(String serialNumber);

    /**
     * 查询设备和运行状态
     *
     * @param deviceId 设备主键
     * @return 设备
     */
    public DeviceShortOutput selectDeviceRunningStatusByDeviceId(Long deviceId);

    /**
     * 上报设备的物模型
     * @param input
     * @return
     */
    public int reportDeviceThingsModelValue(ThingsModelValuesInput input,int type);

    /**
     * 查询设备列表
     * 
     * @param device 设备
     * @return 设备集合
     */
    public List<Device> selectDeviceList(Device device);

    /**
     * 查询设备简短列表
     *
     * @param device 设备
     * @return 设备集合
     */
    public List<DeviceShortOutput> selectDeviceShortList(Device device);

    /**
     * 新增设备
     * 
     * @param device 设备
     * @return 结果
     */
    public int insertDevice(Device device);

    /**
     * 修改设备
     * 
     * @param device 设备
     * @return 结果
     */
    public int updateDevice(Device device);

    /**
     * 上报设备信息
     * @param device 设备
     * @return 结果
     */
    public int reportDevice(Device device);

    /**
     * 批量删除设备
     * 
     * @param deviceIds 需要删除的设备主键集合
     * @return 结果
     */
    public int deleteDeviceByDeviceIds(Long[] deviceIds);

    /**
     * 删除设备信息
     * 
     * @param deviceId 设备主键
     * @return 结果
     */
    public int deleteDeviceByDeviceId(Long deviceId);
}

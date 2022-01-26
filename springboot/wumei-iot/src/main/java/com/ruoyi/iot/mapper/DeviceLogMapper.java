package com.ruoyi.iot.mapper;

import java.util.List;
import com.ruoyi.iot.domain.DeviceLog;

/**
 * 设备日志Mapper接口
 * 
 * @author kerwincui
 * @date 2022-01-13
 */
public interface DeviceLogMapper 
{
    /**
     * 查询设备日志
     * 
     * @param logId 设备日志主键
     * @return 设备日志
     */
    public DeviceLog selectDeviceLogByLogId(Long logId);

    /**
     * 查询设备日志列表
     * 
     * @param deviceLog 设备日志
     * @return 设备日志集合
     */
    public List<DeviceLog> selectDeviceLogList(DeviceLog deviceLog);

    /**
     * 新增设备日志
     * 
     * @param deviceLog 设备日志
     * @return 结果
     */
    public int insertDeviceLog(DeviceLog deviceLog);

    /**
     * 修改设备日志
     * 
     * @param deviceLog 设备日志
     * @return 结果
     */
    public int updateDeviceLog(DeviceLog deviceLog);

    /**
     * 删除设备日志
     * 
     * @param logId 设备日志主键
     * @return 结果
     */
    public int deleteDeviceLogByLogId(Long logId);

    /**
     * 批量删除设备日志
     * 
     * @param logIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDeviceLogByLogIds(Long[] logIds);
}

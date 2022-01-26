package com.ruoyi.iot.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.iot.mapper.DeviceLogMapper;
import com.ruoyi.iot.domain.DeviceLog;
import com.ruoyi.iot.service.IDeviceLogService;

/**
 * 设备日志Service业务层处理
 * 
 * @author kerwincui
 * @date 2021-12-16
 */
@Service
public class DeviceLogServiceImpl implements IDeviceLogService 
{
    @Autowired
    private DeviceLogMapper deviceLogMapper;

    /**
     * 查询设备日志
     * 
     * @param deviceLogId 设备日志主键
     * @return 设备日志
     */
    @Override
    public DeviceLog selectDeviceLogByDeviceLogId(Long deviceLogId)
    {
        return deviceLogMapper.selectDeviceLogByDeviceLogId(deviceLogId);
    }

    /**
     * 查询设备日志列表
     * 
     * @param deviceLog 设备日志
     * @return 设备日志
     */
    @Override
    public List<DeviceLog> selectDeviceLogList(DeviceLog deviceLog)
    {
        return deviceLogMapper.selectDeviceLogList(deviceLog);
    }

    /**
     * 新增设备日志
     * 
     * @param deviceLog 设备日志
     * @return 结果
     */
    @Override
    public int insertDeviceLog(DeviceLog deviceLog)
    {
        deviceLog.setCreateTime(DateUtils.getNowDate());
        return deviceLogMapper.insertDeviceLog(deviceLog);
    }

    /**
     * 修改设备日志
     * 
     * @param deviceLog 设备日志
     * @return 结果
     */
    @Override
    public int updateDeviceLog(DeviceLog deviceLog)
    {
        return deviceLogMapper.updateDeviceLog(deviceLog);
    }

    /**
     * 批量删除设备日志
     * 
     * @param deviceLogIds 需要删除的设备日志主键
     * @return 结果
     */
    @Override
    public int deleteDeviceLogByDeviceLogIds(Long[] deviceLogIds)
    {
        return deviceLogMapper.deleteDeviceLogByDeviceLogIds(deviceLogIds);
    }

    /**
     * 删除设备日志信息
     * 
     * @param deviceLogId 设备日志主键
     * @return 结果
     */
    @Override
    public int deleteDeviceLogByDeviceLogId(Long deviceLogId)
    {
        return deviceLogMapper.deleteDeviceLogByDeviceLogId(deviceLogId);
    }
}

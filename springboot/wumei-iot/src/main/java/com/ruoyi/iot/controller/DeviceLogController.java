package com.ruoyi.iot.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.iot.domain.DeviceLog;
import com.ruoyi.iot.service.IDeviceLogService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 设备日志Controller
 * 
 * @author kerwincui
 * @date 2022-01-13
 */
@RestController
@RequestMapping("/iot/deviceLog")
public class DeviceLogController extends BaseController
{
    @Autowired
    private IDeviceLogService deviceLogService;

    /**
     * 查询设备日志列表
     */
    @PreAuthorize("@ss.hasPermi('iot:device:list')")
    @GetMapping("/list")
    public TableDataInfo list(DeviceLog deviceLog)
    {
        startPage();
        List<DeviceLog> list = deviceLogService.selectDeviceLogList(deviceLog);
        return getDataTable(list);
    }

    /**
     * 导出设备日志列表
     */
    @PreAuthorize("@ss.hasPermi('iot:device:export')")
    @Log(title = "设备日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DeviceLog deviceLog)
    {
        List<DeviceLog> list = deviceLogService.selectDeviceLogList(deviceLog);
        ExcelUtil<DeviceLog> util = new ExcelUtil<DeviceLog>(DeviceLog.class);
        util.exportExcel(response, list, "设备日志数据");
    }

    /**
     * 获取设备日志详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:device:query')")
    @GetMapping(value = "/{logId}")
    public AjaxResult getInfo(@PathVariable("logId") Long logId)
    {
        return AjaxResult.success(deviceLogService.selectDeviceLogByLogId(logId));
    }

    /**
     * 新增设备日志
     */
    @PreAuthorize("@ss.hasPermi('iot:device:add')")
    @Log(title = "设备日志", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DeviceLog deviceLog)
    {
        return toAjax(deviceLogService.insertDeviceLog(deviceLog));
    }

    /**
     * 修改设备日志
     */
    @PreAuthorize("@ss.hasPermi('iot:device:edit')")
    @Log(title = "设备日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DeviceLog deviceLog)
    {
        return toAjax(deviceLogService.updateDeviceLog(deviceLog));
    }

    /**
     * 删除设备日志
     */
    @PreAuthorize("@ss.hasPermi('iot:device:remove')")
    @Log(title = "设备日志", businessType = BusinessType.DELETE)
	@DeleteMapping("/{logIds}")
    public AjaxResult remove(@PathVariable Long[] logIds)
    {
        return toAjax(deviceLogService.deleteDeviceLogByLogIds(logIds));
    }
}

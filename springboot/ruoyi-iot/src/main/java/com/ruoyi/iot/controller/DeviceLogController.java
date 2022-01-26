package com.ruoyi.iot.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
 * @date 2021-12-16
 */
@Api(tags="设备日志/告警")
@RestController
@RequestMapping("/iot/log")
public class DeviceLogController extends BaseController
{
    @Autowired
    private IDeviceLogService deviceLogService;

    /**
     * 查询设备日志列表
     */
    @PreAuthorize("@ss.hasPermi('iot:log:list')")
    @GetMapping("/list")
    @ApiOperation("日志列表")
    public TableDataInfo list(DeviceLog deviceLog)
    {
        startPage();
        List<DeviceLog> list = deviceLogService.selectDeviceLogList(deviceLog);
        return getDataTable(list);
    }

    /**
     * 导出设备日志列表
     */
    @PreAuthorize("@ss.hasPermi('iot:log:export')")
    @Log(title = "设备日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation("导出日志")
    public void export(HttpServletResponse response, DeviceLog deviceLog)
    {
        List<DeviceLog> list = deviceLogService.selectDeviceLogList(deviceLog);
        ExcelUtil<DeviceLog> util = new ExcelUtil<DeviceLog>(DeviceLog.class);
        util.exportExcel(response, list, "设备日志数据");
    }

    /**
     * 获取设备日志详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:log:query')")
    @GetMapping(value = "/{deviceLogId}")
    @ApiOperation("获取设备详情")
    public AjaxResult getInfo(@PathVariable("deviceLogId") Long deviceLogId)
    {
        return AjaxResult.success(deviceLogService.selectDeviceLogByDeviceLogId(deviceLogId));
    }

    /**
     * 新增设备日志
     */
    @PreAuthorize("@ss.hasPermi('iot:log:add')")
    @Log(title = "设备日志", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("添加日志")
    public AjaxResult add(@RequestBody DeviceLog deviceLog)
    {
        return toAjax(deviceLogService.insertDeviceLog(deviceLog));
    }

    /**
     * 修改设备日志
     */
    @ApiOperation("修改日志")
    @PreAuthorize("@ss.hasPermi('iot:log:edit')")
    @Log(title = "设备日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DeviceLog deviceLog)
    {
        return toAjax(deviceLogService.updateDeviceLog(deviceLog));
    }

    /**
     * 删除设备日志
     */
    @ApiOperation("批量删除日志")
    @PreAuthorize("@ss.hasPermi('iot:log:remove')")
    @Log(title = "设备日志", businessType = BusinessType.DELETE)
	@DeleteMapping("/{deviceLogIds}")
    public AjaxResult remove(@PathVariable Long[] deviceLogIds)
    {
        return toAjax(deviceLogService.deleteDeviceLogByDeviceLogIds(deviceLogIds));
    }
}

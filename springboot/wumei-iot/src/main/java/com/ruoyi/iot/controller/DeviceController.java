package com.ruoyi.iot.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.iot.model.DeviceShortOutput;
import com.ruoyi.iot.model.IdAndName;
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
import com.ruoyi.iot.domain.Device;
import com.ruoyi.iot.service.IDeviceService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 设备Controller
 * 
 * @author kerwincui
 * @date 2021-12-16
 */
@Api(tags = "设备管理")
@RestController
@RequestMapping("/iot/device")
public class DeviceController extends BaseController
{
    @Autowired
    private IDeviceService deviceService;

    /**
     * 查询设备列表
     */
    @PreAuthorize("@ss.hasPermi('iot:device:list')")
    @GetMapping("/list")
    @ApiOperation("设备分页列表")
    public TableDataInfo list(Device device)
    {
        startPage();
        List<Device> list = deviceService.selectDeviceList(device);
        return getDataTable(list);
    }

    /**
     * 查询设备简短列表
     */
    @PreAuthorize("@ss.hasPermi('iot:device:list')")
    @GetMapping("/shortList")
    @ApiOperation("设备分页简短列表")
    public TableDataInfo shortList(Device device)
    {
        startPage();
        List<DeviceShortOutput> list = deviceService.selectDeviceShortList(device);
        return getDataTable(list);
    }

    /**
     * 导出设备列表
     */
    @PreAuthorize("@ss.hasPermi('iot:device:export')")
    @Log(title = "设备", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation("导出设备")
    public void export(HttpServletResponse response, Device device)
    {
        List<Device> list = deviceService.selectDeviceList(device);
        ExcelUtil<Device> util = new ExcelUtil<Device>(Device.class);
        util.exportExcel(response, list, "设备数据");
    }

    /**
     * 获取设备详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:device:query')")
    @GetMapping(value = "/{deviceId}")
    @ApiOperation("获取设备详情")
    public AjaxResult getInfo(@PathVariable("deviceId") Long deviceId)
    {
        return AjaxResult.success(deviceService.selectDeviceByDeviceId(deviceId));
    }

    /**
     * 获取设备详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:device:query')")
    @GetMapping(value = "/runningStatus/{deviceId}")
    @ApiOperation("获取设备详情和运行状态")
    public AjaxResult getRunningStatusInfo(@PathVariable("deviceId") Long deviceId)
    {
        return AjaxResult.success(deviceService.selectDeviceRunningStatusByDeviceId(deviceId));
    }

    /**
     * 新增设备
     */
    @PreAuthorize("@ss.hasPermi('iot:device:add')")
    @Log(title = "设备", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("添加设备")
    public AjaxResult add(@RequestBody Device device)
    {
        return toAjax(deviceService.insertDevice(device));
    }

    /**
     * 修改设备
     */
    @PreAuthorize("@ss.hasPermi('iot:device:edit')")
    @Log(title = "设备", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改设备")
    public AjaxResult edit(@RequestBody Device device)
    {
        return toAjax(deviceService.updateDevice(device));
    }

    /**
     * 删除设备
     */
    @PreAuthorize("@ss.hasPermi('iot:device:remove')")
    @Log(title = "设备", businessType = BusinessType.DELETE)
	@DeleteMapping("/{deviceIds}")
    @ApiOperation("批量删除设备")
    public AjaxResult remove(@PathVariable Long[] deviceIds)
    {
        return toAjax(deviceService.deleteDeviceByDeviceIds(deviceIds));
    }
}

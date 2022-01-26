package com.ruoyi.iot.util.quartz;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.iot.domain.DeviceJob;
import com.ruoyi.quartz.domain.SysJob;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * 任务执行工具
 *
 * @author ruoyi
 */
public class JobInvokeUtil
{
    /**
     * 执行方法
     *
     * @param deviceJob 系统任务
     */
    public static void invokeMethod(DeviceJob deviceJob) throws Exception
    {
        String invokeTarget = deviceJob.getActions();
        System.out.println("------------------------执行了一次任务-----------------------------");



    }


}

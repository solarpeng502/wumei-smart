package com.ruoyi.iot.mqtt;

import com.alibaba.fastjson.JSON;
import com.ruoyi.iot.domain.Device;
import com.ruoyi.iot.domain.DeviceLog;
import com.ruoyi.iot.model.NtpModel;
import com.ruoyi.iot.model.SimpleThingsModel;
import com.ruoyi.iot.model.ThingsModelItem.ThingsModelValueItemInput;
import com.ruoyi.iot.model.ThingsModels.ThingsModelValuesInput;
import com.ruoyi.iot.service.IDeviceLogService;
import com.ruoyi.iot.service.IDeviceService;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmqxService {
    private static final Logger logger = LoggerFactory.getLogger(EmqxService.class);

    @Autowired
    private EmqxClient emqxClient;

    @Autowired
    private IDeviceService deviceService;
    @Autowired
    private IDeviceLogService deviceLogService;

    private static final String prefix="/+/+/";
    private static final String suffixPost="post";
    private static final String suffixGet="get";

    private static final String shadow="shadow/";
    private static final String info="info/";
    private static final String ntp="ntp/";
    private static final String ota="ota/";
    private static final String status="status/";
    private static final String property="property/";
    private static final String function="function/";
    private static final String event="event/";


    public void subscribe(MqttClient client) throws MqttException {
        // 订阅设备信息
        client.subscribe(prefix+info+suffixPost, 1);
        // 订阅时钟同步
        client.subscribe(prefix+ntp+suffixPost, 1);
        // 订阅设备属性
        client.subscribe(prefix+property+suffixPost,1);
        // 订阅设备功能
        client.subscribe(prefix+function+suffixPost,1);
        // 订阅设备事件
        client.subscribe(prefix+event+suffixPost,1);
        // 订阅属性（影子模式）
        client.subscribe(prefix+shadow+property+suffixPost,0);
        // 订阅功能（影子模式）
        client.subscribe(prefix+shadow+function+suffixPost,0);
        logger.info("mqtt订阅了设备信息和物模型主题");
    }

    public void subscribeCallback(String topic, MqttMessage mqttMessage){
        // subscribe后得到的消息会执行到这里面
        String message=new String(mqttMessage.getPayload());
        logger.info("接收消息主题 : " + topic);
        logger.info("接收消息Qos : " + mqttMessage.getQos());
        logger.info("接收消息内容 : " + message);

        String[] topicItem=topic.substring(1).split("/");
        Long productId= Long.valueOf(topicItem[0]);
        String deviceNum=topicItem[1];
        String name=topicItem[2];
        switch (name){
            case info:
                reportDevice(productId,deviceNum,message);
                break;
            case ntp:
                publishNtp(productId,deviceNum,message);
                break;
            case property:
                reportProperty(productId,deviceNum,message);
                break;
            case function:
                reportFunction(productId,deviceNum,message);
                break;
            case event:
                reportEvent(productId,deviceNum,message);
                break;
        }
    }

    /**
     * 上报设备信息
     */
    private void reportDevice(Long productId,String deviceNum,String message){
        Device device=JSON.parseObject(message,Device.class);
        device.setProductId(productId);
        device.setSerialNumber(deviceNum);
        deviceService.reportDevice(device);
    }

    /**
     * 上报属性
     * @param message
     */
    private void reportProperty(Long productId,String deviceNum,String message){
        List<ThingsModelValueItemInput> thingsModelValueItemInputs=JSON.parseArray(message,ThingsModelValueItemInput.class);
        ThingsModelValuesInput input=new ThingsModelValuesInput();
        input.setProductId(productId);
        input.setDeviceNumber(deviceNum);
        input.setThingsModelValueItemInputs(thingsModelValueItemInputs);
        deviceService.reportDeviceThingsModelValue(input,1);
    }

    /**
     * 上报功能
     * @param message
     */
    private void reportFunction(Long productId,String deviceNum,String message){
        List<ThingsModelValueItemInput> thingsModelValueItemInputs=JSON.parseArray(message,ThingsModelValueItemInput.class);
        ThingsModelValuesInput input=new ThingsModelValuesInput();
        input.setProductId(productId);
        input.setDeviceNumber(deviceNum);
        input.setThingsModelValueItemInputs(thingsModelValueItemInputs);
        deviceService.reportDeviceThingsModelValue(input,2);
    }

    /**
     * 上报事件
     * @param message
     */
    // @Transactional(rollbackFor = Exception.class)
    private void reportEvent(Long productId,String deviceNum,String message){
        List<ThingsModelValueItemInput> thingsModelValueItemInputs=JSON.parseArray(message,ThingsModelValueItemInput.class);
        Device device =deviceService.selectDeviceBySerialNumber(deviceNum);
        for(int i=0;i<thingsModelValueItemInputs.size();i++) {
            // 添加到设备日志
            DeviceLog deviceLog = new DeviceLog();
            deviceLog.setDeviceId(device.getDeviceId());
            deviceLog.setDeviceName(device.getDeviceName());
            deviceLog.setLogName(thingsModelValueItemInputs.get(i).getName());
            deviceLog.setLogValue(thingsModelValueItemInputs.get(i).getValue());
            deviceLog.setRemark(thingsModelValueItemInputs.get(i).getRemark());
            deviceLog.setIdentity(thingsModelValueItemInputs.get(i).getId());
            deviceLog.setLogType(3);
            deviceLogService.insertDeviceLog(deviceLog);
        }
    }

    /**
     * 1.发布设备状态
     */
    public void publishStatus(Long productId,String deviceNum,int deviceStatus){
        emqxClient.publish(1,false,"/"+productId+"/"+deviceNum+"/"+status+suffixPost, "{\"status\":"+deviceStatus+"}");
    }

    /**
     * 2.发布时钟同步信息
     * @param message
     */
    private void publishNtp(Long productId,String deviceNum,String message){
        NtpModel ntpModel=JSON.parseObject(message,NtpModel.class);
        ntpModel.setServerRecvTime(System.currentTimeMillis());
        ntpModel.setServerSendTime(System.currentTimeMillis());
        emqxClient.publish(1, false, "/"+productId+"/"+deviceNum+"/"+ntp+suffixGet, JSON.toJSONString(ntpModel));
    }

    /**
     * 3.发布设备升级
     */
    public void publishOta(Long productId,String deviceNum){
        emqxClient.publish(1,false,"/"+productId+"/"+deviceNum+"/"+ota+suffixGet, "");
    }



    /**
     * 4.发布属性
     */
    public void publishProperty(Long productId,String deviceNum,List<SimpleThingsModel> thingsList){
        emqxClient.publish(1,false,"/"+productId+"/"+deviceNum+"/"+property+suffixGet, JSON.toJSONString(thingsList));
    }

    /**
     * 5.发布功能
     */
    public void publishFunction(Long productId,String deviceNum,List<SimpleThingsModel> thingsList){
        emqxClient.publish(1,false,"/"+productId+"/"+deviceNum+"/"+function+suffixGet, JSON.toJSONString(thingsList));
    }




}

/***********************************************************
 * author: kerwincui
 * create: 2022-02-20
 * email：164770707@qq.com
 * source:https://github.com/kerwincui/wumei-smart
 * board:esp8266 core for arduino v3.0.2
 ***********************************************************/

#include "Helper.h"

long lastMqttConn;    //上次mqtt连接时间

/**
 * 启动
 */
void setup()
{
  //打开串行端口：
  Serial.begin(115200);
  printMsg("wumei smart device starting...");  
  //连接WIFI
  connectWifi();
  //连接MQTT
  connectMqtt();
}

/**
 * 循环执行
 */
void loop()
{
  //Wifi掉线重连
  if (WiFi.status() != WL_CONNECTED)
  {
    connectWifi();
  }

  //Mqtt重连，间隔30秒
  if (WiFi.status() == WL_CONNECTED)
  {
    long now = millis();
    if (!mqttClient.connected())
    {
      if (now - lastMqttConn > 30000)
      {
        lastMqttConn = now;
        connectMqtt();
      }
    }
    else
    {
      mqttClient.loop();
    }
  }
}








/**
 * 1.更新设备状态
 * 
 */
void subscribeUpdateStatus(){
  // client.subscribe("status/set/"+(String)deviceNum, [](const String & payload) {  
  //   StaticJsonDocument<1024> doc; //1024字节内存池容量
  //   //解析JSON
  //   DeserializationError error = deserializeJson(doc, payload);
  //   if (error) {
  //     Serial.print(F("deserializeJson() failed: "));
  //     Serial.println(error.f_str());
  //     return;
  //   }
  
  //   // 获取状态
  //   const char* deviceNum = doc["deviceNum"];  
  //   Serial.println("deviceNum:"+(String)deviceNum);
    
  //   int relayStatus = doc["relayStatus"];  
  //   Serial.print("relayStatus:");
  //   Serial.println(relayStatus);

  //   // 最后发布设备状态
  //   publishStatus();
  // });
}



/**
 * 1.发布设备信息
 * 
 */
void publishDeviceInfo(){
  // StaticJsonDocument<300> doc;
  // doc["deviceNum"] = (String)deviceNum;
  // doc["categoryId"] = productId;
  // doc["firmwareVersion"] = "1.0";
  // doc["ownerId"]= (String)userId;
  // Serial.print("publish device info:");
  // serializeJson(doc, Serial);
  // //发布  
  // String output;
  // serializeJson(doc, output);
  // const char *msg=output.c_str();
  // client.publish("device_info",msg); 
}



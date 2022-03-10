/***********************************************************
 * author: kerwincui
 * create: 2022-02-20
 * email：164770707@qq.com
 * source:https://github.com/kerwincui/wumei-smart
 * board:esp8266 core for arduino v3.0.2
 ***********************************************************/

#include "Helper.h"

long lastMqttConn;    // 上次mqtt连接时间
long lastPublish;     // 上次发布监测数据时间

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
  // Wifi掉线重连
  if (WiFi.status() != WL_CONNECTED)
  {
    connectWifi();
  }

  // 非阻塞Mqtt重连，间隔30秒
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

  // 非阻塞发布实时监测数据,间隔默认1000毫秒
  if(WiFi.status() == WL_CONNECTED && monitorCount>0){
    long now = millis();
    if (now - lastPublish > monitorInterval)
      {
        lastPublish = now;
        monitorCount--;
        publishMonitor();
      }
  }
}

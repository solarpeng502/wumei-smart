/***********************************************************
 * author: kerwincui
 * create: 2022-02-20
 * email：164770707@qq.com
 * source:https://github.com/kerwincui/wumei-smart
 * board:esp8266 core for arduino v3.0.2
 ***********************************************************/
 
#ifndef _HELPER_H
#define _HELPER_H

#include "Base64.h"
#include <ESP8266WiFi.h>
#include <Ethernet.h>
#include <ESP8266HTTPClient.h>
#include <PubSubClient.h>                   // 版本2.7.0
#include <ArduinoJson.h>                    // 版本6.19.1

extern WiFiClient wifiClient;
extern PubSubClient mqttClient;

extern char *deviceNum ;      // 设备编号（重要，同时是Mqtt的clientId）
extern char *userId;          // 用户ID
extern float rssi;            // 信号强度（信号极好4格[-55— 0]，信号好3格[-70— -55]，信号一般2格[-85— -70]，信号差1格[-100— -85]）
extern float firmwareVersion; // 固件版本
extern long productId;        // 产品ID
extern char *wifiSsid;        // WIFI的SSID
extern char *wifiPwd;         // WIFI的密码
extern char *mqttHost;        // Mqtt消息服务器地址
extern int mqttPort;          // Mqtt消息服务器端口
extern char *mqttUserName;    // Mqtt消息服务器账号
extern char *mqttPwd;         // Mqtt消息服务器密码
extern char mqttSecret[17];   // Mqtt秘钥,16位
extern char wumei_iv[17];     // AES加密偏移量，固定值16位
extern String ntpServer;      //NTP服务地址



// Mqtt回调
void callback(char *topic, byte *payload, unsigned int length);
// 连接wifi
void connectWifi();
// 连接mqtt
void connectMqtt();

//打印提示信息
void printMsg(String tips);

// 生成密码
String generationPwd();
// 获取时间
String getTime();

// AES加密
String encrypt(String plain_data,char *wumei_key,char *wumei_iv);
// AES解密
String decrypt(String encoded_data_str,char *wumei_key,char *wumei_iv);


#endif 

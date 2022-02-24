/***********************************************************
 * author: kerwincui
 * create: 2022-02-20
 * email：164770707@qq.com
 * source:https://github.com/kerwincui/wumei-smart
 * board:esp8266 core for arduino v3.0.2
 ***********************************************************/

#include <ESP8266WiFi.h>
#include <Ethernet.h>
#include <ESP8266HTTPClient.h>
#include "Base64.h"
#include <PubSubClient.h>              // 版本2.7.0
#include <ArduinoJson.h>               // 版本6.19.1

char *deviceNum = "ADEF5561"; // 设备编号（重要，同时是Mqtt的clientId）
char *userId="1";                      // 用户ID
float rssi=0;                          // 信号强度（信号极好4格[-55— 0]，信号好3格[-70— -55]，信号一般2格[-85— -70]，信号差1格[-100— -85]）
float firmwareVersion=1.1;             // 固件版本
long productId=1;                      // 产品ID

char *wifiSsid="huawei";               // WIFI的SSID
char *wifiPwd="15208747707";           // WIFI的密码

char *mqttHost="wumei.live";           // Mqtt消息服务器地址
int mqttPort=1883;                     // Mqtt消息服务器端口

char *mqttUserName="wumei-smart";      // Mqtt消息服务器账号
char *mqttPwd="PARI266S5L8K5475";           // Mqtt消息服务器密码
char cipher_key[17]="KHR00FS8T30ISD7S";        // Mqtt秘钥
char cipher_iv[17]="wumei-smart-open";           // AES加密偏移量，固定值16位

String ntpServer="http://wumei.live:8080/iot/tool/ntp?deviceSendTime=";  //NTP服务地址

long lastMqttConn;                    //上次mqtt连接时间
WiFiClient wifiClient;
PubSubClient mqttClient(wifiClient);

// AES CBC Encryption
String encrypt(String plain_data){
  int i;
  // PKCS#7 Padding (Encryption), Block Size : 16
  int len = plain_data.length();
  int n_blocks = len / 16 + 1;
  uint8_t n_padding = n_blocks * 16 - len;
  uint8_t data[n_blocks*16];
  memcpy(data, plain_data.c_str(), len);
  for(i = len; i < n_blocks * 16; i++){
    data[i] = n_padding;
  }
  
  uint8_t key[16], iv[16];
  memcpy(key, cipher_key, 16);
  memcpy(iv, cipher_iv, 16);

  // encryption context
  br_aes_big_cbcenc_keys encCtx;

  // reset the encryption context and encrypt the data
  br_aes_big_cbcenc_init(&encCtx, key, 16);
  br_aes_big_cbcenc_run( &encCtx, iv, data, n_blocks*16 );

  // Base64 encode
  len = n_blocks*16;
  char encoded_data[ base64_enc_len(len) ];
  base64_encode(encoded_data, (char *)data, len);
  
  return String(encoded_data);
}

// AES CBC Decryption
String decrypt(String encoded_data_str){  
  int input_len = encoded_data_str.length();
  char *encoded_data = const_cast<char*>(encoded_data_str.c_str());
  int len = base64_dec_len(encoded_data, input_len);
  uint8_t data[ len ];
  base64_decode((char *)data, encoded_data, input_len);
  
  uint8_t key[16], iv[16];
  memcpy(key, cipher_key, 16);
  memcpy(iv, cipher_iv, 16);

  int n_blocks = len / 16;

  br_aes_big_cbcdec_keys decCtx;

  br_aes_big_cbcdec_init(&decCtx, key, 16);
  br_aes_big_cbcdec_run( &decCtx, iv, data, n_blocks*16 );

  // PKCS#7 Padding (Decryption)
  uint8_t n_padding = data[n_blocks*16-1];
  len = n_blocks*16 - n_padding;
  char plain_data[len + 1];
  memcpy(plain_data, data, len);
  plain_data[len] = '\0';

  return String(plain_data);
}

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

  // Serial.println("Encrypting...");  
  // String encdata = encrypt(mqttPwd);
  // Serial.println("encrypted:");  
  // Serial.println(encdata);  
  // String decdata = decrypt(encdata);
  // Serial.println("decrypted:");  
  // Serial.println(decdata);
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

// Mqtt回调
void callback(char *topic, byte *payload, unsigned int length)
{
  
}

// 连接wifi
void connectWifi()
{
    printMsg("连接 ");
    Serial.print(wifiSsid);
    WiFi.mode(WIFI_STA);
    WiFi.begin(wifiSsid, wifiPwd);
    while (WiFi.status() != WL_CONNECTED)
    {
        delay(500);
        Serial.print(".");
    }
    printMsg("WiFi连接成功");
    printMsg("IP地址: ");
    Serial.print(WiFi.localIP());
}

// 连接mqtt
void connectMqtt()
{  
    printMsg("连接Mqtt服务器...");
    // 生成mqtt认证密码
    String password=generationPwd();
    String encryptPassword=encrypt(password);
    printMsg("加密密码："+encryptPassword);
    mqttClient.setServer(mqttHost, mqttPort);
    mqttClient.setCallback(callback);
    //连接
    bool connectResult = mqttClient.connect(deviceNum, mqttUserName, encryptPassword.c_str());
    if (connectResult)
    {
        printMsg("连接成功");
        //发布设备状态
        // publishState(client, deviceId, apiKey);
        //订阅
        printMsg("订阅云端数据");
        // String switchTopic = "set/switch/" + (String)deviceId;
        // mqttClient.subscribe(switchTopic.c_str());
    }
    else
    {
        printMsg("连接失败, rc=");
        Serial.print(mqttClient.state());
    }
}


//打印提示信息
void printMsg(String tips)
{
    Serial.print("\r\n[");
    Serial.print(millis());
    Serial.print("ms]");
    Serial.print(tips);
}


/**
 * 生成密码
 */
String generationPwd(){
  String jsonTime=getTime();
  StaticJsonDocument<128> doc; //128字节内存池容量
  //解析JSON
  DeserializationError error = deserializeJson(doc, jsonTime);
  if (error) {
    printMsg("Json解析失败：");
    Serial.print(error.f_str());
    return "";
  }
  // 获取当前时间
  float deviceSendTime = doc["deviceSendTime"];
  float serverSendTime = doc["serverSendTime"];
  float serverRecvTime = doc["serverRecvTime"];
  float deviceRecvTime = millis();
  float now=(serverSendTime+serverRecvTime+deviceRecvTime-deviceSendTime)/2;
  float expireTime=now+24*60*60*1000; //当前时间延长24小时
  String password=(String)mqttPwd+"&"+String(expireTime,0);
  printMsg("未加密密码:"+password);
  //AES-CBC-128加密

  return password;
}

/**
 * 获取时间
 */
String getTime(){
  while(WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    printMsg("获取时间...");
    if (http.begin(wifiClient, (ntpServer+(String)millis()).c_str())) {
      // 发送请求
      int httpCode = http.GET();
      if (httpCode > 0) {
        if (httpCode == HTTP_CODE_OK || httpCode == HTTP_CODE_MOVED_PERMANENTLY) {
          printMsg("获取时间成功，data:");
          Serial.print(http.getString());
          return http.getString();
        }
      } else {        
        printMsg("获取时间失败，error:");
        Serial.printf(http.errorToString(httpCode).c_str());
      }
      http.end();
    } else {
      printMsg("连接Http失败");
    }
    delay(500);
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
 * 2.获取设备状态
 * 
 */
void subscribeGetStatus(){
  // client.subscribe("status/get/"+(String)deviceNum, [](const String & payload) {
  //   // 发布设备状态
  //   publishStatus();
  // });
}

/**
 * 3.更新设备配置
 * 
 */
void subscribeUpdateSetting(){
  // client.subscribe("setting/set/"+(String)deviceNum, [](const String & payload) {
  //   StaticJsonDocument<1024> doc; //1024字节内存池容量
  //   //解析JSON
  //   DeserializationError error = deserializeJson(doc, payload);
  //   if (error) {
  //     Serial.print(F("deserializeJson() failed: "));
  //     Serial.println(error.f_str());
  //     return;
  //   }
  
  //   // 获取配置
  //   const char* deviceNum = doc["deviceNum"];  
  //   Serial.println("deviceNum:"+(String)deviceNum);

  //   int isAlarm = doc["isAlarm"];  
  //   Serial.print("isAlarm:");  
  //   Serial.println(isAlarm);  

  //   // 最后发布设备配置
  //   publishSetting();
  // });

}

/*
 * 4.获取设备配置
 *
 */
void subscribeGetSetting(){
  // client.subscribe("setting/get/"+(String)deviceNum, [](const String & payload) {
  //   // 发布设备配置
  //   publishSetting();
  // });
}


/**
 * 1.发布设备信息
 * 
 */
void publishDeviceInfo(){
  StaticJsonDocument<300> doc;
  doc["deviceNum"] = (String)deviceNum;
  doc["categoryId"] = productId;
  doc["firmwareVersion"] = "1.0";
  doc["ownerId"]= (String)userId;
  Serial.print("publish device info:");
  serializeJson(doc, Serial);
  //发布  
  String output;
  serializeJson(doc, output);
  const char *msg=output.c_str();
  // client.publish("device_info",msg); 
}

/**
 * 2.发布设备状态
 * 
 */
void publishStatus(){
  StaticJsonDocument<1024> doc;
  doc["deviceNum"] = (String)deviceNum;
  doc["relayStatus"] = 1;
  doc["lightStatus"] = 1;
  doc["isOnline"] = 1;
  doc["rssi"] = -73;
  doc["deviceTemperature"] = 28.68;
  doc["airTemperature"] = 24.66;
  doc["airHumidity"] = 46.8;
  doc["triggerSource"] = 0;
  doc["brightness"] = 100;
  doc["lightInterval"] = 500;
  doc["lightMode"] = 0;
  doc["fadeTime"] = 259;
  doc["red"] = 255;
  doc["green"] = 255;
  doc["blue"] = 255; 
  Serial.print("publish status:");
  serializeJson(doc, Serial); 
  // 发布  
  String output;
  serializeJson(doc, output);
  const char *msg=output.c_str();
  // client.publish("status",msg); 
}

/**
 * 3.发布设备配置
 * 
 */
void publishSetting(){
  StaticJsonDocument<1024> doc;
  doc["deviceNum"] = (String)deviceNum;
  doc["isAlarm"] = 0;
  doc["isRadar"] = 0;
  doc["isHost"] = 0;
  doc["isRfControl"]= 1,
  doc["rfOneFunc"] = 1;
  doc["rfTwoFunc"] = 2;
  doc["rfThreeFunc"] = 3;
  doc["rfFourFunc"] = 4;
  doc["ownerId"] = (String)userId;
  doc["isReset"] = 0;
  doc["isAp"] = 0;
  doc["isRfLearn"] = 0;
  doc["isRfClear"] = 0;
  doc["isSmartConfig"] = 0;
  doc["radarInterval"] = 5;
  doc["isWifiOffline"] = 0;
  doc["isOpenCertifi"] = 0;
  Serial.print("publish setting:");
  serializeJson(doc, Serial);
  //发布
  String output;
  serializeJson(doc, output);
  const char *msg=output.c_str();
  // client.publish("setting",msg); 
}

/**
 * 4.遗嘱消息
 * 
 */
void publishLastWill(){
  StaticJsonDocument<100> doc;
  doc["deviceNum"] = (String)deviceNum;
  doc["isOnline"] = 0;
  Serial.print("\npublish last will:");
  serializeJson(doc, Serial);
  // 发布
  String output;
  serializeJson(doc, output);
  int len = output.length();
  char *msg =new char[len+1];
  strcpy(msg,output.c_str());
}

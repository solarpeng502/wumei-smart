/***********************************************************
 * author: kerwincui
 * create: 2022-02-20
 * email：164770707@qq.com
 * source:https://github.com/kerwincui/wumei-smart
 * board:esp8266 core for arduino v3.0.2
 ***********************************************************/

#include "Helper.h"

WiFiClient wifiClient;
PubSubClient mqttClient;
// 设备信息
String deviceNum = "ADEF5561"; 
String userId="1";
String productId="1"; 
float rssi=0;
float firmwareVersion=1.1;
// Wifi配置
char *wifiSsid="huawei";
char *wifiPwd="15208747707";
// Mqtt配置
char *mqttHost="wumei.live";
int mqttPort=1883;
char *mqttUserName="wumei-smart";
char *mqttPwd="PARI266S5L8K5475";
char mqttSecret[17]="KHR00FS8T30ISD7S";
char wumei_iv[17]="wumei-smart-open"; 
int publishNum=0;
long publishInterval=600;
// NTP地址
String ntpServer="http://wumei.live:8080/iot/tool/ntp?deviceSendTime=";

// 订阅的主题
String sInfoTopic="/"+productId+"/"+deviceNum+"/info/get";
String sNtpTopic = "/"+productId+"/"+deviceNum+"/ntp/get";
String sPropertyTopic="/"+productId+"/"+deviceNum+"/property/get";
String sFunctionTopic="/"+productId+"/"+deviceNum+"/function/get";
String sMonitorTopic="/"+productId+"/"+deviceNum+"/monitor/get";
String sOtaTopic = "/"+productId+"/"+deviceNum+"/ota/get";
// 发布的主题
String pInfoTopic="/"+productId+"/"+deviceNum+"/info/post";
String pNtpTopic = "/"+productId+"/"+deviceNum+"/ntp/post";
String pPropertyTopic="/"+productId+"/"+deviceNum+"/property/post";
String pFunctionTopic="/"+productId+"/"+deviceNum+"/function/post";
String pMonitorTopic="/"+productId+"/"+deviceNum+"/monitor/post";
String pEventTopic="/"+productId+"/"+deviceNum+"/event/post";

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
    String encryptPassword=encrypt(password,mqttSecret,wumei_iv);
    printMsg("密码(已加密)："+encryptPassword);
    mqttClient.setClient(wifiClient);
    mqttClient.setServer(mqttHost, mqttPort);
    mqttClient.setCallback(callback);
    //连接
    bool connectResult = mqttClient.connect(deviceNum.c_str(), mqttUserName, encryptPassword.c_str());
    if (connectResult)
    {
        printMsg("连接成功");
        // 订阅(设备信息、OTA、NTP、属性、功能、实时监测) ,订阅4个以上主题会不断重连，未解决问题          
        mqttClient.subscribe(sInfoTopic.c_str(),1);
        // mqttClient.subscribe(sOtaTopic.c_str(),1);
        // mqttClient.subscribe(sNtpTopic.c_str(),1);
        mqttClient.subscribe(sPropertyTopic.c_str(),1);
        mqttClient.subscribe(sFunctionTopic.c_str(),1);
        mqttClient.subscribe(sMonitorTopic.c_str(),1);
        printMsg("订阅主题："+sInfoTopic);
        printMsg("订阅主题："+sPropertyTopic);
        printMsg("订阅主题："+sFunctionTopic);
        printMsg("订阅主题："+sMonitorTopic);        
    }
    else
    {
        printMsg("连接失败, rc=");
        Serial.print(mqttClient.state());
    }
}

// Mqtt回调
void callback(char *topic, byte *payload, unsigned int length)
{
  printMsg("接收数据：");
  String data="";
  for (int i = 0; i < length; i++)
  {
    Serial.print((char)payload[i]);
    data+=(char)payload[i];
  }
  
  StaticJsonDocument<1024> doc; //1024字节内存池容量
  //解析JSON
  DeserializationError error = deserializeJson(doc, payload);
  if (error) {
      Serial.print(F("deserializeJson() failed: "));
      Serial.println(error.f_str());
      return;
    }

  if(strcmp(topic, sInfoTopic.c_str()) == 0){
    // 获取设备信息判断是否启用设备影子，并发布信息

    publishInfo();
  }else if(strcmp(topic, sOtaTopic.c_str()) == 0){
    // Http下载固件，设备升级

  }else if(strcmp(topic, sNtpTopic.c_str()) == 0){
    // 计算设备当前时间：(${serverRecvTime} + ${serverSendTime} + ${deviceRecvTime} - ${deviceSendTime}) / 2
    float deviceSendTime = doc["deviceSendTime"];
    float serverSendTime = doc["serverSendTime"];
    float serverRecvTime = doc["serverRecvTime"];
    float deviceRecvTime = millis();
    float now=(serverSendTime+serverRecvTime+deviceRecvTime-deviceSendTime)/2;
    printMsg("当前时间："+String(now,0));
  }else if(strcmp(topic, sPropertyTopic.c_str()) == 0){
    // 根据属性，设备修改对应属性,并发布属性

    printMsg("修改引脚电平");
    pinMode(15,OUTPUT);
    digitalWrite(15, HIGH);
    delay(3000);
    digitalWrite(15,LOW);
    publishProperty();
  }else if(strcmp(topic, sFunctionTopic.c_str()) == 0){
    // 根据功能，设备执行对应功能,并发布功能

    publishFunction();
  }else if(strcmp(topic, sMonitorTopic.c_str()) == 0){
    // 设置对应的实时监测次数和间隔

    
  }
}


// 发布设备信息
void publishInfo(){
  StaticJsonDocument<256> doc;
  doc["rssi"] = WiFi.RSSI();
  doc["firmwareVersion"] = firmwareVersion;
  doc["status"] = 3; // （1-未激活，2-禁用，3-在线，4-离线）
  doc["userId"]= (String)userId;
  Serial.print("发布设备信息:");
  serializeJson(doc, Serial);
  //发布  
  String output;
  serializeJson(doc, output);
  const char *msg=output.c_str();
  mqttClient.publish(pInfoTopic.c_str(),msg); 
}

// 发布时钟同步信息
void publishNtp(){
  StaticJsonDocument<128> doc;
  doc["deviceSendTime"] = millis();
  Serial.print("发布NTP信息:");
  serializeJson(doc, Serial);
  //发布  
  String output;
  serializeJson(doc, output);
  const char *msg=output.c_str();
  mqttClient.publish(pNtpTopic.c_str(),msg); 
}

// 发布属性
void publishProperty(){
  StaticJsonDocument<1024> doc;
  // 复制物模型中的属性
  doc["property"] = 0;
  Serial.print("发布属性:");
  serializeJson(doc, Serial);
  //发布  
  String output;
  serializeJson(doc, output);
  const char *msg=output.c_str();
  mqttClient.publish(pPropertyTopic.c_str(),msg); 
}

// 发布功能
void publishFunction(){
  StaticJsonDocument<1024> doc;
  // 复制物模型中的功能
  doc["function"] = 0;
  Serial.print("发布功能:");
  serializeJson(doc, Serial);
  //发布  
  String output;
  serializeJson(doc, output);
  const char *msg=output.c_str();
  mqttClient.publish(pFunctionTopic.c_str(),msg); 
}

// 发布事件
void publishEvent(){
  StaticJsonDocument<1024> doc;
  // 复制物模型中的事件
  doc["event"] = 0;
  Serial.print("发布事件:");
  serializeJson(doc, Serial);
  //发布  
  String output;
  serializeJson(doc, output);
  const char *msg=output.c_str();
  mqttClient.publish(pEventTopic.c_str(),msg); 
}

// 发布实时监测数据
void publishMonitor(){
  StaticJsonDocument<512> doc;
  // 复制物模型中的属性（实时监测模型）
  doc["monitor"] = 0;
  Serial.print("发布事件:");
  serializeJson(doc, Serial);
  //发布  
  String output;
  serializeJson(doc, output);
  const char *msg=output.c_str();
  mqttClient.publish(pMonitorTopic.c_str(),msg); 
}

// 生成密码
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
  printMsg("密码(未加密):"+password);
  return password;
}

// 获取时间
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

//打印提示信息
void printMsg(String tips)
{
    Serial.print("\r\n[");
    Serial.print(millis());
    Serial.print("ms]");
    Serial.print(tips);
}

// 加密 (AES-CBC-128-pkcs5padding)
String encrypt(String plain_data,char *wumei_key,char *wumei_iv){
  int i;
  // pkcs7padding填充 Block Size : 16
  int len = plain_data.length();
  int n_blocks = len / 16 + 1;
  uint8_t n_padding = n_blocks * 16 - len;
  uint8_t data[n_blocks*16];
  memcpy(data, plain_data.c_str(), len);
  for(i = len; i < n_blocks * 16; i++){
    data[i] = n_padding;
  }
  uint8_t key[16], iv[16];
  memcpy(key, wumei_key, 16);
  memcpy(iv, wumei_iv, 16);
  // 加密
  br_aes_big_cbcenc_keys encCtx;
  br_aes_big_cbcenc_init(&encCtx, key, 16);
  br_aes_big_cbcenc_run( &encCtx, iv, data, n_blocks*16 );
  // Base64编码
  len = n_blocks*16;
  char encoded_data[ base64_enc_len(len) ];
  base64_encode(encoded_data, (char *)data, len);  
  return String(encoded_data);
}

// 解密 (AES-CBC-128-pkcs5padding)
String decrypt(String encoded_data_str,char *wumei_key,char *wumei_iv){  
  int input_len = encoded_data_str.length();
  char *encoded_data = const_cast<char*>(encoded_data_str.c_str());
  int len = base64_dec_len(encoded_data, input_len);
  uint8_t data[ len ];
  base64_decode((char *)data, encoded_data, input_len);  
  uint8_t key[16], iv[16];
  memcpy(key, wumei_key, 16);
  memcpy(iv, wumei_iv, 16);
  int n_blocks = len / 16;
  br_aes_big_cbcdec_keys decCtx;
  br_aes_big_cbcdec_init(&decCtx, key, 16);
  br_aes_big_cbcdec_run( &decCtx, iv, data, n_blocks*16 );
  // PKCS#7 Padding 填充
  uint8_t n_padding = data[n_blocks*16-1];
  len = n_blocks*16 - n_padding;
  char plain_data[len + 1];
  memcpy(plain_data, data, len);
  plain_data[len] = '\0';
  return String(plain_data);
}

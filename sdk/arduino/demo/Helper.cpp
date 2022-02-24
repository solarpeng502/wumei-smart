/***********************************************************
 * author: kerwincui
 * create: 2022-02-20
 * email：164770707@qq.com
 * source:https://github.com/kerwincui/wumei-smart
 * board:esp8266 core for arduino v3.0.2
 ***********************************************************/

#include "Helper.h"

WiFiClient wifiClient;
PubSubClient mqttClient(wifiClient);

char *deviceNum = "ADEF5561"; 
char *userId="1";
float rssi=0;
float firmwareVersion=1.1;
long productId=1; 

char *wifiSsid="huawei";
char *wifiPwd="15208747707";

char *mqttHost="wumei.live";
int mqttPort=1883;

char *mqttUserName="wumei-smart";
char *mqttPwd="PARI266S5L8K5475";
char mqttSecret[17]="KHR00FS8T30ISD7S";
char wumei_iv[17]="wumei-smart-open"; 

String ntpServer="http://wumei.live:8080/iot/tool/ntp?deviceSendTime=";


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
    String encryptPassword=encrypt(password,mqttSecret,wumei_iv);
    printMsg("密码(已加密)："+encryptPassword);
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

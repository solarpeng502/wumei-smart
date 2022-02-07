package com.ruoyi.iot.oauth.api;

import com.alibaba.fastjson.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * kerwincui
 */
@RestController
public class SpeakerController {
    @GetMapping("/oauth/speaker/classical")
    public JSONObject getBook() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JSONObject bookJson = new JSONObject();
        bookJson.put("1", "西游记");
        bookJson.put("2", "水浒传");
        bookJson.put("3", "红楼梦");
        bookJson.put("4", "三国演义");
        return bookJson;
    }
    @PostMapping("/oauth/speaker/sell")
    public JSONObject sellBook() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JSONObject bookJson = new JSONObject();
        bookJson.put("4", "三国演义");
        return bookJson;
    }
}

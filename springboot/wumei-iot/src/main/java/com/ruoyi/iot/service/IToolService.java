package com.ruoyi.iot.service;

import com.ruoyi.common.core.domain.model.RegisterBody;
import com.ruoyi.iot.model.RegisterUserInput;


import java.util.List;

/**
 * 
 * @author kerwincui
 * @date 2021-12-16
 */
public interface IToolService
{
    /**
     * 注册
     */
    public String register(RegisterUserInput registerBody);
}

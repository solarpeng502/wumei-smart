package com.ruoyi.iot.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.iot.domain.ThingsModelTemplate;
import com.ruoyi.iot.mapper.ThingsModelTemplateMapper;
import com.ruoyi.iot.model.ImportThingsModelInput;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.iot.mapper.ThingsModelMapper;
import com.ruoyi.iot.domain.ThingsModel;
import com.ruoyi.iot.service.IThingsModelService;

import static com.ruoyi.common.utils.SecurityUtils.getLoginUser;

/**
 * 物模型Service业务层处理
 * 
 * @author kerwincui
 * @date 2021-12-16
 */
@Service
public class ThingsModelServiceImpl implements IThingsModelService 
{
    @Autowired
    private ThingsModelMapper thingsModelMapper;

    @Autowired
    private ThingsModelTemplateMapper thingsModelTemplateMapper;

    /**
     * 查询物模型
     * 
     * @param modelId 物模型主键
     * @return 物模型
     */
    @Override
    public ThingsModel selectThingsModelByModelId(Long modelId)
    {
        return thingsModelMapper.selectThingsModelByModelId(modelId);
    }

    /**
     * 查询物模型列表
     * 
     * @param thingsModel 物模型
     * @return 物模型
     */
    @Override
    public List<ThingsModel> selectThingsModelList(ThingsModel thingsModel)
    {
        return thingsModelMapper.selectThingsModelList(thingsModel);
    }

    /**
     * 新增物模型
     * 
     * @param thingsModel 物模型
     * @return 结果
     */
    @Override
    public int insertThingsModel(ThingsModel thingsModel)
    {
        SysUser user = getLoginUser().getUser();
        thingsModel.setTenantId(user.getUserId());
        thingsModel.setTenantName(user.getUserName());
        thingsModel.setCreateTime(DateUtils.getNowDate());
        return thingsModelMapper.insertThingsModel(thingsModel);
    }

    @Override
    public int importByTemplateIds(ImportThingsModelInput input){
        SysUser user = getLoginUser().getUser();
        // 根据ID集合获取通用物模型列表
        List<ThingsModelTemplate> templateList=thingsModelTemplateMapper.selectThingsModelTemplateByTemplateIds(input.getTemplateIds());
        //转换为产品物模型，并批量插入
        List<ThingsModel> list=new ArrayList<>();
        for(ThingsModelTemplate template : templateList){
            ThingsModel thingsModel= new ThingsModel();
            BeanUtils.copyProperties(template,thingsModel);
            thingsModel.setTenantId(user.getUserId());
            thingsModel.setTenantName(user.getUserName());
            thingsModel.setCreateTime(DateUtils.getNowDate());
            thingsModel.setProductId(input.getProductId());
            thingsModel.setProductName(input.getProductName());
            thingsModel.setModelId(template.getTemplateId());
            thingsModel.setModelName(template.getTemplateName());
            list.add(thingsModel);
        }
        return thingsModelMapper.insertBatchThingsModel(list);
    }

    /**
     * 修改物模型
     * 
     * @param thingsModel 物模型
     * @return 结果
     */
    @Override
    public int updateThingsModel(ThingsModel thingsModel)
    {
        thingsModel.setUpdateTime(DateUtils.getNowDate());
        return thingsModelMapper.updateThingsModel(thingsModel);
    }

    /**
     * 批量删除物模型
     * 
     * @param modelIds 需要删除的物模型主键
     * @return 结果
     */
    @Override
    public int deleteThingsModelByModelIds(Long[] modelIds)
    {
        return thingsModelMapper.deleteThingsModelByModelIds(modelIds);
    }

    /**
     * 删除物模型信息
     * 
     * @param modelId 物模型主键
     * @return 结果
     */
    @Override
    public int deleteThingsModelByModelId(Long modelId)
    {
        return thingsModelMapper.deleteThingsModelByModelId(modelId);
    }
}

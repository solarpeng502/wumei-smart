package com.ruoyi.iot.model;

import java.math.BigDecimal;
import java.util.List;

public class EnumModel extends ThingsModelDto
{
    private List<EnumItem> enumList;

    public List<EnumItem> getEnumList() {
        return enumList;
    }

    public void setEnumList(List<EnumItem> enumList) {
        this.enumList = enumList;
    }
}

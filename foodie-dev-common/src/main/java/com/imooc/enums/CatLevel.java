package com.imooc.enums;

/**
 * @Desc: 分类级别
 */
public enum  CatLevel {
    ROOT(1,"第一级"),
    SECOND(2,"第二级"),
    THIRD(3,"第三级");

    public final Integer type;
    public final String value;

    CatLevel(Integer type,String value){
        this.type=type;
        this.value=value;
    }
}

package com.netease.im.uikit.contact.core.query;


import com.netease.im.uikit.contact.core.item.AbsContactItem;
import java.util.List;

/**
 * 通讯录数据源提供者接口
 * Created by huangjun on 2015/4/2.
 */
public interface IContactDataProvider {
    public List<AbsContactItem> provide(TextQuery query);
}

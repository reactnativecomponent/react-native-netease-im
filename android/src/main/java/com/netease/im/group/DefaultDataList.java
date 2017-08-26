package com.netease.im.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dowin on 2017/8/16.
 */

public class DefaultDataList extends AbstractDataList{
    private List<AbstractDataList.Group> groups;

    private Map<String, Integer> indexes;

    public DefaultDataList(GroupStrategy groupStrategy) {
        super(groupStrategy);
    }

    @Override
    public int getCount() {
        int count = 0;
        for (AbstractDataList.Group group : groups) {
            count += group.getCount();
        }
        return count;
    }

    @Override
    public AbstractItem getItem(int index) {
        int count = 0;
        for (AbstractDataList.Group group : groups) {
            int gIndex = index - count;
            int gCount = group.getCount();

            if (gIndex >= 0 && gIndex < gCount) {
                return group.getItem(gIndex);
            }

            count += gCount;
        }

        return null;
    }

    @Override
    public boolean isEmpty() {
        return groups.isEmpty() || indexes.isEmpty();
    }

    @Override
    public List<AbstractItem> getItems() {
        List<AbstractItem> items = new ArrayList<>();
        for (AbstractDataList.Group group : groups) {
            AbstractItem head = group.getHead();
            if (head != null) {
                items.add(head);
            }
            items.addAll(group.getItems());
        }

        return items;
    }

    @Override
    public Map<String, Integer> getIndexes() {
        return indexes;
    }

    @Override
    public void build() {
        //
        // GROUPS
        //

        List<AbstractDataList.Group> groups = new ArrayList<>();
        groups.add(groupNull);
        groups.addAll(groupMap.values());
        sortGroups(groups);

        //
        // INDEXES
        //

        Map<String, Integer> indexes = new HashMap<>();
        int count = 0;
        for (AbstractDataList.Group group : groups) {
            if (group.id != null) {
                indexes.put(group.id, count);
            }

            count += group.getCount();
        }

        //
        // RESULT
        //

        this.groups = groups;
        this.indexes = indexes;
    }

    public void clear(){
        super.clear();
        if(groups!=null){
            groups.clear();
        }
        if(indexes!=null){
            indexes.clear();
        }
    }
}

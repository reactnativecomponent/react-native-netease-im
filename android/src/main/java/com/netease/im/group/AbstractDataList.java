package com.netease.im.group;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dowin on 2017/8/16.
 */

public abstract class AbstractDataList {
    protected final GroupStrategy groupStrategy;

    protected final Map<String, Group> groupMap = new HashMap<>();

    protected final Group groupNull = new Group(null, null);

    private static final class NoneGroupStrategy extends GroupStrategy {
        @Override
        public String belongs(AbstractItem item) {
            return null;
        }

        @Override
        public int compare(String lhs, String rhs) {
            return 0;
        }
    }

    public AbstractDataList(GroupStrategy groupStrategy) {
        if (groupStrategy == null) {
            groupStrategy = new NoneGroupStrategy();
        }

        this.groupStrategy = groupStrategy;
    }

    public abstract void build();
    public abstract int getCount();

    public abstract boolean isEmpty();

    public abstract AbstractItem getItem(int index);

    public abstract List<AbstractItem> getItems();

    public abstract Map<String, Integer> getIndexes();


    public final void add(AbstractItem item) {
        if (item == null) {
            return;
        }

        Group group;

        String id = groupStrategy.belongs(item);
        if (id == null) {
            group = groupNull;
        } else {
            group = groupMap.get(id);
            if (group == null) {
                group = new Group(id, groupStrategy.getName(id));
                groupMap.put(id, group);
            }
        }

        group.add(item);
    }

    protected final void sortGroups(List<Group> groups) {
        Collections.sort(groups, new Comparator<Group>() {
            @Override
            public int compare(Group lhs, Group rhs) {
                return groupStrategy.compare(lhs.id, rhs.id);
            }
        });
    }
    protected static final class Group {
        final String id;

        final String title;

        final boolean hasHead;

        final List items = new ArrayList();

        Group(String id, String title) {
            this.id = id;
            this.title = title;
            this.hasHead = !TextUtils.isEmpty(title);
        }

        int getCount() {
            return items.size() + (hasHead ? 1 : 0);
        }

        AbstractItem getItem(int index) {
            if (hasHead) {
                if (index == 0) {
                    return getHead();
                } else {
                    index--;
                    return (AbstractItem) (index >= 0 && index < items.size() ? items.get(index) : null);
                }
            } else {
                return (AbstractItem) (index >= 0 && index < items.size() ? items.get(index) : null);
            }
        }

        AbstractItem getHead() {
            return hasHead ? new LabeItem(title) : null;
        }

        List<AbstractItem> getItems() {
            return items;
        }

        void add(AbstractItem add) {
            if (add instanceof Comparable) {
                addComparable((Comparable<AbstractItem>) add);
            } else {
                items.add(add);
            }
        }

        void merge(Group group) {
            for (Object item : group.items) {
                add((AbstractItem) item);
            }
        }

        void addComparable(Comparable<AbstractItem> add) {
            if (items.size() < 8) {
                for (int index = 0; index < items.size(); index++) {
                    Comparable<AbstractItem> item = (Comparable<AbstractItem>) items.get(index);
                    if ((item.compareTo((AbstractItem) add)) > 0) {
                        items.add(index, add);
                        return;
                    }
                }
                items.add(add);
            } else {
                int index = Collections.binarySearch(items, add);
                if (index < 0) {
                    index = -index;
                    --index;
                }
                if (index >= items.size()) {
                    items.add(add);
                } else {
                    items.add(index, add);
                }
            }
        }
    }
    public void clear(){
        if(groupMap!=null){
            groupMap.clear();
        }
    }
}

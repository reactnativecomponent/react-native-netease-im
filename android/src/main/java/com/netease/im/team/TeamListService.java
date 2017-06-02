package com.netease.im.team;

import android.os.AsyncTask;

import com.netease.im.uikit.common.util.log.LogUtil;
import com.netease.im.uikit.contact.core.item.ContactItemFilter;
import com.netease.im.uikit.contact.core.item.ItemTypes;
import com.netease.im.uikit.contact.core.model.AbsContactDataList;
import com.netease.im.uikit.contact.core.model.ContactDataList;
import com.netease.im.uikit.contact.core.model.ContactDataTask;
import com.netease.im.uikit.contact.core.model.ContactGroupStrategy;
import com.netease.im.uikit.contact.core.provider.ContactDataProvider;
import com.netease.im.uikit.contact.core.query.IContactDataProvider;
import com.netease.im.uikit.contact.core.query.TextQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dowin on 2017/5/9.
 */

public class TeamListService {

    public interface OnLoadListener {
        void updateData(ContactDataList datas);
    }

    final static String TAG = "TeamListService";
    private final List<Task> tasks = new ArrayList<>();
    private final ContactGroupStrategy groupStrategy = new ContactsGroupStrategy();

    private final IContactDataProvider dataProvider = new ContactDataProvider(ItemTypes.TEAM);
    private ContactItemFilter filter;
    private ContactDataList datas;
    private OnLoadListener onLoadListener;

    private TeamListService() {

    }

    static class InstanceHolder {
        final static TeamListService instance = new TeamListService();
    }

    public static TeamListService getInstance() {
        return InstanceHolder.instance;
    }


    public final void query(String query) {
        startTask(new TextQuery(query), true);
    }

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }

    public void clear() {
        if (datas != null) {
            datas.clear();
            datas = null;
        }

        for (Task task : tasks) {
            task.cancel(false); // 设为true有风险！
        }
        tasks.clear();
    }

    public final boolean load(boolean reload) {
        if (!reload && !isEmpty()) {

            return false;
        }

        LogUtil.i(TAG, "contact load data");

        startTask(null, false);

        return true;
    }

    public boolean isEmpty() {
        return datas != null ? datas.isEmpty() : true;
    }

    /**
     * 启动搜索任务
     *
     * @param query 要搜索的信息，填null表示查询所有数据
     * @param abort 是否终止：例如搜索的时候，第一个搜索词还未搜索完成，第二个搜索词已生成，那么取消之前的搜索任务
     */
    private void startTask(TextQuery query, boolean abort) {
        if (abort) {
            for (Task task : tasks) {
                task.cancel(false); // 设为true有风险！
            }
        }

        Task task = new Task(new ContactDataTask(query, dataProvider, filter));

        tasks.add(task);

        task.execute();
    }

    /**
     * 搜索/查询数据异步任务
     */

    private class Task extends AsyncTask<Void, Object, Void> implements ContactDataTask.Host {
        final ContactDataTask task;

        Task(ContactDataTask task) {
            task.setHost(this);

            this.task = task;
        }

        //
        // HOST
        //

        @Override
        public void onData(ContactDataTask task, AbsContactDataList datas, boolean all) {
            publishProgress(datas, all);
        }

        @Override
        public boolean isCancelled(ContactDataTask task) {
            return isCancelled();
        }

        //
        // AsyncTask
        //

        @Override
        protected void onPreExecute() {
//            onPreReady();
        }

        @Override
        protected Void doInBackground(Void... params) {
            task.run(new ContactDataList(groupStrategy));

            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            AbsContactDataList datas = (AbsContactDataList) values[0];
            boolean all = (Boolean) values[1];
            LogUtil.i(TAG, datas.getCount() + "");


//            onPostLoad(datas.isEmpty(), datas.getQueryText(), all);

            updateData(datas);
        }

        @Override
        protected void onPostExecute(Void result) {
//            onTaskFinish(this);
        }

        @Override
        protected void onCancelled() {
            onLoadListener = null;
//            onTaskFinish(this);
        }
    }

    private void updateData(AbsContactDataList datas) {
        this.datas = (ContactDataList) datas;
        if (onLoadListener != null) {
            onLoadListener.updateData(this.datas);
        }
    }

    private static final class ContactsGroupStrategy extends ContactGroupStrategy {
        public ContactsGroupStrategy() {
            add(ContactGroupStrategy.GROUP_NULL, -1, "");
        }
    }

}

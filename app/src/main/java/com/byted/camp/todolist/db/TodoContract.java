package com.byted.camp.todolist.db;

import android.provider.BaseColumns;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {

    // TODO 定义表结构和 SQL 语句常量
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TodoEntry.TABLE_NAME + " (" +
                    TodoEntry._ID + " LONG PRIMARY KEY," +
                    TodoEntry.COLUMN_NAME_CONTENT + " TEXT," +
                    TodoEntry.COLUMN_NAME_STATE +" INT," +
                    TodoEntry.COLUMN_NAME_DATE + " TEXT)";

    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TodoEntry.TABLE_NAME;

    private TodoContract() {
    }

    public static class TodoEntry implements BaseColumns{

        public static final String TABLE_NAME = "Note";

        public static final String COLUMN_NAME_DATE = "Date";

        public static final String COLUMN_NAME_STATE = "State";

        public static final String COLUMN_NAME_CONTENT = "Content";
    }
}

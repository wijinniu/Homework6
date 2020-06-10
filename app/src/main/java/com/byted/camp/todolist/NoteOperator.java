package com.byted.camp.todolist;

import com.byted.camp.todolist.beans.Note;

import java.text.ParseException;

/**
 * Created on 2019/1/23.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public interface NoteOperator {

    void deleteNote(Note note);

    void updateNote(Note note);

    void reflash() throws ParseException;
}

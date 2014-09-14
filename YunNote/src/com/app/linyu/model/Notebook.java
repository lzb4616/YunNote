package com.app.linyu.model;




import java.util.Date;

import com.app.linyu.config.Constants;
import com.app.linyu.json.JSONObject;


/**
 * notebook 笔记本
 *
 * @author haiwen
 */
public class Notebook {
    /**
     * JSON field names for notebook 
     */
    public static final String PATH = "path";
    public static final String NAME = "name";
    public static final String NOTES = "notes_num";
    public static final String CREATE_TIME = "create_time";
    public static final String MODIFY_TIME = "modify_time";

    private String path;
    private String name;
    private int notesNum;

    private long createTime;
    private long modifyTime;

    public Notebook() {
    }

    public Notebook(String json) {
        JSONObject jsonObj = new JSONObject(json);
        this.path = jsonObj.getString(PATH);
        this.name = jsonObj.getString(NAME);
        this.notesNum = jsonObj.getInt(NOTES);
        this.createTime = jsonObj.getLong(CREATE_TIME);
        this.modifyTime = jsonObj.getLong(MODIFY_TIME);
    }

    
    public String getPath() {
        return path;
    }

   
    public void setPath(String path) {
        this.path = path;
    }

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public int getNotesNum() {
        return notesNum;
    }

    
    public void setNotesNum(int notesNum) {
        this.notesNum = notesNum;
    }

   
    public long getCreateTime() {
        return createTime;
    }

    
    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

   
    public long getModifyTime() {
        return modifyTime;
    }

   
    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        return "[Notebook path=" + path
                + ", name=" + name
                + ", notesNum=" + notesNum
                + ", createTime=" + Constants.DATE_FORMATTER.format(new Date(createTime * 1000))
                + ", modifyTime=" + Constants.DATE_FORMATTER.format(new Date(modifyTime * 1000))
                + "]";
    }
}


package com.app.linyu.model;

import com.app.linyu.config.Constants;
import com.app.linyu.json.JSONObject;

import java.util.Date;

public class User {
	
	 /**
     * JSON field names for user 
     */
    public static final String ID = "id";
    public static final String USER_NAME = "user";
    public static final String TOTAL_SIZE = "total_size";
    public static final String USED_SIZE = "used_size";
    public static final String REGISTER_TIME = "register_time";
    public static final String LAST_LOGIN_TIME = "last_login_time";
    public static final String LAST_MODIFY_TIME = "last_modify_time";
    public static final String DEFAULT_NOTEBOOK = "default_notebook";

    private String id;
    private String userName;
    private String password;
    private long totalSize;
    private long usedSize;

    private long registerTime;
    private long lastLoginTime;
    private long lastModifyTime;

   
    private String defaultNotebook;

    public User() {
    }

    public User(String json) {
        JSONObject jsonObj = new JSONObject(json);
        this.id = jsonObj.getString(ID);
        this.userName = jsonObj.getString(USER_NAME);
        this.totalSize = jsonObj.getLong(TOTAL_SIZE);
        this.usedSize = jsonObj.getLong(USED_SIZE);
        this.registerTime = jsonObj.getLong(REGISTER_TIME);
        this.lastLoginTime = jsonObj.getLong(LAST_LOGIN_TIME);
        this.lastModifyTime = jsonObj.getLong(LAST_MODIFY_TIME);
        this.defaultNotebook = jsonObj.getString(DEFAULT_NOTEBOOK);
    }
	
	
	
	
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}


	
	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

   
    public long getTotalSize() {
        return totalSize;
    }

    
    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

   
    public long getUsedSize() {
        return usedSize;
    }

    
    public void setUsedSize(long usedSize) {
        this.usedSize = usedSize;
    }

    
    public long getRegisterTime() {
        return registerTime;
    }

   
    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
    }

    
    public long getLastLoginTime() {
        return lastLoginTime;
    }

    
    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    
    public long getLastModifyTime() {
        return lastModifyTime;
    }

    
    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

   
    public String getDefaultNotebook() {
        return defaultNotebook;
    }

    
    public void setDefaultNotebook(String defaultNotebook) {
        this.defaultNotebook = defaultNotebook;
    }

    public String toString() {
        return "[User id=" + id
                + ", userName=" + userName
                + ", totalSize=" + totalSize
                + ", usedSize=" + usedSize
                + ", registerTime=" + Constants.DATE_FORMATTER.format(new Date(registerTime))
                + ", lastLoginTime=" + Constants.DATE_FORMATTER.format(new Date(lastLoginTime))
                + ", lastModifyTime=" + Constants.DATE_FORMATTER.format(new Date(lastModifyTime))
                + ", defaultNotebook=" + defaultNotebook + "]";
    }
}

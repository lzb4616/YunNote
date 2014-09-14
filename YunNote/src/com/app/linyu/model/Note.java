package com.app.linyu.model;

import com.app.linyu.config.Constants;
import com.app.linyu.json.JSONObject;

import java.io.Serializable;
import java.util.Date;


public class Note implements Serializable {

		public static final String ID = "id";
	    public static final String PATH = "path";
	    public static final String TITLE = "title";
	    public static final String AUTHOR = "author";
	    public static final String SOURCE = "source";
	    public static final String SIZE = "size";
	    public static final String CREATE_TIME = "create_time";
	    public static final String MODIFY_TIME = "modify_time";
	    public static final String CONTENT = "content";
	    public static final String LOCAL_PATH = "local_path";
	    
	    private String id;
	    private String path;
	    private String title;
	    private String author;
	    private String source;
	    private long size;
	    private long createTime = -1;
	    private long modifyTime = -1;
	    private String content;
	    private String local_path;
	    

	    public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getLocal_path() {
			return local_path;
		}

		public void setLocal_path(String local_path) {
			this.local_path = local_path;
		}

	public Note() {
	    }

	    public Note(String json) {
	        JSONObject jsonObj = new JSONObject(json);
	        // this.path = jsonObj.getString(PATH);
	        this.title = jsonObj.getString(TITLE);
	        this.author = jsonObj.getString(AUTHOR);
	        this.source = jsonObj.getString(SOURCE);
	        this.size = jsonObj.getInt(SIZE);
	        this.createTime = jsonObj.getLong(CREATE_TIME);
	        this.modifyTime = jsonObj.getLong(MODIFY_TIME);
	        this.content = jsonObj.getString(CONTENT);
	    }


	  public String getPath() {
	        return path;
	    }

	   
	    public void setPath(String path) {
	        this.path = path;
	    }

	   
	    public String getTitle() {
	        return title;
	    }

	   
	    public void setTitle(String title) {
	        this.title = title;
	    }

	   
	    public String getAuthor() {
	        return author;
	    }

	    public void setAuthor(String author) {
	        this.author = author;
	    }

	    
	    public String getSource() {
	        return source;
	    }

	   
	    public void setSource(String source) {
	        this.source = source;
	    }

	   
	    public long getSize() {
	        return size;
	    }

	    
	    public void setSize(long size) {
	        this.size = size;
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

	    
	    public String getContent() {
	        return content;
	    }

	   
	    public void setContent(String content) {
	        this.content = content;
	    }

	    @Override
	    public String toString() {
	        return "[Notebook path=" + path
	                + ", title=" + title
	                + ", author=" + author
	                + ", source=" + source
	                + ", size=" + size
	                + ", createTime=" + Constants.DATE_FORMATTER.format(new Date(createTime * 1000))
	                + ", modifyTime=" + Constants.DATE_FORMATTER.format(new Date(modifyTime * 1000))
	                + "]";
	    }
}

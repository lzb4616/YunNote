package com.app.linyu.client;


import com.app.linyu.config.Constants;
import com.app.linyu.json.JSONArray;
import com.app.linyu.json.JSONObject;
import com.app.linyu.model.Note;
import com.app.linyu.model.Notebook;
import com.app.linyu.model.Resource;
import com.app.linyu.model.User;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.oauth.OAuth;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthServiceProvider;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;



/**
 *@author zibin
 *
 */



/*
 * 此类用于通过运用有道云API获取数据，
 * OAuth认证内容详见{@link http://oauth.net/}
 * @author haiwen
 */
public class HttpClient {

    private final OAuthAccessor accessor;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    //==========================================================================================
    /**oauth认证*/
    private  final OAuthServiceProvider SERVICE_PROVIDER ;

    private  final String CONSUMER_KEY ;
    private  final String CONSUMER_SECRET;

    private  final OAuthConsumer CONSUMER ;

    private static HttpClient client ;


    //===========================================================================================
    /**
     * 构造方法，为每个user构造一个client
     *
     *
     */
    private HttpClient() {
        SERVICE_PROVIDER = new OAuthServiceProvider(
                Constants.REQUEST_TOKEN_URL,
                Constants.USER_AUTHORIZATION_URL,
                Constants.ACCESS_TOKEN_URL);
        CONSUMER_KEY = Constants.CONSUMER_KEY;
        CONSUMER_SECRET = Constants.CONSUMER_SECRET;
        CONSUMER = new OAuthConsumer(null,
                CONSUMER_KEY, CONSUMER_SECRET, SERVICE_PROVIDER);
        this.accessor = new OAuthAccessor(CONSUMER);

    }

    public static HttpClient getClient(){
        if (client==null){
            synchronized (HttpClient.class) {
                if (client == null) {
                    client = new HttpClient();
                    client.setAccessToken("e2c9c4a1519b86913372bc9bb752f310",
                            "3a33324e2e360266b1dd82ef897091e7");
                }
            }
        }
        return client;
    }
    /**
     * @return 用户的认证信息 accessor
     */
    public OAuthAccessor getOAuthAccessor() {
        return accessor;
    }

    /**
     *通过consumer key 和 consumer secret获取request token 和 secret（即OAuth认证第一步）
     * @return authorization URL
     * @throws AppException
     * @throws java.io.IOException
     */
    public String grantRequestToken(String callbackURL) throws IOException,
            AppException {
        lock.writeLock().lock();
        try {
            HttpResponse response = HttpUtils.doGet(
                    accessor.consumer.serviceProvider.requestTokenURL,
                    null, accessor);
           
            String content = HttpUtils.getResponseContent(
                    response.getEntity().getContent());
           Map<String, String> model = HttpUtils.parseOAuthResponse(content);
            accessor.requestToken = model.get(OAuth.OAUTH_TOKEN);
            accessor.tokenSecret = model.get(OAuth.OAUTH_TOKEN_SECRET);
                String authorizationURL = OAuth.addParameters(
                    accessor.consumer.serviceProvider.userAuthorizationURL,
                    OAuth.OAUTH_TOKEN, accessor.requestToken);
            if (callbackURL == null || callbackURL.isEmpty()) {
                callbackURL = accessor.consumer.callbackURL;
        }
            if (callbackURL != null && !callbackURL.isEmpty()) {
                authorizationURL = OAuth.addParameters(authorizationURL,
                        OAuth.OAUTH_CALLBACK, callbackURL);
            }
            return authorizationURL;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 获取access token 和 secret （OAuth认证第三步）
     * @param verifier （第二步返回参数）
     * @throws AppException
     * @throws java.io.IOException
     */
    public void grantAccessToken(String verifier) throws IOException, AppException {
        lock.writeLock().lock();
        try {
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put(OAuth.OAUTH_TOKEN, accessor.requestToken);
            parameters.put(OAuth.OAUTH_VERIFIER, verifier);
            HttpResponse response = HttpUtils.doGet(
                    accessor.consumer.serviceProvider.accessTokenURL,
                    parameters, accessor);
            String content = HttpUtils.getResponseContent(
                    response.getEntity().getContent());
            Map<String, String> model = HttpUtils.parseOAuthResponse(content);
            accessor.accessToken = model.get(OAuth.OAUTH_TOKEN);
            accessor.tokenSecret = model.get(OAuth.OAUTH_TOKEN_SECRET);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 设置accessToken 和 tokenSecret
     * @param accessToken 
     * @param tokenSecret
     */
    public void setAccessToken(String accessToken, String tokenSecret) {
        lock.writeLock().lock();
        try {
            accessor.accessToken = accessToken;
            accessor.tokenSecret = tokenSecret;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * @return baseUrl,用于字符串拼接
     */
    private String getBaseURL() {
        String[] parts =
            accessor.consumer.serviceProvider.accessTokenURL.split("oauth");
        return parts[0] + "yws/open/";
    }


    /**
     * 获取当前用户信息
     * @return 用户信息
     * @throws java.io.IOException
     * @throws AppException
     */
    public User getUser() throws IOException, AppException {
        lock.readLock().lock();
        try {
            String url = getBaseURL() + "user/get.json";
            HttpResponse response = HttpUtils.doGet(url, null, accessor);
            String content = HttpUtils.getResponseContent(
                    response.getEntity().getContent());
            return new User(content);
        } finally {
            lock.readLock().unlock();
        }
    }
    /**
     * 获取当前分享信息
     * @return 分享信息
     * @throws java.io.IOException
     * @throws AppException
     */
    public String getShare(String notepath) throws IOException, AppException {
        lock.readLock().lock();
        try {
            String url = getBaseURL() + "share/publish.json";
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put(Constants.PATH_PARAM, notepath);
            HttpResponse response = HttpUtils.doPostByUrlEncoded(url,
                    parameters, accessor);
            String content = HttpUtils.getResponseContent(
                    response.getEntity().getContent());
            return content;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 查看用户全部笔记本
     * http://note.youdao.com/yws/open/notebook/all.json
     *
     * @return 用户全部笔记本
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     * @throws OAuthException 
     * @throws AppException
     */
    public List<Notebook> getAllNotebooks() throws IOException, AppException {
        lock.readLock().lock();
        try {
            String url = getBaseURL() + "notebook/all.json";
            HttpResponse response = HttpUtils.doPostByUrlEncoded(url,
                    null, accessor);
            String content = HttpUtils.getResponseContent(
                    response.getEntity().getContent());
            JSONArray array = new JSONArray(content);
            List<Notebook> notebooks = new ArrayList<Notebook>();
            for (int i = 0; i < array.length(); i++) {
                Notebook notebook = new Notebook(array.get(i).toString());
                notebooks.add(notebook);
            }
            return notebooks;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 列出某一笔记本下的全部笔记
     * http://note.youdao.com/yws/open/notebook/list.json
     * @param notebookPath
     * @return notes
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     * @throws OAuthException 
     * @throws AppException
     */
    public List<String> listNotes(String notebookPath) throws IOException,
            AppException {
        lock.readLock().lock();
        try {
            String url = getBaseURL() + "notebook/list.json";
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put(Constants.NOTEBOOK_PARAM, notebookPath);
            HttpResponse response = HttpUtils.doPostByUrlEncoded(url,
                    parameters, accessor);
            String content = HttpUtils.getResponseContent(
                    response.getEntity().getContent());
            JSONArray array = new JSONArray(content);
            List<String> notes = new ArrayList<String>();
            for (int i = 0; i < array.length(); i++) {
                notes.add(array.getString(i));
            }
            return notes;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 新建笔记本
     * http://note.youdao.com/yws/open/notebook/create.json
     * @param name（笔记本名）
     * @param group
     * @return path(笔记本路径)
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     * @throws OAuthException 
     * @throws AppException
     */
    public String createNotebook(String name, String group)
            throws IOException, AppException {
        lock.readLock().lock();
        try {
            String url = getBaseURL() + "notebook/create.json";
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put(Constants.NAME_PARAM, name);
            if (group != null) {
                parameters.put(Constants.GROUP_PARAM, group);
            }
            HttpResponse response = HttpUtils.doPostByUrlEncoded(url,
                    parameters, accessor);
            String content = HttpUtils.getResponseContent(
                    response.getEntity().getContent());
           
            JSONObject json = new JSONObject(content);
            return json.getString(Notebook.PATH);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 删除笔记本
     * http://note.youdao.com/yws/open/notebook/delete.json
     *
     * @param notebookPath
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     * @throws OAuthException 
     * @throws AppException
     */
    public void deletedNotebook(String notebookPath) throws IOException,
            AppException {
        lock.readLock().lock();
        try {
            String url = getBaseURL() + "notebook/delete.json";
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put(Constants.NOTEBOOK_PARAM, notebookPath);
            HttpResponse response = HttpUtils.doPostByUrlEncoded(url,
                    parameters, accessor);
           
            response.getEntity().consumeContent();
        } finally {
            lock.readLock().unlock();
        }
    }


    /**
     * 查看笔记
     * http://note.youdao.com/yws/open/note/get.json
     * @param notePath
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     * @throws OAuthException 
     * @throws AppException
     */
    public Note getNote(String notePath) throws IOException, AppException {
        lock.readLock().lock();
        try {
            String url = getBaseURL() + "note/get.json";
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put(Constants.PATH_PARAM, notePath);
            HttpResponse response = HttpUtils.doPostByUrlEncoded(url,
                    parameters, accessor);
            String content = HttpUtils.getResponseContent(
                    response.getEntity().getContent());
            Note note = new Note(content);
            note.setPath(notePath);
            return note;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 新建笔记
     * http://note.youdao.com/yws/open/note/create.json
     *
     * @param notebookPath
     * @param note
     * @return 笔记和笔记路径
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     * @throws OAuthException 
     * @throws AppException
     */
    public Note createNote(String notebookPath, Note note) throws IOException,
            AppException {
        lock.readLock().lock();
        try {
            String url = getBaseURL() + "note/create.json";
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put(Constants.TITLE_PARAM, note.getTitle());
            parameters.put(Constants.AUTHOR_PARAM, note.getAuthor());
            parameters.put(Constants.SOURCE_PARAM, note.getSource());
            parameters.put(Constants.CONTENT_PARAM, note.getContent());
            parameters.put(Constants.CREATE_TIME_PARAM, note.getCreateTime());
            if (!StringUtils.isBlank(notebookPath)) {
                parameters.put(Constants.NOTEBOOK_PARAM, notebookPath);
            }
            HttpResponse response = HttpUtils.doPostByMultipart(url,
                    parameters, accessor);
            String content = HttpUtils.getResponseContent(
                    response.getEntity().getContent());
            JSONObject json = new JSONObject(content);
            note.setPath(json.getString(Note.PATH));
            return note;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 更新指定笔记
     * http://note.youdao.com/yws/open/note/update.json
     *
     * @param note(新笔记)
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     * @throws OAuthException 
     * @throws AppException
     */
    public void updateNote(Note note) throws IOException, AppException {
        lock.readLock().lock();
        try {
            String url = getBaseURL() + "note/update.json";
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put(Constants.PATH_PARAM, note.getPath());
            parameters.put(Constants.TITLE_PARAM, note.getTitle());
            parameters.put(Constants.AUTHOR_PARAM, note.getAuthor());
            parameters.put(Constants.SOURCE_PARAM, note.getSource());
            parameters.put(Constants.CONTENT_PARAM, note.getContent());
            parameters.put(Constants.MODIFY_TIME_PARAM, note.getModifyTime());
            HttpResponse response = HttpUtils.doPostByMultipart(url,
                    parameters, accessor);
             response.getEntity().consumeContent();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 移动笔记
     * http://note.youdao.com/yws/open/note/move.json
     *
     * @param notePath（比较所在路径）
     * @param destNotebookPath（目标笔记本）
     * @return 笔记新路径
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     * @throws OAuthException 
     */
    public String moveNote(String notePath, String destNotebookPath)
            throws IOException, AppException {
        lock.readLock().lock();
        try {
            String url = getBaseURL() + "note/move.json";
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put(Constants.PATH_PARAM, notePath);
            parameters.put(Constants.NOTEBOOK_PARAM, destNotebookPath);
            HttpResponse response = HttpUtils.doPostByUrlEncoded(url,
                    parameters, accessor);
            String content = HttpUtils.getResponseContent(
                    response.getEntity().getContent());
            JSONObject json = new JSONObject(content);
            return json.getString(Note.PATH);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 删除笔记
     * http://note.youdao.com/yws/open/note/delete.json
     *
     * @param notePath（笔记路径）
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     * @throws OAuthException 
     */
    public void deleteNote(String notePath) throws IOException, AppException {
        lock.readLock().lock();
        try {
            String url = getBaseURL() + "note/delete.json";
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put(Constants.PATH_PARAM, notePath);
            HttpResponse response = HttpUtils.doPostByUrlEncoded(url,
                    parameters, accessor);
            response.getEntity().consumeContent();
        } finally {
            lock.readLock().unlock();
        }
    }


    /**
     * 上传资源
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     * @throws OAuthException 
     */
    public Resource uploadResource(File resource) throws IOException,
            AppException {
        lock.readLock().lock();
        try {
            String url = getBaseURL() + "resource/upload.json";
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put(Constants.FILE_PARAM, resource);
            HttpResponse response = HttpUtils.doPostByMultipart(url,
                    parameters, accessor);
            String content = HttpUtils.getResponseContent(
                    response.getEntity().getContent());
            JSONObject json = new JSONObject(content);
            if (json.has(Resource.SRC)) {
                // attachment
                return new Resource(json.getString(Resource.URL),
                        json.getString(Resource.SRC));
            } else {
                // image
                return new Resource(json.getString(Resource.URL));
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 下载资源
     * @param url resource url
     * @return resource body stream
     * @throws OAuthException
     * @throws java.io.IOException
     * @throws java.net.URISyntaxException
     */
    public InputStream downloadResource(String url) throws
            IOException, AppException {
        lock.readLock().lock();
        try {
            HttpResponse response = HttpUtils.doGet(url, null, accessor);
            return response.getEntity().getContent();
        } finally {
            lock.readLock().unlock();
        }
    }
}


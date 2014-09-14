package com.app.linyu.config;



import java.text.SimpleDateFormat;

/**
 * 用于ReadMeClient的各个常量
 *
 * @author zibin
 */
public class Constants {

    public static final String ENCODING = "UTF-8";

    /**
     * OAuth 认证  线上环境 url
     */
    public static final String REQUEST_TOKEN_URL = "http://note.youdao.com/oauth/request_token";
    public static final String USER_AUTHORIZATION_URL = "http://note.youdao.com/oauth/authorize";
    public static final String ACCESS_TOKEN_URL = "http://note.youdao.com/oauth/access_token";

    /**
     * OAuth认证 测试环境url
     */
    public static final String SANDBOX_REQUEST_TOKEN_URL = "http://sandbox.note.youdao.com/oauth/request_token";
    public static final String SANDBOX_USER_AUTHORIZATION_URL = "http://sandbox.note.youdao.com/oauth/authorize";
    public static final String SANDBOX_ACCESS_TOKEN_URL = "http://sandbox.note.youdao.com/oauth/access_token";

    /**
     * 软件有关信息
     */
    public static final String CONSUMER_KEY = "824cf448318658ea7e1738d90f55bbab";
    public static final String CONSUMER_SECRET = "087544ef5fb142fbb50bf5a790b92981";
    public static final String accessToken = "e2c9c4a1519b86913372bc9bb752f310";
    public static final String tokenSecret = "3a33324e2e360266b1dd82ef897091e7";
    public static final String notebookPath = "/4AA210FB0DBA41B2A997E893D9F32813";
    
    /**
     * 有关参数的值
     */
    public static final String NOTEBOOK_PARAM = "notebook";
    public static final String GROUP_PARAM = "group";
    public static final String NAME_PARAM = "name";
    public static final String PATH_PARAM = "path";
    public static final String SOURCE_PARAM = "source";
    public static final String AUTHOR_PARAM = "author";
    public static final String TITLE_PARAM = "title";
    public static final String CONTENT_PARAM = "content";
    public static final String FILE_PARAM = "file";
    public static final String CREATE_TIME_PARAM = "create_time";
    public static final String MODIFY_TIME_PARAM = "modify_time";

    public static final SimpleDateFormat DATE_FORMATTER =
        new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
}

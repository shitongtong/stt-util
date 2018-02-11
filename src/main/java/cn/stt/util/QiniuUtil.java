package cn.stt.util;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;


public class QiniuUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(QiniuUtil.class);

    private static final String DOMAIN = "DOMAIN";
    // 设置好账号的ACCESS_KEY和SECRET_KEY
    private static final String ACCESS_KEY = "ACCESS_KEY";
    private static final String SECRET_KEY = "SECRET_KEY";
    // 要上传的空间
    private static final String BUCKETNAME = "BUCKETNAME";
    //构造一个带指定Zone对象的配置类生成上传对象
    private static final UploadManager UPLOADMANAGER = new UploadManager(new Configuration(Zone.zone0()));
    //生成上传凭证
    private static final Auth AUTH = Auth.create(ACCESS_KEY, SECRET_KEY);

    /**
     * 数据流上传
     *
     * @param is
     * @param fileName 默认不指定fileName的情况下，以文件内容的hash值作为文件名
     * @return
     * @throws QiniuException
     */
    public static String upload(InputStream is, String fileName) throws QiniuException {
        try {
            return processResponse(UPLOADMANAGER.put(is, fileName, AUTH.uploadToken(BUCKETNAME), null, null));
        } catch (QiniuException ex) {
            processException(ex);
            throw ex;
        }
    }

    /**
     * 字节数组上传
     *
     * @param bytes
     * @param fileName 默认不指定fileName的情况下，以文件内容的hash值作为文件名
     * @return
     * @throws QiniuException
     */
    public static String upload(byte[] bytes, String fileName) throws QiniuException {
        try {
            return processResponse(UPLOADMANAGER.put(bytes, fileName, AUTH.uploadToken(BUCKETNAME)));
        } catch (QiniuException ex) {
            processException(ex);
            throw ex;
        }
    }

    /**
     * 文件上传
     *
     * @param file
     * @param fileName 默认不指定fileName的情况下，以文件内容的hash值作为文件名
     * @return
     * @throws QiniuException
     */
    public static String upload(File file, String fileName) throws QiniuException {
        try {
            return processResponse(UPLOADMANAGER.put(file, fileName, AUTH.uploadToken(BUCKETNAME)));
        } catch (QiniuException ex) {
            processException(ex);
            throw ex;
        }
    }

    /**
     * 处理上传异常
     *
     * @param ex
     */
    private static void processException(QiniuException ex) {
        com.qiniu.http.Response r = ex.response;
        LOGGER.info("文件上传异常:r={}", r);
        try {
            LOGGER.info("异常信息:body={}", r.bodyString());
        } catch (QiniuException ex2) {
//                ignore
        }
    }

    /**
     * 处理上传返回值，返回上传文件的url
     *
     * @param response
     * @return
     * @throws QiniuException
     */
    private static String processResponse(com.qiniu.http.Response response) throws QiniuException {
        LOGGER.info("response={}", response);
        //解析上传成功的结果
        DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        LOGGER.info("文件上传成功:key={},hash={}", putRet.key, putRet.hash);
        String url = DOMAIN + putRet.key;
        LOGGER.info("文件上传成功:链接url={}", url);
        return url;
    }

    /*public static void main(String[] args) throws Exception {
        //文件上传
        String filePath = "d:/ddf07d57825d4004bee6d37d73af669b10.db";
        File file = new File(filePath);
        String url = upload(file, file.getName());
        //字节数组上传
//        byte[] uploadBytes = "hello qiniu cloud".getBytes("utf-8");
//        String url = upload(uploadBytes, null);
        //数据流上传
//        InputStream is = new FileInputStream(file);
//        String url = upload(is, file.getName());

        LOGGER.info("文件url={}", url);
    }*/

}

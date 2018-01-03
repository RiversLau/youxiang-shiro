package com.youxiang.shiro.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Author: RiversLau
 * Date: 2018/1/3 11:09
 */
public class ResourceUtils {

    public static final String CLASSPATH_PREFIX = "classpath:";

    public static final String URL_PREFIX = "url:";

    public static final String FILE_PREFIX = "file:";

    private static final Logger log = LoggerFactory.getLogger(ResourceUtils.class);

    /**
     * 私有构造方法，无法new
     */
    private ResourceUtils() {
    }

    /**
     * 判断资源文件开头是否以"classpath:"、"url:"、"file:"开头
     *
     * @param resourcePath
     * @return
     */
    public static boolean hasResourcePrefix(String resourcePath) {
        return resourcePath != null && (
                resourcePath.startsWith(CLASSPATH_PREFIX) ||
                        resourcePath.startsWith(URL_PREFIX) ||
                        resourcePath.startsWith(FILE_PREFIX));
    }

    /**
     * 判断资源文件是否存在
     * @param resourcePath
     * @return
     */
    public static boolean resourceExists(String resourcePath) {

        InputStream stream = null;
        boolean exists = false;

        try {
            stream = getInputStreamForPath(resourcePath);
            exists = true;
        } catch (IOException e) {
            stream = null;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }
        return exists;
    }

    /**
     * 返回指定路径下资源文件对应的InputStream
     * @param resourcePath
     * @return
     * @throws IOException
     */
    public static InputStream getInputStreamForPath(String resourcePath) throws IOException {

        InputStream is;
        if (resourcePath.startsWith(CLASSPATH_PREFIX)) {
            is = loadFromClassPath(stripPrefix(resourcePath));
        } else if (resourcePath.startsWith(URL_PREFIX)) {
            is = loadFromUrl(stripPrefix(resourcePath));
        } else if (resourcePath.startsWith(FILE_PREFIX)) {
            is = loadFromFile(stripPrefix(resourcePath));
        } else {
            is = loadFromFile(resourcePath);
        }

        if (is == null) {
            throw new IOException("Resource [" + resourcePath + "] could not be found.");
        }
        return is;
    }

    /**
     * 关闭指定的InputStream，并记录可能出现的IOException
     * @param is
     */
    public static void close(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                log.warn("Error closing input stream." + e);
            }
        }
    }

    private static InputStream loadFromFile(String path) throws IOException {
        log.debug("Opening file {}", path);
        return new FileInputStream(path);
    }

    private static InputStream loadFromUrl(String urlPath) throws IOException {
        log.debug("Opening url {}", urlPath);
        URL url = new URL(urlPath);
        return url.openStream();
    }

    private static InputStream loadFromClassPath(String path) {
        log.debug("Opening resource from class path {}", path);
        return ClassUtils.getResourceAsStream(path);
    }

    /**
     * 去除"classpath:"、"url:"、"file:"前缀
     * @param resourcePath
     * @return
     */
    private static String stripPrefix(String resourcePath) {
        return resourcePath.substring(resourcePath.indexOf(":") + 1);
    }
}

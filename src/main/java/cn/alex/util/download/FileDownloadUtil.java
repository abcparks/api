package cn.alex.util.download;

import org.apache.tika.Tika;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Created by WCY on 2020/4/13
 */
public class FileDownloadUtil {

    /**
     * 下载文件
     * @param bos      ByteArrayOutputStream
     * @param filename 文件名
     * @param suffix   文件后缀
     */
    public static void download(ByteArrayOutputStream bos, String filename, String suffix) {
        // 设置内容大小
        HttpServletResponse response = getHttpServletResponse(filename, suffix);
        response.setContentLength(bos.size());
        try (OutputStream os = response.getOutputStream()) {
            bos.writeTo(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载文件
     * @param bos          ByteArrayOutputStream
     * @param fullFilename 文件名(包含后缀)
     */
    public static void download(ByteArrayOutputStream bos, String fullFilename) {
        String suffix = fullFilename.substring(fullFilename.lastIndexOf(".") + 1);
        download(bos, fullFilename, suffix);
    }

    /**
     * 获取编码后文件名
     * @param fullFilename 文件名(包含后缀)
     * @return 编码文件名
     */
    public static String getEncodeFilename(String fullFilename) {
        // 获取HttpServletRequest
        HttpServletRequest request = getHttpServletRequest();
        String encodeFileName = null;
        // Http Header信息, 提供浏览器类型和版本信息
        String agent = request.getHeader("USER-AGENT").toUpperCase();
        try {
            // IE浏览器
            if (agent.contains("MSIE") || agent.contains("TRIDENT") || agent.contains("EDGE")) {
                encodeFileName = URLEncoder.encode(fullFilename, "UTF-8");
            } else { // 其他浏览器
                encodeFileName = new String(fullFilename.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeFileName;
    }

    /**
     * 获取编码后文件名
     * @param filename 文件名
     * @param suffix   文件后缀名
     * @return 编码文件名
     */
    public static String getEncodeFilename(String filename, String suffix) {
        return getEncodeFilename(filename + "." + suffix);
    }

    /**
     * 全局获取HttpServletRequest
     * @return HttpServletRequest
     */
    public static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) Objects.
                requireNonNull(RequestContextHolder.getRequestAttributes())).
                getRequest();
    }

    /**
     * 全局获取HttpServletResponse
     * @return HttpServletResponse
     */
    public static HttpServletResponse getHttpServletResponse() {
        return ((ServletRequestAttributes) Objects.
                requireNonNull(RequestContextHolder.getRequestAttributes())).
                getResponse();
    }

    /**
     * 获取HttpServletResponse
     * @return HttpServletResponse
     */
    private static HttpServletResponse getHttpServletResponse(String filename, String suffix) {
        // 获取HttpServletResponse
        HttpServletResponse response = getHttpServletResponse();
        // 清除首部的空白行(jsp生成html文件的时候, html文件内部出现空白行)
        response.reset();
        // 设置响应类型
        response.setContentType(new Tika().detect(suffix));
        response.setHeader("Content-Disposition", "attachment;filename=" + getEncodeFilename(filename, suffix));
        return response;
    }

}

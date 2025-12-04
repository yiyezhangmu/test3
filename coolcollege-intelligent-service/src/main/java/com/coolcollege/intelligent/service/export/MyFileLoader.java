package com.coolcollege.intelligent.service.export;

import cn.afterturn.easypoi.cache.manager.FileLoaderImpl;
import cn.afterturn.easypoi.cache.manager.IFileLoader;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ExportImageDomainReplaceCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author byd
 * @date 2021-10-08 11:45
 */
@Slf4j
@Component
public class MyFileLoader implements IFileLoader {

    public byte[] getFile(String url) {
        log.info("getFile#ulr:{}", url);
        InputStream fileis = null;
        ByteArrayOutputStream baos = null;

        try {
            if (url.startsWith("http")) {
                url = ExportImageDomainReplaceCodeEnum.resetUrl(url);
                if (!url.contains("x-oss-process=image") && !url.contains("?")) {
                    url = url + "?x-oss-process=image/resize,h_100,m_lfit";
                }
                URL urlObj = new URL(url);
                URLConnection urlConnection = urlObj.openConnection();
                urlConnection.setConnectTimeout(30000);
                urlConnection.setReadTimeout(60000);
                urlConnection.setDoInput(true);
                fileis = urlConnection.getInputStream();
            } else {
                try {
                    fileis = new FileInputStream(url);
                } catch (FileNotFoundException var11) {
                    fileis = FileLoaderImpl.class.getClassLoader().getResourceAsStream(url);
                }
            }

            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];

            int len;
            while((len = ((InputStream)fileis).read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }

            baos.flush();
            byte[] var6 = baos.toByteArray();
            return var6;
        } catch (Exception var12) {
            log.error(var12.getMessage(), var12);
        } finally {
            IOUtils.closeQuietly((Closeable)fileis);
            IOUtils.closeQuietly(baos);
        }

        log.error(fileis + "这个路径文件没有找到,请查询,url:{}", url);
        return null;
    }
}

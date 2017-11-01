package com.youdaigc.report.itext.provider;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.codec.Base64;
import com.itextpdf.tool.xml.pipeline.html.AbstractImageProvider;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by GaoWeiFeng on 2017-08-24.
 */
public class MyImageProvider extends AbstractImageProvider implements Serializable{
    private static final long serialVersionUID = -9007781829898391648L;
    private static final Logger logger = LoggerFactory.getLogger(MyImageProvider.class);
    public static final String IMG_BASE64_PREFIX = "base64,";

    @Override
    public Image retrieve(String src) {
        int pos = src.indexOf(IMG_BASE64_PREFIX);

        Image image = null;
        try {
            if (src.contains("data:") && pos > 0) {
                image = Image.getInstance(
                        Base64.decode(
                                src.substring(pos + IMG_BASE64_PREFIX.length())
                        )
                );
            } else {
                image = Image.getInstance(
                        FileUtils.readFileToByteArray(new File(src.replace("'", "").replace("\"", "")))
                );
            }
        } catch (BadElementException ex) {
            logger.error(ex.getMessage(), ex);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }

        return image;
    }

    /**
     * @return a rootpath to set before the src attribute
     */
    @Override
    public String getImageRootPath() {
        return null;
    }
}

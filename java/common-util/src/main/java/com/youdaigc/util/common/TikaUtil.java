package com.youdaigc.util.common;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeTypes;
import org.xml.sax.SAXException;

import java.io.*;

/**
 * Created by GaoWei on 2017-07-14.
 */
public class TikaUtil implements Serializable {

    private static final long serialVersionUID = -8483225096034422772L;

    /**
     * @param file
     * @return
     * @throws IOException
     * @throws SAXException
     * @throws TikaException
     */
    public static String parseFileMimeTypeString(File file) throws IOException, SAXException, TikaException {
        InputStream stream = null;
        try {
            // Note: Must wrap with Buffered InputStream, due to tika will invoke "reset" method on Stream
            if((stream = new BufferedInputStream(new FileInputStream(file))) != null) {
                Metadata metadata = new Metadata();
                MimeTypes mimeTypes = TikaConfig.getDefaultConfig().getMimeRepository();
                return mimeTypes.detect(stream, metadata).toString();
            }else {
                return null;
            }
        }finally {
            if(stream != null) {
                try {
                    stream.close();
                }catch (Exception ex){}
            }
        }
    }


    /**
     * @param fileName
     * @return
     * @throws IOException
     * @throws SAXException
     * @throws TikaException
     */
    public static String parseFileMimeTypeString(String fileName) throws IOException, SAXException, TikaException {
        InputStream stream = null;
        try {
            // Try to Load as Resource first
            if ((stream = TikaUtil.class.getResourceAsStream(fileName)) == null) {
                // If Cannot retrieve File Input Stream as Resource, then try to use as File Path
                // Note: Must wrap with Buffered InputStream, due to tika will invoke "reset" method on Stream
                stream = new BufferedInputStream(new FileInputStream(new File(fileName)));
            }

            if(stream != null) {
                Metadata metadata = new Metadata();
                MimeTypes mimeTypes = TikaConfig.getDefaultConfig().getMimeRepository();
                return mimeTypes.detect(stream, metadata).toString();
            }else {
                return null;
            }
        }finally {
            if(stream != null) {
                try {
                    stream.close();
                }catch (Exception ex){}
            }
        }
    }


}

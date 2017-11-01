package com.youdaigc.report.itext.provider;

import com.itextpdf.text.Font;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Created by GaoWeiFeng on 2017-08-24.
 */
public class MyFontsProvider extends XMLWorkerFontProvider implements Serializable {
    private static final long serialVersionUID = -1688660228808883126L;
    private static final Logger logger = LoggerFactory.getLogger(MyFontsProvider.class);



    public MyFontsProvider(){
        super(null, null);
    }

    @Override
    public Font getFont(final String fontname, String encoding, float size, final int style) {
        String fntname = fontname;
//        if (fntname == null) {
            fntname = "C:/WINDOWS/Fonts/simkai.ttf";
//        }
        if (size == 0) {
            size = 4;
        }
        return super.getFont(fntname, encoding, size, style);
    }
}

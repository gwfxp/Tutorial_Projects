package com.youdaigc.report.itext.event;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.*;

import java.io.Serializable;

/**
 * Created by GaoWeiFeng on 2017-08-24.
 */
public class MyHeaderFooterEventHandler extends PdfPageEventHelper implements Serializable {
    private static final long serialVersionUID = -1286895952395424519L;
    Font ffont = new Font(Font.FontFamily.UNDEFINED, 5, Font.ITALIC);

    protected PdfPTable table;
    protected float tableHeight;
    public MyHeaderFooterEventHandler() {
        table = new PdfPTable(1);
        table.setTotalWidth(523);
        table.setLockedWidth(true);
        table.addCell("Header row 1");
        table.addCell("Header row 2");
        table.addCell("Header row 3");
        tableHeight = table.getTotalHeight();
    }

    public float getTableHeight() {
        return tableHeight;
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();
        Phrase header = new Phrase(String.format("Header on Page:%s", document.getPageNumber()), ffont);
        Phrase footer = new Phrase(String.format("Footer on Page:%s", document.getPageNumber()), ffont);

        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                header,
                (document.right() - document.left()) / 2 + document.leftMargin(),
                document.top() + 10, 0);
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                footer,
                (document.right() - document.left()) / 2 + document.leftMargin(),
                document.bottom() - 10, 0);

//        table.writeSelectedRows(0, -1,
//                document.left(),
//                document.top() + ((document.topMargin() + tableHeight) / 2),
//                writer.getDirectContent());
    }
}

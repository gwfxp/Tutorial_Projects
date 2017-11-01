package com.youdaigc.report.generator;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hwpf.converter.HtmlDocumentFacade;
import org.apache.poi.hwpf.converter.WordToHtmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by GaoWeiFeng on 2017-08-29.
 */
public class ThymeleafDocumentFacade extends HtmlDocumentFacade{
    public ThymeleafDocumentFacade(Document document) {
        super(document);
        html.setAttribute("xmlns:th", "http://www.thymeleaf.org");
    }

    @Override
    public Element createTableRow()
    {
        return document.createElement( "tr" );
    }

    @Override
    public Element createTableCell()
    {
        return document.createElement( "td" );
    }
}

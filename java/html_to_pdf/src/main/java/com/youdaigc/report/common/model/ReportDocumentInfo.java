package com.youdaigc.report.common.model;

import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.youdaigc.report.common.ReportDocFileType;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by GaoWeiFeng on 2017-08-24.
 * Information model used on Report Document generation
 *
 */
public class ReportDocumentInfo implements Serializable {
    private static final long serialVersionUID = 8495196376289559596L;

    /**
     * Report Document File Type
     */
    protected ReportDocFileType reportDocFileType = ReportDocFileType.PDF;

    /**
     * Document Page Size
     */
    protected Rectangle pageSize = PageSize.A4;

    /**
     * Document Page Left Margin
     */
    protected float marginLeft = 36;

    /**
     * Document Page Right Margin
     */
    protected float marginRight = 36;

    /**
     * Document Page Top Margin
     */
    protected float marginTop = 36;

    /**
     * Document Page Bottom Margin
     */
    protected float marginBottom = 36;

    /**
     * Data Source
     */
    protected byte[] source;

    /**
     * Output File Name
     */
    protected String outputFileName;

    /**
     * Document Creation Date
     */
    protected Date docCreationDate = new Date(System.currentTimeMillis());

    /**
     * Document Author Name
     */
    protected String docAuthor;

    /**
     * Document Charset;
     */
    protected Charset charset = Charset.forName("UTF-8");

    public ReportDocumentInfo setPageSize(Rectangle pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public ReportDocumentInfo setMarginLeft(float marginLeft) {
        this.marginLeft = marginLeft;
        return this;
    }

    public ReportDocumentInfo setMarginRight(float marginRight) {
        this.marginRight = marginRight;
        return this;
    }

    public ReportDocumentInfo setMarginTop(float marginTop) {
        this.marginTop = marginTop;
        return this;
    }

    public ReportDocumentInfo setMarginBottom(float marginBottom) {
        this.marginBottom = marginBottom;
        return this;
    }

    public ReportDocumentInfo setSource(byte[] source) {
        this.source = source;
        return this;
    }

    public ReportDocumentInfo setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
        return this;
    }

    public ReportDocumentInfo setDocCreationDate(Date docCreationDate) {
        this.docCreationDate = docCreationDate;
        return this;
    }

    public ReportDocumentInfo setDocAuthor(String docAuthor) {
        this.docAuthor = docAuthor;
        return this;
    }

    public ReportDocumentInfo setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ReportDocumentInfo{");
        sb.append("reportDocFileType=").append(reportDocFileType);
        sb.append(", pageSize=").append(pageSize);
        sb.append(", marginLeft=").append(marginLeft);
        sb.append(", marginRight=").append(marginRight);
        sb.append(", marginTop=").append(marginTop);
        sb.append(", marginBottom=").append(marginBottom);
        sb.append(", source=").append(Arrays.toString(source));
        sb.append(", outputFileName='").append(outputFileName).append('\'');
        sb.append(", docCreationDate=").append(docCreationDate);
        sb.append(", docAuthor='").append(docAuthor).append('\'');
        sb.append(", charset=").append(charset);
        sb.append('}');
        return sb.toString();
    }

    public ReportDocFileType getReportDocFileType() {
        return reportDocFileType;
    }

    public void setReportDocFileType(ReportDocFileType reportDocFileType) {
        this.reportDocFileType = reportDocFileType;
    }

    public Rectangle getPageSize() {
        return pageSize;
    }

    public float getMarginLeft() {
        return marginLeft;
    }

    public float getMarginRight() {
        return marginRight;
    }

    public float getMarginTop() {
        return marginTop;
    }

    public float getMarginBottom() {
        return marginBottom;
    }

    public byte[] getSource() {
        return source;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public Date getDocCreationDate() {
        return docCreationDate;
    }

    public String getDocAuthor() {
        return docAuthor;
    }

    public Charset getCharset() {
        return charset;
    }
}

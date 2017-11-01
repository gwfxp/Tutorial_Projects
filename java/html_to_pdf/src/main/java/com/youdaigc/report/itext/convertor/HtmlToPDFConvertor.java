package com.youdaigc.report.itext.convertor;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.Pipeline;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFilesImpl;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.DefaultTagProcessorFactory;
import com.itextpdf.tool.xml.html.TagProcessorFactory;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import com.youdaigc.report.common.ReportDocFileType;
import com.youdaigc.report.common.model.ReportDocumentInfo;
import com.youdaigc.report.itext.event.MyHeaderFooterEventHandler;
import com.youdaigc.report.itext.provider.MyFontsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;


/**
 * Created by GaoWeiFeng on 2017-08-24.
 */
public class HtmlToPDFConvertor implements Serializable {
    private static final long serialVersionUID = 8137475597391103197L;
    private static final Logger logger = LoggerFactory.getLogger(HtmlToPDFConvertor.class);

    protected MyFontsProvider fontImp = new MyFontsProvider();

    /**
     * Generate Report Document
     *
     * @param reportDocumentInfo
     * @throws IOException
     * @throws DocumentException
     */
    public void generateReportDocument(ReportDocumentInfo reportDocumentInfo) throws IOException, DocumentException {
        if(!ReportDocFileType.PDF.equals(reportDocumentInfo.getReportDocFileType())) {
            return;
        }

        // step 1
        Document document = new Document(reportDocumentInfo.getPageSize(),
//                            0, 0, 0, 0
                        reportDocumentInfo.getMarginLeft(),
                        reportDocumentInfo.getMarginRight(),
                        reportDocumentInfo.getMarginTop(),
                        reportDocumentInfo.getMarginBottom()
            );

        // step 2
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(reportDocumentInfo.getOutputFileName()));
        writer.setPageEvent(new MyHeaderFooterEventHandler());
        document.open();

        TagProcessorFactory tagProcessorFactory = (DefaultTagProcessorFactory) Tags.getHtmlTagProcessorFactory();
//        tagProcessorFactory.addProcessor(new InputTagProcessor(), "input");

//        fontImp.setUseUnicode(true);
//        fontImp.register(fontName);
        try {
            XMLWorkerHelper helper = XMLWorkerHelper.getInstance();
//            XMLWorkerHelper.getInstance().parseXHtml(writer, document,
//                    new ByteArrayInputStream(reportDocumentInfo.getSource()),
//                    reportDocumentInfo.getCharset(),
//                    fontImp
//            );


            CssFilesImpl cssFiles = new CssFilesImpl();
            InputStream inCssFile = XMLWorkerHelper.class.getResourceAsStream("/default.css");
            if (inCssFile != null) {
                cssFiles.add(XMLWorkerHelper.getCSS(inCssFile));
            } else {
                cssFiles.add(helper.getDefaultCSS());
            }
            StyleAttrCSSResolver cssResolver = new StyleAttrCSSResolver(cssFiles);
            HtmlPipelineContext hpc = new HtmlPipelineContext(new CssAppliersImpl(fontImp));
            hpc.setAcceptUnknown(true).autoBookmark(true).setTagFactory(tagProcessorFactory).setResourcesRootPath(null);
            HtmlPipeline htmlPipeline = new HtmlPipeline(hpc, new PdfWriterPipeline(document, writer));
            Pipeline<?> pipeline = new CssResolverPipeline(cssResolver, htmlPipeline);
            XMLWorker worker = new XMLWorker(pipeline, true);
            XMLParser p = new XMLParser(true, worker, reportDocumentInfo.getCharset());
            if (reportDocumentInfo.getCharset() != null) {
                p.parse(new ByteArrayInputStream(reportDocumentInfo.getSource()), reportDocumentInfo.getCharset());
            } else {
                p.parse(new ByteArrayInputStream(reportDocumentInfo.getSource()));
            }

        }finally {
            document.close();
        }
    }

    public void generateWordReportDocument(ReportDocumentInfo reportDocumentInfo) throws IOException, DocumentException {

    }

    public void generateExcelReportDocument(ReportDocumentInfo reportDocumentInfo) throws IOException, DocumentException {

    }

    public void generateImageReportDocument(ReportDocumentInfo reportDocumentInfo) throws IOException, DocumentException {

    }
}

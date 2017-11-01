package com.youdaigc;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.youdaigc.model.SZP17I2Model;
import com.youdaigc.model.SZP17I2RSModel;
import com.youdaigc.report.common.model.ReportDocumentInfo;
import com.youdaigc.report.generator.MyThymeleafPageGenerator;
import com.youdaigc.report.itext.convertor.ExcelToHtmlTemplateConvertor;
import com.youdaigc.report.itext.convertor.HtmlToPDFConvertor;
import com.youdaigc.util.common.MyFileUtils;
import com.youdaigc.util.common.MyStringUtils;
import org.apache.commons.io.FileUtils;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.*;

/**
 * Unit test for Excel To Thymleaf Template File
 */
public class ExcelToThymleafTest{

    private static final String Output_File_Path = System.getProperty("user.dir") + "/Test_Output/html_to_pdf/";
    private static final String Excel_Sample_File = "Sample_1.xls";

    /**
     * Load Template File From Class Path Resource Folder
     * @param fileName
     * @return
     */
    protected File loadFile(String fileName){
        try {
            String classPath = getClass().getClassLoader().getResource(".").toURI().getPath();
            return new File(classPath + fileName);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void convertExcelToHtml() throws ParserConfigurationException, TransformerException, IOException {
        StringBuilder builder = ExcelToHtmlTemplateConvertor.convertExcelToHTML(
                loadFile(Excel_Sample_File),
                Arrays.asList("Sheet2"),
                PageSize.A4, 36, 36);

        FileUtils.write(Paths.get(Output_File_Path, Excel_Sample_File + ".html").toFile(),
//                Jsoup.clean(builder.toString(), Whitelist.basic()),
                builder.toString(),
                "UTF-8"
        );

    }

    public void convertHtmlToPDF() throws IOException, DocumentException {
        MyThymeleafPageGenerator pageGenerator = new MyThymeleafPageGenerator(Output_File_Path, null,
                MyThymeleafPageGenerator.TemplateResolverType.FileTemplateResolver);

        SZP17I2Model szp17I2Model = new SZP17I2Model();
        szp17I2Model.setIproCd("02");
        szp17I2Model.setOagrAmt(10000.00);
        szp17I2Model.setIintRt(15.6);

        List<SZP17I2RSModel> ors = new ArrayList<>();
        SZP17I2RSModel rsModel;
        for(int i=1; i<=36; i++){
            rsModel = new SZP17I2RSModel();
            rsModel.setRpcnt(i);
            rsModel.setRpdueDt(MyStringUtils.getDate(2017, i%11+1, (i%27)+1));
            rsModel.setRpstnDt(MyStringUtils.getDate(2017, (i%11)+1, (i%27)+1));
            rsModel.setRpamt(1000.0*i);
            rsModel.setUseIntDay(i+10);
            rsModel.setUseIntAmt(i*12.0);
            rsModel.setLnbal(30000-rsModel.getRpamt());
            rsModel.setPrpmtAmt(3600 - i*100.0);
            rsModel.setUseAccFee1(i*12.0);
            ors.add(rsModel);
        }

        szp17I2Model.setOrs(ors);


        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("SZp17i2", szp17I2Model);
        paramMap.put("prods", szp17I2Model);

        String str = pageGenerator.generatorPageString(Excel_Sample_File, paramMap);


        MyFileUtils.write(Paths.get(Output_File_Path, Excel_Sample_File + "_gen.html").toString(), str);

        ReportDocumentInfo documentInfo = new ReportDocumentInfo().setPageSize(PageSize.A4).setOutputFileName(
                Paths.get(Output_File_Path, Excel_Sample_File + ".pdf").toString()).setSource(str.getBytes());

        HtmlToPDFConvertor convertor = new HtmlToPDFConvertor();
        convertor.generateReportDocument(documentInfo);
    }

    /**
     * Run this class as a program
     *
     * @param args The command line arguments.
     *
     * @throws Exception Exception we don't recover from.
     */
    public static void main(String[] args) throws Exception {
        ExcelToThymleafTest test = new ExcelToThymleafTest();
        test.convertExcelToHtml();

        test.convertHtmlToPDF();
    }
}

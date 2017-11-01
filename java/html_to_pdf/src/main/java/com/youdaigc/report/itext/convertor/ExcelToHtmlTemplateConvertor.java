package com.youdaigc.report.itext.convertor;

import com.itextpdf.text.Rectangle;
import com.youdaigc.report.common.model.CellVarFuncInfo;
import com.youdaigc.report.common.model.CellVarFuncInfoGroup;
import com.youdaigc.report.generator.ThymeleafDocumentFacade;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.converter.ExcelToFoUtils;
import org.apache.poi.hssf.converter.ExcelToHtmlUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hwpf.converter.DefaultFontReplacer;
import org.apache.poi.hwpf.converter.FontReplacer;
import org.apache.poi.hwpf.converter.NumberFormatter;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Reference From "ExcelToHtmlConverter"
 * Created by A0003 on 2017-08-28.
 */
public class ExcelToHtmlTemplateConvertor implements Serializable {
    private static final long serialVersionUID = 8058839297125420963L;
    transient static final Logger logger = LoggerFactory.getLogger(ExcelToHtmlTemplateConvertor.class);

    protected static final String EMPTY = "";

    protected final HSSFDataFormatter _formatter = new HSSFDataFormatter();

    protected FontReplacer fontReplacer = new DefaultFontReplacer();

    protected boolean outputColumnHeaders = false;

    protected boolean outputHiddenColumns = false;

    protected boolean outputHiddenRows = false;

    protected boolean outputLeadingSpacesAsNonBreaking = true;

    protected boolean outputRowNumbers = false;

    protected String cssClassContainerCell = null;

    protected String cssClassContainerDiv = null;

    protected String cssClassPrefixCell = "c";

    protected String cssClassPrefixDiv = "d";

    protected String cssClassPrefixRow = "r";

    protected String cssClassPrefixTable = "t";

    protected Map<Short, String> excelStyleToClass = new LinkedHashMap<Short, String>();

    protected final ThymeleafDocumentFacade htmlDocumentFacade;

    protected boolean useDivsToSpan = false;

    /**
     * 是否输出Page Header
     */
    protected boolean outputSheetPageHeader = false;

    /**
     * Page Header 的内容
     */
    protected String sheetPageHeader;

    final static public String PARENT_ATTRIBUTE_FLAG = "&&{";
    final static public String ATTRIBUTE_FLAG = "&{";
    final static public String COMMENTS_FLAG = "#{";
    final static public String FUNCTIONS_FLAG = "%{";

    final static public String Thymeleaf_PREFIX = "th:";

    final static public Set<String> Thymeleaf_VARIABLE_LABLES = new HashSet<>(Arrays.asList(
       "text"
    ));

    final static public Set<String> Thymeleaf_VARIABLE_PREFIX = new HashSet<>(Arrays.asList(
            "${", "@{", "#{", "*{"
    ));

    public ExcelToHtmlTemplateConvertor(Document doc ){
        this(new ThymeleafDocumentFacade( doc ));
    }

    public ExcelToHtmlTemplateConvertor(ThymeleafDocumentFacade htmlDocumentFacade ){
        this.htmlDocumentFacade = htmlDocumentFacade;
    }

    public ExcelToHtmlTemplateConvertor() throws ParserConfigurationException {
        this(XMLHelper.getDocumentBuilderFactory().newDocumentBuilder().newDocument());
    }

    public String buildStyle(Workbook workbook, CellStyle cellStyle) {
        StringBuilder style = new StringBuilder();

        style.append("white-space:pre-wrap;");
        ExcelToHtmlUtils.appendAlign(style, cellStyle.getAlignmentEnum().getCode());

        switch (cellStyle.getFillPatternEnum()) {
            // no fill
            case NO_FILL:
                break;
            case SOLID_FOREGROUND:
                final Color foregroundColor = cellStyle.getFillForegroundColorColor();
                if (foregroundColor == null) break;
                style.append("background-color:" + convertCellColorToRGB(foregroundColor) + ";");
                break;
            case FINE_DOTS:
            case ALT_BARS:
            case SPARSE_DOTS:
            case THICK_HORZ_BANDS:
            case THICK_VERT_BANDS:
            case THICK_BACKWARD_DIAG:
            case THICK_FORWARD_DIAG:
            case BIG_SPOTS:
            case BRICKS:
            case THIN_HORZ_BANDS:
            case THIN_VERT_BANDS:
            case THIN_BACKWARD_DIAG:
            case THIN_FORWARD_DIAG:
            case SQUARES:
            case DIAMONDS:
            case LESS_DOTS:
            case LEAST_DOTS:
            default:
                final Color backgroundColor = cellStyle.getFillBackgroundColorColor();
                if (backgroundColor == null) break;
                style.append("background-color:" + convertCellColorToRGB(backgroundColor) + ";");
                break;
        }

        buildStyle_border(workbook, style, "top",    cellStyle.getBorderTopEnum(),    cellStyle.getTopBorderColor());
        buildStyle_border(workbook, style, "right",  cellStyle.getBorderRightEnum(),  cellStyle.getRightBorderColor());
        buildStyle_border(workbook, style, "bottom", cellStyle.getBorderBottomEnum(), cellStyle.getBottomBorderColor());
        buildStyle_border(workbook, style, "left",   cellStyle.getBorderLeftEnum(),   cellStyle.getLeftBorderColor());
//
        Font font = workbook.getFontAt(cellStyle.getFontIndex());

        buildStyle_font(workbook, style, font);

        return style.toString();
    }

    /**
     * 将Cell的颜色设置转为HTML的RGB颜色
     *
     * @param cellColor
     * @return
     */
    public String convertCellColorToRGB(Color cellColor, boolean withEnding){
        String result = null;
        if(cellColor instanceof HSSFColor){
            // XLS Format Excel File (97-2007)
//            HSSFPalette colors = ((HSSFWorkbook)wb).getCustomPalette();
//            HSSFColor color = colors.getColor(colorIndex);
            HSSFColor color = (HSSFColor)cellColor;
            if (color.getIndex() == HSSFColor.HSSFColorPredefined.AUTOMATIC.getColor().getIndex() || color == null) {
//                result = ""+color.getIndex();
            } else {
                short[] rgb = color.getTriplet();
//                result = String.format("#%02x%02x%02x; /* index = %d */", rgb[0], rgb[1], rgb[2], color.getIndex());
                result = String.format("#%02x%02x%02x", rgb[0], rgb[1], rgb[2], color.getIndex());
//                result = String.format("rgba(255, %d, %d, %d);", rgb[0], rgb[1], rgb[2]);
            }
        }else if(cellColor instanceof XSSFColor){
            // New XLSX Format Excel File (2010 +)
            XSSFColor color = (XSSFColor)cellColor;
            if (color == null || color.isAuto()) {
                result = null;
            }else if(color.isRGB()){
                byte[] argb = color.getARGB();
                if(argb != null) {
                    result = String.format("rgba(0x%02x, 0x%02x, 0x%02x, 0x%02x)", argb[3], argb[0], argb[1], argb[2]);
                }else {
                    byte[] rgb = color.getRGB();
                    result = String.format("#%02x%02x%02x", rgb[0], rgb[1], rgb[2]);
                }
            }
        }

        if(withEnding) return result + ";";
        else return result;
    }

    public String convertCellColorToRGB(Color cellColor){
        return convertCellColorToRGB(cellColor, true);
    }

    /**
     * 创建边框的样式
     *
     * @param workbook
     * @param style
     * @param type
     * @param xlsBorder
     * @param borderColor
     */
    protected void buildStyle_border(Workbook workbook, StringBuilder style,
                                   String type, BorderStyle xlsBorder, short borderColor) {
        if (xlsBorder == BorderStyle.NONE) {
            return;
        }

        StringBuilder borderStyle = new StringBuilder();
         borderStyle.append(getBorderWidth(xlsBorder));
        borderStyle.append(' ');
        borderStyle.append(ExcelToHtmlUtils.getBorderStyle(xlsBorder));

        Color color = getPaletteColor(workbook, borderColor);
        if (color != null) {
            borderStyle.append(' ');
//            borderStyle.append(ExcelToHtmlUtils.getColor(color));
            String colorString = convertCellColorToRGB(color, false);
            if(colorString != null){
                borderStyle.append(colorString);
//                style.append("border-" + type + "-color:" + colorString + "; ");
            }
        }

        style.append("border-" + type + ":" + borderStyle + ";");
    }

    /**
     * 从WorkBook中获取指定调色板的颜色
     *
     * @param workbook
     * @param colorIndex
     * @return
     */
    public Color getPaletteColor(Workbook workbook, short colorIndex){
        Color color = null;
        if(workbook instanceof HSSFWorkbook){
            color = ((HSSFWorkbook)workbook).getCustomPalette().getColor(colorIndex);
        }else if(workbook instanceof XSSFWorkbook){
            color = ((XSSFWorkbook)workbook).getStylesSource().getTheme().getThemeColor(colorIndex);
        }else{
            logger.error("Not Support Workbook Instance Type: " + workbook.getClass().getName());
        }

        return color;
    }

    /**
     * 创建Cell的Font设置信息
     *
     * @param workbook
     * @param style
     * @param font
     */
    protected void buildStyle_font(Workbook workbook, StringBuilder style, Font font) {
        StringBuilder fontStyle = new StringBuilder();
        if(font.getBold()) style.append("font-weight: bold; ");
        if(font.getItalic()) fontStyle.append("font-style:italic; ");
        if(font.getStrikeout()) fontStyle.append("text-decoration:line-through; ");

        final Color fontColor = getPaletteColor(workbook, font.getColor());
        if (fontColor != null) style.append("color: " + convertCellColorToRGB(fontColor)+ "; ");

        if (font.getFontHeightInPoints() != 0) style.append("font-size:" + font.getFontHeightInPoints() + "pt;");
    }




    protected void processColumnHeaders(Sheet sheet, int maxSheetColumns, Element table) {
        Element tableHeader = htmlDocumentFacade.createTableHeader();
        table.appendChild(tableHeader);

        Element tr = htmlDocumentFacade.createTableRow();

        if (isOutputRowNumbers()) {
            // empty row at left-top corner
            tr.appendChild(htmlDocumentFacade.createTableHeaderCell());
        }

        for (int c = 0; c < maxSheetColumns; c++) {
            if (!isOutputHiddenColumns() && sheet.isColumnHidden(c))
                continue;

            Element th = htmlDocumentFacade.createTableHeaderCell();
            String text = getColumnName(c);
            th.appendChild(htmlDocumentFacade.createText(text));
            tr.appendChild(th);
        }
        tableHeader.appendChild(tr);
    }

    /**
     * Creates COLGROUP element with width specified for all columns. (Except
     * first if <tt>{@link #isOutputRowNumbers()}==true</tt>)
     */
    protected void processColumnWidths(Sheet sheet, int maxSheetColumns, Element table) {
        // draw COLS after we know max column number
        Element columnGroup = htmlDocumentFacade.createTableColumnGroup();
        if (isOutputRowNumbers()) {
            columnGroup.appendChild(htmlDocumentFacade.createTableColumn());
        }

        List<Element> colList = new ArrayList<>(maxSheetColumns);
        List<Integer> colWidthList = new ArrayList<>(maxSheetColumns);
        int totalColumnWidth = 0, colWidth;
        for (int c = 0; c < maxSheetColumns; c++) {
            if (!isOutputHiddenColumns() && sheet.isColumnHidden(c))
                continue;

            Element col = htmlDocumentFacade.createTableColumn();
            colWidth = getColumnWidth(sheet, c);
            totalColumnWidth += colWidth;

//            if(!hasMaxPageSize) {
//                col.setAttribute("width", String.valueOf(colWidth));
//                columnGroup.appendChild(col);
//            }else{
                colList.add(col);
                colWidthList.add(colWidth);
//            }
        }

//        if(hasMaxPageSize){
        int i=0;
        for(Element col : colList){
            if(col == null) continue;

            col.setAttribute("width", String.valueOf(Math.round(colWidthList.get(i)*100 /totalColumnWidth)) + "%");
            i++;
            columnGroup.appendChild(col);
        }
//        }


        table.appendChild(columnGroup);
    }

    /**
     * 生成对应的标题，作者，备注，关键字信息
     * @param summaryInformation
     */
    protected void processDocumentInformation(SummaryInformation summaryInformation) {
        if (StringUtils.isNoneBlank(summaryInformation.getTitle())) htmlDocumentFacade.setTitle(summaryInformation.getTitle());
        if (StringUtils.isNoneBlank(summaryInformation.getAuthor())) htmlDocumentFacade.addAuthor(summaryInformation.getAuthor());
        if (StringUtils.isNoneBlank(summaryInformation.getKeywords())) htmlDocumentFacade.addKeywords(summaryInformation.getKeywords());
        if (StringUtils.isNoneBlank(summaryInformation.getComments())) htmlDocumentFacade.addDescription(summaryInformation.getComments());
    }


    /**
     * @param excelFile
     * @param sheetNames
     * @return
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    public static StringBuilder convertExcelToHTML(File excelFile, List<String> sheetNames, Rectangle pageSize, float marginLeft, float marginRight) throws IOException, ParserConfigurationException, TransformerException {
        ExcelToHtmlTemplateConvertor converterInstance = new ExcelToHtmlTemplateConvertor();
//        converterInstance.setUseDivsToSpan(true);

        final HSSFWorkbook workbook = ExcelToFoUtils.loadXls(excelFile);
        converterInstance.processWorkbook(workbook, sheetNames, pageSize, marginLeft, marginRight);

        DOMSource domSource = new DOMSource(converterInstance.getDocument());
        StringBuilderWriter writer = new StringBuilderWriter();
        StreamResult streamResult = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        tf.setAttribute("indent-number", new Integer(2));

        Transformer serializer = tf.newTransformer();

        // TODO set encoding from a command argument
        serializer.setOutputProperty( OutputKeys.ENCODING, "UTF-8" );
        serializer.setOutputProperty( OutputKeys.INDENT, "true" );
        serializer.setOutputProperty( OutputKeys.METHOD, "xml" );
//        serializer.setOutputProperty( OutputKeys.METHOD, "html" );
        serializer.transform(domSource, streamResult);

        return new StringBuilder(
                writer.getBuilder().toString().replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>", "<!DOCTYPE html>")
        );
    }


    /**
     * (Main)处理Excel Workbook的主方法
     * @param workbook
     */
    public void processWorkbook(Workbook workbook, List<String> sheetNames, Rectangle pageSize, float marginLeft, float marginRight) {
        if(workbook instanceof HSSFWorkbook) {
            final SummaryInformation summaryInformation = ((HSSFWorkbook)workbook).getSummaryInformation();
            if (summaryInformation != null) {
                processDocumentInformation(summaryInformation);
            }
        }
        htmlDocumentFacade.addMeta("charset", "UTF-8");

        if (isUseDivsToSpan()) {
            // prepare CSS classes for later usage
            this.cssClassContainerCell = htmlDocumentFacade.getOrCreateCssClass(cssClassPrefixCell,
                                                    "padding:0;margin:0;align:left;vertical-align:top;");
            this.cssClassContainerDiv = htmlDocumentFacade.getOrCreateCssClass(cssClassPrefixDiv, "position:relative;");
        }

        if(sheetNames == null || sheetNames.isEmpty()){
            sheetNames = new ArrayList<>();
            for(int i=0; i<workbook.getNumberOfSheets(); i++){
                sheetNames.add(workbook.getSheetName(i));
            }
        }

        for (String sheetName : sheetNames) {
            processSheet(workbook.getSheet(sheetName), pageSize, marginLeft, marginRight);
        }

        htmlDocumentFacade.updateStylesheet();
    }

    public void processWorkbook(Workbook workbook) {
        processWorkbook(workbook, null, null, 36, 36);
    }

    /**
     * 处理指定的Sheet
     *
     * @param sheet
     */
    protected void processSheet(Sheet sheet, Rectangle pageSize, float marginLeft, float marginRight) {
        processSheetHeader(htmlDocumentFacade.getBody(), sheet);

        final int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
        if (physicalNumberOfRows <= 0) return;

        Element table = htmlDocumentFacade.createTable();
//        table.setAttribute("width", "100%");
        htmlDocumentFacade.addStyleClass(table, cssClassPrefixTable,"border-collapse:collapse;border-spacing:0; width:100%");

        Element tableBody = htmlDocumentFacade.createTableBody();

        // 1. 获取合并的单元格信息
        final CellRangeAddress[][] mergedRanges = buildMergedRangesMap(sheet);

        final List<Element> emptyRowElements = new ArrayList<>(physicalNumberOfRows);
        int maxSheetColumns = 1;
        // 2. 遍历每一行
        for (int r = sheet.getFirstRowNum(); r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);

            if (row == null)
                continue;

            if (!isOutputHiddenRows() && row.getZeroHeight())
                continue;

            Element tableRowElement = htmlDocumentFacade.createTableRow();
            htmlDocumentFacade.addStyleClass(tableRowElement,
                    cssClassPrefixRow,
                    "height:" + (row.getHeight() / 20f) + "pt;"
                );

            // 处理当前行
            int maxRowColumnNumber = processRow(mergedRanges, row, tableRowElement);

            if (maxRowColumnNumber == 0) {
                emptyRowElements.add(tableRowElement);
            } else {
                if (!emptyRowElements.isEmpty()) {
                    for (Element emptyRowElement : emptyRowElements) {
                        tableBody.appendChild(emptyRowElement);
                    }
                    emptyRowElements.clear();
                }

                tableBody.appendChild(tableRowElement);
            }
            maxSheetColumns = Math.max(maxSheetColumns, maxRowColumnNumber);
        }

        processColumnWidths(sheet, maxSheetColumns, table);

        if (isOutputColumnHeaders()) {
            processColumnHeaders(sheet, maxSheetColumns, table);
        }

        table.appendChild(tableBody);

        htmlDocumentFacade.getBody().appendChild(table);
    }

    protected void processSheetHeader(Element htmlBody, Sheet sheet) {
        if(outputSheetPageHeader) {
            Element h2 = htmlDocumentFacade.createHeader2();
            if(sheetPageHeader == null) sheetPageHeader = sheet.getSheetName();
            h2.appendChild(htmlDocumentFacade.createText(sheetPageHeader));
            htmlBody.appendChild(h2);
        }
    }

    /**
     * Parse Parent Attribute From Current Cell (Normally is first cell in row)
     * @param cell
     * @return
     */
    protected List<CellVarFuncInfo> parseParentAttributeList(Cell cell) {
        String value = getCellStringValue(cell);
        CellVarFuncInfoGroup cellVarFuncInfoGroup = CellVarFuncInfoGroup.parseVarFuncInfo(value);

        return cellVarFuncInfoGroup.filterCellInfo(CellVarFuncInfo.VarFunctionType.PARENT_ATTRIBUTE);
    }


    /**
     * @return maximum 1-base index of column that were rendered, zero if none
     */
    protected int processRow(CellRangeAddress[][] mergedRanges, Row row, Element tableRowElement) {
        final Sheet sheet = row.getSheet();
        final short maxColIx = row.getLastCellNum();
        if (maxColIx <= 0) return 0;

        List<CellVarFuncInfo> attributeList;
        CellVarFuncInfoGroup cellVarFuncInfoGroup;
        final List<Element> emptyCells = new ArrayList<>(maxColIx);

        // 如果需要输出仿Excel最左侧的的Row Number
        if (isOutputRowNumbers()) {
            Element tableRowNumberCellElement = htmlDocumentFacade.createTableHeaderCell();
            processRowNumber(row, tableRowNumberCellElement);
            emptyCells.add(tableRowNumberCellElement);
        }

        int maxRenderedColumn = 0;
        for (int colIx = 0; colIx < maxColIx; colIx++) {
            // 如果不输出隐藏列, 则跳过
            if (!isOutputHiddenColumns() && sheet.isColumnHidden(colIx)) continue;

            // 如果是不是合并的单元格的第一格, 则跳过 (后续会合并)
            CellRangeAddress range = ExcelToHtmlUtils.getMergedRange(mergedRanges, row.getRowNum(), colIx);
            if (range != null && (range.getFirstColumn() != colIx || range.getFirstRow() != row.getRowNum())) continue;

            Cell cell = row.getCell(colIx);

            int divWidthPx = 0;
            if (isUseDivsToSpan()) {
                divWidthPx = getColumnWidth(sheet, colIx);

                boolean hasBreaks = false;
                for (int nextColumnIndex = colIx + 1; nextColumnIndex < maxColIx; nextColumnIndex++) {
                    if (!isOutputHiddenColumns() && sheet.isColumnHidden(nextColumnIndex)) continue;

                    if (row.getCell(nextColumnIndex) != null
                            && !isTextEmpty(row.getCell(nextColumnIndex))) {
                        hasBreaks = true;
                        break;
                    }

                    divWidthPx += getColumnWidth(sheet, nextColumnIndex);
                }

                if (!hasBreaks)
                    divWidthPx = Integer.MAX_VALUE;
            }

            Element tableCellElement = htmlDocumentFacade.createTableCell();

            // 创建合并单元的单元格 (由于上面已经过滤掉了非第一列的单元格，因此这里应该都是第一列的单元格，可以直接创建)
            if (range != null) {
                if (range.getFirstColumn() != range.getLastColumn())
                    tableCellElement.setAttribute(
                            "colspan",
                            String.valueOf(range.getLastColumn()
                                    - range.getFirstColumn() + 1));
                if (range.getFirstRow() != range.getLastRow())
                    tableCellElement.setAttribute(
                            "rowspan",
                            String.valueOf(range.getLastRow()
                                    - range.getFirstRow() + 1));
            }

            boolean emptyCell;
            String[] attributeVal;
            if (cell != null) {
                if((cellVarFuncInfoGroup = CellVarFuncInfoGroup.parseVarFuncInfo(getCellStringValue(cell))) != null){
                    // 尝试检查是否存在需要添加到当前 Table Row上的Attribute
                    if((attributeList = cellVarFuncInfoGroup.filterCellInfo(CellVarFuncInfo.VarFunctionType.PARENT_ATTRIBUTE)) != null){
                        for(CellVarFuncInfo attribute : attributeList){
                            if((attributeVal = attribute.getAttribute()) != null) {
//                                if (attributeVal[1] != null){
//                                    varValue = attributeVal[1].trim();
//
//                                    if(!varValue.startsWith("\"") && !varValue.startsWith("'")){
//                                        varValue = "\""+ varValue + "\"";
//                                    }
//                                }
                                tableRowElement.setAttribute(Thymeleaf_PREFIX + attributeVal[0], attributeVal[1]);
                            }
                        }
                    }

                    // 检查当前的Cell 上是否有Attribute需要添加
                    if((attributeList = cellVarFuncInfoGroup.filterCellInfo(CellVarFuncInfo.VarFunctionType.CURRENT_ATTRIBUTE)) != null){
                        for(CellVarFuncInfo attribute : attributeList){
                            if((attributeVal = attribute.getAttribute()) != null){
                                tableCellElement.setAttribute(Thymeleaf_PREFIX + attributeVal[0], attributeVal[1]);
                            }
                        }
                    }
                }

                emptyCell = processCell(cell, tableCellElement,
                        getColumnWidth(sheet, colIx), divWidthPx,
                        row.getHeight() / 20f,
                        cellVarFuncInfoGroup
                );
            } else {
                emptyCell = true;
            }

            if (emptyCell) {
                emptyCells.add(tableCellElement);
            } else {
                for (Element emptyCellElement : emptyCells) {
                    tableRowElement.appendChild(emptyCellElement);
                }
                emptyCells.clear();

                tableRowElement.appendChild(tableCellElement);
                maxRenderedColumn = colIx;
            }
        }

        return maxRenderedColumn + 1;
    }

    /**
     * 获取行号
     * @param row
     * @param tableRowNumberCellElement
     */
    protected void processRowNumber(Row row, Element tableRowNumberCellElement) {
        tableRowNumberCellElement.setAttribute("class", "rownumber");
        Text text = htmlDocumentFacade.createText(getRowName(row));
        tableRowNumberCellElement.appendChild(text);
    }


    /**
     * 处理单个Cell的内容
     *
     * @param cell
     * @param tableCellElement
     * @param normalWidthPx
     * @param maxSpannedWidthPx
     * @param normalHeightPt
     * @return
     */
    protected boolean processCell(Cell cell, Element tableCellElement,
                                  int normalWidthPx, int maxSpannedWidthPx, float normalHeightPt, CellVarFuncInfoGroup cellVarFuncInfoGroup) {
        final CellStyle cellStyle = cell.getCellStyle();

        // 1. 获取Cell的值
        String value = null;
        if(cellVarFuncInfoGroup != null){
            List<CellVarFuncInfo> cellVarFuncInfos = cellVarFuncInfoGroup.filterCellInfo(CellVarFuncInfo.VarFunctionType.PLAIN_TEXT, CellVarFuncInfo.VarFunctionType.COMMENT);
            if(cellVarFuncInfos != null && !cellVarFuncInfos.isEmpty()){
                StringBuilder buf = new StringBuilder();
                for(CellVarFuncInfo cellVarFuncInfo : cellVarFuncInfos){
                    if(cellVarFuncInfo.getValues() != null) {
                        buf.append(cellVarFuncInfo.getAttribute()[1]);
                    }
                }
                value= buf.toString();
            }else{
                value= "";
            }
        }else{
            value= getCellStringValue(cell);
        }

        if(value == null) return true;

        final boolean noText = StringUtils.isBlank(value);
        final boolean wrapInDivs = !noText && isUseDivsToSpan() && !cellStyle.getWrapText();

        // 2. 获取并设置HTML Table Cell的样式
        if (cellStyle.getIndex() != 0) {
            Workbook workbook = cell.getRow().getSheet().getWorkbook();
            String mainCssClass = getStyleClassName(workbook, cellStyle);

            if (wrapInDivs) {
                tableCellElement.setAttribute("class", mainCssClass + " "+ cssClassContainerCell);
            } else {
                tableCellElement.setAttribute("class", mainCssClass);
            }

            if (noText) {
                /*
                 * if cell style is defined (like borders, etc.) but cell text
                 * is empty, add "&nbsp;" to output, so browser won't collapse
                 * and ignore cell
                 */
                value = "\u00A0";
            }
        }

        if (isOutputLeadingSpacesAsNonBreaking() && value.startsWith(" ")) {
            StringBuilder builder = new StringBuilder();
            for (int c = 0; c < value.length(); c++) {
                if (value.charAt(c) != ' ')
                    break;
                builder.append('\u00a0');
            }

            if (value.length() != builder.length())
                builder.append(value.substring(builder.length()));

            value = builder.toString();
        }

        Text text = htmlDocumentFacade.createText(value);

        if (wrapInDivs) {
            Element outerDiv = htmlDocumentFacade.createBlock();
            outerDiv.setAttribute("class", this.cssClassContainerDiv);

            Element innerDiv = htmlDocumentFacade.createBlock();
            StringBuilder innerDivStyle = new StringBuilder();
            innerDivStyle.append("position:absolute;min-width:");
            innerDivStyle.append(normalWidthPx);
            innerDivStyle.append("px;");
            if (maxSpannedWidthPx != Integer.MAX_VALUE) {
                innerDivStyle.append("max-width:");
                innerDivStyle.append(maxSpannedWidthPx);
                innerDivStyle.append("px;");
            }
            innerDivStyle.append("overflow:hidden;max-height:");
            innerDivStyle.append(normalHeightPt);
            innerDivStyle.append("pt;white-space:nowrap;");
            ExcelToHtmlUtils.appendAlign(innerDivStyle, cellStyle.getAlignmentEnum().getCode());
            htmlDocumentFacade.addStyleClass(outerDiv, cssClassPrefixDiv,
                    innerDivStyle.toString());

            innerDiv.appendChild(text);
            outerDiv.appendChild(innerDiv);
            tableCellElement.appendChild(outerDiv);
        } else {
            tableCellElement.appendChild(text);
        }

        //////////////////////////////////////////////////////////
        // Handle Functional and Variable
        //////////////////////////////////////////////////////////
        if(CellVarFuncInfo.VarFunctionType.hasVarFunction(value)){
            doFunctionAndVariableHandling(value, tableCellElement);
        }

        return StringUtils.isEmpty(value) && (cellStyle.getIndex() == 0);
    }


    /**
     * 处理Cell中的Cell定义
     * @param value
     * @param tableCellElement
     */
    protected void doFunctionAndVariableHandling(String value, Element tableCellElement){
        CellVarFuncInfoGroup group = new CellVarFuncInfoGroup();
        group.parseVarFuncInfo(value);

//        group.getInfoList()
    }

    public static List<String> parserVariable(String value){
        List<String> variableList = new ArrayList<>();
        int startPos = 0, currPos;
        String varBuf;
        while((currPos = value.indexOf("&{", startPos))>=0){
            varBuf = value.substring(currPos + 2, value.indexOf("}", currPos) );
            variableList.add(varBuf);

            startPos = value.indexOf("}", currPos);
        }

        return variableList;
    }


    public static void main(String[] args){
        // String str = "&{var=123}%{f=dt}&{cat=---}#{c=test}&{var=456}%{f=decode,BrCT}";
        String str = "#{c=还款次数}&&{each=\"prod : ${prods}\"}Test&{var=SZp17i2.ors.RpCnt}%{f=decode,BrCT}-Gao-&&{class=\"${iterStat.odd}? 'odd'\"}";
        CellVarFuncInfoGroup group = new CellVarFuncInfoGroup();
        group.parseVarFuncInfo(str);

//        List<String> list = ExcelToHtmlTemplateConvertor.parserVariable(str);
        System.out.println(group);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Common Helper Method to Access Excel Workbook and Sheet
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     *  从CellStyle的缓存中获取样式的名称
     * @param workbook
     * @param cellStyle
     * @return
     */
    protected String getStyleClassName(Workbook workbook, CellStyle cellStyle) {
        final Short cellStyleKey = Short.valueOf(cellStyle.getIndex());

        String knownClass = excelStyleToClass.get(cellStyleKey);
        if (knownClass != null) return knownClass;

        String cssStyle = buildStyle(workbook, cellStyle);
        String cssClass = htmlDocumentFacade.getOrCreateCssClass(cssClassPrefixCell, cssStyle);
        excelStyleToClass.put(cellStyleKey, cssClass);

        return cssClass;
    }

    /**
     * 获取指定Cell的String Value (如果返回Null则说明发生错误)
     *
     * @param cell
     * @return
     */
    public String getCellStringValue(Cell cell){
        String value = null;
        switch (cell.getCellTypeEnum()) {
            case STRING:
                // XXX: enrich
                value = cell.getRichStringCellValue().getString();
                break;
            case FORMULA:
                switch (cell.getCachedFormulaResultTypeEnum()) {
                    case STRING:
                        RichTextString str = cell.getRichStringCellValue();
                        if (str != null && str.length() > 0) {
                            value = (str.toString());
                        } else {
                            value = EMPTY;
                        }
                        break;
                    case NUMERIC:
                        double nValue = cell.getNumericCellValue();
                        final CellStyle cellStyle = cell.getCellStyle();

                        short df = cellStyle.getDataFormat();
                        String dfs = cellStyle.getDataFormatString();
                        value = _formatter.formatRawCellContents(nValue, df, dfs);
                        break;
                    case BOOLEAN:
                        value = String.valueOf(cell.getBooleanCellValue());
                        break;
                    case ERROR:
                        value = ErrorEval.getText(cell.getErrorCellValue());
                        break;
                    default:
                        logger.error("Unexpected cell cachedFormulaResultType [{}]", cell.getCachedFormulaResultTypeEnum());
                        value = EMPTY;
                        break;
                }
                break;
            case BLANK:
                value = EMPTY;
                break;
            case NUMERIC:
                value = _formatter.formatCellValue(cell);
                break;
            case BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            case ERROR:
                value = ErrorEval.getText(cell.getErrorCellValue());
                break;
            default:
                logger.error("Unexpected cell type [{}]", cell.getCellTypeEnum());
        }

        return value;
    }


    /**
     * 获取Cell的边框宽度
     *
     * @param xlsBorder
     * @return
     */
    public static String getBorderWidth(BorderStyle xlsBorder) {
        final String borderWidth;
        switch (xlsBorder) {
            case MEDIUM_DASH_DOT:
            case MEDIUM_DASH_DOT_DOT:
            case MEDIUM_DASHED:
                borderWidth = "2pt";
                break;
            case THICK:
                borderWidth = "1pt";
                break;
            default:
//                borderWidth = "thin";
                borderWidth = "0.1pt";
                break;
        }
        return borderWidth;
    }

    protected static int getColumnWidth(Sheet sheet, int columnIndex) {
        return ExcelToHtmlUtils.getColumnWidthInPx(sheet.getColumnWidth(columnIndex));
    }

    protected static int getDefaultColumnWidth(Sheet sheet) {
        return ExcelToHtmlUtils.getColumnWidthInPx(sheet.getDefaultColumnWidth());
    }

    /**
     * Generates name for output as column header in case
     * <tt>{@link #isOutputColumnHeaders()} == true</tt>
     *
     * @param columnIndex 0-based column index
     */
    protected String getColumnName(int columnIndex) {
        return NumberFormatter.getNumber(columnIndex + 1, 3);
    }


    public FontReplacer getFontReplacer() {
        return fontReplacer;
    }

    /**
     * Generates name for output as row number in case
     * <tt>{@link #isOutputRowNumbers()} == true</tt>
     */
    protected String getRowName(Row row) {
        return String.valueOf(row.getRowNum() + 1);
    }

    public boolean isOutputColumnHeaders() {
        return outputColumnHeaders;
    }

    public boolean isOutputHiddenColumns() {
        return outputHiddenColumns;
    }

    public boolean isOutputHiddenRows() {
        return outputHiddenRows;
    }

    public boolean isOutputLeadingSpacesAsNonBreaking() {
        return outputLeadingSpacesAsNonBreaking;
    }

    public boolean isOutputRowNumbers() {
        return outputRowNumbers;
    }

    /**
     * Check Cell is empty or not
     *  1. HSSFCell --> Excel 97-2003 Format (XLS)
     *  2. XSSFCell --> Excel 2010 new Format (XLSX)
     * @param cell
     * @return
     */
    protected boolean isTextEmpty(Cell cell) {
        final String value;

        switch (cell.getCellTypeEnum()) {
            case STRING:
                // XXX: enrich
                value = cell.getRichStringCellValue().getString();
                break;
            case FORMULA:
                switch (cell.getCachedFormulaResultTypeEnum()) {
                    case STRING:
                        RichTextString str = cell.getRichStringCellValue();
                        if (str == null || str.length() <= 0)
                            return false;

                        value = str.toString();
                        break;
                    case NUMERIC:
                        CellStyle style = cell.getCellStyle();
                        double nval = cell.getNumericCellValue();
                        short df = style.getDataFormat();
                        String dfs = style.getDataFormatString();
                        value = _formatter.formatRawCellContents(nval, df, dfs);
                        break;
                    case BOOLEAN:
                        value = String.valueOf(cell.getBooleanCellValue());
                        break;
                    case ERROR:
                        value = ErrorEval.getText(cell.getErrorCellValue());
                        break;
                    default:
                        value = EMPTY;
                        break;
                }
                break;
            case BLANK:
                value = EMPTY;
                break;
            case NUMERIC:
                value = _formatter.formatCellValue(cell);
                break;
            case BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            case ERROR:
                value = ErrorEval.getText(cell.getErrorCellValue());
                break;
            default:
                return true;
        }

        return StringUtils.isBlank(value);
    }

    /**
     * Creates a map (i.e. two-dimensional array) filled with ranges. Allow fast
     * retrieving {@link CellRangeAddress} of any cell, if cell is contained in
     * range.
     *
     */
    public static CellRangeAddress[][] buildMergedRangesMap(Sheet sheet )
    {
        CellRangeAddress[][] mergedRanges = new CellRangeAddress[1][];
        for ( final CellRangeAddress cellRangeAddress : sheet.getMergedRegions() )
        {
            final int requiredHeight = cellRangeAddress.getLastRow() + 1;
            if ( mergedRanges.length < requiredHeight )
            {
                CellRangeAddress[][] newArray = new CellRangeAddress[requiredHeight][];
                System.arraycopy( mergedRanges, 0, newArray, 0,
                        mergedRanges.length );
                mergedRanges = newArray;
            }

            for ( int r = cellRangeAddress.getFirstRow(); r <= cellRangeAddress
                    .getLastRow(); r++ )
            {
                final int requiredWidth = cellRangeAddress.getLastColumn() + 1;

                CellRangeAddress[] rowMerged = mergedRanges[r];
                if ( rowMerged == null )
                {
                    rowMerged = new CellRangeAddress[requiredWidth];
                    mergedRanges[r] = rowMerged;
                }
                else
                {
                    final int rowMergedLength = rowMerged.length;
                    if ( rowMergedLength < requiredWidth )
                    {
                        final CellRangeAddress[] newRow = new CellRangeAddress[requiredWidth];
                        System.arraycopy( rowMerged, 0, newRow, 0,
                                rowMergedLength );

                        mergedRanges[r] = newRow;
                        rowMerged = newRow;
                    }
                }

                Arrays.fill( rowMerged, cellRangeAddress.getFirstColumn(),
                        cellRangeAddress.getLastColumn() + 1, cellRangeAddress );
            }
        }
        return mergedRanges;
    }



    protected Document getDocument(){
        return htmlDocumentFacade.getDocument();
    }


    public void setFontReplacer(FontReplacer fontReplacer) {
        this.fontReplacer = fontReplacer;
    }

    public void setOutputColumnHeaders(boolean outputColumnHeaders) {
        this.outputColumnHeaders = outputColumnHeaders;
    }

    public void setOutputHiddenColumns(boolean outputZeroWidthColumns) {
        this.outputHiddenColumns = outputZeroWidthColumns;
    }

    public void setOutputHiddenRows(boolean outputZeroHeightRows) {
        this.outputHiddenRows = outputZeroHeightRows;
    }

    public void setOutputLeadingSpacesAsNonBreaking(
            boolean outputPrePostSpacesAsNonBreaking) {
        this.outputLeadingSpacesAsNonBreaking = outputPrePostSpacesAsNonBreaking;
    }

    public void setOutputRowNumbers(boolean outputRowNumbers) {
        this.outputRowNumbers = outputRowNumbers;
    }

    public HSSFDataFormatter get_formatter() {
        return _formatter;
    }

    public String getCssClassContainerCell() {
        return cssClassContainerCell;
    }

    public void setCssClassContainerCell(String cssClassContainerCell) {
        this.cssClassContainerCell = cssClassContainerCell;
    }

    public String getCssClassContainerDiv() {
        return cssClassContainerDiv;
    }

    public void setCssClassContainerDiv(String cssClassContainerDiv) {
        this.cssClassContainerDiv = cssClassContainerDiv;
    }

    public String getCssClassPrefixCell() {
        return cssClassPrefixCell;
    }

    public void setCssClassPrefixCell(String cssClassPrefixCell) {
        this.cssClassPrefixCell = cssClassPrefixCell;
    }

    public String getCssClassPrefixDiv() {
        return cssClassPrefixDiv;
    }

    public void setCssClassPrefixDiv(String cssClassPrefixDiv) {
        this.cssClassPrefixDiv = cssClassPrefixDiv;
    }

    public String getCssClassPrefixRow() {
        return cssClassPrefixRow;
    }

    public void setCssClassPrefixRow(String cssClassPrefixRow) {
        this.cssClassPrefixRow = cssClassPrefixRow;
    }

    public String getCssClassPrefixTable() {
        return cssClassPrefixTable;
    }

    public void setCssClassPrefixTable(String cssClassPrefixTable) {
        this.cssClassPrefixTable = cssClassPrefixTable;
    }

    public boolean isUseDivsToSpan() {
        return useDivsToSpan;
    }

    public void setUseDivsToSpan(boolean useDivsToSpan) {
        this.useDivsToSpan = useDivsToSpan;
    }

    public boolean isOutputSheetPageHeader() {
        return outputSheetPageHeader;
    }

    public void setOutputSheetPageHeader(boolean outputSheetPageHeader) {
        this.outputSheetPageHeader = outputSheetPageHeader;
    }

    public String getSheetPageHeader() {
        return sheetPageHeader;
    }

    public void setSheetPageHeader(String sheetPageHeader) {
        this.sheetPageHeader = sheetPageHeader;
    }
}

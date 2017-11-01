# HTML TO PDF
用于演示Java中使用Apache POI将指定格式的Excel转为Thymleaf模版HTML, 然后使用thymeleaf引擎解析生成静态的HTML页面内容，然后调用iText将静态的HTML内容转为PDF文档

* #### 1). 使用的工具类库
    *   1.1). Apache POI
        *   Home: [http://poi.apache.org/]
    *   1.2). Thymleaf
        *   Home: [http://www.thymeleaf.org/index.html]
    *   1.3). iText
        *   Developer Example Home: [https://developers.itextpdf.com/examples-itext5]
    

* #### 2). Excel To Thymleaf Template
    * #### 基本语法
        * 由于生成的目标是Thymleaf的模版, 因此Excel单元格中内容的基本的语法以Thymleaf编写的语法一致
            *   Thymleaf的语法:  http://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html
        * 使用方法：
            * 1). \#{c=【注释内容】}: 
                * 例如: #{c=这是一段注释}
                
            * 2). \&{....} 会转换为th:....:  
                * 例如: 
                    * &{text=${prod.name}} 会转换为: \<td th.text="${prod.name}">
                    * &{text=${#numbers.formatDecimal(prod.amt, 1, 2)}} 会转换为: \<td th:text="${#numbers.formatDecimal(prod.amt, 1, 2)}">
                     
            * 3). \&&{....} 会转换为父节点Tag的属性: th:....:  
                * 例如: &&{each=prod : ${prods.list}} 会转换为: 
                    * \<tr th:each="prod : ${prods.ors}"> 
    
    * #### 工具类
        * ExcelToHtmlTemplateConvertor.convertExcelToHTML(
                    
                    【1. File: Excel 文件】,                    
                    【2. List\<String>: 需要转换的Sheet Name. 为null时转换全部】,
                    【3. 页面大小: PageSize.A4】,
                    【4. 左边距】,
                    【5. 右边距】
            );
            
         * 返回值: StringBuilder
         

* #### 3). Dynamic Thymleaf Template To Static HTML 
    * #### 工具类
         MyThymeleafPageGenerator

* #### 4). Static HTML to PDF
    * #### 工具类
        * ReportDocumentInfo
        * HtmlToPDFConvertor            
            + generateReportDocument

* #### 5). 测试类
    + ExcelToThymleafTest
    


* #### 参考文献：
    * 1).
    * 2).
    * 3).


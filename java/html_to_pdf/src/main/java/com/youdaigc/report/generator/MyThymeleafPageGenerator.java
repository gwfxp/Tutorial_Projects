package com.youdaigc.report.generator;

import org.apache.commons.io.output.StringBuilderWriter;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;

import javax.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

//import org.thymeleaf.templateresource.ITemplateResource templateresolver.Iresourceresolver.IResourceResolver;

/**
 * Created by GaoWeiFeng on 2017-08-24.
 */
public class MyThymeleafPageGenerator implements Serializable {
    private static final long serialVersionUID = -4953368761574356611L;

    public static enum TemplateResolverType{
        ClassLoaderTemplateResolver,
        FileTemplateResolver,
        SpringResourceTemplateResolver,
        UrlTemplateResolver,
        ServletContextTemplateResolver
    }

    protected AbstractConfigurableTemplateResolver resolver;

    /**
     * 模板所在目录，相对于当前classloader的classpath。
     */
    protected String thymeleafTemplatePath = "report_templates/";

    /**
     * 模板文件后缀
     */
    protected String thymeleafTemplateSuffix = ".html";

    /**
     * Thymeleaf Template Engine
     */
    transient protected TemplateEngine templateEngine = null;

    public MyThymeleafPageGenerator(){}

    public MyThymeleafPageGenerator(TemplateResolverType resolverType){
        initTemplateResolverByType(resolverType);
    }

    public MyThymeleafPageGenerator(AbstractConfigurableTemplateResolver resolver){
        this.resolver = resolver;
    }

    public MyThymeleafPageGenerator(String thymeleafTemplatePath){
        if(thymeleafTemplatePath!=null) this.thymeleafTemplatePath = thymeleafTemplatePath;
//        getTemplateEngine();
    }

    public MyThymeleafPageGenerator(String thymeleafTemplatePath, String thymeleafTemplateSuffix){
        if(thymeleafTemplatePath!=null) this.thymeleafTemplatePath = thymeleafTemplatePath;
        if(thymeleafTemplateSuffix!=null) this.thymeleafTemplateSuffix = thymeleafTemplateSuffix;
    }

    public MyThymeleafPageGenerator(String thymeleafTemplatePath, String thymeleafTemplateSuffix, AbstractConfigurableTemplateResolver resolver){
        if(thymeleafTemplatePath!=null) this.thymeleafTemplatePath = thymeleafTemplatePath;
        if(thymeleafTemplateSuffix!=null) this.thymeleafTemplateSuffix = thymeleafTemplateSuffix;
        this.resolver = resolver;
    }

    public MyThymeleafPageGenerator(String thymeleafTemplatePath, String thymeleafTemplateSuffix, TemplateResolverType resolverType){
        if(thymeleafTemplatePath!=null) this.thymeleafTemplatePath = thymeleafTemplatePath;
        if(thymeleafTemplateSuffix!=null) this.thymeleafTemplateSuffix = thymeleafTemplateSuffix;
        initTemplateResolverByType(resolverType);
    }

    protected void initTemplateResolverByType(TemplateResolverType resolverType){
        switch (resolverType){
            case ClassLoaderTemplateResolver:
                this.resolver = new ClassLoaderTemplateResolver();
                break;
            case UrlTemplateResolver:
                this.resolver = new UrlTemplateResolver();
                break;
            case FileTemplateResolver:
                this.resolver = new FileTemplateResolver();
                break;
//            case ServletContextTemplateResolver:
//                this.resolver = new ServletContextTemplateResolver();
//                break;
//            case SpringResourceTemplateResolver:
//                this.resolver = new SpringResourceTemplateResolver();
//                break;
        }
    }

//    /**
//     * <p>
//     * The name of the resource resolver.
//     * </p>
//     *
//     * @return the name of the resource resolver.
//     */
//    @Override
//    public String getName() {
//        return null;
//    }
//
//    /**
//     * <p>
//     * Resolve the resource, this is, open an input stream for it.
//     * </p>
//     * <p>
//     * If the resource cannot be resolved, this method should return null.
//     * </p>
//     *
//     * @param templateProcessingParameters the {@link TemplateProcessingParameters} object being used for template processing
//     * @param resourceName                 the resource name to be resolved/read
//     * @return an InputStream on the resource
//     */
//    @Override
//    public InputStream getResourceAsStream(TemplateProcessingParameters templateProcessingParameters, String resourceName) {
//        return null;
//    }

    /**
     *
     * @return
     */
    public TemplateEngine getTemplateEngine() {
        if(templateEngine == null){
            synchronized (this){
                if(templateEngine == null){
                    initTemplateEngine();
                }
            }
        }

        return templateEngine;
    }


    /**
     * 初始化Thymeleaf Template Engine
     */
    @PostConstruct
    public void initTemplateEngine(){
        // 构造模板引擎
//        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
//        TemplateResolver resolver = null;
//        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix(thymeleafTemplatePath);
        resolver.setSuffix(thymeleafTemplateSuffix);

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);

//        templateEngine.getDialects()
    }

    /**
     * 使用Thymeleaf Engine 解析指定的模版, 生成对应的HTML String
     *
     * @param pageTemplateName
     * @param paramObject
     * @return
     * @throws IOException
     */
    public String generatorPageString(String pageTemplateName, Map<String, Object> paramObject) throws IOException {
        // 构造上下文(Model)
        Context context = new Context();
        context.setVariables(paramObject);

        StringBuilderWriter writer = new StringBuilderWriter();
        // 渲染模板
        getTemplateEngine().process(pageTemplateName, context, writer);

        return writer.getBuilder().toString();
    }

    /**
     * 使用Thymeleaf Engine 解析指定的模版, 生成对应的HTML 文件
     *
     * @param outputFileName
     * @param pageTemplateName
     * @param paramObject
     * @throws IOException
     */
    public void outputPageString(String outputFileName, String pageTemplateName, Map<String, Object> paramObject) throws IOException {
        // 构造上下文(Model)
        Context context = new Context();
        context.setVariables(paramObject);

        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
        // 渲染模板
        getTemplateEngine().process(pageTemplateName, context, writer);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ThymeleafPageGenerator{");
        sb.append("thymeleafTemplatePath='").append(thymeleafTemplatePath).append('\'');
        sb.append(", thymeleafTemplateSuffix='").append(thymeleafTemplateSuffix).append('\'');
        sb.append(", templateEngine=").append(templateEngine);
        sb.append('}');
        return sb.toString();
    }

    public String getThymeleafTemplatePath() {
        return thymeleafTemplatePath;
    }

    public void setThymeleafTemplatePath(String thymeleafTemplatePath) {
        this.thymeleafTemplatePath = thymeleafTemplatePath;
    }

    public String getThymeleafTemplateSuffix() {
        return thymeleafTemplateSuffix;
    }

    public void setThymeleafTemplateSuffix(String thymeleafTemplateSuffix) {
        this.thymeleafTemplateSuffix = thymeleafTemplateSuffix;
    }
}

package com.youdaigc.util.bean;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by GaoWeiFeng on 2017-06-05.
 * Common class reflection operation methods
 */
public class MyClassHelper implements Serializable{
    private static final Logger logger = LoggerFactory.getLogger(MyClassHelper.class);

    final public static String ClassPathStr = "classpath:";
    private static final long serialVersionUID = -8691284644610550233L;

    /**
     * 获取Class Path 的根目录
     * @return
     */
    static public String getClassPathRootPath(){
        try {
            return MyClassHelper.class.getResource("/").toURI().getPath();
        } catch (URISyntaxException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取指定路径的真实全路径
     *  如果是classpath:xxxx 则会转为当前Classpath + xxxx路径
     * @param originalPath
     * @return
     */
    static public String getAbsolutePath(String originalPath){
        if(originalPath == null) {
            return null;
        }


        StringBuilder fullPath = new StringBuilder();
        if(originalPath.toLowerCase().startsWith(ClassPathStr)) {
            // If Path Start with classpath:, then use relative path
            fullPath.append(getClassPathRootPath());
            if (!fullPath.toString().endsWith("/") && !fullPath.toString().endsWith("\\")) {
                fullPath.append(File.separatorChar);
            }
            originalPath = originalPath.substring(ClassPathStr.length());
        }

        fullPath.append(originalPath);
        return fullPath.toString();
    }

    /**
     *  获取指定路径的真实全路径 （如果是classpath:xxxx 则会转为当前Classpath + xxxx路径）
     *
     * @param fileSuffix 文件的后缀
     * @param pathName 路径的片段名称
     * @return
     */
    static public String getAbsolutePathEx(String fileSuffix, String... pathName){
        StringBuilder fullPath = new StringBuilder();

        if(pathName != null && pathName.length > 0){
            boolean needAppendPathSeparator = false;

            for(String path : pathName){
                // 如果Path中包含ClassPath字符
                if(path.toLowerCase().startsWith(ClassPathStr)) {
                    fullPath.append(getClassPathRootPath());
                    // 截取ClassPath后面的路径
                    path = path.substring(ClassPathStr.length());
                }

                // 如果已存在的路径不是分隔符结尾
                if(fullPath.length() >1 && !fullPath.toString().trim().endsWith("/") && !fullPath.toString().trim().endsWith("\\")){
                    needAppendPathSeparator = true;
                }else{
                    needAppendPathSeparator = false;
                }

                // 如果后面的字符串不是分隔符开始，且之前的字符串没有分隔符结尾，就需要自动补足分隔符
                if(needAppendPathSeparator && !path.toString().trim().startsWith("/") && !path.toString().trim().startsWith("\\")){
                    fullPath.append(File.separatorChar);
                }

                fullPath.append(path);
            }
        }

        // 添加文件后缀:
        // Note: 不排除空字符串，是考虑可能出现的特殊格式要求
        if(fileSuffix != null){
            fullPath.append(fileSuffix);
        }

        return fullPath.toString();
    }

    /**
     * 获取指定的Class中实现的所有接口
     * @param sourceClass
     * @return
     */
    static public List<Class> getClassAllInterfaces(Class<?> sourceClass){
        if(sourceClass == null) {
            return null;
        }

        List<Class> resultList = new ArrayList<>();
        List<Class> bufList;
        Class<?> targetClass = sourceClass.getSuperclass();

        if(targetClass != null){
            // 递归获取父类的 接口
            bufList = getClassAllInterfaces(targetClass);
            if(bufList != null){
                resultList.addAll(bufList);
            }
        }

        // 获取当前Class实现的接口
        Class[] currentClassInterfaces = sourceClass.getInterfaces();
        if(currentClassInterfaces != null){
            resultList.addAll(Arrays.asList(currentClassInterfaces));
        }

        return resultList;
    }

    /**
     * 检查指定的Class中是否实现了 指定的接口
     * @param sourceClass
     * @param targetInterface
     * @return
     */
    static public boolean isClassImplInterfaces(Class<?> sourceClass, Class<?> targetInterface){
        if(sourceClass == null || targetInterface == null) {
            return false;
        }

        Class<?> parentClass = sourceClass.getSuperclass();
        if(targetInterface.isInterface()){
            // 检查当前Class的Interface实现
            for(Class inf : sourceClass.getInterfaces()){
                if(inf.equals(targetInterface)) {
                    return true;
                }
            }
        }else {
            if(sourceClass.equals(targetInterface)) {
                return true;
            }
        }

        // 递归检查父类的实现
        if(parentClass != null && !parentClass.equals(Object.class)){
            return isClassImplInterfaces(parentClass, targetInterface);
        }
        return false;

    // 1. 检查当前的Class是否实现
//        if(sourceClass.isAssignableFrom(targetInterface)) return true;
//        else {
//            Class<?> targetClass = sourceClass.getSuperclass();
//            // 递归查询父类是否实现了 接口
//            return isClassImplInterfaces(targetClass, targetInterface);
//        }
    }


    /**
     * 根据名字创建对应的Class
     * @param classFullName
     * @return
     */
    static public Class getClassFromName(String classFullName){
        if(StringUtils.isNoneBlank(classFullName)){
            try {
                return Class.forName(classFullName);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        return null;
    }


    /**
     * 根据指定的Class名称创建对应Class的实例对象
     * @param classFullName : 需要实例化的对象Class全名
     * @param constructorAgrs  需要实例化的对象构造式的必须参数
     * @return
     */
    static public <T> T getClassInstance(String classFullName, Object... constructorAgrs){
        Class targetClass = getClassFromName(classFullName);
        if(targetClass == null) {
            return null;
        }

        try {
            if(constructorAgrs != null && constructorAgrs.length>0 && constructorAgrs[0]!=null){
                Class[] argClassType = new Class[constructorAgrs.length];
                for(int i=0; i<constructorAgrs.length; i++){
                    argClassType[i] = constructorAgrs[i].getClass();
                }
                return (T) targetClass.getDeclaredConstructor(argClassType).newInstance(constructorAgrs);
            }else{
                return (T) targetClass.newInstance();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }


    /**
     * 根据指定的Class名称创建对应Class的实例对象 (可以带需要初始化的参数类名)
     * @param classFullName : 需要实例化的对象Class全名
     * @param constructorAgrs  需要实例化的对象构造式的必须参数类的全名
     * @return
     */
    static public <T> T getClassInstanceWithClassNameArgs(String classFullName, String... constructorAgrs){
        if(classFullName == null) {
            return null;
        }

        Object[] param = null;
        if(constructorAgrs != null && constructorAgrs.length>0 && constructorAgrs[0]!=null) {
            param = new Object[constructorAgrs.length];
            for (int i = 0; i < constructorAgrs.length; i++) {
                param[i] = MyClassHelper.getClassInstance(constructorAgrs[i]);
            }
        }

        return getClassInstance(classFullName, param);
    }

    /**
     * 获取指定的类方法
     *
     * @param sourceClass
     * @param methodName
     * @param caseSenstive
     * @param parameterTypes
     * @return
     */
    static public Method getClassMethod(final Class sourceClass, final String methodName, final boolean caseSenstive, final Class<?>... parameterTypes){
        if(sourceClass == null || methodName == null) {
            return null;
        }

        boolean matchFound = false;
        for(Method method : sourceClass.getMethods()){
            if(caseSenstive){
                matchFound = methodName.equals(method.getName());
            }else{
                matchFound = methodName.equalsIgnoreCase(method.getName());
            }

            if(matchFound){
                if(parameterTypes != null) {
                    if(parameterTypes.length == method.getParameterCount()){
                        Parameter[] parameters = method.getParameters();
                        for(int i=0; i<parameters.length; i++){
                            if(!parameters[0].getType().equals(parameterTypes[i])){
                                matchFound = false;
                                break;
                            }
                        }
                    }

                }

                if(matchFound) {
                    return method;
                }
            }
        }

        return null;
    }

}

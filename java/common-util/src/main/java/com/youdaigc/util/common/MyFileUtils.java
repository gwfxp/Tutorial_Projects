package com.youdaigc.util.common;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Common File Operation Method
 */
public class MyFileUtils extends FileUtils implements Serializable{
	/** The logger. */
    transient private static final Logger logger = LoggerFactory.getLogger(MyFileUtils.class);
	private static final long serialVersionUID = -6624174320952634524L;

	public static String defaultFileEncoding = "UTF-8"; // GB2312, UTF-8

    public static void write(String fileName, String source, String encoding) throws IOException{
    	if(logger.isDebugEnabled()){
            logger.debug("Start Output File " + fileName);
        }
    	write(new File(fileName), source, encoding); 
    	if(logger.isDebugEnabled()){
            logger.debug("Completed Output File " + fileName);
        }
    }

    public static void write(String fileName, String source) throws IOException{
    	write(new File(fileName), source, defaultFileEncoding);
    }
    
	/**
	 * Load from resource.
	 *
	 * @param resourceName the resource name
	 * @return the string
	 * @throws IOException 
	 */
	public static String loadStringFromResource(String resourceName, String fileEncoding) throws IOException{
		return readFileToString(
				(new ClassPathResource(resourceName)).getFile(), fileEncoding
			);
	}
	
	public static String loadStringFromResource(String resourceName) throws IOException{
		return loadStringFromResource(resourceName, defaultFileEncoding);
	}
	
	/**
	 * Load property.
	 *
	 * @param fileName the file name
	 * @param loadAsResource the load as resource
	 * @throws IOException the IO exception
	 */
	public static Properties loadProperty(String fileName, boolean loadAsResource) throws IOException{
		Properties result = new Properties();
		File sourceFile;
		if(loadAsResource){
			sourceFile = (new ClassPathResource(fileName)).getFile();
		}else{
			sourceFile = new File(fileName);
		}
		
		fileName = sourceFile.getName();
		if(fileName.toLowerCase().endsWith(".xml")){
			result.loadFromXML(new FileInputStream(sourceFile));
		}else{
			result.load(new FileInputStream(sourceFile));
		}
		
		return result;
	}
	
	public static Properties loadProperty(String fileName) throws IOException{
		return loadProperty(fileName, false);
	}

    /**
     * Load code template model from xml.
     *
     * @param fileName
     * @return the code template model
     * @throws IOException
     */
    public static Properties loadXMLPropertyXML(String fileName) throws IOException{
        File file = new File(fileName);

        if(file.exists() && file.isFile()){
            Properties p = new Properties();
            p.loadFromXML(new FileInputStream(file));
            return p;
        }else {
            throw new IOException(String.format("File %s not found!", fileName));
        }
    }


    /**
	 * Save property.
	 *
	 * @param fileName the file name
	 * @param loadAsResource the load as resource
	 * @param comments the comments
	 * @throws IOException the IO exception
	 */
	public static void saveProperty(String fileName, boolean loadAsResource, String comments) throws IOException{
		Properties result = new Properties();
		File sourceFile;
		if(loadAsResource){
			sourceFile = (new ClassPathResource(fileName)).getFile();
		}else{
			sourceFile = new File(fileName);
		}
		
		fileName = sourceFile.getName();
		if(fileName.toLowerCase().endsWith(".xml")){
			result.storeToXML(new FileOutputStream(sourceFile), comments);
		}else{
			result.store(new FileOutputStream(sourceFile), comments);
		}
	}
	
	/**
	 * Gets the folder file name list.
	 *
	 * @param path the path
	 * @return the folder file list
	 */
	public static List<String> getFolderFileNameList(String path){
		List<String> resultList = new ArrayList<String>();

		if(MyStringUtils.isNoneBlank(path)){
			File folder = new File(path);
			for(File f : folder.listFiles()){
				if(f.isDirectory()){
					resultList.addAll(getFolderFileNameList(f.getPath()));
				}else{
					resultList.add(f.getPath());
				}
			}
		}
		
		return resultList;
	}
	
	/**
	 * Gets the folder file list.
	 *
	 * @param path the path
	 * @return the folder file list
	 */
	public static List<File> getFolderFileList(String path){
		List<File> resultList = new ArrayList<File>();

		if(MyStringUtils.isNoneBlank(path)){
			File folder = new File(path);
			for(File f : folder.listFiles()){
				if(f.isDirectory()){
					resultList.addAll(getFolderFileList(f.getPath()));
				}else{
					resultList.add(f);
				}
			}
		}
		
		return resultList;
	}


}

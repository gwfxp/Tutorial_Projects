package com.youdaigc.util.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Common String operation method
 */
public class MyStringUtils extends StringUtils implements Serializable {
	private static final long serialVersionUID = -6766210690344901861L;

	private static final Logger logger = LoggerFactory.getLogger(MyStringUtils.class);

	public static final String DEFAULT_DELIMITER = ",";

	public static final String DEFAULT_VARIABLE_PREFIX = "${";
	public static final String DEFAULT_VARIABLE_SUFFIX = "}";


	/** The decimal format. */
	transient static final public SimpleDateFormat defualtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	transient static final public SimpleDateFormat dateFormatByDay = new SimpleDateFormat("yyyy-MM-dd");

	/** The decimal format. */
	transient static final public DecimalFormat decimalFormat = new DecimalFormat("0.00");

	/** The decimal rate format. */
	transient static final public DecimalFormat decimalRateFormat = new DecimalFormat("0.000%");

	transient static final public Random random = new Random(System.currentTimeMillis());


	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	Begin: 字符串操作 相关方法
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 获取Enum类型中的名称列表
	 * @param <T>
	 * @param source
	 * @return
	 */
	public static <T> List<String> getEnumNameList(Enum source){
		List<String> nameList = new ArrayList<>();
		if(source == null) {
            return nameList;
        }


		for(Object e : source.getDeclaringClass().getEnumConstants()){
			nameList.add(((Enum)e).name());
		}

		return nameList;
	}


	/**
	 * NVL 函数, 如果指定内容是null, 则返回给定的默认值
	 * @param source
	 * @param defaultValue
	 * @param <T>
	 * @return
	 */
	public static <T> T nvl(T source, T defaultValue){
		if(source != null) {
            return source;
        } else {
            return defaultValue;
        }
	}

	public static <T> boolean isEqual(T source, T target){
		if(source == null && target == null) {
            return true;
        }
		if(source == null){
			return target.equals(source);
		}else{
			return source.equals(target);
		}
	}

	public static <T> boolean isStringEqual(T source, T target, boolean ignoreCase){
		if(source == null && target == null) {
            return true;
        }
		if(source == null){
			if(ignoreCase){
				return target.toString().equalsIgnoreCase((source==null)?null:source.toString());
			}else{
				return target.toString().equals((source==null)?null:source.toString());
			}
		}else{
			if(ignoreCase){
				return source.toString().equalsIgnoreCase((target==null)?null:target.toString());
			}else{
				return source.toString().equals((target==null)?null:target.toString());
			}
		}
	}

	public static Double sumDouble(Double... source){
		Double result = 0D;
		if(source != null && source.length > 0){
			for(Double i : source){
				if(i != null) {
                    result += i;
                }
			}
		}

		return result;
	}

	public static Integer sumInt(Integer... source){
		Integer result = 0;
		if(source != null && source.length > 0){
			for(Integer i : source){
				if(i != null) {
                    result += i;
                }
			}
		}

		return result;
	}

	/**
	 * Format rate double value.
	 *
	 * @param source the source
	 * @return the string
	 */
	public static String formatRateDoubleValue(Double source){
		if(source == null) {
            return decimalRateFormat.format(0);
        } else {
            return decimalRateFormat.format(source);
        }
	}

	/**
	 * Format amt double value.
	 *
	 * @param source the source
	 * @return the string
	 */
	public static String formatAmtDoubleValue(Double source){
		if(source == null) {
            return decimalFormat.format(0);
        } else {
            return decimalFormat.format(source);
        }
	}

	public static <T> T getTValue(T source, T defaultValue){
		if(source == null) {
            return defaultValue;
        }
		return source;
	}

	public static double getDoubleValue(Double source, Double defaultValue){
		if(source == null) {
            return defaultValue;
        }
		return source;
	}
	public static double getDoubleValue(Double source){
		return getDoubleValue(source, 0d);
	}

	public static int getIntValue(Integer source, Integer defaultValue){
		if(source == null) {
            return defaultValue;
        }
		return source;
	}
	public static int getIntValue(Integer source){
		return getIntValue(source, 0);
	}

	/**
	 * Convert DBC case To SBC case.<br>
	 * 转全角的函数(SBC case)<br>
	 * 全角空格为12288，半角空格为32 * 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248<br>
	 *
	 * @param input the input
	 * @return the string
	 */
	public static String toSBC(String input) {
		if(isBlank(input)) {
            return input;
        }
		// 半角转全角：
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 32) {
				c[i] = (char) 12288;
				continue;
			}
			if (c[i] < 127) {
                c[i] = (char) (c[i] + 65248);
            }
		}
		return new String(c);
	}

	/**
	 * Convert SBC case To DBC case.<br>
	 * 转半角的函数(DBC case)<br>
	 * 全角空格为12288，半角空格为32 * 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248<br>
	 *
	 * @param input the input
	 * @return the string
	 */
	public static String toDBC(String input) {
		if(isBlank(input)) {
            return input;
        }

		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375) {
                c[i] = (char) (c[i] - 65248);
            }
		}
		return new String(c);
	}

	/**
	 * Convert String coding to BG2312.
	 *
	 * @param input the input
	 * @return the string
	 */
	public static String toGB2312(String input){
		try {
			// new String(buf.getBytes("ISO-8859-1"),"GBK");
			return  new String(input.getBytes(), "GB2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return input;
		}
	}


	/**
	 * Low case string for pojo.
	 *
	 * @param source the source
	 * @return the string
	 */
	public static String lowcaseStringForPojo(String source){
		return lowcaseString(source, 3);
	}

	/**
	 * Lowcase string.
	 *
	 * @param source the source
	 * @param count the count
	 * @return the string
	 */
	public static String lowcaseString(String source, int count){
		if(source == null) {
            return null;
        }

		if(source.length() <=3){
			return source.toLowerCase();
		}else{
			return source.substring(0,  count).toLowerCase() + source.substring(count,  source.length());
		}
	}

	/**
	 * Gets the string for pojo class name.
	 * Main Method for all Class Name, File Name
	 *
	 * @param source the source
	 * @return the string for pojo class name
	 */
	public static String getStringForPojoClassName(Object source){
		if(source == null) {
            return null;
        }
		return StringUtils.capitalize(getStringVal(source));
	}

	public static String getStringVal(Object source){
		if(source != null) {
            return source.toString().trim().replace(" ", "")
                    .replace("_", "").replace("-", "").replace(".", "");
        }
		return null;
	}

	public static String getStrValue(Object val){
		if(val == null) {
            return null;
        }
		return val.toString().trim();
	}

	public static Double getDoubleValue(Object val){
		if(val == null) {
            return null;
        }
		return Double.valueOf(val.toString());
	}

	public static Integer getIntValue(Object val){
		if(val == null) {
            return null;
        }
		return Integer.valueOf(val.toString());
	}

	public static Long getLongValue(Object val){
		if(val == null) {
            return null;
        }
		return Long.valueOf(val.toString());
	}

	public static Boolean getBooleanValue(Object val){
		if(val == null) {
            return false;
        }

		String valStr = val.toString();
		if("Y".equalsIgnoreCase(valStr) || "YES".equalsIgnoreCase(valStr) || "T".equalsIgnoreCase(valStr) || "TRUE".equalsIgnoreCase(valStr)){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 解析变量内容
	 * @param source
	 * @param prefix
	 * @param suffix
	 * @return
	 */
	public static String parseVariable(String source, String prefix, String suffix){
		if(isBlank(source)) {
            return source;
        }

		if(source.indexOf(prefix)>=0){
			return source.substring(source.indexOf(prefix) + prefix.length(), source.indexOf(suffix));
		}else{
			return source;
		}
	}

	public static String parseVariable(String source){
		return parseVariable(source, DEFAULT_VARIABLE_PREFIX, DEFAULT_VARIABLE_SUFFIX);
	}

	public static boolean containVariable(String source, String prefix){
		return ((source!=null) && (source.indexOf(prefix)>=0));
	}

	public static boolean containVariable(String source){
		return containVariable(source, DEFAULT_VARIABLE_PREFIX);
	}

	public static String replaceVariableValue(String source, String variableName, String prefix, String suffix, String targetValue){
		if(source == null || targetValue == null || variableName == null) {
            return source;
        }

		return source.replace(prefix + variableName + suffix, targetValue);
	}

	public static String replaceVariableValue(String source, String variableName, String targetValue){
		return replaceVariableValue(source, variableName, DEFAULT_VARIABLE_PREFIX, DEFAULT_VARIABLE_SUFFIX, targetValue);
	}

	/**
	 * 获取指定字符串中的纯数字和字母(空格，下划线, 横线, 点号, 逗号，分号都会被删除)
	 * @param source
	 * @return
	 */
	public static String getAlphaNumericStringVal(Object source){
		if(source != null) {
            return source.toString().replaceAll("[^A-Za-z0-9]", "");
        }
		return null;
	}

	/**
	 * 将字符转为驼峰式命名规范
	 * @param source
	 * @return
	 */
	public static String getCamelNamingString(Object source){
		if(source == null) {
            return null;
        }

		String[] buf = StringUtils.split(source.toString().toLowerCase(), "_");
		StringBuilder result = new StringBuilder();

		for(String seg : buf){
			result.append(StringUtils.capitalize(seg));
		}
		return result.toString();
	}

	/**
	 * Split string to list.
	 *
	 * @param sourceString the source string
	 * @param delimiter the delimiter
	 * @param lowcase the lowcase
	 * @return the list
	 */
	public static List<String> splitStringToList(String sourceString, boolean lowcase, String delimiter){
		if(StringUtils.isEmpty(sourceString)) {
            return null;
        }

		List<String> result = new ArrayList<String>();
		for(String name: StringUtils.split(sourceString, delimiter)){
			if(StringUtils.isNotBlank(name)){
				if(lowcase){
					result.add(name.trim().toLowerCase());
				}else{
					result.add(name.trim());
				}
			}
		}

		return result;
	}

	public static List<String> splitStringToList(String sourceString, boolean lowcase){
		return splitStringToList(sourceString, lowcase, DEFAULT_DELIMITER);
	}

	/**
	 * Strip off xmlc data mark.
	 *
	 * @param orgMsg the org msg
	 */
	public static String stripOffXMLCDataMark(String orgMsg){
		if(orgMsg == null) {
            return orgMsg;
        }

		if(orgMsg.contains("&lt;![CDATA[")){
			return StringUtils.substringBetween(orgMsg, "&lt;![CDATA[", "]]&gt;");
		}else if(orgMsg.contains("<![CDATA[")){
			return StringUtils.substringBetween(orgMsg, "<![CDATA[", "]]>");
		}
		return orgMsg;
	}

	/**
	 * Gets the convert collection to string.
	 *
	 * @param source the source
	 * @param delimiter the delimiter
	 * @return the convert collection to string
	 */
	public static String getConvertCollectionToString(Collection<?> source, String delimiter, String prefix){
		StringBuilder buf = new StringBuilder();

		// Loop using Entry Set in case is LinkedHashMap. KeySet<Set> is not sorted list
		if(source!= null){
			for(Object en : source){
				if(en == null) {
                    continue;
                }
				if(buf.length() >0) {
                    buf.append(delimiter);
                }
				if(prefix!=null) {
                    buf.append(prefix);
                }
				buf.append(en.toString());
			}
		}

		return buf.toString();
	}

	/**
	 * Gets the convert collection to string.
	 *
	 * @param source the source
	 * @return the convert collection to string
	 */
	public static String getConvertCollectionToString(Collection<?> source){
		return getConvertCollectionToString(source, DEFAULT_DELIMITER, null);
	}

	/**
	 * Dump column map.
	 *
	 * @param prefix the prefix
	 * @return the string
	 */
	public static String dumpMapValue(Map<?, ?> sourceMap, String prefix, String title, boolean keyOnly){
		StringBuilder buf = new StringBuilder();

		if(sourceMap!= null){
			int i=1;
			buf.append(String.format("%s%s (Count=%s)", prefix, title, sourceMap.size()));

			for(Map.Entry<?, ?> en : sourceMap.entrySet()){
				if(keyOnly){
					buf.append(String.format("%s\t(%s). %s", prefix, i++, en.getKey()));
				}else{
					buf.append(String.format("%s\t(%s). %s=%s", prefix, i++, en.getKey(), en.getValue()));
				}
			}
		}else{
			buf.append(String.format("%s%s is null", prefix, title));
		}

		return buf.toString();
	}

	/**
	 * Dump map value.
	 *
	 * @param sourceMap the source map
	 * @param prefix the prefix
	 * @param title the title
	 * @return the string
	 */
	public static String dumpMapValue(Map<?, ?> sourceMap, String prefix, String title){
		return dumpMapValue(sourceMap, prefix, title, false);
	}

	/**
	 * Dump Array Value into Single String
	 * @param objects
	 * @return
	 */
	static public String dumpArrayValue(Object[] objects){
		if(objects == null || objects.length <1) {
            return null;
        }

		StringBuilder result = new StringBuilder("{");
		for(Object o : objects){
			if(result.length() > 2) {
                result.append(", ");
            }
			result.append(String.format("%s", (o!=null)?o.toString():null));
		}

		result.append("}");
		return result.toString();
	}

	/**
	 * @return
	 */
	static public String UUIDString(){
		return UUID.randomUUID().toString();
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	End: 字符串操作 相关方法
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	Begin: Date 相关方法
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 * @param source
	 * @return
	 */
	public static java.sql.Date converToSqlDate(Date source){
		if(source == null) {
            return null;
        } else {
            return new java.sql.Date(source.getTime());
        }
	}

	/**
	 * Days between.
	 *
	 * @param smdate the smdate
	 * @param bdate the bdate
	 * @return the int
	 * @throws ParseException the parse exception
	 */
	public static int daysBetween(Date smdate, Date bdate){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		try {
			smdate=sdf.parse(sdf.format(smdate));
			bdate=sdf.parse(sdf.format(bdate));
		} catch (ParseException e) {
			return -1* Integer.MIN_VALUE;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(smdate);

		long time1 = cal.getTimeInMillis();
		cal.setTime(bdate);

		long time2 = cal.getTimeInMillis();
		long between_days=(time2-time1)/(1000*3600*24);

		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * Gets the calender.
	 *
	 * @param year the year
	 * @param month the month
	 * @param date the date
	 * @return the calender
	 */
	public static GregorianCalendar getCalender(Integer year, Integer month, Integer date){
		GregorianCalendar gc = new GregorianCalendar();
		gc.setLenient(false);
		if(year != null) {
            gc.set(GregorianCalendar.YEAR, year);
        }
		if(month != null){
			gc.set(GregorianCalendar.MONTH, month-1);
		}else{
			gc.set(GregorianCalendar.MONTH, GregorianCalendar.JANUARY);
		}
		if(date != null){
			gc.set(GregorianCalendar.DATE, date);
		}else{
			gc.set(GregorianCalendar.DATE, 1);
		}

		return gc;
	}

	/**
	 * Gets the calender.
	 *
	 * @param year the year
	 * @return the calender
	 */
	public static GregorianCalendar getCalender(Integer year){
		return getCalender(year, null, null);
	}

	/**
	 * Gets the calender.
	 *
	 * @param year the year
	 * @param month the month
	 * @return the calender
	 */
	public static GregorianCalendar getCalender(Integer year, Integer month){
		return getCalender(year, month, null);
	}

	/**
	 * Gets the calender by integer value.
	 *
	 * @param source the source
	 * @return the calender by integer value
	 */
	public static GregorianCalendar getCalenderByIntegerValue(Integer source){
		if(source == null) {
            return null;
        }

		String buf = source.toString();
		switch(buf.length()){
			case 4:	// Input Year only
				return getCalender(source);
			case 6: // Input Year Month Only
				return getCalender(Integer.valueOf(buf.substring(0, 4)), Integer.valueOf(buf.substring(4, 6)));
			case 8: // Input Year Month and Date
				return getCalender(Integer.valueOf(buf.substring(0, 4)), Integer.valueOf(buf.substring(4, 6)), Integer.valueOf(buf.substring(6, 8)));
			default:
				return null;
		}
	}

	/**
	 * @param source
	 * @return
	 */
	public static Date convertIntegerToDate(Integer source){
		if(source == null) {
            return null;
        }
		GregorianCalendar calendar = getCalenderByIntegerValue(source);
		if(calendar != null){
			return calendar.getTime();
		}else{
			return null;
		}
	}

	/**
	 * Gets the date.
	 *
	 * @param year the year
	 * @param month the month
	 * @param day the day
	 * @return the date
	 */
	public static Date getDate(int year, int month, int day){
		if(month < 0 || month >12) {
            return null;
        }
		if(day < 0 || day >31) {
            return null;
        }
		if(month>1) {
            month--;
        }

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);

		return cal.getTime();
	}

	/**
	 * 生成规则: System.currentTimeMillis() + [1-100000之间的随机数] ==> 转换为16进制的字符串
	 * @return
	 */
	public static String getTimeStampWthRamNumHex(){
		String ramNum = Integer.toString(random.nextInt(999));
		ramNum = "000".substring(ramNum.length()) + ramNum;
		return ramNum;
	}

	/**
	 * @param outputFile
	 * @param source
	 */
	public static void serializeObject(File outputFile, Serializable source){
		ObjectOutputStream os = null;
		try {
			os = new ObjectOutputStream(new FileOutputStream(outputFile));
			os.writeObject(source);
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

	public static <T> T DserializeObject(File sourceFile){
		ObjectInputStream os = null;
		try {
			os = new ObjectInputStream(new FileInputStream(sourceFile));
			return (T)os.readObject();
		}catch (Exception ex){
			ex.printStackTrace();
			return null;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	End: Date 相关方法
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


}

package com.youdaigc.util.bean;

import com.youdaigc.util.common.MyStringUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Common Bean Operation methods
 */
public class MyBeanUtilsBean extends PropertyUtilsBean implements Serializable{
	transient static protected final String MAP_SUFFIX = "Map";
	transient static protected final String[] RemovedPojoCalssSuffix = {
			null, // Note: null is required for no replacement condition
			"Model", "model", "Bean", "bean"
	};

	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(MyBeanUtilsBean.class);
	private static final long serialVersionUID = 3819865563393638461L;
	transient final private SimpleDateFormat simpleDateFormat_4 = new SimpleDateFormat("yyyy");
	transient final private SimpleDateFormat simpleDateFormat_6 = new SimpleDateFormat("yyyyMM");
	transient final private SimpleDateFormat simpleDateFormat_8 = new SimpleDateFormat("yyyyMMdd");
	transient final private SimpleDateFormat simpleDateFormat_14 = new SimpleDateFormat("yyyyMMddHHmmSS");

	/**
	 * Global Static Reference for MyBeanUtilsBean
	 */
	transient static private MyBeanUtilsBean myBeanUtilsBean;

	/**
	 * Retrieve Global Static MyBeanUtilsBean Reference
	 * @return
	 */
	public static MyBeanUtilsBean getMyBeanUtils(){
		if(myBeanUtilsBean == null){
			synchronized (""){
				if(myBeanUtilsBean == null) {
					myBeanUtilsBean = new MyBeanUtilsBean();
				}
			}
		}

		return myBeanUtilsBean;
	}

	/**
	 * 使用指定的数据Bean，替换模版中的字符串
	 *
	 * @param sourceTemplateString:  替换的模版
	 * @param replaceHolderTemplate： 查找占位符的模版
	 * @param dataBean : 数据Bean
	 * @return 替换好的字符串
	 *
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 */
	public String replaceTemplateStringHolder(String sourceTemplateString, String replaceHolderTemplate, Object dataBean){
		if(dataBean == null) {
			return sourceTemplateString;
		}

		String replace_name;
		Object replace_value;
		Class propertyType;
		for(PropertyDescriptor propertyDescriptor : getPropertyDescriptors(dataBean)) {
			propertyType = propertyDescriptor.getPropertyType();
			if (propertyType.isPrimitive()
					|| propertyType.equals(String.class)
					|| propertyType.equals(Double.class)
					|| propertyType.equals(Float.class)
					|| propertyType.equals(Integer.class)
					|| propertyType.equals(Long.class)
					|| propertyType.equals(Date.class) ) {
				replace_name = propertyDescriptor.getName();

				// Note: Better to Check the Template String contains target Replace Name before retrieve the actual value from Data Source Bean
				// 			which can avoid overhead the use of "getSimpleProperty" method
				if(!sourceTemplateString.contains(String.format(replaceHolderTemplate, replace_name))) {
					continue;
				}

				try {
					replace_value = getSimpleProperty(dataBean, replace_name);
				}catch (Exception ex){
					logger.error(ex.getMessage(), ex);
					continue;
				}

				sourceTemplateString = sourceTemplateString.replace(
						String.format(replaceHolderTemplate, replace_name),
						(replace_value != null) ? String.valueOf(replace_value) : ""
				);
			}
		}

		return sourceTemplateString;
	}

	@Override
	public void copyProperties(Object destinationObject, Object originalObject)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		copyProperties(destinationObject, originalObject, null);
	}

	/**
	 * Copy properties.
	 * @param destinationObject the destination object
	 * @param originalObject the original object
	 * @param excludedPropertyList the excluded property list
	 * @throws IllegalAccessException the illegal access exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchMethodException the no such method exception
	 */
	public void copyProperties(Object destinationObject, Object originalObject, Set<String> excludedPropertyList)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		copyProperties(destinationObject, originalObject, excludedPropertyList, false, true, false);
	}


	/**
	 * Copy properties. Exclude Mode
	 *
	 * @param destinationObject
	 * @param originalObject
	 * @param propertyList
	 * @param caseSensitive
	 * @param filterNull
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public void copyProperties(Object destinationObject, Object originalObject, Collection<String> propertyList, boolean caseSensitive, boolean filterNull)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		copyProperties(destinationObject, originalObject, propertyList, caseSensitive, true, filterNull);
	}


	/**
	 * Copy properties.
	 *
	 * @param destinationObject the destination object
	 * @param originalObject the original object
	 * @param excludedPropertyList the excluded property list
	 * @param caseSensitive the case sensitive
	 * @param filterNull filter null properties
	 * @throws IllegalAccessException the illegal access exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchMethodException the no such method exception
	 */
	@SuppressWarnings("rawtypes")
	public void copyProperties(Object destinationObject, Object originalObject, Collection<String> excludedPropertyList, boolean caseSensitive, boolean exlcudeMode, boolean filterNull)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (destinationObject == null) {
			throw new IllegalArgumentException("No destination bean specified");
		}
		if (originalObject == null) {
			throw new IllegalArgumentException("No origin bean specified");
		}

		if(excludedPropertyList == null){
			excludedPropertyList = new HashSet<String>();
		}else{
			if(!caseSensitive){
				// If not case sensitive, then convert all propertyName to low case
				Set<String> newList = new HashSet<String>();
				for(String name : excludedPropertyList){
					newList.add(name.toLowerCase());
				}

				excludedPropertyList = newList;
			}
		}

		if (originalObject instanceof DynaBean) {
			DynaProperty[] origDescriptors = ((DynaBean) originalObject).getDynaClass().getDynaProperties();
			for (int i = 0; i < origDescriptors.length; i++) {
				String name = origDescriptors[i].getName();

				if(excludedPropertyList.contains(name.toLowerCase())){
					// Exclude Mode
					if(exlcudeMode) {
						continue;
					}
				}else {
					// Include Mode
					if(!exlcudeMode) {
						continue;
					}
				}

				if (isReadable(originalObject, name) && isWriteable(destinationObject, name)) {
					try {
						Object value = ((DynaBean) originalObject).get(name);
						// 2016.12.05 zhoush:判断值是否为空
						if(filterNull && value == null) {
							continue;
						}
						if (destinationObject instanceof DynaBean) {
							((DynaBean) destinationObject).set(name, value);
						} else {
							setSimpleProperty(destinationObject, name, value);
						}
					} catch (NoSuchMethodException e) {
						if (logger.isDebugEnabled()) {
							logger.debug("Error writing to '" + name + "' on class '" + destinationObject.getClass() + "'", e);
						}
					}
				}
			}
		} else if (originalObject instanceof Map) {
			Iterator entries = ((Map) originalObject).entrySet().iterator();
			while (entries.hasNext()) {
				Map.Entry entry = (Map.Entry) entries.next();
				String name = (String) entry.getKey();
				if(excludedPropertyList.contains(name.toLowerCase())){
					// Exclude Mode
					if(exlcudeMode) {
						continue;
					}
				}else {
					// Include Mode
					if(!exlcudeMode) {
						continue;
					}
				}

				if (isWriteable(destinationObject, name)) {
					try {
						// 2016.12.05 zhoush:判断值是否为空
						if(filterNull && entry.getValue() == null) {
							continue;
						}
						if (destinationObject instanceof DynaBean) {
							((DynaBean) destinationObject).set(name, entry.getValue());
						} else {
							setSimpleProperty(destinationObject, name, entry.getValue());
						}
					} catch (NoSuchMethodException e) {
						if (logger.isDebugEnabled()) {
							logger.debug("Error writing to '" + name + "' on class '" + destinationObject.getClass() + "'", e);
						}
					}
				}
			}
		} else /* if (orig is a standard JavaBean) */{
			PropertyDescriptor[] origDescriptors = getPropertyDescriptors(originalObject);
			for (int i = 0; i < origDescriptors.length; i++) {
				String name = origDescriptors[i].getName();

				if(excludedPropertyList.contains(name.toLowerCase())){
					// Exclude Mode
					if(exlcudeMode) {
						continue;
					}
				}else {
					// Include Mode
					if(!exlcudeMode) {
						continue;
					}
				}

				if (isReadable(originalObject, name) && isWriteable(destinationObject, name)) {
					try {
						Object value = getSimpleProperty(originalObject, name);
						if (destinationObject instanceof DynaBean) {
							((DynaBean) destinationObject).set(name, value);
						} else {
							setSimpleProperty(destinationObject, name, value);
						}
					} catch (NoSuchMethodException e) {
						if (logger.isDebugEnabled()) {
							logger.debug("Error writing to '" + name + "' on class '" + destinationObject.getClass() + "'", e);
						}
					}
				}
			}
		}
	}


	/**
	 * Set the value of the specified simple property of the specified bean,
	 * with no type conversions.
	 *
	 * @param bean Bean whose property is to be modified
	 * @param name Name of the property to be modified
	 * @param value Value to which the property should be set
	 *
	 * @exception IllegalAccessException if the caller does not have
	 *  access to the property accessor method
	 * @exception IllegalArgumentException if <code>bean</code> or
	 *  <code>name</code> is null
	 * @exception IllegalArgumentException if the property name is
	 *  nested or indexed
	 * @exception InvocationTargetException if the property accessor method
	 *  throws an exception
	 * @exception NoSuchMethodException if an accessor method for this
	 *  propety cannot be found
	 */
	@Override
	public void setSimpleProperty(Object bean, String name, Object value)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		// Retrieve the property setter method for the specified property
		PropertyDescriptor descriptor =
				getPropertyDescriptor(bean, name);
		if (descriptor == null) {
			throw new NoSuchMethodException("Unknown property '" + name + "' on class '" + bean.getClass() + "'" );
		}
		Method writeMethod = getWriteMethod(descriptor);
//		Method writeMethod = getWriteMethod(bean.getClass(), descriptor);
		if (writeMethod == null) {
			throw new NoSuchMethodException("Property '" + name + "' has no setter method in class '" + bean.getClass() + "'");
		}

		if(value != null && writeMethod.getParameterTypes() != null && writeMethod.getParameterTypes().length >0) {
			value = convertObjectValue(value, writeMethod.getParameterTypes()[0]);
		}

		super.setSimpleProperty(bean, name, value);
	}


	/**
	 * Gets the property.
	 *
	 * @param bean the bean
	 * @param propertyName the property name
	 * @param caseSensitive the case sensitive
	 * @return the property
	 * @throws IllegalAccessException the illegal access exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchMethodException the no such method exception
	 */
	public Object getProperty(Object bean, String propertyName, boolean caseSensitive) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if(caseSensitive){
			return super.getProperty(bean, propertyName);
		}else{
			PropertyDescriptor[] origDescriptors = getPropertyDescriptors(bean);
			for (int i = 0; i < origDescriptors.length; i++) {
				String name = origDescriptors[i].getName();

				// If Property Name Match the Descriptor Name
				if(name.equalsIgnoreCase(propertyName)){
					if (isReadable(bean, name)) {
						try {
							return getSimpleProperty(bean, name);
						} catch (NoSuchMethodException e) {
							if (logger.isDebugEnabled()) {
								logger.error("Error Getting to '" + propertyName + "' on class '" + bean.getClass() + "'", e);
							}
						}
					}
				}
			}
		}
		return null;
	}

	public Object getPropertyIgnoreCase(Object bean, String propertyName) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return getProperty(bean, propertyName, false);
	}

	/**
	 * Gets the nest property ignore case.
	 * 支持获取嵌套式的Bean取值: classA.list.property1.proerty2....
	 * 	Note: String[] params 参数可以为空, 也可以用来指定List Number, 或Map Key...
	 *
	 * @param bean the Root bean
	 * @param propertyNameList the property name (ClassA.list.property1.proerty2....)
	 * @param params the params
	 * @return the nest property ignore case
	 * @throws IllegalAccessException the illegal access exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchMethodException the no such method exception
	 */
	@SuppressWarnings("rawtypes")
	public Object getNestPropertyIgnoreCase(Object bean, List<String> propertyNameList, Object... params) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if(bean == null) {
			throw new NoSuchMethodException("Lookup Bean is null");
		}
		if(propertyNameList == null) {
			throw new NoSuchMethodException("Lookup Property Name List is null");
		}
		if(propertyNameList.size() < 1) {
			throw new NoSuchMethodException("Lookup Property Name List is Empty");
		}

		if(logger.isDebugEnabled()){
			logger.debug("Lookup Property " + propertyNameList);
		}
		// Note: Need to make copy, otherwise for List of Child, name list will remove from the first round recursive loop.
		List<String> propertyNameListCopy = new LinkedList<String>(propertyNameList);


		// 取出 Property Name List 中的第一位
		String propertyName = propertyNameListCopy.remove(0);
		// 直接从Bean中获取Property的值
		Object childBean = getNestObjectValue(
				getProperty(bean, propertyName, false),
				params
		);
		if(propertyNameListCopy.isEmpty()){
			// 如果剩下的Property List为空，则当前的Property name为最后一个, 直接返回最后获取的Object Value
			return childBean;
		}else{
			// 如果Bean的类型是List, 而且没有指定Additional Parameter,则尝试获List中所有变量值
			if(childBean instanceof Collection){
				List<Object> resultList = new LinkedList<Object>();

				Collection beanListBuf = ((Collection)childBean);
				for(Iterator it = beanListBuf.iterator(); it.hasNext(); ){
					// 作为普通Bean继续递归
					childBean=it.next();
					resultList.add(
							getNestPropertyIgnoreCase(childBean, propertyNameListCopy, params)
					);
				}

				return resultList;
			}else{
				// 作为普通Bean继续递归
				return getNestPropertyIgnoreCase(childBean, propertyNameListCopy, params);
			}
		}
	}

	/**
	 * Gets the nest object value.
	 * 读取List或者Map中的值
	 *
	 * @param bean the bean
	 * @param params the params
	 * @return the nest object value
	 */
	@SuppressWarnings("rawtypes")
	protected Object getNestObjectValue(Object bean, Object... params){
		if(bean == null || params == null || params.length < 1) {
			return bean;
		}

		if(bean instanceof List){
			return ((List)bean).get((Integer)params[0]);
		}else if(bean instanceof Map){
			return ((Map)bean).get(params[0]);
		}

		return bean;
	}

	/**
	 * Gets the nest property ignore case.
	 * 支持获取嵌套式的Bean取值: classA.list.property1.proerty2....
	 *
	 * @param bean the bean
	 * @param propertyName the property name
	 * @param params the params
	 * @return the nest property ignore case
	 * @throws IllegalAccessException the illegal access exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchMethodException the no such method exception
	 */
	public Object getNestPropertyIgnoreCase(Object bean, String propertyName, Object... params) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		// 检查Class中有没有包含嵌套式 ".", "\", "/"
		propertyName = StringUtils.replace(propertyName, "\\", ".");
		propertyName = StringUtils.replace(propertyName, "/", ".");
		if(!propertyName.contains(".")){
			// 如果没有包含嵌套式,则直接使用之前的方法获取Ben中的Property
			return getProperty(bean, propertyName, false);
		}else{
			List<String> lookupPath = MyStringUtils.splitStringToList(propertyName, true, ".");
			return getNestPropertyIgnoreCase(bean, lookupPath, params);
		}
	}


	/**
	 * 转换数据类型
	 *
	 * @param sourceVal
	 * @param targetClass
	 * @param <T>
	 * @return
	 */
	public <T> T convertObjectValue(Object sourceVal, Class<T> targetClass){
		if(sourceVal == null) {
			return null;
		}

		String targetClassName = targetClass.getSimpleName().toLowerCase();

		if(targetClassName.contains("int")) {
			targetClassName = "int";
		}
		String valStr = sourceVal.toString();

		try {
			switch (targetClassName) {
				case "int":
					return (T) Integer.valueOf(Double.valueOf(valStr).intValue());
				case "long":
					return (T) Long.valueOf(Double.valueOf(valStr).longValue());
				case "double":
					return (T) Double.valueOf(valStr);
				case "float":
					return (T) Float.valueOf(Double.valueOf(valStr).floatValue());
				case "date":
					if (sourceVal instanceof Date) {
						return (T) sourceVal;
					} else {
						valStr = valStr.trim().replace("-", "").replace("/", "").replace("\\", "")
								.replace(".", "").replace(":", "").replace(" ", "");
						// 包含时间分隔符
						switch (valStr.length()) {
							case 4:
								return (T)simpleDateFormat_4.parse(valStr);
							case 6:
								return (T)simpleDateFormat_6.parse(valStr);
							case 8:
								return (T)simpleDateFormat_8.parse(valStr);
							case 14:
								return (T)simpleDateFormat_14.parse(valStr);
							default:
								return null;
						}
					}
				default:
					break;
			}
		}catch (Exception ex){
			logger.error(ex.getMessage(), ex);
		}

		return (T) sourceVal;
	}

	/**
	 * 2017.06.06(GWF):
	 * 	该方法用于查找Target Bean中, 第一个可以设置Inject Value的方法, 并将值设置进去.
	 * 		(查找顺序 SetXXX -> AddXXX --> 其他只有一个参数且类型匹配的方法)
	 *
	 * @param targetBean
	 * @param injectValue
	 */
	public boolean injectValue(Object targetBean, Object injectValue) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		if(targetBean == null || injectValue == null) {
			return false;
		}

		String className = injectValue.getClass().getSimpleName();

		PropertyDescriptor propertyDescriptor = getPropertyDescriptor(targetBean, "set" + className);
		if(propertyDescriptor == null){
			propertyDescriptor = getPropertyDescriptor(targetBean, "add" + className);
		}

		if(propertyDescriptor == null){
			Method method;
			for(PropertyDescriptor p : getPropertyDescriptors(targetBean)){
				if((method = p.getWriteMethod()) != null){
					if(method.getTypeParameters() != null && method.getTypeParameters().length ==1
							&& method.getTypeParameters()[0].getClass().equals(injectValue.getClass())){
						propertyDescriptor = p;
						break;
					}
				}
			}
		}

		if(propertyDescriptor != null){
			setSimpleProperty(targetBean, propertyDescriptor.getName(), injectValue);
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 获取Bean中Field的描述信息
	 *
	 * @param sourceClassType
	 * @param fieldName
	 * @param caseSensitive
	 * @return
	 */
	public PropertyDescriptor getPropertyDescriptorByName(Class<?> sourceClassType, String fieldName, boolean caseSensitive){
		if(sourceClassType != null && fieldName != null) {
			boolean result = false;
			try {
				for(PropertyDescriptor propertyDescriptor : getPropertyDescriptors(sourceClassType)){
					if(caseSensitive){
						result = propertyDescriptor.getName().equals(fieldName);
					}else {
						result = propertyDescriptor.getName().equalsIgnoreCase(fieldName);
					}

					if(result) {
						return propertyDescriptor;
					}
				}
			} catch (Exception e) {}
		}

		return null;
	}

	/**
	 * 获取Bean中Field的描述信息
	 *
	 * @param sourceClassType
	 * @param fieldName
	 * @return
	 */
	public PropertyDescriptor getPropertyDescriptorByName(Class<?> sourceClassType, String fieldName){
		return getPropertyDescriptorByName(sourceClassType, fieldName, false);
	}

	/**
	 * 获取Bean中Field的数据类型
	 *
	 * @param sourceClassType
	 * @param fieldName
	 * @param caseSensitive
	 * @return
	 */
	public Class<?> getPropertyDataType(Class<?> sourceClassType, String fieldName, boolean caseSensitive){
		PropertyDescriptor propertyDescriptor = getPropertyDescriptorByName(sourceClassType, fieldName, caseSensitive);
		if(propertyDescriptor != null){
			return propertyDescriptor.getPropertyType();
		}
		return null;
	}

	/**
	 * 获取Bean中Field的数据类型
	 *
	 * @param sourceClassType
	 * @param fieldName
	 * @return
	 */
	public Class<?> getPropertyDataType(Class<?> sourceClassType, String fieldName){
		return getPropertyDataType(sourceClassType, fieldName, false);
	}


	/**
	 * 获取指定的数据类型值
	 *
	 * @param sourceBean
	 * @param fieldName
	 * @param defaultValue
	 * @return
	 */
	public String getPropertyStringValue(Object sourceBean, String fieldName, String defaultValue){
		if(sourceBean == null || fieldName == null) {
			return defaultValue;
		}

		try {
			Object result = getProperty(sourceBean, fieldName);
			if(result != null){
				return result.toString();
			}
		} catch (Exception e) {
		}

		return defaultValue;
	}

	public <T> T getPropertyValueEx(Object sourceBean, String fieldName, Class<T> targetType, T defaultValue){
		if(sourceBean == null || fieldName == null) {
			return defaultValue;
		}

		try {
			Object result = getProperty(sourceBean, fieldName);
			if(result != null){
				if(targetType != null && MyClassHelper.isClassImplInterfaces(result.getClass(), targetType)){
					return targetType.cast(targetType);
				}
			}
		} catch (Exception e) {
		}

		return defaultValue;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	Begin: Utility Method the Help Set Pojo Class into Target Data bean Map Instance
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Retrieve Property Get / Set Method Name by Property Name
	 *
	 * @param sourceDataBean
	 * @param targetClassType
	 * @param suffix
	 * @param asSetMethod : True Return SetMethod Name; False return GetMethod Name
	 * @param <T>
	 * @return
	 */
	public <T> String getPropertyGetSetMethodName(Object sourceDataBean, Class<T> targetClassType, String suffix, boolean asSetMethod) {
		PropertyDescriptor propertyDescriptor = getClassPropertyDescriptor(sourceDataBean, targetClassType, suffix);
		if(propertyDescriptor != null){
			if(asSetMethod){
				if(propertyDescriptor.getWriteMethod() != null) {
					return propertyDescriptor.getWriteMethod().getName();
				}
			}else{
				if(propertyDescriptor.getReadMethod() != null) {
					return propertyDescriptor.getReadMethod().getName();
				}
			}
		}

		return null;
	}

	/**
	 * Retrieve Property From Data Bean
	 *
	 * @param sourceDataBean
	 * @param targetClassType
	 * @param suffix
	 * @param <T>
	 * @return
	 */
	public <T> PropertyDescriptor getClassPropertyDescriptor(Object sourceDataBean, Class<T> targetClassType, String suffix) {
		if (targetClassType == null || sourceDataBean == null) {
			return null;
		}

		PropertyDescriptor propertyDescriptor;
		String mapName = null;

		for (String replaceName : RemovedPojoCalssSuffix) {
			if(replaceName == null) {
				// 1. Try Get Method Name with directly "Target Class Name" + "Map"
				mapName = targetClassType.getSimpleName() + ((suffix == null) ? "" : suffix);
			}else {
				// 2. Try Get Method Name with "Target Class Name"(remove ending Suffix) + "Map"
				mapName = targetClassType.getSimpleName();
				// Remove the Ending Suffix
				if (mapName.endsWith(replaceName)) {
					mapName = mapName.substring(0, mapName.length() - replaceName.length());
				}

				mapName = mapName + ((suffix == null) ? "" : suffix);
			}

			mapName = MyStringUtils.uncapitalize(mapName);
			try {
				if ((propertyDescriptor = getPropertyDescriptor(sourceDataBean, mapName)) != null) {
					return propertyDescriptor;
				}
			}catch (Exception ex){}
		}

		return null;
	}

	/**
	 * Retrieve Child Collection Instance From Data Bean with given Collection item class type
	 *
	 * @param dataBean
	 * @param classType
	 * @param collectionType
	 * @param suffix
	 * @param autoCrete
	 * @param <T>
	 * @param <E>
	 * @return
	 */
	public <T, E> E getChildCollection(Object dataBean, Class<T> classType, Class<E> collectionType, String suffix, boolean autoCrete){
		PropertyDescriptor propertyDescriptor = getClassPropertyDescriptor(dataBean, classType, suffix);
		if(propertyDescriptor == null) {
			return null;
		}

		Object propertyVal;
		E targetCollection = null;
		try {
			if((propertyVal = propertyDescriptor.getReadMethod().invoke(dataBean)) == null){
				// If need to auto create Map Instance
				if(autoCrete) {
					switch (collectionType.getSimpleName().toLowerCase()){
						case "map":
							targetCollection = (E) new LinkedHashMap<>();
							break;
						case "list":
							targetCollection = (E) new ArrayList<>();
							break;
						default:
							targetCollection = collectionType.newInstance();
							break;
					}
					propertyDescriptor.getWriteMethod().invoke(dataBean, targetCollection);
				}
			}else{
				if(MyClassHelper.isClassImplInterfaces(propertyVal.getClass(), collectionType)){
					targetCollection = (E)propertyVal;
				}
			}
		}catch (Exception ex){
			logger.error(ex.getMessage(), ex);
			targetCollection = null;
		}

		return targetCollection;
	}


	/**
	 * Retrieve Map Instance from target Data Bean Instance
	 *
	 * @param dataBean
	 * @param classType
	 * @param suffix
	 * @param autoCrete
	 * @param <T>
	 * @return
	 */
	public <T> Map getChildMap(Object dataBean, Class<T> classType, String suffix, boolean autoCrete){
		return getChildCollection(dataBean, classType, Map.class, suffix, autoCrete);
	}

	public <T> List getChildList(Object dataBean, Class<T> classType, String suffix, boolean autoCrete){
		return getChildCollection(dataBean, classType, List.class, suffix, autoCrete);
	}

	public <E extends Collection, T> void removeAllChildModel(Object dataBean, Class<T> classType, Class<E> collectionType, String suffix) {
		E collection = getChildCollection(dataBean, classType, collectionType, suffix, false);
		if(collection != null){
			collection.clear();
		}
	}

	/**
	 * @return Child Count
	 */
	public <E extends Collection, T> int getChildCount(Object dataBean, Class<T> classType, Class<E> collectionType, String suffix) {
		E collection = getChildCollection(dataBean, classType, collectionType, suffix, false);
		return (collection != null)?collection.size():0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	End: Utility Method the Help Set Pojo Class into target Target Data bean Map Instance
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




	public static void main(String[] arg) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
	}


}

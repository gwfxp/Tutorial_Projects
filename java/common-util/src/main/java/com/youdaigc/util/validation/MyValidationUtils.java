package com.youdaigc.util.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by  on 2017-06-05.
 */
public class MyValidationUtils implements Serializable{
    private static final Logger logger = LoggerFactory.getLogger(MyValidationUtils.class);
    transient static final public SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyyMMdd");
    private static final long serialVersionUID = -3375314228371587151L;

    /**
     * 1､将前面的身份证号码17位数分别乘以不同的系数｡
     * 从第一位到第十七位的系数分别为:7-9-10-5-8-4-2-1-6-3-7-9-10-5-8-4-2｡
     * 2､将这17位数字和系数相乘的结果相加｡
     * 3､用加出来和除以11,看余数是多少?
     * 4､余数只可能有0-1-2-3-4-5-6-7-8-9-10这11个数字｡其分别对应的最后一位身份证的号码为1-0-X -9-8-7-6-5-4-3-2｡
     * 5､通过上面得知如果余数是2,身份证的最后一位号码就是罗马数字x｡如果余数是10,就会在身份证的第18位数字上出现的是2｡
     *
     * @param idNumber
     * @return
     */
    public static Boolean validIdNumber(String idNumber){
        if(logger.isDebugEnabled()) {
            logger.debug(String.format("验证身份证号码： idno=%s;", idNumber));
        }

        //检查位数
        if (idNumber.length() != 18)
        {
            return false;
        }

        //将身份证号码转化为数组,不考虑最后一位
        int[] id = new int[18];

        for (int iCount = 0; iCount < 17; iCount++)
        {
            try{
                id[iCount] = Integer.parseInt(idNumber.substring(iCount, iCount+1));
            }catch(Exception ex){
                return false;
            }
            //info.message = info.message + Convert.ToString(id[iCount]) + " ";
        }
        //系数
        int[] iCoefficient = new int[] { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
        int sum = 0;
        //乘以系数相加
        for (int i = 0; i < iCoefficient.length; i++)
        {
            sum = sum + (id[i] * iCoefficient[i]);
        }
        //取余数
        int remainder = sum % 11;

        //
        String[] codeTable = new String[] { "1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2" };
        String checksum = codeTable[remainder];
        if (idNumber.substring(17, 18).equals(checksum))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 2017.01.13 zhoush:解析身份证中的性别
     * 身份证中的倒数第二位为偶数:女、基数:男;
     * @param idNumber	身份证号码
     * @return	0:未知；1:男；2:女；
     */
    public static int prasingIDNoToSex(String idNumber) {
        if(logger.isDebugEnabled()) {
            logger.debug(String.format("解析身份证中的性别 IDNO=%s;", idNumber));
        }
        // 验证身份证是否有效
        if(validIdNumber(idNumber)) {
            if(Integer.valueOf(idNumber.substring(idNumber.length() - 2, idNumber.length() - 1)) % 2 == 0) {
                return 2;
            } else {
                return 1;
            }
        } else {
            return 0;
        }
    }

    /**
     * 2017.01.13 zhoush:解析身份证中的出生日期
     * @param idNumber 身份证号码
     * @return	Date
     * @throws ParseException
     */
    public static Date prasingIDNoToBthDT(String idNumber) throws ParseException {
        if(logger.isDebugEnabled()) {
            logger.debug(String.format("解析身份证中的出生日期 IDNO=%s;", idNumber));
        }
        // 验证身份证是否有效
        if(validIdNumber(idNumber)) {
            try {
                return outputDateFormat.parse(idNumber.substring(6,14));
            } catch (ParseException e) {
                logger.error(e.getMessage());
                throw e;
            }
        } else {
            return null;
        }
    }

    /**
     * 2017.01.16 zhoush:验证手机号码
     * @param mobNo
     * @return
     */
    public static Boolean isMobileNum(String mobNo){
        if(logger.isDebugEnabled()) {
            logger.debug(String.format("验证手机号码是否正确 mobNo=%s;", mobNo));
        }
//		return mobNo.matches("^((13[0-9])|(14[4,7])|(15[^4,\\d])|(17[6-8])|(18[0-9]))(\\d{8})$");
        return mobNo.matches("^1\\d{10}$");
    }
}

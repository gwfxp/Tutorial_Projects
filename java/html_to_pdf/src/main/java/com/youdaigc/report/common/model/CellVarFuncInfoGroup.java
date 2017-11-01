package com.youdaigc.report.common.model;

import java.io.Serializable;
import java.util.*;

import static com.youdaigc.report.common.model.CellVarFuncInfoGroup.LOOKUP_CHAR.*;

/**
  * Variable/Function Type Name
 *  1. &&{          --> Parent Element Attribute
 *  2. &{           --> Current Element Attribute
 *  3. #{c=         --> Comments
 *  4. %{f=         --> Function
 *  5.              --> Normal Text
  *
 * Created by GaoWeiFeng on 2017-08-29.
 */
public class CellVarFuncInfoGroup implements Serializable {
    private static final long serialVersionUID = 2237348554361511924L;


    public enum LOOKUP_CHAR{
        AND('&'),             // "&"
        PERCENT('%'),        // "%"
        SKIP('\\'),          // "\"
        BRACKET_BGN('{'),   // "{"
        BRACKET_END('}'),   // "}"
        POUND('#');          // "#"


        LOOKUP_CHAR(char value){
            charValue = value;
        }

        char charValue;
    };

    private List<CellVarFuncInfo> infoList;

    static public CellVarFuncInfoGroup parseVarFuncInfo(String source) {
        if(source == null) {
            return null;
        }
        CellVarFuncInfoGroup cellVarFuncInfoGroup = new CellVarFuncInfoGroup();
        cellVarFuncInfoGroup.parseString(source);
        return cellVarFuncInfoGroup;
    }

    public List<CellVarFuncInfo> filterCellInfo(CellVarFuncInfo.VarFunctionType... cellTypes){
        if(infoList == null || infoList.isEmpty()) {
            return null;
        }

        List<CellVarFuncInfo> resultList = new ArrayList<>();
        for(CellVarFuncInfo cellInfo : infoList){
            for(CellVarFuncInfo.VarFunctionType cellType : cellTypes){
                if(cellInfo.getCellType() == cellType){
                    resultList.add(cellInfo);
                    break;
                }
            }
        }

        return resultList;
    }

    /**
     * 检查当前的Cell Vall是否为纯Text
     * @return
     */
    public boolean isPlanTextCell(){
        if(infoList != null && !infoList.isEmpty()){
            for(CellVarFuncInfo cellInfo : infoList){
                if(cellInfo.getCellType() != CellVarFuncInfo.VarFunctionType.PLAIN_TEXT) {
                    return false;
                }
            }
        }

        return true;
    }


    public String parserSubString(char[] souceArray, int startIndex){
        return parserSubString(souceArray, startIndex, BRACKET_END.charValue, SKIP.charValue);
    }

    public String parserSubString(char[] souceArray, int startIndex, char endChar, char skipChar){
        StringBuilder stackBuf= new StringBuilder();

        int brackMatchCnt = 1;   // 需要找到匹配的括号数量
        char preChar, currentChar=0;
        for(int i=startIndex; i<souceArray.length; i++){
            preChar = currentChar;          // 保存前一个字符
            currentChar = souceArray[i];    // 获取当前字符

            // 如果发现存在新的开始符号，则增加需要匹配的括号数量
            if(currentChar == BRACKET_BGN.charValue) {
                brackMatchCnt++;
            }

            // 如果发现存在新的结束符号，则减少需要匹配的括号数量
            if(currentChar == endChar && preChar != skipChar) {
                brackMatchCnt--;
                if (brackMatchCnt <= 0) {
                    // 如果当前是结束字符, 且前一个字符不是忽略符号, 则返回
                    return stackBuf.toString().replace(String.valueOf(skipChar) + String.valueOf(endChar), String.valueOf(endChar));
                }
            }

            // 添加到当前的Stack中
            stackBuf.append(currentChar);
        }

        return stackBuf.toString();
    }

    public void addCellVarFuncInfo(CellVarFuncInfo... cellVarFuncInfo) {
        if(cellVarFuncInfo == null || cellVarFuncInfo.length <1) {
            return;
        }
        if(infoList == null) {
            infoList = new ArrayList<>();
        }
        infoList.addAll(Arrays.asList(cellVarFuncInfo));
    }


    /**
     * 解析字符
     * @param source
     */
    public void parseString(String source) {
        if(source == null || "".equals(source.trim())) {
            return;
        }

        char[] souceArray = source.toCharArray();
        char preChar, currentChar = 0;

        CellVarFuncInfo.VarFunctionType cellType = null;
        CellVarFuncInfo normalTextBuf = null;
        String stackBuf = null, checkStringBuf;
        int pos = 0;
        int startPos, endPos;
        while (pos < souceArray.length) {
            preChar = souceArray[(pos-1)>0?(pos-1):0];          // 前一个字符
            currentChar = souceArray[pos];                      // 获取当前字符

            // 如果当前字符是 "{"
            if (currentChar == BRACKET_BGN.charValue && pos < souceArray.length - 1) {
                // 尝试获取当前字符之后到 "}"为止的所有字符
                pos++; // 进一位, 跳过当前的 "{"
                stackBuf = parserSubString(souceArray, pos);

                // 如果是 &{开始的
                startPos = pos - 2; // 当前的起始位置为 "x{"
                endPos = pos + stackBuf.length() + 1;   // 截至位置 = pos + 截取的字符长度 + 1 ("}")
                if (preChar == AND.charValue) {
                    // 检查是否是 &&{ 开始的
                    if (pos >= 3 && souceArray[pos - 3] == AND.charValue) {
                        cellType = CellVarFuncInfo.VarFunctionType.PARENT_ATTRIBUTE;
                        startPos = pos - 3;
                    } else {
                        cellType = CellVarFuncInfo.VarFunctionType.CURRENT_ATTRIBUTE;
                    }
                } else if (preChar == POUND.charValue) {
                    cellType = CellVarFuncInfo.VarFunctionType.COMMENT;
                } else if (preChar == PERCENT.charValue) {
                    cellType = CellVarFuncInfo.VarFunctionType.FUNCTION;
                }

                // 如果之前存在cellVarFuncInfo，则先设置之前的CellVarFuncInfo的结束位置，然后保存
                if (normalTextBuf != null) {
                    if((checkStringBuf = normalTextBuf.getValues()) != null) {
                        if (checkStringBuf.endsWith("&&")) {
                            checkStringBuf = checkStringBuf.substring(0, checkStringBuf.length() - 2);
                        } else if (checkStringBuf.endsWith("#") || checkStringBuf.endsWith("&") || checkStringBuf.endsWith("%")){
                            checkStringBuf = checkStringBuf.substring(0, checkStringBuf.length() - 1);
                        }
                    }

                    if(checkStringBuf != null && !"".equals(checkStringBuf.trim())){
                        normalTextBuf.setValues(checkStringBuf);
                        addCellVarFuncInfo(normalTextBuf);
                    }
                    normalTextBuf = null;
                }

                // 存入当前截取的块字符串
                addCellVarFuncInfo(
                        new CellVarFuncInfo(cellType, startPos, endPos, stackBuf)
                );

                pos += stackBuf.length() + 1;   // 设置新的起始位置为: pos + 截取的字符长度 + 1 ("}")
            } else {
                if (normalTextBuf == null) {
                    normalTextBuf = new CellVarFuncInfo(CellVarFuncInfo.VarFunctionType.PLAIN_TEXT, pos, pos, String.valueOf(currentChar));
                }else {
                    normalTextBuf.addCharValue(currentChar);
                    normalTextBuf.setEndPosistion(pos); // 更新结束位置为当前位置
                }

                pos++;
            }
        }

        if (normalTextBuf != null && normalTextBuf.getCellType() == CellVarFuncInfo.VarFunctionType.PLAIN_TEXT) {
            addCellVarFuncInfo(normalTextBuf);
        }
    }

    public static void main(String[] args){
        //String str = "#{c=还款次数}&&{each=\"prod : ${prods\\}\"}Test&{var=SZp17i2.ors.RpCnt}%{f=decode,BrCT}-Gao-&&{class=\"${iterStat.odd\\}? 'odd'\"}";
        String str = "#{c=商品Code}&{text=SZp17i2.iproCd}%{f=decode,ProCT}&{text=\"${prod.inStock}? #{true} : #{false}\"}&{if=${user.isAdmin()} == false}-EndText";
//        String str = "#{c=还款日}&{text=${#dates.format(prod.rpstnDt, 'yyyy-MM-dd')}}";
//        String str = "Test Plain String";
        CellVarFuncInfoGroup group = new CellVarFuncInfoGroup();
        group.parseString(str);

        int i=0;
        for(CellVarFuncInfo cellVarFuncInfo : group.getInfoList()){
            System.out.println(String.format("%d). type=%s, value=%s",
                    (i++), cellVarFuncInfo.getCellType().name(), cellVarFuncInfo.getValues())
            );
        }

    }

    public List<CellVarFuncInfo> filterInfoList() {

        return infoList;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CellVarFuncInfoGroup{");
        sb.append("infoList=").append(infoList);
        sb.append('}');
        return sb.toString();
    }

    public List<CellVarFuncInfo> getInfoList() {
        return infoList;
    }

    public void setInfoList(List<CellVarFuncInfo> infoList) {
        this.infoList = infoList;
    }
}

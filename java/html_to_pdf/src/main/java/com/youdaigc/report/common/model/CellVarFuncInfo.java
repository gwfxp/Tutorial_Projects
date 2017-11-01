package com.youdaigc.report.common.model;

import java.io.Serializable;

/**
 * Created by GaoWeiFeng on 2017-08-29.
 */
public class CellVarFuncInfo implements Serializable {
    private static final long serialVersionUID = 3084704257462020310L;

    public enum VarFunctionType {
        FUNCTION("%{", "}"),
        PARENT_ATTRIBUTE("&&{", "}"),
        CURRENT_ATTRIBUTE("&{", "}"),
        COMMENT("#{", "}"),
        PLAIN_TEXT("", "");

        private String prefix;
        private String suffix;

        VarFunctionType(String prefix, String suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getSuffix() {
            return suffix;
        }

        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }

        static public boolean hasVarFunction(String source) {
            if (source == null) return false;

            for (VarFunctionType type : VarFunctionType.values()) {
                if (PLAIN_TEXT.equals(type)) continue;
                if (source.contains(type.getPrefix())) return true;
            }

            return false;
        }
    }

    private int startPosition = -1;
    private int endPosistion = -1;

    /**
     * Variable / Function Type
     */
    private VarFunctionType cellType;

    private String values;


    public CellVarFuncInfo(VarFunctionType varFunctionType, int startPosition, int endPosistion, String values) {
        this.cellType = varFunctionType;
        this.values = values;
        this.startPosition = startPosition;
        this.endPosistion = endPosistion;
    }

    public void addCharValue(char value){
        if(values == null) values = String.valueOf(value);
        else values += String.valueOf(value);
    }

    public String[] getAttribute(){
        String[] result = null;
        if(values != null && values.contains("=")) {
            result = new String[]{
                    values.substring(0, values.indexOf("=")),
                    values.substring(values.indexOf("=") + 1, values.length())
            };
//            if(name) {
//                return values.substring(0, values.indexOf("="));
//            }else{
//                return values.substring(values.indexOf("=") + 1, values.length());
//            }
        }

        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CellVarFuncInfo{");
        sb.append("startPosition=").append(startPosition);
        sb.append(", endPosistion=").append(endPosistion);
        sb.append(", cellType=").append(cellType);
        sb.append(", values=").append(values);
        sb.append('}');
        return sb.toString();
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public int getEndPosistion() {
        return endPosistion;
    }

    public void setEndPosistion(int endPosistion) {
        this.endPosistion = endPosistion;
    }

    public VarFunctionType getCellType() {
        return cellType;
    }

    public void setCellType(VarFunctionType cellType) {
        this.cellType = cellType;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }
}

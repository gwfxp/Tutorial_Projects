package com.youdaigc.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


public class SZP17I2Model implements Serializable {
	private static final long serialVersionUID = -4986607965449857695L;


	// 商品Code: 商品Code
	protected String iproCd;

	// 贷款利率: 贷款利率
	protected Double iintRt;

	// 账户管理費率: 账户管理費率
	protected Double iaccFeeRt;

	// 合约日: 合约日
	protected Date iagrDt;

	// 合约金額: 合约金額
	protected Double iagrAmt;

	// 毎月约定还款日: 毎月约定还款日
	protected Integer idueDay;

	// 毎月偿还金額: 毎月偿还金額
	protected Double idueAmt;

	// 还款期数: 还款期数
	protected Integer irpCnt;

	// 签约最大期限: 签约最大期限
	protected Integer iagrTermMx;

	// 最低还款单位: 最低还款单位
	protected Double iutRpAmt;

	// Return Code: Return Code
	protected Integer oretCd;

	// 签约金额: 签约金额
	protected Double oagrAmt;

	// 结果集: 结果集
	protected List<SZP17I2RSModel> ors;
	

	public String toString(){
		 return "SZP17I2 [" + "iproCd=" + iproCd + ", iintRt=" + iintRt + ", iaccFeeRt=" + iaccFeeRt + ", iagrDt=" + iagrDt + ", iagrAmt=" + iagrAmt + ", idueDay=" + idueDay + ", idueAmt=" + idueAmt + ", irpCnt=" + irpCnt + ", iagrTermMx=" + iagrTermMx + ", iutRpAmt=" + iutRpAmt + ", oretCd=" + oretCd + ", oagrAmt=" + oagrAmt + ", ors=" + ors + "]"; 
	}



	// ////////////////////////////////////////////////////////////////////
	// Get and Set Method
	// ////////////////////////////////////////////////////////////////////


	// 获取： 商品Code: 商品Code
	public String getIproCd(){ return this.iproCd; }

	// 设值： 商品Code: 商品Code
	public void setIproCd(String iproCd){ this.iproCd = iproCd; }

	// 获取： 贷款利率: 贷款利率
	public Double getIintRt(){ return this.iintRt; }

	// 设值： 贷款利率: 贷款利率
	public void setIintRt(Double iintRt){ this.iintRt = iintRt; }

	// 获取： 账户管理費率: 账户管理費率
	public Double getIaccFeeRt(){ return this.iaccFeeRt; }

	// 设值： 账户管理費率: 账户管理費率
	public void setIaccFeeRt(Double iaccFeeRt){ this.iaccFeeRt = iaccFeeRt; }

	// 获取： 合约日: 合约日
	public Date getIagrDt(){ return this.iagrDt; }

	// 设值： 合约日: 合约日
	public void setIagrDt(Date iagrDt){ this.iagrDt = iagrDt; }

	// 获取： 合约金額: 合约金額
	public Double getIagrAmt(){ return this.iagrAmt; }

	// 设值： 合约金額: 合约金額
	public void setIagrAmt(Double iagrAmt){ this.iagrAmt = iagrAmt; }

	// 获取： 毎月约定还款日: 毎月约定还款日
	public Integer getIdueDay(){ return this.idueDay; }

	// 设值： 毎月约定还款日: 毎月约定还款日
	public void setIdueDay(Integer idueDay){ this.idueDay = idueDay; }

	// 获取： 毎月偿还金額: 毎月偿还金額
	public Double getIdueAmt(){ return this.idueAmt; }

	// 设值： 毎月偿还金額: 毎月偿还金額
	public void setIdueAmt(Double idueAmt){ this.idueAmt = idueAmt; }

	// 获取： 还款期数: 还款期数
	public Integer getIrpCnt(){ return this.irpCnt; }

	// 设值： 还款期数: 还款期数
	public void setIrpCnt(Integer irpCnt){ this.irpCnt = irpCnt; }

	// 获取： 签约最大期限: 签约最大期限
	public Integer getIagrTermMx(){ return this.iagrTermMx; }

	// 设值： 签约最大期限: 签约最大期限
	public void setIagrTermMx(Integer iagrTermMx){ this.iagrTermMx = iagrTermMx; }

	// 获取： 最低还款单位: 最低还款单位
	public Double getIutRpAmt(){ return this.iutRpAmt; }

	// 设值： 最低还款单位: 最低还款单位
	public void setIutRpAmt(Double iutRpAmt){ this.iutRpAmt = iutRpAmt; }

	// 获取： Return Code: Return Code
	public Integer getOretCd(){ return this.oretCd; }

	// 设值： Return Code: Return Code
	public void setOretCd(Integer oretCd){ this.oretCd = oretCd; }

	// 获取： 签约金额: 签约金额
	public Double getOagrAmt(){ return this.oagrAmt; }

	// 设值： 签约金额: 签约金额
	public void setOagrAmt(Double oagrAmt){ this.oagrAmt = oagrAmt; }

	// 获取： 结果集: 结果集
	public List<SZP17I2RSModel> getOrs(){ return this.ors; }

	// 设值： 结果集: 结果集
	public void setOrs(List<SZP17I2RSModel> ors){ this.ors = ors; }
}

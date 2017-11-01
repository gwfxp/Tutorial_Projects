package com.youdaigc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SZP17I2RSModel implements Serializable {
	private static final long serialVersionUID = -4986607965449857695L;


	// 还款期数: 还款期数
	protected Integer rpcnt;

	// 偿还日: 偿还日
	protected Date rpstnDt;

	// 扣款日: 扣款日
	protected Date rpdueDt;

	// 偿还金額: 偿还金額
	protected Double rpamt;

	// 利用日数: 利用日数
	protected Integer useIntDay;

	// 利用利息: 利用利息
	protected Double useIntAmt;

	// 账户管理費: 账户管理費
	protected Double useAccFee1;

	// 账户管理費: 账户管理費
	protected Double useAccFee2;

	// 贷款金额移动金额: 贷款金额移动金额
	protected Double mvlnBal;

	// 贷款金额: 贷款金额
	protected Double lnbal;

	// 毎月偿还金額: 毎月偿还金額
	protected Double dueAmt;

	// 提前还款金额: 提前还款金额
	protected Double prpmtAmt;
	

	public String toString(){
		 return "SZP17I2RS [" + "rpcnt=" + rpcnt + ", rpstnDt=" + rpstnDt + ", rpdueDt=" + rpdueDt + ", rpamt=" + rpamt + ", useIntDay=" + useIntDay + ", useIntAmt=" + useIntAmt + ", useAccFee1=" + useAccFee1 + ", useAccFee2=" + useAccFee2 + ", mvlnBal=" + mvlnBal + ", lnbal=" + lnbal + ", dueAmt=" + dueAmt + ", prpmtAmt=" + prpmtAmt + "]"; 
	}


	public static List<String> getFetchNameList(){
		List<String> resultList = new ArrayList<String>();
		resultList.add("rpcnt");  // 还款期数
		resultList.add("rpstnDt");  // 偿还日
		resultList.add("rpdueDt");  // 扣款日
		resultList.add("rpamt");  // 偿还金額
		resultList.add("useIntDay");  // 利用日数
		resultList.add("useIntAmt");  // 利用利息
		resultList.add("useAccFee1");  // 账户管理費
		resultList.add("useAccFee2");  // 账户管理費
		resultList.add("mvlnBal");  // 贷款金额移动金额
		resultList.add("lnbal");  // 贷款金额
		resultList.add("dueAmt");  // 毎月偿还金額
		resultList.add("prpmtAmt");  // 提前还款金额
		return resultList;
	}

	// ////////////////////////////////////////////////////////////////////
	// Get and Set Method
	// ////////////////////////////////////////////////////////////////////


	// 获取： 还款期数: 还款期数
	public Integer getRpcnt(){ return this.rpcnt; }

	// 设值： 还款期数: 还款期数
	public void setRpcnt(Integer rpcnt){ this.rpcnt = rpcnt; }

	// 获取： 偿还日: 偿还日
	public Date getRpstnDt(){ return this.rpstnDt; }

	// 设值： 偿还日: 偿还日
	public void setRpstnDt(Date rpstnDt){ this.rpstnDt = rpstnDt; }

	// 获取： 扣款日: 扣款日
	public Date getRpdueDt(){ return this.rpdueDt; }

	// 设值： 扣款日: 扣款日
	public void setRpdueDt(Date rpdueDt){ this.rpdueDt = rpdueDt; }

	// 获取： 偿还金額: 偿还金額
	public Double getRpamt(){ return this.rpamt; }

	// 设值： 偿还金額: 偿还金額
	public void setRpamt(Double rpamt){ this.rpamt = rpamt; }

	// 获取： 利用日数: 利用日数
	public Integer getUseIntDay(){ return this.useIntDay; }

	// 设值： 利用日数: 利用日数
	public void setUseIntDay(Integer useIntDay){ this.useIntDay = useIntDay; }

	// 获取： 利用利息: 利用利息
	public Double getUseIntAmt(){ return this.useIntAmt; }

	// 设值： 利用利息: 利用利息
	public void setUseIntAmt(Double useIntAmt){ this.useIntAmt = useIntAmt; }

	// 获取： 账户管理費: 账户管理費
	public Double getUseAccFee1(){ return this.useAccFee1; }

	// 设值： 账户管理費: 账户管理費
	public void setUseAccFee1(Double useAccFee1){ this.useAccFee1 = useAccFee1; }

	// 获取： 账户管理費: 账户管理費
	public Double getUseAccFee2(){ return this.useAccFee2; }

	// 设值： 账户管理費: 账户管理費
	public void setUseAccFee2(Double useAccFee2){ this.useAccFee2 = useAccFee2; }

	// 获取： 贷款金额移动金额: 贷款金额移动金额
	public Double getMvlnBal(){ return this.mvlnBal; }

	// 设值： 贷款金额移动金额: 贷款金额移动金额
	public void setMvlnBal(Double mvlnBal){ this.mvlnBal = mvlnBal; }

	// 获取： 贷款金额: 贷款金额
	public Double getLnbal(){ return this.lnbal; }

	// 设值： 贷款金额: 贷款金额
	public void setLnbal(Double lnbal){ this.lnbal = lnbal; }

	// 获取： 毎月偿还金額: 毎月偿还金額
	public Double getDueAmt(){ return this.dueAmt; }

	// 设值： 毎月偿还金額: 毎月偿还金額
	public void setDueAmt(Double dueAmt){ this.dueAmt = dueAmt; }

	// 获取： 提前还款金额: 提前还款金额
	public Double getPrpmtAmt(){ return this.prpmtAmt; }

	// 设值： 提前还款金额: 提前还款金额
	public void setPrpmtAmt(Double prpmtAmt){ this.prpmtAmt = prpmtAmt; }
}

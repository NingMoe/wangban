/**  
 * @Title: RecordQueryThreadPool.java 
 * @Package app.icity.project 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author  chenzhiye
 * @date 2013-10-29 下午6:00:04 
 * @version V1.0  
 */ 
package app.icity.project;

import com.icore.core.ThreadPoolBean;
import com.icore.core.ThreadPoolManager;
import com.inspur.bean.ParameterSet;

/** 
 * @ClassName: RecordQueryThreadPool 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author  chenzhiye
 * @date 2013-10-29 下午6:00:04  
 */

public class RecordQueryThreadPool extends ThreadPoolBean {
	
	String CXBH;
	String XZQH;
	String CXLYDM;
	String YWSBLSH;
	String BCXZZJGDM;
	java.sql.Timestamp CXSJ;
	String SQR;
	String YWBLJG;
	String BLJG;
	String BZ;
	String BYZD;
	
	public RecordQueryThreadPool(String YWSBLSH, String SQR, String BCXZZJGDM, String YWBLJG, String BLJG){
		this.YWSBLSH=YWSBLSH;
		this.SQR=SQR;
		this.BCXZZJGDM=BCXZZJGDM;
		this.YWBLJG=YWBLJG;
		this.BLJG=BLJG;
	}
	 
	@Override
	public boolean handler() {
		ParameterSet pSet = new ParameterSet();
		pSet.setParameter("YWSBLSH", YWSBLSH);
		pSet.setParameter("SQR", SQR);
		pSet.setParameter("BCXZZJGDM", BCXZZJGDM);
		pSet.setParameter("YWBLJG", YWBLJG);
		pSet.setParameter("BLJG", BLJG);
		RecordQueryThreadPoolDao.getInstance().insert(pSet);
		return false;
	}
	
	/**
	 * @return the cXBH
	 */
	public String getCXBH() {
		return CXBH;
	}

	/**
	 * @param cXBH the cXBH to set
	 */
	public void setCXBH(String cXBH) {
		CXBH = cXBH;
	}

	/**
	 * @return the xZQH
	 */
	public String getXZQH() {
		return XZQH;
	}

	/**
	 * @param xZQH the xZQH to set
	 */
	public void setXZQH(String xZQH) {
		XZQH = xZQH;
	}

	/**
	 * @return the cXLYDM
	 */
	public String getCXLYDM() {
		return CXLYDM;
	}

	/**
	 * @param cXLYDM the cXLYDM to set
	 */
	public void setCXLYDM(String cXLYDM) {
		CXLYDM = cXLYDM;
	}

	/**
	 * @return the yWSBLSH
	 */
	public String getYWSBLSH() {
		return YWSBLSH;
	}

	/**
	 * @param yWSBLSH the yWSBLSH to set
	 */
	public void setYWSBLSH(String yWSBLSH) {
		YWSBLSH = yWSBLSH;
	}

	/**
	 * @return the bCXZZJGDM
	 */
	public String getBCXZZJGDM() {
		return BCXZZJGDM;
	}

	/**
	 * @param bCXZZJGDM the bCXZZJGDM to set
	 */
	public void setBCXZZJGDM(String bCXZZJGDM) {
		BCXZZJGDM = bCXZZJGDM;
	}

	/**
	 * @return the cXSJ
	 */
	public java.sql.Timestamp getCXSJ() {
		return CXSJ;
	}

	/**
	 * @param cXSJ the cXSJ to set
	 */
	public void setCXSJ(java.sql.Timestamp cXSJ) {
		CXSJ = cXSJ;
	}

	/**
	 * @return the sQR
	 */
	public String getSQR() {
		return SQR;
	}

	/**
	 * @param sQR the sQR to set
	 */
	public void setSQR(String sQR) {
		SQR = sQR;
	}

	/**
	 * @return the yWBLJG
	 */
	public String getYWBLJG() {
		return YWBLJG;
	}

	/**
	 * @param yWBLJG the yWBLJG to set
	 */
	public void setYWBLJG(String yWBLJG) {
		YWBLJG = yWBLJG;
	}

	/**
	 * @return the bLJG
	 */
	public String getBLJG() {
		return BLJG;
	}

	/**
	 * @param bLJG the bLJG to set
	 */
	public void setBLJG(String bLJG) {
		BLJG = bLJG;
	}

	/**
	 * @return the bZ
	 */
	public String getBZ() {
		return BZ;
	}

	/**
	 * @param bZ the bZ to set
	 */
	public void setBZ(String bZ) {
		BZ = bZ;
	}

	/**
	 * @return the bYZD
	 */
	public String getBYZD() {
		return BYZD;
	}

	/**
	 * @param bYZD the bYZD to set
	 */
	public void setBYZD(String bYZD) {
		BYZD = bYZD;
	}

	@Override
	public void errorHandler() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String descript() {
		// TODO Auto-generated method stub
		return "记录"+YWSBLSH+"信息";
	}
	
	

}

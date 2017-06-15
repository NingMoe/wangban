package app.db.bean;

import java.util.List;

public class ProxyBean{
	private String dataSource;
	private String name;
	private String title;
	private String sql;
	private List<ParameterBean> parameters;
	private List<ParameterBean> constants;
	private String other;
	
	
	/**
	 * @return dataSource
	 */
	public String getDataSource() {
		return dataSource;
	}
	/**
	 * @param dataSource 要设置的 dataSource
	 */
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name 要设置的 name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title 要设置的 title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return sql
	 */
	public String getSql() {
		return sql;
	}
	/**
	 * @param sql 要设置的 sql
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}
	/**
	 * @return parameters
	 */
	public List<ParameterBean> getParameters() {
		return parameters;
	}
	/**
	 * @param parameters 要设置的 parameters
	 */
	public void setParameters(List<ParameterBean> parameters) {
		this.parameters = parameters;
	}
	/**
	 * @return constants
	 */
	public List<ParameterBean> getConstants() {
		return constants;
	}
	/**
	 * @param constants 要设置的 constants
	 */
	public void setConstants(List<ParameterBean> constants) {
		this.constants = constants;
	}
	/**
	 * @return other
	 */
	public String getOther() {
		return other;
	}
	/**
	 * @param other 要设置的 other
	 */
	public void setOther(String other) {
		this.other = other;
	}
	
}

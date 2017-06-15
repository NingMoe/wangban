package app.db.bean;

public class ParameterBean{
	private String name;
	private String type;
	private Boolean must;
	private String def;
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
	 * @return type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type 要设置的 type
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return must
	 */
	public Boolean getMust() {
		return must;
	}
	/**
	 * @param must 要设置的 must
	 */
	public void setMust(Boolean must) {
		this.must = must;
	}
	/**
	 * @return def
	 */
	public String getDef() {
		return def;
	}
	/**
	 * @param def 要设置的 def
	 */
	public void setDef(String def) {
		this.def = def;
	}
}

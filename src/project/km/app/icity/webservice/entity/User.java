package km.app.icity.webservice.entity;
/**
 * 
 * ClassName: User <br/>
 * date: 2015年10月14日 上午10:57:49 <br/>
 * @author lw
 */
public class User {
	private int id;
	private String name;
	private String address;
	private String email;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}

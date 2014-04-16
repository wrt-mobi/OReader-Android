package mobi.wrt.oreader.app.clients;

import java.io.Serializable;


public class Profile implements Serializable {

	public static enum Sex {
		MALE, FEMALE, UNKNOWN;
	}

	private static final long serialVersionUID = -8110790846919273420L;

	private String id;
	
	private String firstName;
	
	private String lastName;
	
	private String nickname;
	
	private Sex sex = Sex.UNKNOWN;
	
	private Long birthday;
	
	private String email;
	
	private String mobilePhone;
	
	private String photo;

	private AuthManagerFactory.Type type;

	private String token; 
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public Long getBirthday() {
		return birthday;
	}

	public void setBirthday(Long birthday) {
		this.birthday = birthday;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public AuthManagerFactory.Type getType() {
		return type;
	}

	public void setType(AuthManagerFactory.Type type) {
		this.type = type;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
}

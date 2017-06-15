package app.pmi.validation;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Response<T> implements Serializable{
	
	//成功标识
	public static final int SUCCESS = 0;
	
	public static final int BUSINESS_ERROR = -1;
	
	/** 返回状态码 0 为成功   负数为失败 */
	private int code = 0;
	/** 返回错误消息 **/
	private String message = "";
	/** 返回的结果对象 */
	private T data;
	/** sessionId */
	private String sessionId = "";
	
	public Response(){
		
	}
	
	/**
	 * @param code
	 * @param message
	 */
	public Response(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "Response [code=" + code + ", message=" + message + ", data="
				+ data + ", sessionId=" + sessionId + "]";
	}
	
	public static <T>Response<T> newInstance(){
		Response<T> rp = new Response<T>();
		return rp;
	}
}
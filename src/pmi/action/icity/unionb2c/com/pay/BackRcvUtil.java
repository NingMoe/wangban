package action.icity.unionb2c.com.pay;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import net.sf.json.JSONObject;

import com.icore.http.util.HttpUtil;
import com.inspur.util.Command;
import com.inspur.util.PathUtil;
import com.inspur.util.SecurityConfig;

import core.util.HttpClientUtil;
/**
 * 缴费成功后 反馈 博思 外网
 * 重庆缴费使用
 * @author yanhao
 *
 */
public class BackRcvUtil {
	//缴款成功回调博思确认缴款接口
	@SuppressWarnings("deprecation")
	public  String bosiConfirmPay(String payCode,String totalAmount) {
		//TOKEN、TIMESTAMP、NONCE、SRC
		Date date = new Date();
		String token = "CHQPAY";
		String timestamp = String.valueOf(date.getTime());
		String nonce = generateString(32);
		String src = "INSPURICITY";
		String signature = "";
		StringBuffer signatureBuffer = new StringBuffer();
		//字典排序并加密
		String [] tmpArr  = new String []{token,timestamp,nonce};
		Arrays.sort(tmpArr);
		for(int i=0;i<3;i++){
			signatureBuffer.append(tmpArr[i]);
		}
		signature = signatureBuffer.toString();
		signature = SHA(signature, "SHA-256");
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formatter1 = new SimpleDateFormat("HH:mm:ss");
	    String cfmDate = formatter.format(new Date()); 
	    String cfmTime = formatter1.format(new Date()); 
		
		JSONObject param = new JSONObject();
	    param.put("payCode", payCode);
		param.put("totalAmount", totalAmount);
		param.put("cfmDate", cfmDate);
		param.put("cfmTime", cfmTime);
		
		String url = HttpUtil.formatUrl(SecurityConfig.getString("bosiConfirmPayUrl"));
		url = url+"?timestamp="+timestamp+"&nonce="+nonce+"&signature="+signature+"&src="+src;
		
		HttpClientUtil client = new HttpClientUtil();
		String str = client.getResult(url,param.toString(),true);
		printLog("博思："+str);
		return str;
	}
	
	//缴款成功更新外网数据状态及推送审批状态
	public void updateBillState(String orderId){
		Command cmd = new Command("app.icity.project.ProjectIndexCmd");
		cmd.setParameter("ORDERID",orderId);
		cmd.execute("updateBillState");
	};
	
	//缴款成功与失败 向数据库记录缴费日志
		public void updateBillLog(String orderId,String logStr){
			Command cmd = new Command("app.icity.project.ProjectIndexCmd");
			cmd.setParameter("ORDERID",orderId);
			cmd.setParameter("LOG",logStr);
			cmd.execute("updateBillLog");
		};
	
	//生成32位数字和字母
	public String generateString(int length) { 
		
		String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyz";
		
        StringBuffer sb = new StringBuffer();  
        SecureRandom random = new SecureRandom();  
        for (int i = 0; i < length; i++) {  
            sb.append(ALLCHAR.charAt(random.nextInt(ALLCHAR.length())));  
        }  
        return sb.toString();  
    } 
	
	/**
	 * 字符串 SHA 加密
	 * 
	 * @param strSourceText
	 * @return
	 */
	private String SHA(final String strText, final String strType) {
		// 返回值
		String strResult = null;

		// 是否是有效字符串
		if (strText != null && strText.length() > 0) {
			try {
				// SHA 加密开始
				// 创建加密对象 并傳入加密類型
				MessageDigest messageDigest = MessageDigest
						.getInstance(strType);
				// 传入要加密的字符串
				messageDigest.update(strText.getBytes());
				// 得到 byte 類型结果
				byte byteBuffer[] = messageDigest.digest();

				// 將 byte 轉換爲 string
				StringBuffer strHexString = new StringBuffer();
				// 遍歷 byte buffer
				for (int i = 0; i < byteBuffer.length; i++) {
					String hex = Integer.toHexString(0xff & byteBuffer[i]);
					if (hex.length() == 1) {
						strHexString.append('0');
					}
					strHexString.append(hex);
				}
				// 得到返回結果
				strResult = strHexString.toString();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}

		return strResult;
	}
	
	public boolean printLog(String filecontent) {
		boolean bool = false;
		@SuppressWarnings("deprecation")
		String filenameTemp = PathUtil.getWebPath() + "log_CCB.txt";
		File file = new File(filenameTemp);
		try {
			// 如果文件不存在，则创建新的文件
			if (!file.exists()) {
				bool = file.createNewFile();
				// 创建文件成功后，写入内容到文件里
				writeFileContent(filenameTemp, filecontent);
			} else {
				writeFileContent(filenameTemp, filecontent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bool;
	}
	/**
	 * 向文件中写入内容
	 * 
	 * @param filepath
	 *            文件路径与名称
	 * @param newstr
	 *            写入的内容
	 * @return
	 * @throws IOException
	 */
	public static boolean writeFileContent(String filepath, String newstr)
			throws IOException {
		Boolean bool;
		String filein = newstr + "\r\n";// 新写入的行，换行
		String temp = "";
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		FileOutputStream fos = null;
		PrintWriter pw = null;
		try {
			File file = new File(filepath);// 文件路径(包括文件名称)
			// 将文件读入输入流
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis);
			StringBuffer buffer = new StringBuffer();
			br = new BufferedReader(isr);
			try {
			// 文件原有内容
			for (;(temp = br.readLine()) != null;) {
				buffer.append(temp);
				// 行与行之间的分隔符 相当于“\n”
				buffer = buffer.append(System.getProperty("line.separator"));
			}
			}finally{
				br.close();
			}
			buffer.append(filein);
			fos = new FileOutputStream(file);
			pw = new PrintWriter(fos);
			pw.write(buffer.toString().toCharArray());
			pw.flush();
			bool = true;
		}finally {
			// 不要忘记关闭
			if (pw != null) {
				pw.close();
			}
			if (fos != null) {
				fos.close();
			}
			if (br != null) {
				br.close();
			}
			if (isr != null) {
				isr.close();
			}
			if (fis != null) {
				fis.close();
			}
		}
		return bool;
	}
}

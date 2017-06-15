package app.icity.govservice;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;




import net.sf.json.JSONArray;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.BASE64Decoder;
import app.icity.project.ProjectBusinessStatDao;

import com.icore.util.DaoFactory;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.Tools;

public class AESDao extends BaseJdbcDao {
	private static Logger log = LoggerFactory.getLogger(ProjectBusinessStatDao.class);
	private static String icityDataSource = "icityDataSource";
	private AESDao() {
		this.setDataSourceName("icityDataSource");
	}
	public static AESDao getInstance() {
		return DaoFactory.getDao(AESDao.class.getName());
	}
	
	//AES加解密 用于太和县解密讯飞传入的url参数
	public DataSet aes(ParameterSet pSet){
		DataSet ds = new DataSet();
		String[] array=new String[2];
		JSONArray jsa = new JSONArray();
		String iv = "0123456789abcdef";
        /*
         * 此处使用AES/CBC/128/NoPadding加密模式，key需要为16位。
         */
        
        // 需要加密的字串
        String account = pSet.getParameter("username").toString();
        String pwd = pSet.getParameter("password").toString();
        String expireTime = pSet.getParameter("expireTime").toString();
        String cKey = expireTime+"SPUR";
        // 加密
        //String enString = AESDao.encrypt(account, cKey, iv);
 
        // 解密
		try {
			account = AESDao.Decrypt(account, cKey, iv).trim();
			pwd = AESDao.Decrypt(pwd, cKey, iv).trim();
			jsa.add(0, account);
			jsa.add(1, pwd);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ds.setData(Tools.stringToBytes(jsa.toString()));
		return ds;
	}
	

    // 加密
	public static String encrypt(String data, String key, String iv) {
		try {

			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			int blockSize = cipher.getBlockSize();

			byte[] dataBytes = data.getBytes();
			int plaintextLength = dataBytes.length;
			if (plaintextLength % blockSize != 0) {
				plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
			}

			byte[] plaintext = new byte[plaintextLength];
			System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

			cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
			byte[] encrypted = cipher.doFinal(plaintext);
			return new String(new Base64().encode(encrypted));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
 
    // 解密
    public static String Decrypt(String sSrc, String sKey, String iv) throws Exception {
    	try
        {
            
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);
            
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keyspec = new SecretKeySpec(sKey.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
            
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
 
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original);
            return originalString;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
 
}
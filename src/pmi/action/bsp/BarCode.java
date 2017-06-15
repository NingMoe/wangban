/** 
 * 支持EAN13, EAN8, UPCA, UPCE, Code 3 of 9, Codabar, Code 11, Code 93, Code 128, MSI/Plessey, Interleaved 2 of PostNet等
 * 利用jbarcode生成各种条形码
 */  
package action.bsp;
import java.awt.image.BufferedImage;  
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.imageio.ImageIO;

import org.jbarcode.JBarcode;
import org.jbarcode.encode.Code128Encoder;
import org.jbarcode.paint.BaseLineTextPainter;
import org.jbarcode.paint.WidthCodedPainter;

import com.icore.http.util.HttpUtil;
import com.icore.util.StringUtil;
import com.inspur.base.BaseAction;
import com.inspur.http.util.UrlHelper;

public class BarCode extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean handler(Map<String, Object> data) throws Exception {
		String fileUrl = this.getParameter("fileUrl");
		/*
		 * if(org.apache.commons.lang.StringUtils.isEmpty(fileUrl)){
		 * getHttpAdapter().setStatus(HttpResponseStatus.NOT_FOUND); throw new
		 * Exception("访问的地址不符合规范."); }
		 */

		// 是否产生图片附件，默认true
		String genImg = (String) this.getParameter("genImg");
		if (StringUtil.isEmpty(genImg)) {
			genImg = "true";
		}

		//String header = this.getHttpAdapter().getHeader("Host");
		String content = fileUrl;
		//content = HttpUtil.formatUrl(content);
		// 输出的字节
		//byte[] outByte = null;

		// 文件属性
		String fileName = getMD5Str(content) + content.hashCode();
		String filePath = getImgPath();// 文件夹路径
		String imgPath = filePath + File.separator + fileName + ".png"; // 文件路径
		File file = new File(filePath);
		File imgFile = new File(imgPath);

		// 文件夹是否存在，不存在创建文件夹
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}

		// 如果文件存在，直接输出文件数据
		if (!imgFile.exists()) {
			// 生成条形码
			JBarcode jb = new JBarcode(Code128Encoder.getInstance(),WidthCodedPainter.getInstance(),BaseLineTextPainter.getInstance());  
			jb.setShowText(false);//是否显示图片下字符串内容
			jb.setCheckDigit(false);
			jb.setShowCheckDigit(false);
			//jb.setShowCheckDigit(true);//显示字符串内容中是否显示检查码内容
			//jb.setCheckDigit(false);//不生成检查码
			//jb.setEncoder(null);//设置类型
			//jb.setBarHeight();
			
			BufferedImage localBufferedImage = jb.createBarcode(content);
			// 根据参数判断是否生成文件
			if ("true".equals(genImg)) {
				File newFile = new File(imgPath);
				try {
					ImageIO.write(localBufferedImage, "png", newFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// 将条形码转换为字节输出
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(localBufferedImage, "png", out);
			//outByte = out.toByteArray();
		} /*else {
			outByte = Tools.getFile(imgFile);
		}*/

		this.write(HttpUtil.formatUrl(changeG(imgPath)));
		this.setContentType("image/jpeg");
		return false;
	}

	private String changeG(String str) {
		str = str.replace("\\", "/");
		return str;
	}
	/**
	 * MD5 加密
	 */
	private String getMD5Str(String str) {
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance("MD5");

			messageDigest.reset();

			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		StringBuffer md5StrBuff = new StringBuffer();
		if (messageDigest != null) {
			byte[] byteArray = messageDigest.digest();
			for (int i = 0; i < byteArray.length; i++) {
				if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
					md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
				else
					md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
			}
		}

		return md5StrBuff.toString();
	}

	private String getImgPath() {
		String path = "file" + File.separator + "dimensionImg";
		String realPath = UrlHelper.getRealPath(path);
		return realPath;
	}
}

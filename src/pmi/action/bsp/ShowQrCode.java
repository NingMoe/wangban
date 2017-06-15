package action.bsp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.imageio.ImageIO;

import com.icore.http.HttpResponseStatus;
import com.icore.http.util.HttpUtil;
import com.icore.util.StringUtil;
import com.inspur.base.BaseAction;
import com.inspur.http.util.UrlHelper;
import com.inspur.util.Tools;
import com.swetake.util.Qrcode;

public class ShowQrCode extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean handler(Map<String, Object> data) throws Exception {
		String fileUrl = this.getParameter("fileUrl");
		if (org.apache.commons.lang.StringUtils.isEmpty(fileUrl)) {
			getHttpAdapter().setStatus(HttpResponseStatus.NOT_FOUND);
			throw new Exception("访问的地址不符合规范.");
		}
		// 是否产生图片附件，默认true
		/*
		 * String genImg = (String)this.getParameter("genImg");
		 * if(StringUtil.isEmpty(genImg)){ genImg = "true"; }
		 */

		//String header = this.getHttpAdapter().getHeader("Host");
		String content = fileUrl;
		content = HttpUtil.formatUrl(content);
		// 输出的字节
		byte[] outByte = null;

		// 文件属性
		String fileName = getMD5Str(content) + content.hashCode();
		String filePath = getImgPath();// 文件夹路径
		String imgPath = filePath + File.separator + fileName + ".png"; // 文件路径
		// File file = new File(filePath);
		File imgFile = new File(imgPath);

		// 文件夹是否存在，不存在创建文件夹
		/*
		 * if(!file.exists() && !file.isDirectory()){ file.mkdir(); }
		 */

		// 如果文件存在，直接输出文件数据
		if (!imgFile.exists()) {
			// 生成二维码
			BufferedImage bufImg = qRCodeCommon(content, "png", 7);

			/*
			 * //根据参数判断是否生成文件 if("true".equals(genImg)){ File newFile = new
			 * File(imgPath); try { ImageIO.write(bufImg, "png", newFile); }
			 * catch (IOException e) { e.printStackTrace(); } }
			 */

			// 将二维码转换为字节输出
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(bufImg, "png", out);
			outByte = out.toByteArray();
		} else {
			outByte = Tools.getFile(imgFile);
		}

		this.write(outByte);
		this.setContentType("image/jpeg");
		return false;
	}

	/**
	 * 生成二维码(QRCode)图片的公共方法
	 * 
	 * @param content
	 *            存储内容
	 * @param imgType
	 *            图片类型
	 * @param size
	 *            二维码尺寸
	 * @return
	 */
	private BufferedImage qRCodeCommon(String content, String imgType, int size) {
		BufferedImage bufImg = null;
		try {
			Qrcode qrcodeHandler = new Qrcode();
			// 设置二维码排错率，可选L(7%)、M(15%)、Q(25%)、H(30%)，排错率越高可存储的信息越少，但对二维码清晰度的要求越小
			qrcodeHandler.setQrcodeErrorCorrect('M');
			qrcodeHandler.setQrcodeEncodeMode('B');
			// 设置设置二维码尺寸，取值范围1-40，值越大尺寸越大，可存储的信息越大
			qrcodeHandler.setQrcodeVersion(size);
			// 获得内容的字节数组，设置编码格式
			byte[] contentBytes = content.getBytes("utf-8");
			// 图片尺寸
			int imgSize = 67 + 12 * (size - 1);
			bufImg = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB);
			Graphics2D gs = bufImg.createGraphics();
			// 设置背景颜色
			gs.setBackground(Color.WHITE);
			gs.clearRect(0, 0, imgSize, imgSize);

			// 设定图像颜色> BLACK
			gs.setColor(Color.BLACK);
			// 设置偏移量，不设置可能导致解析出错
			int pixoff = 2;
			// 输出内容> 二维码
			if (contentBytes.length > 0 && contentBytes.length < 800) {
				boolean[][] codeOut = qrcodeHandler.calQrcode(contentBytes);
				for (int i = 0; i < codeOut.length; i++) {
					for (int j = 0; j < codeOut.length; j++) {
						if (codeOut[j][i]) {
							gs.fillRect(j * 3 + pixoff, i * 3 + pixoff, 3, 3);
						}
					}
				}
			} else {
				throw new Exception("QRCode content bytes length = " + contentBytes.length + " not in [0, 800].");
			}
			gs.dispose();
			bufImg.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bufImg;
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

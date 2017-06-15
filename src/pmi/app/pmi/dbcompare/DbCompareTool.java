package app.pmi.dbcompare;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.inspur.util.Tools;

public class DbCompareTool {

	/**
	 * 关闭数据库连接
	 * 
	 * @param conn 数据库连接
	 */
	public static void closeConn(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}

	/**
	 * 读取xml报文，返回document对象
	 * @param filepath 文件路径
	 * @return Document 文档对象
	 */
	public static Document readDoc(String filepath) {
		Document document = null;
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		try {
			File file = new File(filepath);
			if (file.exists()) {
				fis = new FileInputStream(file);
				bis = new BufferedInputStream(fis);
				SAXReader reader = new SAXReader();
				document = reader.read(bis);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try{
					fis.close();
				}catch(IOException ioe){
					ioe.printStackTrace();
				}				
			}
			if (bis != null) {
				try{
					bis.close();
				}catch(IOException ioe){
					ioe.printStackTrace();
				}				
			}
		}
		return document;
	}

	/**
	 * 将document对象写入xml报文
	 * @param filepath 文件路径
	 * @param document 文档对象
	 */
	public static boolean writeDoc(String filepath, Document document) {
		XMLWriter writer = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			File file = new File(filepath);
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			writer = new XMLWriter(bos, format);
			writer.write(document);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (writer != null) {
				try{
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
			if (bos != null) {
				try{
					bos.close();
				}catch (IOException e) {
					e.printStackTrace();
				}				
			}
			if (fos != null) {
				try{
					fos.close();
				}catch (IOException e) {
					e.printStackTrace();
				}				
			}
		}
		return true;
	}

	/**
	 * 格式化日期为字符串
	 * @param date 日期对象
	 * @return String 格式化日期字符串
	 */
	public static String formatDateToStr(Date date) {
		String ret = null;
		try {
			if (date != null) {
				ret = Tools.formatDate(date);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
}

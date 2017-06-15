import java.io.File;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.pmi.config.JobHandle;
import com.icore.base.IStartup;
import com.icore.file.FileManager;
import com.icore.http.HttpServer;
import com.icore.http.netty.NettyServer;
import com.icore.util.FrameConfig;
import com.inspur.HsfServer;
import com.inspur.util.PathUtil;
import com.inspur.util.Tools;

public class Startup implements IStartup {
	private Boolean _productModel;

	public Startup(Boolean productModel) {
		_productModel = productModel;
	}

	public Startup() {
		this(true);
	}

	private static Logger _log = LoggerFactory.getLogger(Startup.class);

	/**
	 * 递归删除目录下的所有文件及子目录下所有文件
	 * 
	 * @param dir
	 *            将要删除的文件目录
	 */
	private static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}
	@Override
	public void run()  {
		// TODO Auto-generated method stub
		_log.info("程序路径为："+PathUtil.getWebPath()+",模式为："+(Tools.productModel() ? "产品模式":"开发模式"));
		
		try{
			File updaterFile = new File(PathUtil.getWebPath()+"updater.zip");
			if(updaterFile.exists()){
				FileManager.unZip(updaterFile);
				File bFile = new File(PathUtil.getFileRoot()+"updater"+File.separator+"updater/");
				if(!bFile.exists()){
					bFile.mkdirs();
				}
				String backUpdaterFile = bFile.getPath()+File.separator+"updater_"+Tools.formatDate(new Date(), "yyyyMMddHHmmss")+".zip";
				FileManager.copyFile(updaterFile,backUpdaterFile);
				updaterFile.delete();
				_log.info("更新程序成功，更新文件备份到了："+backUpdaterFile);
			}
			
		}
		catch(Exception updaterException){
			_log.error("更新文件失败",updaterException);
		}
		String tempPath = PathUtil.getTempPath();
		
		File tempDir = new File(tempPath);
		
		//启动时，清除临时文件
		if(!tempDir.exists() || !tempDir.isDirectory()){
			tempDir.mkdirs();
		}

		Tools.setProdcutMode(_productModel);
		Tools.init();
		
		
		//是否开启hsf服务
		int hsfFlag = FrameConfig.getInt(FrameConfig.ConfigEnum.HSF_FLAG);
		if(hsfFlag==1){
			HsfServer.init();
		}
		//开启job
		try {
			JobHandle.getInstance().init();
		} catch (Exception e) {
			_log.error("开启job失败",e);
		}
		Tools.isStop(false);
	}

	public static void main(String[] args) throws Exception {
		System.out
				.println("__        __   _                            _          _      _ _         ");
		System.out
				.println("\\ \\      / /__| | ___ ___  _ __ ___   ___  | |_ ___   (_) ___(_) |_ _   _ ");
		System.out
				.println(" \\ \\ /\\ / / _ \\ |/ __/ _ \\| '_ ` _ \\ / _ \\ | __/ _ \\  | |/ __| | __| | | |");
		System.out
				.println("  \\ V  V /  __/ | (_| (_) | | | | | |  __/ | || (_) | | | (__| | |_| |_| |");
		System.out
				.println("   \\_/\\_/ \\___|_|\\___\\___/|_| |_| |_|\\___|  \\__\\___/  |_|\\___|_|\\__|\\__, |");
		System.out
				.println("                                                                    |___/ ");

		Boolean productMode = FrameConfig.productModel();
		for (String s : args) {
			if (s.equalsIgnoreCase("productModel")) {
				productMode = true;
			}
		}
		Tools.isStop(true);
		int webPort = FrameConfig.webPort();
		// 启动Web服务器
		/*for(int i=0;i<5;i++){
			Tools.webPort = webPort+i;
			if(Tools.isPortUsing("127.0.0.1", Tools.webPort)){
				continue;
			}
			HttpServer hs = new HttpServer(Tools.webPort);
			Thread thWebServer = new Thread(hs);
			thWebServer.start();
			break;
		}*/
		Tools.webPort=webPort;
		HttpServer hs = new HttpServer(webPort);
		Thread thWebServer = new Thread(hs);
		thWebServer.start();
		if (FrameConfig.serverClass().equalsIgnoreCase(
				NettyServer.class.getName())) {
			IStartup startup = new Startup(productMode);
			startup.run();
		}
	}

}
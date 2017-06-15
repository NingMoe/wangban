package app.pmi.config;

import com.icore.util.PathUtil;

public interface ISecurity {
	public static final String JOB_FILE = PathUtil.getWebInfPath()+"/classes/job.xml";
	public static final String PLUGIN_FILE = PathUtil.getWebInfPath()+"/classes/plugin.xml";
	public static final String REGISTERJOB_FILE = PathUtil.getWebPath()+"/conf/registerJob.xml";
	public static final String REGISTERPLUGIN_FILE = PathUtil.getWebPath()+"/conf/registerPlugin.xml";
	public static final String SECURITY_FILE = PathUtil.getWebInfPath()+"/classes/security.xml";
	//public static final String REGISTERSECURITY_FILE = PathUtil.getWebPath()+"/conf/security.properties";
	public static final String CUSTOM_FILE = PathUtil.getConfigPath()+"custom.xml";
	
}

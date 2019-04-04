package com.fh.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.StringUtils;

import com.fh.util.Logger;


/**
 * 如需支持动态加载配置文件，请配置环境变量 LOP_HOME,指向安装目录
 * 
 * 并将变量的获取方法改为 get方法，具体可参考GudongConfig中的getReloadPropertiesInterval()方法。
 * @author suxiaoyong
 *
 */
public class Configure {

	private static final Logger logger = Logger.getLogger(Configure.class);
	
	protected static Properties p = new Properties();
	private static final Timer timer = new Timer(true);
	protected static void init(final String propertyFileName,final String homedir) {
		Map<String,String> systemVariableMap = System.getenv();
		String path=systemVariableMap.get(StringUtils.upperCase(homedir));
		if(StringUtils.isEmpty(path)){
			path=systemVariableMap.get(StringUtils.lowerCase(homedir));
		}
		if(StringUtils.isEmpty(path)){
			staticInitProperty(propertyFileName);
		}
		else{
			final String filePath=path+File.separator+"conf"+File.separator+propertyFileName;
			DynamicInitProperty(filePath);
			timer.schedule(new TimerTask() {
				public void run() {
					try {
						DynamicInitProperty(filePath);
					} catch(Throwable t) {
							logger.error("读取配置文件失败", t);
					}
				}
			}, 5000, 60000);
		}	
	}
	
	protected static void DynamicInitProperty(String path) {
		Properties prop = new Properties(); 
		InputStream in=null;
		try {
			logger.info("load properties from "+path);
			in = new FileInputStream(path);
			if (in != null){
				prop.load(in);
				p=prop;
			}

		} catch (IOException e) {
			logger.error(MessageFormat.format("load {0} into Contants error",
					path));
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error("载入系统配置文件时出错，文件路径：" + path);
				}
			}
		}
	}
	protected static void staticInitProperty(String propertyFileName) {
		InputStream in = null;
		try {
			logger.info("staticInitProperty propertyFileName "+propertyFileName);
			in = LopConfig.class.getResourceAsStream(propertyFileName);
			if (in != null)
				p.load(in);
			logger.info("propertyFileName load p finished "+p);
		} catch (IOException e) {
			logger.error(MessageFormat.format("load {0} into Contants error",
					propertyFileName));
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error("载入系统配置文件时出错，文件路径：" + propertyFileName);
				}
			}
		}
	}



	protected static String getProperty(String key, String defaultValue) {
		return p.getProperty(key, defaultValue);
	}

	protected static boolean getProperty(String key, boolean defaultValue) {
		String result = p.getProperty(key);
		if (!StringUtils.isBlank(result)) {
			try {
				return Boolean.parseBoolean(result);
			} catch (Exception e) {
				return defaultValue;
			}
		} else {
			return defaultValue;
		}
	}

	protected static int getProperty(String key, int defaultValue) {
		String result = p.getProperty(key);
		if (!StringUtils.isBlank(result)) {
			try {
				return Integer.parseInt(result);
			} catch (Exception e) {
				return defaultValue;
			}
		} else {
			return defaultValue;
		}
	}

	protected static long getProperty(String key, long defaultValue) {
		String result = p.getProperty(key);
		if (!StringUtils.isBlank(result)) {
			try {
				return Long.parseLong(result);
			} catch (Exception e) {
				return defaultValue;
			}
		} else {
			return defaultValue;
		}
	}
}

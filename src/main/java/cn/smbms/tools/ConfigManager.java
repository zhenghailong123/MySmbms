package cn.smbms.tools;

import cn.smbms.dao.BaseDao;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    Properties params;

    private ConfigManager(){
        params = new Properties();
        String configFile = "database.properties";
        InputStream is= BaseDao.class.getClassLoader().getResourceAsStream(configFile);
        try {
            params.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 内部静态类
     */
    public static class ConfigManager_Helper{
        public static ConfigManager configManager = new ConfigManager();
    }

    public static ConfigManager getConfigManager(){
        return ConfigManager_Helper.configManager;
    }

    public String getValueByKey(String key){
        return params.getProperty(key);
    }
}

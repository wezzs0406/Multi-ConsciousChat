package dev.mmc.xingtuan.core;

public class MMC2 {
    public static final String NAME = "MMC2";
    public static final String VERSION = "2.0-new";
    public static final String FULL_NAME = "Multi-ConsciousChat 2";
    public static final String Writer1 = "xingtuan";
    public static final String Writer2 = "yunxi";
    public static final String COPYRIGHT = "©2025 MMC2 Team. All rights reserved.";
    public static final String DESCRIPTION = "为多意识体系统提供友好交流平台的应用程序";
    public static final String WEBSITE = "https://github.com/mmc2/multi-conscious-chat";
    public static final String ICON_PATH = "/img/MMC2.ico";
    public static final String DATA_EXPORT_FORMAT = "JSON";
    public static final String DATA_IMPORT_FORMAT = "JSON";
    public static final String CONFIG_FILE = "system.yaml";
    public static final String APP_DATA_DIR = System.getProperty("user.home") + "/.mmc2";

    // 应用配置
    public static final class Config {
        public static final boolean AUTO_SAVE = true;
        public static final int AUTO_SAVE_INTERVAL = 30000; // 30秒
        public static final boolean ENABLE_LOGGING = true;
        public static final int MAX_LOG_SIZE = 10485760; // 10MB
        public static final int MAX_CONVERSATION_MESSAGES = 1000;
        public static final int MAX_MEMBERS = 50;
    }

    // 主题配置
    public static final class Theme {
        public static final String DEFAULT_THEME = "默认蓝";
        public static final String[] AVAILABLE_THEMES = {
            "默认蓝", "温柔紫", "自然绿", "温暖橙", "深邃夜"
        };
    }
}

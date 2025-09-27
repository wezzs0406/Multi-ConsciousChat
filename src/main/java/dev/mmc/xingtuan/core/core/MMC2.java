package dev.mmc.xingtuan.core.core;

import dev.mmc.xingtuan.core.core.member.Consciousness;
import dev.mmc.xingtuan.core.core.member.PrivacyLevel;

public class MMC2 {
    static String ANOTHER1 = "yunxi";
    static String ANOTHER2 = "xingtuan";
    public static String ANOTHER_NAME = ANOTHER1 + " and " + ANOTHER2;

    public static void run() {
        // 初始化系统配置
        SystemConfig config = new SystemConfig();
        // 可以在这里加载已有的系统配置
    }
    
    // 系统配置类
    public static class SystemConfig {
        private java.util.List<Consciousness> members;
        private String currentMemberId;
        
        public SystemConfig() {
            this.members = new java.util.ArrayList<>();
            this.currentMemberId = "";
        }
        
        // getters and setters
        public java.util.List<Consciousness> getMembers() { return members; }
        public void setMembers(java.util.List<Consciousness> members) { this.members = members; }
        public String getCurrentMemberId() { return currentMemberId; }
        public void setCurrentMemberId(String currentMemberId) { this.currentMemberId = currentMemberId; }
    }
}

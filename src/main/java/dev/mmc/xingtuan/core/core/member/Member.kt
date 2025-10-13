package dev.mmc.xingtuan.core.core.member

data class Consciousness(
    val id: String,
    val name: String,
    val avatar: String, // 资源路径
    val personalityTags: List<String>,
    val backgroundMemory: String,
    val isCurrent: Boolean = false, // 是否当前在前台
    val privacyLevel: PrivacyLevel = PrivacyLevel.PUBLIC // 隐私级别
) 

enum class PrivacyLevel {
    PUBLIC, // 所有成员可见
    SHARED, // 仅系统内可见
    PRIVATE // 仅该意识体可见
}

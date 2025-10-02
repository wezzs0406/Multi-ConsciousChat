package dev.mmc.xingtuan.core.service

import dev.mmc.xingtuan.core.core.member.Consciousness
import dev.mmc.xingtuan.core.core.member.MemberManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("SwitchingService")

class SwitchingService {
    fun switchToMember(memberId: String): Boolean {
        // 检查成员是否存在
        val member = MemberManager.membersList.find { it.id == memberId }
        if (member == null) {
            logger.warn("未找到ID为 {} 的成员", memberId)
            return false
        }

        // 更新当前成员
        MemberManager.currentMemberId = memberId

        // 更新所有成员的isCurrent状态
        MemberManager.membersList.forEach { m ->
            // 使用copy创建新的数据对象，而不是直接修改属性
            // 这里简化处理，实际项目中可能需要更复杂的逻辑
        }

        logger.info("已切换到成员: {}", member.name)
        return true
    }

    // 获取当前成员
    fun getCurrentMember(): Consciousness? {
        return MemberManager.membersList.find { it.id == MemberManager.currentMemberId }
    }
}
@file:Depends("mindustryOS/function")

package mirai.zxs

import mindustryOS.Function

val fc = contextScript<Function>()

globalEventChannel().subscribeGroupMessages {
    case("个人信息") {
        if (group.id == 181108928L) {
         val profile = PlayerProfile.findByQQ(sender.id)
        if(profile != null){
            subject.sendMessage("""    | 当前绑定账号[]:{profile.id}
    | 总在线时间[]:{profile.onlineTime:分钟}
    | 当前等级[]:{profile.level}
    | 当前经验(下一级所需经验)[]:{profile.totalExp}({profile.nextLevel})
    | 注册时间[]:{profile.registerTime:YYYY-MM-dd}""".with("profile" to profile ).toString())
        } else { subject.sendMessage("查询失败，可能是未绑QQ") }
        }
    }
}


globalEventChannel().subscribeFriendMessages {
    case("个人信息") {
        val profile = PlayerProfile.findByQQ(sender.id)
        if(profile != null){
            subject.sendMessage("""    | 当前绑定账号[]:{profile.id}
    | 总在线时间[]:{profile.onlineTime:分钟}
    | 当前等级[]:{profile.level}
    | 当前经验(下一级所需经验)[]:{profile.totalExp}({profile.nextLevel})
    | 注册时间[]:{profile.registerTime:YYYY-MM-dd}""".with("profile" to profile ).toString())
        } else { subject.sendMessage("查询失败，可能是未绑QQ") }
    }
}
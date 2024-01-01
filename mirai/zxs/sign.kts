@file:Depends("wayzer/user/userService")
@file:Depends("mirai/send")

package mirai.zxs

import coreLibrary.lib.util.loop
import kotlinx.coroutines.delay
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeFriendMessages
import net.mamoe.mirai.event.subscribeGroupMessages
import wayzer.lib.dao.PlayerProfile
import wayzer.user.UserService
import mirai.Send
import java.time.LocalTime

var qqlist = mutableListOf<Long>()
val send = contextScript<Send>()
var us = mutableMapOf("users" to 0, "zjy" to 0)
val userService = contextScript<UserService>()
globalEventChannel().subscribeGroupMessages {
    case("签到") { if (group.id == 181108928L) {
        val mes = sign(sender.id)
        subject.sendMessage(mes)
        send.sendFriend(mes,sender.id)
    } } }
globalEventChannel().subscribeFriendMessages {
    case("签到") { subject.sendMessage(sign(sender.id)) } }
fun sign(qq:Long):String {
    return if(!qqlist.contains(qq)) {
        val profile = PlayerProfile.findByQQ(qq)
        if (profile != null) {
            qqlist += qq
            val jy = 抽卡()
            profile?.let { userService.updateExp(it, jy) }
            us["users"] = us["users"]!! + 1
            us["zjy"] = us["zjy"]!! + jy
            "签到成功！\n经验+${jy}\n签到人数${us["users"]}\n平均${us["zjy"]!!/us["users"]!!.toFloat()}\n同步发送至私信(如果您有机器人的好友)"
        } else { "未查询到相关玩家，可能是您未绑定" }
    } else { "您今天已经签到" }
}
onEnable {
        loop {
        delay(1000)
        if (LocalTime.now() == LocalTime.of(4, 0)) {
            qqlist.clear()
            us["users"] = 0
            us["zjy"] = 0
        }
    }
}
fun 抽卡(): Int {
    return if ((1..10).random() == 6) {
        (50..100).random()
    } else if((1..5).random() == 7) {
        (20..50).random()
    } else (1..20).random()
}
package mirai

import net.mamoe.mirai.Bot

fun sendFriend(msg: String ,qqid: Long) {
    return
    Bot.instancesSequence.forEach { bot ->
        launch {
            val friend = bot.getFriend(qqid)
            friend?.sendMessage(msg)
        }
    }
}
fun sendGroup(msg: String, groupId: Long) {
    if (groupId <= 0) return
    Bot.instancesSequence.forEach { bot ->
        launch {
            val group = bot.getGroup(groupId)
            group?.sendMessage(msg)
        }
    }
}

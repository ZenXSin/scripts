@file:Depends("wayzer/user/ext/profileBind")

package mirai.zxs

import wayzer.user.ext.ProfileBind
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

val bind = contextScript<ProfileBind>()

globalEventChannel().subscribeFriendMessages {
    contains("bind", true).reply {
        val admins : List<Long> = listOf(809109491, 1625717688, 2025504162, 488254306, 2719754408, 2650806595, 3462772994, 171864117, 3143758845)
        if (admins.contains(sender.id)) {
            val bd = bind.generate(it.replace("bind", "").toLong()).toString()
            subject.sendMessage(bd)
        }
    }
}
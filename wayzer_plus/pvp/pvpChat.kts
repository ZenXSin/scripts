package wayer_plus

import arc.util.Log
import mindustry.game.Team
import mindustry.gen.Groups
import mindustry.gen.Player
import mindustry.net.Administration

val Team.colorizeName get() = "[#${color}]${name}[]"

fun message(from: Player, msg: String, team: Boolean) {
    val typeStr = if (team) "[cyan][队内]" else ""
    val teamStr = "[violet][[${from.team().colorizeName}]"

    for (p in Groups.player) {
        val prefix = when {
            p.team() == Team.all[255] -> "${typeStr}${teamStr}"
            team && from.team() == p.team() -> typeStr
            !team -> teamStr
            else -> continue
        }
        p.sendMessage(prefix + netServer.chatFormatter.format(from, msg), from, msg)
    }
}

val filter = Administration.ChatFilter { p, t ->
    if (!state.rules.pvp) return@ChatFilter t
    message(p, t, true)
    Log.info("&fi@: @", "&lc" + p.name, "&lw$t")
    null
}

onEnable {
    netServer.admins.chatFilters.add(filter)
    onDisable {
        netServer.admins.chatFilters.remove(filter)
    }
}

command("t", "PVP模式全体聊天") {
    type = CommandType.Client
    body {
        val msg = arg.joinToString(" ")
        message(player!!, msg, false)
    }
}
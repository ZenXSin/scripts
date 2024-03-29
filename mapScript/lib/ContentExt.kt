package mapScript.lib

import cf.wayzer.scriptAgent.define.Script
import cf.wayzer.scriptAgent.define.ScriptDsl
import cf.wayzer.scriptAgent.depends
import cf.wayzer.scriptAgent.import

@ScriptDsl
fun Script.modeIntroduce(mode: String, introduce: String) {
    onEnable {
        depends("wayzer/map/mapInfo")?.import<(String, String) -> Unit>("addModeIntroduce")
            ?.invoke(mode, introduce)
    }
}
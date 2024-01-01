package mirai

import mindustry.game.Schematics
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import java.io.File

name = "schematic"

globalEventChannel().subscribeMessages {
    startsWith("bXNja"){ it ->
    val qq = sender.id
        logger.info("hi")
        val base = "bXNja" + it
        val schematic = Schematics.readBase64(base)
        var msg = """
        |蓝图名称: ${schematic.name()}
        |蓝图描述: ${schematic.description()}
        |蓝图大小: ${schematic.width}x${schematic.height}
        |电力使用: 产出${schematic.powerProduction()} 消耗${schematic.powerConsumption()} 共${schematic.powerProduction() - schematic.powerConsumption()}
        |建造所需: 
        |${schematic.requirements().toString()}
        |核心蓝图: ${if(schematic.hasCore()){"是"}else{"否"}}
        """.trimMargin().toString()
        subject.sendMessage(msg)
        delay(50)
        }
    }
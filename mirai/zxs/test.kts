package mirai

import net.mamoe.mirai.message.data.RichMessage

globalEventChannel().subscribeGroupMessages {
    case("测试") {
        val xml = buildXmlMessage(60) {
            item {                 
            title("寄")
            }                 
        }
        subject.sendMessage(xml)
        logger.info("寄")
    }
}
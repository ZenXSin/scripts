@file:Depends("mirai/broadcastToGroup", "shout")

package mirai

import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import net.mamoe.mirai.message.data.QuoteReply
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.PlainText


globalEventChannel().subscribeGroupMessages {
    case("help") {
        val file = File("n.png").toExternalResource()
        val image = subject.uploadImage(file)
        val file1 = File("t.png").toExternalResource()
        val image1 = subject.uploadImage(file1)
        val forward: ForwardMessage = buildForwardMessage {
            add(809109491L, "ra", PlainText("------帮助面板------\n创建日期 2023.4.17\n最后更新于 2023.5.15\n感谢:RA wayzer cong W 冥 ...\n----服务器信息----\n系统:mcsm java17\n内存:4+10G\n上行 15mbps\n下行 100mbps\n新服务器地址:mdtbaohe.work:43252\n新服为按负载计费，切记勿炸服。(想的话可以先给服务器打点钱ĕĕĕ)\n游戏版本:144MindustryX\nmod版本:3.0.15\n------QQ群机器人指令------\nhelp 查看帮助面板\n绑定 绑定游戏与qq\n服务器状态 查看服务器状态\n开发者帮助 管理员专用指令，用于管理服务器\nms[url] 查询地址延迟\ngc 清理缓存\n------服务器指令------\n自行用/help指令查看\n------QQ绑定服务器教程------\n群发'绑定'，获取绑定码\n------重要公告------\n新服务器已开启\n服务器地址\nmdtbaohe.work:43252\n------捐赠------\n捐赠是为了让玩家们有更好的游戏体验,这些钱会用于购买/续费服务器.dns,域名,脚本等(只要钱到位,wz服玩的饱和也能玩。支付后将支付截图发给机器人3天内添加不需要加好友)\n------捐赠名单------\n*器 6.33rmb\n1379号监听员 13.09rmb\n狗尾巴草 20rmb\n2H₂O₂ 5rmb\n甘雨她老公 1rmb\n*给 5rmb\ntt 2rmb\n946519 2rmb\n------制作人名单------\n游戏本体-anuke\n服务器插件-wayzer\n饱火mod-RA\n帮助面板-zxs") + image + image2)
        }
        subject.sendMessage(forward)
    }
}
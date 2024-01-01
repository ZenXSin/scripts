package mirai

import coreMindustry.lib.RootCommands.handleInput

globalEventChannel().subscribeMessages {
    case("强制换图") {
    val user = sender.id
            if (user == 3113769776 || user == 2504013368 || user == 548580339L || user == 1471135253L || user == 809109491L || user == 488254306L) {
        coreMindustry.lib.RootCommands.handleInput("host", null)
        subject.sendMessage("!!!换图成功!!!")
        }
    }    
}
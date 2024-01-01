import java.lang.System


globalEventChannel().subscribeMessages {
    case("gc") {
        val runtime = Runtime.getRuntime()
        val usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory()
        System.gc() // 触发垃圾回收
        val usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory()
        val msg = "清理完毕，清理前:${usedMemoryBefore / 1024 / 1024} MB，清理后:${usedMemoryAfter / 1024 / 1024} MB"
        subject.sendMessage(msg)
    }
}
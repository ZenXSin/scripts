@file:Depends("coreMindustry")
@file:Depends("coreLibrary/DBApi", "数据库服务")
@file:Import("com.google.guava:guava:30.1-jre", mavenDepends = true)
@file:Import("wayzer.lib.*", defaultImport = true)
@file:Import("wayzer.lib.dao.*", defaultImport = true)

package wayzer

import wayzer.*
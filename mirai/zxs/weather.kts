@file:Import("org.json:json:20230227", mavenDepends = true)

package mirai

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import net.mamoe.mirai.message.data.QuoteReply
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.PlainText

/*fun getWeather(city: String, key: String) {
    val url = "http://apis.juhe.cn/simpleWeather/query?city=$city&key=$key"
    val obj = URL(url)

    with(obj.openConnection() as HttpURLConnection) {
        requestMethod = "POST"

        println("Response Code : ${responseCode}")

        BufferedReader(InputStreamReader(inputStream)).use {
            val response = it.lines().collect(java.util.stream.Collectors.joining("\n"))
            val jsonResponse = JSONObject(response)
            val errorCode = jsonResponse.get("error_code") as Int
            if (errorCode == 0) {
                val result = jsonResponse.get("result") as JSONObject
                val city = result.get("city")
                val realtime = result.get("realtime") as JSONObject
                val temperature = realtime.get("temperature")
                val humidity = realtime.get("humidity")
                val aqi = realtime.get("aqi")
                val info = realtime.get("info")
                val direct = realtime.get("direct")
                println("城市: $city")
                println("温度: $temperature")
                println("湿度: $humidity")
                println("空气质量: $aqi")
                println("天气情况: $info")
                println("风向: $direct")

                val future = result.get("future") as org.json.JSONArray
                for(i in 0 until future.length()){
                    val futureDay = future.get(i) as JSONObject
                    val date = futureDay.get("date")
                    val futureTemp = futureDay.get("temperature")
                    val futureWeather = futureDay.get("weather")
                    val futureDirect = futureDay.get("direct")
                    println("日期: $date")
                    println("温度: $futureTemp")
                    println("天气情况: $futureWeather")
                    println("风向: $futureDirect")
                }
            } else {
                val reason = jsonResponse.get("reason")
                println("查询失败, 失败原因: $reason")
            }

        }
    }
}*/

//
fun getWeather(city: String, key: String): String {
    val url = "http://apis.juhe.cn/simpleWeather/query?city=$city&key=$key"
    val obj = URL(url)
    var resultText = ""

    with(obj.openConnection() as HttpURLConnection) {
        requestMethod = "POST"

        BufferedReader(InputStreamReader(inputStream)).use {
            val response = it.lines().collect(java.util.stream.Collectors.joining("\n"))
            val jsonResponse = JSONObject(response)
            val errorCode = jsonResponse.get("error_code") as Int
            if (errorCode == 0) {
                val result = jsonResponse.get("result") as JSONObject
                val city = result.get("city")
                val realtime = result.get("realtime") as JSONObject
                val temperature = realtime.get("temperature")
                val humidity = realtime.get("humidity")
                val aqi = realtime.get("aqi")
                val info = realtime.get("info")
                val direct = realtime.get("direct")
                resultText += "城市: $city\n"
                resultText += "温度: $temperature\n"
                resultText += "湿度: $humidity\n"
                resultText += "空气质量: $aqi\n"
                resultText += "天气情况: $info\n"
                resultText += "风向: $direct\n"

                val future = result.get("future") as org.json.JSONArray
                for(i in 0 until future.length()){
                    val futureDay = future.get(i) as JSONObject
                    val date = futureDay.get("date")
                    val futureTemp = futureDay.get("temperature")
                    val futureWeather = futureDay.get("weather")
                    val futureDirect = futureDay.get("direct")
                    resultText += "日期: $date\n"
                    resultText += "温度: $futureTemp\n"
                    resultText += "天气情况: $futureWeather\n"
                    resultText += "风向: $futureDirect\n"
                }
            } else {
                val reason = jsonResponse.get("reason")
                resultText += "查询失败, 失败原因: $reason\n"
            }

        }
    }
    return resultText
}

//

val key = "251518e073ef6c3c9504dd286c3f6a86"
//getWeather(city, key)


// 要检测的QQ号
val targetQQ = 2504013368L

// 订阅群消息事件
globalEventChannel().subscribeGroupMessages {
    // 处理收到的群消息
    always {
        // 遍历消息链中的所有元素
        message.forEach { message ->
            // 检查元素是否为At类型
            if (message is At) {
                // 检查At的目标是否为要检测的QQ号
                if (message.target == targetQQ) {
                    // 有人@了特定的QQ号
                    // 在这里处理@特定QQ号的逻辑
                 if (it.contains("天气")) {
                 val text = it.replace("@2504013368 ", "").replace("天气", "")
                val mes = getWeather(text, key).toString()
              subject.sendMessage(mes)
                }
                }
            }
        }
    }
}

globalEventChannel().subscribeFriendMessages {
contains("天气", true).reply {
              subject.sendMessage(getWeather(it.replace("天气", ""), key))
              }
}


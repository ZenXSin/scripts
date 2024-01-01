@file:Depends("coreMindustry/utilNext", "调用菜单")
@file:Depends("coreMindustry/utilMapRule", "修改核心单位,单位属性")

package mapScript

import arc.math.Mathf
import arc.util.Align
import arc.util.Time
import coreLibrary.lib.util.loop
import mindustry.content.Blocks
import mindustry.content.Items
import mindustry.content.StatusEffects
import mindustry.content.UnitTypes
import mindustry.entities.Units
import mindustry.game.Team
import mindustry.gen.Groups
import mindustry.gen.Iconc.*
import mindustry.type.StatusEffect
import mindustry.type.UnitType
import mindustry.world.blocks.storage.CoreBlock
import kotlin.random.Random

/**@author xkldklp
 * https://mdt.wayzer.top/v2/map/13599/latest
 * 代码优化整理: WayZer blac
 */
name = "[sky]STAR[red]BLAST"

val menu = contextScript<coreMindustry.UtilNext>()

fun Player.upgrade(type: UnitType, coreCost: Int) {
    val unit = unit()
    unit(type.spawn(team(), unit).apply {
        spawnedByCore = true

        if (type != UnitTypes.horizon) {//轰炸机太慢了不能攻击
            apply(StatusEffects.freezing, Float.MAX_VALUE)
        }
        apply(StatusEffects.electrified, Float.MAX_VALUE)
        apply(StatusEffects.sapped, Float.MAX_VALUE)

        if (type == UnitTypes.mega || type == UnitTypes.quad) {//你跑太快力
            apply(StatusEffects.sporeSlowed, Float.MAX_VALUE)
        }

        apply(StatusEffects.invincible, 5f * 60f)
        apply(StatusEffects.shielded, 10f * 60f)
        apply(StatusEffects.disarmed, 5f * 60f)
    })
    if (coreCost > 0) {
        core().items.remove(Items.copper, coreCost)
        sendTeamMessage("${name}使用了核心资源$itemCopper${coreCost}升级为${type.emoji()}")
    }
}

fun Player.teamMessage(message: String) {
    Call.label("${name}:[#${team().color}]${message}", 12f, x, y)
    //大  声  密  谋
}

fun Player.sendTeamMessage(message: String) {//发送到聊天栏
    Groups.player.filter{ it.team() == team() }.forEach {
        it.sendMessage(message)
    }
}

fun mindustry.gen.Unit.addItem(amount: Int, fromX: Float, fromY: Float) {
    val added = amount.coerceAtMost(itemCapacity() - stack.amount)
    stack.amount += added
    if (added < amount)//剩下的给予核心
        core()?.items?.add(Items.copper, amount - added)
    repeat(amount) {
        var color = "[white]"
        if (amount - it > added) color = "[yellow]"
        Call.label(
            player?.con ?: return@repeat,
            "$color$itemCopper",
            2f,
            fromX + Random.nextFloat() * 8,
            fromY + Random.nextFloat() * 8
        )
    }
}

fun itemDrop(x: Float, y: Float, unit: Any, amount: Int) {
    val units = buildList {
        Units.nearby(null, x, y, 24 * 8f) {
            if (it != unit && it.type != UnitTypes.flare && !it.hasEffect(StatusEffects.disarmed)) add(it)
        }
    }
    if (units.isEmpty()) return
    val moreLuck = units.shuffled().take(amount % units.size).toSet()
    units.forEach {
        it.addItem(amount / units.size + (if (it in moreLuck) 1 else 0), x, y)
    }
}

suspend fun Player.sendFlareUpgradeMenu() {
    menu.sendMenuBuilder<Unit>(
        this, 30_000, "flare直升",
        """
            [cyan]一键升级所需单位!
            [red]背包资源不足时花费核心资源
            [white]需要离核心足够近才能使用
            [cyan]T4需要核心铜>=3000
            [cyan]T5需要核心铜>=18000
        """.trimIndent()
    ) {
        fun unitUpgrade(type: UnitType, cost: Int,coreCost: Float = 0f, needCopper: Int = 0) =
            "升级为 ${type.emoji()}\n$itemCopper$cost" +
                    if (coreCost > 0f) { "+[yellow]${(coreCost * core().items.get(Items.copper)).toInt()}\n[lightgray](${(coreCost * 100).toInt()}%核心资源)" } else { "" } to suspend {
                if (unit().stack().amount + core().items.get(Items.copper) < cost)
                    sendMessage("[red]核心/背包资源不足")
                else if (core().items.get(Items.copper) < needCopper)
                    sendMessage("[red]核心资源不足")
                else if (!unit().within(core().tile, itemTransferRange)) {
                    sendMessage("[red]距离核心过远，无法拿取资源")
                }
                else {
                    var needCoreCopper = 0
                    if (cost <= unit().stack().amount) {
                        unit().stack().amount -= cost
                    } else {
                        needCoreCopper = cost - unit().stack().amount
                        unit().stack().amount = 0
                    }
                    upgrade(type,(core().items.get(Items.copper) * coreCost).toInt() + needCoreCopper)
                }
            }
        this += listOf(
            unitUpgrade(UnitTypes.poly,   0),
            unitUpgrade(UnitTypes.horizon,0)
        )
        this += listOf(
            unitUpgrade(UnitTypes.zenith, 30),
            unitUpgrade(UnitTypes.mega,   30),
            unitUpgrade(UnitTypes.cyerce, 30)
        )
        if (core().items.get(Items.copper) >= 3000) {
            this += listOf(
                unitUpgrade(UnitTypes.antumbra, 110, 0.1f, 3000),
                unitUpgrade(UnitTypes.quad,     90,  0.1f, 3000)
            )
            this += listOf(
                unitUpgrade(UnitTypes.aegires,  110, 0.1f, 3000),
                unitUpgrade(UnitTypes.sei,      110, 0.2f, 3000)
            )
        }
        if (core().items.get(Items.copper) >= 18000) {
            this += listOf(
                unitUpgrade(UnitTypes.eclipse,  290, 0.3f, 18000),
                unitUpgrade(UnitTypes.oct,      240, 0.3f, 18000)
            )
            this += listOf(
                unitUpgrade(UnitTypes.navanax,  280, 0.3f, 18000),
                unitUpgrade(UnitTypes.omura,    260, 0.3f, 18000)
            )
        }
        add(listOf("返回" to {sendMenu()}))
    }
}
suspend fun Player.sendMenu() {
    menu.sendMenuBuilder<Unit>(
        this, 30_000, "升级界面",
        """
            [green]升级单位使用单位背包资源
            [red]有些单位升级需要额外使用核心资源！(百分比)
            [cyan]T4需要核心铜>=3000
            [cyan]T5需要核心铜>=18000
        """.trimIndent()
    ) {
        fun addUnitUpgrade(type: UnitType, cost: Int = 0, coreCost: Float = 0f, needCopper: Int = 0) {
            add(listOf("升级为 ${type.emoji()}  $itemCopper$cost" +
                    if (coreCost > 0f) { "+[yellow]${(coreCost * core().items.get(Items.copper)).toInt()}\n[lightgray](${coreCost * 100}%核心资源)" } else { "" } to suspend {
                if (unit().stack().amount < cost)
                    sendMessage("[red]单位背包资源不足")
                else if (core().items.get(Items.copper) < needCopper)
                    sendMessage("[red]核心资源不足")
                else upgrade(type,(core().items.get(Items.copper) * coreCost).toInt())
            }))
        }
        //单位升级
        when (unit().type) {
            UnitTypes.flare -> {
                addUnitUpgrade(UnitTypes.horizon)
                addUnitUpgrade(UnitTypes.poly)
            }
            UnitTypes.horizon -> {
                addUnitUpgrade(UnitTypes.zenith, 30)
                addUnitUpgrade(UnitTypes.mega,   30)
            }
            UnitTypes.poly -> {
                addUnitUpgrade(UnitTypes.mega,   30)
                addUnitUpgrade(UnitTypes.cyerce, 30)
            }
        }
        if (core().items.get(Items.copper) >= 3000) {
            when (unit().type) {
                UnitTypes.zenith -> {
                    addUnitUpgrade(UnitTypes.antumbra, 80, 0.05f, 3000)
                    addUnitUpgrade(UnitTypes.quad,     80, 0.1f,  3000)
                }
                UnitTypes.mega -> {
                    addUnitUpgrade(UnitTypes.quad,     60, 0.05f, 3000)
                    addUnitUpgrade(UnitTypes.aegires,  60, 0.1f,  3000)
                }
                UnitTypes.cyerce -> {
                    addUnitUpgrade(UnitTypes.aegires,  80, 0.05f, 3000)
                    addUnitUpgrade(UnitTypes.sei,      80, 0.15f, 3000)
                }
            }
        }
        if (core().items.get(Items.copper) >= 18000) {
            when (unit().type) {
                UnitTypes.antumbra -> {
                    addUnitUpgrade(UnitTypes.eclipse, 180, 0.2f, 18000)
                    addUnitUpgrade(UnitTypes.oct,     180, 0.5f, 18000)
                    addUnitUpgrade(UnitTypes.navanax, 180, 0.5f, 18000)
                    addUnitUpgrade(UnitTypes.omura,   180, 0.5f, 18000)
                }
                UnitTypes.quad -> {
                    addUnitUpgrade(UnitTypes.eclipse, 140, 0.5f, 18000)
                    addUnitUpgrade(UnitTypes.oct,     140, 0.2f, 18000)
                    addUnitUpgrade(UnitTypes.navanax, 140, 0.5f, 18000)
                    addUnitUpgrade(UnitTypes.omura,   140, 0.5f, 18000)
                }
                UnitTypes.aegires -> {
                    addUnitUpgrade(UnitTypes.eclipse, 170, 0.5f, 18000)
                    addUnitUpgrade(UnitTypes.oct,     170, 0.5f, 18000)
                    addUnitUpgrade(UnitTypes.navanax, 170, 0.2f, 18000)
                    addUnitUpgrade(UnitTypes.omura,   170, 0.5f, 18000)
                }
                UnitTypes.sei -> {
                    addUnitUpgrade(UnitTypes.eclipse, 150, 0.5f, 18000)
                    addUnitUpgrade(UnitTypes.oct,     150, 0.5f, 18000)
                    addUnitUpgrade(UnitTypes.navanax, 150, 0.5f, 18000)
                    addUnitUpgrade(UnitTypes.omura,   150, 0.2f, 18000)
                }
            }
        }

        var allBuffCost = 0

        fun addEffect(effect: StatusEffect, cost: Int): Pair<String, suspend () -> Unit>? {
            return if (!unit().hasEffect(effect)) {
                allBuffCost += cost
                "添加 ${effect.emoji()}\n$itemCopper${cost}" to suspend {
                    if (unit().stack().amount < cost)
                        sendMessage("[red]背包资源不足")
                    else unit().apply(effect)
                }
            } else null
        }
        fun removeEffect(effect: StatusEffect, cost: Int): Pair<String, suspend () -> Unit>? {
            return if (unit().hasEffect(effect)) {
                allBuffCost += cost
                "移除 ${effect.emoji()}\n$itemCopper${cost}" to suspend {
                    if (unit().stack().amount < cost)
                        sendMessage("[red]背包资源不足")
                    else unit().unapply(effect)
                }
            } else null
        }

        //buff增减
        val debuffList = listOfNotNull(
            removeEffect(StatusEffects.electrified, (unit().itemCapacity() * 0.5f).toInt()),
            removeEffect(StatusEffects.sapped,      (unit().itemCapacity() * 0.5f).toInt()),
            removeEffect(StatusEffects.freezing,    (unit().itemCapacity() * 0.5f).toInt())
        )
        if (debuffList.any()) this += debuffList
        val buffList = listOfNotNull(
            addEffect(StatusEffects.overclock,   (unit().itemCapacity() * 0.6f).toInt()),
            addEffect(StatusEffects.overdrive,   (unit().itemCapacity() * 1f).toInt()),
            addEffect(StatusEffects.boss,        (unit().itemCapacity() * 1f).toInt())
        )
        if (buffList.any()) this += buffList

        if (allBuffCost > 0) {
            add(listOf("一键购买所有buff选项  $itemCopper${allBuffCost}\n[red]背包资源不足时消耗核心资源(需要能够到核心)" to suspend {
                if (unit().stack().amount + core().items.get(Items.copper) < allBuffCost)
                    sendMessage("[red]核心/背包资源不足")
                else if (unit().stack().amount < allBuffCost && !within(core().tile, itemTransferRange))
                    sendMessage("[red]距离核心过远，无法拿取资源")
                else {
                    if (allBuffCost <= unit().stack().amount) {
                        unit().stack().amount -= allBuffCost
                    } else {
                        val needCoreCopper = allBuffCost - unit().stack().amount
                        core().items?.remove(Items.copper, needCoreCopper)
                        unit().stack().amount = 0
                        sendTeamMessage("${name}使用了核心资源$itemCopper${needCoreCopper}购买了所有buff")
                    }
                    unit().apply{
                        unapply(StatusEffects.electrified)
                        unapply(StatusEffects.sapped)
                        unapply(StatusEffects.freezing)
                        apply(StatusEffects.overclock, Float.MAX_VALUE)
                        apply(StatusEffects.overdrive, Float.MAX_VALUE)
                        apply(StatusEffects.boss, Float.MAX_VALUE)
                    }
                    Unit
                }
            }))
        }
        //直升
        if (unit().type == UnitTypes.flare)
            add(listOf("flare直升\n[red]需要在核心取物范围才能使用" to {
                if (!unit().within(core().tile, itemTransferRange))
                    sendMessage("[red]距离核心过远，无法使用此功能")
                else sendFlareUpgradeMenu()
            }))
        //团队指令
        add(buildList {
            add("团队指令：发起进攻" to {
                teamMessage("\uE861进攻\uE861")
            })
            add("团队指令：注意基地" to {
                teamMessage("⚠基地\uE84D")
            })
            add("团队指令：跟着我" to {
                teamMessage("\uF844请求跟随\uE872")
            })
        })
        add(buildList {
            add("团队指令：No" to {
                teamMessage("\uE815NO\uE815")
            })
            add("团队指令：OK" to {
                teamMessage("\uE800OK\uE800")
            })
        })

        add(listOf("取消" to {}))
    }
}

listen<EventType.TapEvent> {
    if (it.tile.block() is CoreBlock) {
        val player = it.player
        if (!player.dead() && it.tile.team() == player.team()) {
            launch(Dispatchers.game) { player.sendMenu() }
        }
    }
}

val copperToCoreHealth = state.rules.tags.getInt("@copperToCoreHealth", 10)
val copperToUnitHealth = state.rules.tags.getFloat("@copperToUnitHealth", 0.5f)
listen(EventType.Trigger.update){
    state.teams.getActive().each { data ->//核心回血
        val core = data.core() ?: return@each
        val use = (1 - core.health / copperToCoreHealth).toInt().coerceAtMost(core.items.get(Items.copper))
        if (use > 0) {
            core.items.remove(Items.copper, use)
            core.health += use * copperToCoreHealth
            itemDrop(core.x, core.y, "This is a build", use)
        }
    }
    state.teams.getActive().each { data ->//单位回血
        data.units.each {//每个铜都能回血 一背包铜回复50%血量
            val copperToHealth = (it.maxHealth * copperToUnitHealth) / it.itemCapacity()
            val use = ((it.maxHealth - it.health) / copperToHealth).toInt().coerceAtMost(it.stack().amount)
            if (use > 0) {
                it.stack.amount -= use
                it.health += use * copperToHealth
                itemDrop(it.x, it.y, it, (use.toFloat() * 0.8f).toInt())
            }
        }
    }
    Groups.unit.each{//单位墙上掉血
        val hitSize = Mathf.ceil(it.hitSize / 8)

        val building = (if (hitSize / 2 > 0) world.tileBuilding(
            Random.nextInt(it.tileX() - hitSize / 2,it.tileX() + hitSize / 2),
            Random.nextInt(it.tileY() - hitSize / 2,it.tileY() + hitSize / 2)
        ) else it.tileOn())?.build ?: return@each

        if (!it.within(building,it.hitSize / 2) ||
            building.team == it.team ||
            it.type in listOf(UnitTypes.flare,UnitTypes.horizon)) return@each
        val damage = it.maxHealth * 0.2f / 60
        it.damagePierce(damage)
        building.damage(it.team,damage)
    }
}

onEnable {
    contextScript<coreMindustry.UtilMapRule>().apply {
        //核心属性
        registerMapRule((Blocks.coreShard as CoreBlock)::unitType) { UnitTypes.flare }
        registerMapRule((Blocks.coreFoundation as CoreBlock)::unitType) { UnitTypes.flare }
        registerMapRule((Blocks.coreNucleus as CoreBlock)::unitType) { UnitTypes.flare }

        registerMapRule((Blocks.coreShard as CoreBlock)::health) { 5000 }
        registerMapRule((Blocks.coreFoundation as CoreBlock)::health) { 10000 }
        registerMapRule((Blocks.coreNucleus as CoreBlock)::health) { 20000 }

        registerMapRule((Blocks.coreShard as CoreBlock)::itemCapacity) { 100000000 }
        registerMapRule((Blocks.coreFoundation as CoreBlock)::itemCapacity) { 100000000 }
        registerMapRule((Blocks.coreNucleus as CoreBlock)::itemCapacity) { 100000000 }
        //t1
        registerMapRule(UnitTypes.flare::health) { 1500f }
        registerMapRule(UnitTypes.flare.weapons.get(0).bullet::damage) { 0f }
        //t2
        registerMapRule(UnitTypes.poly.weapons.get(0).bullet::healPercent) { 0f }
        registerMapRule(UnitTypes.poly.weapons.get(0).bullet::damage) { 34f }
        registerMapRule(UnitTypes.poly::armor) { 0f }
        //t3
        registerMapRule(UnitTypes.zenith::armor) { 6f }

        registerMapRule(UnitTypes.mega::health) { 560f }
        registerMapRule(UnitTypes.mega::armor) { 0f }
        registerMapRule(UnitTypes.mega.weapons.get(0).bullet::healPercent) { 0f }
        registerMapRule(UnitTypes.mega.weapons.get(0).bullet::damage) { 16f }
        registerMapRule(UnitTypes.mega.weapons.get(2).bullet::healPercent) { 0f }
        registerMapRule(UnitTypes.mega.weapons.get(2).bullet::damage) { 8f }

        registerMapRule(UnitTypes.cyerce::flying) { true }
        registerMapRule(UnitTypes.cyerce::health) { 400f }
        registerMapRule(UnitTypes.cyerce::armor) { 0f }
        registerMapRule(UnitTypes.cyerce.weapons.get(2).bullet::healPercent) { 0f }
        //t4
        registerMapRule(UnitTypes.antumbra::health) { 3600f }
        registerMapRule(UnitTypes.antumbra::armor) { 0f }
        registerMapRule(UnitTypes.antumbra.weapons.get(0).bullet::damage) { 9f }
        registerMapRule(UnitTypes.antumbra.weapons.get(0).bullet::splashDamage) { 18f }
        registerMapRule(UnitTypes.antumbra.weapons.get(5).bullet::damage) { 33f }

        registerMapRule(UnitTypes.quad::health) { 3600f }
        registerMapRule(UnitTypes.quad::armor) { 0f }
        registerMapRule(UnitTypes.quad.weapons.get(0).bullet::splashDamage) { 44f }
        registerMapRule(UnitTypes.quad.weapons.get(0).bullet::splashDamageRadius) { 240f }
        registerMapRule(UnitTypes.quad.weapons.get(0).bullet::collidesAir) { true }

        registerMapRule(UnitTypes.sei::flying) { true }
        registerMapRule(UnitTypes.sei::health) { 1800f }
        registerMapRule(UnitTypes.sei.weapons.get(0).bullet::damage) { 15f }
        registerMapRule(UnitTypes.sei.weapons.get(0).bullet::splashDamage) { 7f }
        registerMapRule(UnitTypes.sei.weapons.get(1).bullet::damage) { 24f }
        registerMapRule(UnitTypes.sei::armor) { 0f }

        registerMapRule(UnitTypes.aegires::flying) { true }
        registerMapRule(UnitTypes.aegires::health) { 1600f }
        registerMapRule(UnitTypes.aegires::armor) { 0f }
        registerMapRule(UnitTypes.aegires.weapons.get(0).bullet::damage) { 96f }
        registerMapRule(UnitTypes.aegires.weapons.get(3).bullet::damage) { 96f }
        //t5
        registerMapRule(UnitTypes.eclipse::health) { 7200f }
        registerMapRule(UnitTypes.eclipse::armor) { 0f }
        registerMapRule(UnitTypes.eclipse.weapons.get(0).bullet::damage) { 55f }
        registerMapRule(UnitTypes.eclipse.weapons.get(2).bullet::splashDamage) { 25f }

        registerMapRule(UnitTypes.oct::health) { 8000f }
        registerMapRule(UnitTypes.oct::armor) { -50f }

        registerMapRule(UnitTypes.omura::flying) { true }
        registerMapRule(UnitTypes.omura::health) { 5600f }
        registerMapRule(UnitTypes.omura::armor) { 0f }
        registerMapRule(UnitTypes.omura.weapons.get(0).bullet::damage) { 600f }

        registerMapRule(UnitTypes.navanax::flying) { true }
        registerMapRule(UnitTypes.navanax::health) { 3800f }
        registerMapRule(UnitTypes.navanax::armor) { -8f }
        registerMapRule(UnitTypes.navanax.weapons.get(4).bullet::status) { StatusEffects.disarmed }
        registerMapRule(UnitTypes.navanax.weapons.get(4).bullet::statusDuration) { 0.4f * 60 }
        //小 心 洪 水
        registerMapRule(UnitTypes.quad.weapons.get(0)::reload) { 55f }//防止被flood覆盖
    }
    //刷新区域半边长
    val range = state.rules.tags.getInt("@refreshRange", 50)
    loop(Dispatchers.game) {
        delay(1000)
        repeat(3) {
            val tile = world.tiles.getn(
                Random.nextInt(world.width() / 2 - range, (world.width() + 1) / 2 + range),
                Random.nextInt(world.height() / 2 - range, (world.height() + 1) / 2 + range)
            )
            tile.setNet(Blocks.thoriumWall, Team.crux, 0)
        }
    }
    loop(Dispatchers.game) {
        delay(1000)
        Groups.unit.each{
            //单位护盾自然恢复
            val maxShield = it.maxHealth / 2
            if(it.shield < maxShield && it.type != UnitTypes.oct)
                it.shield += maxShield / 120
        }
    }
    val copperToCoreHealth = state.rules.tags.getInt("@copperToCoreHealth", 10)
    loop(Dispatchers.game) {
        delay(2000)
        val text = "全局资源显示\n[yellow]每个铜能恢复核心${copperToCoreHealth}点血量[]\n" + state.teams.getActive().joinToString("\n") {
            val core = it.core() ?: return@joinToString ""
            "[#${core.team.color}]${core.team.name}[white]   $itemCopper ${core.items.get(Items.copper)}"
        }
        Groups.player.forEach {
            val unit = it.unit()
            Call.infoPopup(
                it.con,
                text +
                        "\n[yellow]$modeSurvival[white] ${unit.shield}/${unit.maxHealth * 0.5f}\n" +
                        "$itemCopper[yellow]$defense[white] ${unit.maxHealth * unit.stack.amount / unit.itemCapacity() / 2}\n" +
                        "[green]$add[white] ${unit.health}/${unit.maxHealth}", 2.013f,
                Align.topLeft, 350, 0, 0, 0
            )
        }
    }
}
val oreCostIncreaseTime = state.rules.tags.getInt("@oreCostIncreaseTime", 240) * 1000f
val startTime by lazy { Time.millis() }
listen<EventType.BlockDestroyEvent> { t ->
    val time = (Time.timeSinceMillis(startTime) / oreCostIncreaseTime).toInt()
    val amount = Random.nextInt(time + 1, time + 3)
    itemDrop(t.tile.worldx(), t.tile.worldy(), "This is a build", amount)
}

listen<EventType.UnitDestroyEvent> { u ->
    itemDrop(u.unit.x, u.unit.y, u.unit, u.unit.stack.amount + (u.unit.itemCapacity() * 0.4f).toInt())
}



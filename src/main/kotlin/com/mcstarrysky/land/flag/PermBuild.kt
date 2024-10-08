package com.mcstarrysky.land.flag

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.util.prettyInfo
import com.mcstarrysky.land.util.registerPermission
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.hanging.HangingPlaceEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem

/**
 * Land
 * com.mcstarrysky.land.flag.PermBuild
 *
 * @author mical
 * @since 2024/8/3 17:26
 */
object PermBuild : Permission {

    @Awake(LifeCycle.ENABLE)
    private fun init() {
        registerPermission()
    }

    override val id: String
        get() = "build"

    override val default: Boolean
        get() = false

    override val worldSide: Boolean
        get() = true

    override val playerSide: Boolean
        get() = true

    override fun generateMenuItem(land: Land, player: OfflinePlayer?): ItemStack {
        return buildItem(XMaterial.GRASS_BLOCK) {
            name = "&f建筑 ${flagValue(land, player)}"
            lore += listOf(
                "&7允许行为:",
                "&8放置方块, 破坏方块, 放置挂饰, 破坏挂饰",
                "",
                "&e左键修改值, 右键取消设置"
            )

            flags += ItemFlag.values().toList()
            if (land.getFlagValueOrNull(id) == true) shiny()
            colored()
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: BlockBreakEvent) {
        LandManager.getLand(e.block.location)?.run {
            if (!hasPermission(e.player, this@PermBuild)) {
                e.isCancelled = true
                e.player.prettyInfo("没有权限, 禁止打破方块/挂画&7\\(标记: ${this@PermBuild.id}\\)")
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: BlockPlaceEvent) {
        LandManager.getLand(e.block.location)?.run {
            if (!hasPermission(e.player, this@PermBuild)) {
                e.isCancelled = true
                e.player.prettyInfo("没有权限, 禁止放置方块/挂画或接触展示框&7\\(标记: ${this@PermBuild.id}\\)")
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: HangingPlaceEvent) {
        val player = e.player ?: return
        LandManager.getLand(e.block.location)?.run {
            if (!hasPermission(player, this@PermBuild)) {
                e.isCancelled = true
                player.prettyInfo("没有权限, 禁止放置方块/挂画或接触展示框&7\\(标记: ${this@PermBuild.id}\\)")
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: HangingBreakByEntityEvent) {
        if (e.remover is Player) {
            val player = e.remover as Player
            LandManager.getLand(e.entity.location.block.location)?.run {
                if (!hasPermission(player, this@PermBuild)) {
                    e.isCancelled = true
                    player.prettyInfo("没有权限, 禁止打破方块/挂画&7\\(标记: ${this@PermBuild.id}\\)")
                }
            }
        }
    }
}
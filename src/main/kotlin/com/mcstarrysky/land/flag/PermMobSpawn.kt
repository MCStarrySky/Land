package com.mcstarrysky.land.flag

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.util.registerPermission
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Monster
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem

/**
 * Land
 * com.mcstarrysky.land.flag.PermMobSpawn
 *
 * @author mical
 * @since 2024/8/3 17:23
 */
object PermMobSpawn : Permission {

    @Awake(LifeCycle.ENABLE)
    private fun init() {
        registerPermission()
    }

    override val id: String
        get() = "mob_spawn"

    override val default: Boolean
        get() = true

    override val worldSide: Boolean
        get() = true

    override val playerSide: Boolean
        get() = false

    override fun generateMenuItem(land: Land, player: OfflinePlayer?): ItemStack {
        return buildItem(XMaterial.ZOMBIE_SPAWN_EGG) {
            name = "&f怪物产生 ${flagValue(land, player)}"
            lore += listOf(
                "&7允许行为:",
                "&8生成怪物",
                "",
                "&e左键修改值, 右键取消设置"
            )
            flags += ItemFlag.values().toList()
            if (land.getFlagValueOrNull(id) == true) shiny()
            colored()
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: EntitySpawnEvent) {
        if (e.entity is Monster) {
            LandManager.getLand(e.entity.location)?.run {
                if (!getFlag(this@PermMobSpawn.id)) {
                    e.isCancelled = true
                }
            }
        }
    }
}
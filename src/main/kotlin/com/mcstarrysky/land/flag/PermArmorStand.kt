package com.mcstarrysky.land.flag

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.util.prettyInfo
import com.mcstarrysky.land.util.registerPermission
import org.bukkit.OfflinePlayer
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.attacker
import taboolib.platform.util.buildItem

/**
 * Land
 * com.mcstarrysky.land.flag.PermArmorStand
 *
 * @author HXS__
 * @since 2024/8/20 23:6
 */
object PermArmorStand : Permission {

    @Awake(LifeCycle.ENABLE)
    private fun init() {
        registerPermission()
    }

    override val id: String
        get() = "armor_stand"

    override val default: Boolean
        get() = false

    override val worldSide: Boolean
        get() = true

    override val playerSide: Boolean
        get() = true

    override fun generateMenuItem(land: Land, player: OfflinePlayer?): ItemStack {
        return buildItem(XMaterial.ARMOR_STAND) {
            name = "&f盔甲架 ${flagValue(land, player)}"
            lore += listOf(
                "&7允许行为:",
                "&8放置盔甲架, 破坏盔甲架",
                "",
                "&e左键修改值, 右键取消设置"
            )

            flags += ItemFlag.values().toList()
            if (land.getFlagValueOrNull(id) == true) shiny()
            colored()
        }
    }


    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: PlayerInteractEvent) {
        if (e.action == Action.RIGHT_CLICK_BLOCK && e.item?.type == org.bukkit.Material.ARMOR_STAND) {
            LandManager.getLand(e.clickedBlock?.location ?: return)?.run {
                if (!hasPermission(e.player, this@PermArmorStand)) {
                    e.isCancelled = true
                    e.player.prettyInfo("没有权限, 禁止触碰盔甲架&7\\(标记: ${this@PermArmorStand.id}\\)")
                }
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: EntityDamageByEntityEvent) {
        if (e.entity is ArmorStand) {
            val player = e.attacker as? Player ?: return
//            val player = Servers.getAttackerInDamageEvent(e) ?: return
            LandManager.getLand(e.entity.location.block.location)?.run {
                if (!hasPermission(player, this@PermArmorStand)) {
                    e.isCancelled = true
                    player.prettyInfo("没有权限, 禁止触碰盔甲架&7\\(标记: ${this@PermArmorStand.id}\\)")
                }
            }
        }
    }
}
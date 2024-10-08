package com.mcstarrysky.land.menu

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.util.MenuRegistry
import com.mcstarrysky.land.util.MenuRegistry.markHeader
import com.mcstarrysky.land.util.MenuRegistry.markPageButton
import com.mcstarrysky.land.util.prettyInfo
import com.mcstarrysky.land.util.skull
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import taboolib.common.util.sync
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.PageableChest
import taboolib.platform.util.buildItem
import taboolib.platform.util.nextChat
import java.util.function.Consumer

/**
 * Land
 * com.mcstarrysky.land.menu.LandCooperatorsMenu
 *
 * @author mical
 * @since 2024/8/3 16:45
 */
object LandPlayerPermsMenu {

    // 为什么要设计那个 elements 参数当返回? 什么脑回路
    fun openMenu(player: Player, land: Land, back: Consumer<Player>?) {//, elements: List<Land>) {
        player.openMenu<PageableChest<OfflinePlayer>>("玩家权限管理") {
            // virtualize()

            map(
                "b===+==pn",
                "#########",
                "#########"
            )

            slotsBy('#')

            elements { land.users.keys.map { Bukkit.getOfflinePlayer(it) } }

            markHeader()
            markPageButton()

            set('b', MenuRegistry.BACK) { back?.accept(player) }

            onGenerate(async = true) { _, p, _, _ ->
                buildItem(XMaterial.PLAYER_HEAD) {
                    name = "&f" + p.name
                    lore += listOf(
                        "&e左击删除, 右击管理权限"
                    )
                    colored()
                }.skull(p.name)
            }

            onClick { e, p ->
                when (e.clickEvent().click) {
                    ClickType.LEFT, ClickType.SHIFT_LEFT -> {
                        land.users -= p.uniqueId
                        openMenu(player, land, back)
                    }
                    ClickType.RIGHT, ClickType.SHIFT_RIGHT -> {
                        LandFlagsMenu.openMenu(player, land, p) {
                            openMenu(player, land, back)
                        }
                    }
                    else -> {
                    }
                }
            }

            set('+', buildItem(XMaterial.NAME_TAG) {
                name = "&a添加合作者"
                lore += listOf(
                    "&7协作者具有领地的大部分权限",
                    "&7但没有领地的设置与修改权"
                )
                colored()
            }) {
                clicker.closeInventory()
                clicker.prettyInfo("请在聊天框输入合作者名字, 或输入'取消'来取消操作!")
                clicker.nextChat { ctx ->
                    if (ctx == "取消")
                        return@nextChat
                    val offlinePlayer = Bukkit.getOfflinePlayerIfCached(ctx)
                    if (offlinePlayer == null) {
                        clicker.prettyInfo("并没有找到这位玩家!")
                        return@nextChat
                    }
                    if (land.users[offlinePlayer.uniqueId] != null) {
                        clicker.prettyInfo("已存在该玩家的信息! 将为您直接打开设置面版!")
                    } else {
                        land.users[offlinePlayer.uniqueId] = HashMap()
                        clicker.prettyInfo("添加成功!")
                    }
                    // land.cooperators += offlinePlayer.uniqueId
                    // 优化: 添加后直接打开对应玩家的权限配置面板, 因为玩家添加就是为了设置的, 直接打开省一步操作, 优化体验
                    // sync { openMenu(clicker, land, back) }
                    sync { LandFlagsMenu.openMenu(clicker, land, offlinePlayer) {
                        openMenu(clicker, land, back)
                    } }
                }
            }

            onClose {
                land.export()
            }
        }
    }
}
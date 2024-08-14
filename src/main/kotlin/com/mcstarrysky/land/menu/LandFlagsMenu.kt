package com.mcstarrysky.land.menu

import com.mcstarrysky.land.data.Land
import com.mcstarrysky.land.flag.Permission
import com.mcstarrysky.land.manager.LandManager
import com.mcstarrysky.land.util.MenuRegistry
import com.mcstarrysky.land.util.MenuRegistry.markHeader
import com.mcstarrysky.land.util.MenuRegistry.markPageButton
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import taboolib.common.util.sync
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.PageableChest
import java.util.function.Consumer

/**
 * Land
 * com.mcstarrysky.land.menu.LandFlagsMenu
 *
 * @author mical
 * @since 2024/8/3 16:27
 */
object LandFlagsMenu {

    fun openMenu(player: Player, land: Land, back: Consumer<Player>?, elements: List<Land>) {
        player.openMenu<PageableChest<Permission>>("领地(ID:${land.id}) ${land.name} 标记管理") {
            virtualize()

            map(
                "b======pn",
                "#########",
                "#########"
            )

            slotsBy('#')

            elements { LandManager.permissions }

            onGenerate(async = true) { _, flag, _, _ -> flag.generateMenuItem(land) }

            markHeader()
            markPageButton()

            set('b', MenuRegistry.BACK) { LandInfoMenu.openMenu(player, land, back, elements) }

            onClick { event, flag ->
                when (event.virtualEvent().clickType) {
                    ClickType.LEFT, ClickType.SHIFT_LEFT -> {
                        // 如果没设置, 就设置成默认值
                        if (land.getFlagOrNull(flag.id) == null) {
                            land.setFlag(flag.id, flag.default)
                        } else {
                            val value = land.getFlag(flag.id)
                            land.setFlag(flag.id, !value)
                        }
                        openMenu(player, land, back, elements)
                    }
                    ClickType.RIGHT, ClickType.SHIFT_RIGHT -> {
                        if (land.getFlagOrNull(flag.id) != null) {
                            land.setFlag(flag.id, null)
                            openMenu(player, land, back, elements)
                        }
                    }
                    else -> {
                    }
                }
            }

            onClose {
                land.export()
            }
        }
    }
}
package com.mcstarrysky.land.util

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import taboolib.platform.util.BukkitSkull
import taboolib.platform.util.isAir

/**
 * Land
 * com.mcstarrysky.land.util.SkullUtils
 *
 * @author mical
 * @since 2024/8/3 16:52
 */
fun ItemStack.skull(skull: String?): ItemStack {
    skull ?: return this
    if (this.isAir) return this
    if (itemMeta !is SkullMeta) return this
    return textured(skull)
}

/**
 * Aiyatsbus
 * com.mcstarrysky.aiyatsbus.module.ui.internal.function.Skull
 *
 * @author mical
 * @since 2024/2/18 12:21
 */
infix fun ItemStack.textured(headBase64: String): ItemStack {
    return BukkitSkull.applySkull(this, headBase64)
}
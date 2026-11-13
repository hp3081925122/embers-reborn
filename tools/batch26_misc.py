import re, pathlib

root = pathlib.Path(__file__).resolve().parent.parent / "src/main/java/com/rekindled/embers"


def edit(rel, *pairs, regex=False):
    f = root / rel
    s = f.read_bytes().decode("utf-8")
    for old, new in pairs:
        n = (re.subn(old, new, s) if regex else (s.replace(old, new), s.count(old)))
        s2, count = n
        if count == 0:
            print("MISS", rel, old[:60])
        s = s2
    f.write_bytes(s.encode("utf-8"))


# pipes: NBT getters return Optional; 26.1 updateShape(level, ticks, pos, dir, neighbourPos, neighbourState, random)
edit("blockentity/PipeBlockEntityBase.java",
     ('nbt.getBoolean("from" + facing.get3DDataValue())', 'nbt.getBooleanOr("from" + facing.get3DDataValue(), false)'),
     ('nbt.getInt("connection" + direction.get3DDataValue())', 'nbt.getIntOr("connection" + direction.get3DDataValue(), 0)'))
edit("block/PipeBlockBase.java",
     (r"facingState\.updateShape\((\w+)\.getOpposite\(\), state, level, facingPos, pos\);",
      r"facingState.updateShape(level, level, facingPos, \1.getOpposite(), pos, state, level.getRandom());"), regex=True)
edit("blockentity/ItemPipeBlockEntityBase.java",
     ('nbt.put("inventory", inventory.serializeNBT(com.rekindled.embers.util.NbtBridge.registries()));',
      'nbt.put("inventory", com.rekindled.embers.util.NbtBridge.save(inventory, com.rekindled.embers.util.NbtBridge.registries()));'))

# ashen armor gems: getCompound returns Optional
edit("item/AshenArmorGemItem.java",
     (r'_getOrCreateTag\(holder\)\.getCompound\("gem" \+ (\w+)\)', r'_getOrCreateTag(holder).getCompoundOrEmpty("gem" + \1)'), regex=True)

# events
edit("EmbersEvents.java",
     ("import net.minecraft.Util;", "import net.minecraft.util.Util;"),
     ("for (ItemStack armor : event.getEntity().getArmorSlots()) {",
      "for (net.minecraft.world.entity.EquipmentSlot armorSlot : net.minecraft.world.entity.EquipmentSlotGroup.ARMOR) {\n\t\t\tItemStack armor = event.getEntity().getItemBySlot(armorSlot);"),
     # 1.20.1 Forge isRepairable() == damageable and not unbreakable for vanilla-style items
     ("event.getLeft().isRepairable()", "event.getLeft().isDamageableItem()"))

# misc
edit("util/Misc.java",
     ("player.getInventory().selected", "player.getInventory().getSelectedSlot()"),
     ("player.getInventory().items.get(slot)", "player.getInventory().getNonEquipmentItems().get(slot)"),
     ("alchemy.bindId(holder.id());", "alchemy.bindId(holder.id().identifier());"))

# Embers.java dead imports
edit("Embers.java",
     ("import net.minecraft.client.renderer.RenderType;", "import net.minecraft.client.renderer.rendertype.RenderType;"),
     ("import net.neoforged.neoforge.common.crafting.CraftingHelper;\r\n", ""))
print("done")

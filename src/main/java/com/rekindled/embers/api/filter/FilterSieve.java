package com.rekindled.embers.api.filter;

import java.util.List;
import java.util.Objects;

import com.rekindled.embers.Embers;
import com.rekindled.embers.util.FilterUtil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class FilterSieve implements IFilter {
	public static final Identifier RESOURCE_LOCATION = Identifier.fromNamespaceAndPath(Embers.MODID, "sieve");

	private ItemStack stack1;
	private ItemStack stack2;
	private int offset;
	private EnumFilterSetting setting;
	private boolean inverted;
	private IFilterComparator comparator;

	public FilterSieve(CompoundTag tag) {
		readFromNBT(tag);
	}

	public FilterSieve(ItemStack stack1, ItemStack stack2, int offset, EnumFilterSetting setting, boolean inverted) {
		this.stack1 = stack1;
		this.stack2 = stack2;
		this.offset = offset;
		this.setting = setting;
		this.inverted = inverted;
		findComparator();
	}

	private void findComparator() {
		if(stack1.isEmpty() && stack2.isEmpty())
			comparator = FilterUtil.ANY;
		else {
			List<IFilterComparator> comparators = FilterUtil.getComparators(stack1, stack2);
			comparator = comparators.get(offset % comparators.size());
		}
	}

	@Override
	public Identifier getType() {
		return RESOURCE_LOCATION;
	}

	@Override
	public boolean acceptsItem(ItemStack stack) {
		return comparator.isBetween(stack1, stack2, stack, setting) != inverted;
	}

	@Override
	public String formatFilter() {
		if(comparator == null)
			return "INVALID COMPARATOR";
		return comparator.format(stack1,stack2,setting,inverted);
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag tag) {
		tag.putString("type",getType().toString());
		tag.put("stack1",com.rekindled.embers.util.NbtBridge.saveOptional(stack1, com.rekindled.embers.util.NbtBridge.registries()));
		tag.put("stack2",com.rekindled.embers.util.NbtBridge.saveOptional(stack2, com.rekindled.embers.util.NbtBridge.registries()));
		tag.putInt("offset",offset);
		tag.putInt("setting",setting.ordinal());
		tag.putBoolean("inverted",inverted);
		tag.putString("comparator", comparator.getName());
		return tag;
	}

	@Override
	public void readFromNBT(CompoundTag tag) {
		stack1 = com.rekindled.embers.util.NbtBridge.parse(com.rekindled.embers.util.NbtBridge.registries(), tag.getCompoundOrEmpty("stack1")).orElse(net.minecraft.world.item.ItemStack.EMPTY);
		stack2 = com.rekindled.embers.util.NbtBridge.parse(com.rekindled.embers.util.NbtBridge.registries(), tag.getCompoundOrEmpty("stack2")).orElse(net.minecraft.world.item.ItemStack.EMPTY);
		offset = tag.getIntOr("offset", 0);
		setting = EnumFilterSetting.values()[tag.getIntOr("setting", 0)];
		inverted = tag.getBooleanOr("invert", false);
		if(tag.contains("comparator"))
			comparator = FilterUtil.getComparator(tag.getStringOr("comparator", ""));
		else
			findComparator();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof FilterSieve)
			return equals((FilterSieve) obj);
		return super.equals(obj);
	}

	private boolean equals(FilterSieve other) {
		return Objects.equals(comparator, other.comparator)
				&& Objects.equals(inverted,other.inverted)
				&& Objects.equals(setting, other.setting)
				&& ItemStack.isSameItemSameComponents(stack1, other.stack1)
				&& ItemStack.isSameItemSameComponents(stack2, other.stack2);
	}
}

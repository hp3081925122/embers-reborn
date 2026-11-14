package com.rekindled.embers.client;

import com.rekindled.embers.gui.GuiCodex;
import com.rekindled.embers.research.ResearchBase;
import com.rekindled.embers.util.sound.ItemUseSound;
import com.rekindled.embers.util.sound.MachineSound;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

//公共代码只能通过这里访问客户端类，避免专用服务端加载客户端类型
public final class ClientAccess {

	private ClientAccess() {}

	public static Player player() {
		return Minecraft.getInstance().player;
	}

	public static Level level() {
		return Minecraft.getInstance().level;
	}

	public static boolean hasControlDown() {
		return Minecraft.getInstance().hasControlDown();
	}

	public static void openCodex() {
		Minecraft.getInstance().setScreen(GuiCodex.instance);
	}

	public static void openCodex(ResearchBase page) {
		GuiCodex.instance.researchPage = page;
		Minecraft.getInstance().setScreen(GuiCodex.instance);
	}

	public static void playMachineSound(BlockEntity tile, int id, SoundEvent soundIn, SoundSource categoryIn, boolean repeat, float volume, float pitch, float xIn, float yIn, float zIn) {
		Minecraft.getInstance().getSoundManager().play(new MachineSound(tile, id, soundIn, categoryIn, repeat, volume, pitch, xIn, yIn, zIn));
	}

	public static void playItemSound(LivingEntity entity, Item item, SoundEvent soundIn, SoundSource categoryIn, boolean repeat, float volume, float pitch) {
		Minecraft.getInstance().getSoundManager().play(new ItemUseSound(entity, item, soundIn, categoryIn, repeat, volume, pitch));
	}
}

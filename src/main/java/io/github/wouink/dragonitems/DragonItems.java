package io.github.wouink.dragonitems;

import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShootableItem;
import net.minecraft.item.TridentItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

@Mod("dragonitems")
public class DragonItems {

	public DragonItems() {
		MinecraftForge.EVENT_BUS.register(Events.class);
	}

	public static class Events {
		private static ItemStack writeTag(ItemStack stack) {
			CompoundNBT tag = stack.getOrCreateTag();
			tag.putBoolean("KilledDragon", true);
			stack.setTag(tag);
			return stack;
		}

		@SubscribeEvent
		public static void onDragonKilled(LivingDeathEvent event) {
			if(event.getEntityLiving().level.isClientSide()) return;
			if(!(event.getEntityLiving() instanceof EnderDragonEntity)) return;
			if(!(event.getSource() instanceof EntityDamageSource)) return;

			EntityDamageSource source = (EntityDamageSource) event.getSource();
			if(!(source.getDirectEntity() instanceof PlayerEntity)) return;

			PlayerEntity playerEntity = (PlayerEntity) source.getEntity();
			ItemStack stack = playerEntity.getItemInHand(Hand.MAIN_HAND);
			ItemStack stackOff = playerEntity.getItemInHand(Hand.OFF_HAND);

			if(source.getMsgId().equals("arrow")) {
				if(!stack.isEmpty() && stack.getItem() instanceof ShootableItem) playerEntity.setItemInHand(Hand.MAIN_HAND, writeTag(stack));
				else if(!stackOff.isEmpty() && stackOff.getItem() instanceof ShootableItem) playerEntity.setItemInHand(Hand.OFF_HAND, writeTag(stackOff));
			}

			else if(source.getMsgId().equals("trident")) {
				if(!stack.isEmpty() && stack.getItem() instanceof TridentItem) playerEntity.setItemInHand(Hand.MAIN_HAND, writeTag(stack));
				else if(!stackOff.isEmpty() && stackOff.getItem() instanceof TridentItem) playerEntity.setItemInHand(Hand.OFF_HAND, writeTag(stackOff));
			}

			else if(!stack.isEmpty()) {
				playerEntity.setItemInHand(Hand.MAIN_HAND, writeTag(stack));
			}
		}

		@SubscribeEvent
		public static void renderTooltip(ItemTooltipEvent event) {
			if(!event.getItemStack().hasTag()) return;

			CompoundNBT tag = event.getItemStack().getTag();
			if(tag.contains("KilledDragon") && tag.getBoolean("KilledDragon")) {
				event.getToolTip().add(1, new TranslationTextComponent("tooltip.killed_dragon").withStyle(TextFormatting.ITALIC).withStyle(TextFormatting.LIGHT_PURPLE));
			}
		}
	}

}

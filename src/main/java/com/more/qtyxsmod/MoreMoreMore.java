package com.more.qtyxsmod;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(MoreMoreMore.MOD_ID)
public class MoreMoreMore {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "moremoremore";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // 保存玩家死亡时的物品栏
    private static final Map<UUID, List<ItemStack>> savedInventories = new HashMap<>();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public MoreMoreMore(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in.
        NeoForge.EVENT_BUS.register(this);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // Register items
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MOD_ID);



    // 自定义护符物品类
    public static class CharmItem extends Item {
        public CharmItem(Properties properties) {
            super(properties);
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft != null) {
                boolean shiftDown = InputConstants.isKeyDown(minecraft.getWindow().getWindow(), InputConstants.KEY_LSHIFT) ||
                                   InputConstants.isKeyDown(minecraft.getWindow().getWindow(), InputConstants.KEY_RSHIFT);
                if (shiftDown) {
                    tooltipComponents.add(Component.translatable(this.getDescriptionId() + ".description"));
                } else {
                    tooltipComponents.add(Component.translatable("item.moremoremore.item_charm.shift_hint"));
                }
            }
        }

        public void onUse(Player player, ItemStack stack) {
            // 直接设置损伤值而非使用EquipmentSlot
            stack.setDamageValue(stack.getDamageValue() + 1);
            if (stack.getDamageValue() >= stack.getMaxDamage()) {
                stack.shrink(1);
            }
        }
    }

    // 物品护符注册
    public static final DeferredHolder<Item, Item> ITEM_CHARM = ITEMS.register("item_charm", () -> new CharmItem(new Item.Properties()
        .stacksTo(1) // 不可堆叠
        .durability(3) // 3次使用次数
    ));

    // 创造模式标签注册
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MORE_MORE_MORE_TAB = CREATIVE_MODE_TABS.register("more_more_more_tab",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.moremoremore"))
            .icon(() -> new ItemStack(ITEM_CHARM.get()))
            .displayItems((params, output) -> {
                output.accept(ITEM_CHARM.get());
            })
            .build()
    );

    // 玩家死亡事件处理
    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            // 检查玩家所有物品栏槽位寻找护符
            List<ItemStack> allItems = new ArrayList<>();
            allItems.addAll(player.getInventory().items);
            allItems.addAll(player.getInventory().armor);
            allItems.addAll(player.getInventory().offhand);
            
            for (ItemStack stack : allItems) {
                if (stack.getItem() instanceof CharmItem) {
                    // 立即使用护符
                    ((CharmItem) stack.getItem()).onUse(player, stack);
                    
                    // 保存玩家UUID和所有物品栏槽位的副本
                    UUID playerUUID = player.getUUID();
                    List<ItemStack> savedItems = new ArrayList<>();
                    // 复制主物品栏
                    for (ItemStack item : player.getInventory().items) {
                        savedItems.add(item.copy());
                    }
                    // 复制盔甲栏
                    for (ItemStack item : player.getInventory().armor) {
                        savedItems.add(item.copy());
                    }
                    // 复制副手栏
                    for (ItemStack item : player.getInventory().offhand) {
                        savedItems.add(item.copy());
                    }
                    savedInventories.put(playerUUID, savedItems);
                    // 清空玩家背包防止物品掉落
                    player.getInventory().clearContent();
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        Player originalPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();
        UUID playerUUID = originalPlayer.getUUID();

        // 仅在死亡重生时恢复物品
        if (event.isWasDeath() && savedInventories.containsKey(playerUUID)) {
            List<ItemStack> savedItems = savedInventories.get(playerUUID);
            
            // 恢复主物品栏（0-35槽位）
            for (int i = 0; i < 36 && i < savedItems.size(); i++) {
                newPlayer.getInventory().setItem(i, savedItems.get(i));
            }
            
            // 恢复盔甲栏（36-39槽位）
            for (int i = 0; i < 4 && (i + 36) < savedItems.size(); i++) {
                newPlayer.getInventory().setItem(36 + i, savedItems.get(36 + i));
            }
            
            // 恢复副手栏（40槽位）
            if (savedItems.size() > 40) {
                newPlayer.getInventory().setItem(40, savedItems.get(40));
            }
            
            // 移除已处理的保存数据
            savedInventories.remove(playerUUID);
        }
    }
}

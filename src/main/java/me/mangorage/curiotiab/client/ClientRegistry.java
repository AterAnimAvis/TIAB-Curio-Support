package me.mangorage.curiotiab.client;

import me.mangorage.curiotiab.client.screens.overlays.CurioTiabHudOverlay;
import me.mangorage.curiotiab.common.network.NetworkHandler;
import me.mangorage.curiotiab.common.network.client.UseTiabPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.network.PacketDistributor;

import static com.mojang.blaze3d.platform.InputConstants.KEY_G;
import static com.mojang.blaze3d.platform.InputConstants.KEY_H;
import static me.mangorage.curiotiab.common.core.Constants.MODID;
import static me.mangorage.curiotiab.common.core.Translatable.*;
import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.FORGE;
import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

@Mod.EventBusSubscriber(modid = MODID, bus = MOD, value = Dist.CLIENT)
public class ClientRegistry {
    public final static KeyMapping useTiab = new KeyMapping(USE_KEY.getKey(), KEY_G, CATEGORY_KEY.getKey());
    public final static KeyMapping hideGUI = new KeyMapping(TOGGLE_KEY.getKey(), KEY_H, CATEGORY_KEY.getKey());

    @SubscribeEvent
    public static void setup(final FMLClientSetupEvent evt) {
        net.minecraftforge.client.ClientRegistry.registerKeyBinding(useTiab);
        net.minecraftforge.client.ClientRegistry.registerKeyBinding(hideGUI);
        OverlayRegistry.registerOverlayTop(MODID, CurioTiabHudOverlay.getInstance());
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = FORGE, value = Dist.CLIENT)
    class Forge {
        @SubscribeEvent
        public static void onKeyInput(final TickEvent.ClientTickEvent evt) {
            if (evt.phase != TickEvent.Phase.END)
                return;

            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                if (ClientRegistry.useTiab.consumeClick())
                    NetworkHandler.NETWORK_CHANNEL.send(PacketDistributor.SERVER.noArg(), UseTiabPacket.getInstance());

                if (ClientRegistry.hideGUI.consumeClick())
                    CurioTiabHudOverlay.getInstance().toggleOverlay();
            }
        }
    }
}

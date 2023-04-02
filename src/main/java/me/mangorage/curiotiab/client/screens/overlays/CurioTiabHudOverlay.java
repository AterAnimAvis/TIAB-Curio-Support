package me.mangorage.curiotiab.client.screens.overlays;

import com.haoict.tiab.utils.lang.Translation;
import com.mojang.blaze3d.vertex.PoseStack;
import me.mangorage.curiotiab.client.config.CurioTiabClientConfig;
import me.mangorage.curiotiab.common.core.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CurioTiabHudOverlay implements IIngameOverlay {


    private final static CurioTiabHudOverlay INSTANCE = new CurioTiabHudOverlay();

    public final static CurioTiabHudOverlay getInstance() {
        return INSTANCE;
    }

    public final static void setHidden(boolean value) {
        getInstance().HIDDEN.set(value);
    }

    public final static boolean isHidden() {
        return getInstance().HIDDEN.get();
    }

    private final AtomicBoolean HIDDEN = new AtomicBoolean(true);
    private int x, y = 5;
    private boolean check = true;

    private CurioTiabHudOverlay() {

    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;

        CurioTiabClientConfig.POSX.set(x);
        CurioTiabClientConfig.POSY.set(y);

        CurioTiabClientConfig.POSX.save();
        CurioTiabClientConfig.POSY.save();
    }

    public void toggleOverlay() {
        HIDDEN.set(!HIDDEN.get());
    }

    @Override
    public void render(ForgeIngameGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        if (HIDDEN.get())
            return;

        if (check) {
            this.x = CurioTiabClientConfig.POSX.get();
            this.y = CurioTiabClientConfig.POSY.get();
            check = !check;
        }

        AtomicInteger yOffset = new AtomicInteger(0);
        Player player = Minecraft.getInstance().player;
        Font font = Minecraft.getInstance().font;

        if (player != null) {
            ItemStack TIAB = Util.getTiabCurioItemStack(player);
            if (TIAB != ItemStack.EMPTY) {
                List<Component> Lines = new ArrayList<>();
                // Lines.add(0, new TextComponent("Time in a Bottle Curio").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GOLD));

                TIAB.getTooltipLines(player, TooltipFlag.Default.NORMAL).forEach((component -> {
                    if (component instanceof TranslatableComponent translatableComponent) {
                        if (Translation.TOOLTIP_STORED_TIME.getTranslationKey().equals(translatableComponent.getKey())) Lines.add(component);
                    }
                }));

                Lines.forEach((component) ->
                        GuiComponent.drawString(poseStack, font, component, this.x, this.y + yOffset.getAndAdd(10), 128));
            }
        }
    }


}
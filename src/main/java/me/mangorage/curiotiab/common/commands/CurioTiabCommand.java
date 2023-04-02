package me.mangorage.curiotiab.common.commands;

import com.haoict.tiab.config.TiabConfig;
import com.haoict.tiab.items.TimeInABottleItem;
import com.haoict.tiab.utils.SendMessage;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mangorage.curiotiab.common.core.Constants;
import me.mangorage.curiotiab.common.core.Util;
import me.mangorage.curiotiab.common.network.NetworkHandler;
import me.mangorage.curiotiab.common.network.server.OpenConfigScreen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.network.PacketDistributor;

public class CurioTiabCommand {
    protected static boolean requires(CommandSourceStack stack) {
        return stack.hasPermission(2);
    }

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("curiotiab")
                .then(Commands.literal("addTime")
                        .requires(CurioTiabCommand::requires)
                        .then(Commands.argument("seconds", IntegerArgumentType.integer())
                                .executes((stack) -> {return processTimeCommand(stack, true);})))
                .then(Commands.literal("removeTime")
                        .requires(CurioTiabCommand::requires)
                        .then(Commands.argument("seconds", IntegerArgumentType.integer())
                                .executes((stack) -> {return processTimeCommand(stack, false);})))
                .then(Commands.literal("configureOverlay")
                        .executes((stack) -> {
                            Entity entity = stack.getSource().getEntity();
                            if (entity instanceof ServerPlayer player)
                                NetworkHandler.NETWORK_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), OpenConfigScreen.getInstance());
                            return 1;
                        })));
    }

    private static int processTimeCommand(CommandContext<CommandSourceStack> ctx, boolean isAdd) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        ServerPlayer player = source.getPlayerOrException();

        int timeToAdd = IntegerArgumentType.getInteger(ctx, "seconds");
        if (timeToAdd < 0) {
            throw new NumberFormatException();
        }

        if (timeToAdd > TiabConfig.COMMON.maxStoredTime.get() / 20) {
            timeToAdd = TiabConfig.COMMON.maxStoredTime.get() / 20;
        }

        ItemStack tiabCurioItemStack = Util.getTiabCurioItemStack(player);
        if (tiabCurioItemStack != ItemStack.EMPTY) {
            TimeInABottleItem item = (TimeInABottleItem) Constants.TIAB_ITEM;
            int currentStoredEnergy = item.getStoredEnergy(tiabCurioItemStack);

            if (!isAdd) {
                if (currentStoredEnergy / 20 < timeToAdd) {
                    timeToAdd = currentStoredEnergy / 20;
                }

                timeToAdd = -timeToAdd;
            }

            item.setStoredEnergy(tiabCurioItemStack, currentStoredEnergy + timeToAdd * 20);
            SendMessage.sendStatusMessage(player, String.format("%s %d seconds", isAdd ? "Added" : "Removed ", timeToAdd));
        } else {
            SendMessage.sendStatusMessage(player, "No Time in a bottle item in curio slot");
        }

        return 1;
    }
}

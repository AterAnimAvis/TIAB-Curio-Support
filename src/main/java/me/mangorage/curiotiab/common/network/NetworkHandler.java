package me.mangorage.curiotiab.common.network;

import me.mangorage.curiotiab.common.core.Constants;
import me.mangorage.curiotiab.common.core.Util;
import me.mangorage.curiotiab.common.network.client.UseTiabPacket;
import me.mangorage.curiotiab.common.network.server.OpenConfigScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class NetworkHandler {
    private final static ArtifactVersion NETWORK_VERSION = new DefaultArtifactVersion("1.0.1");
    public final static SimpleChannel NETWORK_CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Constants.MODID, "main"),
            NETWORK_VERSION ::toString,
            clientVer -> Util.checkMajor(clientVer, NETWORK_VERSION),
            serverVer -> Util.checkMajor(serverVer, NETWORK_VERSION)
    );

    private static int id = 0;
    private static <MSG> void emptyPayload(MSG packet, FriendlyByteBuf buf) {}

    public static void register() {
        NETWORK_CHANNEL.messageBuilder(UseTiabPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .decoder(UseTiabPacket::getInstance)
                .encoder(NetworkHandler::emptyPayload)
                .consumer(UseTiabPacket::handle)
                .add();

        NETWORK_CHANNEL.messageBuilder(OpenConfigScreen.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(OpenConfigScreen::getInstance)
                .encoder(NetworkHandler::emptyPayload)
                .consumer(OpenConfigScreen::handle)
                .add();
    }
}

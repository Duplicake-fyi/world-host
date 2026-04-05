package io.github.gaming32.worldhost.mixin;

import io.github.gaming32.worldhost.WorldHost;
import io.github.gaming32.worldhost.proxy.ProxyChannels;
import io.github.gaming32.worldhost.proxy.ProxyClient;
import io.github.gaming32.worldhost.versions.Components;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.util.HttpUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntegratedServer.class)
public abstract class MixinIntegratedServer {
    @Shadow @Final private Minecraft minecraft;
    @Shadow private int publishedPort;

    @Shadow public abstract boolean isPublished();
    @Shadow public abstract ServerConnectionListener getConnection();

    @Inject(method = "publishServer", at = @At(value = "RETURN", ordinal = 0))
    private void serverIsOpen(CallbackInfoReturnable<Boolean> cir) {
        if (WorldHost.protoClient != null && WorldHost.CONFIG.isEnableFriends()) {
            WorldHost.protoClient.publishedWorld(WorldHost.CONFIG.getFriends());
        }
    }

    @Inject(method = "halt", at = @At("TAIL"))
    private void serverIsClosed(CallbackInfo ci) {
        WorldHost.CONNECTED_PROXY_CLIENTS.values().forEach(ProxyClient::close);
        WorldHost.CONNECTED_PROXY_CLIENTS.clear();
        if (isPublished() && WorldHost.protoClient != null) {
            WorldHost.protoClient.closedWorld(WorldHost.CONFIG.getFriends());
        }
    }

    @Inject(method = "setUUID", at = @At("TAIL"))
    private void shareWorldOnLoad(CallbackInfo ci) {
        if (!WorldHost.shareWorldOnLoad) return;
        WorldHost.shareWorldOnLoad = false;
        final var server = (IntegratedServer)(Object)this;
        final boolean allowCommands = server.getWorldData().isAllowCommands();
        final Component message;
        if (server.publishServer(server.getWorldData().getGameType(), allowCommands, HttpUtil.getAvailablePort())) {
            message = wh$getOpenedMessage();
        } else {
            message = Component.translatable("world-host.share_world.failed").withStyle(ChatFormatting.RED);
        }
        minecraft.getChatListener().handleSystemMessage(message, false);
    }

    @Unique
    private Component wh$getOpenedMessage() {
        final Component port = Components.copyOnClickText(publishedPort);
        if (WorldHost.CONFIG.isEnableFriends()) {
            return Component.translatable("world-host.lan_opened.friends", port);
        }
        final String externalIp = WorldHost.getExternalIp();
        if (externalIp == null) {
            return Component.translatable("commands.publish.started", port);
        }
        return Component.translatable(
            "world-host.lan_opened.no_friends",
            Components.copyOnClickText(externalIp), port
        );
    }

    @Inject(
        method = "publishServer",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerConnectionListener;startTcpServerListener(Ljava/net/InetAddress;I)V",
            shift = At.Shift.AFTER
        )
    )
    private void startProxyChannel(CallbackInfoReturnable<Boolean> cir) {
        WorldHost.proxySocketAddress = ProxyChannels.startProxyChannel(getConnection());
    }
}

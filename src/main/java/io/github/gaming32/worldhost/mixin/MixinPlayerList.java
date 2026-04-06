package io.github.gaming32.worldhost.mixin;

import io.github.gaming32.worldhost.WorldHost;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.NameAndId;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public class MixinPlayerList {
    @Shadow @Final private MinecraftServer server;

    @Inject(method = "isWhiteListed", at = @At("HEAD"), cancellable = true)
    private void whitelistFriends(NameAndId profile, CallbackInfoReturnable<Boolean> cir) {
        if (!WorldHost.CONFIG.isWhitelistJoins()) return;
        if (!server.isSingleplayerOwner(profile) && !WorldHost.isFriend(profile.id())) {
            cir.setReturnValue(false);
        }
    }
}

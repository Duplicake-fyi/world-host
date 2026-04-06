package io.github.gaming32.worldhost.mixin;

import io.github.gaming32.worldhost.WorldHost;
import io.github.gaming32.worldhost.WorldHostComponents;
import io.github.gaming32.worldhost.gui.widget.FriendsButton;
import io.github.gaming32.worldhost.gui.screen.OnlineFriendsScreen;
import io.github.gaming32.worldhost.versions.ButtonBuilder;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JoinMultiplayerScreen.class)
public abstract class MixinJoinMultiplayerScreen extends Screen {
    @Shadow
    @Final
    private Screen lastScreen;

    @Shadow
    @Final
    private HeaderAndFooterLayout layout;

    @Shadow
    protected abstract void repositionElements();

    @Unique
    private Button worldHost$serversTab;

    @Unique
    private FriendsButton worldHost$friendsTab;

    protected MixinJoinMultiplayerScreen(Component component) {
        super(component);
    }

    @Inject(method = "init()V", at = @At("TAIL"))
    private void tabs(CallbackInfo ci) {
        if (!WorldHost.CONFIG.isEnableFriends()) return;

        worldHost$serversTab = addRenderableWidget(
            new ButtonBuilder(WorldHostComponents.SERVERS, button -> {})
                .pos(0, 0)
                .width(100)
                .build()
        );
        worldHost$serversTab.active = false;

        worldHost$friendsTab = addRenderableWidget(new FriendsButton(
            0, 0, 100, 20,
            button -> {
                assert minecraft != null;
                minecraft.setScreen(new OnlineFriendsScreen(lastScreen));
            }
        ));

        layout.setHeaderHeight(60);
        worldHost$positionTabs();
        repositionElements();
    }

    @Inject(method = "repositionElements()V", at = @At("TAIL"))
    private void repositionTabs(CallbackInfo ci) {
        worldHost$positionTabs();
    }

    @Unique
    private void worldHost$positionTabs() {
        if (!WorldHost.CONFIG.isEnableFriends() || worldHost$serversTab == null || worldHost$friendsTab == null) {
            return;
        }

        worldHost$serversTab.setPosition(width / 2 - 102, 40);
        worldHost$friendsTab.setPosition(width / 2 + 2, 40);
    }
}

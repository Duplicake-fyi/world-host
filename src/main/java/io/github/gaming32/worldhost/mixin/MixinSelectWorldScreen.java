package io.github.gaming32.worldhost.mixin;

import io.github.gaming32.worldhost.WorldHost;
import io.github.gaming32.worldhost.WorldHostComponents;
import io.github.gaming32.worldhost.ext.SelectWorldScreenExt;
import io.github.gaming32.worldhost.gui.screen.WorldHostScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SelectWorldScreen.class)
public class MixinSelectWorldScreen extends Screen implements SelectWorldScreenExt {
    @Shadow private WorldSelectionList list;

    @Shadow private Button selectButton;

    @Unique
    private Button wh$shareButton;

    @Unique
    private boolean wh$shareButtonPressed;

    protected MixinSelectWorldScreen(Component component) {
        super(component);
    }

    @Inject(method = "init()V", at = @At("TAIL"))
    private void addShareWorldButton(CallbackInfo ci) {
        wh$shareButtonPressed = false;
        if (!WorldHost.CONFIG.isShareButton()) {
            wh$shareButton = null;
            return;
        }

        if (selectButton != null) {
            selectButton.setMessage(WorldHostComponents.PLAY_TEXT);
        }

        wh$shareButton = addRenderableWidget(
            WorldHostScreen.button(Component.translatable("world-host.share_world"), b ->
                list.getSelectedOpt().ifPresent(worldListEntry -> {
                    wh$shareButtonPressed = true;
                    worldListEntry.joinWorld();
                })
            ).pos(0, 0)
                .width(100)
                .build()
        );
        wh$shareButton.active = false;
        wh$positionShareButton();
    }

    @Inject(method = "repositionElements", at = @At("TAIL"))
    private void repositionShareButton(CallbackInfo ci) {
        wh$positionShareButton();
    }

    //#if MC >= 1.20.3
    @ModifyArg(
        method = "updateButtonStatus",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/Button;setMessage(Lnet/minecraft/network/chat/Component;)V",
            ordinal = 0
        )
    )
    private Component changePlayButtonTextOnUpdate(Component original) {
        return WorldHost.CONFIG.isShareButton() ? WorldHostComponents.PLAY_TEXT : original;
    }
    //#endif

    @Inject(method = "updateButtonStatus", at = @At("TAIL"))
    //#if MC >= 1.20.3
    private void updateShareButtonStatus(LevelSummary levelSummary, CallbackInfo ci) {
        if (wh$shareButton != null) {
            wh$shareButton.active = levelSummary != null && levelSummary.primaryActionActive();
        }
    }
    //#else
    //$$ private void updateShareButtonStatus(
    //$$     boolean active,
        //#if MC > 1.19.2
        //$$ boolean bl2,
        //#endif
    //$$     CallbackInfo ci
    //$$ ) {
    //$$     if (wh$shareButton != null) {
    //$$         wh$shareButton.active = active;
    //$$     }
    //$$ }
    //#endif

    @Override
    public boolean wh$shareButtonPressed() {
        return wh$shareButtonPressed;
    }

    @Unique
    private void wh$positionShareButton() {
        if (wh$shareButton == null) {
            return;
        }
        wh$shareButton.setPosition(width / 2 - 50, height - 84);
    }
}

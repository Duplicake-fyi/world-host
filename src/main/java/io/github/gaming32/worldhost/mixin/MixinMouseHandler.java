package io.github.gaming32.worldhost.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import io.github.gaming32.worldhost.toast.WHToast;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MixinMouseHandler {
    @Shadow @Final private Minecraft minecraft;

    @Shadow private double xpos;

    @Shadow private double ypos;

    @Inject(method = "onButton", at = @At("HEAD"), cancellable = true)
    private void toastClick(long windowPointer, MouseButtonInfo button, int action, CallbackInfo ci) {
        if (action != InputConstants.PRESS) return;
        final Window window = minecraft.getWindow();
        if (WHToast.click(
            xpos * window.getGuiScaledWidth() / window.getScreenWidth(),
            ypos * window.getGuiScaledHeight() / window.getScreenHeight(),
            button.button()
        )) {
            ci.cancel();
        }
    }
}

package io.github.gaming32.worldhost.gui.widget;

import io.github.gaming32.worldhost.WHPlayerSkin;
import io.github.gaming32.worldhost.gui.screen.WorldHostScreen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

//#if MC >= 1.20.0
import net.minecraft.client.gui.GuiGraphics;
//#else
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif

public class WHPlayerSkinWidget extends AbstractWidget {
    private final Supplier<WHPlayerSkin> skin;

    public WHPlayerSkinWidget(
        int x, int y, int width, int height,
        Supplier<WHPlayerSkin> skin,
        EntityModelSet models
    ) {
        super(x, y, width, height, Component.empty());
        this.skin = skin;
    }

    @Override
    public void renderWidget(
        GuiGraphics context,
        int mouseX, int mouseY, float partialTick
    ) {
        final var texture = skin.get().texture();
        final int size = Math.min(getWidth(), getHeight());
        final int drawX = getX() + (getWidth() - size) / 2;
        final int drawY = getY() + (getHeight() - size) / 2;
        WorldHostScreen.blit(context, texture, drawX, drawY, 8, 8, size, size, 8, 8, 64, 64);
        WorldHostScreen.blit(context, texture, drawX, drawY, 40, 8, size, size, 8, 8, 64, 64);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }
}

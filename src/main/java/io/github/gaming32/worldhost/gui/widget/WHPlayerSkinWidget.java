package io.github.gaming32.worldhost.gui.widget;

import io.github.gaming32.worldhost.WHPlayerSkin;
import io.github.gaming32.worldhost.gui.screen.WorldHostScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class WHPlayerSkinWidget extends AbstractWidget {
    private static final int SKIN_TEXTURE_SIZE = 64;
    private static final int FACE_U = 8;
    private static final int FACE_V = 8;
    private static final int HAT_U = 40;
    private static final int HAT_V = 8;

    private final Supplier<WHPlayerSkin> skin;

    public WHPlayerSkinWidget(
        int x, int y, int width, int height,
        Supplier<WHPlayerSkin> skin,
        @SuppressWarnings("unused") EntityModelSet models
    ) {
        super(x, y, width, height, Component.empty());
        this.skin = skin;
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float partialTick) {
        final int size = Math.min(getWidth(), getHeight());
        final int drawX = getX() + (getWidth() - size) / 2;
        final int drawY = getY() + (getHeight() - size) / 2;
        final var texture = skin.get().texture();

        WorldHostScreen.blit(context, texture, drawX, drawY, FACE_U, FACE_V, size, size, 8, 8, SKIN_TEXTURE_SIZE, SKIN_TEXTURE_SIZE);
        WorldHostScreen.blit(context, texture, drawX, drawY, HAT_U, HAT_V, size, size, 8, 8, SKIN_TEXTURE_SIZE, SKIN_TEXTURE_SIZE);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}

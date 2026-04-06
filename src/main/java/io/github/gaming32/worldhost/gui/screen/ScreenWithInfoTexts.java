package io.github.gaming32.worldhost.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.gaming32.worldhost.WorldHost;
import io.github.gaming32.worldhost.plugin.InfoTextsCategory;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

//#if MC >= 1.20.0
import net.minecraft.client.gui.GuiGraphics;
//#else
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif

public abstract class ScreenWithInfoTexts extends WorldHostScreen {
    private final List<Component> infoTexts;

    protected ScreenWithInfoTexts(Component component, InfoTextsCategory category) {
        super(component);
        infoTexts = WorldHost.getInfoTexts(category);
    }

    public final int getInfoTextsAdjustedBottomMargin(int baseMargin) {
        return !infoTexts.isEmpty()
            ? baseMargin + 10 + font.lineHeight * infoTexts.size()
            : baseMargin;
    }

    @Override
    public void render(
        @NotNull
        //#if MC < 1.20.0
        //$$ PoseStack context,
        //#else
        GuiGraphics context,
        //#endif
        int mouseX, int mouseY, float delta
    ) {
        int textY = height - 73;
        for (final Component line : infoTexts.reversed()) {
            drawCenteredString(context, font, line, width / 2, textY, 0xffffff);
            textY -= font.lineHeight;
        }
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() != InputConstants.MOUSE_BUTTON_LEFT) {
            return super.mouseClicked(event, doubleClick);
        }
        final double mouseX = event.x();
        final double mouseY = event.y();
        int textY = height - 73;
        for (final Component line : infoTexts.reversed()) {
            final int textWidth = font.width(line);
            final int textX = width / 2 - textWidth / 2;
            if (mouseX >= textX && mouseX <= textX + textWidth) {
                if (mouseY >= textY && mouseY <= textY + font.lineHeight) {
                    final var style = line.getStyle();
                    if (style != null && style.getClickEvent() != null) {
                        defaultHandleClickEvent(style.getClickEvent(), minecraft, this);
                        return true;
                    }
                }
            }
            textY -= font.lineHeight;
        }
        return super.mouseClicked(event, doubleClick);
    }
}

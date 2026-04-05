package io.github.gaming32.worldhost.gui.widget;

import io.github.gaming32.worldhost.FriendsListUpdate;
import io.github.gaming32.worldhost.WorldHostComponents;
import io.github.gaming32.worldhost.gui.screen.WorldHostScreen;
import io.github.gaming32.worldhost.plugin.OnlineFriend;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

//#if MC >= 1.20.0
import net.minecraft.client.gui.GuiGraphics;
//#else
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif

public final class FriendsButton extends Button implements FriendsListUpdate {
    private int bgX, bgWidth;

    public FriendsButton(int x, int y, int width, int height, OnPress onPress) {
        super(
            x, y, width, height, CommonComponents.EMPTY, onPress
            //#if MC >= 1.19.4
            , DEFAULT_NARRATION
            //#endif
        );
        registerForUpdates();
    }

    @Override
    public void friendsListUpdate(Map<UUID, OnlineFriend> friends) {
        final int online = friends.size();
        final var baseText = WorldHostComponents.FRIENDS.copy().append("  " + online + " ");
        setMessage(baseText);
        final Font textRenderer = Minecraft.getInstance().font;
        bgX = width / 2 - textRenderer.width(baseText) / 2 + textRenderer.width(WorldHostComponents.FRIENDS.copy().append(" "));
        bgWidth = textRenderer.width(" " + online + " ");
    }

    //#if MC < 1.19.4
    //$$ private int getX() {
    //$$     return x;
    //$$ }
    //$$
    //$$ private int getY() {
    //$$     return y;
    //$$ }
    //#endif

    @Override
    protected void renderContents(@NotNull GuiGraphics context, int mouseX, int mouseY, float partialTick) {
        final Font font = Minecraft.getInstance().font;
        final int baseX = getX() + bgX;
        final int baseY = getY() + (height - 12) / 2;
        WorldHostScreen.fill(context, baseX, baseY, baseX + bgWidth, baseY + 12, 0x80000000);
        WorldHostScreen.drawCenteredString(
            context,
            font,
            getMessage(),
            getX() + width / 2,
            getY() + (height - font.lineHeight) / 2,
            active ? 0xffffff : 0xa0a0a0
        );
    }
}

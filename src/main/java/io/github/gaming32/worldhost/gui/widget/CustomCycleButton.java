package io.github.gaming32.worldhost.gui.widget;

import io.github.gaming32.worldhost.gui.screen.WorldHostScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

//#if MC >= 1.19.4
import net.minecraft.client.gui.components.Tooltip;
//#endif

public abstract class CustomCycleButton<T, B extends CustomCycleButton<T, B>> extends Button {
    @Nullable
    private final Component title;
    @Nullable
    private final Map<Component, Component> messages;

    private final Consumer<B> onUpdate;

    private final T[] values;
    private int valueIndex;

    protected CustomCycleButton(
        int x, int y,
        int width, int height,
        @Nullable Component title,
        Consumer<B> onUpdate, T[] values
    ) {
        this(x, y, width, height, title, null, onUpdate, values);
    }

    @SuppressWarnings("this-escape")
    protected CustomCycleButton(
        int x, int y,
        int width, int height,
        @Nullable Component title, @Nullable Component tooltip,
        Consumer<B> onUpdate, T[] values
    ) {
        super(
            x, y, width, height, CommonComponents.EMPTY, b -> {
                @SuppressWarnings("unchecked") final B cycle = (B)b;
                // TODO: Update for 1.21.11 - check shift key state
                // For now, assume shift isn't pressed (cycle forward only)
                final int add = -1; // This will cycle forward since we're using floorMod with negative
                cycle.setValueIndex(Math.floorMod(cycle.getValueIndex() + 1, cycle.getValues().length));
                cycle.getOnUpdate().accept(cycle);
            },
            //#if MC >= 1.19.4
            DEFAULT_NARRATION
            //#else
            //$$ tooltip != null ? WorldHostScreen.onTooltip(tooltip) : NO_TOOLTIP
            //#endif
        );

        this.title = title;
        messages = title != null ? new WeakHashMap<>() : null;

        //#if MC >= 1.19.4
        if (tooltip != null) {
            setTooltip(Tooltip.create(tooltip));
        }
        //#endif

        this.onUpdate = onUpdate;
        this.values = values;
    }

    public T getValue() {
        return values[valueIndex];
    }

    public Consumer<B> getOnUpdate() {
        return onUpdate;
    }

    protected T[] getValues() {
        return values;
    }

    public int getValueIndex() {
        return valueIndex;
    }

    public void setValueIndex(int index) {
        valueIndex = index;
    }

    @Override
    public final void setMessage(@NotNull Component message) {
        throw new UnsupportedOperationException("Cannot set message of " + getClass().getSimpleName());
    }

    @NotNull
    @Override
    public Component getMessage() {
        final Component valueMessage = getValueMessage();
        if (messages == null) {
            return valueMessage;
        }
        assert title != null;
        Component result = messages.get(valueMessage);
        if (result != null) {
            return result;
        }
        result = title.copy().append(": ").append(valueMessage);
        messages.put(valueMessage, result);
        return result;
    }

    @NotNull
    public abstract Component getValueMessage();

    @Override
    protected void renderContents(GuiGraphics context, int mouseX, int mouseY, float partialTick) {
        final int color = active ? 0xffffff : 0xa0a0a0;
        final int textY = getY() + (height - Minecraft.getInstance().font.lineHeight) / 2;
        WorldHostScreen.drawCenteredString(context, Minecraft.getInstance().font, getMessage(), getX() + width / 2, textY, color);
    }
}

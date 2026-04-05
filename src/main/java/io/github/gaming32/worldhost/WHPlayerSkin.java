package io.github.gaming32.worldhost;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

// TODO: Remove in 1.20.2+
public record WHPlayerSkin(
    Identifier texture,
    @Nullable Identifier capeTexture,
    Model model
) {
    public static WHPlayerSkin fromSkinManager(SkinManager skinManager, GameProfile profile) {
        // TODO: Update for 1.21.11 SkinManager API changes
        // For now, return a simple default skin
        final var uuid = profile.id();
        // Use the default Steve/Alex skin based on UUID
        final Identifier skinTexture = Identifier.tryParse("minecraft:textures/entity/steve.png");
        return new WHPlayerSkin(
            skinTexture,
            null,
            Model.WIDE
        );
    }

    public enum Model {
        SLIM, WIDE;

        public static Model byName(String name) {
            return switch (name) {
                case "slim" -> SLIM;
                case null, default -> WIDE;
            };
        }
    }
}

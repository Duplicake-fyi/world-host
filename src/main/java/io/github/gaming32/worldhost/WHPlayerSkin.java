package io.github.gaming32.worldhost;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

// TODO: Remove in 1.20.2+
public record WHPlayerSkin(
    Identifier texture,
    @Nullable Identifier capeTexture,
    Model model
) {
    public static WHPlayerSkin fromSkinManager(SkinManager skinManager, GameProfile profile) {
        final var map = skinManager.getInsecureSkinInformation(profile);
        final MinecraftProfileTexture skin = map.get(MinecraftProfileTexture.Type.SKIN);
        final Identifier skinTexture;
        final String skinModel;
        if (skin != null) {
            skinTexture = skinManager.registerTexture(skin, MinecraftProfileTexture.Type.SKIN);
            skinModel = skin.getMetadata("model");
        } else {
            final var uuid = UUIDUtil.getOrCreatePlayerUUID(profile);
            skinTexture = DefaultPlayerSkin.get(uuid);
            skinModel = DefaultPlayerSkin.getSkinModelName(uuid);
        }
        final MinecraftProfileTexture cape = map.get(MinecraftProfileTexture.Type.CAPE);
        return new WHPlayerSkin(
            skinTexture,
            cape != null ? skinManager.registerTexture(cape, MinecraftProfileTexture.Type.CAPE) : null,
            Model.byName(skinModel)
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

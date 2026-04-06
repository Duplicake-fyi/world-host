package io.github.gaming32.worldhost.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

//#if FABRIC
import net.fabricmc.loader.api.FabricLoader;
//#endif

public class WorldHostMixinPlugin implements IMixinConfigPlugin {
    private static final String MODMENU_MIXIN =
        "io.github.gaming32.worldhost.mixin.modmenu.MixinModMenuEventHandler";

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        //#if FABRIC
        if (MODMENU_MIXIN.equals(mixinClassName)) {
            return FabricLoader.getInstance().isModLoaded("modmenu");
        }
        //#endif
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}

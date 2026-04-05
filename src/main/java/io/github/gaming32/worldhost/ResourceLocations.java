package io.github.gaming32.worldhost;

import net.minecraft.resources.Identifier;

public final class ResourceLocations {
    private static final String WORLD_HOST_NAMESPACE = "world-host";
    //#if MC >= 1.19.4
    private static final Identifier WORLD_HOST_TEMPLATE = namespaced(WORLD_HOST_NAMESPACE, "");
    //#endif

    private ResourceLocations() {
    }

    public static Identifier minecraft(String path) {
        return Identifier.withDefaultNamespace(path);
    }

    public static Identifier worldHost(String path) {
        //#if MC >= 1.19.4
        return WORLD_HOST_TEMPLATE.withPath(path);
        //#else
        //$$ return new ResourceLocation(WORLD_HOST_NAMESPACE, path);
        //#endif
    }

    public static Identifier namespaced(String namespace, String path) {
        return Identifier.tryBuild(namespace, path);
    }
}

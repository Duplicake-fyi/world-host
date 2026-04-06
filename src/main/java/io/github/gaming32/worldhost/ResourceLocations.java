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
        //#if MC >= 1.21
        return Identifier.withDefaultNamespace(path);
        //#else
        //$$ return new ResourceLocation(ResourceLocation.DEFAULT_NAMESPACE, path);
        //#endif
    }

    public static Identifier worldHost(String path) {
        //#if MC >= 1.19.4
        return WORLD_HOST_TEMPLATE.withPath(path);
        //#else
        //$$ return new ResourceLocation(WORLD_HOST_NAMESPACE, path);
        //#endif
    }

    public static Identifier namespaced(String namespace, String path) {
        //#if MC >= 1.21
        return Identifier.fromNamespaceAndPath(namespace, path);
        //#else
        //$$ return new ResourceLocation(namespace, path);
        //#endif
    }
}

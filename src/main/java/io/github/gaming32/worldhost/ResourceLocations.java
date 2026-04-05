package io.github.gaming32.worldhost;

//#if MC >= 1.21.11
//$$ import net.minecraft.resources.Identifier;
//#else
import net.minecraft.resources.ResourceLocation;
//#endif

public final class ResourceLocations {
    private static final String WORLD_HOST_NAMESPACE = "world-host";
    //#if MC >= 1.19.4
    private static final
        //#if MC >= 1.21.11
        //$$ Identifier
        //#else
        ResourceLocation
        //#endif
        WORLD_HOST_TEMPLATE = namespaced(WORLD_HOST_NAMESPACE, "");
    //#endif

    private ResourceLocations() {
    }

    public static
        //#if MC >= 1.21.11
        //$$ Identifier
        //#else
        ResourceLocation
        //#endif
        minecraft(String path) {
        //#if MC >= 1.21.11
        //$$ return Identifier.withDefaultNamespace(path);
        //#elseif MC >= 1.21
        return ResourceLocation.withDefaultNamespace(path);
        //#else
        //$$ return new ResourceLocation(ResourceLocation.DEFAULT_NAMESPACE, path);
        //#endif
    }

    public static
        //#if MC >= 1.21.11
        //$$ Identifier
        //#else
        ResourceLocation
        //#endif
        worldHost(String path) {
        //#if MC >= 1.19.4
        return WORLD_HOST_TEMPLATE.withPath(path);
        //#else
        //$$ return new ResourceLocation(WORLD_HOST_NAMESPACE, path);
        //#endif
    }

    public static
        //#if MC >= 1.21.11
        //$$ Identifier
        //#else
        ResourceLocation
        //#endif
        namespaced(String namespace, String path) {
        //#if MC >= 1.21.11
        //$$ return Identifier.fromNamespaceAndPath(namespace, path);
        //#elseif MC >= 1.21
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
        //#else
        //$$ return new ResourceLocation(namespace, path);
        //#endif
    }
}

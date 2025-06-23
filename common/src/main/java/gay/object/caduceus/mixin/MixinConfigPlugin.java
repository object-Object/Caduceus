package gay.object.caduceus.mixin;

import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame;
import com.google.common.util.concurrent.MoreExecutors;
import dev.architectury.injectables.targets.ArchitecturyTarget;
import gay.object.caduceus.utils.continuation.ContinuationMarkHolder;
import io.github.classgraph.ClassGraph;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.security.Permission;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class MixinConfigPlugin implements IMixinConfigPlugin {
    private static final Logger LOGGER = LogManager.getLogger("caduceus-mixins");
    private static final String BASE_MIXIN_CLASSNAME = "MixinAllContinuationFrames";
    private static String mixinClassname = BASE_MIXIN_CLASSNAME;

    @Override
    public void onLoad(String rawMixinPackage) {
        var mixinPackage = rawMixinPackage.replace('.', '/');

        LOGGER.info("Scanning class graph.");
        var graph = new ClassGraph()
            .enableClassInfo()
            .ignoreClassVisibility()
            .enableAnnotationInfo()
            .disableModuleScanning() // forge :/
            .rejectPackages(
                "com.mojang",
                "net.minecraft",
                "net.fabricmc",
                "net.minecraftforge"
            );
        //noinspection UnreachableCode
        try (
            // on Fabric, scan deadlocks if NOT on main thread
            // on Forge, scan is cancelled if ON main thread
            var scanResult = ArchitecturyTarget.getCurrentTarget().equals("fabric")
                ? graph.scan(MoreExecutors.newDirectExecutorService(), 1)
                : graph.scan()
        ) {
            var targetClasses = scanResult
                .getClassesImplementing(ContinuationFrame.class)
                .filter(info ->
                    !info.hasAnnotation(Mixin.class) // don't mixin mixins
                    && !info.implementsInterface(ContinuationMarkHolder.class) // don't mixin frames that already support marks
                );
            LOGGER.info("Target ContinuationFrame subclasses:\n  {}", String.join("\n  ", targetClasses.getNames()));

            var mixinName = mixinPackage + "/" + mixinClassname;
            LOGGER.info("Creating mixin: {}", mixinName);

            var writer = new ClassWriter(0);

            // move mixin out of template package
            var remapper = new ClassRemapper(writer, new SimpleRemapper(
                mixinPackage + "/template/" + mixinClassname,
                mixinName
            ));

            // update mixin targets
            var visitor = new ClassVisitor(Opcodes.ASM9, remapper) {
                @Override
                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    var annotationVisitor = super.visitAnnotation(descriptor, visible);
                    if (!descriptor.equals("Lorg/spongepowered/asm/mixin/Mixin;")) {
                        return annotationVisitor;
                    }

                    var arrayVisitor = annotationVisitor.visitArray("targets");
                    for (var target : targetClasses.getNames()) {
                        arrayVisitor.visit(null, Type.getType('L' + target.replace('.', '/') + ';'));
                    }
                    arrayVisitor.visitEnd();

                    return new AnnotationVisitor(Opcodes.ASM9, annotationVisitor) {
                        @Override
                        public AnnotationVisitor visitArray(String name) {
                            if (name.equals("value") || name.equals("targets")) {
                                return null;
                            }
                            return super.visitArray(name);
                        }
                    };
                }
            };

            var node = MixinService.getService()
                .getBytecodeProvider()
                .getClassNode(rawMixinPackage + ".template." + mixinClassname);
            node.accept(visitor);

            // FIXME: only works on Fabric :(
            LOGGER.info("Loading generated mixin.");
            var mixinBytes = writer.toByteArray();
            getAddURL().accept(
                new URL(
                    "caduceus-mixins",
                    null,
                    -1,
                    "/",
                    new URLStreamHandler() {
                        @Override
                        protected URLConnection openConnection(URL url) {
                            if (!url.getPath().equals("/" + mixinName + ".class")) {
                                return null;
                            }
                            return new URLConnection(url) {
                                @Override
                                public void connect() {}

                                @Override
                                public InputStream getInputStream() {
                                    return new ByteArrayInputStream(mixinBytes);
                                }

                                @Override
                                public Permission getPermission() {
                                    return null;
                                }
                            };
                        }
                    }
                )
            );
        } catch (Throwable t) {
            LOGGER.error("Mixin generation failed, falling back to default targets :(", t);
            mixinClassname = "template." + BASE_MIXIN_CLASSNAME;
        }
    }

    // see: https://github.com/Chocohead/Fabric-ASM/blob/39abe596bce4e353594bea207eae43eee181ce9c/src/com/chocohead/mm/Plugin.java
    private static Consumer<URL> getAddURL() {
        var loader = MixinConfigPlugin.class.getClassLoader();
        var addURLMethod = getAddURLMethod(loader);
        addURLMethod.setAccessible(true);
        try {
            var handle = MethodHandles.lookup().unreflect(addURLMethod);
            return url -> {
                try {
                    handle.invoke(loader, url);
                } catch (Throwable t) {
                    throw new RuntimeException("Failed to add URL", t);
                }
            };
        } catch (Exception e) {
            throw new RuntimeException("Failed to get handle for " + addURLMethod, e);
        }
    }

    private static Method getAddURLMethod(ClassLoader loader) {
        for (var method : loader.getClass().getDeclaredMethods()) {
            if (
                method.getReturnType() == Void.TYPE
                && method.getParameterCount() == 1
                && method.getParameterTypes()[0] == URL.class
            ) {
                return method;
            }
        }
        throw new RuntimeException("addURL method not found in loader: " + loader);
    }

    @Override
    public List<String> getMixins() {
        return List.of(mixinClassname);
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}

package cc.modlabs.basicx;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
@SuppressWarnings("UnstableApiUsage") // We keep an eye on that.
public class DependencyLoader implements PluginLoader {

    @Override
    public void classloader(PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver maven = new MavenLibraryResolver();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(".dependencies"))))) {
            reader.lines().forEach(dependency -> {
                maven.addDependency(new Dependency(new DefaultArtifact(dependency), null));
            });

            maven.addRepository(new RemoteRepository.Builder(
                    "modlabs",
                    "default",
                    "https://repo-api.modlabs.cc/repo/maven/maven-public/"
            ).build());
            maven.addRepository(new RemoteRepository.Builder(
                    "maven-central",
                    "default",
                    MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR
            ).build());
            maven.addRepository(new RemoteRepository.Builder(
                    "codemc",
                    "default",
                    "https://repo.codemc.io/repository/maven-releases/"
            ).build());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to configure BasicX runtime dependencies", e);
        }

        classpathBuilder.addLibrary(maven);
    }
}

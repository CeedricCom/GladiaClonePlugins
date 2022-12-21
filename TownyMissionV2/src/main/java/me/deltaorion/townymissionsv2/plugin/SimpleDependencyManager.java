package me.deltaorion.townymissionsv2.plugin;

import com.google.common.base.Preconditions;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SimpleDependencyManager implements DependencyManager {

    private final Map<String, Dependency> dependencies;
    private final Plugin master;

    public SimpleDependencyManager(Plugin master) {
        this.dependencies = new HashMap<>();
        this.master = master;
    }

    public void registerDependency(String name, boolean required) {

        Preconditions.checkNotNull(name);

        Dependency dependency = new Dependency(master, name, required);
        dependency.check();

        this.dependencies.put(name, dependency);
    }

    /**
     * Gets a plugin dependency object.
     * <p>
     * Use {@link Dependency#isActive()} to check if the dependency is active
     * CAST {@link Dependency#getPlugin()#getPluginObject()} to get the actual plugin.
     *
     * @param name The name of the dependency
     * @return a dependency object.
     */

    @Nullable
    public Dependency getDependency(String name) {
        return dependencies.get(name);
    }

    public boolean hasDependency(String name) {
        return dependencies.containsKey(name);
    }

    public Set<String> getDependencies() {
        return dependencies.keySet();
    }
}

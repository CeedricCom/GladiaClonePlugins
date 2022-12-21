package me.deltaorion.townymissionsv2.plugin;

import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface DependencyManager {

    @Nullable
    public Dependency getDependency(String name);

    public boolean hasDependency(String name);

    public Set<String> getDependencies();

    public void registerDependency(String name, boolean required);
}

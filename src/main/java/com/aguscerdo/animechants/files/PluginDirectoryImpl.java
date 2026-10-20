package com.aguscerdo.animechants.files;

import com.aguscerdo.animechants.Annotations;
import com.google.inject.Provider;
import net.runelite.client.util.Filepath;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class PluginDirectoryImpl implements PluginDirectory {

    @Inject
    @Annotations.PluginFilePath
    Provider<Filepath> pluginDirectoryProvider;

    private Optional<Filepath> pluginDirectory = Optional.empty();


    @Override
    public Optional<Filepath> getPluginDirectory() {
        if (pluginDirectory.isPresent()) {
            return pluginDirectory;
        }

        pluginDirectory = Optional.ofNullable(pluginDirectoryProvider.get());
        return pluginDirectory;
    }
}

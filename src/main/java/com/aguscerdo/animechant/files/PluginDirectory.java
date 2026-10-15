package com.aguscerdo.animechant.files;

import com.google.inject.ImplementedBy;
import net.runelite.client.util.Filepath;

import java.util.Optional;

@ImplementedBy(PluginDirectoryImpl.class)
public interface PluginDirectory {
    Optional<Filepath> getPluginDirectory();
}

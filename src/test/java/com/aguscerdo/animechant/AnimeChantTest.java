package com.aguscerdo.animechant;

import com.example.ExamplePlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class AnimeChantTest {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(AnimeChantPlugin.class);
        RuneLite.main(args);
    }
}
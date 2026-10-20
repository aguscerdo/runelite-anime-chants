package com.aguscerdo.animechants;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class AnimeChantsTest {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(AnimeChantsPlugin.class);
        RuneLite.main(args);
    }
}
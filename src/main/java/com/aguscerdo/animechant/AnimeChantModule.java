package com.aguscerdo.animechant;

import com.google.inject.AbstractModule;

/**
 * Guice bindings for AnimeChant. Install from the plugin's {@code configure(Binder)}
 * override: {@code binder.install(new AnimeChantModule());}
 */
public class AnimeChantModule extends AbstractModule {
    @Override
    protected void configure() {
        // bind(PresetLoader.class).to(PresetLoaderImpl.class).in(Singleton.class);
        // bind(OverheadTextService.class).to(OverheadTextServiceImpl.class).in(Singleton.class);
        //
        // bind(SpellCastDetector.class).to(SpellCastDetectorImpl.class).in(Singleton.class);
        // bind(SpellTextResolver.class).to(SpellTextResolverImpl.class).in(Singleton.class);
        // bind(SpellPlugin.class).to(SpellPluginImpl.class).in(Singleton.class);
        //
        // bind(SpecialAttackPlugin.class).to(SpecialAttackPluginImpl.class).in(Singleton.class);
        // bind(SpecialAttackTextResolver.class).to(SpecialAttackTextResolverImpl.class).in(Singleton.class);
    }
}

package com.aguscerdo.animechant;

import com.aguscerdo.animechant.presets.PresetLoader;
import com.aguscerdo.animechant.presets.PresetLoaderImpl;
import com.aguscerdo.animechant.spells.SpellCastDetector;
import com.aguscerdo.animechant.spells.SpellCastDetectorImpl;
import com.aguscerdo.animechant.spells.SpellTextResolver;
import com.aguscerdo.animechant.spells.SpellTextResolverImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 * Guice bindings for AnimeChant. Install from the plugin's {@code configure(Binder)}
 * override: {@code binder.install(new AnimeChantModule());}
 */
public class AnimeChantModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(PresetLoader.class).to(PresetLoaderImpl.class).in(Singleton.class);
        bind(SpellCastDetector.class).to(SpellCastDetectorImpl.class).in(Singleton.class);
        bind(SpellTextResolver.class).to(SpellTextResolverImpl.class).in(Singleton.class);
    }
}

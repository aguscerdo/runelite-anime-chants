package com.aguscerdo.animechant;

import com.aguscerdo.animechant.spells.SpellChantResolver;
import com.aguscerdo.animechant.spells.SpellChantResolverImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 * Guice bindings for AnimeChant. Install from the plugin's {@code configure(Binder)}
 * override: {@code binder.install(new AnimeChantModule());}
 */
public class AnimeChantModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(SpellChantResolver.class).to(SpellChantResolverImpl.class).in(Singleton.class);
    }
}

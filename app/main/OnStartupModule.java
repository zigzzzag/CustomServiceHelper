package main;

import com.google.inject.AbstractModule;

/**
 * Created by Zigzag on 10.09.2016.
 */
public class OnStartupModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(OnStartup.class).asEagerSingleton();
    }
}

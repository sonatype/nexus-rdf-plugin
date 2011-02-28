package org.sonatype.nexus.plugin.rdf.internal.guice;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.sonatype.nexus.configuration.application.NexusConfiguration;

@Named
@Singleton
public class StorageDirProvider
    implements Provider<File>
{

    private final NexusConfiguration nexusConfiguration;

    @Inject
    public StorageDirProvider( NexusConfiguration nexusConfiguration )
    {
        this.nexusConfiguration = nexusConfiguration;
    }

    public File get()
    {
        return nexusConfiguration.getWorkingDirectory( "rdf" );
    }

}

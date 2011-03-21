package org.sonatype.nexus.plugin.rdf.internal.guice;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.sonatype.nexus.configuration.application.NexusConfiguration;

@Named
@Singleton
public class NexusConfigurationStorageDirProvider
    implements Provider<File>
{

    private final NexusConfiguration nexusConfiguration;

    @Inject
    public NexusConfigurationStorageDirProvider( NexusConfiguration nexusConfiguration )
    {
        this.nexusConfiguration = nexusConfiguration;
    }

    public File get()
    {
        return nexusConfiguration.getWorkingDirectory( "rdf" );
    }

}

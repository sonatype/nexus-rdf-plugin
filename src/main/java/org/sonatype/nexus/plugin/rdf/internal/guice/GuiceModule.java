package org.sonatype.nexus.plugin.rdf.internal.guice;

import static org.sonatype.sisu.rdf.Names.LOCAL_STORAGE;

import java.io.File;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;

@Named
@Singleton
public class GuiceModule
    implements Module
{

    public void configure( Binder binder )
    {
        binder.bind( File.class )
            .annotatedWith( Names.named( LOCAL_STORAGE ) )
            .toProvider( NexusConfigurationStorageDirProvider.class );
    }

}

package org.sonatype.nexus.plugin.rdf.internal.guice;

import static org.sonatype.sisu.maven.bridge.Names.LOCAL_REPOSITORY_DIR;
import static org.sonatype.sisu.rdf.Names.LOCAL_STORAGE_DIR;

import java.io.File;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.model.resolution.ModelResolver;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.nexus.web.DynamicFilterDefinitions;
import org.sonatype.nexus.web.DynamicServletDefinitions;
import org.sonatype.sisu.maven.bridge.internal.RemoteModelResolver;
import org.sonatype.sisu.maven.bridge.internal.RepositorySystemSessionProvider;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.google.inject.servlet.FilterDefinitions;
import com.google.inject.servlet.ServletDefinitions;

@Named
@Singleton
public class GuiceModule
    implements Module
{

    public void configure( Binder binder )
    {
        binder.bind( File.class ).annotatedWith( Names.named( LOCAL_STORAGE_DIR ) ).toProvider(
            NexusConfigurationStorageDirProvider.class );
        binder.bind( File.class ).annotatedWith( Names.named( LOCAL_REPOSITORY_DIR ) ).toProvider(
            LocalRepositoryProvider.class );
        binder.bind( File.class ).annotatedWith( Names.named( org.sonatype.sisu.rdf.query.Names.HISTORY_HUB_STORAGE ) ).toProvider(
            NexusConfigurationStorageDirProvider.class );

        binder.bind( ModelResolver.class ).to( RemoteModelResolver.class );
        binder.bind( RepositorySystemSession.class ).toProvider( RepositorySystemSessionProvider.class );

        binder.bind( FilterDefinitions.class ).to( DynamicFilterDefinitions.class );
        binder.bind( ServletDefinitions.class ).to( DynamicServletDefinitions.class );
    }

}

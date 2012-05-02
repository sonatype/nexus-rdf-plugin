package org.sonatype.nexus.plugin.rdf.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.FileModelSource;
import org.apache.maven.model.building.ModelBuildingException;
import org.sonatype.nexus.plugins.mavenbridge.NexusMavenBridge;
import org.sonatype.nexus.proxy.NoSuchRepositoryException;
import org.sonatype.nexus.proxy.maven.MavenRepository;
import org.sonatype.nexus.proxy.registry.RepositoryRegistry;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.sisu.rdf.maven.ModelResolver;

@Singleton
@Named
public class NexusModelResolver
    implements ModelResolver
{

    private final NexusMavenBridge nexusMavenBridge;

    private final RepositoryRegistry repositoryRegistry;

    @Inject
    public NexusModelResolver( final NexusMavenBridge nexusMavenBridge,
                               final RepositoryRegistry repositoryRegistry )
    {
        this.nexusMavenBridge = nexusMavenBridge;
        this.repositoryRegistry = repositoryRegistry;
    }

    public Model resolve( final File file, final String... repositories )
        throws ModelBuildingException
    {
        final List<MavenRepository> mavenRepositories = new ArrayList<MavenRepository>();
        if ( repositories != null )
        {
            for ( String repositoryId : repositories )
            {
                try
                {
                    final Repository repository = repositoryRegistry.getRepository( repositoryId );
                    // TODO check that repository is a maven repository
                    final MavenRepository mavenRepository = repository.adaptToFacet( MavenRepository.class );
                    mavenRepositories.add( mavenRepository );
                }
                catch ( NoSuchRepositoryException e )
                {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    // TODO throw some exception
                }
            }
        }
        return nexusMavenBridge.buildModel( new FileModelSource( file ), mavenRepositories );
    }

}

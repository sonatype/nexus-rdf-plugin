package org.sonatype.nexus.plugin.rdf.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.FileModelSource;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.graph.DependencyVisitor;
import org.sonatype.nexus.plugins.mavenbridge.NexusMavenBridge;
import org.sonatype.nexus.proxy.maven.MavenRepository;
import org.sonatype.nexus.proxy.registry.RepositoryRegistry;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.sisu.rdf.maven.MavenResolver;

@Singleton
@Named
public class NexusModelResolver
    implements MavenResolver
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

    @Override
    public Model resolveModel( final File file, final String... repositories )
        throws Exception
    {
        final List<MavenRepository> mavenRepositories = asNexusRepositories( repositories );
        return nexusMavenBridge.buildModel( new FileModelSource( file ), mavenRepositories );
    }

    @Override
    public Dependency[] collectDependencies( final Model model, final String... repositories )
        throws Exception
    {
        final List<MavenRepository> mavenRepositories = asNexusRepositories( repositories );
        final DependencyNode root = nexusMavenBridge.collectDependencies( model, mavenRepositories );
        final List<Dependency> dependencies = new ArrayList<Dependency>();
        root.accept( new DependencyVisitor()
        {
            @Override
            public boolean visitEnter( final DependencyNode node )
            {
                if ( node.getDependency() != null )
                {
                    dependencies.add( toDependency( node.getDependency() ) );
                }
                return true;
            }

            @Override
            public boolean visitLeave( final DependencyNode node )
            {
                return true;
            }
        } );
        return dependencies.toArray( new Dependency[dependencies.size()] );
    }

    private Dependency toDependency( final org.sonatype.aether.graph.Dependency dependency )
    {
        final Dependency mavenDependency = new Dependency();

        mavenDependency.setArtifactId( dependency.getArtifact().getArtifactId() );
        mavenDependency.setClassifier( dependency.getArtifact().getClassifier() );
        mavenDependency.setGroupId( dependency.getArtifact().getGroupId() );
        mavenDependency.setOptional( dependency.isOptional() );
        mavenDependency.setScope( dependency.getScope() );
        mavenDependency.setType( dependency.getArtifact().getExtension() );
        mavenDependency.setVersion( dependency.getArtifact().getVersion() );

        // TODO exclusions?

        return mavenDependency;
    }

    private List<MavenRepository> asNexusRepositories( final String[] repositories )
        throws Exception
    {
        final List<MavenRepository> mavenRepositories = new ArrayList<MavenRepository>();
        if ( repositories != null )
        {
            for ( String repositoryId : repositories )
            {
                final Repository repository = repositoryRegistry.getRepository( repositoryId );
                // TODO check that repository is a maven repository
                final MavenRepository mavenRepository = repository.adaptToFacet( MavenRepository.class );
                mavenRepositories.add( mavenRepository );
            }
        }
        return mavenRepositories;
    }

}

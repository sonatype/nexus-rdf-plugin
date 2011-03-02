/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://www.sonatype.com/products/nexus/attributions.
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.plugin.rdf.internal.sp;

import static java.lang.String.format;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.index.artifact.Gav;
import org.openrdf.model.Statement;
import org.slf4j.Logger;
import org.sonatype.nexus.plugin.rdf.ItemPath;
import org.sonatype.nexus.plugin.rdf.RDFConfiguration;
import org.sonatype.nexus.plugin.rdf.StatementsProducer;
import org.sonatype.sisu.rdf.maven.MavenToRDF;

@Named( value = "artifact" )
@Singleton
public class ArtifactStatementsProducer
    implements StatementsProducer
{

    private final MavenToRDF mavenToRDF;

    @Inject
    private Logger logger;

    @Inject
    public ArtifactStatementsProducer( MavenToRDF mavenToRDF )
    {
        this.mavenToRDF = mavenToRDF;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<Statement> parse( final ItemPath path, RDFConfiguration configuration )
    {
        assert path != null : "Parsed path must be specified (cannot be null)";

        final Gav gav = path.gav();

        if ( path.gav() == null || path.isPathOfCustomMetadata() || path.gav().isHash() || path.gav().isSignature() )
        {
            return Collections.emptyList();
        }
        
        logger.debug( String.format( "Producing artifact RDF statements for item [%s]", path ) );
        
        try
        {
            Collection<Statement> statements =
                mavenToRDF.artifact( gav.getGroupId(), gav.getArtifactId(), gav.getVersion(), gav.getClassifier(),
                    gav.getExtension() );
            return statements;
        }
        catch ( Exception ignore )
        {
            logger.warn(
                  format(
                      "Could not index content of [%s] because [%s]. Skipped", gav.getName(), ignore.getMessage()
                  ) );
            return Collections.emptyList();
        }
    }

}
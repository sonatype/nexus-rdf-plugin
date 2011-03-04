/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://www.sonatype.com/products/nexus/attributions.
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.plugin.rdf;

import java.io.File;

import org.apache.maven.index.artifact.Gav;
import org.apache.maven.index.artifact.GavCalculator;
import org.apache.maven.index.artifact.IllegalArtifactCoordinateException;
import org.sonatype.nexus.plugin.rdf.internal.Utils;
import org.sonatype.nexus.proxy.maven.MavenRepository;
import org.sonatype.sisu.rdf.ItemPath;

/**
 * Path to an item in a maven repository.
 * 
 * @author Alin Dreghiciu
 */
public class NexusItemPath
    extends ItemPath
{

    /**
     * Name of directory storing metadata.
     */
    public static final String METADATA_DIR = "/.meta";

    /**
     * Maven repository containing the item.
     */
    private final MavenRepository repository;

    /**
     * Constructor.
     * 
     * @param repository maven repository containing the item
     * @param repositoryRoot repository root file
     * @param path path to item in repository.
     */
    public NexusItemPath( final MavenRepository repository,
                          final File repositoryRoot,
                          final String path )
    {
        super(repositoryRoot,path,repository.getGavCalculator());
        
        assert repository != null : "Item repository must be specified (cannot be null)";

        this.repository = repository;
    }

    /**
     * Getter.
     * 
     * @return true if the path is a path to a custom metadata file.
     */
    public boolean isPathOfCustomMetadata()
    {
        return path().startsWith( METADATA_DIR );
    }

    /**
     * Getter.
     * 
     * @return item repository
     */
    public MavenRepository repository()
    {
        return repository;
    }

    /**
     * Create a new item path relative to passed item path root.
     * 
     * @param path path to item in repository.
     */
    public NexusItemPath relative( final File file )
    {
        assert file != null : "File must be specified (cannot be null)";

        return new NexusItemPath( repository(), repositoryRoot(), Utils.getRelativePath( repositoryRoot(), file ) );
    }

    /**
     * Calculate the GAV for specified path.
     * 
     * @return calculated GAV or null if GAV cannot be calculated
     */
    @Override
    protected Gav calculateGav( String path, GavCalculator gavCalculator )
    {
        Gav gav = null;
        try
        {
            if ( path.startsWith( METADATA_DIR ) )
            {
                gav = repository.getGavCalculator().pathToGav( path.replace( METADATA_DIR, "" ) );
            }
            else
            {
                gav = super.calculateGav( path, gavCalculator );
            }
        }
        catch ( IllegalArtifactCoordinateException ignore )
        {
            // ignore
        }
        return gav;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return String.format( "%s:%s", repository.getId(), path() );
    }

}

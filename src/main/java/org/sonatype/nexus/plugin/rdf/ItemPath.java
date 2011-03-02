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
import org.apache.maven.index.artifact.IllegalArtifactCoordinateException;
import org.sonatype.nexus.plugin.rdf.internal.Utils;
import org.sonatype.nexus.proxy.maven.MavenRepository;

/**
 * Path to an item in a maven repository.
 * 
 * @author Alin Dreghiciu
 */
public class ItemPath
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
     * Path to item in repository.
     */
    private final String path;

    /**
     * Calculated GAV for item (or null if an invalid path).
     */
    private final Gav gav;

    /**
     * Path to file on file system.
     */
    private final File file;

    /**
     * Repository root file.
     */
    private final File repositoryRoot;

    /**
     * Constructor.
     * 
     * @param repository maven repository containing the item
     * @param repositoryRoot repository root file
     * @param path path to item in repository.
     */
    public ItemPath( final MavenRepository repository,
                     final File repositoryRoot,
                     final String path )
    {
        assert repository != null : "Item repository must be specified (cannot be null)";
        assert repositoryRoot != null : "Item repository root must be specified (cannot be null)";
        assert path != null : "Item path must be specified (cannot be null)";
        assert path.trim().length() != 0 : "Item path must be specified (cannot be empty)";

        this.repository = repository;
        this.repositoryRoot = repositoryRoot;
        String canonicalPath = path.replace( "\\", "/" );
        if ( canonicalPath.startsWith( "/" ) )
        {
            canonicalPath = canonicalPath.substring( 1 );
        }
        this.path = canonicalPath;
        this.file = new File( repositoryRoot, canonicalPath );
        gav = calculateGav();
    }

    /**
     * Getter.
     * 
     * @return item path
     */
    public String path()
    {
        return path;
    }

    /**
     * Getter.
     * 
     * @return true if the path is a path to a custom metadata file.
     */
    public boolean isPathOfCustomMetadata()
    {
        return path.startsWith( METADATA_DIR );
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
     * Getter.
     * 
     * @return item repository root file
     */
    public File repositoryRoot()
    {
        return repositoryRoot;
    }

    /**
     * Getter.
     * 
     * @return item GAV or null if GAV cannot be calculated
     */
    public Gav gav()
    {
        return gav;
    }

    /**
     * @return path to file on file system.
     */
    public File file()
    {
        return file;
    }

    /**
     * Create a new item path relative to passed item path root.
     * 
     * @param path path to item in repository.
     */
    public ItemPath relative( final File file )
    {
        assert path != null : "Item path must be specified (cannot be null)";
        assert file != null : "File must be specified (cannot be null)";

        return new ItemPath( repository(), repositoryRoot(), Utils.getRelativePath( repositoryRoot, file ) );
    }

    /**
     * Calculate the GAV for specified path.
     * 
     * @return calculated GAV or null if GAV cannot be calculated
     */
    private Gav calculateGav()
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
                gav = repository.getGavCalculator().pathToGav( path );
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
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ItemPath ) )
        {
            return false;
        }

        ItemPath itemPath = (ItemPath) o;

        if ( !path.equals( itemPath.path ) )
        {
            return false;
        }
        if ( !repository.equals( itemPath.repository ) )
        {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        int result = repository.hashCode();
        result = 31 * result + path.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return String.format( "%s:%s", repository.getId(), path );
    }

}

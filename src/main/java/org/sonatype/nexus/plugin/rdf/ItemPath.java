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
    private final MavenRepository m_repository;
    /**
     * Path to item in repository.
     */
    private final String m_path;
    /**
     * Calculated GAV for item (or null if an invalid path).
     */
    private final Gav m_gav;
    /**
     * Path to file on file system.
     */
    private final File m_file;

    /**
     * Constructor.
     *
     * @param repository maven repository containing the item
     * @param path       path to item in repository.
     */
    public ItemPath( final MavenRepository repository,
                     final String path )
    {
        assert repository != null : "Item repository must be specified (cannot be null)";
        assert path != null : "Item path must be specified (cannot be null)";
        assert path.trim().length() != 0 : "Item path must be specified (cannot be empty)";

        m_repository = repository;
        m_file = new File( path );
        m_path = path;
        m_gav = calculateGav();
    }
    
    /**
     * Constructor.
     *
     * @param repository maven repository containing the item
     * @param repositoryRoot repository root file
     * @param path       path to item in repository.
     */
    public ItemPath( final MavenRepository repository,
                     final File repositoryRoot,
                     final String path )
    {
        assert repository != null : "Item repository must be specified (cannot be null)";
        assert repositoryRoot != null : "Item repository root must be specified (cannot be null)";
        assert path != null : "Item path must be specified (cannot be null)";
        assert path.trim().length() != 0 : "Item path must be specified (cannot be empty)";

        m_repository = repository;
        m_file = new File(repositoryRoot, path);
        m_path = path;
        m_gav = calculateGav();
    }

    /**
     * Getter.
     *
     * @return item path
     */
    public String path()
    {
        return m_path;
    }

    /**
     * Getter.
     *
     * @return true if the path is a path to a custom metadata file.
     */
    public boolean isPathOfCustomMetadata()
    {
        return m_path.startsWith( METADATA_DIR );
    }

    /**
     * Getter.
     *
     * @return item repository
     */
    public MavenRepository repository()
    {
        return m_repository;
    }

    /**
     * Getter.
     *
     * @return item GAV or null if GAV cannot be calculated
     */
    public Gav gav()
    {
        return m_gav;
    }
    
    /**
     * @return path to file on file system.
     */
    public File file()
    {
        return m_file;
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
            if ( m_path.startsWith( METADATA_DIR ) )
            {
                gav = m_repository.getGavCalculator().pathToGav( m_path.replace( METADATA_DIR, "" ) );
            }
            else
            {
                gav = m_repository.getGavCalculator().pathToGav( m_path );
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

        if ( !m_path.equals( itemPath.m_path ) )
        {
            return false;
        }
        if ( !m_repository.equals( itemPath.m_repository ) )
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
        int result = m_repository.hashCode();
        result = 31 * result + m_path.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return String.format( "%s:%s", m_repository.getId(), m_path );
    }

}

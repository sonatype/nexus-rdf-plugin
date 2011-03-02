package org.sonatype.nexus.plugin.rdf.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.sonatype.nexus.proxy.LocalStorageException;
import org.sonatype.nexus.proxy.ResourceStoreRequest;
import org.sonatype.nexus.proxy.item.RepositoryItemUid;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.proxy.storage.local.fs.DefaultFSLocalRepositoryStorage;

public class Utils
{

    public static File getRepositoryLocalStorageAsFile( Repository repository )
        throws LocalStorageException
    {
        if ( repository.getLocalUrl() != null
            && repository.getLocalStorage() instanceof DefaultFSLocalRepositoryStorage )
        {
            File baseDir =
                ( (DefaultFSLocalRepositoryStorage) repository.getLocalStorage() ).getBaseDir( repository,
                    new ResourceStoreRequest( RepositoryItemUid.PATH_ROOT ) );
            return baseDir;
        }

        throw new LocalStorageException( String.format( "Repository [%s] does not have an local storage",
            repository.getId() ) );
    }

    public static File safeGetRepositoryLocalStorageAsFile( Repository repository, Logger logger )
    {
        try
        {
            return getRepositoryLocalStorageAsFile( repository );
        }
        catch ( LocalStorageException e )
        {
            logger.warn( String.format( "Cannot determine repository [%s] basedir", repository.getId() ), e );
        }
        return null;
    }

    public static File getFile( final Repository repository, final String path )
    {
        try
        {
            final ResourceStoreRequest request = new ResourceStoreRequest( path );
            final File content =
                ( (DefaultFSLocalRepositoryStorage) repository.getLocalStorage() ).getFileFromBase( repository, request );
            return content;
        }
        catch ( final LocalStorageException e )
        {
            throw new RuntimeException( e );
        }
    }

    public static String getRelativePath( final File fromFile, final File toFile )
    {
        final String[] fromSegments = getReversePathSegments( fromFile );
        final String[] toSegments = getReversePathSegments( toFile );

        String relativePath = "";
        int i = fromSegments.length - 1;
        int j = toSegments.length - 1;

        // first eliminate common root
        while ( ( i >= 0 ) && ( j >= 0 ) && ( fromSegments[i].equals( toSegments[j] ) ) )
        {
            i--;
            j--;
        }

        for ( ; i >= 0; i-- )
        {
            relativePath += ".." + File.separator;
        }

        for ( ; j >= 1; j-- )
        {
            relativePath += toSegments[j] + File.separator;
        }

        relativePath += toSegments[j];

        return relativePath;
    }

    private static String[] getReversePathSegments( final File file )
    {
        final List<String> paths = new ArrayList<String>();

        File segment;
        try
        {
            segment = file.getCanonicalFile();
            while ( segment != null )
            {
                paths.add( segment.getName() );
                segment = segment.getParentFile();
            }
        }
        catch ( final IOException e )
        {
            return null;
        }
        return paths.toArray( new String[paths.size()] );
    }

}
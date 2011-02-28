package org.sonatype.nexus.plugin.rdf.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.sonatype.nexus.proxy.LocalStorageException;
import org.sonatype.nexus.proxy.ResourceStoreRequest;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.proxy.storage.local.fs.DefaultFSLocalRepositoryStorage;

class Utils
{

    static File getFile( final Repository repository, final String path )
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

    static String getRelativePath( final File fromFile, final File toFile )
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
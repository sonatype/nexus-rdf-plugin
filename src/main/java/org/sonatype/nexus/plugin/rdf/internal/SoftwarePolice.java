package org.sonatype.nexus.plugin.rdf.internal;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.sonatype.nexus.proxy.repository.Repository;

@Named
@Singleton
public class SoftwarePolice
{

    @Inject
    Logger logger;

    @Inject
    public SoftwarePolice(QueryDi)
    {
    }

    public void check( final Repository repository, final String vulnerabilitiesSPARQLEndpoints )
    {
        logger.debug( String.format(
            "About to check vulnerabilities from repository [%s] against SPARQL endpoints [%s]",
            repository.getId(), vulnerabilitiesSPARQLEndpoints ) );
    }

}

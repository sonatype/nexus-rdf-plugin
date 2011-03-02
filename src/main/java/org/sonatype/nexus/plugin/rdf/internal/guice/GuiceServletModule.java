package org.sonatype.nexus.plugin.rdf.internal.guice;

import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.sisu.sparql.endpoint.SparqlServlet;

import com.google.inject.servlet.ServletModule;

@Named
@Singleton
public class GuiceServletModule
    extends ServletModule
{

    @Override
    protected void configureServlets()
    {
        serve( "/sparql/*" ).with( SparqlServlet.class );
    }

}

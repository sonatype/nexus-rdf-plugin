package org.sonatype.nexus.plugin.rdf.internal;

import org.codehaus.plexus.component.annotations.Component;
import org.sonatype.nexus.feeds.NexusArtifactEvent;
import org.sonatype.nexus.rest.feeds.sources.NexusArtifactEventEntryBuilder;
import org.sonatype.nexus.rest.feeds.sources.SyndEntryBuilder;

@Component( role = SyndEntryBuilder.class, hint = "softwarePolice" )
public class SoftwarePoliceNexusArtifactEventEntryBuilder
    extends NexusArtifactEventEntryBuilder
{

    @Override
    protected String buildDescriptionMsgItem( NexusArtifactEvent event )
    {
        return "";
    }

    @Override
    protected String buildDescriptionMsgAction( NexusArtifactEvent event )
    {
        return "";
    }

}

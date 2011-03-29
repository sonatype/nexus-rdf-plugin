package org.sonatype.nexus.plugin.rdf.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.feeds.FeedRecorder;
import org.sonatype.nexus.feeds.NexusArtifactEvent;
import org.sonatype.nexus.rest.feeds.sources.AbstractNexusItemEventFeedSource;
import org.sonatype.nexus.rest.feeds.sources.FeedSource;
import org.sonatype.nexus.rest.feeds.sources.SyndEntryBuilder;
import org.sonatype.nexus.timeline.RepositoryIdTimelineFilter;
import org.sonatype.timeline.TimelineFilter;

@Named( "softwarePoliceVulnerabilities" )
@Singleton
public class SoftwarePoliceVulnerabilitiesFeedSource
    extends AbstractNexusItemEventFeedSource
    implements FeedSource
{

    public static final String ACTION = "softwarePoliceVulnerabilities";

    public static final String CHANNEL_KEY = "softwarePoliceVulnerabilities";

    @Inject
    private FeedRecorder feedRecorder;

    @Named( "softwarePolice" )
    @Inject
    private SyndEntryBuilder<NexusArtifactEvent> entryBuilder;

    public String getFeedKey()
    {
        return CHANNEL_KEY;
    }

    public String getFeedName()
    {
        return getDescription();
    }

    @Override
    public String getDescription()
    {
        return "Software Police Vulnerabilities in all Nexus repositories.";
    }

    @Override
    public List<NexusArtifactEvent> getEventList( Integer from, Integer count, Map<String, String> params )
    {
        Set<String> repositoryIds = getRepoIdsFromParams( params );
        TimelineFilter filter =
            ( repositoryIds == null || repositoryIds.isEmpty() ) ? null
                            : new RepositoryIdTimelineFilter( repositoryIds );

        return feedRecorder.getNexusArtifectEvents(
            new HashSet<String>( Arrays.asList( new String[] { ACTION } ) ), from, count, filter );
    }

    @Override
    public String getTitle()
    {
        return "Projects with vulnerabilities";
    }

    @Override
    public SyndEntryBuilder<NexusArtifactEvent> getSyndEntryBuilder( NexusArtifactEvent event )
    {
        return entryBuilder;
    }

}
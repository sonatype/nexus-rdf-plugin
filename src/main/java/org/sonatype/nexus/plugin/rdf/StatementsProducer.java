/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://www.sonatype.com/products/nexus/attributions.
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.plugin.rdf;

import java.util.Collection;

import org.openrdf.model.Statement;
import org.sonatype.plugin.ExtensionPoint;

/**
 * An statements producer is able to parse a maven repository item into a collection of index statements.
 *
 * @author Alin Dreghiciu
 */
@ExtensionPoint
public interface StatementsProducer
{

    /**
     * Parses an repository item specified by its GAV and produces index statements.
     *
     * @param path path of item to be parsed
     *
     * @return index statements. It can be null or empty, if the producer does not handle the gav.
     */
    Collection<Statement> parse( ItemPath path );

}
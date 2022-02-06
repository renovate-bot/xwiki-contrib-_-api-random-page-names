/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.contrib.validation.uuidpages;

import java.util.UUID;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.configuration.ConfigurationSource;
import org.xwiki.model.EntityType;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.model.reference.EntityReferenceProvider;
import org.xwiki.model.validation.AbstractEntityNameValidation;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;

/**
 * A name strategy that will create a unique page name from an UUID,
 * without repect to the proposed name.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Singleton
@Named(UUIDEntityNameValidation.COMPONENT_NAME)
public class UUIDEntityNameValidation extends AbstractEntityNameValidation implements Initializable
{
    protected static final String COMPONENT_NAME = "UUIDEntityNameValidation";
    private static final Pattern VALIDATION_PATTERN = Pattern
        .compile("^\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}$");

    @Inject
    private Logger logger;

    @Inject
    @Named("default")
    private EntityReferenceProvider defaultEntities;

    @Inject
    @Named("xwikiproperties")
    private ConfigurationSource config;
    
    private NoArgGenerator uuidGenerator;

    private String defaultPageName;

    /**
     * Initialize with a default generator using the first MAC address found.
     */
    public void initialize()
    {
        uuidGenerator = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
        EntityReference defaultPage = defaultEntities.getDefaultReference(EntityType.DOCUMENT);
        defaultPageName = (defaultPage != null) ? defaultPage.getName() : "WebHome";
    }

    @Override
    public String transform(String name)
    {
        if (isValid(name)) {
            return name;
        }
        UUID uuid = uuidGenerator.generate();
        logger.debug("transformed [{}] to uuid [{}]", name, uuid);
        return uuid.toString();
    }

    @Override
    public boolean isValid(String name)
    {
        boolean valid = (name == null) || VALIDATION_PATTERN.matcher(name).matches();
        logger.trace("is name [{}] valid? [{}]", name, valid);
        return valid;
    }

    /**
     * Check is the page name part of an entity is valid.
     * Unlike the base class it only checks the current page name, not the complete path.
     * @param entityReference the reference to be tested
     * @return true if the page name is a valid (well-formed) UUID
     */
    @Override
    public boolean isValid(EntityReference entityReference)
    {
        if (entityReference == null) {
            return true;
        }

        boolean result = true;
        switch (entityReference.getType()) {
            case DOCUMENT:
                String name = entityReference.getName();
                if (defaultPageName.equals(name)) {
                    EntityReference parent = entityReference.getParent();
                    if (parent != null) {
                        name = parent.getName();
                    }
                }
                result = isValid(name);
                break;
            default:
                // nothing to do
                break;
        }

        logger.trace("entity [{}] valid? [{}]", entityReference, result);
        return result;
    }

    /**
     * The uuid generator in use.
     * @return the uuidGenerator
     */
    public NoArgGenerator getUuidGenerator()
    {
        return uuidGenerator;
    }

    /**
     * Allows to change the uuid generator.
     * @param uuidGenerator the new uuidGenerator
     */
    public void setUuidGenerator(NoArgGenerator uuidGenerator)
    {
        this.uuidGenerator = uuidGenerator;
    }

}

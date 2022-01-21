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
    @Named("xwikiproperties")
    private ConfigurationSource config;
    
    private NoArgGenerator uuidGenerator;

    /**
     * Initialize with a default generator using the first MAC address found.
     */
    public void initialize()
    {
        uuidGenerator = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
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
        return VALIDATION_PATTERN.matcher(name).matches();
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

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

import org.junit.jupiter.api.Test;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ComponentTest
public class UUIDEntityNameValidationTest
{
    @InjectMockComponents
    private UUIDEntityNameValidation validator;
    
    @Test
    public void transformIsValid()
    {
        String input = "ignored";
        String name1 = validator.transform(input);
        // System.err.println("we got uuid "+name1);
        assertTrue(validator.isValid(name1));
    }

    @Test
    public void noTransformIfAlreadyUUID()
    {
        String input = "ignored";
        String name1 = validator.transform(input);
        String name2 = validator.transform(name1);
        assertEquals(name1, name2);
    }
    
    @Test
    public void useDifferentGenerator()
    {
        EthernetAddress dummy = new EthernetAddress(0L);
        TimeBasedGenerator otherGenerator = Generators.timeBasedGenerator(dummy);
        assertNotEquals(validator.getUuidGenerator(), otherGenerator);
        validator.setUuidGenerator(otherGenerator);
        String uuid = validator.transform("");
        assertTrue(uuid.endsWith("-000000000000"));
        assertSame(otherGenerator, validator.getUuidGenerator());
    }

}

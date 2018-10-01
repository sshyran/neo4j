/*
 * Copyright (c) 2002-2018 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.kernel.impl.transaction.state;

import org.junit.Test;

import org.neo4j.internal.kernel.api.schema.IndexProviderDescriptor;
import org.neo4j.kernel.api.index.IndexProvider;
import org.neo4j.kernel.impl.api.index.IndexProviderNotFoundException;
import org.neo4j.kernel.impl.util.Dependencies;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultIndexProviderMapTest
{
    @Test
    public void shouldNotSupportMultipleProvidersWithSameDescriptor()
    {
        // given
        IndexProviderDescriptor descriptor = new IndexProviderDescriptor( "provider", "1.2" );
        IndexProvider provider1 = mock( IndexProvider.class );
        when( provider1.getProviderDescriptor() ).thenReturn( descriptor );
        IndexProvider provider2 = mock( IndexProvider.class );
        when( provider2.getProviderDescriptor() ).thenReturn( descriptor );
        when( provider1.compareTo( provider2 ) ).thenReturn( 1 );

        Dependencies dependencies = new Dependencies();
        dependencies.satisfyDependency( provider1 );
        dependencies.satisfyDependency( provider2 );

        // when
        assertThrows( IllegalArgumentException.class, () -> new DefaultIndexProviderMap( dependencies ).init() );
    }

    @Test
    public void shouldThrowOnLookupOnUnknownProvider()
    {
        // given
        IndexProvider provider = mock( IndexProvider.class );
        when( provider.getProviderDescriptor() ).thenReturn( new IndexProviderDescriptor( "provider", "1.2" ) );
        Dependencies dependencies = new Dependencies();
        dependencies.satisfyDependency( provider );

        // when
        DefaultIndexProviderMap defaultIndexProviderMap = new DefaultIndexProviderMap( dependencies );
        defaultIndexProviderMap.init();
        assertThrows( IndexProviderNotFoundException.class, () -> defaultIndexProviderMap.lookup( new IndexProviderDescriptor( "provider2", "1.2" ) ) );
    }
}
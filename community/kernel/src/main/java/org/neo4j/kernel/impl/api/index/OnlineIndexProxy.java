/**
 * Copyright (c) 2002-2014 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
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
package org.neo4j.kernel.impl.api.index;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Future;

import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.kernel.api.exceptions.index.IndexPopulationFailedKernelException;
import org.neo4j.kernel.api.index.IndexAccessor;
import org.neo4j.kernel.api.index.IndexDescriptor;
import org.neo4j.kernel.api.index.IndexReader;
import org.neo4j.kernel.api.index.IndexUpdater;
import org.neo4j.kernel.api.index.InternalIndexState;
import org.neo4j.kernel.api.index.SchemaIndexProvider;

import static org.neo4j.helpers.FutureAdapter.VOID;
import static org.neo4j.kernel.impl.transaction.log.TransactionIdStore.MAX_TX_ID;

public class OnlineIndexProxy implements IndexProxy
{
    private final IndexDescriptor descriptor;
    final IndexAccessor accessor;
    private final SchemaIndexProvider.Descriptor providerDescriptor;
    private IndexSizeVisitor indexSizeVisitor;

    public OnlineIndexProxy( IndexDescriptor descriptor, SchemaIndexProvider.Descriptor providerDescriptor,
                             IndexAccessor accessor, IndexStoreView view )
    {
        this.descriptor = descriptor;
        this.providerDescriptor = providerDescriptor;
        this.accessor = accessor;
        this.indexSizeVisitor = IndexStoreView.IndexCountVisitors.newIndexSizeVisitor( view, descriptor );
    }

    @Override
    public void start()
    {
    }

    @Override
    public IndexUpdater newUpdater( final IndexUpdateMode mode, long transactionId )
    {
        return new CountingIndexUpdater( transactionId, accessor.newUpdater( mode ), indexSizeVisitor );
    }

    @Override
    public Future<Void> drop() throws IOException
    {
        indexSizeVisitor.replaceIndexSize( MAX_TX_ID, 0 );
        accessor.drop();
        return VOID;
    }

    @Override
    public IndexDescriptor getDescriptor()
    {
        return descriptor;
    }

    @Override
    public SchemaIndexProvider.Descriptor getProviderDescriptor()
    {
        return providerDescriptor;
    }

    @Override
    public InternalIndexState getState()
    {
        return InternalIndexState.ONLINE;
    }

    @Override
    public void force() throws IOException
    {
        accessor.force();
    }

    @Override
    public Future<Void> close() throws IOException
    {
        accessor.close();
        return VOID;
    }

    @Override
    public IndexReader newReader()
    {
        return accessor.newReader();
    }

    @Override
    public boolean awaitStoreScanCompleted() throws IndexPopulationFailedKernelException, InterruptedException
    {
        return false; // the store scan is already completed
    }

    @Override
    public void activate()
    {
        // ok, already active
    }

    @Override
    public void validate()
    {
        // ok, it's online so it's valid
    }

    @Override
    public IndexPopulationFailure getPopulationFailure() throws IllegalStateException
    {
        throw new IllegalStateException( this + " is ONLINE" );
    }

    @Override
    public ResourceIterator<File> snapshotFiles() throws IOException
    {
        return accessor.snapshotFiles();
    }

    @Override
    public String toString()
    {
        return getClass().getSimpleName() + "[accessor:" + accessor + ", descriptor:" + descriptor + "]";
    }
}

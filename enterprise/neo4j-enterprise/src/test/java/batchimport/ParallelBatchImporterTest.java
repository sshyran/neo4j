/*
 * Copyright (c) 2002-2019 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j Enterprise Edition. The included source
 * code can be redistributed and/or modified under the terms of the
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3
 * (http://www.fsf.org/licensing/licenses/agpl-3.0.html) with the
 * Commons Clause, as found in the associated LICENSE.txt file.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * Neo4j object code can be licensed independently from the source
 * under separate terms from the AGPL. Inquiries can be directed to:
 * licensing@neo4j.com
 *
 * More information is also available at:
 * https://neo4j.com/licensing/
 */
package batchimport;

import org.neo4j.kernel.impl.store.format.RecordFormats;
import org.neo4j.kernel.impl.store.format.highlimit.HighLimit;
import org.neo4j.unsafe.impl.batchimport.ParallelBatchImporter;
import org.neo4j.unsafe.impl.batchimport.cache.idmapping.IdGenerator;
import org.neo4j.unsafe.impl.batchimport.cache.idmapping.IdMapper;

/**
 * Test for {@link ParallelBatchImporter} in an enterprise environment so that enterprise store is used.
 */
public class ParallelBatchImporterTest extends org.neo4j.unsafe.impl.batchimport.ParallelBatchImporterTest
{
    public ParallelBatchImporterTest( InputIdGenerator inputIdGenerator, IdMapper idMapper, IdGenerator idGenerator,
            boolean multiPassIterators )
    {
        super( inputIdGenerator, idMapper, idGenerator, multiPassIterators );
    }

    @Override
    public RecordFormats getFormat()
    {
        return HighLimit.RECORD_FORMATS;
    }
}
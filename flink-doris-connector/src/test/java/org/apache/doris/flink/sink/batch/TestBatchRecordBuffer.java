// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.apache.doris.flink.sink.batch;

import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * test for RecordBuffer.
 */
public class TestBatchRecordBuffer {

    @Test
    public void testInsert(){
        BatchRecordBuffer recordBuffer = new BatchRecordBuffer("\n".getBytes(StandardCharsets.UTF_8),1);
        recordBuffer.insert("doris,1".getBytes(StandardCharsets.UTF_8));

        Assert.assertEquals(1, recordBuffer.getNumOfRecords());
        Assert.assertEquals("doris,1".getBytes(StandardCharsets.UTF_8).length, recordBuffer.getBufferSizeBytes());

        recordBuffer.insert("doris,2".getBytes(StandardCharsets.UTF_8));
        Assert.assertEquals(2, recordBuffer.getNumOfRecords());
        Assert.assertEquals("doris,1\ndoris,2".getBytes(StandardCharsets.UTF_8).length - "\n".getBytes(StandardCharsets.UTF_8).length, recordBuffer.getBufferSizeBytes());

        ByteBuffer data = recordBuffer.getData();
        Assert.assertEquals("doris,1\ndoris,2", new String(data.array(), data.arrayOffset(), data.limit()));

        //mock flush
        recordBuffer.clear();
        recordBuffer.insert("doris,3".getBytes(StandardCharsets.UTF_8));
        Assert.assertEquals(1, recordBuffer.getNumOfRecords());
        Assert.assertEquals("doris,3".getBytes(StandardCharsets.UTF_8).length, recordBuffer.getBufferSizeBytes());
    }

    @Test
    public void testGrowCapacity(){
        BatchRecordBuffer recordBuffer = new BatchRecordBuffer("\n".getBytes(StandardCharsets.UTF_8),1);
        recordBuffer.ensureCapacity(10);

        Assert.assertEquals(recordBuffer.getBuffer().capacity(), 10 + 1);

        recordBuffer.ensureCapacity(100);
        Assert.assertEquals(recordBuffer.getBuffer().capacity(), 100 + 11);

        recordBuffer.ensureCapacity(1024);
        Assert.assertEquals(recordBuffer.getBuffer().capacity(), 1024 + 111);

        recordBuffer = new BatchRecordBuffer("\n".getBytes(StandardCharsets.UTF_8),16);
        recordBuffer.ensureCapacity(16);
        Assert.assertEquals(recordBuffer.getBuffer().capacity(), 16);

        recordBuffer.insert("1234567890".getBytes(StandardCharsets.UTF_8));
        recordBuffer.ensureCapacity(8);
        Assert.assertEquals(recordBuffer.getBuffer().capacity(), 32);
    }

}

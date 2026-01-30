/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.db.protocol.xugu.packet.command.query.binary.execute.protocol;

import org.apache.shardingsphere.db.protocol.xugu.payload.XuguPacketPayload;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Binary protocol value for time for MySQL.
 */
public final class XuguTimeBinaryProtocolValue implements XuguBinaryProtocolValue {
    
    @Override
    public Object read(final XuguPacketPayload payload, final boolean unsigned) throws SQLException {
        int length = payload.readInt1();
        payload.readInt1();
        payload.readInt4();
        switch (length) {
            case 0:
                return new Timestamp(0L);
            case 8:
                return getTimestamp(payload);
            case 12:
                Timestamp result = getTimestamp(payload);
                result.setNanos(payload.readInt4());
                return result;
            default:
                throw new SQLFeatureNotSupportedException(String.format("Wrong length `%d` of MYSQL_TYPE_DATE", length));
        }
    }
    
    private Timestamp getTimestamp(final XuguPacketPayload payload) {
        Timestamp result = Timestamp.valueOf(LocalDateTime.of(0, 1, 1, payload.readInt1(), payload.readInt1(), payload.readInt1()));
        result.setNanos(0);
        return result;
    }
    
    @Override
    public void write(final XuguPacketPayload payload, final Object value) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(((Time) value).getTime()), ZoneId.systemDefault());
        int hours = localDateTime.getHour();
        int minutes = localDateTime.getMinute();
        int seconds = localDateTime.getSecond();
        int nanos = localDateTime.getNano();
        boolean isTimeAbsent = 0 == hours && 0 == minutes && 0 == seconds;
        boolean isNanosAbsent = 0 == nanos;
        if (isTimeAbsent && isNanosAbsent) {
            payload.writeInt1(0);
            return;
        }
        if (isNanosAbsent) {
            payload.writeInt1(8);
            writeTime(payload, hours, minutes, seconds);
            return;
        }
        payload.writeInt1(12);
        writeTime(payload, hours, minutes, seconds);
        writeNanos(payload, nanos);
    }
    
    private void writeTime(final XuguPacketPayload payload, final int hourOfDay, final int minutes, final int seconds) {
        payload.writeInt1(0);
        payload.writeInt4(0);
        payload.writeInt1(hourOfDay);
        payload.writeInt1(minutes);
        payload.writeInt1(seconds);
    }
    
    private void writeNanos(final XuguPacketPayload payload, final int nanos) {
        payload.writeInt4(nanos);
    }
}

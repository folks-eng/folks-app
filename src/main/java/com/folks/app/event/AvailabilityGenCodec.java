package com.folks.app.event;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;

/**
 *
 * @author schan280
 */
public class AvailabilityGenCodec implements MessageCodec<AvailabilityGenEvent, AvailabilityGenEvent> {

    @Override
    public void encodeToWire(Buffer buffer, AvailabilityGenEvent s) {
        Buffer tmp = Json.encodeToBuffer(s);
        buffer.appendBuffer(tmp);
    }

    @Override
    public AvailabilityGenEvent decodeFromWire(int pos, Buffer buffer) {
        return Json.decodeValue(buffer, AvailabilityGenEvent.class);
    }

    @Override
    public AvailabilityGenEvent transform(AvailabilityGenEvent s) {
        return s;
    }

    @Override
    public String name() {
        return "booking::codec";
    }

    @Override
    public byte systemCodecID() {
        return (byte)-1;            // Non-system codec
    }
    
}

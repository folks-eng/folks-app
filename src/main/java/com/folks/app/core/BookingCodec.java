package com.folks.app.core;

import com.folks.app.model.Booking;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;

/**
 *
 * @author schan280
 */
public class BookingCodec implements MessageCodec<Booking, Booking> {

    @Override
    public void encodeToWire(Buffer buffer, Booking s) {
        Buffer tmp = Json.encodeToBuffer(s);
        buffer.appendBuffer(tmp);
    }

    @Override
    public Booking decodeFromWire(int pos, Buffer buffer) {
        return Json.decodeValue(buffer, Booking.class);
    }

    @Override
    public Booking transform(Booking s) {
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

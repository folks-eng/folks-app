package com.folks.app.event;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;

/**
 *
 * @author schan280
 */
public class ProfessionalRegCodec implements MessageCodec<ProfessionalRegEvent, ProfessionalRegEvent> {

    @Override
    public void encodeToWire(Buffer buffer, ProfessionalRegEvent s) {
        Buffer tmp = Json.encodeToBuffer(s);
        buffer.appendBuffer(tmp);
    }

    @Override
    public ProfessionalRegEvent decodeFromWire(int pos, Buffer buffer) {
        return Json.decodeValue(buffer, ProfessionalRegEvent.class);
    }

    @Override
    public ProfessionalRegEvent transform(ProfessionalRegEvent s) {
        return s;
    }

    @Override
    public String name() {
        return "professional::codec";
    }

    @Override
    public byte systemCodecID() {
        return (byte)-1;            // Non-system codec
    }
    
}

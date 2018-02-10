package me.loki2302.time.messages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CurrentTimeUpdateMessage.class, name = "currentTimeUpdate"),
        @JsonSubTypes.Type(value = NewChatMessage.class, name = "newChatMessage")
})
public abstract class ServerMessage {
}

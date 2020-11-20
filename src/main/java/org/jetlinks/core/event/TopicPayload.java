package org.jetlinks.core.event;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.util.Recycler;
import io.netty.util.ReferenceCountUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetlinks.core.Payload;
import org.jetlinks.core.codec.Decoder;
import org.jetlinks.core.utils.TopicUtils;

import javax.annotation.Nonnull;
import java.util.Map;

@Getter
@AllArgsConstructor(staticName = "of")
@Slf4j
public class TopicPayload implements Payload {

    public static Recycler<TopicPayload> RECYCLER = new Recycler<TopicPayload>() {
        @Override
        protected TopicPayload newObject(Handle<TopicPayload> handle) {
            return new TopicPayload(handle);
        }
    };

    private String topic;

    private Payload payload;

    private final Recycler.Handle<TopicPayload> handle;

    private TopicPayload(Recycler.Handle<TopicPayload> handle) {
        this.handle = handle;
    }

    public static TopicPayload of(String topic, Payload payload) {
        try {
            TopicPayload topicPayload = RECYCLER.get();
            topicPayload.topic = topic;
            topicPayload.payload = payload;
            return topicPayload;
        } catch (Exception e) {
            return TopicPayload.of(topic, payload, null);
        }
    }

    @Nonnull
    @Override
    public ByteBuf getBody() {
        return payload.getBody();
    }

    @Override
    public TopicPayload slice() {
        return TopicPayload.of(topic, payload.slice());
    }

    @Override
    public boolean release() {
        return handleRelease(ReferenceCountUtil.release(payload));
    }

    @Override
    public boolean release(int dec) {
        return handleRelease(ReferenceCountUtil.release(payload, dec));
    }

    @Override
    protected void finalize() throws Throwable {
        int refCnt = refCnt();
        if (refCnt != 0) {
            log.debug("topic [{}] payload was not release properly, release() was not called before it's garbage-collected. refCnt={}", toString(), refCnt);
        }
        super.finalize();
    }

    protected boolean handleRelease(boolean success) {
        if (success) {
            deallocate();
        }
        return success;
    }

    protected void deallocate() {
        payload = null;
        topic = null;
        if (handle != null) {
            handle.recycle(this);
        }
    }

    @Override
    public TopicPayload retain() {
        payload.retain();
        return this;
    }

    @Override
    public TopicPayload retain(int inc) {
        payload.retain(inc);
        return this;
    }

    @Override
    public TopicPayload touch(Object o) {
        payload.touch(o);
        return this;
    }

    @Override
    public TopicPayload touch() {
        payload.touch();
        return this;
    }

    @Override
    public int refCnt() {
        return payload == null ? 0 : payload.refCnt();
    }

    @Override
    public String toString() {
        return "{" +
                "topic='" + topic + '\'' +
                ", payload=" + payload +
                '}';
    }

    @Override
    public JSONObject bodyToJson(boolean release) {
        return payload.bodyToJson(release);
    }

    @Override
    public JSONArray bodyToJsonArray(boolean release) {
        return payload.bodyToJsonArray(release);
    }

    @Override
    public String bodyToString() {
        return payload.bodyToString();
    }

    @Override
    public String bodyToString(boolean release) {
        return payload.bodyToString(release);
    }

    @Override
    public Object decode() {
        return payload.decode();
    }

    @Override
    public Object decode(boolean release) {
        return payload.decode(release);
    }

    @Override
    public <T> T decode(Class<T> decoder) {
        return payload.decode(decoder);
    }

    @Override
    public <T> T decode(Class<T> decoder, boolean release) {
        return payload.decode(decoder, release);
    }

    @Override
    public <T> T decode(Decoder<T> decoder) {
        return payload.decode(decoder);
    }

    @Override
    public <T> T decode(Decoder<T> decoder, boolean release) {
        return payload.decode(decoder, release);
    }

    public Map<String, String> getTopicVars(String pattern) {
        return TopicUtils.getPathVariables(pattern, getTopic());
    }
}

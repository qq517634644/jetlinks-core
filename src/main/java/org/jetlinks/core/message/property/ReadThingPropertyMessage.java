package org.jetlinks.core.message.property;

import org.jetlinks.core.message.MessageType;
import org.jetlinks.core.message.RepayableThingMessage;
import org.jetlinks.core.things.ThingType;

import java.util.List;

/**
 * 读取设备属性消息, 方向: 平台->设备
 * <p>
 * 下发指令后,设备需要回复指令{@link ReadPropertyMessageReply}
 *
 * @author zhouhao
 * @see ReadPropertyMessageReply
 * @since 1.0.0
 */
public interface ReadThingPropertyMessage<T extends ReadThingPropertyMessageReply> extends RepayableThingMessage<T> {

    /**
     * 要读取的属性列表,协议包可根据实际情况处理此参数,
     * 有的设备可能不支持读取指定的属性,则直接读取全部属性返回即可
     */
    List<String> getProperties();

    ReadThingPropertyMessage<T> addProperties(List<String> properties);

    default MessageType getMessageType() {
        return MessageType.READ_PROPERTY;
    }

    static ReadPropertyMessage forDevice(String deviceId) {
        ReadPropertyMessage message = new ReadPropertyMessage();
        message.setDeviceId(deviceId);
        return message;
    }

    static DefaultReadPropertyMessage forThing(ThingType thingType, String deviceId) {
        DefaultReadPropertyMessage message = new DefaultReadPropertyMessage();
        message.setThingId(deviceId);
        message.setThingType(thingType.getId());
        return message;
    }
}

package org.jetlinks.core.message.property;

import org.jetlinks.core.message.MessageType;
import org.jetlinks.core.message.RepayableThingMessage;

import java.util.Map;

/**
 * 读取设备属性消息, 方向: 平台->设备
 * <p>
 * 下发指令后,设备需要回复指令{@link ReadPropertyMessageReply}
 *
 * @author zhouhao
 * @see ReadPropertyMessageReply
 * @since 1.0.0
 */
public interface WriteThingPropertyMessage<T extends WriteThingPropertyMessageReply> extends RepayableThingMessage<T> {

    /**
     * 要读取的属性列表,协议包可根据实际情况处理此参数,
     * 有的设备可能不支持读取指定的属性,则直接读取全部属性返回即可
     */
    Map<String,Object> getProperties();

    void addProperty(String key, Object value);

    default MessageType getMessageType() {
        return MessageType.READ_PROPERTY;
    }

}

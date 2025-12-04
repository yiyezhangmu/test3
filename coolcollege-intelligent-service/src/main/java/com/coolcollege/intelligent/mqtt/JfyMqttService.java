package com.coolcollege.intelligent.mqtt;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.service.device.DeviceService;
import com.coolcollege.intelligent.util.JfySignatureUtil;
import com.coolcollege.intelligent.util.JfyTimeMillisUtil;
import com.coolstore.base.utils.CommonContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.eclipse.paho.mqttv5.common.packet.UserProperty;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 杰峰云MQTT消息 服务类
 * </p>
 *
 * @author wangff
 * @since 2025/7/29
 */
@Service
@RequiredArgsConstructor
@Slf4j
@DependsOn("deviceService")
public class JfyMqttService {
    private final DeviceService deviceService;

    @Value("${jfy.accessKey}")
    private String accessKey;
    @Value("${jfy.secretKey}")
    private String secretKey;
    @Value("${jfy.uuid}")
    private String uuid;

    private MqttAsyncClient mqtt;

    private static final int moveCard = 5;
    private static final String broker = "tcp://jfmq-v2.xmcsrv.net:1883";


    @PostConstruct
    public void init() {
        connect();
    }

    public boolean connect() {
        if (Objects.nonNull(mqtt) && mqtt.isConnected()) {
            log.info("jfy mqtt已连接");
            return false;
        }
        try {
            String username = accessKey;
            String timeMillis = JfyTimeMillisUtil.getTimMillis();
            String password = JfySignatureUtil.getEncryptStr(uuid, accessKey, secretKey, timeMillis, moveCard);
            // 服务器集群部署时需要保证多节点该参数唯一
            String clientId = "mqtt:coolstore:" + uuid + ":" + CommonContextUtil.getProfileName();
            mqtt = new MqttAsyncClient(broker, clientId);
            MqttConnectionOptions options = new MqttConnectionOptions();
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(20);
            options.setAutomaticReconnect(true);
            options.setMaxReconnectDelay(2000);
            options.setCleanStart(false);
            options.setUserName(username);
            options.setPassword(password.getBytes(StandardCharsets.UTF_8));

            List<UserProperty> userProperties = new ArrayList<>();
            userProperties.add(new UserProperty("timeMillis", timeMillis));
            options.setUserProperties(userProperties);

            mqtt.setCallback(new MqttCallback() {
                @Override
                public void disconnected(MqttDisconnectResponse disconnectResponse) {
                }

                @Override
                public void mqttErrorOccurred(MqttException exception) {
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    MDC.put(Constants.REQUEST_ID, UUIDUtils.get32UUID());
                    String sn = null;
                    String msg = new String(message.getPayload());
                    try {
                        JSONObject jsonObject = JSONObject.parseObject(msg);
                        sn = jsonObject.getString("sn");
                        log.info("jfy设备{}状态更新推送", sn);
                        deviceService.callbackUpdateDeviceStatus(sn);
                    } catch (Exception e) {
                        log.info("jfy设备{}状态更新失败, message:{}, errorMsg:{}", sn, msg, e.getMessage());
                    }
                }

                @Override
                public void deliveryComplete(IMqttToken token) {
                }

                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                }

                @Override
                public void authPacketArrived(int reasonCode, MqttProperties properties) {
                }
            });
            mqtt.connect(options, null, new MqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    log.info("jfy mqtt连接成功, clientId: {}", asyncActionToken.getClient().getClientId());
                    try {
                        //连接成功之后，处理订阅
                        //订阅设备状态消息
                        mqtt.subscribe(uuid + "/device/+/status", 1);
                        log.info("设备状态订阅成功");
                    } catch (Exception e) {
                        log.info("订阅失败", e);
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    log.info("jfy mqtt连接失败, clientId:{}, errorMsg:{}", asyncActionToken.getClient().getClientId(), exception.getMessage());
                }
            });
            return true;
        } catch (Exception e) {
            log.error("MQTT连接异常", e);
        }
        return false;
    }

    /**
     * 断开连接
     */
    public boolean disconnect() {
        if (Objects.nonNull(mqtt) && mqtt.isConnected()) {
            try {
                IMqttToken iMqttToken = mqtt.disconnect();
                iMqttToken.waitForCompletion();
            } catch (Exception e) {
                log.info("jfy mqtt断开连接失败", e);
            }
        }
        log.info("jfy mqtt已关闭");
        return true;
    }
}

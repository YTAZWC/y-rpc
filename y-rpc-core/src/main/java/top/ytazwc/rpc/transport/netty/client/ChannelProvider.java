package top.ytazwc.rpc.transport.netty.client;

import lombok.extern.slf4j.Slf4j;
import io.netty.channel.Channel;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 00103943
 * @date 2025-03-27 08:31
 * @package top.ytazwc.rpc.transport.netty.client
 * @description Channel 管理器
 */
@Slf4j
public class ChannelProvider {

    // 用于绑定 InetSocketAddress 和 Channel 的关系
    private final Map<String, Channel> channelMap;

    public ChannelProvider() {
        channelMap = new ConcurrentHashMap<>();
    }

    /**
     * 根据连接地址来获取可用连接
     * @param address 连接地址
     * @return 可用连接
     */
    public Channel get(InetSocketAddress address) {
        // 网络连接地址为 key
        String key = address.toString();

        // 确定是否有相应地址的连接
        if (channelMap.containsKey(key)) {
            // 有 则获取相应连接
            Channel channel = channelMap.get(key);
            // 判断连接是否可用
            if (Objects.nonNull(channel) && channel.isActive()) {
                // 可用则返回
                return channel;
            } else {
                // 不可用则删除
                channelMap.remove(key);
            }
        }
        return null;
    }

    /**
     * 缓存连接地址及对应连接
     * @param address 连接地址
     * @param channel 对应连接
     */
    public void set(InetSocketAddress address, Channel channel) {
        String key = address.toString();
        channelMap.put(key, channel);
    }

    /**
     * 删除连接地址对应的连接 并打印当前可用连接数量
     * @param address 指定连接地址
     */
    public void remove(InetSocketAddress address) {
        String key = address.toString();
        channelMap.remove(key);
        log.info("当前缓存连接数量: [{}]", channelMap.size());
    }



}

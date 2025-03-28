# y-rpc
是基于 Netty 作为网络通信，使用 Zookeeper 作为服务注册中心，实现的 rpc(远程服务调用) 框架；

## 1.0.0.socket

该分支是使用 Java Socket 作为网络通信实现的 rpc 框架；

## 1.1.0.socket

该分支是基于 1.0.0.socket 分支的基础上，借助 Spring 而实现的可以使用注解来进行服务注册和服务消费；

## 2.1.0.netty

则是在 1.1.0.socket 的基础上使用 Netty 来作为服务端与客户端的通信，并使用 Kryo 序列化框架和 Gzip 压缩来提高服务通信的效率;

## 参考与学习


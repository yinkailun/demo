## Dubbo简介
1. 传统服务远程调用(http协议，RPC协议)
    - restTemplate
    - webservice(soap)
    - hessian
    - RMI
2.dubbo会对外暴露一个ip+port的服务，使用的是dubbo自定义的协议，Tomcat是对外暴露一个HTTP协议
    - dubbo使用netty进行网络通信，服务端有一个nettyServer,客户端有nettyClient，使用dubbo协议
    - 不止是一个框架，更是一个生态，平台，
    - 只是不同的注册中心(redis/zookeeper/nacos/etcd/consul...)
    - 支持多协议 (基于不同的协议将服务暴露出去)
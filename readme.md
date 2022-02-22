# 聊天室项目

## 文件介绍

### 启动类

标准服务器端启动类

![image-20220130180434097](C:\Users\Lenvov\AppData\Roaming\Typora\typora-user-images\image-20220130180434097.png)

标准客户端启动类

![image-20220130180358113](C:\Users\Lenvov\AppData\Roaming\Typora\typora-user-images\image-20220130180358113.png)

`RPC` 远程调用服务器端

![image-20220130180523071](C:\Users\Lenvov\AppData\Roaming\Typora\typora-user-images\image-20220130180523071.png)

`RPC` 远程调用客户端（只是手动填写要调用的方法）

![image-20220130180612664](C:\Users\Lenvov\AppData\Roaming\Typora\typora-user-images\image-20220130180612664.png)

`RPC` 远程调用客户端代理类

![image-20220130180710668](C:\Users\Lenvov\AppData\Roaming\Typora\typora-user-images\image-20220130180710668.png)

### 基本对象类

各种消息

![image-20220130180807385](C:\Users\Lenvov\AppData\Roaming\Typora\typora-user-images\image-20220130180807385.png)

* 在基类定义好各消息的对应的编号，方便远程调用的时候根据协议的编号找到对应的消息类型
* 抽象类表示所有响应消息的父类
* `ping` 和 `pong` 消息主要用于客户端定时向客户端发送心跳包

### 业务相关

目前只有登录这一业务

![image-20220130181903895](C:\Users\Lenvov\AppData\Roaming\Typora\typora-user-images\image-20220130181903895.png)

* 两个Hello是用来测试 `RPC` 的
* 登录没有连接数据库，只是用本地map存储用户信息

### 会话包

一些处理会话命令的接口和实现方法

![image-20220130182535572](C:\Users\Lenvov\AppData\Roaming\Typora\typora-user-images\image-20220130182535572.png)

* 用到了工厂模式，硬编码指定了接口对应的实现类

### 处理器类

![image-20220130214902416](C:\Users\Lenvov\AppData\Roaming\Typora\typora-user-images\image-20220130214902416.png)

![image-20220130214935832](C:\Users\Lenvov\AppData\Roaming\Typora\typora-user-images\image-20220130214935832.png)

* 这样在 `pipeline ` 中添加比较方便
* 还可以用到 `sharable handler`  作为全局变量

### 协议相关

![image-20220130215347250](C:\Users\Lenvov\AppData\Roaming\Typora\typora-user-images\image-20220130215347250.png)

* 自定义编解码定义了基于协议的编码、解码方法
* `ProtocolFrameDecoder` 主要用于解决黏包半包问题
* `Serializer` 定义了序列化和反序列化的方法


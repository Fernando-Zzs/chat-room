# 启动类

## ChatServer

ChannelOption.SO_BACKLOG 是用来暂时存放完成三次握手的但服务器还没来得及处理的请求的队列

handler顺序

1. 相关协议的数据包划分处理器，继承了LengthFieldBasedFrameDecoder类，根据自定义协议的规则调整参数

2. Logging_handler，打印日志信息

3. 相关协议的编解码器，继承了MessageToMessageCodec类，保证了handler是sharable的（ByteToMessageCodec不可sharable），按照规则填入数据并通过config中指定的算法对传输内容进行序列化和反序列化。

4. IdleStateHandler，用于判断是否读写空闲时间过长，及时关闭避免资源浪费

5. ChannelDuplexHandler，出入站处理器用于出发特殊事件，通常用于监听channel中的事件并根据类型进行处理

6. 自定义的业务相关handler，包括以下：

   ![image](https://user-images.githubusercontent.com/65518148/155132731-21bd2ac7-685c-4905-8d67-eac9c9180488.png)

## ChatClient

countdownlatch和原子变量用于记录登录、退出的信息，保证不会同时有两个线程同时登陆成功

handler顺序

1. 相关协议的数据包划分处理器，继承了LengthFieldBasedFrameDecoder类，根据自定义协议的规则调整参数
2. 相关协议的编解码器，继承了MessageToMessageCodec类，保证了handler是sharable的（ByteToMessageCodec不可sharable），按照规则填入数据并通过config中指定的算法对传输内容进行序列化和反序列化。

3. IdleStateHandler，用于判断是否写空闲时间过长，指定时间内没有写数据会触发一个事件供下一个处理器检测处理
4. ChannelDuplexHandler，接收到写空闲事件后发送一个心跳包
5. 自定义handler，是ChannelInboundHandlerAdapter的一个实现类，重写了channelRead，channelActive，channelInactive，exceptionCaught方法



# DTO

## Message

抽象类，定义了消息类型、序列号，各种消息的代号

## AbstractResponseMessage

响应消息的抽象类，是否成功和提示信息

## 各种Message

和业务设计有关

业务类



# Handler处理器（核心）

服务器端这里所有的handler都是sharable且继承自SimpleChannelInboundHandler<T> 泛型与关注消息的类型有关

SimpleChannelInboundHandler与ChannelInboundHandler的区别，前者的channelRead0方法完成时，已经有了传入消息，会自动释放ByteBuf的内存引用

## ChatRequestMessageHandler

找到接收的对象注册在session的对应channel，如果在线则发送

## GroupChatRequestMessageHandler

通过groupSession的方法得到组内所有能获取到的channel，并写入消息发送

## GroupCreateRequestMessageHandler

通过群管理器的方法创建群，遍历得到群成员的channel并挨个通知，如果群名已存在就会报错误信息

## GroupJoinRequestMessageHandler

通过群管理器的方法添加成员并给对应的提示信息

## GroupMembersRequestMessageHandler

通过群管理器的方法获得所有成员的信息

## GroupQuitRequestMessageHandler

通过群管理器的方法移除对应用户的信息

## QuitHandler

重写了channelInactive和exceptionCaught方法

## LoginRequestMessageHandler

获取用户的输入并用UserService的方法查询是否输入无误，然后返回响应信息



# 业务相关

## Session

会话管理接口，定义了绑定或解绑channel，根据用户名获取channel等抽象方法

## SessionFactory

指定Session接口的实现类为SessionFactory，并定义返回的单例session方法

## SessionMemoryImpl

Session的实现类，记录了用户名-channel、channel-用户名、channel-用户属性三个ConcurrentHashMap

绑定和解绑的过程就是往这里面放键值对的过程

## Group

聊天组的实体类，保存组的信息

## GroupSession

组会话管理接口，定义了创建、删除聊天组，加入聊天组、退出聊天组、查看组成员、查看组成员channel等方法接口

## GroupSessionFactory

指定GroupSession接口的实现类为GroupSessionMemoryImpl，并定义返回的单例groupSession方法

## GroupSessionMemoryImpl

维护了群名-Group的ConcurrentHashMap

## UserService

定义了业务相关的服务的接口，比如登录

## UserServiceFactory

指定UserService接口的实现类为UserServiceMemoryImpl，并定义返回的单例userService方法

## UserServiceMemoryImpl

存储用户登录信息及登录逻辑等



# 协议相关

## Serializer

扩展序列化反序列化算法，利用多个枚举类实现序列化接口的序列化反序列化方法，目前有jdk自带的序列化机制和json序列化两种


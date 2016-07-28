# 简介
本项目是七牛[自定义数据处理](http://developer.qiniu.com/article/dora/ufop/ufop-introduction.html)的示例程序。这个项目使用Java语言编写，可以作为接入七牛自定义数据处理的参考。
这个项目主要是实现一个计算文件md5的功能，其中的框架代码完全可以在开发其他的自定义数据处理的时候使用，只需要实现一个简单的接口，并注册服务即可。


# 背景

七牛的自定义数据处理的威力十分强大，几乎可以做任何你想做的事情。传统意义上来说，我们要对一个文件进行处理，基本流程如下：

1. 应用程序从磁盘读取文件到内存
2. 应用程序对内存中的数据进行处理
3. 应用程序将处理的结果再次存入磁盘

而在云计算的时代，这个模型其实也适用，只不过各个角色变得大了一点。比如传统意义上的数据存储位置磁盘变成了云存储，而跑在系统上面的应用程序，现在变成了跑在docker里面的应用程序，变成了容器就是应用，应用就是容器。而传统意义上的应用程序和磁盘进行交互所需要的IO总线变成了内网的光纤。想一想就知道，其实不过是每个关键组成部分变得高大上了一点，有时候内网带宽的数据传输速度会把IO总线的速度虐成渣渣，所以坐拥存储而不去搞计算才是无法理解的。

用户自定义数据处理的出现适应了不同客户的需求。七牛官方提供的图片处理，音视频处理等功能只适合大部分的应用场景，还有很多客户特有的场景，比如数据加密存储，文件内容识别，音视频文件特殊方式转码，以及本例子中的获取小文件的md5值都是七牛官方所没有的，那么这种情况下，为了满足客户的需求，就必须搭建一套平台来开放应用的开发接口给客户，让客户自行实现自己所需要的功能，另外为了适应不同的规模的计算，弹性扩容也是自定义数据处理服务的一大特色，无论是实例的数量还是宿主机的硬件配置都是可以根据客户需要进行动态调整的。

# 流程

UFOP 的基本接口其实就是监听本地地址 `0.0.0.0`  的 `9100` 端口，并在路径 `/uop` 上面接受 HTTP POST 请求的HTTP服务器。然后对请求来的格式为 `application/json` 的 POST BODY 进行解析，获取其中的原始文件和处理指令相关的信息，接下来应用程序下载原始文件，保存在内存或者是系统的临时目录，对文件进行处理，最后把处理的结果写入到 HTTP RESPONSE 中。 最后一定记住要删除保存在本地的临时文件。

UFOP 服务接受的请求格式是固定的 JSON 格式。各个参数的意思可以参考文档的描述。

# 代码

本项目的代码结构如下：

```
└── com
    └── qiniu
        ├── MainEntry.java
        ├── service
        │   ├── MD5ServiceImpl.java
        │   └── ServiceInterface.java
        └── ufop
            ├── UfopConfig.java
            ├── UfopError.java
            ├── UfopRequest.java
            ├── UfopRequestHandler.java
            ├── UfopServer.java
            ├── UfopSrcInfo.java
            └── UfopUtil.java
```

其中 `ServiceInterface.java` 是定义的实际数据处理的接口，任何其他的服务只需要实现这个接口即可。这个接口包含两个方法：

```
String Name();

void Do(UfopRequest ufopReq, HttpExchange exchange);
```

其中`Name`方法返回服务的名称，而`Do`方法执行文件下载，处理，写入回复等操作。
`MD5ServiceImpl.java` 是实现了接口 `ServiceInterface` 的服务，它是本项目的实例服务。

在实现服务接口之后，还需要在`UfopRequestHandler.java`里面注册一下服务就可以使用了。

```
public UfopRequestHandler(UfopConfig ufopConfig) {
    this.md5Service = new MD5ServiceImpl();
    this.ufopServices = new HashMap<String, ServiceInterface>();
    this.ufopServices.put(this.md5Service.Name(), this.md5Service);
    this.ufopConfig = ufopConfig;
}
```

# 部署

UFOP 服务的部署，需要利用到`qufopctl`工具，可以从[http://developer.qiniu.com/article/dora/ufop/ufop-cli.html](http://developer.qiniu.com/article/dora/ufop/ufop-cli.html)下载。

这里介绍一下本项目的部署方式。首先要建立一个文件夹，里面的文件结构如下：

```
└── md5
    ├── env
    │   └── jdk-7u45-linux-x64.tar.gz
    ├── lib
    │   ├── commons-codec-1.10.jar
    │   └── gson-2.6.2.jar
    ├── qufop.conf
    ├── qufop.jar
    └── ufop.yaml
```

|文件名|描述|
|-----|----|
|env/jdk-7u45-linux-x64.tar.gz|这个是JDK的安装包，因为UFOP的运行环境是docker，默认没有安装JDK，为了方便客户使用，可以从[http://devtools.qiniu.com/jdk-7u45-linux-x64.tar.gz](http://devtools.qiniu.com/jdk-7u45-linux-x64.tar.gz)下载。|
|lib/*|项目的依赖库，可以把所有的项目依赖的jar都放在这里，当然项目打包的时候注意`MANIFEST.MF`文件引用了这里。|
|qufop.conf|项目启动相关的配置，本项目采用了JSON做配置，其实你可以用任何格式，只要你的程序能够解析它。|
|qufop.jar|打包好的项目执行文件，本地测试的时候一定要保证能通过`java -jar qufop.jar qufop.conf`运行。|
|ufop.yaml|最重要的配置文件，用来描述UFOP程序构建的时候的相关配置信息，编写方式根据需求可以更改，这里的指令会被docker执行。|

在构建UFOP程序之前，我们首先要用`qufopctl`工具注册一个 UFOP 服务：

```
$ qufopctl reg qn-md5 -m 2 -d 'ufop demo md5'
```

这里我们注册了一个 UFOP 程序叫做 `qn-md5`，其中 `qn-` 是我们配置在 `qufop.conf` 里面的 `ufopPrefix`，由于很多时候我们可能需要通过前缀来区分服务，所以本项目的程序里面把服务的名称和注册的 UFOP 名称分开了，其中注册的 UFOP 名称等于配置文件中的前缀拼接上服务的名称。比如这里注册的 `qn-md5` 就是通过拼接配置文件里面的 `qn-` 和 md5 服务的名称 `md5` 构成的。

然后我们就需要构建这个 UFOP 程序了：

```
$ qufopctl build qn-md5 -f md5
```

接下来等待构建完成，期间可以使用如下命令查看构建状态：

```
$ qufopctl version qn-md5

*********qn-md5*********
version: 1
state: building
description:
createAt: 2016-07-28 18:15:51.363837161 +0800 CST
```

构建完成之后，我们需要切换 UFOP 的版本：

```
$ qufopctl chver qn-md5 -c 1
```

切换完毕之后，如果是新建的 UFOP 程序可以通过如下命令使得程序上线：

```
$ qufopctl resize qn-md5 -n 1
```

如果是升级 UFOP 程序，则需要通过如下的命令来升级线上实例程序：

```
$ qufopctl upgrade qn-md5
```

部署完毕之后，我们测试下这个实例的功能，访问 `http://devtools.qiniu.com/qiniu.png?qn-md5` 则得到文件的md5值。

对于部署在自己账号下的客户，请使用自己空间绑定的域名去触发 UFOP 的调用。

# 依赖

[commons-codec](http://commons.apache.org/proper/commons-codec/)

# 支持

如果遇到任何问题，欢迎联系QQ（ 3304087589 或 2842916733 ）获取帮助。
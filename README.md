# EasyGrpc开发文档
Easygrpc是一款基于gprc的远程调用框架,极大的简化了使用方式,同时支持流式调用、熔断、容灾、限流降级等功能

## 快速开始
例子代码: [easygrpc samples](https://github.com/chenhaitao888/easygrpc-example.git)

### maven打包
克隆代码: https://github.com/chenhaitao888/easygrpc.git, maven本地打包上通过deploy上传到私服仓库，对于使用spring集成的方式的仅需要对easygrpc-spring-boot-starter模块打包deploy
即可，对于非spring方式的对easygrpc-startup打包deploy即可

### maven依赖
spring方式: 
```
 <dependency>
    <groupId>com.cht</groupId>
    <artifactId>easygrpc-spring-boot-starter</artifactId>
    <version>${version}</version>
 </dependency>
```
非spring方式:
```
<dependency>
    <groupId>com.cht</groupId>
    <artifactId>easygrpc-startup</artifactId>
    <version>${version}</version>
 </dependency>
 ```
 
### 原生使用方式(推荐使用springboot方式)
#### 服务端
1. resources目录下新建easy-grpc.yml文件
```
serverConfig:
  serviceName: "EasyGrpcTest"
  workThreads: 50
  queueCapacity: 500
  port: 8888
  servicePackages: ["com.cht.easygrpc.remoting.iface"]
  serviceImplPackages: ["com.cht.easygrpc.remoting.iface"]

commonConfig:
  registryAddress: "zk地址"
  appId: "EasyGrpcTest"
  parameters:
    easygrpc.logger: "slf4j"
    easygrpc.proxy: "jdk"
```  
说明：其中serviceName和appId为服务端名称，servicePackages和serviceImplPackages在非spring模式下这个两个必填，分别为接口所在包名和接口实现所在包名

2. 代码实现
接口实现类上添加注解@EasyGrpcService
```
@EasyGrpcService
public class EasyGrpcTestImpl implements EasyGrpcTest{

    @Override
    public String hello(String req) {
        System.out.println("hello: " + req);
        return "hello: " + req;
    }
}
```
3. 服务端启动
```
EasyGrpcBootstrap bootstrap = new EasyGrpcBootstrap();
bootstrap.start();
```
#### 客户端
1. resources目录下新建easy-grpc.yml文件
```
easygrpc:
  serverConfig:
    serviceName: "EasyGrpcClient"
    port: 8077
  commonConfig:
    registryAddress: "zk地址"
    appId: "EasyGrpcClient"
    parameters:
      easygrpc.logger: "slf4j"
      easygrpc.proxy: "jdk"

  clientConfig: [
    {
      clientName: "EasyGrpcServer",
      ifaceNames: ["com.easygrpc.example.client.service.EasyGrpcExample"],
      workThreads: 20,
      timeoutInMillis: 5000,
      queueCapacity: 500
    },
    {
      clientName: "EasyGrpcServer",
      ifaceNames: ["com.easygrpc.example.client.service.EasyGrpcStreamExample"],
      workThreads: 20,
      timeoutInMillis: 5000,
      queueCapacity: 500,
      stubType: 1
    }
  ]
```
说明: clientConfig为需要调用的服务端的信息，clientName对应于服务端的名，ifaceNames为需要远程调用的接口名，timeoutInMillis为接口调用超时时间，queueCapacity为线程池队列大小，stubType为调用方式。目前支持block和stream两种方式，分别对应0和1，默认为0
2. 客户端启动
```
EasyGrpcBootstrap bootstrap = new EasyGrpcBootstrap();
bootstrap.start();
```
3. 调用实现
```
Container container = EasyGrpcInjector.getInstance(Container.class);
EasyGrpcTest instance = container.createInstance(EasyGrpcTest.class);
String result = instance.hello("ada");
System.out.println(result);
```

### springboot集成方式(推荐)
#### 服务端
1. yml文件添加
```
easy:
  grpc:
    enabled: true
easygrpc:
  serverConfig:
    serviceName: "EasyGrpcServer"
    workThreads: 50
    queueCapacity: 500
    port: 8887
#    servicePackages: ["com.example.demo.service.iface"]  springboot 启动可以不配置此项
#    serviceImplPackages: ["com.example.demo.service.iface.impl"]  springboot 启动可以不配置此项

  commonConfig:
    registryAddress: "192.168.1.6:2181"
    appId: "EasyGrpcServer"
    parameters:
      easygrpc.logger: "slf4j"
      easygrpc.proxy: "jdk"
```
说明: spring启动servicePackages和serviceImplPackages可以不用配置, easy.grpc.enabled设置为true（springboot启动必须设置为true,否则默认不启动easygrpc）

2. 启动方式
springboot启动入口application添加注解@EnableEasyGrpc，正常启动springboot项目即可
```
@SpringBootApplication
@EnableEasyGrpc
public class Server {

    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }

}
```

3. 代码实现
实现类上添加注解@EasyGrpcSpringService(interfaces = {EasyGrpcExample.class})，interfaces为此接口的实现类，目前还只支持一个接口一个实现，后续会实现一个接口多实现，动态调用
```
@EasyGrpcSpringService(interfaces = {EasyGrpcExample.class})
public class EasyGrpcExampleImpl implements EasyGrpcExample {
    @Override
    public String hello(String req) {
        return "hello: " + req;
    }

}
```

#### 客户端
1. yml文件添加
```
easy:
  grpc:
    enabled: true
    
easygrpc:
  serverConfig:
    serviceName: "EasyGrpcClient"
    port: 8077
  commonConfig:
    registryAddress: "192.168.1.6:2181"
    appId: "EasyGrpcClient"
    parameters:
      easygrpc.logger: "slf4j"
      easygrpc.proxy: "jdk"

  clientConfig: [
    {
      clientName: "EasyGrpcServer",
      ifaceNames: ["com.easygrpc.example.client.service.EasyGrpcExample"],
      workThreads: 20,
      timeoutInMillis: 5000,
      queueCapacity: 500
    },
    {
      clientName: "EasyGrpcServer",
      ifaceNames: ["com.easygrpc.example.client.service.EasyGrpcStreamExample"],
      workThreads: 20,
      timeoutInMillis: 5000,
      queueCapacity: 500,
      stubType: 1
    }
  ]
```
说明：这里配置和原生的没有区别，除了需要添加easy.grpc.enabled: true以保证easygrpc启动

2. 启动方式
springboot启动入口application添加注解@EnableEasyGrpc，正常启动springboot项目即可
```
@SpringBootApplication
@EnableEasyGrpc
public class Client {

    public static void main(String[] args) {
        SpringApplication.run(Client.class, args);
    }

}
```
3. 代码实现
非流式调用使用注解@EasyGrpcAutowired， 流式调用使用注解@EasyGrpcAutowired(type = EasyGrpcConstants.EASY_GRPC_TYPE_STREAM)
```
@EasyGrpcAutowired
private EasyGrpcExample easyGrpcExample;

@EasyGrpcAutowired(type = EasyGrpcConstants.EASY_GRPC_TYPE_STREAM)
private EasyGrpcStreamExample streamExample;
```

### easygrpc流式调用方式
流式调用可以实现批量调用及文件上传下载的功能，使用方式如下
#### 服务端
定义接口入参及返回必须为EasyGrpcStreamObserver<?>，泛型参数根据需求自定义
```
public interface EasyGrpcStreamExample {

    EasyGrpcStreamObserver<String> helloStream(EasyGrpcStreamObserver<String> request);
}

@EasyGrpcSpringService(interfaces = {EasyGrpcStreamExample.class})
public class EasyGrpcStreamExampleImpl implements EasyGrpcStreamExample {

    @Override
    public EasyGrpcStreamObserver<String> helloStream(EasyGrpcStreamObserver<String> request) {
        EasyGrpcStreamObserver<String> easyGrpcStreamObserver = new EasyGrpcStreamObserver<String>() {
            @Override
            public void onNext(String value) {
                System.out.println("receive client value: " + value);
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                request.onNext("server had completed client request...");
                request.onCompleted();

            }
        };
        return easyGrpcStreamObserver;
    }
}
```
#### 客户端
定义回调obsever,可以直接继承AbstractEasyGrpcStreamObserver抽象类，或者实现EasyGrpcStreamObserver,但是后者的方式需要用户自己定义getReturnType服务端返回类型
```

    @EasyGrpcAutowired(type = EasyGrpcConstants.EASY_GRPC_TYPE_STREAM)
    private EasyGrpcStreamExample streamExample;
    
    final CountDownLatch done = new CountDownLatch(1);
    // 定义服务端返回结果的回调
    AbstractEasyGrpcStreamObserver<String> clientStreamObserver = new AbstractEasyGrpcStreamObserver<String>() {
         @Override
         public void onNext(String value) {
            System.out.println("receive server response: " + value);
         }

         @Override
         public void onError(Throwable t) {
             t.printStackTrace();
         }

         @Override
         public void onCompleted() {
             System.out.println("stream onCompleted");
             done.countDown();
         }
        };

        EasyGrpcStreamObserver<String> serverStreamObserver = streamExample.helloStream(clientStreamObserver);
        for(int i = 0; i < 10; i++){
            serverStreamObserver.onNext(i + "");
        }
        serverStreamObserver.onCompleted();
        done.await();
```


### 框架设计




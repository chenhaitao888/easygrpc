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


# 概述
这是一个简单易用的支持office在线编辑的工具


## 开发声明
onlyoffice是一个提供office相关操作的优秀开源组件，功能丰富强大，详情可查看[官网地址][onlyoffice官网地址]。  
该项目80%的源码是onlyoffice官方提供的，在此基础上做了一些功能扩展和开放接口，官方样例代码演示了如何使用java spring项目
访问交互onlyoffice文档服务器，详情可查看[样例代码][Java Spring example]。  


## 使用说明
该项目是一个以使用onlyoffice文档服务器服务(documentserver)为前提的java-web服务，该服务会与onlyoffice文档服务器交互以获取需要的功能。  
onlyoffice文档服务器并不好直接与之交互使用，官方只提供了一个引用其核心api.js文件进行开发使用的方式，
虽然onlyoffice官方文档提供了丰富的二次开发或集成的资料，但是基于前端的页面集成和二次开发会更加容易一些，
该项目便是为了提供一个java-web的门面端服务与之交互，再对外提供一些方便易用的开放接口。  
（当然如果有需要的话，官方也提供了收费的onlyoffice企业版服务套件，提供了丰富的开放接口以及详细的接口文档）


## 核心功能说明
官方源代码样例已经提供了较多的试用功能包括文件管理，用户权限控制样例，编辑界面使用等，但不太实用，特别是文件管理这块fileId和fileName混乱不堪。。。  
该项目在不改动原试用功能的情况下额外增加了两个简单实用的接口，两个接口都是传入一个文件资源后返回一个可在线编辑的html编辑器地址，
只是请求方式和入参要求不同，并剥离了用户权限相关的逻辑代码（后续有需求再以切面切入），另外对filed重新进行了管理（不影响原试用接口功能逻辑）。  
目前并没有做多人协作在线编辑的二次开发并暴露开放接口，暂时无此需求，后续有时间精力会考虑扩展二次开发该功能。当然官方组件的功能很强大丰富，
不仅多人协作，还有文档类型转换导出，门户，权限控制等等都支持，更多功能可查看[官网地址][onlyoffice官网地址]。


## maven项目引入使用
该项目对外sdk已经以spring-boot-starter的方式集成到springboot框架中，并已发布到maven中央仓库，
在maven项目中添加如下依赖即可使用：
```xml
<dependency>
  <groupId>io.github.kggiypp</groupId>
  <artifactId>easy-onlyoffice-spring-boot-starter</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```


[onlyoffice官网地址]:https://www.onlyoffice.com
[Java Spring example]:https://api.onlyoffice.com/docs/docs-api/samples/language-specific-examples/java-spring-examples
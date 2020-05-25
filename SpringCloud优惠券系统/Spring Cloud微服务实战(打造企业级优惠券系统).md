[TOC]

#  Spring Cloud微服务实战(打造企业级优惠券系统)

## 一.准备工作(工欲善其事,必先利其器)"项目中有实现和注释"

##  SQL数据表和JSON测试数据分别在CouponTemplate和CouponSettlement 的 resources文件中

###  1.Redis(非关系型数据库)

**特性:**

**1**.支持的数据类型:**String,List,Hash,Set,SortedSet**

- **String：是Redis最基本的类型，由于是二进制存储，所以，它可以包含任何数据。单个value的最大上限是1G。理论上来说，我们在使用Redis的时候，可以仅仅使用String来完成任何操作，甚至有些工程就是这样做的。但是，这样一定会失去一些便利性与性能。我们应该只用String去存储一些独立的内容，即Redis中的多个String不应该有关联关系。**
- **List：列表，也可以理解为队列，用于存储序列集合。List不要求元素唯一。适合存储一系列有顺序要求的数据。Redis中的List其实是一个双向链表实现的，通过push、pop操作从链表的头部或者尾部添加删除数据。所以，可以把它当作栈，也可以当做队列。**
- **Hash：在redis中叫做字典，用于存储多个kv数据，且这些kv数据是属于一类的。例如：hash类型常常被用于存储一个人的信息，可以有姓名、年龄、性别等等。我们在使用Hash类型的时候，可以把它想象成Java中的HashMap去使用，用于存储比较复杂的数据结构。**
- **Set：无序的方式存储多个不同的元素，对元素可以进行快速的添加、查找和删除。相同的元素只算一个，且可以包含2的32次方减1个元素。所以，在容量上，几乎不需要去考虑溢出的问题。set集合类型除了基本的添加删除操作，其他有用的操作还包含集合的取并集，交集，差集。最后，就是需要注意，set中的每个元素不能是重复的。**
- **SortedSet：它与Set类型很像，也是String类型元素的集合。不同的是每个元素都关联一个浮点数类型的权重值。通过权重值可以有序（数字顺序）的获取集合中的元素。如果两个元素的权重值是一样的，那就按照元素的字节顺序排列先后。SortedSet最常应用的场景是排行榜这类应用。例如，SortedSet中存储系统中的用户，并且把每个用户的积分设置为权重值。按照自定义的顺序排列，就得到了用户排行榜。**

**2**.Redis的所有操作都是原子的(一个操作要么完成要么不完成,Redis单线程)

**3**.Redis可以对key设置过期时间(定时删除,惰性删除,定期删除)

**4**.Redis支持两种持久化方式:RDB(快照,默认,保存数据),AOF(保存命令)

- **RDB：该持久化默认开启，称为快照持久化。一次性把redis中全部的数据保存一份存储在硬盘中。**
- **AOF：它的本质是把用户执行的每个“写”指令(添加、修改、删除)都备份到文件中，还原数据的时候就是执行具体的写指令。这也是它与RDB最大的不同，RDB是保存数据，AOF则是保存命令。**

**Redis速度速度快的原因:**

1.完全基于内存

2.数据结构简单

3.单线程,没有切换

4.多路IO复用模型

**缓存穿透和缓存雪崩的问题:**

**缓存穿透**是指查询一个不存在的数据,但是Cache不命中,有需要去DB中查询,造成性能下降

​              **解决方案:**给没有命中的Key设定"没有意义的空值"

**缓存雪崩**是指Cache设置了相同的过期时间,导致Cache在同一时间失效,请求全部转发的到DB,DB的瞬时压力过大,造成雪崩

​           **解决方案:**给key设置不同的(随机的)过期时间

**Redis的I/O模型:**

Redis的Reactor设计模式

### 2.Mysql

mysql的索引是B+树

![屏幕截图(117)](C:\Users\晴天真美\Pictures\Screenshots\微服务\屏幕截图(117).png)

![屏幕截图(118)](C:\Users\晴天真美\Pictures\Screenshots\微服务\屏幕截图(118).png)

![屏幕截图(119)](C:\Users\晴天真美\Pictures\Screenshots\微服务\屏幕截图(119).png)

![屏幕截图(120)](C:\Users\晴天真美\Pictures\Screenshots\微服务\屏幕截图(120).png)

![屏幕截图(121)](C:\Users\晴天真美\Pictures\Screenshots\微服务\屏幕截图(121).png)

#### SpringBoot2 HikariCP 的配置项含义

- **spring.datasource.hikari.connection-timeout：客户端等待连接池连接的最大毫秒数。即如果在这个时间内客户端获取不到连接，则会抛出连接超时异常**

- **spring.datasource.hikari.minimum-idle：连接池中维护的最小空闲连接数。即使当前没有任何客户端使用数据库连接，连接池中也会维护一些连接供将来使用。这个配置就是指定连接池中保持连接的最小个数**

- **spring.datasource.hikari.maximum-pool-size：最大池大小。因为连接池中的连接会随着客户端连接的增长而增长，因为客户端不断的申请连接，连接池也需要去创建连接。但是，这种增长不能是无限制的，这会导致内存被耗尽。所以，这个配置用于限制连接池维护的最大连接数。一旦与客户端的连接达到了这个数字，即使客户端再来申请，也只能等待其他的客户端释放连接，或者报连接超时异常**

- **spring.datasource.hikari.idle-timeout：允许连接在连接池中空闲的最长时间（毫秒）。这个参数比较好理解，空闲的连接一直保持在连接池中，无疑是资源的浪费。所以，超过一定时间之后，连接池会主动释放掉。但是，需要注意，连接池中总会维护一些连接，这个数字由minimum-idle控制**

- **spring.datasource.hikari.max-lifetime：池中连接关闭后的最长生命周期（毫秒）。在使用中的连接永远不会被关闭，只有当它关闭时才会在最长生命周期后删除掉**

- **spring.datasource.hikari.auto-commit：从池返回的连接的默认自动提交行为（默认为 true）。在InnoDb表中，所有的语句都是需要commit后，才会在真实数据库中生效**

  

### 3.Spring Data Jpa

![屏幕截图(122)](C:\Users\晴天真美\Pictures\Screenshots\微服务\屏幕截图(122).png)

![屏幕截图(123)](C:\Users\晴天真美\Pictures\Screenshots\微服务\屏幕截图(123).png)

### 4.数据库连接池

![屏幕截图(124)](C:\Users\晴天真美\Pictures\Screenshots\微服务\屏幕截图(124).png)

![屏幕截图(125)](C:\Users\晴天真美\Pictures\Screenshots\微服务\屏幕截图(125).png)

### 5.Kafka

- **Topic：在Kafka中，使用一个类别属性来划分数据的所属类，划分数据的这个类称为topic。如果把Kafka看做为一个数据库，topic可以理解为数据库中的一张表，topic的名字即为表名。**
- **Partition：topic中的数据分割为一个或多个partition。每个topic至少有一个partition。每个partition中的数据使用多个文件存储。partition中的数据是有序的，partition间的数据丢失了数据的顺序。如果topic有多个partition，消费数据时就不能保证数据的顺序。在需要严格保证消息的消费顺序的场景下，需要将partition数目设为1。（我们在创建Kafka  Topic的时候，是可以指定partition的个数的）。**
- **Partition offset：每条消息都有一个当前Partition下唯一的64字节的offset，它指明了这条消息的起始位置。**
- **Replicas of  partition：副本是一个分区的备份。副本不会被消费者消费，副本只用于防止数据丢失。即消费者一定不会消费副本partition中的数据，而是从leader的partition中读取数据。同时，还需要注意的是，我们的单机版Kafka不能给partition设置副本。因为我们只有一台机器，而副本需要保存在其他的机器或者实例上。**
- **Broker：Kafka  集群包含一个或多个服务器，服务器节点称为broker。broker存储topic的数据。如果某topic有N个partition，集群有N个broker，那么每个broker存储该topic的一个partition，主要为了维护整个系统的负载均衡。**
- **Producer：生产者即数据的发布者，该角色将消息发布到Kafka的topic中。broker接收到生产者发送的消息后，broker将该消息追加到当前用于追加数据的partition文件中。生产者发送的消息，存储到一个partition中，生产者也可以指定数据存储的partition。**
- **Consumer：消费者可以从broker中读取数据。消费者可以消费多个topic中的数据。多个 Consumer 构成 Group。**

![屏幕截图(126)](C:\Users\晴天真美\Pictures\Screenshots\微服务\屏幕截图(126).png)

![屏幕截图(127)](C:\Users\晴天真美\Pictures\Screenshots\微服务\屏幕截图(127).png)![屏幕截图(128)](C:\Users\晴天真美\Pictures\Screenshots\微服务\屏幕截图(128).png)

![屏幕截图(129)](C:\Users\晴天真美\Pictures\Screenshots\微服务\屏幕截图(129).png)

![屏幕截图(130)](C:\Users\晴天真美\Pictures\Screenshots\微服务\屏幕截图(130).png)

![屏幕截图(131)](C:\Users\晴天真美\Pictures\Screenshots\微服务\屏幕截图(131).png)

![屏幕截图(132)](C:\Users\晴天真美\Pictures\Screenshots\微服务\屏幕截图(132).png)

![屏幕截图(133)](C:\Users\晴天真美\Pictures\Screenshots\微服务\屏幕截图(133).png)



**windows 下 需要把.sh改为.bat , bin需要加上 bin\windows\ **

```
使用 Kafka
功能	启动命令	备注
启动 ZK	bin/zookeeper-server-start.sh  config/zookeeper.properties	Kafka 安装包自带 ZK，可以单节点启动
启动 Kafka 服务器	bin/kafka-server-start.sh config/server.properties	
创建 Topic（test）	bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test	
Topic 列表	bin/kafka-topics.sh --list --zookeeper localhost:2181	
启动 Producer	bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test	
启动 Consumer	bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning	
Topic 相关信息（test）	bin/kafka-topics.sh --describe --zookeeper localhost:2181 --topic test使用 Kafka
```





##  二.SpringBoot的常用特性

### SpringBoot 应用启动入口

- **在SpringBoot的入口类中，我们通常是通过调用SpringApplication的run方法（一个静态方法），另外再加上@SpringBootApplication注解来启动SpringBoot项目**
- **通过调用SpringApplication的方法，调整应用的行为**
- **SpringApplicationBuilder提供了Fluent API，可以实现链式调用。我们可以用它来实现刚刚定义的功能，可以发现代码层面在编写上较为方便**

***示例代码***

```java
@SpringBootApplication
public class SpringBootStudyApplication {

    public static void main(String[] args) {

        // 1. 通过静态 run 方法
        SpringApplication.run(SpringBootStudyApplication.class, args);

        // 2. 通过 api 调整应用行为
        SpringApplication application =
                new SpringApplication(SpringBootStudyApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);

        // 3. SpringApplicationBuilder Fluent Api, 链式调用
        new SpringApplicationBuilder(SpringBootStudyApplication.class)
                .bannerMode(Banner.Mode.OFF)
//                .web(WebApplicationType.NONE)
                .run(args);
    }
}
```

### SpringBoot 自动配置原理

***简单的说，自动配置就是会根据在类路径中的jar、类自动配置Bean。Spring Boot将所有的功能场景都抽取出来，做成一个个的starter（启动器），只需要在项目里面引入这些starter，相关场景的所有依赖都会导入进来。***

- **自动配置就是基于三个重要的注解实现的（实际就是 @SpringBootApplication 注解）**

```java
// @SpringBootConfiguration：我们点进去以后可以发现底层是Configuration注解，其实就是支持JavaConfig的方式来进行配置(使用Configuration配置类等同于XML文件)
// @EnableAutoConfiguration：这个注解用来开启自动配置，是自动配置实现的核心注解
// @ComponentScan：这个注解，学过Spring的同学应该对它不会陌生，就是扫描注解，默认是扫描当前类下的package。将@Controller/@Service/@Component/@Repository等注解加载到IOC容器中
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
public @interface SpringBootApplication {
}
```

- **@EnableAutoConfiguration源码**

```java
// @AutoConfigurationPackage：自动配置包
// @Import：给IOC容器导入组件
@AutoConfigurationPackage
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {
}
```

- **@AutoConfigurationPackage 源码**

```java
@Import(AutoConfigurationPackages.Registrar.class)
public @interface AutoConfigurationPackage

public void registerBeanDefinitions(AnnotationMetadata metadata,
                BeanDefinitionRegistry registry) {
  register(registry, new PackageImport(metadata).getPackageName())
        }
// 很容易可以看到，它的作用就是将主配置类(@SpringBootApplication)的所在包及其子包里边的组件扫描到Spring容器中
```

- **@Import(AutoConfigurationImportSelector.class) 源码**

```java
public String[] selectImports(AnnotationMetadata annotationMetadata) {
        if (!isEnabled(annotationMetadata)) {
            return NO_IMPORTS;
        }
        AutoConfigurationMetadata autoConfigurationMetadata = AutoConfigurationMetadataLoader
                .loadMetadata(this.beanClassLoader);
        AutoConfigurationEntry autoConfigurationEntry = getAutoConfigurationEntry(
                autoConfigurationMetadata, annotationMetadata);

// 可以得到了很多配置信息
protected AutoConfigurationEntry getAutoConfigurationEntry(...) {
    AnnotationAttributes attributes = getAttributes(annotationMetadata);
    List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes);

// 配置信息从这里来
protected List<String> getCandidateConfigurations(...) {
    List<String> configurations = SpringFactoriesLoader.loadFactoryNames(...);

// 配置加载的位置
public static List<String> loadFactoryNames(...) {
    String factoryClassName = factoryClass.getName();
    return loadSpringFactories(classLoader)...;
}

private static Map<String, List<String>> loadSpringFactories(...) {
    Enumeration<URL> urls = (classLoader != null ?
        classLoader.getResources(FACTORIES_RESOURCE_LOCATION) :
        Properties properties = PropertiesLoaderUtils.loadProperties(resource);
        for (Map.Entry<?, ?> entry : properties.entrySet()) {
            result.add(factoryClassName, factoryName.trim());

// 这个方法也就是自动配置的核心实现了，主要是三点内容：
// FACTORIES_RESOURCE_LOCATION的值是META-INF/spring.factories
// Spring启动的时候会扫描所有jar路径下的META-INF/spring.factories，将其文件包装成Properties对象
// 从Properties对象获取到key值为EnableAutoConfiguration的数据，然后添加到容器里边。
```

### SpringBoot 配置文件

- **同一个目录下的 application 和 bootstrap**
  - **bootstrap 优先级高于 application，优先被加载**
  - **bootstrap 用于应用程序上下文的引导阶段，由父 ApplicationContext 加载**
  - **bootstrap 是系统级别的配置（不变的参数），application 是应用级别的配置**
- **不同位置的配置文件加载顺序（优先级）**
  - **file：./config/ - 优先级最高（项目根路径下的 config）**
  - **file：./ - 优先级第二（项目根路径下）**
  - **classpath:/config/ - 优先级第三（项目 resources/config 下）**
  - **classpath:/ - 优先级第四（项目 resources 目录下）**
  - **高优先级覆盖低优先级相同配置、多个配置文件互补**

### SpringBoot Actuator监控

***微服务的特点决定了功能模块的部署是分布式的，大部分功能模块都是运行在不同的机器上，彼此通过服务调用进行交互，前后台的业务流会经过很多个微服务的处理和传递，出现了异常如何快速定位是哪个环节出现了问题？***
 ***在这种框架下，微服务的监控显得尤为重要。SpringBoot给我们提供了一个用于监控的组件：Actuator，它也是一个starter，方便在日常的开发、运行中对我们的微服务进行监控治理。***

- Actuator监控分类
  - **应用配置类：可以查看应用在运行期的静态信息：例如自动配置信息、加载的 springbean 信息、yml 文件配置信息、环境信息、请求映射信息**
  - **度量指标类：主要是运行期的动态信息，例如堆栈、请求连、一些健康指标、metrics 信息等**
  - **操作控制类：主要是指 shutdown,用户可以发送一个请求将应用的监控功能关闭**

### 配置注入的方式

- **直接使用 @Value**
- **使用 @ConfigurationProperties + prefix 的方式**

### Jackson 的使用技巧

***通常在项目中处理JSON一般用的都是阿里巴巴的fastjson， 后来发现使用Spring Boot内置的Jackson来完成JSON的序列化和反序列化操作也是非常方便的。***

- **@JsonProperty，作用在属性上，用来为JSON Key指定一个别名**
- **@Jsonlgnore，作用在属性上，用来忽略此属性**
- **@JsonIgnoreProperties，忽略一组属性，作用于类上**
- **@JsonFormat，用于日期格式化**
- **Jackson通过使用ObjectMapper的writeValueAsString方法将Java对象序列化为JSON格式字符串**
- **反序列化使用 ObjectMapper 的 readValue**

### 定时任务

- **@EnableScheduling：允许当前的应用开启定时任务**
- **@Scheduled：指定定时任务的运行规则**

### 异步任务

***通常代码都是顺序执行（一行一行的执行），这也就是同步调用。但是异步编程却没有这样的限制，代码执行并不是阻塞的。可以直接调用不用等待返回，而是在某一个想要获取结果的时间点再去获取结果。***

- **引入spring-boot-starter-web依赖**
- **在SpringBoot入口类上加上 @EnableAsync 注解，开启异步支持**
- **只需要在方法上加上 @Async 注解，则当前方法就是异步方法**

***默认情况下的异步线程池配置使得线程不能被重用，每次调用异步方法都会新建一个线程，我们可以自己定义异步线程池来优化。***

### 单元测试

***编写单元测试可以帮助开发人员编写高质量的代码，提升代码质量，减少Bug，便于重构。SpringBoot提供了一些实用程序和注解，用来帮助我们测试应用程序，在SpringBoot中开启单元测试只需引入spring-boot-starter-test即可，其包含了一些主流的测试库。***

**一个标准的SpringBoot测试用例应该包含两个注解：**

- **@SpringBootTest：意思是带有 SpringBoot 支持的引导程序，其中提供了可以指定 Web 环境的参数**
- **@RunWith(SpringRunner.class)：告诉JUnit运行使用Spring的测试支持。SpringRunner是SpringJUnit4ClassRunner的新名字，这个名字只是让名字看起来简单些**

## 三.系统通用组件搭建

### 1.0搭建网关

![](C:\Users\晴天真美\Pictures\Screenshots\微服务\屏幕截图(140).png) 

#### 1.1自定义抽象过滤器

![](C:\Users\晴天真美\Pictures\Screenshots\微服务\屏幕截图(141).png)

- **pre：在请求被路由之前调用**
- **route：在路由请求时被调用**
- **post：在 route 和 error 过滤器之后被调用**
- **error：处理请求时发生错误时被调用**

```
/**
 * 通用的抽象过滤器类
 */
public abstract class AbstractZuuleFilter extends ZuulFilter {

    //用于过滤器之间传递消息,数据保存在每个请求的 ThreadLocal 中
    //扩展了Map
    RequestContext requestContext;

    private final static String NEXT= "next";

    @Override
    public boolean shouldFilter() {
        RequestContext  ctx=RequestContext.getCurrentContext();
        return (Boolean)ctx.getOrDefault(NEXT,true);
    }

    @Override
    public Object run() throws ZuulException {
        requestContext=RequestContext.getCurrentContext();
        return cRun();
    }

    protected abstract Object cRun();

    Object fail(int code,String msg){
        requestContext.set(NEXT,false);
        requestContext.setSendZuulResponse(false);
        requestContext.getResponse().setContentType("text/html;charset=utf-8");
        requestContext.setResponseStatusCode(code);
        requestContext.setResponseBody(String.format("{\"result\": \"%s!\"}",msg));
        return null;
    }

    Object success(){
        requestContext.set(NEXT,true);
        return null;
    }
}
```

##### 1.1.2 继承AbstractZuuleFilter,定义过滤器的FilterConstants.POST_TYPE

```
package com.imooc.conpon.filter;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

public abstract class AbstractPostZuuleFilter extends AbstractZuuleFilter{
    @Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
    }
}
```

##### 1.1.3 自定义Token校验过滤器

```
package com.imooc.conpon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 校验请求中传递的 token
 */
@Slf4j
@Component
public class TokenFilter extends AbstractPreZuuleFilter {
    @Override
    protected Object cRun() {
        HttpServletRequest request=requestContext.getRequest();
        log.info(String.format("%s request %s"),request.getMethod(),
                request.getRequestURL().toString());
        Object token=request.getParameter("token");
        if(token==null){
            log.error("error: token is empty");
            return fail(401,"error: token is empty");
        }
        return success();
    }

    @Override
    public int filterOrder() {
        return 1;
    }
}
```

##### 1.1.4 自定义限流过滤器

```
package com.imooc.conpon.filter;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 限流过滤器
 */
@Slf4j
@Component
@SuppressWarnings("all") //忽略所有错误
public class RateLimiterFilter extends AbstractPreZuuleFilter {

     //每秒可以获得两个令牌,限流器
    RateLimiter rateLimiter=RateLimiter.create(2.0);

    protected Object cRun() {
        HttpServletRequest request=requestContext.getRequest();
        if (rateLimiter.tryAcquire()){//尝试去获取令牌
            log.info("get rate token success");
            return success();
        }else {
            log.error("rate limit {}",request.getRequestURI());
            return fail(402,"error: rate limit");
        }
    }

    @Override
    public int filterOrder() {
        return 2;
    }
}
```

##### 1.1.4 自定义访问日志过滤器

```
package com.imooc.conpon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 在过滤器中存取客户端发起请求的时间戳
 */
@Slf4j
@Component
public class PreRequestFilter extends AbstractPreZuuleFilter {
    @Override
    protected Object cRun() {
        requestContext.set("startTime",System.currentTimeMillis());
        return success();
    }

    @Override
    public int filterOrder() {
        return 0;
    }
}
```

```
package com.imooc.conpon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 *在过滤器中获取请求响应时间  : 当前时间戳-startTime
 */
@Slf4j
@Component
public class AccessLogFilter extends AbstractPostZuuleFilter {

    protected Object cRun() {
        HttpServletRequest request = requestContext.getRequest();

        //从 PreRequestFilter 获取设置的请求时间戳
        Long startTime = (Long) requestContext.get("startTime");
        String uri = request.getRequestURI();
        long duration = System.currentTimeMillis() - startTime;

        //从网关通过的请求都会打印日志记录: uri+duration
        log.info("uri: {},duration: {}", uri, duration);
        return success();
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SEND_RESPONSE_FILTER_ORDER - 1;
    }
}
```

##### 1.1.5 提问?

**1. Zuul 的功能大部分都是由过滤器实现的，你还可以定义怎样的过滤器实现你想要的功能呢 ？**

       端口、服务健康信息、续约信息等，存储于专门为服务开辟的注册表中，用于其他组件使用实现整个微服务生态。
       还可以自定义元数据信息，使用 eureka.instance.metadata-map.=value来配置。内部其实就是维护了一个 Map 来保存自定义元数据信息，可以配置在远端服务，随服务一并注册保存在 Eureka 注册表中。

**2. 如果要给我们的系统接入用户模块（用户和权限），放在网关里面做合适吗 ？**

```
  用户模块往往都会与权限独立的成为一个微服务存在，这个服务可以提供例如：用户注册、用户登录、权限校验这类单一的功能。另外，这个服务不仅仅是可以应用在某一个业务里面。很多时候，是整个公司提供一个用户账户服务，所有的产品线或者业务工程都会使用这一个用户服务。
   我们的 SpringCloud 框架服务，入口是网关（Zuul、GateWay 等等），最合理的方式做权限校验一定是放在入口处，而不要下推到功能服务（Service、微服务）里面。所以，放在网关中做权限校验是合理的。
```

## 四.微服务通用模块

***一个大的业务系统拆分为多个小的功能微服务，必然会存在着一些代码在多个微服务中都会用到。这类代码我们称之为通用代码或者基础代码，通常，我们会把它们定义在一个（或者多个） xxx-common 包中（jar）， 让其他的微服务去依赖它。***

 ###  设计思想与实现的功能:

#### 设计思想

- **通用的代码、配置不应该散落在各个业务模块中，不利于维护与更新**
- **一个大的系统，响应对象需要统一外层格式**
- **各种业务设计与实现，可能会抛出各种各样的异常，异常信息的收集也应该做到统一**

### 实现难点与不易理解的知识点说明:

#### 1.1 统一响应

**响应对象定义:**

```
/**
 * <h1>响应对象定义</>
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> implements Serializable {

    private Integer code;
    private String message;
    private T data;

    public CommonResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
```

**忽略统一响应注解定义:**

```
/**
 * <h1>忽略统一响应注解定义</h1>
 */

@Target({ElementType.TYPE,ElementType.METHOD})  //将注解标注在什么类型上面(类,方法)
@Retention(RetentionPolicy.RUNTIME)  //运行时起作用
public @interface IgnoreResponseAdvice {
}
```

**统一响应:**

```
/**
 * <h1>统一响应</h1>
 */
@RestControllerAdvice   //对所有controller请求进行拦截
public class CommonResponseDataAdvice implements ResponseBodyAdvice<Object> {

    /**
     *<h1>判断是否对响应进行处理</h1>
     * @param methodParameter
     * @param aClass
     * @return
     */
    @SuppressWarnings("all")
    public boolean supports(MethodParameter methodParameter,  //当前controller方法的定义
                            Class<? extends HttpMessageConverter<?>> aClass) {
        //如果当前方法所在的类标识了@IgnoreResponseAdvice 注解,不需要处理
        if(methodParameter.getDeclaringClass().isAnnotationPresent(IgnoreResponseAdvice.class)){
            return false;
        }
        //如果当前方法标识了@IgnoreResponseAdvice 注解,不需要处理
        if (methodParameter.getMethod().isAnnotationPresent(IgnoreResponseAdvice.class)){
            return false;
        }
        //对响应进行处理 执行 beforeBodyWrite 方法
        return true;
    }

    /**
     *<h2>响应之前进行处理beforeBodyWrite</h2>
     */
    @SuppressWarnings("all")
    public Object beforeBodyWrite(Object o,   //方法的响应对象  ,响应之前进行处理beforeBodyWrite
                                  MethodParameter methodParameter,
                                  MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> aClass,
                                  ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {
        //定义最终的返回对象
        CommonResponse<Object> response=new CommonResponse<>(0,"");
        //如果 o 是 null, 不需要设置data
        if(o==null){
            return response;
            //如果 o 已经是CommonResponse,不需要再次处理
        }else if(o instanceof CommonResponse){
            response=(CommonResponse<Object>) o;
            //否则把响应对象作为 CommonResponse 的data的部分
        }else {
            response.setData(o);
        }
        return response;
    }
}
```

#### 1.2 通用异常

**优惠券通用异常定义:**

```
/**
 * <h1>优惠券通用异常定义</h1>
 */
public class CouponException extends Exception{

    public CouponException(String message){
         super(message);
    }
}
```

**全局异常处理:**

```
/**
 * 全局异常处理
 */

@RestControllerAdvice   //对所有controller请求进行拦截
public class GlobaExceptionAdvice {

    /**
     * <h2>对CouponException进行统一异常处理</h2>
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(value = CouponException.class)  //ExceptionHandler: 可以对指定的异常进行拦截
    public CommonResponse<String> handlerCouponException(HttpServletRequest request,
                                                         CouponException ex){
        // 统一异常接口的响应
        // 优化: 定义不同类型的异常枚举(异常码和异常信息)
          CommonResponse<String> response=new CommonResponse<>(-1,"business error");
          response.setData(ex.getMessage());
          return response;
    }
}
```

#### 1.1.3 提问?

1.如果要对统一响应格式做扩展，你会考虑怎么做？这么做的理由是什么呢？

```
   可以对统一响应做扩展，加上一些错误枚举类（包含错误码和错误描述）。需要把这些错误的枚举类定义在通用模块中。
```

2.除了通用的 CouponException，你可能还会设计哪些自定义异常类呢？这样设计的理由是什么呢？

```
   优惠券系统包含了三个功能微服务：模板、分发和结算。每一个微服务中都可能会抛出各类异常，可以分模块去确定各类异常。
```

3.你还能想到哪些通用的代码可以放到 common 模块中？

```
可以把实体的基类、业务对象（VO）和工具类放到 common 模块中。
```

## 五.优惠券系统整体业务思想和架构

### 1.1.1 优惠券模板微服务业务思想

***这一章完整的介绍了优惠券系统的业务思想，包含三个功能微服务：模板微服务、分发微服务和结算微服务。之后对存储方面的设计进行了介绍，包含  MySQL 和 Redis 缓存的设计思想。最后，对系统的整体架构进行了介绍。优惠券系统的架构分为两类：SpringCloud  组件架构和功能微服务架构。***

### 1.1.2 模板微服务

**先由运营人员创建优惠券模板，之后再去生成对应数量的优惠券，最后用户才可以去领取优惠券。这个模块（或者微服务）的核心功能都是围绕优惠券模板的。运营人员设定好条件（名称、logo、分类、产品线、数量、规则等等），后台异步创建优惠券模板。之所以是异步过程，是因为创建优惠券模板的过程是比较耗时的，HTTP接口不返回是一种不好的用户体验。**

**生成优惠券码需要考虑两个方面：**

- **不可以重复**
- **有一定的识别性**

***最终，我把优惠券码设定为18位，由三个部分组成：***

- **前四位：产品线和类型**
- **中间六位：日期随机**
- **后八位：0 ~ 9 之间的随机数**

***业务思想如下图所示***

![](C:\Users\晴天真美\Pictures\Screenshots\微服务\屏幕截图(142).png)

**模板创建的一个关键步骤是异步的生成对应的优惠券码（前面已经介绍了它是怎样构成的），并保存到 Redis 中。需要注意的地方：**

- **提高异步线程池的效率，自定义线程池实现**
- **静态单实例生成优惠券码**

***业务思想如下图所示***
 ![](C:\Users\晴天真美\Pictures\Screenshots\微服务\屏幕截图(143).png)

**运营人员创建的优惠券模板不可能是一直有效的（模板一旦过期，它所对应的优惠券则不能再分发给用户。但是，已经分发给用户的，可以是不过期的），所以，需要有一个过期机制能够让过期的优惠券不返回给用户展示。我在这里设计了两种实现策略：**

- **优惠券模板模块中实现一个定时任务，例如每个小时运行一次，定时清理过期的优惠券模板**
- **其他模块从模板模块获取优惠券模板时，自己去判断是否已经过期。之所以需要这样做，是因为定时任务总会存在一个定时间隔的延迟，并不能保证实时的过期**

### 1.1.3 分发微服务

***优惠券分发模块主要涉及四个核心的功能点。***

#### 根据用户id和优惠券状态查找用户优惠券记录

- **首先，由于我们的系统暂时没有接入用户系统，所以，关于用户相关的创建、校验等功能是没有的，这些会在代码中进行简单的fake，或者叫做mock数据；这其实也很常见，我们在实际的企业级开发中，也会通过这样的方式去完成应用和对应用可用性的验证工作**
- **第二，我这里把属于用户的优惠券状态（注意，这里所说的优惠券是用户相关的，需要与优惠券模板区分开）定义为三类。可用的和已使用的都是字面意思，过期的指的是超出了优惠券的有效使用期，但是仍未被使用的**
- **第三，为了提升系统的响应速度，把用户的数据存储于Redis中，也就是与用户相关的优惠券信息都存储于Redis中；可以想象，在将来，展示用户数据的时候，将直接从Redis中读取**
- **第四，第二条中说到优惠券存在过期的状态，那么，什么时候确定优惠券过期了呢？这里也会使用延迟处理的策略。也就是当用户查看自己优惠券的时候，判断是否存在过期的但是没有被标记的优惠券。如果存在，除了展示用户优惠券信息外，再做额外的过期处理**

***业务思想如下图所示***

![](C:\Users\晴天真美\Pictures\Screenshots\微服务\屏幕截图(145).png)

#### 根据用户id查找当前可以领取的优惠券模板

- **第一，优惠券模板是一个独立的服务，所以，分发模块需要通过微服务调用去获取模板数据。但是访问任何一个微服务都存在不确定性，所以，这里要有熔断兜底的策略**
- **第二，从模板服务中获取到的优惠券模板，并不一定都是可领取的，需要去比对优惠券模板的相关限制。例如，有一张优惠券模板A，限制用户只能领取一张可用。那么，如果之前用户已经领取过了，且状态仍是可用状态，则这次就不能再次领取了**

***业务思想如下图所示***

![](C:\Users\晴天真美\Pictures\Screenshots\微服务\屏幕截图(146).png)

#### 用户领取优惠券

- **第一，优惠券模板是一个独立的服务，所以，分发模块需要通过微服务调用去获取模板数据。但是访问任何一个微服务都存在不确定性，所以，这里要有熔断兜底的策略**
- **第二，从模板服务中获取到的优惠券模板，并不一定都是可领取的，需要去比对优惠券模板的相关限制。例如，有一张优惠券模板A，限制用户只能领取一张可用。那么，如果之前用户已经领取过了，且状态仍是可用状态，则这次就不能再次领取了**
- **第三，由于每一张优惠券模板都要求它们所对应的优惠券要有优惠券码，且在生成的时候，直接放入到Redis中。所以，这里需要尝试从Redis中获取优惠券码**
- **第四，通过了验证，即优惠券模板是可以领取的，且成功获取到了优惠券码，就可以将优惠券写入MySQL和Redis了**

***业务思想如下图所示***
 ![](C:\Users\晴天真美\Pictures\Screenshots\微服务\屏幕截图(147).png)

#### 结算（核销）优惠券

- **第一，无论是结算还是核销，都需要对前端/客户端传递的参数值进行校验，判断当前用户想要使用的优惠券是否是合法的，合法的标准是属于当前用户且优惠券的状态是可用**
- **第二，由于我们的分发微服务直接面向用户，而结算这样的功能实际只与优惠券的相关，更细致的说，是只与优惠券模板定义的规则相关。所以，结算功能不放在分发微服务中，而是由优惠券系统中的第三个功能微服务负责，即结算微服务**
- **第三，需要知道，结算和核销是两个不同的概念。结算是计算利用优惠券可以优惠的金额，但并不是使用。这种场景发生在我们付款之前，付款之前，优惠券并未使用，但是，也会显示使用优惠券之后优惠的金额和实际需要结算的金额。而核销则是使用优惠券。所以，对于核销这种情况，需要把数据回写到数据库中**

***业务思想如下图所示***

![](C:\Users\晴天真美\Pictures\Screenshots\微服务\屏幕截图(148).png)

### 1.1.4 结算微服务

***结算微服务只提供一个功能：根据优惠券类型结算优惠券***

- **第一，我们在设计优惠券的时候，会对优惠券设置不同的分类，例如：满减类、折扣类，大家也可以自行扩展更多的分类**
- **第二，由于优惠券种类的不同，自然会有不同的结算方式，或者说结算的算法。例如，满减券是根据满多少金额减去多少金额，而折扣券是直接打一定的折扣等等。另外，更复杂的情况是优惠券之间可以组合。例如满减和折扣组合，先去满减，再去打一定的折扣。需要注意，由于优惠券种类比较多，如果枚举出所有的组合，将会有巨大的工作量。所以，我在课程中，给出了一个组合优惠券的结算过程，其他的组合方式，大家可以按照我的实现方式自行修改，这个过程也并不会很复杂**

***业务思想如下图所示***

![](C:\Users\晴天真美\Pictures\Screenshots\微服务\屏幕截图(149).png)

### 1.1.5存储设计

 ####  MySQL 表设计

**系统中一共有两张 MySQL 表：**

**优惠券模板表：优惠券模板是与用户无关的，是对一类优惠券的描述。运营人员通过设定模板，来描述优惠券的各种信息。**

![](C:\Users\晴天真美\Pictures\Screenshots\微服务\c5ab9976gy1g6590o367kj20wx0i2n41.jpg)

**用户优惠券表：优惠券模板是用来描述优惠券的，而优惠券表则是记录用户用户优惠券信息。这张表比较简单，除了主键之外，只有5个字段。**

![](C:\Users\晴天真美\Pictures\Screenshots\微服务\c5ab9976gy1g6590rj30dj20xy0b6n1a.jpg)

#### Redis 缓存设计

**对于缓存，也是有两类，且都是使用Redis来实现。**

- 优惠券码缓存
  - **使用Redis实现，KV类型的缓存**
  - **Key是需要有意义的，即最好能够根据Key来识别它对应的是什么数据。且需要注意，Redis这类基础工具往往是通用的，不要与其他的Key有冲突**
  - **由于优惠券码需要一直保持在系统中，等待分发（即等待用户的领取），所以，并不设置过期时间。**

***总结下来，为了保证优惠券码的Key不冲突，以前缀+主键的形式构成；且使用list类型（当然，使用set也是可以的）来保存优惠券码。***

- 用户优惠券信息缓存
  - **使用Redis实现，KV类型的缓存**
  - **Key是需要有意义的，即最好能够根据Key来识别它对应的是什么数据。且需要注意，Redis这类基础工具往往是通用的，不要与其他的Key有冲突**
  - **由于优惠券分为3类，为了更加高效的检索，我这里的实现也会使用到三个缓存去实现。且由于每一类优惠券都可能是很多个，这里我选择使用Redis的hash类型**
  - **由于用户数据量比较大，且在MySQL中保存有完整的用户信息。所以，不在Redis中长时间保留用户优惠券信息。需要设置一个过期时间**

***用户优惠券信息缓存的key是前缀+用户id的形式；value是hash类型，hash的key是优惠券id，hash的value是优惠券信息。***

#### 架构设计

**SpringCloud微服务组件架构**

**这里主要是两个组件：Eureka和Zuul。客户端的请求入口是Zuul，也就是整个系统的网关服务。网关服务的最核心功能是能够根据请求做分发。把不同的请求分发到对应的微服务上去。Eureka  Server是整个系统的注册中心，是SpringCloud服务治理的基础。不论是网关还是功能微服务，都需要把自己注册到Eureka  Server上。各自在需要系统元信息的时候，再去询问Eureka Server去主动获取。**

![](C:\Users\晴天真美\Pictures\Screenshots\微服务\c5ab9976gy1g65980oqx9j21bq0fu0v5.jpg)

**功能微服务架构设计**

**结算服务是比较独立的。目前只是我们的优惠券分发服务在做结算时会使用到。但是，对于结算，可以设计的更加通用，不只是优惠券的结算，还可以扩展成商品的结算等等。所以，在实现上，我会把结算服务单独的作为一个微服务。模板服务和结算服务不依赖于其他的服务，而分发服务则会依赖它们两个。实现上，需要考虑调用方式和熔断降级策略。**

![](C:\Users\晴天真美\Pictures\Screenshots\微服务\c5ab9976gy1g6598clgwzj216m0imjtj.jpg)

## 六.创建模板微服务

***这一章完成了优惠券系统第一个功能微服务-模板微服务的代码编写工作，内容包括：创建模板模块（在 imooc-coupon-service 下面）、编写服务功能代码、编写测试用例校验 service 的可用与正确性。***

###  创建模板模块

**模板模块属于优惠券系统中的功能微服务类别（另一类是 SpringCloud 基础组件），所以，把它置于 imooc-coupon-service 父模块的目录中。创建模块的步骤非常简单：**

- **新建 Maven 工程**
- **修改 pom.xml 文件，添加对应的依赖项**

**关于依赖项，有两个需要注意：MySQL 的驱动一定要与你使用的 MySQL 版本对应；通用模块是所有微服务的依赖项，理解其含义**

```xml
<!-- MySQL 驱动, 注意, 这个需要与 MySQL 版本对应 -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.12</version>
    <scope>runtime</scope>
</dependency>
<!-- 通用模块 -->
<dependency>
    <groupId>com.imooc.coupon</groupId>
    <artifactId>coupon-common</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

- **编写 SpringBoot 应用启动程序**

```java
/**
 * <h1>模板微服务的启动入口</h1>
 * Created by Qinyi.
 */
@EnableScheduling       // 允许启动定时任务
@EnableJpaAuditing      // 启用 Spring Data JPA 审计功能，自动填充或更新实体中的CreateDate
@EnableEurekaClient     // 标识当前的应用是 EurekaClient
@SpringBootApplication  // 标识当前是 SpringBoot 应用
public class TemplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(TemplateApplication.class, args);
    }
}
```

### 编写优惠券模板服务功能代码 

**服务功能代码的编写包含了以下的几个过程（代码编写过程）：**

- **数据表实体类**
- **数据表 Dao 接口**
- **服务功能接口定义**
- **服务功能接口实现**
- **对外服务接口（Controller）实现**
- **网关路由配置定义**

***功能微服务的代码实现肯定要建立在对业务思想的理解上，特别是对数据表（和缓存）的理解。在知道了要做什么之后，剩下的就是按部就班的去完成怎么做。可以在编码之前在纸上画一画各个功能之间的状态流转图，以此来更好的指导对代码的实现。***

**异步生成优惠券码,具体service实现,项目中有实现和注释**

```
/**
 * 异步服务接口类
 */
public interface AsyncService {

    /**
     * 根据模板异步创建优惠券码
     * @param template {@link CouponTemplate} 模板实体
     */
    void asyncConstructCouponByTemplate(CouponTemplate template);

}
```



**自定义异步任务线程池**

```
/**
 * 自定义异步任务线程池
 */
@Slf4j
@EnableAsync
@Configuration
public class AsyncPoolConfig implements AsyncConfigurer {

    @Bean
    public Executor getAsyncExecutor() {
        //定义线程池实现
        ThreadPoolTaskExecutor executor=new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); //核心线程数
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(20);  //队列
        executor.setKeepAliveSeconds(60); //空闲时的生存时间
        executor.setThreadNamePrefix("ImoocAsync_");
        executor.setWaitForTasksToCompleteOnShutdown(true); //任务关闭时线程池是否退出
        executor.setAwaitTerminationSeconds(60); //服务关闭时线程的最长等待时间
        executor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.CallerRunsPolicy()  //拒绝策略
        );
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncExceptionHandler();
    }

    class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler{

        //异常捕获Handler
        @Override
        public void handleUncaughtException(Throwable throwable, //异常任务抛出异常
                                            Method method,  //异步任务对应方法
                                            Object... objects){ //异步任务参数数组
                 throwable.printStackTrace(); //打印异常堆栈
              log.error("AsyncError{},method{},Param{}",throwable.getMessage(),
                      method.getName(),JSON.toJSONString(objects));

              //TODO 发送邮件短信,做进一步的处理
        }
    }
```



**构建优惠券模板接口定义**

```
/**
 * 构建优惠券模板接口定义
 */
public interface BuildTemplateService {

    /**
     * 创建优惠券模板
     * @param request {@link TemplateRequest} 模板信息请求对象
     * @return {@link CouponTemplate} 优惠券模板实体
     * @throws CouponException
     */
    CouponTemplate buildTemplate(TemplateRequest request)throws CouponException;
}
```

**优惠券模板基础(view,delete,update)服务定义**

```
/**
 * 优惠券模板基础(view,delete,update)服务定义
 */
public interface TemplateBaseService {

    /**
     * 根据优惠券模板 id 获取优惠券模板信息
     * @param id 模板id
     * @return {@link CouponTemplate} 优惠券实体
     * @throws CouponException
     */
    CouponTemplate buildTemplateInfo(Integer id)throws CouponException;

    /**
     * 查找所有可用的优惠券模板
     * @return {@link CouponTemplate} s
     */
    List<CouponTemplateSDK> findAllUsableTemplate();

    /**
     * 获取模板 ids 到 CouponTemplateSDK的映射
     * @param ids 模板 ids
     * @return Map<key: ids, value: CouponTemplateSDK>
     */
    Map<Integer,CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids);
}
```

### 编写优惠券模板Controller

```
/**
 *优惠券模板controller
 */
@Slf4j
@RestController
public class CouponTemplateController {

    private final BuildTemplateService buildTemplateService;

    private final TemplateBaseService templateBaseService;

    @Autowired
    public CouponTemplateController(BuildTemplateService buildTemplateService, TemplateBaseService templateBaseService) {
        this.buildTemplateService = buildTemplateService;
        this.templateBaseService = templateBaseService;

    }

    /**
     * 创建优惠券模板
     * 127.0.0.1:7001/distribution-template/template/build
     * 127.0.0.1:9000/coupon-template/distribution-template/template/build
     * @param request
     * @return
     * @throws CouponException
     */
    @PostMapping("/template/build")
    public CouponTemplate buildTemplate(@RequestBody TemplateRequest request)
            throws CouponException {
        log.info("Build Template: {}", JSON.toJSONString(request));
        return buildTemplateService.buildTemplate(request);
    }

    /**
     * 根据id获取优惠券详情
     * 127.0.0.1:7001/distribution-template/template/info
     *
     * @param id
     * @return
     * @throws CouponException
     */
    @GetMapping("/template/info")
    public CouponTemplate buildTemplateInfo(@RequestParam("id") Integer id)
            throws CouponException {
        log.info("Build Template Info Is id :{}", id);
        return templateBaseService.buildTemplateInfo(id);
    }

    /**
     * 查询所有可用优惠券模板
     * 127.0.0.1:7001/distribution-template/template/sdk/all
     *
     * @return
     */
    @GetMapping("/template/sdk/all")
    public List<CouponTemplateSDK> findAllUsableTemplate() {
        log.info("Find All Usable Template.");
        return templateBaseService.findAllUsableTemplate();
    }

    /**
     * 获取模板 ids 到 CouponTemplateSDK的映射
     * 127.0.0.1:7001/distribution-template/template/sdk/infos
     *
     * @param ids
     * @return
     */
    @GetMapping("/template/sdk/infos")
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(
            @RequestParam("ids") Collection<Integer> ids) {
        log.info("findIds2TemplateSDK: {}", JSON.toJSONString(ids));
        return templateBaseService.findIds2TemplateSDK(ids);
    }
```



### 编写测试用例

**测试用例是对功能服务（也就是 service 的实现）进行校验的一种方法。SpringBoot 已经把测试用例的实现过程封装的非常简单了，而且我们通常都会定义一个 contextLoad 来检验当前的测试环境是否可用。**

```java
/**
 * <h1>模板系统测试程序</h1>
 * Created by Qinyi.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TemplateApplicationTests {

    @Test
    public void contextLoad() {

    }
}
```

​          

## 七.分发微服务

***这一章开始去完成第二个微服务模块-分发微服务。这一章的内容主要包含：创建新模块、实体类即 Dao 接口定义、功能服务接口定义。***

### 创建分发模块

**分发模块属于优惠券系统中的功能微服务类别（另一类是 SpringCloud 基础组件），所以，把它置于 imooc-coupon-service 父模块的目录中。创建模块的步骤非常简单：**

- **新建 Maven 工程**
- **修改 pom.xml 文件，添加对应的依赖项**
- **编写应用启动类：DistributionApplication**

```java
/**
 * <h1>分发系统的启动入口</h1>
 * Created by Qinyi.
 */
@EnableJpaAuditing      // 开启 Jpa 的审计功能
@EnableFeignClients     // 开启 Feign, 允许应用访问其他的微服务
@EnableCircuitBreaker   // 开启断路器
@EnableEurekaClient     // 标识当前的应用是 Eureka Client, 即需要向 Eureka Server 去注册
@SpringBootApplication  // 标识是 SpringBoot 应用
public class DistributionApplication {

    @Bean
    @LoadBalanced   // 开启负载均衡
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(DistributionApplication.class, args);
    }
}
```



### 编写实体类（自定义序列化）和 Dao 接口定义

***实体类直接映射到数据表 coupon，需要特别注意这几个注解（一定要去理解这些注解的功能）：***

- **@Entity：标识当前的类定义是实体类**
- **@EntityListeners(AuditingEntityListener.class)：实体类监听器，由 Jpa 审计功能实现对属性的填充**
- **@Table(name = “coupon”)：指定实体类映射数据表的一些属性**
- **@JsonSerialize(using = CouponSerialize.class)：自定义 Jackson 序列化的实现**
- **@CreatedDate：创建时间，由 Jpa 审计功能填充**
- **@Transient：不被包含在数据表列的范围**
- **@Convert：数据表列与 Java 属性之间的转换规则定义**



### 功能服务接口的定义

***功能服务接口即对外提供服务的接口，但是在分发微服务中有两个接口的定义比较特殊：IRedisService、IKafkaService。它们都属于工具接口，服务于 IUserService。对于这三个功能接口，需要注意：***

- **IRedisService 中的保存无效优惠券接口的目的是避免缓存穿透**
- **IKafkaService 不被任何服务调用，它是 Kafka 的消费者，由 Kafka（框架）去管理并调用**
- **IUserService 中的 SettlementInfo 与将来要实现的结算微服务是通用的，所以，需要定义在共用的地方，即 coupon-common 中**

***这一章里，我们完成了  Redis、Kafka 的相关功能（可以理解为用户服务的工具类），以及用户服务功能实现。Redis、Kafka 在 SpringBoot  中的使用方法需要大家认真学习并理解，另外，微服务之间的调用方式与兜底策略也是需要重点掌握和理解的。***



### Redis 在 SpringBoot 中的应用

**我在分发服务中使用的 Redis 客户端都是  StringRedisTemplate，它是最常用的 Redis 客户端，用于存取 key 和 value 都是字符串类型的数据。默认采用  String 的序列化策略（StringRedisSerializer），可以查看其源码如下：**

```java
public class StringRedisTemplate extends RedisTemplate<String, String> {

    /**
     * Constructs a new <code>StringRedisTemplate</code> instance. {@link #setConnectionFactory(RedisConnectionFactory)}
     * and {@link #afterPropertiesSet()} still need to be called.
     */
    public StringRedisTemplate() {
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        setKeySerializer(stringSerializer);
        setValueSerializer(stringSerializer);
        setHashKeySerializer(stringSerializer);
        setHashValueSerializer(stringSerializer);
    }

  ...
}
```

**这里一定需要注意，不要去使用  RedisTemplate。因为在使用 RedisTemplate 时，通常需要自己去指定 key 和 value  的序列化器。如果没有指定，则使用默认的 JdkSerializationRedisSerializer（JDK  的序列化策略），这往往会使你得到不一样的数据缓存。可以查看其源码如下：**

```java
public class RedisTemplate<K, V> extends RedisAccessor implements RedisOperations<K, V>, BeanClassLoaderAware {

    ...

    @SuppressWarnings("rawtypes") private @Nullable RedisSerializer keySerializer = null;
    @SuppressWarnings("rawtypes") private @Nullable RedisSerializer valueSerializer = null;
    @SuppressWarnings("rawtypes") private @Nullable RedisSerializer hashKeySerializer = null;
    @SuppressWarnings("rawtypes") private @Nullable RedisSerializer hashValueSerializer = null;
    private RedisSerializer<String> stringSerializer = new StringRedisSerializer();

  ...

    @Override
    public void afterPropertiesSet() {

        super.afterPropertiesSet();

        boolean defaultUsed = false;

        if (defaultSerializer == null) {

            defaultSerializer = new JdkSerializationRedisSerializer(
                    classLoader != null ? classLoader : this.getClass().getClassLoader());
        }

        ...

        initialized = true;
}
```

**关于 Redis 在 SpringBoot 中的应用，我这里做几点简单的总结：**

- **操作 Redis 的数据结构时，使用 opsForXXX，这里的 XXX 就对应到 Redis 提供的数据结构，表达的意思是对 XXX 数据结构进行操作**
- **当需要存取的数据是字符串类型时（大多数情况下），使用 StringRedisTemplate**
- **当需要存取的数据是 Java Object 时，使用 RedisTemplate（需要定义好序列化策略）**
- **不需要使用 RedisConnection**



### Kafka 在 SpringBoot 中的应用

**Kafka  的概念是非常复杂的，包含很多名词（例如：broker、topic、partition、producer、consumer），且都需要大家去理解掌握。但是，spring-kafka  将对 Kafka 的使用封装的十分简单。发送消息即 send、消费消息即 KafkaListener。下面，我来总结下在 SpringBoot  中使用 Kafka 的三个步骤：**

- **Maven 依赖和程序配置**

```xml
<!-- 需要注意 pom 版本要适应 Kafka 的版本 -->
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
    <version>2.2.0.RELEASE</version>
</dependency>
spring:
  kafka:
    # broker 列表，数组结构
    bootstrap-servers:
      - 127.0.0.1:9092
    consumer:
      # 消费者默认的 group id
      group-id: imooc-coupon-x
      # 消费者默认的消费策略，这里指的是从最新的位置开始消费，即不管之前的消息
      auto-offset-reset: latest
```

- **发送 Kafka 消息**

**首先，需要注入  KafkaTemplate<String, String>，两个泛型分别是 key 和 value 的类型。key 用于做分区的  hash，value 则是 kafka 消息。之后，就可以通过 send 方法发送消息。send  有很多重载方法，最简单的一种（也是课程中所使用的）如下所示：**

```java
public ListenableFuture<SendResult<K, V>> send(String topic, @Nullable V data) {
	ProducerRecord<K, V> producerRecord = new ProducerRecord<>(topic, data);
	return doSend(producerRecord);
}
```

**对于这个 send 方法，可以发现：**

- **key 并不需要传递，即为 null，代表使用默认的消息分区策略**
- **是个异步方法，因为返回 Future 对象**
- **消费 Kafka 消息**

**消费 Kafka 消息，只需要在相应的处理方法上标注 @KafkaListener 注解即可，并在注解中指定相关的属性。注解源码解释如下：**

```java
public @interface KafkaListener {

  ......

  // 消费的 topic 数组
	String[] topics() default {};

	// 消费的 topic 正则表达式
	String topicPattern() default "";

	// 消费指定 topic 的 partition
	TopicPartition[] topicPartitions() default {};

  ......

	// 指定 group id，如果不指定，则使用 application 中配置的
	String groupId() default "";

  ......
}
```



### 微服务之间的调用方式

**SpringCloud 提供了 Ribbon 和 Feign 两个组件用于微服务之间的调用，且使用 Rest（HTTP）的调用方式。另外，Feign 可以结合 Hystrix 来实现调用出现问题时的兜底熔断。下面，我对这三个组件做总结。**

- **Ribbon**

**Ribbon 是 Netflix  发布的开源项目，主要功能是提供客户端的软件负载均衡算法。Ribbon  客户端组件提供一系列完善的配置项如连接超时，重试等。简单的说，就是在配置文件中列出 Load Balancer 后面所有的机器，Ribbon  会自动的帮助你基于某种规则（如简单轮询，随机连接等）去连接这些机器。我们也很容易使用 Ribbon  实现自定义的负载均衡算法。简单地说，Ribbon 是一个客户端负载均衡器。**

**Ribbon 工作时分为两步：**

- **第一步先选择 Eureka Server, 它优先选择在同一个 Zone 且负载较少的 Server**
- **第二步再根据用户指定的策略，从 Server 取到的服务注册列表中选择一个地址**
- **Feign**

**Feign 是一个声明式的 web  service 客户端，它使得编写 web service 客户端更为容易。创建接口，为接口添加注解，即可使用 Feign。Feign 可以使用  Feign 注解或者 JAX-RS 注解，还支持热插拔的编码器和解码器。Spring Cloud 为 Feign 添加了 Spring MVC  的注解支持，并整合了 Ribbon 和 Eureka 来为使用 Feign 时提供负载均衡。**

**课程中介绍了使用 Feign 组件实现微服务调用的方法，例如，调用模板微服务：**

```java
/**
 * <h1>优惠券模板微服务 Feign 接口定义</h1>
 * Created by Qinyi.
 */
 // 1. 使用 FeignClient 标注当前定义一个 Feign 客户端
 // 2. value 声明调用的微服务应用名
 // 3. fallback 定义降级策略（Hystrix）
@FeignClient(value = "eureka-client-coupon-template",
        fallback = TemplateClientHystrix.class)
public interface TemplateClient {

    /**
     * <h2>查找所有可用的优惠券模板</h2>
     * */
     // RequestMapping 就是 SpringMVC 中用于指定 url 和 method 的注解
    @RequestMapping(value = "/coupon-template/template/sdk/all",
            method = RequestMethod.GET)
    CommonResponse<List<CouponTemplateSDK>> findAllUsableTemplate();

    /**
     * <h2>获取模板 ids 到 CouponTemplateSDK 的映射</h2>
     * */
    @RequestMapping(value = "/coupon-template/template/sdk/infos",
            method = RequestMethod.GET)
    CommonResponse<Map<Integer, CouponTemplateSDK>> findIds2TemplateSDK(
            @RequestParam("ids") Collection<Integer> ids
    );
}
```

- **Hystrix**

**Hystrix  熔断器，容错管理工具，旨在通过熔断机制控制服务和第三方库的节点，从而对延迟和故障提供更强大的容错能力。在 Spring Cloud  Hystrix 中实现了线程隔离、断路器等一系列的服务保护功能。它也是基于 Netflix 的开源框架 Hystrix  实现的，该框架目标在于通过控制那些访问远程系统、服务和第三方库的节点，从而对延迟和故障提供更强大的容错能力。Hystrix  具备了服务降级、服务熔断、线程隔离、请求缓存、请求合并以及服务监控等强大功能。**

**课程中同样使用了 Hystrix 来实现微服务的熔断降级，例如，对于模板微服务：**

```java
/**
 * <h1>优惠券模板 Feign 接口的熔断降级策略</h1>
 * Created by Qinyi.
 */
 // Hystrix 中需要实现 Feign Client 中定义的所有方法（方法签名一定要保证完全一致），所以，最简单的办法就是实现 Feign 接口
@Slf4j
@Component
public class TemplateClientHystrix implements TemplateClient {

    /**
     * <h2>查找所有可用的优惠券模板</h2>
     */
     // 自定义熔断降级策略，这里只做了两件事：
     // 1. 打印错误日志，记录调用异常
     // 2. 返回兜底数据，空列表
    @Override
    public CommonResponse<List<CouponTemplateSDK>> findAllUsableTemplate() {

        log.error("[eureka-client-coupon-template] findAllUsableTemplate " +
                "request error");
        return new CommonResponse<>(
                -1,
                "[eureka-client-coupon-template] request error",
                Collections.emptyList()
        );
    }

    /**
     * <h2>获取模板 ids 到 CouponTemplateSDK 的映射</h2>
     * @param ids 优惠券模板 id
     */
    @Override
    public CommonResponse<Map<Integer, CouponTemplateSDK>>
    findIds2TemplateSDK(Collection<Integer> ids) {

        log.error("[eureka-client-coupon-template] findIds2TemplateSDK" +
                "request error");

        return new CommonResponse<>(
                -1,
                "[eureka-client-coupon-template] request error",
                new HashMap<>()
        );
    }
}
```

***到目前为止，已经介绍了 SpringCloud 最常用、最核心的几个组件，一定要理解这几个组件，且能够用自己的语言表达清楚它们的基本思想与使用方法***



### 1.0优惠券分发模块功能微服务接口实现

#### RedisService相关操作实现

**根据userId和缓存状态找到缓存的优惠券数据 :**

因为Redis中没有记录,说明MySQL没有该用户信息,所以不用查询数据库,直接在Redis中返回null(无效优惠券信息)

```
/**
 * 根据userId和缓存状态找到缓存的优惠券数据
 * @param userId 用户Id
 * @param status 缓存状态 {@link com.imooc.distribution.constant.CouponStatus}
 * @return {@link Coupon} 注意,可能返回null,代表从没有过记录
 */
@Override
public List<Coupon> getCacheCoupons(Long userId, Integer status) {
    log.info("Get Coupons From Cache: {}, {}", userId, status);
    String redisKey = status2RedisKey(status, userId);
    List<String> values = redisTemplate.opsForHash().values(redisKey) //List<Object>
            .stream() //Stream<Object>
            .map(o -> Objects.toString(o, null)) //Stream(String)
            .collect(Collectors.toList());
    if (CollectionUtils.isEmpty(values)) {
        saveEmptyCouponListToCache(userId,
                Collections.singletonList(status));
        return Collections.emptyList();
    }
    return values.stream().map(v -> JSON.parseObject(v, Coupon.class))
            .collect(Collectors.toList());
}
```



**保存空的有优惠券列表到缓存中:** 

  **目的:**避免缓存穿透  (查询Redis中数据为null,再去进行查询一次MySQL再一次为null)

使用SessionCallback操作pipeline, 把数据命令放入Redis 的pipeline
 pipeline(**可以让我们一次性执行多个命令,命令执行完后一次性全部返回给我们,Redis是单线程,执行一条返回一条**)

```
/**
 * 保存空的优惠券列表到缓存中  (避免缓存穿透)
 * 目的: 避免缓存穿透
 *
 * @param userId 用户id
 * @param status 优惠券状态列表
 */
@Override
@SuppressWarnings("all")
public void saveEmptyCouponListToCache(Long userId, List<Integer> status) {
    log.info("Save Empty List to Cache For User: {}, Status: {}",
            userId, JSON.toJSONString(status));

    //key 是Coupon_id , value是序列化的Coupon
    Map<String, String> invalidCouponMap = new HashMap<>();
    invalidCouponMap.put("-1", JSON.toJSONString(Coupon.invalidCoupon()));

    //用户优惠券缓存信息
    //KV
    //K: status -> redisKey
    //V: {coupon_id, 序列化的 coupon}


    //使用SessionCallback 把数据命令放入Redis 的pipeline
    //pipeline(可以让我们一次性执行多个命令,命令执行完后一次性全部返回给我们,Redis是单线程,执行一条返回一条)
    SessionCallback sessionCallback = new SessionCallback() {
        @Override
        public Object execute(RedisOperations redisOperations) throws DataAccessException {
            status.forEach(s -> {
                String redisKey = status2RedisKey(s, userId);
                redisOperations.opsForHash().putAll(redisKey, invalidCouponMap);
            });
            return null;
        }
    };
    log.info("Pipeline Exe Result: {}",
            JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
}
```



**尝试从Cache中获取优惠券码:**    优惠券码的组成 :

**Constant.RedisPrefix.COUPON_TEMPLATE(枚举类)+ templateId(优惠券模板Id)**

```
  /**
     * 尝试从Cache中获取优惠券码
     *
     * @param templateId 优惠券模板主键
     * @return 优惠券码
     */
    @Override
    public String tryToAcquireCouponCodeFromCache(Integer templateId) {
        String redisKey = String.format("%s%s",
                Constant.RedisPrefix.COUPON_TEMPLATE, templateId.toString());
        //优惠券码不存在顺序关系,left或者right没有关系
        String couponCode = redisTemplate.opsForList().leftPop(redisKey);
        log.info("Acquire Coupon code: {},{},{}", templateId, redisKey, couponCode);
        return couponCode;
    }
```



**把优惠券保存在Cache中:**  

1.首先需要判断优惠券的状态  判断传进来的优惠券是  1.可用(USABLE)   2.已使用(USED)  3.过期(EXPIRE)

2.如果是可用状态 : 只会影响一个Cache  直接保存到 USABLE   **设置Reids中的Key过期时间**

3.如果是已使用状态 : 会影响两个Cache  USABLE   USED :

**首先** 获取可用的优惠券个数  和  Coupon(用户领取的优惠券记录)个数进行比较

(Redis中的优惠券个数一定大于 1  因为 saveEmptyCouponListToCache 防止缓存穿透,在Redis中存了一个无效优惠券)

**然后** 检查当前优惠券个数是否与Cache中相匹配  可以用到 : 

 isSubCollection ( A , B )   A->B 子集

```
import org.apache.commons.collections4.CollectionUtils;
CollectionUtils.isSubCollection(A, B)
```

**最后**  使用 SessionCallback 操作 pipeline  

​    已使用的优惠券  添加到Cache中

​    可使用的的优惠券 清理

​    重置 key 的过期时间

4.如果是已过期状态,  同上

```
/**
 * 将优惠券保存到Cache中
 *
 * @param userId  用户id
 * @param coupons {@link Coupon}
 * @param status  优惠券状态
 * @return 保存成功个数
 */
@Override
public Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status)
        throws CouponException {
    log.info("Add Coupon TO Cache: {},{},{}",
            userId, JSON.toJSONString(coupons), status);
    Integer result = -1;
    CouponStatus couponStatus = CouponStatus.of(status);
    switch (couponStatus) {
        case USABLE:
            result = addCouponToCacheForUsable(userId, coupons);
            break;
        case USED:
            result = addCouponToCacheForUsed(userId, coupons);
            break;
        case EXPIRED:
            result = addCouponToCacheForExpire(userId, coupons);
            break;
    }
    return result;
}




 /**
     * 新增加优惠券到Cache中  USEABLE
     *
     * @param userId
     * @param coupons
     * @return
     */
    private Integer addCouponToCacheForUsable(Long userId, List<Coupon> coupons) {
        // 如果 status 是 USBALE, 代表是新增加的优惠券
        // 只会影响一个Cache ,USER_COUPON_USABLE
        log.debug("Add Coupon To Cache For Usable");
        Map<String, String> needCacheToUsable = new HashMap<>();
        coupons.forEach(coupon -> {
            needCacheToUsable.put(coupon.getId().toString(),
                    JSON.toJSONString(coupon));
        });
        String redisKey = String.format("%s%s",
                Constant.RedisPrefix.USER_COUPON_USABLE, userId);
        redisTemplate.opsForHash().putAll(redisKey, needCacheToUsable);
        log.info("Add {} Coupon To Cache :{},{}",
                needCacheToUsable.size(), userId, redisKey);

        //设置过期时间
        redisTemplate.expire(
                redisKey,
                getRandExpirationTime(1, 2),
                TimeUnit.SECONDS
        );
        return needCacheToUsable.size();
    }



    /**
     * 将已使用的优惠券加入到Cache中  USED
     *
     * @param userId
     * @param coupons
     * @return
     */
    @SuppressWarnings("all")
    private Integer addCouponToCacheForUsed(Long userId, List<Coupon> coupons) throws CouponException {
        //如果status 是USED, 代表用户操作是使用当前的优惠券,影响到两个Cache
        //USED , USABLE
        Map<String, String> needCacheFOrUsed = new HashMap<>(coupons.size());

        String redisKeyUsable = status2RedisKey(CouponStatus.USABLE.getCode(), userId);
        String redisKeyUsed = status2RedisKey(CouponStatus.USED.getCode(), userId);

        //获取当前用户可用的优惠券
        List<Coupon> usableCache = getCacheCoupons(userId, CouponStatus.USABLE.getCode());
        //当前可用的优惠券个数一个大于1  (因为之前没有优惠券信息我们会塞入一个无效优惠券)
        assert usableCache.size() > coupons.size();

        coupons.forEach(coupon -> {
            needCacheFOrUsed.put(
                    coupon.getId().toString(),
                    JSON.toJSONString(coupon)
            );
        });

        //校验当前优惠券中的参数是否与Cache中相匹配
        List<Integer> usableIds = usableCache.stream().map(Coupon::getId)
                .collect(Collectors.toList());
        List<Integer> paramIds = coupons.stream().map(Coupon::getId)
                .collect(Collectors.toList());
        if (!CollectionUtils.isSubCollection(paramIds, usableIds)) { //isSubCollection(A,B) A->B 子集
            log.error("CurCoupon Is Not Equal ToCache:{},{},{}",
                    userId,
                    JSON.toJSONString(usableIds),
                    JSON.toJSONString(paramIds));
            throw new CouponException("CurCoupon Is Not Equal ToCache!");
        }

        List<String> needCleanKey = paramIds.stream().map(e -> e.toString())
                .collect(Collectors.toList());
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                //1.已使用的优惠券 Cache 缓存添加
                redisOperations.opsForHash().putAll(
                        redisKeyUsed, needCacheFOrUsed
                );
                //2.可用的优惠券 Cache 缓存清理
                redisOperations.opsForHash().delete(
                        redisKeyUsable, usableCache.toArray());
                //3.重置过期时间
                redisOperations.expire(
                        redisKeyUsable,
                        getRandExpirationTime(1, 2),
                        TimeUnit.SECONDS
                );
                redisOperations.expire(
                        redisKeyUsed,
                        getRandExpirationTime(1, 2),
                        TimeUnit.SECONDS
                );
                return null;
            }
        };
        log.info("Pipeline Exe Result:{}",
                JSON.toJSONString(sessionCallback));
        return coupons.size();
    }


    /**
     * 将已过期的优惠券加入 Cache 中 Expire
     *
     * @param userId
     * @param coupons
     * @return
     */
    @SuppressWarnings("all")
    private Integer addCouponToCacheForExpire(Long userId, List<Coupon> coupons)
            throws CouponException {
        //如果status 是Expire, 代表优惠券已过期
        //影响两个Cache USABLE , EXPIRE
        Map<String, String> needCacheForExpire = new HashMap<>(coupons.size());

        String redisUsable = status2RedisKey(
                CouponStatus.USABLE.getCode(), userId
        );
        String redisExpire = status2RedisKey(
                CouponStatus.EXPIRED.getCode(), userId
        );

        List<Coupon> usableCache = getCacheCoupons(
                userId, CouponStatus.USABLE.getCode()
        );

        //当前可用优惠券一定大于Coupon
        assert usableCache.size() > coupons.size();

        coupons.forEach(e -> {
            needCacheForExpire.put(
                    e.getId().toString(),
                    JSON.toJSONString(e)
            );
        });
        //校验当前优惠券参数是否与Cache中相匹配
        List<Integer> usableIds = usableCache.stream()
                .map(Coupon::getId).collect(Collectors.toList());

        List<Integer> paramIds = coupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());

        if (!CollectionUtils.isSubCollection(paramIds, usableIds)) {
            log.error("Coupon is not Equal ToCache:{},{},{}",
                    userId,
                    JSON.toJSONString(paramIds),
                    JSON.toJSONString(usableIds));
            throw new CouponException("Coupon is not ToCache");
        }

        List<String> needCleanKey = paramIds.stream()
                .map(e -> e.toString()).collect(Collectors.toList());

        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                //1.已过期的优惠券 Expire 添加
                redisOperations.opsForHash().putAll(redisExpire, needCacheForExpire);

                //2.可用优惠券 Usable 清理
                redisOperations.opsForHash().delete(redisUsable, needCleanKey);

                //3.重置过期时间
                redisOperations.expire(
                        redisExpire,
                        getRandExpirationTime(1, 2),
                        TimeUnit.SECONDS
                );
                redisOperations.expire(
                        redisUsable,
                        getRandExpirationTime(1, 2),
                        TimeUnit.SECONDS
                );
                return null;
            }
        };
        log.info("Pipeline Exe Result: {}",
                redisTemplate.executePipelined(sessionCallback));

        return coupons.size();
    }
```



**根据 status 和 userId 获取RedsKey : **

```
/**
 * 根据status获取RedisKey
 *
 * @param status
 * @param userId
 * @return
 */
private String status2RedisKey(Integer status, Long userId) {
    String redisKey = null;
    CouponStatus couponStatus = CouponStatus.of(status);
    switch (couponStatus) {
        case USABLE:
            redisKey = String.format("%s%S",
                    Constant.RedisPrefix.USER_COUPON_USABLE, userId);
            break;
        case USED:
            redisKey = String.format("%s%s",
                    Constant.RedisPrefix.USER_COUPON_USED, userId);
            break;
        case EXPIRED:
            redisKey = String.format("%s%s",
                    Constant.RedisPrefix.USER_COUPON_EXPIRED, userId);
    }
    return redisKey;
}
```



**设置Redis中的Key的随机过期时间: 防止服务雪崩:**

```
/**
 * 获取一个随机的过期时间
 * 缓存雪崩: key 在同一时间失效
 *
 * @param min 最小小时数
 * @param max 最大小时数
 * @return 返回[min, max] 之间的随机秒数
 */
private Long getRandExpirationTime(Integer min, Integer max) {
    return RandomUtils.nextLong(
            min * 60 * 60,
            max * 60 * 60
    );
}
```

#### KafkaService 操作实现

1.@KafkaListener  由Kafka监听消息  有消息传进来序列化成 **ConsumerRecord<?, ?> record**

2.在 @KafkaListener 此方法下进行逻辑性的消费`

```
/*Coupon Dao*/
private final CouponDao couponDao;

@Autowired
public KafkaServiceImple(CouponDao couponDao) {
    this.couponDao = couponDao;
}

/**
 * 消费优惠券Kafka信息
 *
 * @param record {@link ConsumerRecord}
 */
@Override
@KafkaListener(topics = {Constant.TOPIC}, groupId = "imooc-coupon-1")
public void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record) {
    //可以为空
    Optional<?> kafkaOption = Optional.ofNullable(record.value());
    //如果存在消息
    if (kafkaOption.isPresent()) {
        Object message = kafkaOption.get();
        CouponKafkaMessage couponInfo = JSON.parseObject(
                message.toString(), CouponKafkaMessage.class
        );
        log.info("Receive CouponKafkaMessage: {}", message.toString());

        CouponStatus status = CouponStatus.of(couponInfo.getStatus());
        switch (status) {
            case USABLE:
                break;
            case USED:
                processUsedCoupons(couponInfo, status);
                break;
            case EXPIRED:
                processExpireCoupons(couponInfo, status);
                break;
        }
    }
}

/**
 * 处理已使用的优惠券
 *
 * @param kafkaMessage
 * @param status
 */
private void processUsedCoupons(CouponKafkaMessage kafkaMessage,
                                CouponStatus status) {
    //可以根据优惠券的类型对用户进行不同的操作
    //TODO 给用户发送短信
    processCouponByStatus(kafkaMessage, status);
}

/**
 * 处理已过期的优惠券
 *
 * @param kafkaMessage
 * @param status
 */
private void processExpireCoupons(CouponKafkaMessage kafkaMessage,
                                  CouponStatus status) {
    //TODO 给用户推送消息
    processCouponByStatus(kafkaMessage, status);
}


/**
 * 根据状态处理优惠券信息
 *
 * @param kafkaMessage
 * @param status
 */
private void processCouponByStatus(CouponKafkaMessage kafkaMessage,
                                   CouponStatus status) {
    List<Coupon> coupons = couponDao.findAllById(kafkaMessage.getIds());
    if (CollectionUtils.isEmpty(coupons)
            || coupons.size() != kafkaMessage.getIds().size()) {
        log.error("Can not find Right Coupon Info: {}",
                JSON.toJSONString(kafkaMessage));
        //TODO 发送邮件
        return;
    }
    coupons.forEach(c -> c.setStatus(status));
    log.info("CouponKafkaMessage Op Coupon Count: {}",
            couponDao.saveAll(coupons).size());
}
```

#### Feign声明式调用和Hystrix兜底回退



```
/**
 * 优惠券模板微服务 feign 接口定义
 */
@FeignClient(value = "eureka-client-coupon-template",
        fallback = TemplateClientHystrix.class)
public interface TemplateClient {

    /**
     * 查找所有可用的优惠券模板
     *
     * @return
     */
    @RequestMapping(value = "/coupon-template/template/sdk/all",
            method = RequestMethod.GET)
    CommonResponse<List<CouponTemplateSDK>> findAllUsableTemplate();

    /**
     * 获取优惠券模板 ids 到CouponTemplateSDK的映射
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/coupon-template/template/sdk/infos",
            method = RequestMethod.GET)
    CommonResponse<Map<Integer, CouponTemplateSDK>> findIds2TemplateSDK(
            @RequestParam("ids") Collection<Integer> ids);
}
```



```
**
 * 优惠券模板 Feign 熔断降级策略
 */

@Slf4j
@Component
public class TemplateClientHystrix implements TemplateClient {

    /**
     * 查找所有了用的优惠券模板
     *
     * @return
     */
    @Override
    public CommonResponse<List<CouponTemplateSDK>> findAllUsableTemplate() {
        log.error("[eureka-client-coupon-template] findAllUsableTemplate " +
                "request error");
        return new CommonResponse<>(
                -1,
                "[eureka-client-coupon-template] request error",
                Collections.emptyList()
        );
    }

    /**
     * 获取模板 ids 到CouponTemplateSDK的映射
     *
     * @param ids
     * @return
     */
    @Override
    public CommonResponse<Map<Integer, CouponTemplateSDK>> findIds2TemplateSDK(Collection<Integer> ids) {
        log.error("[eureka-client-coupon-template] findIds2TemplateSDK " +
                "request error");
        return new CommonResponse<>(
                -1,
                "[eureka-client-coupon-template] request error",
                new HashMap<>()
        );
    }
}
```



#### 用户服务相关操作实现

**根据用户 id 和状态查询优惠券记录:**

首先从Redis中查询List<Coupon> 优惠券记录

1.如果Redis存在 ,进行下一步  ,  如果Redis中不存在, 查询MySQL 

下一步:  将无效优惠券进行分类 , 如果当前获得的是可用优惠券,需要对过期优惠券的延迟处理处理

发送到kafka延时处理

```
/*Coupon Dao*/
private final CouponDao couponDao;
/*Redis 服务*/
private final RedisService redisService;
/*kafka 服务*/
@Autowired
private final KafkaTemplate kafkaTemplate;
/*模板微服务客户端*/
private final TemplateClient templateClient;
/*结算微服务客户端*/
private final SettlementClient settlementClient;

@Autowired
public UserServiceImple(CouponDao couponDao, RedisService redisService, KafkaTemplate kafkaTemplate, TemplateClient templateClient, SettlementClient settlementClient) {
    this.couponDao = couponDao;
    this.redisService = redisService;
    this.kafkaTemplate = kafkaTemplate;
    this.templateClient = templateClient;
    this.settlementClient = settlementClient;
}

/**
 * 根据用户 id 和状态查询优惠券记录
 *
 * @param userId 用户id
 * @param status 优惠券状态
 * @return {@link Coupon} s
 */
@Override
public List<Coupon> findCouponsByStatus(Long userId, Integer status)
        throws CouponException {

    List<Coupon> curCache = redisService.getCacheCoupons(userId, status);
    List<Coupon> preTarget;

    if (CollectionUtils.isNotEmpty(curCache)) {
        log.debug("coupon cache is not empty: {},{} ", userId, status);
        preTarget = curCache;
    } else {
        log.debug("coupon cache is empty, get coupon from db: {},{}",
                userId, status);
        List<Coupon> dbCoupons = couponDao.findByUserIdAndStatus(
                userId, CouponStatus.of(status)
        );
        //如果数据库中没有记录,直接返回就可以了,Cache中已经加入了一个无效优惠券
        if (CollectionUtils.isEmpty(dbCoupons)) {
            log.debug("coupon is empty from db: {},{}",
                    userId, status);
            return dbCoupons;
        }

        //填充dbCoupon中的 templateSDK字段
        Map<Integer, CouponTemplateSDK> id2Template =
                templateClient.findIds2TemplateSDK(
                        dbCoupons.stream().map(Coupon::getId)
                                .collect(Collectors.toList())
                ).getData();
        dbCoupons.forEach(e -> e.setTemplateSDK(
                id2Template.get(e.getTemplateId()))
        );
        //数据库中存在记录
        preTarget = dbCoupons;
        //将记录写入Cache
        redisService.addCouponToCache(userId, preTarget, status);
    }

    //将无效优惠券剔除
    preTarget = preTarget.stream()
            .filter(c -> c.getId() != -1)
            .collect(Collectors.toList());
    //如果当前获得的是可用优惠券,需要对过期优惠券的延迟处理处理
    if (CouponStatus.of(status) ==CouponStatus.USABLE){
        CouponClassify classify=CouponClassify.classify(preTarget);
        //如果已过期不为空,需要延迟处理
        if(CollectionUtils.isNotEmpty(classify.getExpired())){
            log.info("Add Expired Coupon to Cache findCouponByStatus:{},{}",
                    userId,status);
            redisService.addCouponToCache(
                    userId,classify.getExpired(),
                    CouponStatus.EXPIRED.getCode());
            //发送到kafka做异步处理
            kafkaTemplate.send(
                    Constant.TOPIC,
                    JSON.toJSONString(new CouponKafkaMessage(
                            CouponStatus.EXPIRED.getCode(),
                            classify.getExpired().stream()
                            .map(Coupon::getId).collect(Collectors.toList())
                    ))
                    );
        }
        return classify.getUsable();
    }
    return preTarget;
}
```



**根据用户id 查找当前可以领取的优惠券模板:**

首先: 获取所有可用的优惠券模板 , 然后过滤过期的优惠券模板(因为设置的定时任务是延迟过期策略)

然后再查询用户领取的优惠券记录, 将用户领取的优惠券记录进行分组,

根据<TemplateId,List<Coupon>(领取次数)>  

和优惠券模板规则 Rule中的limitation进行判断

```
/**
 * 根据用户id 查找当前可以领取的优惠券模板
 *
 * @param userId 用户id
 * @return {@link CouponTemplateSDK}
 */
@Override
public List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException {

    Long curTime = new Date().getTime();
    List<CouponTemplateSDK> templateSDKS =
            templateClient.findAllUsableTemplate().getData();

    log.debug("Find All Template (From TemplateClient) count: {}", templateSDKS.size());

    //过滤过期的优惠券模板  //因为设置的定时任务是延迟过期策略
    templateSDKS = templateSDKS.stream().filter(
            t -> t.getRule().getExpiration().getDeadLine() > curTime
    ).collect(Collectors.toList());

    log.info("Find Usable Template Count:{}", templateSDKS.size());

    //key 是 TemplateId
    //value 中 left是 Template limitation ,right是优惠券模板
    Map<Integer, Pair<Integer, CouponTemplateSDK>> limit2Template =
            new HashMap<>(templateSDKS.size());
    templateSDKS.forEach(t ->
            limit2Template.put(t.getId(),
                    Pair.of(t.getRule().getLimitation(), t)
            )
    );

    List<CouponTemplateSDK> result =
            new ArrayList<>(limit2Template.size());
    List<Coupon> userUsableCoupons =
            findCouponsByStatus(userId, CouponStatus.USABLE.getCode());

    log.debug("Current User has Usable Coupons:{}", userId,
            userUsableCoupons.size());

    //key是templateId
    Map<Integer, List<Coupon>> template2IdCoupons = userUsableCoupons
            .stream().collect(Collectors.groupingBy(Coupon::getTemplateId));

    //根据Template 的 Rule判断是否可用领取优惠券模板
    limit2Template.forEach((k, v) -> {
        int limitation = v.getLeft();
        CouponTemplateSDK templateSDK = v.getRight();

        if (template2IdCoupons.containsKey(k)
                && template2IdCoupons.get(k).size() >= limitation) {
            return;
        }
        result.add(templateSDK);
    });

    return result;
}
```



**用户领取优惠券:**

首先从TemplateClient拿到对应的优惠券模板, 并检查是否过期,

根据TemplateRule中的 limitation 判断用户可以领取优惠券

尝试去获取优惠券码

save to db

填充CouponTemplateSDK

save to Cache

```
/**
 * 用户领取优惠券
 * 1.从TemplateClient拿到对应的优惠券模板, 并检查是否过期
 * 2.根据limitation 判断用户可用领取
 * 3.save to db
 * 4.填充CouponTemplateSDK
 * 5.save to Cache
 *
 * @param request {@link AcquireTemplateRequest}
 * @return {@link Coupon}
 */
@Override
public Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException {
    Map<Integer, CouponTemplateSDK> id2Template =
            templateClient.findIds2TemplateSDK(
                    Collections.singletonList(request.getTemplateSDK().getId())
            ).getData();

    //判断优惠券模板是否存在
    if (id2Template.size() <= 0) {
        log.error("Can not Acquire Template From TemplateClient: {}",
                request.getTemplateSDK().getId());
        throw new CouponException("Can not Acquire Template From TemplateClient");
    }

    //用户是否可以领取这张优惠券
    List<Coupon> userUsableCoupons = findCouponsByStatus(
            request.getUserId(), CouponStatus.USABLE.getCode());
    Map<Integer, List<Coupon>> templateId2Coupons = userUsableCoupons.stream()
            .collect(Collectors.groupingBy(Coupon::getTemplateId));

    if (templateId2Coupons.containsKey(request.getTemplateSDK().getId())
            && templateId2Coupons.get(request.getTemplateSDK().getId()).size() >=
            request.getTemplateSDK().getRule().getLimitation()) {
        log.error("Exceed Template Assign Limitation: {}",
                request.getTemplateSDK().getId());
        throw new CouponException("Exceed Template Assign Limitation");
    }

    //尝试去获取优惠券码
    String couponCode = redisService.tryToAcquireCouponCodeFromCache(
            request.getTemplateSDK().getId()
    );
    if (StringUtils.isEmpty(couponCode)) {
        log.error("couponCode is empty:{}",
                request.getTemplateSDK().getId());
        throw new CouponException("couponCode is empty");
    }

    Coupon newCoupon = new Coupon(
            request.getTemplateSDK().getId(),
            request.getUserId(),
            couponCode,
            CouponStatus.USABLE
    );
    newCoupon = couponDao.save(newCoupon);

    //填充Coupon 对象的 TemplateSDK , 一定要注意在缓存之前放入
    newCoupon.setTemplateSDK(request.getTemplateSDK());

    //放入缓存中
    redisService.addCouponToCache(
            request.getUserId(),
            Collections.singletonList(newCoupon),
            CouponStatus.USABLE.getCode()
    );

    return newCoupon;
}
```



**结算(核销)优惠券**

if: 当没有传递优惠券时, 直接返回商品总价 , 

没有优惠券也就不存在优惠券核销,SettlementInfo 其他字段不需要修改

else:

校验传递的优惠券是否是自己的

通过结算微服务获取结算信息

更新缓存

通过kafka更新db

```
/**
 * 结算(核销)优惠券
 * 这里需要注意 ,规则相关处理需要由 Settlement 系统去做 ,当前系统仅仅做
 * 业务处理过程(校验过程)
 *
 * @param info {@link SettlementInfo}
 * @return {@link SettlementInfo}
 * @throws CouponException
 */
@Override
public SettlementInfo settlement(SettlementInfo info) throws CouponException {
    //当没有传递优惠券时, 直接返回商品总价
    List<SettlementInfo.CouponAndTemplateInfo> ctInfos =
            info.getCouponAndTemplateInfo();
    if (CollectionUtils.isEmpty(ctInfos)) {
        log.info("Empty Coupon for Settlement");
        double goodSum = 0.0;

        for (GoodInfo gi : info.getGoodInfos()) {
            goodSum += gi.getPrice() * gi.getCount();
        }

        //没有优惠券也就不存在优惠券核销,SettlementInfo 其他字段不需要修改
        info.setCost(retain2Decimals(goodSum));

    }

    //校验传递的优惠券是否是自己的
    List<Coupon> coupons = findCouponsByStatus(
            info.getUserId(), CouponStatus.USABLE.getCode()
    );
    Map<Integer, Coupon> id2Coupons = coupons.stream()
            .collect(Collectors.toMap(Coupon::getId, Function.identity()));

    if (MapUtils.isEmpty(id2Coupons) || !CollectionUtils.isSubCollection(
            ctInfos.stream().map(SettlementInfo.CouponAndTemplateInfo::getId)
                    .collect(Collectors.toList()), id2Coupons.keySet()
    )) {
        log.info("{}", id2Coupons.keySet());
        log.info("{}", ctInfos.stream().map(SettlementInfo.CouponAndTemplateInfo::getId)
                .collect(Collectors.toList()));
        log.error("User Coupon has some Problem " +
                ",It is Not SubCollection is Coupons!");
        throw new CouponException("User Coupon has some Problem " +
                ",It is Not SubCollection is Coupons!");
    }

    log.debug("Current Settlement Coupons Is User's:{}", ctInfos.size());

    List<Coupon> settleCoupons = new ArrayList<>(ctInfos.size());
    ctInfos.forEach(i -> settleCoupons.add(id2Coupons.get(i.getId())));

    //通过结算微服务获取结算信息
    SettlementInfo processedInfo =
            settlementClient.computeRule(info).getData();

    if (processedInfo.getEmploy() && CollectionUtils.isNotEmpty(
            processedInfo.getCouponAndTemplateInfo()
    )) {
        log.info("Settle User Coupon:{}", info.getUserId(),
                JSON.toJSONString(settleCoupons));
        //更新缓存
        redisService.addCouponToCache(
                info.getUserId(),
                settleCoupons,
                CouponStatus.USED.getCode()
        );
        //更新db
        kafkaTemplate.send(
                Constant.TOPIC,
                JSON.toJSONString(new CouponKafkaMessage(
                        CouponStatus.USED.getCode(),
                        settleCoupons.stream().map(Coupon::getId)
                                .collect(Collectors.toList())
                ))
        );
    }
    return processedInfo;
}

/**
 * 保留两位小数
 *
 * @param value
 * @return
 */
private double retain2Decimals(double value) {

    //BigDecimal.ROUND_HALF_UP 代表四舍五入
    return new BigDecimal(value)
            .setScale(2, BigDecimal.ROUND_HALF_UP)
            .doubleValue();
}
```



#### 优惠券分发模块Controller

```
/**
 * UserService Controller
 */

@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 根据用户id和优惠券状态查询优惠券记录
     *
     * @param userId
     * @param status
     * @return
     */
    @GetMapping("/coupons")
    public List<Coupon> findCouponsByStatus(
            @RequestParam(value = "userId") Long userId,
            @RequestParam(value = "status") Integer status) throws CouponException {
        log.info("Find Coupons By Status:{},{}", userId, status);
        return userService.findCouponsByStatus(userId, status);
    }

    /**
     * 根据用户Id查找可以领取的优惠券模板
     *
     * @param userId
     * @return
     * @throws CouponException
     */
    @GetMapping("/template")
    public List<CouponTemplateSDK> findAvailableTemplate(
            @RequestParam(value = "userId") Long userId) throws CouponException {
        log.info("Find Available Template:{}", userId);
        return userService.findAvailableTemplate(userId);
    }

    /**
     * 用户领取优惠券
     *
     * @param request
     * @return
     * @throws CouponException
     */
    @PostMapping("/acquire/template")
    public Coupon acquireTemplate(@RequestBody AcquireTemplateRequest request)
            throws CouponException {
        log.info("Acquire Template:{}", JSON.toJSONString(request));
        return userService.acquireTemplate(request);
    }

    /**
     * 结算(核销)优惠券
     *
     * @param info
     * @return
     * @throws CouponException
     */
    @PostMapping("/settlement")
    public SettlementInfo settlement(@RequestBody SettlementInfo info) throws CouponException {

        log.info("Settlement:{}", JSON.toJSONString(info));
        return userService.settlement(info);
    }
```



##  八.结算微服务

### 结算模块总结说明

***结算模块最核心的功能当然是实现对用户请求的逻辑计算（结算），用户的请求信息包含了商品信息和优惠券信息，我们要编写的结算规则执行器即对这两类信息进行逻辑计算。针对于课程中定义的所有优惠券类型，我这里都实现了对应的规则执行器。另外，我还实现了一个多种类优惠券组合在一起使用的规则执行器。把这里的执行器搞清楚了，也就清楚了这一章的核心思想。***

#### 优惠券模板规则处理器接口定义

**定义接口除了能对功能进行说明之外，还能够做多个实现。所以，首先，我定义了规则处理器接口。它要求将来实现的所有处理器都包含两个方法。**

```java
/**
 * <h1>优惠券模板规则处理器接口定义</h1>
 * Created by Qinyi.
 */
public interface RuleExecutor {

    /**
     * <h2>规则类型标记</h2>
     * @return {@link RuleFlag}
     * */
    RuleFlag ruleConfig();

    /**
     * <h2>优惠券规则的计算</h2>
     * @param settlement {@link SettlementInfo} 包含了选择的优惠券
     * @return {@link SettlementInfo} 修正过的结算信息
     * */
    SettlementInfo computeRule(SettlementInfo settlement);
}
```

#### 规则执行器抽象类

**将来要去实现多个规则执行器，那么，肯定在功能上会有重叠（因为它们的功能是非常相似的）。由此，我这里实现了一个抽象类，把通用功能代码写在里面。将来在实现具体的执行器时，就可以去继承自这个抽象类，自动拥有这些通用方法。**

```java
/**
 * <h1>规则执行器抽象类, 定义通用方法</h1>
 * Created by Qinyi.
 */
public abstract class AbstractExecutor {

    ......

    /**
     * <h2>商品总价</h2>
     * */
    protected double goodsCostSum(List<GoodsInfo> goodsInfos) {

        return goodsInfos.stream().mapToDouble(
                g -> g.getPrice() * g.getCount()
        ).sum();
    }

    /**
     * <h2>保留两位小数</h2>
     * */
    protected double retain2Decimals(double value) {

        return new BigDecimal(value).setScale(
                2, BigDecimal.ROUND_HALF_UP
        ).doubleValue();
    }

    /**
     * <h2>最小支付费用</h2>
     * */
    protected double minCost() {

        return 0.1;
    }
}
```

#### 结算规则执行器

**如之前所述，我这里实现了四个结算规则执行器，完整的类声明如下：**

- **com.imooc.coupon.executor.impl.ManJianExecutor：满减优惠券结算规则执行器**
- **com.imooc.coupon.executor.impl.ZheKouExecutor：折扣优惠券结算规则执行器**
- **com.imooc.coupon.executor.impl.LiJianExecutor：立减优惠券结算规则执行器**
- **com.imooc.coupon.executor.impl.ManJianZheKouExecutor：满减 + 折扣优惠券结算规则执行器**

#### 优惠券结算规则执行管理器

**编写管理器的目的是实现对结算规则执行器的分发，即什么优惠券应该使用什么结算执行器。这个分发过程下沉到管理器去执行，而不是在执行器的内部。这里需要理解 Bean 后置处理器（BeanPostProcessor）的作用。**

**BeanPostProcessor  接口定义了一个你可以自己实现的回调方法，来实现你自己的实例化逻辑、依赖解决逻辑等，如果你想要在 Spring  完成对象实例化、配置、初始化之后实现自己的业务逻辑，你可以补充实现一个或多个 BeanPostProcessor 的实现。**

**BeanPostProcessor 接口定义如下：**

```java
public interface BeanPostProcessor {
 @Nullable
 default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
  return bean;
 }

 @Nullable
 default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
  return bean;
 }
}
```

###  结算模块功能微服务实现

**优惠券模板规则处理器接口定义:**

```
/**
 * 优惠券模板规则处理器接口定义
 */

public interface RuleExecutor {

    /**
     * 规则类型标记
     *
     * @return
     */
    RuleFlag ruleConfig();

    /**
     * 优惠券规则计算
     *
     * @param settle {@link SettlementInfo} 包含了选择的优惠券
     * @return SettlementInfo {@link SettlementInfo} 修正过的优惠券的信息
     */
    SettlementInfo computeRule(SettlementInfo settle);
}
```



**规则执行器抽象类:**

实现了五个方法:

1. **isGoodsTypeSatisfy() ,  检验商品类型和优惠券类型是否匹配 :**

      需要注意 : 1.这里实现的是单类别优惠券校验 , 多类别重载该方法

   ​                      2.商品只需要一个优惠惠券要求的商品类型去匹配就可以了

  2**.processGoodsTypeNotSatisfy() , 处理优惠券类型与优惠券不匹配的情况**

​           当商品类型不满足时, 直接返回商品总价, 并清空优惠券

3. **goodsSum() ,  计算商品总价**
4. **retain2Decimal() , 保留两位小数**
5. **minCost() , 最小支付费用**

```
/**
 * 规则执行器抽象类
 */
public abstract class AbstractExecutor {

    /**
     * 校验商品类型和优惠券类型是否匹配
     * 需要注意:
     * 1. 这里实现的单品类优惠券的校验,多品类优惠券重写此方法
     * 2. 商品只需要有一个优惠券要求的商品类型去匹配就可以
     *
     * @param settle
     * @return
     */
    @SuppressWarnings("all")
    protected boolean isGoodsTypeSatisfy(SettlementInfo settle) {
        List<Integer> goodsType = settle.getGoodInfos().stream()
                .map(GoodInfo::getType).collect(Collectors.toList());
        List<Integer> templateGoodsType = JSON.parseObject(settle.getCouponAndTemplateInfo().get(0)
                .getTemplateSDK().getRule().getUsage().getGoodsType(), List.class);

        //存在交集即可 CollectionUtils.intersection(A,B)
        return CollectionUtils.isNotEmpty(
                CollectionUtils.intersection(goodsType, templateGoodsType));
    }

    /**
     * 处理商品类型与优惠券不匹配的情况
     *
     * @param settle   {@link SettlementInfo} 用户传递的结算消息
     * @param goodsSum 商品总价
     * @return {@link} 已经修改的结算信息
     */
    protected SettlementInfo processGoodsTypeNotSatisfy(
            SettlementInfo settle, double goodsSum) {
        boolean isGoodsTypeSatisfy = isGoodsTypeSatisfy(settle);

        //当商品类型不满足时, 直接返回总价,并清空优惠券
        if (!isGoodsTypeSatisfy) {
            settle.setCost(goodsSum);
            settle.setCouponAndTemplateInfo(Collections.emptyList());
            return settle;
        }
        return null;
    }

    /**
     * 商品总价
     *
     * @param goodInfos
     * @return
     */
    protected double goodsCostSum(List<GoodInfo> goodInfos) {
        return goodInfos.stream().mapToDouble(
                g -> g.getPrice() * g.getCount()
        ).sum();
    }

    /**
     * 保留两位小数
     *
     * @param value
     * @return
     */
    protected double retain2Decimal(double value) {
        return new BigDecimal(value)
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }

    /**
     * 最小支付费用
     *
     * @return
     */
    protected double minCost() {
        return 0.1;
    }
}
```



**优惠券结算规则执行器实现:**

**单品类:      满减  ,   折扣  ,   立减**  

**多品类:      满减+折扣  ,    //TODO 自行添加** 



**满减优惠券结算规则执行器 :**

//计算商品总价

//判断优惠券类型和商品类型是否匹配

//不匹配,没有优惠券,直接返回商品价格

//判断满减是否符合折扣标准

//如果不符合标准 , 则直接返回商品总价

//计算使用优惠券之后的价格 - 结算

```
/**
 * 满减优惠券结算规则执行器
 */

@Component
@Slf4j
public class ManJianExecutor extends AbstractExecutor implements RuleExecutor {


    /**
     * 规则类型标记
     *
     * @return {@link RuleExecutor}
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.MANJIAN;
    }

    /**
     * 优惠券规则计算
     *
     * @param settle {@link SettlementInfo} 包含了选择的优惠券
     * @return {@link SettlementInfo} 修正过的优惠券信息
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settle) {
        //计算商品总价
        double goodsSum = retain2Decimal(
                goodsCostSum(settle.getGoodInfos()));

        //判断优惠券类型和商品类型是否匹配
        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settle, goodsSum
        );

        //不匹配,没有优惠券,直接返回商品价格
        if (null != probability) {
            log.debug("ManJian Template Is Not Match To GoodsType");
            return probability;
        }

        //判断满减是否符合折扣标准
        CouponTemplateSDK templateSDK = settle.getCouponAndTemplateInfo()
                .get(0).getTemplateSDK();
        //基准(满多少才减)
        double base = (double)templateSDK.getRule().getDiscount().getBase();
        //额度  减多少
        double quota = (double)templateSDK.getRule().getDiscount().getQuota();

        //如果不符合标准 , 则直接返回商品总价
        if (goodsSum < base) {
            log.debug("Current Goods Cost Sum < ManJian Coupon Base!");
            settle.setCost(goodsSum);
            settle.setCouponAndTemplateInfo(Collections.emptyList());
            return settle;
        }
        //计算使用优惠券之后的价格 - 结算
        settle.setCost(retain2Decimal(
                (goodsSum - quota) > minCost() ? (goodsSum - quota) : minCost()
        ));
        log.debug("Use ManJian Coupon Make Goods Cost From {} To {}",
                goodsSum, settle.getCost());
        return settle;
    }
}
```



**立减优惠券结算规则执行器:**

**思路同上, 不过立减优惠券没有门槛,直接减**

```
**
 * 立减优惠券结算规则执行器
 */
@Component
@Slf4j
public class LiJianExecutor extends AbstractExecutor implements RuleExecutor {

    /**
     * 规则类型定义
     *
     * @return
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.LIJIAN;
    }

    /**
     * 优惠券规则计算
     * @param settle {@link SettlementInfo} 包含了选择的优惠券
     * @return {@link SettlementInfo} 修正过的优惠券信息
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settle) {
        double goodsSum = retain2Decimal(goodsCostSum(
                settle.getGoodInfos()));

        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settle, goodsSum
        );

        if (null != probability) {
            log.debug("LiJian Template Is Not Match To GoodsType!");
            return probability;
        }

        //立减优惠券直接使用,没有门槛
        CouponTemplateSDK templateSDK = settle.getCouponAndTemplateInfo()
                .get(0).getTemplateSDK();
        double quota = (double) templateSDK.getRule().getDiscount().getQuota();

        //计算使用优惠券之后的价格
        settle.setCost(retain2Decimal(goodsSum - quota) > minCost() ?
                retain2Decimal(goodsSum - quota) : minCost()
        );
        log.debug("Use LiJian Coupon Make Goods Cost From {} To {}",
                goodsSum, settle.getCost());
        return settle;
    }
}
```



**折扣优惠券结算规则执行器:**

**思路同上, 不过折扣优惠券没有门槛,直接减**

```
**
 * 折扣优惠券结算规则执行器
 */
@Component
@Slf4j
public class ZheKouExecutor extends AbstractExecutor implements RuleExecutor {


    /**
     * 规则类型标记
     *
     * @return {@link RuleFlag}
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.ZHEKOU;
    }

    /**
     * 优惠券规则计算
     *
     * @param settle {@link SettlementInfo} 包含了选择的优惠券
     * @return
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settle) {
        //计算商品总价
        double goodsSun = retain2Decimal(
                goodsCostSum(settle.getGoodInfos())
        );

        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settle, goodsSun
        );

        if (null != probability) {
            log.debug("ZheKou Template Is Not Match To GoodsType");
            return probability;
        }

        //折扣优惠券可以直接使用,没有门槛
        CouponTemplateSDK templateSDK = settle.getCouponAndTemplateInfo().get(0)
                .getTemplateSDK();
        double quota = (double) templateSDK.getRule().getDiscount().getQuota();

        //计算使用优惠券之后的价格
        settle.setCost(retain2Decimal(goodsSun * (quota * 1.0 / 100)) > minCost() ?
                        retain2Decimal(goodsSun * (quota * 1.0 / 100)) : minCost()
        );
        log.debug("Use ZheKou Coupon Make Goods  Cost From {} To {}",
                goodsSun, settle.getCost());
        return settle;
    }
}
```





**满减 + 折扣 优惠券规则结算执行器:**

1.重写父类: isGoodsTypeSatisfy() 方法   校验商品类型和优惠券是否匹配

2.获取settle.getCouponAndTemplateInfo()中的 优惠券模板 , 进行分类  满减  和 折扣

3.判断两张优惠券可不可以一起使用 , isTemplateCanShard() , 

当前的折扣优惠券和满减券如果不能一起使用,清空优惠券,返回商品总价

4.如果可以 , 先计算满减 , 再计算折扣 , 把使用的优惠券添加到setCouponAndTemplateInfo()

```
/**
 * 满减 + 折扣 优惠券规则结算执行器
 */
@Component
@Slf4j
public class ManJianZheKouExecutor extends AbstractExecutor implements RuleExecutor {

    /**
     * 规则类型定义
     *
     * @return
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.MANJIAN_ZHEKOU;
    }

    /**
     * 校验商品类型和优惠券是否匹配
     * 需要注意:
     * 1.这里实现的时 满减 +折扣优惠券的校验
     * 2.如果想要实现多类优惠券 则需要把所有商品类型包含在内 , 即差集为null
     *
     * @param settle {@link SettlementInfo} 用户传递的计算信息
     * @return
     */
    @SuppressWarnings("all")
    @Override
    protected boolean isGoodsTypeSatisfy(SettlementInfo settle) {
        log.debug("ManJian And zheKou Is Match Not Null");
        List<Integer> goodsType = settle.getGoodInfos().stream()
                .map(GoodInfo::getType).collect(Collectors.toList());
        List<Integer> templateGoodsType = new ArrayList<>();

        settle.getCouponAndTemplateInfo().forEach(ct -> {
            templateGoodsType.addAll(
                    JSON.parseObject(ct.getTemplateSDK()
                            .getRule().getUsage().getGoodsType(), List.class));
        });

        //如果想要使用多品类的优惠券, 则必须要所有的商品类型都包含在里面, 即差集为null
        //CollectionUtils.subtract(A,B) A-B=null
        return CollectionUtils.isNotEmpty(
                CollectionUtils.subtract(goodsType, templateGoodsType)
        );
    }

    /**
     * 优惠券规则计算
     *
     * @param settle {@link SettlementInfo} 包含了选择的优惠券
     * @return {@link SettlementInfo} 修正过的优惠券信息
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settle) {
        double goodsSum = retain2Decimal(goodsCostSum(
                settle.getGoodInfos()
        ));

        //商品类型校验
        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settle, goodsSum
        );
        if (null != probability) {
            log.debug("ManJian And ZheKou Template Is Not Match To GoodsType");
            return probability;
        }

        SettlementInfo.CouponAndTemplateInfo manJian = null;
        SettlementInfo.CouponAndTemplateInfo zheKou = null;

        for (SettlementInfo.CouponAndTemplateInfo ct :
                settle.getCouponAndTemplateInfo()) {
            if (CouponCategory.of(ct.getTemplateSDK().getCategory())
                    == CouponCategory.MANJIAN) {
                manJian = ct;
            } else {
                zheKou = ct;
            }
        }
        assert null != manJian;
        assert null != zheKou;

        //当前的折扣优惠券和满减券如果不能一起使用,清空优惠券,返回商品总价
        if (!isTemplateCanShard(manJian, zheKou)) {
            log.debug("Current Manjian and Zhekou Can Not Shared");
            settle.setCouponAndTemplateInfo(Collections.emptyList());
            settle.setCost(goodsSum);
            return settle;
        }

        //可以一起使用
        List<SettlementInfo.CouponAndTemplateInfo> ctInfos = new ArrayList<>();
        double manJianBase = (double) manJian.getTemplateSDK().getRule().getDiscount().getBase();
        double manJianQuota = (double) manJian.getTemplateSDK().getRule().getDiscount().getQuota();

        //最终价格
        double targetSum = goodsSum;

        //先计算满减
        if (targetSum > manJianBase) {
            targetSum -= manJianQuota;
            ctInfos.add(manJian);
        }

        //在计算折扣
        double zheKouQuota = (double) zheKou.getTemplateSDK().getRule().getDiscount().getQuota();
        targetSum *= zheKouQuota * (1.0 / 100);
        ctInfos.add(zheKou);

        settle.setCouponAndTemplateInfo(ctInfos);
        settle.setCost(retain2Decimal(
                targetSum > minCost() ? targetSum : minCost()
        ));

        log.debug("Use ManJian And ZheKou Coupon Make Goods Cost From {} To {}",
                goodsSum, settle.getCost());

        return settle;
    }

    /**
     * 当前的两张优惠券可不可以一起使用
     * 即校验 TemplateRule 中的weight-优惠券的唯一编码 key 是否满足条件
     *
     * @param manJian
     * @param zheKou
     * @return
     */
    @SuppressWarnings("all")
    private boolean isTemplateCanShard(SettlementInfo.CouponAndTemplateInfo manJian,
                                       SettlementInfo.CouponAndTemplateInfo zheKou) {
        String manJianKey = manJian.getTemplateSDK().getKey()
                + String.format("%04d", manJian.getId());
        String zheKouKey = zheKou.getTemplateSDK().getKey()
                + String.format("%%04d", zheKou.getId());

        List<String> allSharedKeysForManjian = new ArrayList<>();
        allSharedKeysForManjian.add(manJianKey);
        allSharedKeysForManjian.addAll(JSON.parseObject(
                manJian.getTemplateSDK().getRule().getWeight(),
                List.class
        ));

        List<String> allSharedKeysForZhekou = new ArrayList<>();
        allSharedKeysForZhekou.add(zheKouKey);
        allSharedKeysForZhekou.addAll(JSON.parseObject(
                zheKou.getTemplateSDK().getRule().getWeight(),
                List.class
        ));

        return CollectionUtils.isSubCollection(
                Arrays.asList(manJianKey, zheKouKey), allSharedKeysForManjian
        ) || CollectionUtils.isSubCollection(
                Arrays.asList(manJianKey, zheKouKey), allSharedKeysForZhekou
        );
    }

}
```



**优惠券结算规则执行管理器:**

**BeanPostProcessor :后置处理器**

 **系统中所有的Bean 都被Spring容器创建完成之后再去调用这个类和里面的方法**

```
/**
 * 优惠券结算规则执行管理器
 * 即根据用户的请求(SettlementInfo)找到对应的 Executor 去做结算
 * BeanPostProcessor : Bean后置处理器
 */
@Component
@Slf4j
@SuppressWarnings("all")
public class ExecuteManager implements BeanPostProcessor {

    /*规则执行映射*/
    private static Map<RuleFlag, RuleExecutor> executorIndex =
            new HashMap<>(RuleFlag.values().length);


    /**
     * 优惠券结算规则计算入口
     * 注意: 一定要保证传进来的优惠券个数>=1
     *
     * @param settlement
     * @return
     */
    public SettlementInfo computeRule(SettlementInfo settlement)
            throws CouponException {
        SettlementInfo result = null;

        //单类优惠券
        if (settlement.getCouponAndTemplateInfo().size() == 1) {
            CouponCategory category = CouponCategory.of(
                    settlement.getCouponAndTemplateInfo()
                            .get(0).getTemplateSDK().getCategory()
            );
            switch (category) {
                case MANJIAN:
                    result = executorIndex.get(RuleFlag.MANJIAN)
                            .computeRule(settlement);
                    break;
                case LIJIAN:
                    result = executorIndex.get(RuleFlag.LIJIAN)
                            .computeRule(settlement);
                    break;
                case ZHEKOU:
                    result = executorIndex.get(RuleFlag.ZHEKOU)
                            .computeRule(settlement);
                    break;
            }
        } else {

            //多品类优惠券
            List<CouponCategory> categories = new ArrayList<>(
                    settlement.getCouponAndTemplateInfo().size()
            );

            settlement.getCouponAndTemplateInfo().forEach(ct ->
                    categories.add(CouponCategory.of(
                            ct.getTemplateSDK().getCategory()
                    )));
            if (categories.size() != 2) {
                throw new CouponException("Not Support For More" +
                        "Template Categories");
            } else {
                if (categories.contains(CouponCategory.MANJIAN)
                        && categories.contains(CouponCategory.ZHEKOU)) {
                    result = executorIndex.get(RuleFlag.MANJIAN_ZHEKOU)
                            .computeRule(settlement);
                } else {
                    throw new CouponException("Not Support For Other" +
                            "Template Category");
                }
            }
        }
        return result;
    }


    /**
     * Bean初始化之前执行
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        if (!(bean instanceof RuleExecutor)) {
            return bean;
        }

        RuleExecutor executor = (RuleExecutor) bean;
        RuleFlag ruleFlag = executor.ruleConfig();

        //bean已经存在
        if (executorIndex.containsKey(ruleFlag)) {
            throw new IllegalStateException("There is already an executor " +
                    "for rule flag" + ruleFlag);
        }

        log.info("Load executor {} for rule flag"
                , executor.getClass(), ruleFlag);

        executorIndex.put(ruleFlag, executor);

        return null;
    }


    /**
     * Bean初始化之后执行
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }
}
```



###  结算Controller

```
/**
 * 优惠券结算 controller
 */

@Slf4j
@RestController
public class settlementController {

    /*优惠券规则执行管理器*/
    private final ExecuteManager executeManager;

    @Autowired
    public settlementController(ExecuteManager executeManager) {
        this.executeManager = executeManager;
    }

    /**
     * 优惠券结算
     * @param settlement
     * @return
     */
    @PostMapping("/settlement/compute")
    public SettlementInfo computeRule(@RequestBody SettlementInfo settlement)
            throws CouponException {
        log.info("settlement: {}",JSON.toJSONString(settlement));
        return executeManager.computeRule(settlement);
    }
```
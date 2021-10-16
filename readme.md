# springboot作业题

>
>
>#### 作业要求：
>
>自定义简易版SpringBoot，实现SpringBoot MVC及内嵌Tomcat启动、DispatcherServlet注册和组件扫描功能。
>
>- 程序通过main方法启动，可以自动启动tomcat服务器
>- 可以自动创建和加载DispatcherServlet组件到ServletContext中
>- 可以自动通过@ComponentScan扫描Controller等组件
>- Controller组件可以处理浏览器请求，返回响应结果



## 提示

传统SpringMVC框架web.xml的配置内容:

```xml
<web-app>
   <!-- 初始化Spring上下文 -->
   <listener>
       <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
   </listener>
   <!-- 指定Spring的配置文件 -->
   <context-param>
       <param-name>contextConfigLocation</param-name>
       <param-value>/WEB-INF/app-context.xml</param-value>
   </context-param>
   <!-- 初始化DispatcherServlet -->
   <servlet>
       <servlet-name>app</servlet-name>
       <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
       <init-param>
           <param-name>contextConfigLocation</param-name>
           <param-value></param-value>
       </init-param>
       <load-on-startup>1</load-on-startup>
   </servlet>
   <servlet-mapping>
       <servlet-name>app</servlet-name>
       <url-pattern>/app/*</url-pattern>
   </servlet-mapping>
</web-app>
```

Spring官方文档中给出了基于Servlet3.0规范如何使用Java代码实现web.xml配置的example

```java
public class MyWebApplicationInitializer implements WebApplicationInitializer {

   @Override
   public void onStartup(ServletContext servletCxt) {

       // Load Spring web application configuration
       //通过注解的方式初始化Spring的上下文
       AnnotationConfigWebApplicationContext ac = new AnnotationConfigWebApplicationContext();
       //注册spring的配置类（替代传统项目中xml的configuration）
       ac.register(AppConfig.class);
       ac.refresh();

       // Create and register the DispatcherServlet
       //基于java代码的方式初始化DispatcherServlet
       DispatcherServlet servlet = new DispatcherServlet(ac);
       ServletRegistration.Dynamic registration = servletContext.addServlet("app", servlet);
       registration.setLoadOnStartup(1);
       registration.addMapping("/app/*");
   }
}
```



## 实现思路

（1）创建maven工程，导入以下依赖：

```xml
<properties>
       <java.version>1.8</java.version>
   </properties>

   <dependencies>
       <dependency>
           <groupId>org.apache.tomcat.embed</groupId>
           <artifactId>tomcat-embed-core</artifactId>
           <version>8.5.32</version>
       </dependency>
       <dependency>
           <groupId>org.springframework</groupId>
           <artifactId>spring-context</artifactId>
           <version>5.0.8.RELEASE</version>
       </dependency>
       <dependency>
           <groupId>org.springframework</groupId>
           <artifactId>spring-webmvc</artifactId>
           <version>5.0.8.RELEASE</version>
       </dependency>
   </dependencies>

   <build>
       <plugins>
           <plugin>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-maven-plugin</artifactId>
           </plugin>
       </plugins>
   </build>
```

（2）创建SpringApplication 类，编写run方法（方法中要求完成tomcat的创建及启动）

```java
 public static void run() throws LifecycleException, ServletException {
        // 方法中要求完成tomcat的创建及启动
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        // 必须指定资源路径，否则Tomcat无法扫描ServletContainerInitializer
        StandardContext ctx = (StandardContext)tomcat.addWebapp("/",new File("src/main").getAbsolutePath());
        ctx.setReloadable(false);

        File file = new File("target/classes");
        // 创建webRoot
        WebResourceRoot resourceRoot = new StandardRoot(ctx);
        // Tomcat内部读取class执行
        resourceRoot.addJarResources(new DirResourceSet(resourceRoot,"/WEB-INF/classes",file.getAbsolutePath(),"/"));

        tomcat.start();
        // 异步接收请求
        tomcat.getServer().await();
    }
```



（3）创建spring的配置类 AppConfig该类上要通过@ComponentScan来进行包扫描

```java
@Configuration
@ComponentScan("com.example")
public class AppConfig {
}
```



（4）创建MyWebApplicationInitializer实现WebApplicationInitializer接口，重写onstartup方法（WebApplicationInitializer实现web.xml的配置）

```
提示：仿写上面Spring官方给出的例子，完成AppConfig的注册，并基于java代码的方式初始化DispatcherServlet
```

```java
public class MyWebApplicationInitializer implements WebApplicationInitializer {
    public void onStartup(ServletContext servletContext) throws ServletException {
        // Load Spring web application configuration
        //通过注解的方式初始化Spring的上下文
        AnnotationConfigWebApplicationContext ac = new AnnotationConfigWebApplicationContext();
        //注册spring的配置类（替代传统项目中xml的configuration）
        ac.register(AppConfig.class);
        ac.refresh();

        // Create and register the DispatcherServlet
        //基于java代码的方式初始化DispatcherServlet
        DispatcherServlet servlet = new DispatcherServlet(ac);
        ServletRegistration.Dynamic registration = servletContext.addServlet("app", servlet);
        registration.setLoadOnStartup(1);
        registration.addMapping("/app/*");
    }
}
```



~~（5）创建MySpringServletContainerInitializer，实现ServletContainerInitializer接口，重写onstartup方法，方法中调用第4步中MyWebApplicationInitializer的onstartup方法~~

```
提示：Servlet 3.0+容器启动时将自动扫描类路径以查找实现Spring的Webapplicationinitializer接口的所有实现，将其放进一个Set集合中，提供给ServletContainerInitializer中onStartup方法的第一个参数。
```



由于我们pom.xml中依赖了spring-webmvc，从而间接依赖了spring-web，恰好spring-web实现了ServletContainerInitializer，在其包下面的META-INF/services里面  `org.springframework.web.SpringServletContainerInitializer`

![image-20211016092154225](.\img\image-20211016092154225.png)

![image-20211016092219875](.\img\image-20211016092219875.png)

~~（6）创建文件：META-INF/services/javax.servlet.ServletContainerInitializer，在该文件中配置ServletContainerInitializer的实现类MySpringServletContainerInitializer~~

（7）编写一个Controller测试类及目标方法，响应输出“hello”即可

```java
@RestController
public class TestController {

    @RequestMapping("/test")
    public String test(){
        return "hello mimi-springboot";
    }
}
```



（8）编写一个启动类MyRunBoot，通过执行main方法启动服务

```
提示：main方法调用第2步中SpringApplication的run方法启动
```

```java
public class MyRunBoot {
    public static void main(String[] args) throws LifecycleException, InterruptedException, ServletException {

        SpringApplication.run();
    }
}
```



（9）通过浏览器对目标方法进行方法

![image-20211016092711461](.\img\image-20211016092711461.png)
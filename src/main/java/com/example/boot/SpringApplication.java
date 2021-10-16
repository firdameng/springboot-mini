package com.example.boot;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import javax.net.ssl.StandardConstants;
import javax.servlet.ServletException;
import java.io.File;

/**
 * @Author CaiWencheng
 * @Date 2021-10-12 23:19
 */
public class SpringApplication {
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

}

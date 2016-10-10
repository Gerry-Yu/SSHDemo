layout: post
title:  "Spring整合Struts&Hibernate"
categories: SSH
tag: SSH
---

* content
{:toc}

## 配置web.xml

启动Struts和Spring容器

> Spring配置文件在resource/conf目录下

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app version="2.4" xmlns="http://java.sun.com/xml/ns/j2ee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee
        http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd">

  <context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>classpath:conf/spring-config.xml</param-value>
  </context-param>
  <listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
  </listener>

  <filter>
    <filter-name>struts2</filter-name>
    <filter-class>org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter</filter-class>
  </filter>
  <filter-mapping>
    <filter-name>struts2</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>
</web-app>
```
## Spring配置文件
+ 配置dataSource，数据库连接池，这里使用C3P0
+ 配置SessionFactory
+ 配置事务
+ 依赖注入的Bean声明 有两种方式，显示指明或使用注解
> `配置事务`有两种方式，配置AOP或注解。注解方式需要在ServiceImp加上@Transactional。`这里需要注意jar包是否完整`  

jdbc.properties
``` properties
jdbc_driverClassName=com.mysql.jdbc.Driver
jdbc_url=jdbc:mysql://localhost:3306/hibernate?useUnicode=true&amp;characterEncoding=utf8
jdbc_username=root
jdbc_password=
```

spring-config.xml(xsd 一般省略版本号)
``` xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:p="http://www.springframework.org/schema/p"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
	   http://www.springframework.org/schema/beans/spring-beans.xsd
	   http://www.springframework.org/schema/context
	   http://www.springframework.org/schema/context/spring-context.xsd
       http://www.springframework.org/schema/tx
       http://www.springframework.org/schema/tx/spring-tx.xsd
       http://www.springframework.org/schema/aop
       http://www.springframework.org/schema/aop/spring-aop.xsd">

    <context:property-placeholder location="classpath:conf/jdbc.properties" />
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource"
          p:driverClass="${jdbc_driverClassName}"
          p:jdbcUrl="${jdbc_url}"
          p:user="${jdbc_username}"
          p:password="${jdbc_password}"
          p:initialPoolSize="2"
          p:maxPoolSize="40"
          p:minPoolSize="2"
          p:maxIdleTime="30" >
    </bean>

    <bean id="sessionFactory" class="org.springframework.orm.hibernate4.LocalSessionFactoryBean" p:dataSource-ref="dataSource">
        <property name="packagesToScan">
            <list>
                <value>com.demo.model</value>
            </list>
        </property>
        <property name="hibernateProperties">
            <props>
                <prop key="hibernate.dialect">org.hibernate.dialect.MySQL5Dialect</prop>
                <prop key="hibernate.hbm2ddl.auto">update</prop>
                <prop key="hibernate.format_sql">true</prop>
            </props>
        </property>
    </bean>

    <bean name="transactionManager" class="org.springframework.orm.hibernate4.HibernateTransactionManager" p:sessionFactory-ref="sessionFactory" />
<!--        注解方式，需要在ServiceImpl加上@Transactional-->
    <tx:annotation-driven transaction-manager="transactionManager"/>
    <!--AOP配置方式-->
<!--
    <aop:config>
        <aop:pointcut id="txServices" expression="execution(* com.demo.service..*.*(..))"/>
        <aop:advisor advice-ref="txAdvice" pointcut-ref="txServices"/>
    </aop:config>

    <tx:advice id="txAdvice" transaction-manager="transactionManager">
        <tx:attributes>
            <tx:method name="get*" read-only="true"/>
            <tx:method name="*" isolation="DEFAULT" propagation="REQUIRED" timeout="5"/>
        </tx:attributes>
    </tx:advice>
-->

    <!-- 显示指明 -->

    <!--
        <bean id="userDao" class="com.demo.dao.impl.UserDaoImpl" p:sessionFactory-ref="sessionFactory"/>
        <bean id="userService" class="com.demo.service.impl.UserServiceImpl" p:userDao-ref="userDao"/>
        <bean id="addUserAction" class="com.demo.action.AddUserAction" scope="prototype" p:userService-ref="userService"/>
    -->

    <!-- 自动扫描dao和service包(自动注入)  使用@@Service@Controller@Repository@Component-->
    <context:component-scan base-package="com.demo.dao,com.demo.service" />

</beans>
```

## Struts配置文件

> 下面配置将全部使用`注解`方式

``` xml
    <package name="default" extends="struts-default">
        <action name="addUser" class="com.demo.action.AddUserAction">
            <result name="success">WEB-INF/success.jsp</result>
        </action>
    </package>
```

## Java代码
这里只说明*DaoImpl，可以使用HibernateTemplate和HIbernateDaoSupport。下面是简单的使用HibernateTemplate

``` java
@Repository("userDao")
public class UserDaoImpl implements UserDao {
    private HibernateTemplate hibernateTemplate;
    @Resource
    public void setSessionFactory (SessionFactory sessionFactory) {
        this.hibernateTemplate =  new HibernateTemplate(sessionFactory);
    }

    public void saveUser(User user) {
        this.hibernateTemplate.save(user);
    }
}
```
注解说明：  
4个Bean注解  
+ @Service用于标注业务层组件  
+ @Controller用于标注控制层组件（如struts中的action）  
+ @Repository用于标注数据访问组件，即DAO组件  
+ @Component泛指组件，当组件不好归类的时候，我们可以使用这个注解进行标注  

2个依赖注入注解：@Resource @Autowire 两个注解都可以用在`字段`上和`setter`方法上  
+ @Autowired默认按类型装配（属于spring），默认情况下必须要求依赖对象必须存在，如果要允许null值，可以设置它的required属性为false。如果想使用名称装配可以结合@Qualifier注解进行使用，如下：
``` java
@Autowired() @Qualifier("baseDao")    
private BaseDao baseDao;
```

+ @Resource是JDK1.6支持的注解，默认按照名称进行装配。名称可以通过name属性进行指定，如果没有指定name属性，当注解写在字段上时，默认取字段名，按照名称查找。如果注解写在setter方法上默认取属性名进行装配。当找不到与名称匹配的bean时才按照类型进行装配。但是需要注意的是，如果name属性一旦指定，就只会按照名称进行装配。（一般建议使用@Resource）

## JUnit测试
> 需要加上@Transactional注解。这里是插入操作，JUnit默认要回滚，defaultRollback = false指定不回滚  


``` java
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = false)
@ContextConfiguration({"classpath:conf/spring-config.xml","classpath:struts.xml"})
@Component
public class UserTest {

    @Resource
    private UserService userService;

    @Test
    @Transactional
    public void addUser() {
        User user = new User();
        user.setPassword("testPasswordServiceTest");
        user.setUsername("testUsernameServiceTest");
        userService.addUser(user);
    }
}
```



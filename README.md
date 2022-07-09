## Background

I learn this simple project from bilibili.com of **ITHEIMA**，the original name  is  **Regie Takeout** ，and there are some bugs during I learn.Thus,I would like to take this chance to fix some of them and improve some functions and features.

## Project Overview

### **Github URL**

https://github.com/IceSwear/takeout

### **Bilibili URL**

https://www.bilibili.com/video/BV1aT411g7s8

### **Swagger API**

http://localhost:8888/api_test/swagger-ui.html#/

### **Employee Backend Management Interface**

http://localhost:8888/backend/page/login/login.html

![img](https://s2.loli.net/2022/07/10/X4uEvgFYifqedW5.png)

### User Interface:

http://localhost:8888/front/page/login.html

![img](https://s2.loli.net/2022/07/10/avTXUA1Je5HnkVq.png)

## Features:

1. Regarding login function,using mail validation instead of SMS validation,it may be more internationalised for general situation;
2. Use Aliyun OSS storage service to take place of local image repository; 
3. Add field "image" to the DB of setmeal dish to save image,so users can see image when they click the setmeal image to see the detailed dish;
4. Limiting size of upload image,so it could push less pressure to the OSS cilent;
5. etc..................... 



## TO DO

1. There are still some bugs in user interface such as order details list displaying duplicate information.
2. Payment interface and delivery interface could be added,though,some APIs may need to ask for a third-part request;
3. Look for a suitable method to change the image display method;
4. Change the type of primary key of user & employee to keep thread safe;
5. etc.....................

## Summary

It's no exaggeration to say that it's a nice demo to recover the confidence during these 90 days that I learned Java at a zero basis level.Belows are some views from me after "C+V" this project——

- **Redis Cache**

I got the ablility to use 2 ways to save cache to make server's repsonse faster.One is using by "***StringRedisTemplate\***" to save and delete in appointed methods with duration.The other is using annotation "**@Cachable**";

**StringRedisTemplate:** Inconvenient  to insernt  easily, but it could set appointed time,more over,flexiable to set time and content you want;

**@Cachable:** Easy to put on the appointed;though, it only can save return content, and should set global duration;

- **Threadlocal**

By using threadlocal, it could easily set & get the variable at the same thread during operations.Nevertheless, it's not safe if 2 users to use  at same situation——That is what I met during I placed an order on the demo;

- **Design Mode**

Per this simple demo,I noticed that APIs could use not only at one places.Some APIs argument is designed a universal for similar purposes.

- **Mybatis-Plus**

I've strengthened usage of Mybatis-Plus and also been awared that its advantage & disadvantage.It's convenient to use for basic CRUD purposes.However, it's hard to use when you want to query multiple tables at the same time;

- **Filter**

Filter interface in this demo help filter url patterns to prevent visit of someone who hasn't logined yet.We should overrider a implement class; 

- **Filter Logic**

When I test front end and back end at the same time, it may occur a conflict in BaseContext.getId();after spent a half day solving them,I have deeper understanding about the thread.

- **Extend Way**

Eg:DishDTO extends Dish,this made DishDTO contain more information;

- **Steam.().map.((item)->{ ...})**

**I never use this way before,but it seems that I can simulate,XD;**

- ***TBD -etc...........***



## Finally

At the end of this article,I would stand here to say again that ——Many thanks for ITHEIMA's free video tutorails that help make tremendous people including me to learn coding easily.

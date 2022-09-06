# 1.패스워드 인코드 만들 때 쓰는 함수

```java
@Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

```
# 2. application.yml 

```java

spring:
  security:
    user:
      name: user1
      password: 1234
      roles: USER
```

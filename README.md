<h1 style="text-align: center">ELADMIN Admin System</h1>
<div style="text-align: center">

[![AUR](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://github.com/elunez/eladmin/blob/master/LICENSE)
[![star](https://gitee.com/elunez/eladmin/badge/star.svg?theme=white)](https://gitee.com/elunez/eladmin)
[![GitHub stars](https://img.shields.io/github/stars/elunez/eladmin.svg?style=social&label=Stars)](https://github.com/elunez/eladmin)
[![GitHub forks](https://img.shields.io/github/forks/elunez/eladmin.svg?style=social&label=Fork)](https://github.com/elunez/eladmin)

</div>

#### Project overview
A front-end/back-end separated admin system based on Spring Boot 2.7.18, Spring Boot JPA, JWT, Spring Security, Redis and Vue

A mybatis-plus based version is now released, project URL:[https://github.com/elunez/eladmin-mp](https://github.com/elunez/eladmin-mp), [https://gitee.com/elunez/eladmin-mp](https://gitee.com/elunez/eladmin-mp). 

**Development docs:**  [https://eladmin.vip](https://eladmin.vip)

**Demo URL:**  [https://eladmin.vip/demo](https://eladmin.vip/demo)

**Account password:** `admin / 123456`

#### Project source

|     |   Back-end source code  |   Front-end source code  |
|---  |--- | --- |
|  github   |  https://github.com/elunez/eladmin   |  https://github.com/elunez/eladmin-web   |
|  Gitee   |  https://gitee.com/elunez/eladmin   |  https://gitee.com/elunez/eladmin-web   |

#### VPS Recommended
<a href="https://bwh81.net/aff.php?aff=70876" target="_blank">
<img src="https://eladmin.vip/images/banner/side.jpeg" style="width: 435px;border-radius: 2px;">
</a>

Use coupon code: `BWHCGLUKKB`, obtainable 6.81%  discount [View introduction](https://bwhstock.in/)

#### Main features
- Uses the latest tech stack with rich community resources.
- Efficient development: the code generator can produce front-end and back-end code with one click
- Supports data dictionaries for easy management of statuses
- Supports endpoint rate limiting to prevent malicious requests from overloading the service layer
- Supports endpoint-level feature permissions and data permissions, with customizable operations
- Custom permission annotation and anonymous endpoint annotation for quickly intercepting or allowing endpoints
- Wraps some common front-end components: table data requests, data dictionaries, etc.
- Unified front-end/back-end exception interception; exceptions are output uniformly to avoid tedious checks
- Supports online user management and server performance monitoring, and can limit a user to a single login
- Supports operations management for easily deploying and managing applications on remote servers

####  System functions
- User management: provides user-related configuration; after adding a user the default password is 123456
- Role management: assign permissions and menus, and set role data permissions by department
- Menu management: dynamic menu routing is implemented, configurable from the back end, with multi-level menu support
- Department management: configure the organization structure, displayed as a tree table
- Job management: configure the positions of each department
- Dictionary management: maintain commonly used fixed data, e.g. status, gender
- System logs: records user operation logs and exception logs so developers can locate and debug problems
- SQL Monitoring: uses druid to monitor database access performance, default username admin, password 123456
- Scheduled tasks: integrates Quartz for scheduled tasks and adds task logs, so task status is clear at a glance
- Code generation: flexibly generate front-end and back-end code, reducing a lot of repetitive work
- Email tool: send HTML emails together with rich text
- Amazon S3 cloud storage: supports most object storage on the market that is compatible with the Amazon S3 protocol, such as Qiniu Cloud and Alibaba Cloud
- Alipay payment: integrates Alipay payment and provides a test account for self-testing
- Service monitoring: monitor the server load
- Operations management: deploy your applications with one click

#### Project structure
The project is developed in modules by function, structured as follows

- `eladmin-common` Common module of the system; various utility classes and shared configuration live in this module

- `eladmin-system` Core system module and project entry module, and the module that is finally packaged and deployed

- `eladmin-logging` Logging module of the system; other modules that need to record logs must import this module

- `eladmin-tools` Third-party tools module, including: email, Amazon S3 cloud storage, local storage, Alipay

- `eladmin-generator` Code generation module of the system, supporting generation of front-end and back-end CRUD code

#### Detailed structure

```
- eladmin-common Common module
    - annotation Custom annotations of the system
    - aspect Aspect for the custom annotation
    - base Provides Entity and DTO base classes and the generic mapstruct mapper
    - config General project configuration
        - Web Configure CORS and static resource mapping, Swagger, and the temporary file upload path
        - Redis Configuration, Redisson configuration, Async thread pool configuration
        - Permission interception config, AuthorityConfig, Druid ad removal config
    - exception Unified exception handling of the project
    - utils General system utility classes, listing some commonly used ones
        - BigDecimaUtils Amount calculation utility class
        - RequestHolder Request utility class
        - SecurityUtils Security utility class
        - StringUtils String utility class
        - SpringBeanHolder Spring Bean Utility class
        - RedisUtils Redis Utility class
        - EncryptUtils Encryption utility class
        - FileUtil File utility class
- eladmin-system System core module (application entry point)
    - sysrunner Process data after the program starts
	- modules System-related modules(Login authorization, system monitoring, scheduled tasks, system modules, operations modules)
- eladmin-logging System logging module
- eladmin-tools System third-party tools module
    - email Email tool
    - amazon Amazon S3 cloud storage tool
    - alipay Alipay payment tool
    - local-storage Local storage tool
- eladmin-generator System code generation module
```

#### Special thanks

- Thanks to [PanJiaChen](https://github.com/PanJiaChen/vue-element-admin) Front-end template provided by a community expert

- Thanks to [Moxun](https://github.com/moxun1639) Generic front-end Curd component provided by a community expert

- Thanks to [zhy6599](https://gitee.com/zhy6599) Back-end operations management features provided by a community expert

- Thanks to [j.yao.SUSE](https://github.com/everhopingandwaiting) Anonymous endpoints and Redis rate limiting provided by a community expert

- Thanks to [d15801543974](https://github.com/d15801543974) Annotation-based generic query approach provided by a community expert

#### Donate
The project cannot grow without your support, buy the author a coffee☕  [Donate](https://eladmin.vip/pages/030101/)

#### Feedback and discussion
- QQ Discussion groups: 891137268, 947578238,659622532
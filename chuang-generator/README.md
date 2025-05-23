# 代码生成模块

基于Velocity模板引擎的代码生成器，支持单表CRUD、树表、主子表等多种模板类型。

## 功能特点

- ✅ **多模板支持**：支持单表CRUD、树表、主子表三种模板类型
- ✅ **Excel导出集成**：自动为实体类添加Excel导出注解，支持字典映射
- ✅ **智能类型映射**：自动将数据库字段类型映射为Java类型
- ✅ **代码预览**：支持在线预览生成的代码
- ✅ **批量生成**：支持批量导入表并生成代码
- ✅ **配置化**：支持自定义包名、作者等配置信息

## 模板类型

### 1. 单表CRUD (crud)
生成标准的增删改查功能，包括：
- Entity实体类
- Mapper接口和XML
- Service接口和实现类
- Controller控制器
- Request/Response DTO类

### 2. 树表 (tree)
适用于具有层级关系的数据表，如部门、菜单等，额外提供：
- 树形结构查询
- 父子节点关系处理
- 递归构建树形数据

### 3. 主子表 (sub)
适用于一对多关系的数据表，如订单和订单详情，提供：
- 主表和子表联合操作
- 事务控制
- 级联删除

## 快速开始

### 1. 导入数据库表

```java
@RestController
@RequestMapping("/generator")
public class GeneratorController {
    
    @Autowired
    private GenTableService genTableService;
    
    @PostMapping("/importTable")
    public ApiResponse<Boolean> importTable(@RequestBody List<String> tableNames) {
        return ApiResponse.success(genTableService.importTable(tableNames));
    }
}
```

### 2. 配置生成参数

```java
// 设置代码生成配置
GenConfigUpdateRequest config = new GenConfigUpdateRequest();
config.setPackageName("cn.zhangchuangla.demo");
config.setAuthor("张创");
genTableService.updateConfigInfo(config);
```

### 3. 生成代码

```java
// 预览代码
Map<String, String> codeMap = genTableService.previewCode("user_info");

// 下载代码压缩包
byte[] zipData = genTableService.downloadCode("user_info");
```

## 模板变量

### 基础变量

| 变量名 | 说明 | 示例 |
|--------|------|------|
| `${packageName}` | 包名 | cn.zhangchuangla.demo |
| `${moduleName}` | 模块名 | demo |
| `${ClassName}` | 类名（首字母大写） | UserInfo |
| `${className}` | 类名（首字母小写） | userInfo |
| `${tableName}` | 表名 | user_info |
| `${functionName}` | 功能名称 | 用户信息 |
| `${author}` | 作者 | 张创 |
| `${datetime}` | 生成日期 | 2025-01-23 |

### 字段变量

| 变量名 | 说明 |
|--------|------|
| `${columns}` | 字段列表 |
| `${pkColumn}` | 主键字段 |
| `${dicts}` | 字典字段列表 |

### 树表变量

| 变量名 | 说明 |
|--------|------|
| `${treeCode}` | 树节点ID字段 |
| `${treeParentCode}` | 父节点ID字段 |
| `${treeName}` | 节点名称字段 |

### 主子表变量

| 变量名 | 说明 |
|--------|------|
| `${subClassName}` | 子表类名 |
| `${subTableFkName}` | 子表外键字段 |

## 生成的代码结构

```
src/main/java/
├── controller/          # 控制器
├── service/            # 服务接口
├── service/impl/       # 服务实现
├── mapper/             # Mapper接口
├── model/
│   ├── entity/         # 实体类
│   ├── request/        # 请求DTO
│   └── vo/            # 响应VO
└── resources/
    └── mapper/         # Mapper XML
```

## Excel导出集成

生成的实体类自动添加Excel导出注解：

```java
@Data
@TableName("user_info")
public class UserInfo {
    
    @Excel(name = "用户ID", sort = 1)
    @TableId(type = IdType.AUTO)
    private Long userId;
    
    @Excel(name = "用户名", sort = 2)
    private String username;
    
    @Excel(name = "性别", sort = 3, dictType = "sys_user_sex")
    private String sex;
    
    @Excel(name = "创建时间", sort = 4, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
```

## 字典映射

支持自动识别字典字段并添加字典类型：

```java
// 在GenTableColumn中配置dictType
column.setDictType("sys_user_sex");

// 生成的实体类会自动添加dictType属性
@Excel(name = "性别", dictType = "sys_user_sex")
private String sex;
```

## 自定义模板

### 1. 创建模板文件

在 `src/main/resources/vm/java/` 目录下创建自定义模板：

```velocity
package ${packageName}.custom;

/**
 * ${functionName} 自定义类
 *
 * @author ${author}
 * @date ${datetime}
 */
public class ${ClassName}Custom {
    // 自定义代码
}
```

### 2. 注册模板

在 `VelocityUtils.getTemplateList()` 方法中添加自定义模板：

```java
templates.add("vm/java/custom.java.vm");
```

## 配置说明

### 数据库字段类型映射

| 数据库类型 | Java类型 |
|-----------|----------|
| varchar, text | String |
| int | Integer |
| bigint | Long |
| decimal, numeric | BigDecimal |
| datetime, timestamp | LocalDateTime |
| date | LocalDate |
| time | LocalTime |
| boolean, bit | Boolean |

### 表单控件映射

| 数据库类型 | 表单控件 |
|-----------|----------|
| text | textarea |
| datetime, timestamp | datetime |
| enum, set | select |
| 其他 | input |

## 注意事项

1. **表名规范**：建议使用下划线命名，如 `user_info`
2. **主键字段**：必须有主键字段，建议使用 `id` 作为主键
3. **字典配置**：需要在Redis中预先配置字典数据
4. **权限控制**：生成的Controller会添加权限注解，需要配置相应权限

## 扩展开发

### 添加新的模板类型

1. 在 `Constants.Generator` 中添加新的模板类型常量
2. 在 `VelocityUtils.setTemplateContext()` 中添加模板变量设置
3. 创建对应的模板文件
4. 在 `getTemplateList()` 中添加模板路径

### 自定义字段处理

在 `GenTableServiceImpl.processColumnInfo()` 方法中添加自定义字段处理逻辑：

```java
// 自定义字段处理
if ("custom_field".equals(column.getColumnName())) {
    column.setHtmlType("custom");
    column.setDictType("custom_dict");
}
``` 
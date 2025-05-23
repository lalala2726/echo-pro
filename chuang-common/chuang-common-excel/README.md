# Excel导出模块

基于FastExcel实现的Excel导出功能，支持字典映射、日期格式化、嵌套属性等特性。

## 功能特点

- ✅ **字典映射**：支持从Redis中获取字典数据进行值转换
- ✅ **日期格式化**：支持自定义日期格式
- ✅ **嵌套属性**：支持通过点号访问嵌套对象属性
- ✅ **列样式**：支持设置列宽、对齐方式、字体样式等
- ✅ **排序控制**：支持通过sort属性控制列的显示顺序
- ✅ **高性能**：基于FastExcel，支持大数据量导出
- ✅ **自动配置**：Spring Boot自动配置，开箱即用

## 快速开始

### 1. 添加依赖

在你的项目中添加Excel模块依赖：

```xml

<dependency>
    <groupId>cn.zhangchuangla</groupId>
    <artifactId>chuang-common-excel</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 定义导出实体

使用`@Excel`注解标记需要导出的字段：

```java

@Data
public class UserExportVO {

    @Excel(name = "用户ID", sort = 1, width = 15)
    private Long userId;

    @Excel(name = "用户名", sort = 2, width = 20)
    private String username;

    @Excel(name = "性别", sort = 3, width = 10, dictType = "sys_user_sex")
    private String sex;

    @Excel(name = "状态", sort = 4, width = 15, dictType = "sys_normal_disable")
    private String status;

    @Excel(name = "部门", sort = 5, width = 25, targetAttr = "dept.deptName")
    private String deptName;

    @Excel(name = "创建时间", sort = 6, width = 25, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 嵌套对象
    private Dept dept;
}
```

### 3. 在Controller中使用

```java

@RestController
@RequiredArgsConstructor
public class UserController {

    private final ExcelUtils excelUtils;
    private final UserService userService;

    @GetMapping("/export")
    public void exportUsers(HttpServletResponse response) {
        List<UserExportVO> users = userService.getAllUsers();
        excelUtils.exportExcel(response, users, UserExportVO.class, "用户列表");
    }
}
```

## 注解属性说明

### @Excel注解属性

| 属性                | 类型         | 默认值               | 说明                          |
|-------------------|------------|-------------------|-----------------------------|
| `name`            | String     | ""                | 列标题名称                       |
| `sort`            | int        | Integer.MAX_VALUE | 列排序序号（升序）                   |
| `width`           | double     | 16                | 列宽度（字符单位）                   |
| `height`          | double     | 14                | 行高度（字符单位）                   |
| `dictType`        | String     | ""                | 字典类型编码，用于字典值映射              |
| `dateFormat`      | String     | ""                | 日期格式，如：yyyy-MM-dd HH:mm:ss  |
| `targetAttr`      | String     | ""                | 目标属性路径，支持多级，如：dept.deptName |
| `type`            | ColumnType | STRING            | 列类型：NUMERIC、STRING、IMAGE    |
| `align`           | Align      | AUTO              | 对齐方式：AUTO、LEFT、CENTER、RIGHT |
| `defaultValue`    | String     | ""                | 默认值                         |
| `suffix`          | String     | ""                | 后缀，如：%                      |
| `isExport`        | boolean    | true              | 是否导出                        |
| `isBold`          | boolean    | false             | 是否加粗                        |
| `color`           | String     | ""                | 字体颜色                        |
| `backgroundColor` | String     | ""                | 背景颜色                        |

## 字典映射

### 1. 字典数据格式

字典数据需要存储在Redis中，格式为：

```
Key: dict:cache:{dictType}
Value: List<Option<String>>
```

其中`Option`对象包含：

- `value`：字典值
- `label`：字典标签

### 2. 使用示例

```java
// 性别字典：sys_user_sex
// Redis中存储：
// dict:cache:sys_user_sex -> [
//   {value: "0", label: "男"},
//   {value: "1", label: "女"},
//   {value: "2", label: "未知"}
// ]

@Excel(name = "性别", dictType = "sys_user_sex")
private String sex; // 值为"0"时，导出显示为"男"
```

### 3. 字典缓存

- 支持本地缓存，避免频繁访问Redis
- 支持预加载，提高导出性能
- 支持缓存清理

## 高级特性

### 1. 嵌套属性访问

```java

@Excel(name = "部门名称", targetAttr = "dept.deptName")
private String deptName;

@Excel(name = "上级部门", targetAttr = "dept.parent.deptName")
private String parentDeptName;
```

### 2. 日期格式化

```java

@Excel(name = "创建时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
private LocalDateTime createTime;

@Excel(name = "生日", dateFormat = "yyyy-MM-dd")
private LocalDate birthday;
```

### 3. 数字格式化

```java

@Excel(name = "金额", numFormat = "%.2f", suffix = "元")
private BigDecimal amount;
```

## API参考

### ExcelUtils

```java
// 导出到HTTP响应
void exportExcel(HttpServletResponse response, List<T> data, Class<T> clazz, String fileName)

// 导出到输出流
void exportExcel(OutputStream outputStream, List<T> data, Class<T> clazz, String sheetName)

// 导出到字节数组
byte[] exportExcelToBytes(List<T> data, Class<T> clazz, String sheetName)
```

### DictDataHandler

```java
// 获取字典标签
String getDictLabel(String dictType, String dictValue)

// 清除缓存
void clearCache(String dictType)

// 预加载字典数据
void preloadDictData(List<String> dictTypes)
```

## 性能优化

1. **预加载字典**：导出前会自动预加载所有需要的字典数据
2. **本地缓存**：字典数据会缓存在本地，避免重复访问Redis
3. **流式处理**：基于FastExcel的流式API，支持大数据量导出
4. **批量写入**：优化写入性能

## 注意事项

1. 确保Redis中存在对应的字典数据
2. 日期格式需要与实际数据类型匹配
3. 嵌套属性路径需要确保对象不为null
4. 大数据量导出时注意内存使用

## 示例代码

完整的使用示例请参考：

- `UserExportExample.java` - 实体类示例
- `ExcelExportController.java` - 控制器示例 
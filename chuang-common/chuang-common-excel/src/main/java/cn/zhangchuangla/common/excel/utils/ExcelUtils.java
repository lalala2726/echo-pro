package cn.zhangchuangla.common.excel.utils;

import org.apache.commons.collections4.CollectionUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

import java.time.format.DateTimeFormatter;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.StringUtils;
import cn.zhangchuangla.common.excel.annotation.Excel;
import cn.zhangchuangla.common.excel.core.DictDataHandler;
import cn.zhangchuangla.common.excel.core.ExcelField;
import cn.zhangchuangla.common.excel.exception.ExcelException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Excel导出工具类
 * 基于FastExcel实现，支持字典映射
 *
 * @author Chuang
 * @since 2025-01-23
 */
@Slf4j
@Component
public class ExcelUtils {

    @Autowired
    private DictDataHandler dictDataHandler;

    /**
     * 导出Excel到响应流
     *
     * @param response 响应对象
     * @param data     数据列表
     * @param clazz    数据类型
     * @param fileName 文件名
     * @param <T>      数据类型
     */
    public <T> void exportExcel(HttpServletResponse response, List<T> data, Class<T> clazz, String fileName) {
        try {
            // 设置响应头
            setResponseHeader(response, fileName);

            // 导出到响应流
            exportExcel(response.getOutputStream(), data, clazz, fileName);
        } catch (IOException e) {
            log.error("导出Excel失败", e);
            throw new ExcelException("导出Excel失败");
        }
    }

    /**
     * 导出Excel到输出流
     *
     * @param outputStream 输出流
     * @param data         数据列表
     * @param clazz        数据类型
     * @param sheetName    工作表名称
     * @param <T>          数据类型
     */
    public <T> void exportExcel(OutputStream outputStream, List<T> data, Class<T> clazz, String sheetName) {
        try (Workbook workbook = new Workbook(outputStream, "ExcelApp", "1.0")) {
            Worksheet worksheet = workbook.newWorksheet(sheetName);

            // 获取Excel字段信息
            List<ExcelField> excelFields = getExcelFields(clazz);
            if (CollectionUtils.isEmpty(excelFields)) {
                log.warn("类 {} 中没有找到@Excel注解的字段", clazz.getSimpleName());
                return;
            }

            // 预加载字典数据
            preloadDictData(excelFields);

            // 写入表头
            writeHeader(worksheet, excelFields);

            // 写入数据
            writeData(worksheet, data, excelFields);

            // 设置列宽
            setColumnWidth(worksheet, excelFields);

        } catch (IOException e) {
            log.error("导出Excel失败", e);
            throw new ExcelException("导出Excel失败");
        }
    }

    /**
     * 导出Excel到字节数组
     *
     * @param data      数据列表
     * @param clazz     数据类型
     * @param sheetName 工作表名称
     * @param <T>       数据类型
     * @return Excel文件字节数组
     */
    public <T> byte[] exportExcelToBytes(List<T> data, Class<T> clazz, String sheetName) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            exportExcel(outputStream, data, clazz, sheetName);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("导出Excel到字节数组失败", e);
            throw new RuntimeException("导出Excel到字节数组失败", e);
        }
    }

    /**
     * 获取类中的Excel字段信息
     *
     * @param clazz 类型
     * @param <T>   类型参数
     * @return Excel字段列表
     */
    private <T> List<ExcelField> getExcelFields(Class<T> clazz) {
        List<ExcelField> excelFields = new ArrayList<>();

        Field[] fields = FieldUtils.getAllFieldsList(clazz).toArray(new Field[0]);
        for (Field field : fields) {
            Excel excel = field.getAnnotation(Excel.class);
            if (excel != null && excel.isExport()) {
                field.setAccessible(true);
                excelFields.add(new ExcelField(field, excel));
            }
        }

        // 按sort排序
        excelFields.sort(Comparator.comparingInt(ExcelField::getSort));

        return excelFields;
    }

    /**
     * 预加载字典数据
     *
     * @param excelFields Excel字段列表
     */
    private void preloadDictData(List<ExcelField> excelFields) {
        List<String> dictTypes = excelFields.stream()
                .map(ExcelField::getDictType)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(dictTypes)) {
            dictDataHandler.preloadDictData(dictTypes);
        }
    }

    /**
     * 写入表头
     *
     * @param worksheet   工作表
     * @param excelFields Excel字段列表
     */
    private void writeHeader(Worksheet worksheet, List<ExcelField> excelFields) {
        for (int i = 0; i < excelFields.size(); i++) {
            ExcelField excelField = excelFields.get(i);
            String title = StringUtils.isNotBlank(excelField.getTitle()) ? excelField.getTitle() : excelField.getFieldName();

            // 写入表头
            worksheet.value(0, i, title);

            // 设置表头样式
            if (excelField.isBold()) {
                worksheet.style(0, i).bold().set();
            }

            if (StringUtils.isNotBlank(excelField.getColor())) {
                // 这里可以根据需要设置字体颜色
            }

            if (StringUtils.isNotBlank(excelField.getBackgroundColor())) {
                // 这里可以根据需要设置背景颜色
            }
        }
    }

    /**
     * 写入数据
     *
     * @param worksheet   工作表
     * @param data        数据列表
     * @param excelFields Excel字段列表
     * @param <T>         数据类型
     */
    private <T> void writeData(Worksheet worksheet, List<T> data, List<ExcelField> excelFields) {
        if (CollectionUtils.isEmpty(data)) {
            return;
        }

        for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
            T item = data.get(rowIndex);
            // Excel行索引从1开始（0是表头）
            int excelRowIndex = rowIndex + 1;

            for (int colIndex = 0; colIndex < excelFields.size(); colIndex++) {
                ExcelField excelField = excelFields.get(colIndex);
                Object value = getFieldValue(item, excelField);

                // 处理字典映射
                String cellValue = processCellValue(value, excelField);

                // 写入单元格
                if (cellValue != null) {
                    // 根据数据类型写入不同格式的值
                    if (excelField.getColumnType() == Excel.ColumnType.NUMERIC && isNumeric(cellValue)) {
                        try {
                            worksheet.value(excelRowIndex, colIndex, Double.parseDouble(cellValue));
                        } catch (NumberFormatException e) {
                            worksheet.value(excelRowIndex, colIndex, cellValue);
                        }
                    } else {
                        worksheet.value(excelRowIndex, colIndex, cellValue);
                    }
                }
            }
        }
    }

    /**
     * 获取字段值
     *
     * @param item       数据对象
     * @param excelField Excel字段信息
     * @return 字段值
     */
    private Object getFieldValue(Object item, ExcelField excelField) {
        try {
            if (StringUtils.isNotBlank(excelField.getTargetAttr())) {
                // 支持多级属性获取
                return getNestedFieldValue(item, excelField.getTargetAttr());
            } else {
                return FieldUtils.readField(excelField.getField(), item, true);
            }
        } catch (Exception e) {
            log.warn("获取字段值失败: {}", excelField.getFieldName(), e);
            return null;
        }
    }

    /**
     * 获取嵌套字段值
     *
     * @param item     数据对象
     * @param attrPath 属性路径，用.分隔
     * @return 字段值
     */
    private Object getNestedFieldValue(Object item, String attrPath) {
        if (item == null || StringUtils.isBlank(attrPath)) {
            return null;
        }

        String[] attrs = attrPath.split("\\.");
        Object currentValue = item;

        for (String attr : attrs) {
            if (currentValue == null) {
                return null;
            }
            try {
                currentValue = FieldUtils.readField(currentValue, attr, true);
            } catch (IllegalAccessException e) {
                log.warn("无法读取字段 {}", attr, e);
                return null;
            }
        }

        return currentValue;
    }

    /**
     * 处理单元格值
     *
     * @param value      原始值
     * @param excelField Excel字段信息
     * @return 处理后的字符串值
     */
    private String processCellValue(Object value, ExcelField excelField) {
        if (value == null) {
            return StringUtils.isNotBlank(excelField.getDefaultValue()) ? excelField.getDefaultValue() : "";
        }

        String cellValue = convertToString(value, excelField);

        // 字典映射
        if (StringUtils.isNotBlank(excelField.getDictType())) {
            cellValue = dictDataHandler.getDictLabel(excelField.getDictType(), cellValue);
        }

        // 添加后缀
        if (StringUtils.isNotBlank(excelField.getSuffix())) {
            cellValue += excelField.getSuffix();
        }

        return cellValue;
    }

    /**
     * 将值转换为字符串
     *
     * @param value      原始值
     * @param excelField Excel字段信息
     * @return 字符串值
     */
    private String convertToString(Object value, ExcelField excelField) {
        if (value == null) {
            return "";
        }

        // 日期格式化
        if (value instanceof Date) {
            String dateFormat = StringUtils.isNotBlank(excelField.getDateFormat()) ? excelField.getDateFormat()
                    : "yyyy-MM-dd HH:mm:ss";
            return new SimpleDateFormat(dateFormat).format((Date) value);
        }

        if (value instanceof LocalDateTime) {
            String dateFormat = StringUtils.isNotBlank(excelField.getDateFormat()) ? excelField.getDateFormat()
                    : "yyyy-MM-dd HH:mm:ss";
            return ((LocalDateTime) value).format(DateTimeFormatter.ofPattern(dateFormat));
        }

        if (value instanceof LocalDate) {
            String dateFormat = StringUtils.isNotBlank(excelField.getDateFormat()) ? excelField.getDateFormat() : "yyyy-MM-dd";

            return ((LocalDate) value).format(DateTimeFormatter.ofPattern(dateFormat));
        }

        // 数字格式化
        if (value instanceof BigDecimal && StringUtils.isNotBlank(excelField.getNumFormat())) {
            return String.format(excelField.getNumFormat(), value);
        }

        return String.valueOf(value);
    }

    /**
     * 设置列宽
     *
     * @param worksheet   工作表
     * @param excelFields Excel字段列表
     */
    private void setColumnWidth(Worksheet worksheet, List<ExcelField> excelFields) {
        for (int i = 0; i < excelFields.size(); i++) {
            ExcelField excelField = excelFields.get(i);
            if (excelField.getWidth() > 0) {
                worksheet.width(i, excelField.getWidth());
            }
        }
    }

    /**
     * 设置响应头
     *
     * @param response 响应对象
     * @param fileName 文件名
     */
    private void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");

            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
            response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName + ".xlsx");
        } catch (Exception e) {
            log.error("设置响应头失败", e);
        }
    }

    /**
     * 判断字符串是否为数字
     *
     * @param str 字符串
     * @return 是否为数字
     */
    private boolean isNumeric(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
package cn.zhangchuangla.common.excel.utils;

import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.excel.annotation.Excel;
import cn.zhangchuangla.common.excel.core.DictDataHandler;
import cn.zhangchuangla.common.excel.core.ExcelField;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
 */
@Slf4j
@Component
public class ExcelExporter {


    /**
     * 默认每个Sheet的最大数据行数（不含表头/提示行）
     */
    private static final int DEFAULT_MAX_ROWS_PER_SHEET = 50000;
    private final DictDataHandler dictDataHandler;

    public ExcelExporter(DictDataHandler dictDataHandler) {
        this.dictDataHandler = dictDataHandler;
    }

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
            throw new ServiceException("导出Excel失败");
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
            // 获取Excel字段信息，传入数据用于判断对象展开
            List<ExcelField> excelFields = getExcelFields(clazz, data);
            if (CollectionUtils.isEmpty(excelFields)) {
                log.warn("类 {} 中没有找到@Excel注解的字段", clazz.getSimpleName());
                return;
            }

            // 预加载字典数据
            preloadDictData(excelFields);

            // 计算是否需要提示行
            boolean hasHints = excelFields.stream().anyMatch(f -> {
                if (f.getExcel() == null) {
                    return false;
                }
                return StringUtils.isNotBlank(f.getExcel().prompt()) || (f.getExcel().combo() != null && f.getExcel().combo().length > 0);
            });

            int total = data == null ? 0 : data.size();
            int sheetCount = Math.max(1, (int) Math.ceil(total / (double) DEFAULT_MAX_ROWS_PER_SHEET));

            for (int s = 0; s < sheetCount; s++) {
                String curSheetName = sheetName + (sheetCount > 1 ? ("_" + (s + 1)) : "");
                Worksheet worksheet = workbook.newWorksheet(curSheetName);

                int fromIndex = s * DEFAULT_MAX_ROWS_PER_SHEET;
                int toIndex = Math.min(fromIndex + DEFAULT_MAX_ROWS_PER_SHEET, total);
                List<T> pageData = data == null ? java.util.Collections.emptyList() : data.subList(fromIndex, toIndex);

                // 行索引计算
                int headerRowIndex = 0;
                int hintRowIndex = hasHints ? 1 : -1;
                int dataStartRow = hasHints ? 2 : 1;

                // 写入表头
                writeHeader(worksheet, excelFields, headerRowIndex);
                // 写入提示
                if (hasHints) {
                    writeHintsRow(worksheet, excelFields, hintRowIndex);
                }
                // 写入数据
                int lastRowIndex = writeData(worksheet, pageData, excelFields, dataStartRow);
                // 写入统计行
                writeStatisticsRow(worksheet, pageData, excelFields, lastRowIndex + 1);
                // 设置列宽
                setColumnWidth(worksheet, excelFields);
            }

        } catch (IOException e) {
            log.error("导出Excel失败", e);
            throw new ServiceException("导出Excel失败");
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
            throw new ServiceException("导出Excel失败");
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
        return getExcelFields(clazz, null);
    }

    /**
     * 获取类中的Excel字段信息
     *
     * @param clazz      类型
     * @param sampleData 样本数据，用于判断对象是否需要展开
     * @param <T>        类型参数
     * @return Excel字段列表
     */
    private <T> List<ExcelField> getExcelFields(Class<T> clazz, List<T> sampleData) {
        List<ExcelField> excelFields = new ArrayList<>();

        Field[] fields = FieldUtils.getAllFieldsList(clazz).toArray(new Field[0]);
        for (Field field : fields) {
            // 过滤 static/transient 字段
            int mod = field.getModifiers();
            if (java.lang.reflect.Modifier.isStatic(mod) || java.lang.reflect.Modifier.isTransient(mod)) {
                continue;
            }
            Excel excel = field.getAnnotation(Excel.class);
            if (excel != null && excel.isExport()) {
                field.setAccessible(true);

                // 如果需要展开对象，处理对象字段
                if (excel.expandObject()) {
                    // 检查是否需要展开（根据 expandIsNullExport 和实际数据）
                    boolean shouldExpand = excel.expandIsNullExport() || hasNonNullObjectInData(field, sampleData);

                    if (shouldExpand) {
                        List<ExcelField> expandedFields = getExpandedFields(field, excel.expandPrefix(), excel.sort());
                        excelFields.addAll(expandedFields);
                    }
                } else {
                    excelFields.add(new ExcelField(field, excel));
                }
            }
        }

        // 按sort排序
        excelFields.sort(Comparator.comparingInt(ExcelField::getSort));

        return excelFields;
    }

    /**
     * 检查数据中是否有非null的对象
     *
     * @param field      字段
     * @param sampleData 样本数据
     * @param <T>        数据类型
     * @return 是否有非null对象
     */
    private <T> boolean hasNonNullObjectInData(Field field, List<T> sampleData) {
        if (CollectionUtils.isEmpty(sampleData)) {
            return false;
        }

        for (T item : sampleData) {
            try {
                Object value = FieldUtils.readField(field, item, true);
                if (value != null) {
                    return true;
                }
            } catch (Exception e) {
                log.warn("检查字段值失败: {}", field.getName(), e);
            }
        }
        return false;
    }

    /**
     * 获取展开对象的字段列表
     *
     * @param parentField 父字段（被展开的对象字段）
     * @param prefix      列名前缀
     * @param parentSort  父字段的排序值
     * @return 展开的字段列表
     */
    private List<ExcelField> getExpandedFields(Field parentField, String prefix, int parentSort) {
        List<ExcelField> expandedFields = new ArrayList<>();

        Field[] fields = FieldUtils.getAllFieldsList(parentField.getType()).toArray(new Field[0]);
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            Excel excel = field.getAnnotation(Excel.class);
            if (excel != null && excel.isExport()) {
                field.setAccessible(true);

                // 创建展开的字段，修改标题和字段路径
                ExcelField expandedField = new ExcelField(field, excel);

                // 设置展开字段的标题（添加前缀）
                String originalTitle = StringUtils.isNotBlank(excel.name()) ? excel.name() : field.getName();
                String expandedTitle = StringUtils.isNotBlank(prefix) ? prefix + originalTitle : originalTitle;
                expandedField.setTitle(expandedTitle);

                // 设置字段路径，用于后续获取值时使用 - 格式为 "parentField.childField"
                expandedField.setTargetAttr(parentField.getName() + "." + field.getName());

                // 设置排序值：父排序权重大，子字段次之，减少冲突
                int base = (parentSort == Integer.MAX_VALUE ? 0 : parentSort) * 1000;
                int childSort = excel.sort() == Integer.MAX_VALUE ? i : excel.sort();
                int expandedSort = base + childSort;
                expandedField.setSort(expandedSort);

                expandedFields.add(expandedField);
            }
        }

        // 按sort排序
        expandedFields.sort(Comparator.comparingInt(ExcelField::getSort));

        return expandedFields;
    }

    /**
     * 预加载字典数据
     *
     * @param excelFields Excel字段列表
     */
    private void preloadDictData(List<ExcelField> excelFields) {
        List<String> dictKeys = excelFields.stream()
                .map(ExcelField::getDictKey)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(dictKeys)) {
            dictDataHandler.preloadDictData(dictKeys);
        }
    }

    /**
     * 写入表头
     *
     * @param worksheet   工作表
     * @param excelFields Excel字段列表
     */
    private void writeHeader(Worksheet worksheet, List<ExcelField> excelFields, int rowIndex) {
        for (int i = 0; i < excelFields.size(); i++) {
            ExcelField excelField = excelFields.get(i);
            String title = StringUtils.isNotBlank(excelField.getTitle()) ? excelField.getTitle() : excelField.getFieldName();

            // 写入表头
            worksheet.value(rowIndex, i, title);

            // 设置表头样式
            if (excelField.isBold()) {
                worksheet.style(rowIndex, i).bold().set();
            }
        }
    }

    /**
     * 写入提示/下拉枚举行（仅文本提示，不做数据有效性约束）
     */
    private void writeHintsRow(Worksheet worksheet, List<ExcelField> excelFields, int rowIndex) {
        for (int i = 0; i < excelFields.size(); i++) {
            ExcelField field = excelFields.get(i);
            if (field.getExcel() == null) {
                continue;
            }
            String prompt = field.getExcel().prompt();
            String[] combo = field.getExcel().combo();
            String hint = null;
            if (StringUtils.isNotBlank(prompt)) {
                hint = prompt;
            }
            if (combo != null && combo.length > 0) {
                String options = String.join(",", combo);
                hint = (hint == null ? "可选:" : hint + " 可选:") + options;
            }
            if (StringUtils.isNotBlank(hint)) {
                worksheet.value(rowIndex, i, hint);
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
    private <T> int writeData(Worksheet worksheet, List<T> data, List<ExcelField> excelFields, int startRow) {
        if (CollectionUtils.isEmpty(data)) {
            return startRow - 1;
        }

        for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
            T item = data.get(rowIndex);
            // Excel行索引从1开始（0是表头）
            int excelRowIndex = startRow + rowIndex;

            for (int colIndex = 0; colIndex < excelFields.size(); colIndex++) {
                ExcelField excelField = excelFields.get(colIndex);
                Object value = getFieldValue(item, excelField);

                // 处理字典映射与显示值
                String cellValue = processCellValue(value, excelField);

                // 优先按原始类型写入数值，避免精度丢失；字符串兜底
                if (excelField.getColumnType() == Excel.ColumnType.IMAGE) {
                    // 图片类型：当前简化为写入占位/URL 文本（FastExcel不直接支持图片绘制）
                    if (value instanceof String) {
                        worksheet.value(excelRowIndex, colIndex, (String) value);
                    } else {
                        worksheet.value(excelRowIndex, colIndex, "[image]");
                    }
                } else if (value instanceof Number && excelField.getColumnType() == Excel.ColumnType.NUMERIC) {
                    worksheet.value(excelRowIndex, colIndex, ((Number) value).doubleValue());
                } else if (cellValue != null) {
                    worksheet.value(excelRowIndex, colIndex, cellValue);
                }
            }
        }
        return startRow + data.size() - 1;
    }

    /**
     * 写入统计行（对开启 isStatistics 的列求和）
     */
    private <T> void writeStatisticsRow(Worksheet worksheet, List<T> data, List<ExcelField> excelFields, int rowIndex) {
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        boolean hasStats = excelFields.stream().anyMatch(f -> f.getExcel() != null && f.getExcel().isStatistics());
        if (!hasStats) {
            return;
        }
        boolean titleWritten = false;
        for (int colIndex = 0; colIndex < excelFields.size(); colIndex++) {
            ExcelField field = excelFields.get(colIndex);
            if (field.getExcel() != null && field.getExcel().isStatistics()) {
                java.math.BigDecimal sum = java.math.BigDecimal.ZERO;
                for (T item : data) {
                    Object v = getFieldValue(item, field);
                    if (v instanceof Number) {
                        sum = sum.add(new java.math.BigDecimal(((Number) v).toString()));
                    } else if (v instanceof String && isNumeric((String) v)) {
                        sum = sum.add(new java.math.BigDecimal((String) v));
                    }
                }
                worksheet.value(rowIndex, colIndex, sum.doubleValue());
                if (!titleWritten) {
                    worksheet.value(rowIndex, 0, "合计");
                    titleWritten = true;
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
                // 支持多级属性获取，包括展开对象的属性
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
        if (StringUtils.isNotBlank(excelField.getDictKey())) {
            cellValue = dictDataHandler.getDictLabel(excelField.getDictKey(), cellValue);
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

        // 数字格式化（优先使用 DecimalFormat 模式，如 0.00）
        if (StringUtils.isNotBlank(excelField.getNumFormat()) && value instanceof Number) {
            try {
                java.text.DecimalFormat df = new java.text.DecimalFormat(excelField.getNumFormat());
                return df.format(((Number) value).doubleValue());
            } catch (IllegalArgumentException ignore) {
                // 回退到默认 toString
            }
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

            String base = fileName.endsWith(".xlsx") ? fileName.substring(0, fileName.length() - 5) : fileName;
            String encoded = URLEncoder.encode(base, StandardCharsets.UTF_8);
            // 兼容 RFC 5987
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + encoded + ".xlsx; filename*=UTF-8''" + encoded + ".xlsx");
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

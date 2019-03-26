package com.biubiu.excel;

import com.biubiu.constants.DatabaseType;
import com.biubiu.datasource.DatabaseContextHolder;
import com.biubiu.mapper.TaskMapper;
import com.biubiu.po.Task;
import com.biubiu.util.CustomBeanUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Haibiao.Zhang on 2019-03-26 11:33
 */
public class ExcelUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtil.class);

    private static final String WINDOW_PATH = "E:/tmp/";

    private static final String LINUX_PATH = "/tmp/";

    private static final String PATTERN = "yyyyMMddHHmmss";

    private static final String EXCEL_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final String START_NUM = "startNum";

    private static final String END_NUM = "endNum";

    private static final String XLSX = ".xlsx";

    private static final String ZIP = ".zip";

    private static final int MAX_ROW_COUNT = 10000;

    private static final int ROW_MEMORY = 1000;

    /**
     * 导出excel
     *
     * @param excelExportParams 下载参数
     * @param taskMapper
     * @throws Exception 异常
     */
    public static void excelExport(ExcelExportParams excelExportParams, TaskMapper taskMapper) throws Exception {
        String taskId = excelExportParams.getTaskId();
        String fileName = excelExportParams.getFileName();
        Map<String, Object> params = excelExportParams.getParams();
        ExportService exportService = excelExportParams.getExportService();
        Class clazz = excelExportParams.getClazz();
        List<String> assetHeadTemp = excelExportParams.getAssetHeadTemp();
        List<String> assetNameTemp = excelExportParams.getAssetNameTemp();

        int allRowNumbers = exportService.queryCountByMap(params);

        //设置批次文件名
        List<String> fileNames = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);
        String filePath;
        if (isWindows()) {
            filePath = WINDOW_PATH;
        } else {
            filePath = LINUX_PATH;
        }
        if (!new File(filePath).exists()) {
            new File(filePath).mkdirs();
        }
        String fileEnd = sdf.format(new Date());
        fileName = fileName + fileEnd;
        File zip = new File(filePath + fileName + ZIP);//压缩文件路径

        if (allRowNumbers > MAX_ROW_COUNT) {
            //分批次生成excel
            int count = allRowNumbers / MAX_ROW_COUNT;
            int tempSize = (allRowNumbers % MAX_ROW_COUNT) == 0 ? count : count + 1;
            for (int i = 0; i < tempSize; i++) {
                if (i == (allRowNumbers / MAX_ROW_COUNT)) {
                    params.put(START_NUM, i * MAX_ROW_COUNT);
                    params.put(END_NUM, MAX_ROW_COUNT);
                } else {
                    params.put(START_NUM, i * MAX_ROW_COUNT);
                    params.put(END_NUM, MAX_ROW_COUNT);
                }
                List result = excelExportParams.getExportService().queryResultByMap(params);
                List<Map> listMap = CustomBeanUtil.listBean2listMap(result, clazz);

                String tempExcelFile = filePath + fileName + "[" + (i + 1) + "]" + XLSX;
                File tempFile = new File(tempExcelFile);
                if (!tempFile.exists()) {
                    tempFile.createNewFile();
                }
                fileNames.add(tempExcelFile);
                FileOutputStream fos = new FileOutputStream(tempExcelFile);
                SXSSFWorkbook wb = new SXSSFWorkbook(ROW_MEMORY);
                doCreateWb(wb, listMap, fos, assetHeadTemp, assetNameTemp);
            }
        } else {
            params.put(START_NUM, 0);
            params.put(END_NUM, MAX_ROW_COUNT);
            List result = excelExportParams.getExportService().queryResultByMap(params);
            List<Map> listMap = CustomBeanUtil.listBean2listMap(result, clazz);

            String tempExcelFile = filePath + fileName + XLSX;
            File tempFile = new File(tempExcelFile);
            if (!tempFile.exists()) {
                tempFile.createNewFile();
            }
            fileNames.add(tempExcelFile);
            FileOutputStream fos = new FileOutputStream(tempExcelFile);
            SXSSFWorkbook wb = new SXSSFWorkbook(ROW_MEMORY);
            doCreateWb(wb, listMap, fos, assetHeadTemp, assetNameTemp);
        }
        //导出zip压缩文件
        exportZip(fileNames, zip, taskMapper, taskId);
    }

    private static boolean isWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().contains("WINDOWS");
    }

    private static void doCreateWb(SXSSFWorkbook wb, List<Map> listMap, FileOutputStream fos, List<String> assetHeadTemp, List<String> assetNameTemp) throws Exception {
        try {
            wb = exportDataToExcelXLSX(wb, listMap, assetHeadTemp, assetNameTemp);
            wb.write(fos);
            fos.flush();
        } catch (RuntimeException e) {
            throw new Exception(e);
        } finally {
            fos.flush();
            fos.close();
            listMap.clear();
        }
    }

    private static SXSSFWorkbook exportDataToExcelXLSX(SXSSFWorkbook wb, List<Map> listMap, List<String> assetHeadTemp, List<String> assetNameTemp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(EXCEL_PATTERN);
        CellStyle columnHeadStyle = wb.createCellStyle();
        columnHeadStyle.setWrapText(true);
        Font f = wb.createFont();// 字体
        f.setFontHeightInPoints((short) 9);// 字号
        columnHeadStyle.setFont(f);
        Sheet sheet = wb.createSheet("sheet");
        Row row = sheet.createRow(0);
        Cell cell;
        sheet.createFreezePane(0, 1, 0, 1);
        for (int i = 0; i < assetHeadTemp.size(); i++) {
            cell = row.createCell(i);
            cell.setCellStyle(columnHeadStyle);
            cell.setCellValue(assetHeadTemp.get(i));
            sheet.setColumnWidth(i, 3000);
        }
        if (listMap != null && listMap.size() > 0) {
            int rowIndex = 1;
            Object value;
            for (Map map : listMap) {
                row = sheet.createRow(rowIndex++);
                int index = 0;
                for (String anAssetNameTemp : assetNameTemp) {
                    cell = row.createCell(index++);
                    value = map.get(anAssetNameTemp);
                    if (value instanceof Date) {
                        cell.setCellValue(dateFormat.format(value));
                    } else {
                        cell.setCellValue(value != null ? value.toString() : "");
                    }
                }
            }
        }
        return wb;
    }

    private static void exportZip(List<String> fileNames, File zip, TaskMapper taskMapper, String taskId) throws IOException {
        //1.压缩文件
        File[] srcFile = new File[fileNames.size()];
        for (int i = 0; i < fileNames.size(); i++) {
            srcFile[i] = new File(fileNames.get(i));
        }
        byte[] byt = new byte[1024];
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zip), Charset.forName("UTF-8"));
        for (File aSrcFile : srcFile) {
            FileInputStream in = new FileInputStream(aSrcFile);
            out.putNextEntry(new ZipEntry(aSrcFile.getName()));
            int length;
            while ((length = in.read(byt)) > 0) {
                out.write(byt, 0, length);
            }
            out.closeEntry();
            in.close();
        }
        out.close();

        //2.删除服务器上的临时文件(excel)
        for (File temFile : srcFile) {
            if (temFile.exists() && temFile.isFile()) {
                temFile.delete();
            }
        }

        updateTaskEndTime(taskMapper, taskId, zip.getAbsolutePath());
    }

    private static void updateTaskEndTime(TaskMapper taskMapper, String taskId, String path) {
        Task task = Task.builder().id(taskId).filePath(path).endTime(new Date()).build();
        try {
            DatabaseContextHolder.setDatabaseType(DatabaseType.SLAVE);
            if (taskMapper.updateByPrimaryKeySelective(task) == 0) {
                LOGGER.error("update task [taskId : {}] filePath or end time error", taskId);
            }
        } finally {
            DatabaseContextHolder.clearDatabaseType();
        }
    }


}

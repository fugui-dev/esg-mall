package org.example.user.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Data;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class OverIssueDataProcessor {
    
    @Data
    public static class OverIssueData {
        @ExcelProperty(index = 0)
        private String address;
        
        @ExcelProperty(index = 3)
        private String amount;
    }
    
    public static void main(String[] args) {
        String inputFile = "src/main/resources/多发统计.xlsx";
        String outputFile = "src/main/resources/db/migration/V2__insert_over_issue_records.sql";
        
        List<OverIssueData> dataList = new ArrayList<>();
        
        // 读取Excel
        EasyExcel.read(inputFile, OverIssueData.class, new AnalysisEventListener<OverIssueData>() {
            @Override
            public void invoke(OverIssueData data, AnalysisContext context) {
                if (data.getAddress() != null && data.getAddress().startsWith("0x")) {
                    dataList.add(data);
                }
            }
            
            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                System.out.println("Excel读取完成，共读取 " + dataList.size() + " 条记录");
            }
        }).sheet().doRead();
        
        // 生成SQL
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
            
            writer.println("-- 插入超发记录数据");
            writer.println("INSERT INTO over_issue_record");
            writer.println("(address, amount, remain_amount, status, create_time, update_time)");
            writer.println("VALUES");
            
            for (int i = 0; i < dataList.size(); i++) {
                OverIssueData data = dataList.get(i);
                writer.print(String.format("('%s', '%s', '%s', 'undeducted', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000)",
                        data.getAddress(), data.getAmount(), data.getAmount()));
                        
                if (i < dataList.size() - 1) {
                    writer.println(",");
                } else {
                    writer.println(";");
                }
            }
            
            System.out.println("SQL生成完成!");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 
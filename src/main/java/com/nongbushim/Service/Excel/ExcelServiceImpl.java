package com.nongbushim.Service.Excel;

import com.nongbushim.Dto.KamisResponse.Daily.DailyItemDto;
import com.nongbushim.Dto.KamisResponse.Monthly.MonthlyItemDto;
import com.nongbushim.Dto.WholesaleInfo.WholesaleDailyInfoDto;
import com.nongbushim.Dto.WholesaleInfo.WholesaleInfoDto;
import com.nongbushim.Dto.WholesaleInfo.WholesaleMonthlyInfoDto;
import com.nongbushim.Helper.MonthlyHelper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
public class ExcelServiceImpl implements ExcelService {

    private static final String MONTHLY_SHEET = "월별";
    private static final String DAILY_SHEET = "일별";
    private static final String MONTH_AVG_HEADER = "월평균";
    private static final String YEAR_AVG_HEADER = "연평균";
    private static final int IDX_YEAR_AVG = 13;

    private final LocalDate now = LocalDate.now();
    private final LocalDate startDate = now.minusYears(1);

    @Override
    public ByteArrayInputStream createExcel(List<WholesaleInfoDto> wholesaleMonthlyInfoList, List<WholesaleInfoDto> wholesaleDailyInfoList, String title) {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet monthlySheet = workbook.createSheet(MONTHLY_SHEET);
            Sheet dailySheet = workbook.createSheet(DAILY_SHEET);

            // Create monthly sheet
            createMonthlySheet(wholesaleMonthlyInfoList, title, monthlySheet);

            // Create daily sheet
            createDailySheet(wholesaleDailyInfoList, title, dailySheet);

            workbook.write(baos);
            workbook.close();
            return new ByteArrayInputStream(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }

    private void createDailySheet(List<WholesaleInfoDto> wholesaleDailyInfoList, String title, Sheet dailySheet) {
        int rowIdx = createTitle(title, dailySheet);

        LocalDate minStartDate = getMinStartDate(wholesaleDailyInfoList);
        LocalDate maxEndDate = getMaxEndDate(wholesaleDailyInfoList);

        // Row for Header
        Row headerRow = dailySheet.createRow(rowIdx++);
        Cell headerCell = headerRow.createCell(0);
        LocalDate baseDate = minStartDate;
        int dateIdx = 1;
        while (baseDate.isBefore(maxEndDate) || baseDate.isEqual(maxEndDate)) {
            if (baseDate.getDayOfWeek() == DayOfWeek.SATURDAY || baseDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                baseDate = baseDate.plusDays(1);
                continue;
            }
            headerCell = headerRow.createCell(dateIdx);
            if (dateIdx == 1) headerCell.setCellValue(baseDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
            else headerCell.setCellValue(baseDate.format(DateTimeFormatter.ofPattern("MM/dd")));
            baseDate = baseDate.plusDays(1);
            dateIdx++;
        }

        // Row for each day
        for (WholesaleInfoDto infoDto : wholesaleDailyInfoList) {
            WholesaleDailyInfoDto dto = (WholesaleDailyInfoDto) infoDto;
            String region = dto.getCountyCode().getName();

            dateIdx = 0;
            Row row = dailySheet.createRow(rowIdx++);
            Cell cell = row.createCell(dateIdx++);
            cell.setCellValue(region);

            baseDate = minStartDate;
            for (DailyItemDto currentItem : dto.getDailyItemList()) {
                LocalDate currentDate = getDate(currentItem);
                while (baseDate.isBefore(currentDate) || baseDate.isEqual(currentDate)) {
                    if (baseDate.getDayOfWeek() == DayOfWeek.SATURDAY || baseDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                        baseDate = baseDate.plusDays(1);
                        continue;
                    }

                    if (baseDate.isBefore(currentDate)) {
                        cell = row.createCell(dateIdx++);
                        cell.setCellValue("-");
                    } else if (baseDate.isEqual(currentDate)) {
                        cell = row.createCell(dateIdx++);
                        cell.setCellValue(currentItem.getPrice());
                    }
                    baseDate = baseDate.plusDays(1);
                }
            }
            rowIdx++;
        }
    }

    private LocalDate getMaxEndDate(List<WholesaleInfoDto> wholesaleDailyInfoList) {
        DailyItemDto maxEndDateItem = wholesaleDailyInfoList.stream().filter(WholesaleDailyInfoDto.class::isInstance)
                .map(WholesaleDailyInfoDto.class::cast)
                .map(WholesaleDailyInfoDto::getDailyItemList)
                .map(list -> {
                    int lastIdx = list.size() - 1;
                    return list.get(lastIdx);
                })
                .max(Comparator.comparing(this::getDate)).get();
        return getDate(maxEndDateItem);
    }

    private LocalDate getMinStartDate(List<WholesaleInfoDto> wholesaleDailyInfoList) {
        DailyItemDto minStartDateItem = wholesaleDailyInfoList.stream().filter(WholesaleDailyInfoDto.class::isInstance)
                .map(WholesaleDailyInfoDto.class::cast)
                .map(WholesaleDailyInfoDto::getDailyItemList)
                .map(list -> list.get(0))
                .min(Comparator.comparing(this::getDate)).get();

        return getDate(minStartDateItem);
    }

    private LocalDate getDate(DailyItemDto dto) {
        return LocalDate.parse(dto.getYyyy() + "/" + dto.getRegday(), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    private void createMonthlySheet(List<WholesaleInfoDto> wholesaleMonthlyInfoList, String title, Sheet monthlySheet) {
        int rowIdx = createTitle(title, monthlySheet);
        for (WholesaleInfoDto dto : wholesaleMonthlyInfoList) {
            WholesaleMonthlyInfoDto wholesaleMonthlyInfoDto = (WholesaleMonthlyInfoDto) dto;
            String region = wholesaleMonthlyInfoDto.getCountyCode().getName();

            // Row for Header
            Row headerRow = monthlySheet.createRow(rowIdx++);
            Cell headerCell = headerRow.createCell(0);
            headerCell.setCellValue(region);
            for (int monthIdx = 1; monthIdx <= 12; monthIdx++) {
                headerCell = headerRow.createCell(monthIdx);
                headerCell.setCellValue(monthIdx + MONTH_AVG_HEADER);
            }
            headerCell = headerRow.createCell(IDX_YEAR_AVG);
            headerCell.setCellValue(YEAR_AVG_HEADER);

            // Row for each year
            for (MonthlyItemDto item : wholesaleMonthlyInfoDto.getPrice().getItem()) {
                Row row = monthlySheet.createRow(rowIdx++);
                int monthIdx = 0;
                List<String> monthlyPriceList = MonthlyHelper.getYearMonthlyPriceList(item);

                Cell cell = row.createCell(monthIdx++);
                cell.setCellValue(item.getYyyy());

                for (String price : monthlyPriceList) {
                    cell = row.createCell(monthIdx++);
                    cell.setCellValue(price);
                }
                cell = row.createCell(monthIdx);
                cell.setCellValue(item.getYearavg());

            }
            rowIdx++;
        }
    }

    private int createTitle(String title, Sheet sheet) {
        int rowIdx = 0;
        Row titleRow = sheet.createRow(rowIdx);
        Cell titleCell = titleRow.createCell(rowIdx);
        titleCell.setCellValue(title);

        CellStyle cellStyle = titleRow.getSheet().getWorkbook().createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCell.setCellStyle(cellStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, 13));
        rowIdx++;

        return rowIdx;
    }

}

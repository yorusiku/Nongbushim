package com.nongbushim.Service.Excel;

import com.nongbushim.Dto.WholesaleInfo.WholesaleInfoDto;

import java.io.InputStream;
import java.util.List;

public interface ExcelService {
//    ByteArrayInputStream excel = null;
    InputStream createExcel(List<WholesaleInfoDto> wholesaleMonthlyInfoList, List<WholesaleInfoDto> wholesaleDailyInfoList, String title);
    void downloadExcel();
    InputStream getExcel();
}

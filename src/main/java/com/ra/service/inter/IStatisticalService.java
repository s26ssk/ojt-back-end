package com.ra.service.inter;

import com.ra.dto.request.SelectDateRequest;
import com.ra.dto.response.DailyReportResponse;
import com.ra.dto.response.StatisticalOrderNumberResponse;
import com.ra.dto.response.StatisticalResponse;
import com.ra.model.Users;

import java.util.List;

public interface IStatisticalService {

    List<DailyReportResponse> getDailyFromWarehouseCode(SelectDateRequest request);

    List<StatisticalOrderNumberResponse> getStatistical(SelectDateRequest request);

    List<StatisticalResponse> getStatisticalByUser(SelectDateRequest request, Users user);
}

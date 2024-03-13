package com.digiwin.code.coverage.backend.service;

import com.digiwin.code.coverage.backend.pojo.dto.ReportJacocoParam;

public interface ReportService {

    public void reportJacoco(ReportJacocoParam reportJacocoParam) throws Exception;
}

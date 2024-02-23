package com.digiwin.code.coverage.backend.service;

import com.digiwin.code.coverage.backend.dto.ReportJacocoParam;

public interface ReportService {

    public void reportJacoco(ReportJacocoParam reportJacocoParam);
}

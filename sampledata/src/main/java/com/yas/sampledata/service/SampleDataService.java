package com.yas.sampledata.service;

import com.yas.sampledata.utils.SqlScriptExecutor;
import com.yas.sampledata.viewmodel.SampleDataVm;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SampleDataService {
    private final DataSource productDataSource;
    private final DataSource mediaDataSource;
    private final SqlScriptExecutor sqlScriptExecutor;

    @Autowired
    public SampleDataService(@Qualifier("productDataSource") DataSource productDataSource,
                             @Qualifier("mediaDataSource") DataSource mediaDataSource,
                             SqlScriptExecutor sqlScriptExecutor) {
        this.productDataSource = productDataSource;
        this.mediaDataSource = mediaDataSource;
        this.sqlScriptExecutor = sqlScriptExecutor;
    }

    public SampleDataVm createSampleData() {
        sqlScriptExecutor.executeScriptsForSchema(productDataSource, "public", "classpath*:db/product/*.sql");
        sqlScriptExecutor.executeScriptsForSchema(mediaDataSource, "public", "classpath*:db/media/*.sql");
        return new SampleDataVm("Insert Sample Data successfully!");
    }
}

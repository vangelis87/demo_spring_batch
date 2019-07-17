package com.example.demospringbatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demospringbatch.processor.PersonaItemProcessor;

@RestController
public class JobLauncherController {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    private Job job;
    
    private static final Logger LOG = LoggerFactory.getLogger(JobLauncherController.class);

    @GetMapping("/hola")
    public void handle() throws Exception{
    	LOG.info("comenzo a los "+System.currentTimeMillis());
    	JobParameters params = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
        jobLauncher.run(job, params);
        LOG.info("terminó"+System.currentTimeMillis());
    }
}

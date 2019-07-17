package com.example.demospringbatch;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import com.example.demospringbatch.listener.JobListener;
import com.example.demospringbatch.model.Persona;
import com.example.demospringbatch.model.PersonaRowMapper;
import com.example.demospringbatch.processor.PersonaItemProcessor;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	
	@Autowired
	public JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	private static final Logger LOG = LoggerFactory.getLogger(BatchConfiguration.class);
	
	@Autowired
	private DataSource dataSource;
	
	@Bean
	@StepScope
	public JdbcPagingItemReader<Persona> reader(){
		  JdbcPagingItemReader<Persona> reader = new JdbcPagingItemReader<Persona>();
		  final SqlPagingQueryProviderFactoryBean sqlPagingQueryProviderFactoryBean = new SqlPagingQueryProviderFactoryBean();
		  try {
		  sqlPagingQueryProviderFactoryBean.setDataSource(dataSource);
			sqlPagingQueryProviderFactoryBean.setSelectClause("select id,  primer_nombre, segundo_nombre, telefono");
			sqlPagingQueryProviderFactoryBean.setFromClause("from persona");
			sqlPagingQueryProviderFactoryBean.setSortKey("id");
			reader.setQueryProvider(sqlPagingQueryProviderFactoryBean.getObject());
			reader.setDataSource(dataSource);
			reader.setPageSize(100);
			reader.setRowMapper(new PersonaRowMapper());
			
				reader.afterPropertiesSet();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			reader.setSaveState(true);
			return reader;
		  
		  

	}
	
	@Bean
	public PersonaItemProcessor processor() {
		return new PersonaItemProcessor();
	}
	
	@Bean
	@StepScope
	public FlatFileItemWriter<Persona> writer(){
		//Create writer instance
        FlatFileItemWriter<Persona> writer = new FlatFileItemWriter<>();
         
        //Set output file location
        writer.setResource(new FileSystemResource("d:/hola/aa"+System.currentTimeMillis()+".txt"));
         
        //All job repetitions should "append" to same output file
        writer.setAppendAllowed(true);
        
        //Name field values sequence based on object properties
        writer.setLineAggregator(new DelimitedLineAggregator<Persona>() {
            {
                setDelimiter(",");
                setFieldExtractor(new BeanWrapperFieldExtractor<Persona>() {
                    {
                        setNames(new String[] { "primerNombre", "segundoNombre", "telefono" });
                    }
                });
            }
        });
        return writer;
	}
	
	
	@Bean
	public Job importPersonaJob(JobListener listener, Step step1) {
		return jobBuilderFactory.get("importPersonaJob")
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(step1)
				.end()
				.build();
	}
	
	@Bean
	public Step step1(PersonaItemProcessor personaaa, DataSource dataSource, FlatFileItemWriter<Persona> write) {
		return stepBuilderFactory.get("step1")
				.<Persona, Persona> chunk(100)
				.reader(reader())
				.processor(personaaa)
				.writer(write)
				.build();
	}
	
	
}

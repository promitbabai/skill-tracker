package com.iiht.fse4.skilltrackerbatch.config;

import com.iiht.fse4.skilltrackerbatch.batch.AssociateProcessor;
import com.iiht.fse4.skilltrackerbatch.entity.Associate;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;


@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactor;

    @Bean
    public Job loadDataJob(){

        return this.jobBuilderFactory.get("Load-Data-Job")
                .incrementer( new RunIdIncrementer())
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public Step step1(){
        return this.stepBuilderFactor.get("METHOD - Step 1")
                .<Associate, Associate>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

//    @Bean
//    public Job job(JobBuilderFactory jobBuilderFactory,
//                   StepBuilderFactory stepBuilderFactory,
//                   ItemReader<Associate> itemReader,
//                   ItemProcessor<Associate, Associate> itemProcessor,
//                   ItemWriter<Associate> itemWriter
//    ) {
//
//        Step step = stepBuilderFactory.get("ETL-file-load")
//                .<Associate, Associate>chunk(100)
//                .reader(itemReader)
//                .processor(itemProcessor)
//                .writer(itemWriter)
//                .build();
//
//
//        return jobBuilderFactory.get("ETL-Load")
//                .incrementer(new RunIdIncrementer())
//                .start(step)
//                .build();
//    }

    @Bean
    public FlatFileItemReader<Associate> reader() {

        System.out.println("Inside READER method - Trying to access CSV File");
        FlatFileItemReader<Associate> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new ClassPathResource("associate.csv"));
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setLineMapper(lineMapper());
        return flatFileItemReader;
    }

    @Bean
    public LineMapper<Associate> lineMapper() {

        DefaultLineMapper<Associate> defaultLineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        //lineTokenizer.setDelimiter(",");
        //lineTokenizer.setStrict(false);
        lineTokenizer.setNames(new String[]{"associateid", "name", "email", "mobile", "lastupdated"});
        lineTokenizer.setIncludedFields(new int[]{0,1,2,3,4});

        BeanWrapperFieldSetMapper<Associate> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Associate.class);

        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);

        return defaultLineMapper;
    }

    @Bean
    public AssociateProcessor processor(){
        return new AssociateProcessor();
    }


    @Bean
    public JdbcBatchItemWriter<Associate> writer(){
        System.out.println("Inside Writer with Associate Object = ");
        JdbcBatchItemWriter<Associate> writer = new JdbcBatchItemWriter<Associate>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Associate>());
        writer.setSql("insert into skilltracker.associate(associateid, name, email, mobile, lastupdated) values (:associateid, :name, :email, :mobile, :lastupdated)");

        writer.setDataSource(this.dataSource);
        return writer;

    }

}

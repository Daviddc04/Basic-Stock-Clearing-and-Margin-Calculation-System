package com.margin.clearing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean(name = "taskExecutor", destroyMethod = "shutdown")
    public ExecutorService taskExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            50,  // corePoolSize
            100, // maximumPoolSize
            60L, TimeUnit.SECONDS, // keepAliveTime
            new LinkedBlockingQueue<>(1000), // workQueue
            r -> {
                Thread t = new Thread(r);
                t.setName("async-executor-" + t.getId());
                t.setDaemon(false);
                return t;
            }
        );
        return executor;
    }
}

/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package io.elasticjob.example.config;

import io.elasticjob.example.job.simple.SpringSimpleJob;
import io.elasticjob.lite.api.JobScheduler;
import io.elasticjob.lite.api.simple.SimpleJob;
import io.elasticjob.lite.config.JobCoreConfiguration;
import io.elasticjob.lite.config.LiteJobConfiguration;
import io.elasticjob.lite.config.simple.SimpleJobConfiguration;
import io.elasticjob.lite.event.JobEventConfiguration;
import io.elasticjob.lite.reg.zookeeper.ZookeeperRegistryCenter;
import io.elasticjob.lite.spring.api.SpringJobScheduler;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimpleJobConfig {
    
    @Resource
    private ZookeeperRegistryCenter regCenter;
    
    @Resource
    private JobEventConfiguration jobEventConfiguration;
    
    @Bean
    public SimpleJob simpleJob() {
        return new SpringSimpleJob(); 
    }
    
    @Bean(initMethod = "init")
    public JobScheduler simpleJobScheduler(final SimpleJob simpleJob, @Value("${simpleJob.cron}") final String cron, @Value("${simpleJob.shardingTotalCount}") final int shardingTotalCount,
                                           @Value("${simpleJob.shardingItemParameters}") final String shardingItemParameters) {
        return new SpringJobScheduler(simpleJob, regCenter, getLiteJobConfiguration(simpleJob.getClass(), cron, shardingTotalCount, shardingItemParameters), jobEventConfiguration);
    }
    
    private LiteJobConfiguration getLiteJobConfiguration(final Class<? extends SimpleJob> jobClass, final String cron, final int shardingTotalCount, final String shardingItemParameters) {
        return LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(JobCoreConfiguration.newBuilder(
                jobClass.getName(), cron, shardingTotalCount).shardingItemParameters(shardingItemParameters).build(), jobClass.getCanonicalName())).overwrite(true).build();
    }
}

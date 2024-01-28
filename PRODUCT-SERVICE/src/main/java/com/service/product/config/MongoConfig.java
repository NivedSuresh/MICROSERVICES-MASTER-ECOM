package com.service.product.config;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.TransactionManager;

@Configuration
@EnableMongoRepositories(basePackages = "com.service.product.repo")
public class MongoConfig {
//    @Bean
//    MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
//        MongoTransactionManager manager =  new MongoTransactionManager(dbFactory);
//        manager.setRollbackOnCommitFailure(true);
//        manager.setFailEarlyOnGlobalRollbackOnly(true);
//        manager.setGlobalRollbackOnParticipationFailure(true);
//        return manager;
//    }
}

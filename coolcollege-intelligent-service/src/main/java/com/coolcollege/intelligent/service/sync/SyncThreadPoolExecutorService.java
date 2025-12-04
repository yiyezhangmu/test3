package com.coolcollege.intelligent.service.sync;

import com.coolcollege.intelligent.common.sync.conf.SyncThreadPoolConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SyncThreadPoolExecutorService implements CommandLineRunner {
    private ExecutorService executorService;

    @Autowired
    private SyncThreadPoolConfig syncThreadPoolConfig;

    public void submitAddressBookTask(AddressBookTask addressBookTask) {
        executorService.submit(addressBookTask);
    }

    @Override
    public void run(String... strings) throws Exception {
        executorService = new CustomerThreadPoolExecutor(syncThreadPoolConfig.getCorePoolSize(), syncThreadPoolConfig.getMaxPoolSize(),
                syncThreadPoolConfig.getKeepAliveTime(), TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    }
}

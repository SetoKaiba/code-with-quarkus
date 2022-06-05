package org.acme;

import io.quarkus.arc.Unremovable;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.protostream.annotations.ProtoField;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Path("/hello")
public class GreetingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from RESTEasy Reactive";
    }

    @Path("/test")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test() throws Exception {
//        Cache<String, Object> idCache = embeddedCacheManager.getCache("DistributedIdCache");
//        Test test = new Test();
//        test.setId("test");
//        test.setName("test");
//        idCache.put("haha", test);
//        System.out.println(idCache.get("haha"));
//        TestTask testTask = new TestTask();
//        testTask.setTest(test);
//        embeddedCacheManager.executor().singleNodeSubmission().submit(testTask);

        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.clustering().cacheMode(CacheMode.DIST_SYNC);

        List<EmbeddedCacheManager> managers = new ArrayList<>(3);
        try {
            // Force TCP to connect to loopback, which our TCPPING in dist.xml connects to for discovery
            String oldProperty = System.setProperty("jgroups.tcp.address", "127.0.0.1");
            for (int i = 0; i < 3; i++) {
                System.out.println(Paths.get(".").toAbsolutePath());
                EmbeddedCacheManager ecm = new DefaultCacheManager(Paths.get("..", "..", "..", "..", "src", "main", "resources", "dist.xml").toString());
                ecm.start();
                managers.add(ecm);
                // Start the default cache
                ecm.getCache();
            }

            if (oldProperty != null) {
                System.setProperty("jgroups.tcp.address", oldProperty);
            }

            long failureTime = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);

            int sizeMatched = 0;
            while (sizeMatched < 3) {
                // reset the size every time
                sizeMatched = 0;
                for (EmbeddedCacheManager ecm : managers) {
                    int size = ecm.getMembers().size();
                    if (size == 3) {
                        sizeMatched++;
                    }
                }
                if (failureTime - System.nanoTime() < 0) {
                    return "Timed out waiting for caches to have joined together!";
                }
            }

            EmbeddedCacheManager embeddedCacheManager = managers.get(2);
            Cache<String, Object> idCache = embeddedCacheManager.getCache("DistributedIdCache");
            Test test = new Test();
            test.setId("test");
            test.setName("test");
            idCache.put("haha", test);
            TestTask testTask0 = new TestTask();
            testTask0.setTest(test);
            System.out.println("aaa:" + testTask0.testAppScope);
            embeddedCacheManager.executor().allNodeSubmission().submit(testTask0);
            TestTask testTask1 = CDI.current().select(TestTask.class).get();
            testTask1.setTest(test);
            System.out.println("bbb:" + testTask1.testAppScope);
            embeddedCacheManager.executor().allNodeSubmission().submit(testTask1);
        } finally {
            managers.forEach(EmbeddedCacheManager::stop);
        }
        return "Success";
    }

    @ApplicationScoped
    public static class TestAppScope {
    }

    @Dependent
    @Unremovable
    public static class TestTask implements Runnable {
        @Inject
        TestAppScope testAppScope;

        private Test test;

        @ProtoField(1)
        public Test getTest() {
            return test;
        }

        public void setTest(Test test) {
            this.test = test;
        }

        @Override
        public void run() {
            // inject fields with BeanManager here
            try {
                System.out.println(this.toString() + test);
                System.out.println(this.toString() + testAppScope);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
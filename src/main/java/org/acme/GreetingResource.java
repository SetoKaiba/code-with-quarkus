package org.acme;

import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class GreetingResource {

    @Inject
    EmbeddedCacheManager embeddedCacheManager;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from RESTEasy Reactive";
    }

    @Path("/test")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        Cache<String, Object> idCache = embeddedCacheManager.getCache("DistributedIdCache");
        Test test = new Test();
        test.setId("test");
        test.setName("test");
        idCache.put("haha", test);
        System.out.println(idCache.get("haha"));
        return "Hello from RESTEasy Reactive";
    }
}
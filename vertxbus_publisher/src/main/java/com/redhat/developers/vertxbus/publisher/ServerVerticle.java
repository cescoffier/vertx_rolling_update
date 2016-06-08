package com.redhat.developers.vertxbus.publisher;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;

// A publisher does not require a Web API
// but we are adding one for the OpenShift readinessProbe
// import io.vertx.ext.web.handler.StaticHandler;
// import io.vertx.ext.web.handler.sockjs.BridgeOptions;
// import io.vertx.ext.web.handler.sockjs.PermittedOptions;
// import io.vertx.ext.web.handler.sockjs.SockJSHandler;

public class ServerVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> future) throws Exception {
    String hostname = System.getenv().getOrDefault("HOSTNAME", "unknown");

    // Now get chatty on the EventBus
    EventBus eb = vertx.eventBus();

    // One of the nice thing about vert.x : verticle are single-threaded, no concurrent access, so you can cache
    // the date format instance
    DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);

    vertx.deployVerticle(HttpPingProbeVerticle.class.getName(), ar -> {
      vertx.setPeriodic(1000L, t -> {
        // Create a timestamp string
        String timestamp = formatter.format(Date.from(Instant.now()));
        //System.out.println(hostname + " sending " + timestamp);
        eb.send("servertopic1", new JsonObject().put("now", hostname + "*-* " + timestamp + " V4"));
      });
      future.complete();
    });


  }

}
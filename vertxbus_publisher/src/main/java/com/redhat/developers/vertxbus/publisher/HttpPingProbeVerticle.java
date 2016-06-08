package com.redhat.developers.vertxbus.publisher;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.ext.web.Router;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class HttpPingProbeVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> future) throws Exception {
    Router router = Router.router(vertx);

    // Health Check
    router.get("/api/health").handler(ctx -> {
      // ? need to see if the eventbus is ready to rock (clustered)
      vertx.eventBus().send("ping", "ping", new DeliveryOptions().setSendTimeout(2000), reply -> {
        if (reply.failed()) {
          System.out.println("health check failed - " + reply.cause().getMessage());
          ctx.response().setStatusCode(503).end("Alone in the dark");
        } else {
          System.out.println("health check ok - " + reply.result().body());
          ctx.response().setStatusCode(200).end("I'm ok");
        }
      });
    });


    // Start the web server and tell it to use the router to handle requests.
    vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("port", 8080), done -> {
      if (done.succeeded()) {
        future.complete();
      } else {
        future.fail(done.cause());
      }
    });
  }

}

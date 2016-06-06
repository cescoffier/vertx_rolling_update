package com.redhat.developers.vertxbus.publisher;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
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
      ctx.response().end("I'm ok");
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

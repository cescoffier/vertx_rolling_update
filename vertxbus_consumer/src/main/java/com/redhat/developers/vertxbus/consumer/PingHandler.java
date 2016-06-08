package com.redhat.developers.vertxbus.consumer;

import io.vertx.core.AbstractVerticle;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class PingHandler extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    vertx.eventBus().consumer("ping", message -> {
      message.reply("pong");
    });
  }
}

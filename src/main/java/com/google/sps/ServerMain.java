package com.google.sps;

import java.net.URL;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.time.Instant;
import java.time.Duration;
import java.util.Date;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.Timestamp;

/**
 * Starts up the server, including a DefaultServlet that handles static files, and any servlet
 * classes annotated with the @WebServlet annotation.
 */
public class ServerMain {

  private static void deleteOldEvents() {
    System.out.println("Deleting events that have occurred more than 1 day in the past");

    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    Query<Entity> query = Query.newEntityQueryBuilder().setKind("Event").build();
    QueryResults<Entity> results = datastore.run(query);

    Instant now = Instant.now(); // current time
    Instant cutoff_time = now.minus(Duration.ofDays(1));
    Date cutoff_date = Date.from(cutoff_time);

    while (results.hasNext()) {
      Entity entity = results.next();

      if (entity.getTimestamp("date").compareTo(Timestamp.of(cutoff_date)) < 0) {
        Long event_id = entity.getKey().getId();
        KeyFactory keyFactory = datastore.newKeyFactory().setKind("Event");
        datastore.delete(keyFactory.newKey(event_id));
        System.out.println("Deleted old event with id " + event_id);
      }
    }
  }

  public static void main(String[] args) throws Exception {

    // Create a server that listens on port 8080.
    Server server = new Server(8080);
    WebAppContext webAppContext = new WebAppContext();
    server.setHandler(webAppContext);

    // Load static content from inside the jar file.
    URL webAppDir = ServerMain.class.getClassLoader().getResource("META-INF/resources");
    webAppContext.setResourceBase(webAppDir.toURI().toString());

    // Enable annotations so the server sees classes annotated with @WebServlet.
    webAppContext.setConfigurations(
        new Configuration[] {
          new AnnotationConfiguration(), new WebInfConfiguration(),
        });

    // Look for annotations in the classes directory (dev server) and in the jar file (live server)
    webAppContext.setAttribute(
        "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
        ".*/target/classes/|.*\\.jar");

    // Handle static resources, e.g. html files.
    ServletHolder defaultServletHolder = webAppContext.addServlet(DefaultServlet.class, "/");
    defaultServletHolder.setInitParameter("cacheControl", "no-store, max-age=0");

    // Start the server! 🚀
    server.start();
    System.out.println("Server started!");

    // https://stackoverflow.com/a/32228421
    // Start thread to periodically delete old events
    ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
    ses.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
          deleteOldEvents();
      }
    }, 0, 1, TimeUnit.HOURS);

    // Keep the main thread alive while the server is running.
    server.join();

    // https://stackoverflow.com/a/38412295
    // Shut down ScheduledExecutorService
    ses.shutdown(); // Stop deleting more events
    try {
      // Wait a while for existing tasks to terminate
      if (!ses.awaitTermination(60, TimeUnit.SECONDS)) {
          ses.shutdownNow();

          // Wait a while for tasks to respond to being cancelled
          if (!ses.awaitTermination(60, TimeUnit.SECONDS)) {
            System.err.println("Delete events thread did not terminate");
        }
      }
    }
    catch (InterruptedException ie) {
      // (Re-)Cancel if current thread also interrupted
      ses.shutdownNow();
      // Preserve interrupt status
      Thread.currentThread().interrupt();
    }
  }
}

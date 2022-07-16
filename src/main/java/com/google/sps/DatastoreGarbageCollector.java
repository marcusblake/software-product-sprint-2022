package com.google.sps;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DatastoreGarbageCollector {
  private ScheduledExecutorService ses;

  private static void deleteOldEvents() {
    System.out.println("Deleting events that have occurred more than 1 day in the past");

    Instant now = Instant.now(); // current time
    Instant cutoff_time = now.minus(Duration.ofDays(1));
    Date cutoff_date = Date.from(cutoff_time);

    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    Query<Entity> query =
        Query.newEntityQueryBuilder()
            .setKind("Event")
            .setFilter(PropertyFilter.lt("date", Timestamp.of(cutoff_date)))
            .build();
    QueryResults<Entity> results = datastore.run(query);

    while (results.hasNext()) {
      Entity entity = results.next();
      Long event_id = entity.getKey().getId();
      KeyFactory keyFactory = datastore.newKeyFactory().setKind("Event");
      datastore.delete(keyFactory.newKey(event_id));
      System.out.println("Deleted old event with id " + event_id);
    }
  }

  // https://stackoverflow.com/a/32228421
  // Start thread to periodically delete old events
  public void start() {
    ses = Executors.newSingleThreadScheduledExecutor();
    ses.scheduleAtFixedRate(
        new Runnable() {
          @Override
          public void run() {
            deleteOldEvents();
          }
        },
        0,
        1,
        TimeUnit.HOURS);
  }

  // https://stackoverflow.com/a/38412295
  // Shut down ScheduledExecutorService
  public void stop() {
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
    } catch (InterruptedException ie) {
      // (Re-)Cancel if current thread also interrupted
      ses.shutdownNow();
      // Preserve interrupt status
      Thread.currentThread().interrupt();
    }
  }
}

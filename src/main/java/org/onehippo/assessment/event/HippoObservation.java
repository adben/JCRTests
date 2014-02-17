package org.onehippo.assessment.event;

import org.onehippo.assessment.RepoConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;

/**
 *
 */
public class HippoObservation implements EventListener {
	private static final Logger LOG = LoggerFactory.getLogger(HippoObservation.class);
	private static final String CONTENT_PATH = "/content/";
	private Counter counter;

	public void activate() throws Exception {
		LOG.info("activating HippoObservation...");
		try {
			counter = new Counter();
			RepoConnector.INSTANCE.initializeConnection();
			Session session = RepoConnector.INSTANCE.getSession();
			final ObservationManager observationManager = session.getWorkspace().getObservationManager();
			observationManager.addEventListener(
					this, //handler
					Event.PROPERTY_ADDED | Event.NODE_ADDED, //binary combination of event types
					CONTENT_PATH, //path
					true, //is Deep?
					null, //uuids filter
					null, //nodetypes filter
					false);
			LOG.info("******** Added JCR event listener");
		} catch (RepositoryException e) {
			LOG.error("unable to register session", e);
			throw new Exception(e);
		}
	}

	public void deactivate() throws Exception {
		LOG.info("deactivating HippoObservation...");
		try {
			RepoConnector.INSTANCE.initializeConnection();
			Session session = RepoConnector.INSTANCE.getSession();
			final ObservationManager observationManager = session.getWorkspace().getObservationManager();
			observationManager.removeEventListener(this);
		} catch (RepositoryException e) {
			LOG.error("unable to deregister session", e);
			throw new Exception(e);
		}
		RepoConnector.INSTANCE.logout();
	}


	@Override
	public void onEvent(EventIterator eventIterator) {
		try {
			while (eventIterator.hasNext()) {
				LOG.info("something has been changed at : {}", eventIterator.nextEvent().getPath());
			  int countAtomically = counter.getCountAtomically();
				if (6 < countAtomically) {
					try {
						deactivate();
					} catch (Exception e) {
						LOG.error("unable to deactivate the Listener", e);
					}
				} else {
					LOG.info("count on {}", countAtomically);
				}
			}
		} catch (RepositoryException e) {
			LOG.error("Error while treating events", e);
		}
	}
}

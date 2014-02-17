package org.onehippo.assessment;

import org.hippoecm.repository.HippoRepository;
import org.hippoecm.repository.HippoRepositoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Holds the instance of the connection to the repository
 */
public enum RepoConnector {
	INSTANCE;
	private static final Logger LOG = LoggerFactory.getLogger(RepoConnector.class);
	private static final String ADMIN = "admin";

	private Session session;

	public void initializeConnection() {
		HippoRepository repo;
		Session obtainedSession = null;
		String repoUrl = "rmi://127.0.0.1:1099/hipporepository";
		try {
			repo = HippoRepositoryFactory.getHippoRepository(repoUrl);
			char[] password = ADMIN.toCharArray();
			String username = ADMIN;
			obtainedSession = repo.login(username, password);
		} catch (RepositoryException e) {
			LOG.error("Error obtaining the Repository session  " + e);
		}
		setSession(obtainedSession);
	}

	private void setSession(Session session) {
		this.session = session;
	}

	public Session getSession() {
		return session;
	}

	public void logout() {
		if (null != this.session) {
			this.session.logout();
		}
	}
}

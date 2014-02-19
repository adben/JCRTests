package org.onehippo.assessment;

import org.hippoecm.repository.HippoRepository;
import org.hippoecm.repository.HippoRepositoryFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Holds the instance of the connection to the repository
 */
public enum RepoConnector {
	INSTANCE;
	private static final String ADMIN = "admin";

	private Session session;

	public void initializeConnection() throws RepositoryException {
		HippoRepository repo;
		String repoUrl = "rmi://127.0.0.1:1099/hipporepository";
		repo = HippoRepositoryFactory.getHippoRepository(repoUrl);
		char[] password = ADMIN.toCharArray();
		String username = ADMIN;
		Session obtainedSession = repo.login(username, password);
		setSession(obtainedSession);
	}

	private void setSession(final Session sessionInst) {
		this.session = sessionInst;
	}

	public Session getSession() {
		return session;
	}

	public void logout() {
		if (null != getSession()) {
			getSession().logout();
		}
	}
}

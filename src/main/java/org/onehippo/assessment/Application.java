package org.onehippo.assessment;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.hippoecm.repository.HippoRepository;
import org.hippoecm.repository.HippoRepositoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version "$Id$"
 */
public final class Application {

    private static Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws RepositoryException {
        String repoUrl = "rmi://localhost:1099/hipporepository";
        String username = "admin";
        char[] password = "admin".toCharArray();
        HippoRepository repository = HippoRepositoryFactory.getHippoRepository(repoUrl);
        Session session = repository.login(username, password);
        session.getRootNode();
        // TODO implement...
    }


}

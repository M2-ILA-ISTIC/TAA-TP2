package fr.istic.taa.jaxrs.dao;

import fr.istic.taa.jaxrs.dao.generic.AbstractJpaDao;
import fr.istic.taa.jaxrs.domain.Child;
import fr.istic.taa.jaxrs.exceptions.ValueAlreadyExistsException;

import javax.ws.rs.NotFoundException;
import java.util.List;

public class ChildDAO extends AbstractJpaDao<Long, Child> {

    public ChildDAO() {
        super(Child.class);
    }

    public void createChild(Child child) throws ValueAlreadyExistsException {
        if (manager.contains(child)) {
            throw new ValueAlreadyExistsException("The child object is already inserted.");
        }
        var tx = manager.getTransaction();
        tx.begin();
        manager.persist(child);
        tx.commit();
    }

    // We think that returning a list is better than returning a single element from a list.
    public List<Child> getChildByName(String firstName, String lastName) {
        return manager
                .createQuery("SELECT c FROM Child c WHERE c.lastName LIKE :lastName " +
                        "AND c.firstName LIKE :firstName", Child.class)
                .setParameter("firstName", firstName)
                .setParameter("lastName", lastName)
                .getResultList();
    }

    public List<Child> getChildList() {
        return manager
                .createQuery("SELECT c FROM Child c", Child.class)
                .getResultList();
    }

    public void removeChildByName(String firstName, String lastName) {
        var res = manager.createQuery("select c from Child c where c.firstName like :first and c.lastName like :last", Child.class)
                .setParameter("first", firstName)
                .setParameter("last", lastName)
                .getResultList();
        if (res.isEmpty()) {
            throw new NotFoundException("The requested child could not be find.");
        } else {
            this.manager.createQuery("delete from Child c where c.firstName = :firstName and c.lastName = :lastName")
                    .setParameter("lastName", lastName)
                    .setParameter("firstName", firstName);
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.bibliotecamunicipal.dao;

import br.com.bibliotecamunicipal.dao.exceptions.IllegalOrphanException;
import br.com.bibliotecamunicipal.dao.exceptions.NonexistentEntityException;
import br.com.bibliotecamunicipal.dao.exceptions.RollbackFailureException;
import br.com.bibliotecamunicipal.entity.Livro;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import br.com.bibliotecamunicipal.entity.Livroemprestimo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author SUELYTA
 */
public class LivroDAO implements Serializable {

    public LivroDAO(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Livro livro) throws RollbackFailureException, Exception {
        if (livro.getLivroemprestimoCollection() == null) {
            livro.setLivroemprestimoCollection(new ArrayList<Livroemprestimo>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Livroemprestimo> attachedLivroemprestimoCollection = new ArrayList<Livroemprestimo>();
            for (Livroemprestimo livroemprestimoCollectionLivroemprestimoToAttach : livro.getLivroemprestimoCollection()) {
                livroemprestimoCollectionLivroemprestimoToAttach = em.getReference(livroemprestimoCollectionLivroemprestimoToAttach.getClass(), livroemprestimoCollectionLivroemprestimoToAttach.getId());
                attachedLivroemprestimoCollection.add(livroemprestimoCollectionLivroemprestimoToAttach);
            }
            livro.setLivroemprestimoCollection(attachedLivroemprestimoCollection);
            em.persist(livro);
            for (Livroemprestimo livroemprestimoCollectionLivroemprestimo : livro.getLivroemprestimoCollection()) {
                Livro oldIdLivroOfLivroemprestimoCollectionLivroemprestimo = livroemprestimoCollectionLivroemprestimo.getIdLivro();
                livroemprestimoCollectionLivroemprestimo.setIdLivro(livro);
                livroemprestimoCollectionLivroemprestimo = em.merge(livroemprestimoCollectionLivroemprestimo);
                if (oldIdLivroOfLivroemprestimoCollectionLivroemprestimo != null) {
                    oldIdLivroOfLivroemprestimoCollectionLivroemprestimo.getLivroemprestimoCollection().remove(livroemprestimoCollectionLivroemprestimo);
                    oldIdLivroOfLivroemprestimoCollectionLivroemprestimo = em.merge(oldIdLivroOfLivroemprestimoCollectionLivroemprestimo);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Livro livro) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Livro persistentLivro = em.find(Livro.class, livro.getId());
            Collection<Livroemprestimo> livroemprestimoCollectionOld = persistentLivro.getLivroemprestimoCollection();
            Collection<Livroemprestimo> livroemprestimoCollectionNew = livro.getLivroemprestimoCollection();
            List<String> illegalOrphanMessages = null;
            for (Livroemprestimo livroemprestimoCollectionOldLivroemprestimo : livroemprestimoCollectionOld) {
                if (!livroemprestimoCollectionNew.contains(livroemprestimoCollectionOldLivroemprestimo)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Livroemprestimo " + livroemprestimoCollectionOldLivroemprestimo + " since its idLivro field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Livroemprestimo> attachedLivroemprestimoCollectionNew = new ArrayList<Livroemprestimo>();
            for (Livroemprestimo livroemprestimoCollectionNewLivroemprestimoToAttach : livroemprestimoCollectionNew) {
                livroemprestimoCollectionNewLivroemprestimoToAttach = em.getReference(livroemprestimoCollectionNewLivroemprestimoToAttach.getClass(), livroemprestimoCollectionNewLivroemprestimoToAttach.getId());
                attachedLivroemprestimoCollectionNew.add(livroemprestimoCollectionNewLivroemprestimoToAttach);
            }
            livroemprestimoCollectionNew = attachedLivroemprestimoCollectionNew;
            livro.setLivroemprestimoCollection(livroemprestimoCollectionNew);
            livro = em.merge(livro);
            for (Livroemprestimo livroemprestimoCollectionNewLivroemprestimo : livroemprestimoCollectionNew) {
                if (!livroemprestimoCollectionOld.contains(livroemprestimoCollectionNewLivroemprestimo)) {
                    Livro oldIdLivroOfLivroemprestimoCollectionNewLivroemprestimo = livroemprestimoCollectionNewLivroemprestimo.getIdLivro();
                    livroemprestimoCollectionNewLivroemprestimo.setIdLivro(livro);
                    livroemprestimoCollectionNewLivroemprestimo = em.merge(livroemprestimoCollectionNewLivroemprestimo);
                    if (oldIdLivroOfLivroemprestimoCollectionNewLivroemprestimo != null && !oldIdLivroOfLivroemprestimoCollectionNewLivroemprestimo.equals(livro)) {
                        oldIdLivroOfLivroemprestimoCollectionNewLivroemprestimo.getLivroemprestimoCollection().remove(livroemprestimoCollectionNewLivroemprestimo);
                        oldIdLivroOfLivroemprestimoCollectionNewLivroemprestimo = em.merge(oldIdLivroOfLivroemprestimoCollectionNewLivroemprestimo);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = livro.getId();
                if (findLivro(id) == null) {
                    throw new NonexistentEntityException("The livro with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Livro livro;
            try {
                livro = em.getReference(Livro.class, id);
                livro.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The livro with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Livroemprestimo> livroemprestimoCollectionOrphanCheck = livro.getLivroemprestimoCollection();
            for (Livroemprestimo livroemprestimoCollectionOrphanCheckLivroemprestimo : livroemprestimoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Livro (" + livro + ") cannot be destroyed since the Livroemprestimo " + livroemprestimoCollectionOrphanCheckLivroemprestimo + " in its livroemprestimoCollection field has a non-nullable idLivro field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(livro);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Livro> findLivroEntities() {
        return findLivroEntities(true, -1, -1);
    }

    public List<Livro> findLivroEntities(int maxResults, int firstResult) {
        return findLivroEntities(false, maxResults, firstResult);
    }

    private List<Livro> findLivroEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Livro.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Livro findLivro(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Livro.class, id);
        } finally {
            em.close();
        }
    }

    public int getLivroCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Livro> rt = cq.from(Livro.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

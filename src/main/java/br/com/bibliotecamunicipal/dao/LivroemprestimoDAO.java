/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.bibliotecamunicipal.dao;

import br.com.bibliotecamunicipal.dao.exceptions.NonexistentEntityException;
import br.com.bibliotecamunicipal.dao.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import br.com.bibliotecamunicipal.entity.Livro;
import br.com.bibliotecamunicipal.entity.Emprestimo;
import br.com.bibliotecamunicipal.entity.Livroemprestimo;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author SUELYTA
 */
public class LivroemprestimoDAO implements Serializable {

    public LivroemprestimoDAO(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Livroemprestimo livroemprestimo) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Livro idLivro = livroemprestimo.getIdLivro();
            if (idLivro != null) {
                idLivro = em.getReference(idLivro.getClass(), idLivro.getId());
                livroemprestimo.setIdLivro(idLivro);
            }
            Emprestimo idEmprestimo = livroemprestimo.getIdEmprestimo();
            if (idEmprestimo != null) {
                idEmprestimo = em.getReference(idEmprestimo.getClass(), idEmprestimo.getId());
                livroemprestimo.setIdEmprestimo(idEmprestimo);
            }
            em.persist(livroemprestimo);
            if (idLivro != null) {
                idLivro.getLivroemprestimoCollection().add(livroemprestimo);
                idLivro = em.merge(idLivro);
            }
            if (idEmprestimo != null) {
                idEmprestimo.getLivroemprestimoCollection().add(livroemprestimo);
                idEmprestimo = em.merge(idEmprestimo);
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

    public void edit(Livroemprestimo livroemprestimo) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Livroemprestimo persistentLivroemprestimo = em.find(Livroemprestimo.class, livroemprestimo.getId());
            Livro idLivroOld = persistentLivroemprestimo.getIdLivro();
            Livro idLivroNew = livroemprestimo.getIdLivro();
            Emprestimo idEmprestimoOld = persistentLivroemprestimo.getIdEmprestimo();
            Emprestimo idEmprestimoNew = livroemprestimo.getIdEmprestimo();
            if (idLivroNew != null) {
                idLivroNew = em.getReference(idLivroNew.getClass(), idLivroNew.getId());
                livroemprestimo.setIdLivro(idLivroNew);
            }
            if (idEmprestimoNew != null) {
                idEmprestimoNew = em.getReference(idEmprestimoNew.getClass(), idEmprestimoNew.getId());
                livroemprestimo.setIdEmprestimo(idEmprestimoNew);
            }
            livroemprestimo = em.merge(livroemprestimo);
            if (idLivroOld != null && !idLivroOld.equals(idLivroNew)) {
                idLivroOld.getLivroemprestimoCollection().remove(livroemprestimo);
                idLivroOld = em.merge(idLivroOld);
            }
            if (idLivroNew != null && !idLivroNew.equals(idLivroOld)) {
                idLivroNew.getLivroemprestimoCollection().add(livroemprestimo);
                idLivroNew = em.merge(idLivroNew);
            }
            if (idEmprestimoOld != null && !idEmprestimoOld.equals(idEmprestimoNew)) {
                idEmprestimoOld.getLivroemprestimoCollection().remove(livroemprestimo);
                idEmprestimoOld = em.merge(idEmprestimoOld);
            }
            if (idEmprestimoNew != null && !idEmprestimoNew.equals(idEmprestimoOld)) {
                idEmprestimoNew.getLivroemprestimoCollection().add(livroemprestimo);
                idEmprestimoNew = em.merge(idEmprestimoNew);
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
                Integer id = livroemprestimo.getId();
                if (findLivroemprestimo(id) == null) {
                    throw new NonexistentEntityException("The livroemprestimo with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Livroemprestimo livroemprestimo;
            try {
                livroemprestimo = em.getReference(Livroemprestimo.class, id);
                livroemprestimo.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The livroemprestimo with id " + id + " no longer exists.", enfe);
            }
            Livro idLivro = livroemprestimo.getIdLivro();
            if (idLivro != null) {
                idLivro.getLivroemprestimoCollection().remove(livroemprestimo);
                idLivro = em.merge(idLivro);
            }
            Emprestimo idEmprestimo = livroemprestimo.getIdEmprestimo();
            if (idEmprestimo != null) {
                idEmprestimo.getLivroemprestimoCollection().remove(livroemprestimo);
                idEmprestimo = em.merge(idEmprestimo);
            }
            em.remove(livroemprestimo);
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

    public List<Livroemprestimo> findLivroemprestimoEntities() {
        return findLivroemprestimoEntities(true, -1, -1);
    }

    public List<Livroemprestimo> findLivroemprestimoEntities(int maxResults, int firstResult) {
        return findLivroemprestimoEntities(false, maxResults, firstResult);
    }

    private List<Livroemprestimo> findLivroemprestimoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Livroemprestimo.class));
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

    public Livroemprestimo findLivroemprestimo(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Livroemprestimo.class, id);
        } finally {
            em.close();
        }
    }

    public int getLivroemprestimoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Livroemprestimo> rt = cq.from(Livroemprestimo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.bibliotecamunicipal.dao;

import br.com.bibliotecamunicipal.dao.exceptions.IllegalOrphanException;
import br.com.bibliotecamunicipal.dao.exceptions.NonexistentEntityException;
import br.com.bibliotecamunicipal.dao.exceptions.RollbackFailureException;
import br.com.bibliotecamunicipal.entity.Emprestimo;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import br.com.bibliotecamunicipal.entity.Funcionario;
import br.com.bibliotecamunicipal.entity.Usuario;
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
public class EmprestimoDAO implements Serializable {

    public EmprestimoDAO(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Emprestimo emprestimo) throws RollbackFailureException, Exception {
        if (emprestimo.getLivroemprestimoCollection() == null) {
            emprestimo.setLivroemprestimoCollection(new ArrayList<Livroemprestimo>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Funcionario idFuncionario = emprestimo.getIdFuncionario();
            if (idFuncionario != null) {
                idFuncionario = em.getReference(idFuncionario.getClass(), idFuncionario.getId());
                emprestimo.setIdFuncionario(idFuncionario);
            }
            Usuario idUsuario = emprestimo.getIdUsuario();
            if (idUsuario != null) {
                idUsuario = em.getReference(idUsuario.getClass(), idUsuario.getId());
                emprestimo.setIdUsuario(idUsuario);
            }
            Collection<Livroemprestimo> attachedLivroemprestimoCollection = new ArrayList<Livroemprestimo>();
            for (Livroemprestimo livroemprestimoCollectionLivroemprestimoToAttach : emprestimo.getLivroemprestimoCollection()) {
                livroemprestimoCollectionLivroemprestimoToAttach = em.getReference(livroemprestimoCollectionLivroemprestimoToAttach.getClass(), livroemprestimoCollectionLivroemprestimoToAttach.getId());
                attachedLivroemprestimoCollection.add(livroemprestimoCollectionLivroemprestimoToAttach);
            }
            emprestimo.setLivroemprestimoCollection(attachedLivroemprestimoCollection);
            em.persist(emprestimo);
            if (idFuncionario != null) {
                idFuncionario.getEmprestimoCollection().add(emprestimo);
                idFuncionario = em.merge(idFuncionario);
            }
            if (idUsuario != null) {
                idUsuario.getEmprestimoCollection().add(emprestimo);
                idUsuario = em.merge(idUsuario);
            }
            for (Livroemprestimo livroemprestimoCollectionLivroemprestimo : emprestimo.getLivroemprestimoCollection()) {
                Emprestimo oldIdEmprestimoOfLivroemprestimoCollectionLivroemprestimo = livroemprestimoCollectionLivroemprestimo.getIdEmprestimo();
                livroemprestimoCollectionLivroemprestimo.setIdEmprestimo(emprestimo);
                livroemprestimoCollectionLivroemprestimo = em.merge(livroemprestimoCollectionLivroemprestimo);
                if (oldIdEmprestimoOfLivroemprestimoCollectionLivroemprestimo != null) {
                    oldIdEmprestimoOfLivroemprestimoCollectionLivroemprestimo.getLivroemprestimoCollection().remove(livroemprestimoCollectionLivroemprestimo);
                    oldIdEmprestimoOfLivroemprestimoCollectionLivroemprestimo = em.merge(oldIdEmprestimoOfLivroemprestimoCollectionLivroemprestimo);
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

    public void edit(Emprestimo emprestimo) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Emprestimo persistentEmprestimo = em.find(Emprestimo.class, emprestimo.getId());
            Funcionario idFuncionarioOld = persistentEmprestimo.getIdFuncionario();
            Funcionario idFuncionarioNew = emprestimo.getIdFuncionario();
            Usuario idUsuarioOld = persistentEmprestimo.getIdUsuario();
            Usuario idUsuarioNew = emprestimo.getIdUsuario();
            Collection<Livroemprestimo> livroemprestimoCollectionOld = persistentEmprestimo.getLivroemprestimoCollection();
            Collection<Livroemprestimo> livroemprestimoCollectionNew = emprestimo.getLivroemprestimoCollection();
            List<String> illegalOrphanMessages = null;
            for (Livroemprestimo livroemprestimoCollectionOldLivroemprestimo : livroemprestimoCollectionOld) {
                if (!livroemprestimoCollectionNew.contains(livroemprestimoCollectionOldLivroemprestimo)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Livroemprestimo " + livroemprestimoCollectionOldLivroemprestimo + " since its idEmprestimo field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idFuncionarioNew != null) {
                idFuncionarioNew = em.getReference(idFuncionarioNew.getClass(), idFuncionarioNew.getId());
                emprestimo.setIdFuncionario(idFuncionarioNew);
            }
            if (idUsuarioNew != null) {
                idUsuarioNew = em.getReference(idUsuarioNew.getClass(), idUsuarioNew.getId());
                emprestimo.setIdUsuario(idUsuarioNew);
            }
            Collection<Livroemprestimo> attachedLivroemprestimoCollectionNew = new ArrayList<Livroemprestimo>();
            for (Livroemprestimo livroemprestimoCollectionNewLivroemprestimoToAttach : livroemprestimoCollectionNew) {
                livroemprestimoCollectionNewLivroemprestimoToAttach = em.getReference(livroemprestimoCollectionNewLivroemprestimoToAttach.getClass(), livroemprestimoCollectionNewLivroemprestimoToAttach.getId());
                attachedLivroemprestimoCollectionNew.add(livroemprestimoCollectionNewLivroemprestimoToAttach);
            }
            livroemprestimoCollectionNew = attachedLivroemprestimoCollectionNew;
            emprestimo.setLivroemprestimoCollection(livroemprestimoCollectionNew);
            emprestimo = em.merge(emprestimo);
            if (idFuncionarioOld != null && !idFuncionarioOld.equals(idFuncionarioNew)) {
                idFuncionarioOld.getEmprestimoCollection().remove(emprestimo);
                idFuncionarioOld = em.merge(idFuncionarioOld);
            }
            if (idFuncionarioNew != null && !idFuncionarioNew.equals(idFuncionarioOld)) {
                idFuncionarioNew.getEmprestimoCollection().add(emprestimo);
                idFuncionarioNew = em.merge(idFuncionarioNew);
            }
            if (idUsuarioOld != null && !idUsuarioOld.equals(idUsuarioNew)) {
                idUsuarioOld.getEmprestimoCollection().remove(emprestimo);
                idUsuarioOld = em.merge(idUsuarioOld);
            }
            if (idUsuarioNew != null && !idUsuarioNew.equals(idUsuarioOld)) {
                idUsuarioNew.getEmprestimoCollection().add(emprestimo);
                idUsuarioNew = em.merge(idUsuarioNew);
            }
            for (Livroemprestimo livroemprestimoCollectionNewLivroemprestimo : livroemprestimoCollectionNew) {
                if (!livroemprestimoCollectionOld.contains(livroemprestimoCollectionNewLivroemprestimo)) {
                    Emprestimo oldIdEmprestimoOfLivroemprestimoCollectionNewLivroemprestimo = livroemprestimoCollectionNewLivroemprestimo.getIdEmprestimo();
                    livroemprestimoCollectionNewLivroemprestimo.setIdEmprestimo(emprestimo);
                    livroemprestimoCollectionNewLivroemprestimo = em.merge(livroemprestimoCollectionNewLivroemprestimo);
                    if (oldIdEmprestimoOfLivroemprestimoCollectionNewLivroemprestimo != null && !oldIdEmprestimoOfLivroemprestimoCollectionNewLivroemprestimo.equals(emprestimo)) {
                        oldIdEmprestimoOfLivroemprestimoCollectionNewLivroemprestimo.getLivroemprestimoCollection().remove(livroemprestimoCollectionNewLivroemprestimo);
                        oldIdEmprestimoOfLivroemprestimoCollectionNewLivroemprestimo = em.merge(oldIdEmprestimoOfLivroemprestimoCollectionNewLivroemprestimo);
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
                Integer id = emprestimo.getId();
                if (findEmprestimo(id) == null) {
                    throw new NonexistentEntityException("The emprestimo with id " + id + " no longer exists.");
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
            Emprestimo emprestimo;
            try {
                emprestimo = em.getReference(Emprestimo.class, id);
                emprestimo.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The emprestimo with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Livroemprestimo> livroemprestimoCollectionOrphanCheck = emprestimo.getLivroemprestimoCollection();
            for (Livroemprestimo livroemprestimoCollectionOrphanCheckLivroemprestimo : livroemprestimoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Emprestimo (" + emprestimo + ") cannot be destroyed since the Livroemprestimo " + livroemprestimoCollectionOrphanCheckLivroemprestimo + " in its livroemprestimoCollection field has a non-nullable idEmprestimo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Funcionario idFuncionario = emprestimo.getIdFuncionario();
            if (idFuncionario != null) {
                idFuncionario.getEmprestimoCollection().remove(emprestimo);
                idFuncionario = em.merge(idFuncionario);
            }
            Usuario idUsuario = emprestimo.getIdUsuario();
            if (idUsuario != null) {
                idUsuario.getEmprestimoCollection().remove(emprestimo);
                idUsuario = em.merge(idUsuario);
            }
            em.remove(emprestimo);
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

    public List<Emprestimo> findEmprestimoEntities() {
        return findEmprestimoEntities(true, -1, -1);
    }

    public List<Emprestimo> findEmprestimoEntities(int maxResults, int firstResult) {
        return findEmprestimoEntities(false, maxResults, firstResult);
    }

    private List<Emprestimo> findEmprestimoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Emprestimo.class));
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

    public Emprestimo findEmprestimo(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Emprestimo.class, id);
        } finally {
            em.close();
        }
    }

    public int getEmprestimoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Emprestimo> rt = cq.from(Emprestimo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

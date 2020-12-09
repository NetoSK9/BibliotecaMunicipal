/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.bibliotecamunicipal.facade;

import br.com.bibliotecamunicipal.entity.Emprestimo;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author SUELYTA
 */
@Stateless
public class EmprestimoFacade extends AbstractFacade<Emprestimo> {

    @PersistenceContext(unitName = "br.com.biblioteca_BibliotecaMunicipal_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EmprestimoFacade() {
        super(Emprestimo.class);
    }
    
}

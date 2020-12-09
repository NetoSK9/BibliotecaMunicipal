/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.bibliotecamunicipal.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author SUELYTA
 */
@Entity
@Table(name = "livroemprestimo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Livroemprestimo.findAll", query = "SELECT l FROM Livroemprestimo l")
    , @NamedQuery(name = "Livroemprestimo.findById", query = "SELECT l FROM Livroemprestimo l WHERE l.id = :id")})
public class Livroemprestimo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_Livro", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Livro idLivro;
    @JoinColumn(name = "id_Emprestimo", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Emprestimo idEmprestimo;

    public Livroemprestimo() {
    }

    public Livroemprestimo(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Livro getIdLivro() {
        return idLivro;
    }

    public void setIdLivro(Livro idLivro) {
        this.idLivro = idLivro;
    }

    public Emprestimo getIdEmprestimo() {
        return idEmprestimo;
    }

    public void setIdEmprestimo(Emprestimo idEmprestimo) {
        this.idEmprestimo = idEmprestimo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Livroemprestimo)) {
            return false;
        }
        Livroemprestimo other = (Livroemprestimo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.bibliotecamunicipal.entity.Livroemprestimo[ id=" + id + " ]";
    }
    
}

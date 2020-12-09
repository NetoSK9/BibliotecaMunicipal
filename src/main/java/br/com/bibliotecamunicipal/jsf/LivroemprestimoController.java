package br.com.bibliotecamunicipal.jsf;

import br.com.bibliotecamunicipal.entity.Livroemprestimo;
import br.com.bibliotecamunicipal.jsf.util.JsfUtil;
import br.com.bibliotecamunicipal.jsf.util.JsfUtil.PersistAction;
import br.com.bibliotecamunicipal.facade.LivroemprestimoFacade;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("livroemprestimoController")
@SessionScoped
public class LivroemprestimoController implements Serializable {

    @EJB
    private br.com.bibliotecamunicipal.facade.LivroemprestimoFacade ejbFacade;
    private List<Livroemprestimo> items = null;
    private Livroemprestimo selected;

    public LivroemprestimoController() {
    }

    public Livroemprestimo getSelected() {
        return selected;
    }

    public void setSelected(Livroemprestimo selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private LivroemprestimoFacade getFacade() {
        return ejbFacade;
    }

    public Livroemprestimo prepareCreate() {
        selected = new Livroemprestimo();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("LivroemprestimoCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("LivroemprestimoUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("LivroemprestimoDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Livroemprestimo> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Livroemprestimo getLivroemprestimo(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Livroemprestimo> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Livroemprestimo> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Livroemprestimo.class)
    public static class LivroemprestimoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            LivroemprestimoController controller = (LivroemprestimoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "livroemprestimoController");
            return controller.getLivroemprestimo(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Livroemprestimo) {
                Livroemprestimo o = (Livroemprestimo) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Livroemprestimo.class.getName()});
                return null;
            }
        }

    }

}

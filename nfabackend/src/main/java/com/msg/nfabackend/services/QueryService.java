package com.msg.nfabackend.services;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.cfg.NotYetImplementedException;

import com.msg.nfabackend.entities.Metric;
import com.msg.nfabackend.entities.NfaCriteria;
import com.msg.nfabackend.entities.NfaFactor;
import com.msg.nfabackend.entities.Project;
import com.msg.nfabackend.entities.Stakeholder;
import com.msg.nfabackend.entities.Type;
import com.msg.nfabackend.entities.nfaCatalog;
import com.msg.nfabackend.entities.nfaCatalog.BpPropertyTemplateNoConditionDe;
import com.msg.nfabackend.entities.nfaCatalog.BpPropertyTemplateNoConditionEn;

@Stateless
public class QueryService {
	
	@PersistenceContext(unitName = "msg-nfa")
	private EntityManager em;
	
	public List<Project> getAllProject() {
		return em.createQuery("from Project",Project.class).getResultList();
    }

	public List<Project> findProject( String status,String lookupCustName) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Project> criteria = criteriaBuilder.createQuery(Project.class);
		Root<Project> root = criteria.from(Project.class);
		Predicate statusQry= criteriaBuilder.like(root.<String>get("projectStatus"),status);
		Predicate lookupQry = criteriaBuilder.like(root.<String>get("customerName"), "%"+lookupCustName+"%");
		criteria.select(root);
		if (!lookupCustName.isEmpty() && status.equalsIgnoreCase("All")) {
		   
		    criteria.where(lookupQry);
		}
		else if  (!lookupCustName.isEmpty()&& !status.equalsIgnoreCase("All")) {
			
			 Predicate qry = criteriaBuilder.and(lookupQry, statusQry);
			 criteria.where(qry);
		}
		else if (lookupCustName.isEmpty()&& !status.equalsIgnoreCase("All")){
			 criteria.where(statusQry);
		}
		return em.createQuery(criteria).getResultList();
	}

	/**
	 * Create new project
	 * @param project
	 * @return Project
	 */
	public Project createProject(Project project) {
		em.merge(project);
		return project;
	}
	
	public nfaCatalog createNfa (Long metricId, nfaCatalog nfaCatalog) {
		Metric metric = em.find(Metric.class, metricId);
		nfaCatalog.setNfaNumber((long) metric.getNfaList().size());
		
		BpPropertyTemplateNoConditionDe de = nfaCatalog.getNfaCatalogBlueprint().getDe();
		if (de.getErklaerung() == null) {
			de.setErklaerung(String.join(" ", 
					de.getCharacteristic(), de.getProperty(), de.getModalVerb(), de.getQualifyingEx(), de.getValueInput(), de.getVerb()));
		}
		BpPropertyTemplateNoConditionEn en = nfaCatalog.getNfaCatalogBlueprint().getEn();
		if (en.getErklaerung() == null) {
			en.setErklaerung(String.join(" ", 
					en.getCharacteristic(), en.getProperty(), en.getModalVerb(), en.getVerb(), en.getQualifyingEx(), en.getValueInput()));
		}
		
		em.persist(nfaCatalog);
		
		metric.getNfaList().add(nfaCatalog);
			
		return nfaCatalog;
	}
	
	public void removeProject(Long id) {
		Project project = em.find(Project.class, id);
		project.getProjectTypes().clear();
		project.getProjectNfas().clear();
		project.getProjectStakeholders().clear();
		em.remove(project);
	}

	/**
	 * Updates a project by finding it by id first
	 * @param id
	 */
	public void updateProject(Project editedProject) {
		//TODO move logic to Project.java
		//TODO extract to ProjectQueryService.java

		Project project = em.find(Project.class, editedProject.getId());

		project.setCustomerName(editedProject.getCustomerName());
	    project.setBranch(editedProject.getBranch());
		project.setContactPersCustomer(editedProject.getContactPersCustomer());
		project.setContactPersMsg(editedProject.getContactPersMsg());
		project.setDevelopmentProcess(editedProject.getDevelopmentProcess());
		project.setProjectPhase(editedProject.getProjectPhase());
		project.setProjectStatus(editedProject.getProjectStatus());
		project.setProjectTypes(editedProject.getProjectTypes());
		project.setProjectStakeholders(editedProject.getProjectStakeholders());
		project.setProjectNfas(editedProject.getProjectNfas());
		em.merge(editedProject);

	}

	public List<Type> getAllType() {
		return em.createQuery("from Type",Type.class).getResultList();
    }
	
	public List<nfaCatalog> getAllNfa() {
		return em.createQuery("from nfaCatalog",nfaCatalog.class).getResultList();
    }
	
	public List<NfaFactor> getAllFactors() {
		return em.createQuery("from NfaFactor",NfaFactor.class).getResultList();
    }
	
	public List<NfaCriteria> getAllNfaCriterias() {
		return em.createQuery("from NfaCriteria",NfaCriteria.class).getResultList();
    }

	public List<NfaCriteria> getAllCriteriasForFactor(NfaFactor factor) {
		// TODO Auto-generated method stub
		
		throw new NotYetImplementedException();
//		return null;
	}
	
	public List<Project> getProjectsByStatus(String status) {

		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

		CriteriaQuery<Project> criteria = criteriaBuilder.createQuery(Project.class);
		Root<Project> fromProject = criteria.from(Project.class);
		criteria.where(criteriaBuilder.like(fromProject.<String>get("projectStatus"), status));
		return em.createQuery(criteria).getResultList();
	}
	
	public List<Stakeholder> getAllStakeholder() {
		return em.createQuery("from Stakeholder",Stakeholder.class).getResultList();
    }

}

package fpt.plms.repository;


import java.util.Set;

import fpt.plms.repository.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {
	Subject findOneById(Integer id);
	
	boolean existsByName(String name);
	
	@Query(nativeQuery = true, value = "select * from SUBJECT where is_disable = 0")
	Set<Subject> getAllSubject();
	
	@Query(nativeQuery = true, value = "select * from SUBJECT where name = ?1 and is_disable = 0")
	Subject findByName(String subjectName);
	
	@Query(nativeQuery = true, value = "select is_disable from SUBJECT where id = ?1")
	Integer isSubjectDisable(Integer id);
	
}

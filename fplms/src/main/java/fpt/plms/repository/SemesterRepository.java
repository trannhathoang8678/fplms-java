package fpt.plms.repository;

import java.sql.Date;
import java.util.Set;

import fpt.plms.repository.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, String>{
	@Query(nativeQuery = true, value = "select * from SEMESTER where code like ?1")
	public Set<Semester> getSemester(String code);
	
	@Query(nativeQuery = true, value = "select end_date from SEMESTER where code = ?1")
	public Date getSemesterEndDate(String code);
	
	@Query(nativeQuery = true, value = "select start_date from SEMESTER where code = ?1")
	public Date getSemesterStartDate(String code);
	
	@Query(nativeQuery = true, value = "select code from SEMESTER where start_date < ?1 and end_date > ?1 limit 1")
	public String getCurrentSemester(Date currentDate);
	
}

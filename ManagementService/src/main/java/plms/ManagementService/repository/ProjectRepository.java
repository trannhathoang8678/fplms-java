package plms.ManagementService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import plms.ManagementService.repository.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Integer> {
}
package it.objectmethod.worldjasper.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.objectmethod.worldjasper.model.Country;

public interface CountryRepository extends JpaRepository<Country, Integer>{

		@Query(value="select distinct continent from country",nativeQuery = true)
		public List<String> getAllContinents();
		
		public Country findByCode(String code);
}

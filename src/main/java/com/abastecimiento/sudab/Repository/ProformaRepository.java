package com.abastecimiento.sudab.Repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abastecimiento.sudab.Model.Proforma;


@Repository
public interface ProformaRepository extends JpaRepository<Proforma, Long> {

    

}
